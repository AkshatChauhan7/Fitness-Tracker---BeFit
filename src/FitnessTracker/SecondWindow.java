package FitnessTracker;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javax.sound.sampled.Line;
import java.time.LocalDate;

public class SecondWindow {
    private String username;
    private MySQLDatabaseConnection databaseConnection;
    private VBox contentArea;

    private final String[] motivationalQuotes = {
            "Push yourself, because no one else is going to do it for you.",
            "Every workout counts. Keep going!",
            "Don’t limit your challenges. Challenge your limits.",
            "You don’t have to be extreme, just consistent.",
            "Strive for progress, not perfection.",
            "A little progress each day adds up to big results.",
            "You’re one workout away from a good mood.",
            "Train insane or remain the same."
    };

    private String getRandomMotivationalQuote() {
        int index = (int) (Math.random() * motivationalQuotes.length);
        return motivationalQuotes[index];
    }

    public SecondWindow(String username) {
        this.username = username;
        this.databaseConnection = new MySQLDatabaseConnection();
        this.databaseConnection.connect();
    }

    public void show(Stage primaryStage) {
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #f4f7fa;");

        VBox sidebar = createSidebar(primaryStage);
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setPrefWidth(950);

        root.getChildren().addAll(sidebar, contentArea);

        loadDashboard();

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Fitness Tracker Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar(Stage primaryStage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #20262E;");
        sidebar.setPadding(new Insets(20));
        sidebar.setSpacing(20);

        HBox profileBox = new HBox();
        profileBox.setAlignment(Pos.BASELINE_LEFT);
        profileBox.setPadding(new Insets(10));

        String displayName = databaseConnection.getUserName(username);
        if (displayName == null || displayName.isEmpty()) displayName = username;

        Label greetingLabel = new Label("Hi " + displayName + "!");
        greetingLabel.setTextFill(Color.WHITE);
        greetingLabel.setFont(Font.font("Arial", 25));
        greetingLabel.setStyle("-fx-font-weight: bold;");

        profileBox.getChildren().add(greetingLabel);

        String[] menuItems = {"Dashboard", "Log Workout", "Log Activity", "Profile"};
        VBox menuBox = new VBox(10);

        for (String item : menuItems) {
            Label menuLabel = new Label(item);
            menuLabel.setPrefWidth(210);
            menuLabel.setPadding(new Insets(8));
            menuLabel.setTextFill(Color.WHITE);
            menuLabel.setFont(Font.font(14));

            menuLabel.setOnMouseClicked(e -> {
                menuBox.getChildren().forEach(node -> {
                    if (node instanceof Label) {
                        ((Label) node).setStyle("");
                        ((Label) node).setTextFill(Color.WHITE);
                    }
                });

                menuLabel.setStyle("-fx-background-color: #2C3540;");
                menuLabel.setTextFill(Color.web("#3B82F6"));

                switch (item) {
                    case "Dashboard":
                        loadDashboard();
                        break;
                    case "Log Workout":
                        loadWorkoutForm();
                        break;
                    case "Log Activity":
                        loadActivityForm();
                        break;
                    case "Profile":
                        loadProfileView();
                        break;
                    default:
                        loadDashboard();
                        break;
                }
            });

            if (item.equals("Dashboard")) {
                menuLabel.setStyle("-fx-background-color: #2C3540;");
                menuLabel.setTextFill(Color.web("#3B82F6"));
            }

            menuBox.getChildren().add(menuLabel);
        }

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white;");
        logoutBtn.setPrefWidth(210);
        logoutBtn.setOnAction(e -> {
            Main main = new Main();
            main.start(primaryStage);
        });

        Label burnedCalories = new Label("Burned Calories Calc");
        burnedCalories.setPrefWidth(210);
        burnedCalories.setPadding(new Insets(8));
        burnedCalories.setTextFill(Color.WHITE);;
        burnedCalories.setFont(Font.font(14));
        burnedCalories.setOnMouseClicked(e -> showBurnedCaloriesCalculator());

        menuBox.getChildren().add(burnedCalories);

        Button bmiButton = new Button("Know Your BMI");
        bmiButton.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white;");
        bmiButton.setPrefWidth(210);
        bmiButton.setOnAction(e -> openBMICalculator());

        sidebar.getChildren().addAll(bmiButton);

        Button calorieNeedButton = new Button("Daily Calorie Need");
        calorieNeedButton.setStyle("-fx-background-color: #1DCD9F; -fx-text-fill: white; -fx-font-weight: bold;");
        calorieNeedButton.setPrefWidth(210);
        calorieNeedButton.setOnAction(e -> openCalorieNeedCalculator());

        sidebar.getChildren().add(calorieNeedButton);

        sidebar.getChildren().addAll(profileBox, menuBox, new Separator(), logoutBtn);
        return sidebar;
    }

    private void loadDashboard() {
        contentArea.getChildren().clear();

        Label quoteLabel = new Label(getRandomMotivationalQuote());
        quoteLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #3B82F6; -fx-font-weight: bold; -fx-font-style: italic;");

        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setFont(Font.font(24));
        dashboardTitle.setStyle("-fx-font-weight: bold;");

        GridPane summaryGrid = createSummaryGrid();
        HBox chartsSection = createChartsSectionWithDropdown();

        contentArea.getChildren().addAll(quoteLabel, dashboardTitle, summaryGrid, chartsSection);
    }

    private GridPane createSummaryGrid() {
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(20);
        summaryGrid.setVgap(20);

        double totalCalories = databaseConnection.getTotalCaloriesBurned(username);
        double totalDistance = databaseConnection.getTotalDistance(username);
        int totalSteps = databaseConnection.getTotalSteps(username);

        String[][] cardDetails = {
                {"Total Calories Burned", String.format("%.1f cal", totalCalories)},
                {"Total Distance Covered", String.format("%.1f km", totalDistance)},
                {"Total Steps Taken", String.format("%d", totalSteps)},
                {"Current Weight", String.format("%.1f kg", databaseConnection.getUserWeight(username))},
                {"Current Age", String.format("%d years", databaseConnection.getUserAge(username))},
                {"Recent Activity", databaseConnection.getLastActivityOrWorkoutDate(username)}

        };

        for (int i = 0; i < cardDetails.length; i++) {
            VBox card = createSummaryCard(cardDetails[i][0], cardDetails[i][1]);
            summaryGrid.add(card, i % 3, i / 3);
        }
        return summaryGrid;
    }

    private VBox createSummaryCard(String title, String value) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPadding(new Insets(15));
        card.setPrefSize(280, 120);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.GRAY);
        titleLabel.setFont(Font.font(14));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font(18));
        valueLabel.setStyle("-fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createChartsSectionWithDropdown() {
        HBox chartsSection = new HBox(20);

        // Workout Chart Section
        VBox workoutChartBox = new VBox(10);
        Label workoutLabel = new Label("Recent Workouts");
        workoutLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<Integer> workoutEntrySelector = new ComboBox<>();
        workoutEntrySelector.getItems().addAll(10, 30, 100);
        workoutEntrySelector.setValue(10);

        // Initial Workout Chart
        LineChart<String, Number> workoutChart = createWorkoutChart(workoutEntrySelector.getValue());
        workoutChartBox.getChildren().addAll(workoutLabel, workoutEntrySelector, workoutChart);

        workoutEntrySelector.setOnAction(e -> {
            LineChart<String, Number> updatedChart = createWorkoutChart(workoutEntrySelector.getValue());
            workoutChartBox.getChildren().remove(2);
            workoutChartBox.getChildren().add(updatedChart);
        });

        // Activity Chart Section
        VBox activityChartBox = new VBox(10);
        Label activityLabel = new Label("Steps and Distance");
        activityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<Integer> activityEntrySelector = new ComboBox<>();
        activityEntrySelector.getItems().addAll(10, 30, 100);
        activityEntrySelector.setValue(10);

        // Initial Activity Chart
        StackPane activityChart = createActivityChart(username, activityEntrySelector.getValue());
        activityChartBox.getChildren().addAll(activityLabel, activityEntrySelector, activityChart);

        activityEntrySelector.setOnAction(e -> {
            StackPane updatedActivityChart = createActivityChart(username, activityEntrySelector.getValue());
            activityChartBox.getChildren().remove(2); // Remove old chart
            activityChartBox.getChildren().add(updatedActivityChart); // Add new chart
        });

        chartsSection.getChildren().addAll(workoutChartBox, activityChartBox);
        return chartsSection;
    }
    
    private LineChart<String, Number> createWorkoutChart(int limit) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Calories (kcal)");
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Calories Burned");
        lineChart.setPrefWidth(450);

        XYChart.Series<String, Number> workoutSeries = databaseConnection.getUserWorkoutData(username, limit);
        if (workoutSeries != null) {
            lineChart.getData().add(workoutSeries);
        }
        return lineChart;
    }

    public StackPane createActivityChart(String username, int limit)
    {
        MySQLDatabaseConnection db = new MySQLDatabaseConnection();

        // X-axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        // Shared Y-axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Steps / Distance (scaled)");

        // LineChart
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Steps and Distance vs Date");
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.setPrefWidth(450);

        // Steps Series
        XYChart.Series<String, Number> stepsSeries = db.getUserStepsData(username, limit);
        stepsSeries.setName("Steps");

        // Distance Series (scaled to steps range, e.g., km × 1000)
        XYChart.Series<String, Number> distanceSeries = new XYChart.Series<>();
        distanceSeries.setName("Distance (×1000 km)");

        XYChart.Series<String, Number> originalDistance = db.getUserDistanceData(username, limit);
        for (XYChart.Data<String, Number> data : originalDistance.getData()) {
            double scaled = data.getYValue().doubleValue() * 1000;
            distanceSeries.getData().add(new XYChart.Data<>(data.getXValue(), scaled));
        }

        chart.getData().addAll(stepsSeries, distanceSeries);

        // Style
        Platform.runLater(() -> {
            if (stepsSeries.getNode() != null)
                stepsSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #FF9800; -fx-stroke-width: 3px;");
            if (distanceSeries.getNode() != null)
                distanceSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #2196F3; -fx-stroke-width: 3px;");
        });

        StackPane stackPane = new StackPane(chart);
        return stackPane;
    }

    private void loadWorkoutForm() {
        contentArea.getChildren().clear();

        Label title = new Label("Log Your Workout");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        title.setFont(Font.font(27));
        title.setStyle("-fx-font-weight: bold;");

        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4); -fx-padding: 30px;");
        formContainer.setPadding(new Insets(20));
        formContainer.setMaxWidth(600);

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa;");

        Label typeLabel = new Label("Workout Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Running", "Jogging", "Walking" , "Others");
        typeComboBox.setValue("Running");
        typeComboBox.setMaxWidth(Double.MAX_VALUE);
        typeComboBox.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label durationLabel = new Label("Duration (minutes):");
        TextField durationField = new TextField();
        durationField.setPromptText("e.g., 30");
        durationField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label caloriesLabel = new Label("Calories Burned (in kcal):");
        TextField caloriesField = new TextField();
        caloriesField.setPromptText("e.g., 250");
        caloriesField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label workoutFormulaLabel = new Label(
                "Formula Used for Calories Need Calculator:\n" +
                        "For Men: BMR = 10 × weight + 6.25 × height - 5 × age + 5\n" +
                        "For Women: BMR = 10 × weight + 6.25 × height - 5 × age - 161\n\n" +
                        "Daily Calorie Need = BMR × Activity Level Multiplier"
        );
        workoutFormulaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-font-weight: bold");
        workoutFormulaLabel.setWrapText(true);

        Label multiplierInfoLabel = new Label(
                "🛠️ Activity Multipliers:\n" +
                        "• Sedentary: ×1.2\n" +
                        "• Lightly Active: ×1.375\n" +
                        "• Moderate: ×1.55\n" +
                        "• Very Active: ×1.725\n" +
                        "• Super Active: ×1.9"
        );
        multiplierInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        multiplierInfoLabel.setWrapText(true);

        Button submitButton = new Button("Log Workout");
        submitButton.setStyle("-fx-background-color: #1DCD9F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10px 20px;");
        submitButton.setPrefWidth(150);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        submitButton.setOnAction(e -> {
            try {
                String workoutType = typeComboBox.getValue();
                double caloriesBurned = Double.parseDouble(caloriesField.getText());
                int duration = Integer.parseInt(durationField.getText());

                LocalDate selectedDate = datePicker.getValue();
                String formattedDate = selectedDate.toString() + " 08:00:00";

                if (databaseConnection.logWorkout(username, workoutType, caloriesBurned, duration, formattedDate)) {
                    errorLabel.setText("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Workout logged successfully!");
                    alert.showAndWait();
                    loadDashboard();
                } else {
                    errorLabel.setText("Failed to log workout. Please try again.");
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers for duration and calories.");
            }
        });

        formContainer.getChildren().addAll(
                dateLabel, datePicker,
                typeLabel, typeComboBox,
                durationLabel, durationField,
                caloriesLabel, caloriesField,
                errorLabel, submitButton, workoutFormulaLabel
        );

        contentArea.getChildren().addAll(title, formContainer);
    }

    private void loadActivityForm() {
        contentArea.getChildren().clear();

        Label title = new Label("Log Your Daily Activity");
        title.setFont(Font.font(24));
        title.setStyle("-fx-font-weight: bold;");

        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        formContainer.setPadding(new Insets(20));
        formContainer.setMaxWidth(600);

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa;");

        Label stepsLabel = new Label("Steps Taken:");
        TextField stepsField = new TextField();
        stepsField.setPromptText("e.g., 8500");
        stepsField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label distanceLabel = new Label("Distance (km):");
        TextField distanceField = new TextField();
        distanceField.setPromptText("e.g., 5.2");
        distanceField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Button submitButton = new Button("Log Activity");
        submitButton.setStyle("-fx-background-color: #1DCD9F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12px; -fx-padding: 10px 20px;");
        submitButton.setPrefWidth(150);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        submitButton.setOnAction(e -> {
            try {
                int steps = Integer.parseInt(stepsField.getText());
                double distance = Double.parseDouble(distanceField.getText());

                LocalDate selectedDate = datePicker.getValue();
                String formattedDate = selectedDate.toString() + " 08:00:00";

                if (databaseConnection.logActivity(username, steps, distance, formattedDate)) {
                    errorLabel.setText("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Activity logged successfully!");
                    alert.showAndWait();
                    loadDashboard();
                } else {
                    errorLabel.setText("Failed to log activity. Please try again.");
                }

            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers for steps and distance.");
            }
        });

        formContainer.getChildren().addAll(
                dateLabel, datePicker,
                stepsLabel, stepsField,
                distanceLabel, distanceField,
                errorLabel, submitButton
        );

        contentArea.getChildren().addAll(title, formContainer);
    }

    private void openBMICalculator() {
        Stage bmiStage = new Stage();
        bmiStage.setTitle("Know Your BMI");

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("""
        -fx-background-color: linear-gradient(to bottom, #ffffff, #f0f4f8);
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0.3, 0, 4);
    """);

        Label title = new Label("Check Your Body Mass Index");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        Label weightLabel = new Label("Weight (kg):");
        TextField weightField = new TextField();
        weightField.setPromptText("e.g. 70");
        weightField.setMaxWidth(Double.MAX_VALUE);
        weightField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Label heightLabel = new Label("Height (cm):");
        TextField heightField = new TextField();
        heightField.setPromptText("e.g. 175");
        heightField.setMaxWidth(Double.MAX_VALUE);
        heightField.setStyle("-fx-background-radius: 8; -fx-padding: 8;");

        Button calculateButton = new Button("Calculate BMI");
        calculateButton.setStyle("""
        -fx-background-color: #3B82F6;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 6;
        -fx-padding: 8 16;
    """);

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1f2937;");

        // Stretch fields across the window
        VBox.setVgrow(weightField, Priority.ALWAYS);
        VBox.setVgrow(heightField, Priority.ALWAYS);

        calculateButton.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(calculateButton, Priority.NEVER);

        calculateButton.setOnAction(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText());
                double heightCm = Double.parseDouble(heightField.getText());
                double heightM = heightCm / 100.0;

                double bmi = weight / (heightM * heightM);
                String category;
                if (bmi < 18.5) category = "Underweight";
                else if (bmi < 25) category = "Normal";
                else if (bmi < 30) category = "Overweight";
                else category = "Obese";

                resultLabel.setText(String.format("Your BMI is %.1f (%s)", bmi, category));
            } catch (NumberFormatException ex) {
                resultLabel.setText("⚠ Please enter valid numbers.");
            }
        });

        root.getChildren().addAll(title, weightLabel, weightField, heightLabel, heightField, calculateButton, resultLabel);

        Scene scene = new Scene(root, 320, 360);
        bmiStage.setScene(scene);
        bmiStage.setResizable(false);
        bmiStage.show();
    }

    private void openCalorieNeedCalculator() {
        Stage calorieStage = new Stage();
        calorieStage.setTitle("Daily Calorie Needs Calculator");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.3, 0, 2);"
        );

        Label heading = new Label("Daily Calorie Needs");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2d4d23;");

        Label ageLabel = createInputLabel("Age:");
        TextField ageField = new TextField();
        styleInputField(ageField);

        Label weightLabel = createInputLabel("Weight (kg):");
        TextField weightField = new TextField();
        styleInputField(weightField);

        Label heightLabel = createInputLabel("Height (cm):");
        TextField heightField = new TextField();
        styleInputField(heightField);

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");
        genderBox.setPromptText("Select Gender");
        styleComboBox(genderBox);

        ComboBox<String> activityBox = new ComboBox<>();
        activityBox.getItems().addAll(
                "Not active/no exercise",
                "Lightly active",
                "Moderately active",
                "Very active",
                "Super active"
        );
        activityBox.setPromptText("Activity Level");
        styleComboBox(activityBox);

        Button calculateButton = new Button("Calculate");
        calculateButton.setStyle(
                "-fx-background-color: #1DCD9F;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 8px 16px;" +
                        "-fx-font-size: 14px;"
        );
        calculateButton.setPrefWidth(180);

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2d4d23; -fx-font-weight: bold;");
        resultLabel.setWrapText(true);
        resultLabel.setAlignment(Pos.CENTER);

        calculateButton.setOnAction(e -> {
            try {
                int age = Integer.parseInt(ageField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());
                String gender = genderBox.getValue();
                String activityLevel = activityBox.getValue();

                if (gender == null || activityLevel == null) {
                    resultLabel.setText("Please select Gender and Activity Level!");
                    return;
                }

                double bmr;
                if (gender.equals("Male")) {
                    bmr = 10 * weight + 6.25 * height - 5 * age + 5;
                } else {
                    bmr = 10 * weight + 6.25 * height - 5 * age - 161;
                }

                double multiplier = switch (activityLevel) {
                    case "Not active/no exercise" -> 1.2;
                    case "Lightly active" -> 1.375;
                    case "Moderately active" -> 1.55;
                    case "Very active" -> 1.725;
                    case "Super active" -> 1.9;
                    default -> 1.2;
                };

                double caloriesNeeded = bmr * multiplier;
                resultLabel.setText(String.format("You need around %.0f calories/day.", caloriesNeeded));
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numbers!");
            }
        });

        root.getChildren().addAll(
                heading,
                ageLabel, ageField,
                weightLabel, weightField,
                heightLabel, heightField,
                genderBox,
                activityBox,
                calculateButton,
                resultLabel
        );

        Scene scene = new Scene(root, 400, 500);
        calorieStage.setScene(scene);
        calorieStage.show();
    }
    private void styleInputField(TextField field) {
        field.setMaxWidth(250);
        field.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 8px;" +
                        "-fx-border-color: lightgray;" +
                        "-fx-font-size: 13px;"
        );
    }

    private void styleComboBox(ComboBox<String> box) {
        box.setMaxWidth(250);
        box.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-padding: 8px;" +
                        "-fx-border-color: lightgray;" +
                        "-fx-font-size: 13px;"
        );
    }

    private Label createInputLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        return label;
    }

    private void showBurnedCaloriesCalculator() {
        Stage popup = new Stage();
        popup.setTitle("Calories Burned Calculator");

        // Outer Container
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 16;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0.2, 0, 6);
            """);

        // Title
        Label title = new Label("Calories Burned");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2e5d2e;");

        // Steps input
        Label stepsLabel = new Label("Steps Taken:");
        stepsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        TextField stepsInput = new TextField();
        stepsInput.setPromptText("e.g., 8000");
        stepsInput.setStyle("-fx-background-radius: 6; -fx-border-color: #ccc; -fx-border-radius: 6;");
        stepsInput.setMaxWidth(200);

        // Duration input
        Label durationLabel = new Label("Duration (minutes):");
        durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        TextField durationInput = new TextField();
        durationInput.setPromptText("e.g., 45");
        durationInput.setStyle("-fx-background-radius: 6; -fx-border-color: #ccc; -fx-border-radius: 6;");
        durationInput.setMaxWidth(200);

        // Calculate Button
        Button calculateBtn = new Button("Estimate");
        calculateBtn.setStyle("""
            -fx-background-color: #2ecc71;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            -fx-padding: 8 16;
            -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 4, 0.0, 0, 2);
        """);

        calculateBtn.setCursor(Cursor.HAND);

        Label formulaLabel = new Label("Formula: (Steps × 0.04) + (Duration × 0.17)");
        formulaLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Label factLabel = new Label("Avg. 100 steps per minute!");
        factLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #777;");

        // Result label
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: #2e5d2e; -fx-font-size: 16px; -fx-font-weight: bold;");

        calculateBtn.setOnAction(e -> {
            try {
                int steps = Integer.parseInt(stepsInput.getText());
                double minutes = Double.parseDouble(durationInput.getText());

                if (steps < 0 || minutes < 0) {
                    resultLabel.setText("Please enter valid non-negative numbers.");
                    return;
                }

                double calories = (steps * 0.04) + (minutes * 0.17);
                resultLabel.setText(String.format("You burned: %.2f kcal 🔥", calories));
            } catch (NumberFormatException ex) {
                resultLabel.setText("❗ Enter valid numbers.");
                resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        container.getChildren().addAll(
                title,
                stepsLabel, stepsInput,
                durationLabel, durationInput,
                calculateBtn,
                resultLabel,
                formulaLabel,
                factLabel
        );

        VBox layout = new VBox(container);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f3f7f9;");

        Scene scene = new Scene(layout, 400, 400);
        popup.setScene(scene);
        popup.show();
    }

    private void loadProfileView() {
        contentArea.getChildren().clear();

        Label title = new Label("Your Profile");
        title.setFont(Font.font(24));
        title.setStyle("-fx-font-weight: bold;");

        VBox profileContainer = new VBox(15);
        profileContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        profileContainer.setPadding(new Insets(20));
        profileContainer.setMaxWidth(600);

        String name = databaseConnection.getUserName(username);
        int age = databaseConnection.getUserAge(username);
        double weight = databaseConnection.getUserWeight(username);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(name != null ? name : "");
        nameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label ageLabel = new Label("Age:");
        TextField ageField = new TextField(String.valueOf(age));
        ageField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Label weightLabel = new Label("Weight (kg):");
        TextField weightField = new TextField(String.valueOf(weight));
        weightField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-background-color: #f4f7fa; -fx-border-color: #adacac;");

        Button updateButton = new Button("Update Profile");
        updateButton.setStyle("-fx-background-color: #1DCD9F; -fx-text-fill: white; -fx-font-weight: bold; ");
        updateButton.setPrefWidth(150);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        updateButton.setOnAction(e -> {
            String newName = nameField.getText();
            String newAge = ageField.getText();
            String newWeight = weightField.getText();

            if (newName == null || newName.isEmpty()) {
                errorLabel.setText("Name cannot be empty.");
                return;
            }

            if (databaseConnection.updateUserProfile(username, newName, newAge, newWeight)) {
                errorLabel.setText("");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Profile updated successfully!");
                alert.showAndWait();
                loadProfileView();
            } else {
                errorLabel.setText("Failed to update profile. Please check your input.");
            }
        });

        profileContainer.getChildren().addAll(
                nameLabel, nameField,
                ageLabel, ageField,
                weightLabel, weightField,
                errorLabel, updateButton
        );
        contentArea.getChildren().addAll(title, profileContainer);
    }
}