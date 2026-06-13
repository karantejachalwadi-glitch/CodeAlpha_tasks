import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple console-based Student Grade Tracker.
 * Manages grades using an ArrayList and computes average, highest, and lowest scores.
 */
public class GradeTracker {
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Pre-load some initial sample students
        loadSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    displayReport();
                    break;
                case 3:
                    System.out.println("Thank you for using Student Grade Tracker! Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("⚠️ Invalid choice. Please enter a number between 1 and 3.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println("          STUDENT GRADE TRACKER          ");
        System.out.println("=========================================");
        System.out.println(" 1. Add Student Grade");
        System.out.println(" 2. Display Student List & Summary Report");
        System.out.println(" 3. Exit");
        System.out.println("=========================================");
    }

    private static void addStudent() {
        System.out.println("--- Add New Student ---");
        
        // Input ID
        String id;
        while (true) {
            System.out.print("Enter Student ID (e.g. CS101): ");
            id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("⚠️ Student ID cannot be empty.");
                continue;
            }
            if (findStudentById(id) != null) {
                System.out.println("⚠️ A student with ID '" + id + "' already exists.");
                continue;
            }
            break;
        }

        // Input Name
        String name;
        while (true) {
            System.out.print("Enter Student Name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("⚠️ Student name cannot be empty.");
                continue;
            }
            break;
        }

        // Input Score
        double score;
        while (true) {
            try {
                System.out.print("Enter Grade Score (0.0 - 100.0): ");
                String scoreStr = scanner.nextLine().trim();
                score = Double.parseDouble(scoreStr);
                if (score < 0.0 || score > 100.0) {
                    System.out.println("⚠️ Score must be between 0.0 and 100.0.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Please enter a valid decimal number for the score.");
            }
        }

        students.add(new Student(id, name, score));
        System.out.println("\n✅ Student '" + name + "' added successfully!");
    }

    private static void displayReport() {
        if (students.isEmpty()) {
            System.out.println("⚠️ No students registered yet.");
            return;
        }

        System.out.println("=================================================================");
        System.out.println("                       STUDENT GRADE REPORT                      ");
        System.out.println("=================================================================");
        System.out.printf("%-15s %-25s %-12s %-12s\n", "Student ID", "Student Name", "Score", "Grade");
        System.out.println("-----------------------------------------------------------------");
        
        double sum = 0;
        Student highest = students.get(0);
        Student lowest = students.get(0);

        for (Student s : students) {
            System.out.printf("%-15s %-25s %-12.2f %-12s\n", 
                s.getId(), s.getName(), s.getScore(), s.getLetterGrade());
            
            sum += s.getScore();
            if (s.getScore() > highest.getScore()) {
                highest = s;
            }
            if (s.getScore() < lowest.getScore()) {
                lowest = s;
            }
        }

        double average = sum / students.size();

        System.out.println("=================================================================");
        System.out.println("                           STATISTICS                            ");
        System.out.println("=================================================================");
        System.out.printf("Total Students : %d\n", students.size());
        System.out.printf("Class Average  : %.2f\n", average);
        System.out.printf("Highest Score  : %.2f (%s)\n", highest.getScore(), highest.getName());
        System.out.printf("Lowest Score   : %.2f (%s)\n", lowest.getScore(), lowest.getName());
        System.out.println("=================================================================");
    }

    private static Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Please enter a valid number.");
            }
        }
    }

    private static void loadSampleData() {
        students.add(new Student("AL-01", "Emma Watson", 94.5));
        students.add(new Student("AL-02", "Alex Mercer", 82.0));
        students.add(new Student("AL-03", "Sophia Lin", 78.8));
        students.add(new Student("AL-04", "Marcus Aurelius", 64.0));
        students.add(new Student("AL-05", "Dianne Prince", 98.2));
    }
}

/**
 * Represents a student with ID, name, and score.
 */
class Student {
    private String id;
    private String name;
    private double score;

    public Student(String id, String name, double score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public String getLetterGrade() {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }
}
