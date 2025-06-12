package FitnessTracker;

import javafx.application.Application;
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

public class Main extends Application {

    private MySQLDatabaseConnection databaseConnection;

    @Override
    public void start(Stage primaryStage) {
        databaseConnection = new MySQLDatabaseConnection();
        databaseConnection.connect();

        primaryStage.setTitle("🏋️ Fitness Tracker - Login");

        // Root layout with background gradient
        BorderPane root = new BorderPane();
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1f4037, #99f2c8);" +
                        "-fx-font-family: 'Segoe UI';"
        );

        // VBox for form layout
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(40));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.06);" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 20, 0, 0, 8);"
        );

        // Drop shadow for better glass effect
        formBox.setEffect(new DropShadow(15, Color.BLACK));

        // Title texts
        Text appTitle = new Text("🏋️ FITNESS TRACKER");
        appTitle.setFont(Font.font("Segoe UI Semibold", 32));
        appTitle.setFill(Color.web("#ffffff"));

        Text subtitle = new Text("Welcome Back");
        subtitle.setFont(Font.font("Segoe UI", 22));
        subtitle.setFill(Color.web("#d0f0c0"));

        // Input fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        styleInput(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);

        // Warning / status label
        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.web("#ff6f61"));
        statusLabel.setFont(Font.font("Segoe UI", 14));

        // Login button
        Button loginButton = new Button("Login");
        styleButton(loginButton, "#2ecc71");

        // Register button
        Button registerButton = new Button("Register");
        styleButton(registerButton, "#3498db");

        // Button actions
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (databaseConnection.validateLogin(username, password)) {
                SecondWindow secondWindow = new SecondWindow(username);
                secondWindow.show(primaryStage);
            } else {
                statusLabel.setText("⚠️ Invalid username or password.");
            }
        });

        registerButton.setOnAction(e -> {
            RegisterWindow registerWindow = new RegisterWindow(databaseConnection);
            registerWindow.show(primaryStage);
        });

        // Add elements to form
        formBox.getChildren().addAll(appTitle, subtitle, usernameField, passwordField,
                loginButton, registerButton, statusLabel);

        // Add form to center
        root.setCenter(formBox);

        // Scene setup
        Scene scene = new Scene(root, 600, 500);
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

    public static void main(String[] args) {
        launch(args);
    }
}
