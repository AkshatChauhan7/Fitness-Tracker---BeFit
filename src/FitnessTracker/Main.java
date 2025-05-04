package FitnessTracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private MySQLDatabaseConnection databaseConnection;

    @Override
    public void start(Stage primaryStage) {
        databaseConnection = new MySQLDatabaseConnection();
        databaseConnection.connect();

        primaryStage.setTitle("Fitness Tracker - Login");

        // Left pane for logo
        VBox leftPane = new VBox();
        leftPane.setStyle("-fx-background-color: #2d4d23;");
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPrefWidth(200);

        Label logoLabel = new Label("Fitness Tracker");
        logoLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 24px; -fx-font-weight: bold;");
        leftPane.getChildren().add(logoLabel);

        // Right pane for login form
        GridPane rightPane = new GridPane();
        rightPane.setPadding(new Insets(50));
        rightPane.setHgap(20);
        rightPane.setVgap(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setStyle("-fx-background-color: #f0f0f0;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 16px;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("@username");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 16px;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(300);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2d4d23; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        loginButton.setPrefWidth(150);

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        registerButton.setPrefWidth(150);

        Label warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: red;");

        // Login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (databaseConnection.validateLogin(username, password)) {
                SecondWindow secondWindow = new SecondWindow(username);
                secondWindow.show(primaryStage);
            } else {
                warningLabel.setText("Invalid username or password.");
            }
        });

        // Register button action to open the Register window
        registerButton.setOnAction(e -> {
            RegisterWindow registerWindow = new RegisterWindow(databaseConnection);
            registerWindow.show(primaryStage);
        });

        // Add components to grid
        rightPane.add(usernameLabel, 0, 0);
        rightPane.add(usernameField, 1, 0);
        rightPane.add(passwordLabel, 0, 1);
        rightPane.add(passwordField, 1, 1);
        rightPane.add(loginButton, 1, 2);
        rightPane.add(registerButton, 1, 3);  // New register button
        rightPane.add(warningLabel, 1, 4);   // Warning label

        // Root pane (combining left and right)
        HBox root = new HBox(leftPane, rightPane);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}