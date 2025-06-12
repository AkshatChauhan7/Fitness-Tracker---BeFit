package FitnessTracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class RegisterWindow {
    private MySQLDatabaseConnection databaseConnection;

    public RegisterWindow(MySQLDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("🏋️ Fitness Tracker - Register");

        // Background gradient
        BorderPane root = new BorderPane();
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1f4037, #99f2c8);" +
                        "-fx-font-family: 'Segoe UI';"
        );

        // Glass-like form card
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 20, 0, 0, 8);"
        );
        formBox.setEffect(new DropShadow(15, Color.BLACK));

        // Title texts
        Text appTitle = new Text("🏋️ FITNESS TRACKER");
        appTitle.setFont(Font.font("Segoe UI Semibold", 32));
        appTitle.setFill(Color.web("#ffffff"));

        Text subtitle = new Text("Create a New Account");
        subtitle.setFont(Font.font("Segoe UI", 22));
        subtitle.setFill(Color.web("#d0f0c0"));

        // Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleInput(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        styleInput(nameField);

        TextField ageField = new TextField();
        ageField.setPromptText("Age");
        styleInput(ageField);

        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        styleInput(weightField);

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.web("#ff6f61"));
        statusLabel.setFont(Font.font("Segoe UI", 14));

        // Buttons
        Button registerButton = new Button("Register");
        styleButton(registerButton, "#2ecc71");

        Button backButton = new Button("Back to Login");
        styleButton(backButton, "#3498db");

        // Register logic (unchanged)
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String age = ageField.getText();
            String weight = weightField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty() || weight.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please fill in all fields.");
            } else {
                try {
                    int ageInt = Integer.parseInt(age);
                    double weightDouble = Double.parseDouble(weight);
                    boolean success = databaseConnection.registerUser(username, password, name, ageInt, weightDouble);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "🎉 Registration successful!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Username already exists.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Invalid input for age or weight.");
                }
            }
        });

        backButton.setOnAction(e -> {
            Main main = new Main();
            main.start(primaryStage);
        });

        // Add all elements to VBox
        formBox.getChildren().addAll(appTitle, subtitle, usernameField, passwordField,
                nameField, ageField, weightField,
                registerButton, backButton, statusLabel);

        root.setCenter(formBox);

        // Scene setup
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void styleInput(TextField field) {
        field.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-prompt-text-fill: #cccccc;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 12;"
        );
        field.setMaxWidth(280);
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 20;"
        ));

        button.setMaxWidth(200);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}
