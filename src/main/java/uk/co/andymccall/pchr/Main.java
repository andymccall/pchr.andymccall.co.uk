package uk.co.andymccall.pchr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Powercurve Hash Reporter");

        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();

        MenuBar mainMenu = new MenuBar();
        mainMenu.prefWidthProperty().bind(primaryStage.widthProperty());
        topContainer.getChildren().add(mainMenu);

        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("Refresh");
        MenuItem saveMenuItem = new MenuItem("Save to File...");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        Menu editMenu = new Menu("Edit");
        MenuItem copyMenuItem = new MenuItem("Copy");
        editMenu.getItems().add(copyMenuItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");
        helpMenu.getItems().add(aboutMenuItem);
        aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                aboutDialog();
            }
        });


        mainMenu.getMenus().addAll(fileMenu, editMenu, helpMenu);

        ToolBar toolBar = new ToolBar();

        Button refreshButton = new Button();
        refreshButton.setGraphic(new ImageView("/icons/refresh.png"));
        refreshButton.setTooltip(new Tooltip("Refresh the hashes"));
        toolBar.getItems().add(refreshButton);

        Button copyButton = new Button();
        copyButton.setGraphic(new ImageView("/icons/copy.png"));
        copyButton.setTooltip(new Tooltip("Copy the hashes to the clipboard"));
        toolBar.getItems().add(copyButton);

        Button exportButton = new Button();
        exportButton.setGraphic(new ImageView("/icons/export.png"));
        exportButton.setTooltip(new Tooltip("Export the hashes to a file..."));
        toolBar.getItems().add(exportButton);

        toolBar.prefWidthProperty().bind(primaryStage.widthProperty());
        topContainer.getChildren().add(toolBar);

        root.setTop(topContainer);

        VBox hashes = new VBox();
        hashes.setPadding(new Insets(20, 25, 50, 50));
        hashes.setSpacing(10);

        String hash;

        try {
             hash = getFileHash("/Users/andymccall/IdeaProjects/pchr.andymccall.co.uk/resource/icons/copy.png");
            Label preLabel = new Label("PRE.jar : " + hash);
            hashes.getChildren().add(preLabel);

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }
        Label postLabel = new Label("POST.jar :");


        hashes.getChildren().add(postLabel);

        topContainer.getChildren().add(hashes);

        Scene scene = new Scene(root, 500, 550, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void aboutDialog() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Powercurve Hash Reporter v0.1");
        alert.setContentText("Written by Andy McCall, mailme@andymccall.co.uk");

        alert.showAndWait();

    }

    public String getFileHash(String filename)
            throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(filename)));
        byte[] digest = md.digest();
        return  DatatypeConverter
                .printHexBinary(digest).toUpperCase();
    }


    public static void main(String[] args) {
        launch(args);
    }
}