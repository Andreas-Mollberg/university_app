package org.example.models;
public class Student {

    private int studentId;
    private String name;
    private String phone;
    private String email;

    public Student(int studentId, String name, String phone, String email) {
        this.studentId = studentId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
