package com.jju;



class Employee extends Person {
    private double salary;

    public Employee(String name, int age, double salary) throws ValidationException {
        super(name, age);
        setSalary(salary);
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) throws ValidationException {
        if (salary < 0) throw new ValidationException("Salary cannot be negative.");
        this.salary = salary;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + String.format(" | Salary: $%.2f", salary);
    }

    public String calculateBonus() {
        if (salary >= 60000) return "Bonus: $5,000";
        if (salary >= 30000) return "Bonus: $2,000";
        return "Bonus: $0";
    }

    // Used by JList to render items cleanly
    @Override
    public String toString() {
        return String.format("%s (Age: %d, $%.0f)", name, age, salary);
    }
}
