import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class AttendanceTrackerProject {

    private static class Node {
        private Student student;
        private Node left, right;

        public Node(Student student) {
            this.student = student;
            this.left = null;
            this.right = null;
        }
    }

    private static class BinarySearchTree {
        private Node root;

        public BinarySearchTree() {
            this.root = null;
        }

        public void insert(Student student) {
            root = insert(root, student);
        }

        private Node insert(Node node, Student student) {
            if (node == null) {
                return new Node(student);
            }

            if (student.getId().compareTo(node.student.getId()) < 0) {
                node.left = insert(node.left, student);
            } else if (student.getId().compareTo(node.student.getId()) > 0) {
                node.right = insert(node.right, student);
            }
            return node;
        }

        public Student search(String id) {
            return search(root, id);
        }

        private Student search(Node node, String id) {
            if (node == null) {
                return null;
            }

            if (id.equals(node.student.getId())) {
                return node.student;
            } else if (id.compareTo(node.student.getId()) < 0) {
                return search(node.left, id);
            } else {
                return search(node.right, id);
            }
        }
    }

    private static class HashTable {
        private static final int DEFAULT_CAPACITY = 16;
        private LinkedList<Student>[] buckets;

        public HashTable() {
            this(DEFAULT_CAPACITY);
        }

        public HashTable(int capacity) {
            buckets = new LinkedList[capacity];
            for (int i = 0; i < capacity; i++) {
                buckets[i] = new LinkedList<>();
            }
        }

        public int hash(String id) {
            int hash = 0;
            for (char c : id.toCharArray()) {
                hash = 31 * hash + c;
            }
            return hash % buckets.length;
        }

        public void insert(Student student) {
            int hash = hash(student.getId());
            buckets[hash].add(student);
        }

        public Student search(String id) {
            int hash = hash(id);
            for (Student student : buckets[hash]) {
                if (student.getId().equals(id)) {
                    return student;
                }
            }
            return null;
        }
    }

    private static class WeightedGraph {
        private static class Edge {
            private Node source;
            private Node destination;
            private double weight;

            public Edge(Node source, Node destination, double weight) {
                this.source = source;
                this.destination = destination;
                this.weight = weight;
            }

            public Node getSource() {
                return source;
            }

            public Node getDestination() {
                return destination;
            }

            public double getWeight() {
                return weight;
            }
        }

        private Map<Node, List<Edge>> adjacencyList;

        public WeightedGraph() {
            adjacencyList = new HashMap<>();
        }

        public void addNode(Node node) {
            if (!adjacencyList.containsKey(node)) {
                adjacencyList.put(node, new ArrayList<>());
            }
        }

        public void addEdge(Node source, Node destination, double weight) {
            addNode(source);
            addNode(destination);
            adjacencyList.get(source).add(new Edge(source, destination, weight));
        }
    }