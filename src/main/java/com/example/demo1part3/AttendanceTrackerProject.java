package com.example.attendancetrackerproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AttendanceTrackerProject extends Application {
    private StudentBST studentTree = new StudentBST();
    private ObservableList<Student> studentObservableList = FXCollections.observableArrayList();
    private ObservableList<Student> studentHashTableObservableList = FXCollections.observableArrayList();
    private SimpleHashTable studentHashTable = new SimpleHashTable(10);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Attendance Tracker");

        ListView<Student> studentListView = new ListView<>(studentObservableList);
        studentListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });

        Button addStudentButton = new Button("Add Student");
        addStudentButton.setOnAction(e -> showAddStudentDialog());

        Button addStudentToHashTableButton = new Button("Add Student to Hash Table");
        addStudentToHashTableButton.setOnAction(e -> showAddStudentDialogForHashTable());

        Button removeStudentButton = new Button("Remove Student");
        removeStudentButton.setOnAction(e -> {
            Student selected = studentListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                studentTree.remove(selected.getId());
                studentObservableList.remove(selected);
                studentHashTable.remove(selected.getId());
                studentHashTableObservableList.remove(selected);
            }
        });

        Button findStudentButton = new Button("Find Student");
        findStudentButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Find Student");
            dialog.setHeaderText("Search for a student by ID");
            dialog.setContentText("Enter student ID:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(id -> {
                Student student = studentTree.find(id);
                if (student != null) {
                    studentListView.getSelectionModel().select(student);
                    studentListView.scrollTo(student);
                } else {
                    showAlert("Search Result", "No student found with ID: " + id);
                }
            });
        });

        ListView<Student> studentHashTableListView = new ListView<>(studentHashTableObservableList);
        studentHashTableListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                studentListView,
                studentHashTableListView, // New ListView for hash table students
                addStudentButton,
                addStudentToHashTableButton,
                removeStudentButton,
                findStudentButton
        );

        Scene scene = new Scene(layout, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAddStudentDialog() {
        createStudentDialog((student) -> {
            studentTree.insert(student);
            studentObservableList.add(student);
        });
    }

    private void showAddStudentDialogForHashTable() {
        createStudentDialog((student) -> {
            studentHashTable.add(student.getId(), student);
            studentHashTableObservableList.add(student);
        });
    }

    private void createStudentDialog(Consumer<Student> action) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Enter student details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField ageField = new TextField();
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));

        idField.setPromptText("ID");
        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");
        ageField.setPromptText("Age");
        genderBox.setPromptText("Select Gender");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Age:"), 0, 3);
        grid.add(ageField, 1, 3);
        grid.add(new Label("Gender:"), 0, 4);
        grid.add(genderBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String id = idField.getText();
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    String gender = genderBox.getValue();
                    Student student = new Student(id, firstName, lastName, age, gender);
                    action.accept(student);
                    return student;
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter a valid number for age.");
                    return null;
                } catch (NullPointerException e) {
                    showAlert("Input Error", "Please select a gender.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class StudentBST {
        private Node root;

        private static class Node {
            Student student;
            Node left, right;

            Node(Student student) {
                this.student = student;
            }
        }

        public void insert(Student student) {
            root = insertRec(root, student);
        }

        private Node insertRec(Node root, Student student) {
            if (root == null) {
                root = new Node(student);
                return root;
            }

            if (student.getId().compareTo(root.student.getId()) < 0) {
                root.left = insertRec(root.left, student);
            } else if (student.getId().compareTo(root.student.getId()) > 0) {
                root.right = insertRec(root.right, student);
            }

            return root;
        }

        public Student find(String id) {
            return findRec(root, id);
        }

        private Student findRec(Node root, String id) {
            if (root == null || root.student.getId().equals(id)) {
                return root != null ? root.student : null;
            }

            if (id.compareTo(root.student.getId()) < 0) {
                return findRec(root.left, id);
            } else {
                return findRec(root.right, id);
            }
        }

        public void remove(String id) {
            root = removeRec(root, id);
        }

        private Node removeRec(Node root, String id) {
            if (root == null) {
                return null;
            }

            if (id.compareTo(root.student.getId()) < 0) {
                root.left = removeRec(root.left, id);
            } else if (id.compareTo(root.student.getId()) > 0) {
                root.right = removeRec(root.right, id);
            } else {
                if (root.left == null) {
                    return root.right;
                } else if (root.right == null) {
                    return root.left;
                }

                root.student = minValue(root.right);
                root.right = removeRec(root.right, root.student.getId());
            }

            return root;
        }

        private Student minValue(Node root) {
            Student minv = root.student;
            while (root.left != null) {
                minv = root.left.student;
                root = root.left;
            }
            return minv;
        }
    }

    public static class Student {
        private final String id;
        private final String firstName;
        private final String lastName;
        private final int age;
        private final String gender;

        public Student(String id, String firstName, String lastName, int age, String gender) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.gender = gender;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id='" + id + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", age=" + age +
                    ", gender='" + gender + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }
    }

    public static class SimpleHashTable {
        private List<HashNode> buckets;
        private int capacity;

        public SimpleHashTable(int capacity) {
            this.capacity = capacity;
            buckets = new LinkedList<>();
            for (int i = 0; i < capacity; i++) {
                buckets.add(null);
            }
        }

        private int getBucketIndex(String key) {
            int hashCode = key.hashCode();
            return Math.abs(hashCode) % capacity;
        }

        public void add(String key, Student value) {
            int bucketIndex = getBucketIndex(key);
            HashNode head = buckets.get(bucketIndex);

            while (head != null) {
                if (head.key.equals(key)) {
                    head.value = value;
                    return;
                }
                head = head.next;
            }

            HashNode newNode = new HashNode(key, value);
            newNode.next = head;
            buckets.set(bucketIndex, newNode);
        }

        public Student get(String key) {
            int bucketIndex = getBucketIndex(key);
            HashNode head = buckets.get(bucketIndex);

            while (head != null) {
                if (head.key.equals(key)) {
                    return head.value;
                }
                head = head.next;
            }
            return null;
        }

        public Student remove(String key) {
            int bucketIndex = getBucketIndex(key);
            HashNode head = buckets.get(bucketIndex);
            HashNode prev = null;

            while (head != null) {
                if (head.key.equals(key)) {
                    if (prev != null) {
                        prev.next = head.next;
                    } else {
                        buckets.set(bucketIndex, head.next);
                    }
                    return head.value;
                }
                prev = head;
                head = head.next;
            }
            return null;
        }

        private static class HashNode {
            String key;
            Student value;
            HashNode next;

            public HashNode(String key, Student value) {
                this.key = key;
                this.value = value;
            }
        }
    }

}
