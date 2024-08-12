import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class Main extends Application {

    private Stage primaryStage;
    private ListView<Option> listView;
    private Map<String, Option> optionsMap;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        optionsMap = new LinkedHashMap<>();
        listView = new ListView<>();
        loadOptions();

        Label infoLabel = new Label();

        Button addOptionButton = new Button("Add Option");
        addOptionButton.setOnAction(e -> openAddOptionWindow());

        Button editButton = new Button("Edit Option");
        editButton.setOnAction(e -> openEditOptionWindow());

        Button deleteButton = new Button("Delete Option");
        deleteButton.setOnAction(e -> deleteOption());

        VBox managementPane = new VBox(10, addOptionButton, editButton, deleteButton);
        managementPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(new VBox(20, listView, managementPane, infoLabel));

        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Option Manager");
        primaryStage.getIcons().add(new Image("file:/home/shreyash/Projects/Bash_automation_And_QuickLaunch_application/QuickLauch_application/Quick.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

        // Handle double-click to open selected option
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Option selectedOption = listView.getSelectionModel().getSelectedItem();
                if (selectedOption != null) {
                    handleSelection(selectedOption, infoLabel);
                } else {
                    infoLabel.setText("Please select an option from the list.");
                }
            }
        });

        // Handle Enter key for selection
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    Option selectedOption = listView.getSelectionModel().getSelectedItem();
                    if (selectedOption != null) {
                        handleSelection(selectedOption, infoLabel);
                    } else {
                        infoLabel.setText("Please select an option from the list.");
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void openAddOptionWindow() {
        Stage addOptionStage = new Stage();
        VBox addOptionPane = new VBox(10);

        TextField descField = new TextField();
        descField.setPromptText("Option Description");
        descField.getStyleClass().add("text-field");

        TextField cmdField = new TextField();
        cmdField.setPromptText("Command (comma separated)");
        cmdField.getStyleClass().add("text-field");

        TextField linkField = new TextField();
        linkField.setPromptText("Link (comma separated)");
        linkField.getStyleClass().add("text-field");

        Button addOptionButton = new Button("Add Option");
        addOptionButton.getStyleClass().add("button");
        addOptionButton.setOnAction(e -> {
            String desc = descField.getText();
            String cmds = cmdField.getText();
            String links = linkField.getText();
            if (!desc.isEmpty()) {
                Option option = optionsMap.computeIfAbsent(desc, Option::new);
                if (!cmds.isEmpty()) {
                    for (String cmd : cmds.split(",")) {
                        option.addCommand(cmd.trim());
                    }
                }
                if (!links.isEmpty()) {
                    for (String link : links.split(",")) {
                        option.addLink(link.trim());
                    }
                }
                saveOptions();
                loadOptions();
                descField.clear();
                cmdField.clear();
                linkField.clear();
                addOptionStage.close();
            }
        });

        addOptionPane.getChildren().addAll(descField, cmdField, linkField, addOptionButton);
        addOptionPane.setPadding(new Insets(20));
        addOptionPane.getStyleClass().add("vbox");

        Scene addOptionScene = new Scene(addOptionPane, 500, 300);
        addOptionScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        addOptionStage.setTitle("Add Option");
        addOptionStage.setScene(addOptionScene);
        addOptionStage.show();
    }

    private void openEditOptionWindow() {
        Option selectedOption = listView.getSelectionModel().getSelectedItem();
        if (selectedOption == null) {
            return;
        }

        Stage editOptionStage = new Stage();
        VBox editOptionPane = new VBox(10);

        TextField descField = new TextField(selectedOption.getDescription());
        descField.setPromptText("Option Description");
        descField.getStyleClass().add("text-field");

        TextField cmdField = new TextField(String.join(",", selectedOption.getCommands()));
        cmdField.setPromptText("Command (comma separated)");
        cmdField.getStyleClass().add("text-field");

        TextField linkField = new TextField(String.join(",", selectedOption.getLinks()));
        linkField.setPromptText("Link (comma separated)");
        linkField.getStyleClass().add("text-field");

        Button updateButton = new Button("Update Option");
        updateButton.getStyleClass().add("button");
        updateButton.setOnAction(e -> {
            String newDesc = descField.getText();
            String cmds = cmdField.getText();
            String links = linkField.getText();

            if (!newDesc.isEmpty()) {
                selectedOption.setDescription(newDesc);
                selectedOption.clearCommands();
                selectedOption.clearLinks();

                if (!cmds.isEmpty()) {
                    for (String cmd : cmds.split(",")) {
                        selectedOption.addCommand(cmd.trim());
                    }
                }
                if (!links.isEmpty()) {
                    for (String link : links.split(",")) {
                        selectedOption.addLink(link.trim());
                    }
                }
                optionsMap.put(newDesc, selectedOption);
                saveOptions();
                loadOptions();
                editOptionStage.close();
            }
        });

        editOptionPane.getChildren().addAll(descField, cmdField, linkField, updateButton);
        editOptionPane.setPadding(new Insets(20));
        editOptionPane.getStyleClass().add("vbox");

        Scene editOptionScene = new Scene(editOptionPane, 500, 300);
        editOptionScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        
        editOptionStage.setTitle("Edit Option");
        editOptionStage.setScene(editOptionScene);
        editOptionStage.show();
    }

    private void deleteOption() {
        Option selectedOption = listView.getSelectionModel().getSelectedItem();
        if (selectedOption == null) {
            return;
        }

        optionsMap.remove(selectedOption.getDescription());
        saveOptions();
        loadOptions();
    }

    private void loadOptions() {
        listView.getItems().clear();
        optionsMap.clear();

        try (BufferedReader cmdReader = new BufferedReader(new FileReader("commands.txt"));
             BufferedReader linkReader = new BufferedReader(new FileReader("links.txt"))) {

            String line;
            while ((line = cmdReader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    Option option = new Option(parts[0]);
                    for (String cmd : parts[1].split(",")) {
                        option.addCommand(cmd.trim());
                    }
                    optionsMap.put(option.getDescription(), option);
                }
            }
            
            while ((line = linkReader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    Option option = optionsMap.get(parts[0]);
                    if (option == null) {
                        option = new Option(parts[0]);
                        optionsMap.put(option.getDescription(), option);
                    }
                    for (String link : parts[1].split(",")) {
                        option.addLink(link.trim());
                    }
                }
            }

            listView.getItems().addAll(optionsMap.values());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveOptions() {
        try (BufferedWriter cmdWriter = new BufferedWriter(new FileWriter("commands.txt"));
             BufferedWriter linkWriter = new BufferedWriter(new FileWriter("links.txt"))) {

            for (Option option : optionsMap.values()) {
                String desc = option.getDescription();
                if (!option.getCommands().isEmpty()) {
                    cmdWriter.write(desc + "=" + String.join(",", option.getCommands()));
                    cmdWriter.newLine();
                }
                if (!option.getLinks().isEmpty()) {
                    linkWriter.write(desc + "=" + String.join(",", option.getLinks()));
                    linkWriter.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSelection(Option option, Label infoLabel) {
        new Thread(() -> {
            try {
                // Execute commands
                for (String command : option.getCommands()) {
                    String[] cmdParts = command.trim().split("\\s+");
                    if (cmdParts.length > 0) {
                        new ProcessBuilder(cmdParts).start();
                    }
                }

                // Open links in browser
                for (String link : option.getLinks()) {
                    openInBrowser(link.trim());
                }

                javafx.application.Platform.runLater(() -> primaryStage.setIconified(true));
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    infoLabel.setText("Error: " + e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void openInBrowser(String url) {
        try {
            new ProcessBuilder("xdg-open", url).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
