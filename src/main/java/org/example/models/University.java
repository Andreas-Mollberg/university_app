package org.example.models;

import java.sql.*;

public class University {
    private int universityId;
    private String name;
    private String address;
    private String phone;
    private String email;

    public University(int universityId, String name, String address, String phone, String email) {
        this.universityId = universityId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public University(int universityId, String name) {
        this.universityId = universityId;
        this.name = name;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public String toString() {
        return "University{" +
                "universityId=" + universityId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}