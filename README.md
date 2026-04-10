# 🎓 Java Swing Employee Manager

A clean, educational Java Swing application demonstrating **Object-Oriented Programming**, **Exception Handling**, **Generics**, **File I/O**, and **UI Design** principles. Built for live classroom demos and student reference.

---

## 📋 Table of Contents

- [✨ Learning Objectives](#-learning-objectives)
- [🏗️ Architecture Overview](#️-architecture-overview)
- [📁 Project Structure](#-project-structure)
- [🚀 How to Run](#-how-to-run)
- [🔍 Code Walkthrough](#-code-walkthrough)
- [🧩 Key Concepts Demonstrated](#-key-concepts-demonstrated)
- [🛠️ Extension Ideas for Students](#️-extension-ideas-for-students)
- [📚 Resources](#-resources)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## ✨ Learning Objectives

By studying this project, students will understand:

| Concept | Where It's Applied |
|---------|-------------------|
| **Encapsulation** | Private fields with validated setters in `Person`/`Employee` |
| **Inheritance** | `Employee extends Person` reuses and extends behavior |
| **Polymorphism** | `@Override getDetails()` and `toString()` for display |
| **Custom Exceptions** | `ValidationException`, `ListCapacityException` for domain rules |
| **Generics** | `List<Employee>`, `DefaultListModel<Employee>` for type safety |
| **Swing UI Design** | Layout managers, borders, fonts, padding, event handling |
| **File I/O** | CSV/JSON save/load with `java.nio.file` |
| **Error Handling** | Try-catch blocks, user-friendly error dialogs |
| **Thread Safety** | `SwingUtilities.invokeLater()` for EDT compliance |

---

## 🏗️ Architecture Overview

### Design Principles Applied
1. **Separation of Concerns**: UI, logic, and data are isolated
2. **Just-In-Time Declaration**: Fields declared when first used (reduces cognitive load)
3. **Defensive Programming**: Validation in setters, not just UI
4. **Atomic State Updates**: Parse to temp list → validate → swap (prevents partial UI state)
5. **User-Centric Errors**: Specific messages, not stack traces

---

## 📁 Project Structure

> 💡 **Note**: For production, split classes into separate files/packages. This single-file version is optimized for classroom demos and easy distribution.

---

## 🚀 How to Run

### Prerequisites
- Java 11 or higher (required for `java.nio.file` API)
- Any IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

### Option 1: Command Line
```bash
# Compile
javac SimpleOOPTaskApp.java

# Run
java SimpleOOPTaskApp
