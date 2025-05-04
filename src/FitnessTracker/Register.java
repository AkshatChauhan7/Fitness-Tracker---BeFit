package FitnessTracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class RegisterWindow {
    private MySQLDatabaseConnection databaseConnection;

    public RegisterWindow(MySQLDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Fitness Tracker - Register");

        // Left pane (green background for theme)
        VBox leftPane = new VBox();
        leftPane.setStyle("-fx-background-color: #2d4d23;");  // Dark green background
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPrefWidth(200);

        Label logoLabel = new Label("Fitness Tracker");
        logoLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 24px; -fx-font-weight: bold;");
        leftPane.getChildren().add(logoLabel);

        // Right pane for registration form
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50));
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #2d4d23; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Heading: Register in the Fitness Tracker
        Label headingLabel = new Label("Register in the Fitness Tracker");
        headingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2d4d23;");
        grid.add(headingLabel, 0, 0, 2, 1);  // Span the heading across 2 columns

        // Username, password, name, age, weight fields
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 16px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        Label ageLabel = new Label("Age:");
        ageLabel.setStyle("-fx-font-size: 16px;");
        TextField ageField = new TextField();
        ageField.setPromptText("Enter your age");

        Label weightLabel = new Label("Weight:");
        weightLabel.setStyle("-fx-font-size: 16px;");
        TextField weightField = new TextField();
        weightField.setPromptText("Enter your weight (e.g., 70.5)");

        // Bigger Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2d4d23; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        registerButton.setPrefWidth(180);  // Set wider button size
        registerButton.setOnAction(e -> {
            // Get user input
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String age = ageField.getText();
            String weight = weightField.getText();

            // Validation check
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty() || weight.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
            } else {
                try {
                    int ageInt = Integer.parseInt(age);
                    double weightDouble = Double.parseDouble(weight);
                    boolean success = databaseConnection.registerUser(username, password, name, ageInt, weightDouble);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Registration successful!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Registration failed. Username may already be taken.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid input for age or weight.");
                }
            }
        });

        // Bigger Back to Login button
        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        backButton.setPrefWidth(180);  // Set wider button size
        backButton.setOnAction(e -> {
            Main main = new Main();
            main.start(primaryStage);  // Go back to the login screen
        });

        // Add components to the grid
        grid.add(usernameLabel, 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(nameLabel, 0, 3);
        grid.add(nameField, 1, 3);
        grid.add(ageLabel, 0, 4);
        grid.add(ageField, 1, 4);
        grid.add(weightLabel, 0, 5);
        grid.add(weightField, 1, 5);
        grid.add(registerButton, 1, 6);
        grid.add(backButton, 0, 6);

        // Root pane (combining left and right panes)
        HBox root = new HBox(leftPane, grid);
        HBox.setHgrow(grid, Priority.ALWAYS);

        // Create the scene and show the stage
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Utility method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}