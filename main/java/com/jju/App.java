package com.jju;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
// import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;




public class App extends JFrame {
    private final List<Employee> repo = new ArrayList<>();
    private final DefaultListModel<Employee> listModel = new DefaultListModel<>();
    private final JList<Employee> employeeList = new JList<>(listModel);
    private final JTextField nameField = new JTextField(15);
    private final JTextField ageField  = new JTextField(5);
    private final JTextField salField  = new JTextField(10);
    private final JTextArea outputArea = new JTextArea(5, 30);
    private final JLabel countLabel    = new JLabel("Employees: 0/7");

    private static final Font LABEL_FONT   = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT  = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font OUTPUT_FONT  = new Font("Consolas", Font.PLAIN, 13);
    private static final Font LIST_FONT    = new Font("Segoe UI", Font.PLAIN, 14);

    public App() {
        super("Employee Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        setContentPane(rootPanel);

        // 🟦 LEFT PANEL: List + Count + Buttons
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        employeeList.setFont(LIST_FONT);
        employeeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeList.setFixedCellHeight(32);
        employeeList.setBorder(new EmptyBorder(5, 5, 5, 5));
        employeeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Employee sel = employeeList.getSelectedValue();
                if (sel != null) {
                    nameField.setText(sel.getName());
                    ageField.setText(String.valueOf(sel.getAge()));
                    salField.setText(String.valueOf(sel.getSalary()));
                }
            }
        });

        JScrollPane listScroll = new JScrollPane(employeeList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Employee List"));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        JButton saveCsvBtn = styleBtn("💾 Save CSV");
        JButton saveJsonBtn = styleBtn("💾 Save JSON");
        JButton loadBtn = styleBtn("📂 Load File");
        saveCsvBtn.addActionListener(e -> saveToCsv());
        saveJsonBtn.addActionListener(e -> saveToJson());
        loadBtn.addActionListener(e -> loadFromFile());
        btnPanel.add(saveCsvBtn);
        btnPanel.add(saveJsonBtn);
        btnPanel.add(loadBtn);

        leftPanel.add(countLabel, BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // 🟦 RIGHT PANEL: Form + Output
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        addFormRow(formPanel, "Name:", nameField);
        addFormRow(formPanel, "Age:", ageField);
        addFormRow(formPanel, "Salary:", salField);

        JButton addBtn = styleBtn("➕ Add Employee");
        addBtn.addActionListener(e -> processAdd());

        outputArea.setFont(OUTPUT_FONT);
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Status"));

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(formPanel, BorderLayout.NORTH);
        rightPanel.add(addBtn, BorderLayout.CENTER);
        rightPanel.add(outputScroll, BorderLayout.SOUTH);

        // 🟦 Split Layout
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(240);
        split.setResizeWeight(0.35);
        split.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        rootPanel.add(split, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JButton styleBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON_FONT);
        b.setMargin(new Insets(8, 14, 8, 14));
        return b;
    }

    private void addFormRow(JPanel panel, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl);
        field.setFont(INPUT_FONT);
        field.setMargin(new Insets(6, 10, 6, 10));
        panel.add(field);
    }

    // [5] Add Logic
    private void processAdd() {
        try {
            if (repo.size() >= 7) throw new ListCapacityException("List full. Max 7 employees allowed.");
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText().trim());
            double sal = Double.parseDouble(salField.getText().trim());
            Employee emp = new Employee(name, age, sal);
            repo.add(emp);
            listModel.addElement(emp);
            updateCount();
            clearFields();
            showSuccess("✅ Added:\n" + emp.getDetails() + "\n" + emp.calculateBonus());
        } catch (NumberFormatException e) { showError("Invalid Input: Age/Salary must be numbers."); }
        catch (ValidationException e)   { showError(e.getMessage()); }
        catch (ListCapacityException e) { showError(e.getMessage()); }
        catch (Exception e)             { showError("Unexpected error: " + e.getMessage()); }
    }

    // [6] Save CSV
    private void saveToCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");
            try {
                StringBuilder sb = new StringBuilder();
                for (Employee e : repo) sb.append(String.format("%s,%d,%.2f%n", e.getName(), e.getAge(), e.getSalary()));
                Files.writeString(file.toPath(), sb.toString());
                showSuccess("✅ Saved " + repo.size() + " records to CSV:\n" + file.getAbsolutePath());
            } catch (IOException e) { showError("Save failed: " + e.getMessage()); }
        }
    }

    // [7] Save JSON
    private void saveToJson() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) file = new File(file.getAbsolutePath() + ".json");
            try {
                StringBuilder sb = new StringBuilder("[\n");
                for (int i = 0; i < repo.size(); i++) {
                    Employee e = repo.get(i);
                    sb.append(String.format("  {\"name\": \"%s\", \"age\": %d, \"salary\": %.2f}", e.getName(), e.getAge(), e.getSalary()));
                    if (i < repo.size() - 1) sb.append(",\n"); else sb.append("\n");
                }
                sb.append("]");
                Files.writeString(file.toPath(), sb.toString());
                showSuccess("✅ Saved " + repo.size() + " records to JSON:\n" + file.getAbsolutePath());
            } catch (IOException e) { showError("Save failed: " + e.getMessage()); }
        }
    }

    // [8] Load (Auto-detects CSV/JSON by extension)
    private void loadFromFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Data Files", "csv", "json"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String ext = file.getName().toLowerCase();
            try {
                List<Employee> temp = new ArrayList<>();
                if (ext.endsWith(".csv")) {
                    temp = parseCsv(file);
                } else if (ext.endsWith(".json")) {
                    temp = parseJson(file);
                } else {
                    throw new ValidationException("Unsupported file format. Use .csv or .json");
                }
                if (temp.size() > 7) throw new ListCapacityException("File exceeds 7-record limit.");

                // Atomic swap
                repo.clear(); listModel.clear();
                repo.addAll(temp); temp.forEach(listModel::addElement);
                updateCount(); clearFields();
                showSuccess("✅ Loaded " + repo.size() + " records.");
            } catch (IOException | ValidationException | ListCapacityException e) {
                showError("Load failed: " + e.getMessage());
                repo.clear(); listModel.clear(); updateCount();
            }
        }
    }

    private List<Employee> parseCsv(File file) throws IOException, ValidationException {
        List<Employee> list = new ArrayList<>();
        for (String line : Files.readAllLines(file.toPath())) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split(",");
            if (p.length != 3) throw new ValidationException("Malformed CSV line.");
            list.add(new Employee(p[0].trim(), Integer.parseInt(p[1].trim()), Double.parseDouble(p[2].trim())));
        }
        return list;
    }

    private List<Employee> parseJson(File file) throws IOException, ValidationException {
        String content = Files.readString(file.toPath()).replaceAll("\\s+", " ").trim();
        if (!content.startsWith("[") || !content.endsWith("]")) throw new ValidationException("Invalid JSON array structure.");
        content = content.substring(1, content.length() - 1).trim();
        if (content.isEmpty()) return new ArrayList<>();

        // Simple lab-safe parser (Production: use Jackson/Gson)
        List<Employee> list = new ArrayList<>();
        Pattern objPattern = Pattern.compile("\\{[^}]+\\}");
        Matcher m = objPattern.matcher(content);
        while (m.find()) {
            String obj = m.group();
            String name = extract(obj, "name");
            String ageStr = extract(obj, "age");
            String salStr = extract(obj, "salary");
            list.add(new Employee(name, Integer.parseInt(ageStr), Double.parseDouble(salStr)));
        }
        if (list.isEmpty()) throw new ValidationException("No valid employee objects found.");
        return list;
    }

    // Helper: Extracts value from "key": value or "key": "value"
    private String extract(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^\"]+?)\"?");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1).trim() : "";
    }

    private void showSuccess(String msg) {
        outputArea.setForeground(new Color(0, 100, 0));
        outputArea.setText(msg);
    }
    private void showError(String msg) {
        outputArea.setForeground(Color.RED);
        outputArea.setText("❌ " + msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void updateCount() {
        countLabel.setText(String.format("Employees: %d/7", repo.size()));
    }
    private void clearFields() {
        nameField.setText(""); ageField.setText(""); salField.setText("");
        employeeList.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}

