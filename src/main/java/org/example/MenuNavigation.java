package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MenuNavigation {
    private final Connection conn;
    private final Scanner scanner;

    public MenuNavigation(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nWelcome to University Database Manager");
            System.out.println("======================================");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Universities");
            System.out.println("3. Manage Courses");
            System.out.println("4. Exit");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");
            int tableChoice = scanner.nextInt();
            scanner.nextLine();

            switch (tableChoice) {
                case 1:
                    handleStudents();
                    break;
                case 2:
                    handleUniversities();
                    break;
                case 3:
                    handleCourses();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }

    private void handleStudents() {
        while (true) {
            System.out.println("\nSelected Students management:");
            System.out.println("======================================");
            System.out.println("1. Create student");
            System.out.println("2. Read student");
            System.out.println("3. Update student");
            System.out.println("4. Display student courses");
            System.out.println("5. Add or remove student from course");
            System.out.println("6. Delete student");
            System.out.println("7. Back to main menu");
            System.out.println("======================================");
            System.out.print("Enter your choice: ");
            int operationChoice = scanner.nextInt();
            scanner.nextLine();

            switch (operationChoice) {
                case 1:
                    createStudent();
                    break;
                case 2:
                    readStudent();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    displayStudentCourses();
                    break;
                case 5:
                    editStudentCourses();
                    break;
                case 6:
                    deleteStudent();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }

    private void editStudentCourses() {
        try {
            System.out.println("Enter student ID:");
            int studentId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter course ID:");
            int courseId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter 1 to add student to course, 2 to remove student from course:");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int id = resultSet.getInt("student_id");
                    String name = resultSet.getString("student_name");

                    String studentCoursesQuery = "SELECT * FROM student_courses WHERE student_id = ? AND course_id = ?";
                    try (PreparedStatement studentCoursesPstmt = conn.prepareStatement(studentCoursesQuery)) {
                        studentCoursesPstmt.setInt(1, studentId);
                        studentCoursesPstmt.setInt(2, courseId);
                        ResultSet studentCoursesResultSet = studentCoursesPstmt.executeQuery();
                        if (choice == 1) {
                            if (studentCoursesResultSet.next()) {
                                System.out.println("Student is already enrolled in the course.");
                            } else {
                                String insertQuery = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
                                try (PreparedStatement insertPstmt = conn.prepareStatement(insertQuery)) {
                                    insertPstmt.setInt(1, studentId);
                                    insertPstmt.setInt(2, courseId);
                                    int rowsInserted = insertPstmt.executeUpdate();
                                    if (rowsInserted > 0) {
                                        System.out.println("Student added to course successfully.");
                                    } else {
                                        System.out.println("Failed to add student to course.");
                                    }
                                }
                            }
                        } else if (choice == 2) {
                            if (studentCoursesResultSet.next()) {
                                String deleteQuery = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
                                try (PreparedStatement deletePstmt = conn.prepareStatement(deleteQuery)) {
                                    deletePstmt.setInt(1, studentId);
                                    deletePstmt.setInt(2, courseId);
                                    int rowsDeleted = deletePstmt.executeUpdate();
                                    if (rowsDeleted > 0) {
                                        System.out.println("Student removed from course successfully.");
                                    } else {
                                        System.out.println("Failed to remove student from course.");
                                    }
                                }
                            } else {
                                System.out.println("Student is not enrolled in the course.");
                            }
                        }
                    }
                } else {
                    System.out.println("No student found with ID: " + studentId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayStudentCourses() {
        try {
            System.out.println("Enter student ID:");
            int studentId = scanner.nextInt();
            scanner.nextLine();

            String query = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    String studentName = resultSet.getString("student_name");

                    String coursesQuery = "SELECT courses.course_id, courses.course_name, universities.university_name " +
                            "FROM student_courses JOIN courses ON student_courses.course_id = courses.course_id " +
                            "JOIN universities ON courses.university_id = universities.university_id " +
                            "WHERE student_courses.student_id = ?";
                    try (PreparedStatement coursesPstmt = conn.prepareStatement(coursesQuery)) {
                        coursesPstmt.setInt(1, studentId);
                        ResultSet coursesResultSet = coursesPstmt.executeQuery();
                        System.out.println("Courses for student " + studentName + ":");
                        System.out.println("=======================================================================");
                        System.out.printf("| %-10s | %-40s | %-15s\n", "Course ID", "Course Name", "University");
                        System.out.println("=======================================================================");
                        while (coursesResultSet.next()) {
                            int courseId = coursesResultSet.getInt("course_id");
                            String courseName = coursesResultSet.getString("course_name");
                            String universityName = coursesResultSet.getString("university_name");
                            System.out.printf("| %-10d | %-40s | %-15s\n", courseId, courseName, universityName);
                        }
                        System.out.println("=======================================================================");
                    }
                } else {
                    System.out.println("No student found with ID: " + studentId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createStudent() {
        try {
            System.out.println("Enter student name:");
            String name = scanner.nextLine();
            System.out.println("Enter student phone:");
            String phone = scanner.nextLine();
            System.out.println("Enter student email:");
            String email = scanner.nextLine();

            String query = "INSERT INTO students (student_name, phone, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, email);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Student created successfully.");
                } else {
                    System.out.println("Failed to create student.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readStudent() {
        try {
            System.out.println("Enter student ID:");
            int studentId = scanner.nextInt();
            scanner.nextLine();

            String query = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    int id = resultSet.getInt("student_id");
                    String name = resultSet.getString("student_name");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");

                    System.out.println("Student ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Phone: " + phone);
                    System.out.println("Email: " + email);
                } else {
                    System.out.println("No student found with ID: " + studentId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateStudent() {
        try {
            System.out.println("Enter student ID:");
            int studentId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter updated student name:");
            String name = scanner.nextLine();
            System.out.println("Enter updated student phone:");
            String phone = scanner.nextLine();
            System.out.println("Enter updated student email:");
            String email = scanner.nextLine();

            String query = "UPDATE students SET student_name = ?, phone = ?, email = ? WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, phone);
                pstmt.setString(3, email);
                pstmt.setInt(4, studentId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Student updated successfully.");
                } else {
                    System.out.println("Failed to update student. Student ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent() {
        try {
            System.out.println("Enter student ID:");
            int studentId = scanner.nextInt();
            scanner.nextLine();

            String query = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Student deleted successfully.");
                } else {
                    System.out.println("Failed to delete student. Student ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void handleUniversities() {
        System.out.println("\nSelected Universities management:");
        System.out.println("======================================");
        System.out.println("1. Create university");
        System.out.println("2. Read university");
        System.out.println("3. Update university");
        System.out.println("4. Delete university");
        System.out.println("5. List all universities");
        System.out.println("6. Back to main menu");
        System.out.println("======================================");
        System.out.print("Enter your choice: ");
        int operationChoice = scanner.nextInt();
        scanner.nextLine();

        switch (operationChoice) {
            case 1:
                createUniversity();
                break;
            case 2:
                readUniversity();
                break;
            case 3:
                updateUniversity();
                break;
            case 4:
                deleteUniversity();
                break;
            case 5:
                listAllUniversities();
            case 6:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void listAllUniversities() {
        try {
            String query = "SELECT universities.university_id, universities.university_name, universities.address," +
                    " universities.phone, universities.email FROM universities ";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet resultSet = pstmt.executeQuery();
                System.out.println("Universities:");
                System.out.println("=======================================================================");
                System.out.printf("| %-23s | %-5s | %-30s | %-15s | %-30s\n", "University", "ID", "Address", "Phone", "Email");
                System.out.println("=======================================================================");
                while (resultSet.next()) {
                    int universityId = resultSet.getInt("university_id");
                    String universityName = resultSet.getString("university_name");
                    String address = resultSet.getString("address");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");
                    System.out.printf("| %-23s | %-5d | %-30s | %-15s | %-30s\n", universityName, universityId, address, phone, email);
                }
                System.out.println("=======================================================================");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createUniversity() {
        try {
            System.out.println("Enter university name:");
            String name = scanner.nextLine();
            System.out.println("Enter university address:");
            String address = scanner.nextLine();
            System.out.println("Enter university phone:");
            String phone = scanner.nextLine();
            System.out.println("Enter university email:");
            String email = scanner.nextLine();

            String query = "INSERT INTO universities (university_name, address, phone, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, address);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("University created successfully.");
                } else {
                    System.out.println("Failed to create university.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readUniversity() {
        try {
            System.out.println("Enter university ID:");
            int universityId = scanner.nextInt();
            scanner.nextLine();

            String query = "SELECT * FROM universities WHERE university_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, universityId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("university_name");
                    String address = resultSet.getString("address");
                    String phone = resultSet.getString("phone");
                    String email = resultSet.getString("email");

                    System.out.println("University ID: " + universityId);
                    System.out.println("Name: " + name);
                    System.out.println("Address: " + address);
                    System.out.println("Phone: " + phone);
                    System.out.println("Email: " + email);
                } else {
                    System.out.println("No university found with ID: " + universityId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUniversity() {
        try {
            System.out.println("Enter university ID:");
            int universityId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter updated university name:");
            String name = scanner.nextLine();
            System.out.println("Enter updated university address:");
            String address = scanner.nextLine();
            System.out.println("Enter updated university phone:");
            String phone = scanner.nextLine();
            System.out.println("Enter updated university email:");
            String email = scanner.nextLine();

            String query = "UPDATE universities SET university_name = ?, address = ?, phone = ?, email = ? WHERE university_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, address);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setInt(5, universityId);
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("University updated successfully.");
                } else {
                    System.out.println("Failed to update university. University ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUniversity() {
        try {
            System.out.println("Enter university ID:");
            int universityId = scanner.nextInt();
            scanner.nextLine();

            String query = "DELETE FROM universities WHERE university_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, universityId);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("University deleted successfully.");
                } else {
                    System.out.println("Failed to delete university. University ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCourses() {
        System.out.println("\nSelected Course management:");
        System.out.println("======================================");
        System.out.println("1. Create course");
        System.out.println("2. Read course");
        System.out.println("3. Update course");
        System.out.println("4. Delete course");
        System.out.println("5. List all courses");
        System.out.println("6. Back to main menu");
        System.out.println("======================================");
        System.out.print("Enter your choice: ");
        int operationChoice = scanner.nextInt();
        scanner.nextLine();

        switch (operationChoice) {
            case 1:
                createCourse();
                break;
            case 2:
                readCourse();
                break;
            case 3:
                updateCourse();
                break;
            case 4:
                deleteCourse();
                break;
            case 5:
                listAllCourses();
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void listAllCourses() {
        try {
            String query = "SELECT courses.course_id, courses.course_name, universities.university_name FROM courses " +
                    "JOIN universities ON courses.university_id = universities.university_id";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet resultSet = pstmt.executeQuery();
                System.out.println("Courses:");
                System.out.println("=======================================================================");
                System.out.printf("| %-15s | %-5s | %-30s\n", "University", "Course ID", "Course Name");
                System.out.println("=======================================================================");
                while (resultSet.next()) {
                    int courseId = resultSet.getInt("course_id");
                    String courseName = resultSet.getString("course_name");
                    String universityName = resultSet.getString("university_name");
                    System.out.printf("| %-15s | %-5d | %-30s\n", universityName, courseId, courseName);}
                System.out.println("=======================================================================");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

private void createCourse() {
    try {
        System.out.println("Enter course name:");
        String name = scanner.nextLine();
        System.out.println("Enter course teacher ID:");
        int teacher = scanner.nextInt();
        System.out.println("Enter course credits:");
        int credits = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter university ID for the course:");
        int universityId = scanner.nextInt();
        scanner.nextLine();

        String query = "INSERT INTO courses (course_name, university_id, teacher_id, credits) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, universityId);
            pstmt.setInt(3, teacher);
            pstmt.setInt(4, credits);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Course created successfully.");
            } else {
                System.out.println("Failed to create course.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void readCourse() {
    try {
        System.out.println("Enter course ID:");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        String query = "SELECT * FROM courses WHERE course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("course_name");
                int universityId = resultSet.getInt("university_id");
                int teacherId = resultSet.getInt("teacher_id");
                int credits = resultSet.getInt("credits");

                // Query the Teachers table to get the teacher's name
                String teacherQuery = "SELECT teacher_name FROM Teachers WHERE teacher_id = ?";
                try (PreparedStatement teacherPstmt = conn.prepareStatement(teacherQuery)) {
                    teacherPstmt.setInt(1, teacherId);
                    ResultSet teacherResultSet = teacherPstmt.executeQuery();
                    String teacherName = "";
                    if (teacherResultSet.next()) {
                        teacherName = teacherResultSet.getString("teacher_name");
                    }

                    System.out.println("Course ID: " + courseId);
                    System.out.println("Name: " + name);
                    System.out.println("University ID: " + universityId);
                    System.out.println("Teacher: " + teacherName);
                    System.out.println("Credits: " + credits);
                }
            } else {
                System.out.println("No course found with ID: " + courseId);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void updateCourse() {
    try {
        System.out.println("Enter course ID:");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter updated course name:");
        String name = scanner.nextLine();
        System.out.println("Enter updated course teacher:");
        String teacher = scanner.nextLine();
        System.out.println("Enter updated course credits:");
        int credits = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter updated university ID for the course:");
        int universityId = scanner.nextInt();
        scanner.nextLine();

        String query = "UPDATE courses SET course_name = ?, university_id = ?, teacher = ?, credits = ? WHERE course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, universityId);
            pstmt.setString(3, teacher);
            pstmt.setInt(4, credits);
            pstmt.setInt(5, courseId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Course updated successfully.");
            } else {
                System.out.println("Failed to update course. Course ID not found.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void deleteCourse() {
    try {
        System.out.println("Enter course ID:");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        String query = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Course deleted successfully.");
            } else {
                System.out.println("Failed to delete course. Course ID not found.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


}
