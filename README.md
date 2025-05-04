# Fitness Tracker App (JavaFX + MySQL)

## 🚀 Overview

**Fitness Tracker** is a Java-based desktop application built using **JavaFX** for the user interface and **MySQL** as the backend database. It allows users to register, log in, and track their fitness progress including workouts, calories, weight, sleep, and other activities in an intuitive dashboard.

---

## ✨ Features

- 👤 User Registration and Login
- 🏋️ Log Workouts (with duration)
- 🍎 Add Meals (Calories)
- 😴 Track Sleep and Weight
- 📊 Graphical Dashboards (Bar and Line Charts)
- 📅 Date-wise activity selection
- 🟢 Clean and interactive JavaFX UI
- 🛢️ MySQL backend database integration

---

## 📁 Project Structure

FitnessTrackerApp/
│
├── Main.java # Launches the application
├── Register.java # Handles user registration UI & logic
├── SecondWindow.java # Main dashboard after login
├── MySQLDatabaseConnection.java # All database-related methods (login, register, log data, fetch charts)
├── FitnessSQL.sql # SQL script to create and initialize the database
├── /resources/ # (If used) FXML/CSS/Image files
└── README.md # This file

---

## 🛠️ Technologies Used

- Java 17+
- JavaFX
- MySQL
- JDBC

---

## 🧩 Database Schema

Use the provided `FitnessSQL.sql` file to set up the database.

### How to setup:

1. Open your MySQL client (e.g. phpMyAdmin, MySQL Workbench).
2. Create a new database, e.g. `fitness_tracker`.
3. Run the contents of `FitnessSQL.sql` to initialize tables like:
   - `users`
   - `workouts`
   - `nutrition`
   - `sleep`
   - `weight`
   - `activities`

---

## 🔧 How to Run

### Prerequisites:
- Java 17 or later
- JavaFX SDK set up in your IDE (like IntelliJ IDEA)
- MySQL server running
- JDBC connector added to your project classpath

### Steps:

1. Clone or download the project files.
2. Set up the MySQL database using `FitnessSQL.sql`.
3. Modify DB credentials in `MySQLDatabaseConnection.java`:
   ```java
   String url = "jdbc:mysql://localhost:3306/fitness_tracker";
   String username = "root";
   String password = "your_password";
