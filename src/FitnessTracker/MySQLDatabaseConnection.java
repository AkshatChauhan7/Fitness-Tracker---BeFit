package FitnessTracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.chart.XYChart;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MySQLDatabaseConnection {

    private Connection connection;

    public boolean connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/fitness_tracker";
            String username = "root";
            String password = "070214";

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");
            return true;

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found.");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateLogin(String username, String password) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("Login validation failed.");
            e.printStackTrace();
            return false;
        }
    }

    // Register a new user
    public boolean registerUser(String username, String password, String name, int age, double weight) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        // First check if username exists
        String checkQuery = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Username already exists
                return false;
            }

            // If not, insert the new user
            String insertQuery = "INSERT INTO users (username, password, name, age, weight) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, name);
                insertStatement.setInt(4, age);
                insertStatement.setDouble(5, weight);
                insertStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Registration failed.");
            e.printStackTrace();
            return false;
        }
    }

    // Update user's profile in the database (name, age, weight)
    public boolean updateUserProfile(String username, String name, String age, String weight) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String query = "UPDATE users SET name = ?, age = ?, weight = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);

            // Add validation for numeric fields
            try {
                preparedStatement.setInt(2, Integer.parseInt(age));
                preparedStatement.setDouble(3, Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format for age or weight");
                return false;
            }

            preparedStatement.setString(4, username);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Profile updated successfully.");
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Log workout with calories, duration, and custom date
    public boolean logWorkout(String username, String workoutType, double caloriesBurned, int duration, String workoutDate) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String query = "INSERT INTO workout_log (username, workout_type, calories, duration, workout_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, workoutType);
            preparedStatement.setDouble(3, caloriesBurned);
            preparedStatement.setInt(4, duration);
            preparedStatement.setString(5, workoutDate); // format: "2025-04-17 08:00:00"
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get the most recent workout date for the user
    public String getLastWorkoutDate(String username) {
        if (connection == null) {
            if (!connect()) {
                return "No data";
            }
        }

        String query = "SELECT workout_date FROM workout_log WHERE username = ? ORDER BY workout_date DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                java.sql.Timestamp timestamp = resultSet.getTimestamp("workout_date");
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy");
                return formatter.format(timestamp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No data";
    }

    // Get most recent workout or activity date
    public String getLastActivityOrWorkoutDate(String username) {
        if (connection == null) {
            if (!connect()) {
                return "No data";
            }
        }

        String query = """
            SELECT MAX(latest_date) AS most_recent
            FROM (
                SELECT MAX(workout_date) AS latest_date FROM workout_log WHERE username = ?
                UNION
                SELECT MAX(date) FROM activity_log WHERE username = ?
            ) AS combined_dates
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                java.sql.Timestamp timestamp = resultSet.getTimestamp("most_recent");
                if (timestamp != null) {
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    return formatter.format(timestamp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No data";
    }

    
    // Log workout with calories
    public boolean logWorkout(String username, String workoutType, double caloriesBurned) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String query = "INSERT INTO workout_log (username, workout_type, calories) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, workoutType);
            preparedStatement.setDouble(3, caloriesBurned);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Log activity (steps and distance)
    public boolean logActivity(String username, int steps, double distance, String activityDate) {
        if (connection == null && !connect()) {
            return false;
        }

        String query = "INSERT INTO activity_log (username, steps, distance, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, steps);
            preparedStatement.setDouble(3, distance);
            preparedStatement.setString(4, activityDate);  // e.g., "2025-04-18 08:00:00"
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Log nutrition with better handling of numeric values
    public boolean logNutrition(String username, String date, String calories) {
        if (connection == null) {
            if (!connect()) {
                return false;
            }
        }

        String query = "INSERT INTO nutrition_log (username, date, calories) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, calories);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve user's name from the database
    public String getUserName(String username) {
        if (connection == null) {
            if (!connect()) {
                return username; // Return username if connection fails
            }
        }

        String query = "SELECT name FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return name != null && !name.isEmpty() ? name : username;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username; // Return username if no name is found
    }

    // Retrieve user's age from the database
    public int getUserAge(String username) {
        if (connection == null) {
            if (!connect()) {
                return 0; // Return 0 if connection fails
            }
        }

        String query = "SELECT age FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("age"); // Return the user's age
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no age is found
    }

    // Retrieve user's weight from the database
    public double getUserWeight(String username) {
        if (connection == null) {
            if (!connect()) {
                return 0.0; // Return 0.0 if connection fails
            }
        }

        String query = "SELECT weight FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("weight"); // Return the user's weight
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0.0 if no weight is found
    }

    // Get total calories burned
    public double getTotalCaloriesBurned(String username) {
        if (connection == null) {
            if (!connect()) {
                return 0.0; // Return 0.0 if connection fails
            }
        }

        String query = "SELECT SUM(calories) AS total_calories FROM workout_log WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double result = resultSet.getDouble("total_calories");
                return resultSet.wasNull() ? 0.0 : result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get total distance covered
    public double getTotalDistance(String username) {
        if (connection == null) {
            if (!connect()) {
                return 0.0; // Return 0.0 if connection fails
            }
        }

        String query = "SELECT SUM(distance) AS total_distance FROM activity_log WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double result = resultSet.getDouble("total_distance");
                return resultSet.wasNull() ? 0.0 : result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get total steps taken
    public int getTotalSteps(String username) {
        if (connection == null) {
            if (!connect()) {
                return 0; // Return 0 if connection fails
            }
        }

        String query = "SELECT SUM(steps) AS total_steps FROM activity_log WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int result = resultSet.getInt("total_steps");
                return resultSet.wasNull() ? 0 : result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Dynamic workout chart data by limit and date (X-axis)
    public XYChart.Series<String, Number> getUserWorkoutData(String username, int limit) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Calories Burned");

        if (connection == null && !connect()) {
            return series;
        }

        Map<String, Double> caloriesPerDate = new TreeMap<>(Collections.reverseOrder());

        String query = "SELECT DATE(workout_date) AS day, SUM(calories) AS total " +
                "FROM workout_log WHERE username = ? GROUP BY day ORDER BY day DESC LIMIT ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getDate("day").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM"));
                double totalCalories = resultSet.getDouble("total");
                caloriesPerDate.put(date, totalCalories);
            }

            // Put the final data in correct (old to new) order
            List<String> sorted = new ArrayList<>(caloriesPerDate.keySet());
            Collections.reverse(sorted);

            for (String date : sorted) {
                series.getData().add(new XYChart.Data<>(date, caloriesPerDate.get(date)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }

    // Get steps data for chart with proper error handling
    public XYChart.Series<String, Number> getUserStepsData(String username, int limit) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Steps");

        if (connection == null && !connect()) return series;

        Map<String, Integer> stepsPerDate = new TreeMap<>();

        String query = "SELECT DATE(date) AS day, SUM(steps) AS total_steps FROM activity_log " +
                "WHERE username = ? GROUP BY day ORDER BY day DESC LIMIT ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getDate("day").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM"));
                int steps = resultSet.getInt("total_steps");
                stepsPerDate.put(date, steps);
            }

            for (Map.Entry<String, Integer> entry : stepsPerDate.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return series;
    }

    // Get distance data for chart with proper error handling
    public XYChart.Series<String, Number> getUserDistanceData(String username, int limit) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distance (km)");

        if (connection == null && !connect()) return series;

        Map<String, Double> distancePerDate = new TreeMap<>();

        String query = "SELECT DATE(date) AS day, SUM(distance) AS total_distance FROM activity_log " +
                "WHERE username = ? GROUP BY day ORDER BY day DESC LIMIT ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = resultSet.getDate("day").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM"));
                double distance = resultSet.getDouble("total_distance");
                distancePerDate.put(date, distance);
            }

            for (Map.Entry<String, Double> entry : distancePerDate.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }

    // Close the connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
