# Student Grade Tracker

A lightweight, simple, and clean console-based Java application to input and manage student grades, calculate grade statistics, and display formatted reports.

---

## Features

- **Add Student Grades**: Input student IDs, names, and grade scores with validation.
- **Auto-Calculations**: Computes class average, highest score, and lowest score dynamically.
- **Letter Grades**: Automatically assigns corresponding letter grades (A, B, C, D, or F).
- **Summary Report**: Displays a cleanly formatted console table summarizing all student records and class statistics.

---

## Prerequisites

Before running the application, make sure you have the Java Development Kit (JDK) installed.
To check if Java is installed, run:
```bash
java -version
javac -version
```

---

## How to Compile & Run

Open your command prompt or terminal in the project directory:

### Step 1: Compile the Java Code
Compile the code using `javac`:
```bash
javac GradeTracker.java
```

### Step 2: Run the Application
Execute the compiled class file using `java`:
```bash
java GradeTracker
```

---

## Example Usage Flow

When you start the application, you will be presented with a menu:

```text
=========================================
          STUDENT GRADE TRACKER          
=========================================
 1. Add Student Grade
 2. Display Student List & Summary Report
 3. Exit
=========================================
Enter your choice: 
```

1. **Option 1**: Enter new students. The program validates that student IDs are unique, names are not blank, and grades are valid decimal numbers between `0.0` and `100.0`.
2. **Option 2**: View the calculated stats cards and student records list.
3. **Option 3**: Safely exit the application.
