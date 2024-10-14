package org.example.models;

public class Course {
    private int courseId;
    private String name;
    private int universityId;
    private String teacher;
    private int credits;

    public Course(int courseId, String name, int universityId, String teacher, int credits) {
        this.courseId = courseId;
        this.name = name;
        this.universityId = universityId;
        this.teacher = teacher;
        this.credits = credits;
    }

    public Course(int courseId, String name, int universityId, int credits) {
        this.courseId = courseId;
        this.name = name;
        this.universityId = universityId;
        this.credits = credits;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
