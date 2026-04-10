package com.jju;

// [2] OOP: Base Class
class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) throws ValidationException {
        setName(name);
        setAge(age);
    }

    public String getName() { return name; }
    public void setName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) throw new ValidationException("Name cannot be empty.");
        this.name = name.trim();
    }

    public int getAge() { return age; }
    public void setAge(int age) throws ValidationException {
        if (age < 0 || age > 120) throw new ValidationException("Age must be between 0 and 120.");
        this.age = age;
    }

    public String getDetails() {
        return String.format("Name: %s | Age: %d", name, age);
    }
}
