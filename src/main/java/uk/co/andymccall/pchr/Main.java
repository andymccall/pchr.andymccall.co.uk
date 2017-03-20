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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import static javafx.geometry.Pos.BASELINE_RIGHT;

public class Main extends Application {

    private static String preFileLocation;
    private static String postFileLocation;
    private static String bcFileLocation;
    private static String bcdmFileLocation;

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
        hashes.setPadding(new Insets(10, 10, 10, 10));
        hashes.setSpacing(10);

        String hash;

        try {
            hash = getFileHash(preFileLocation);

            Label preLabel = new Label("PRE.jar");

            TextField preHash = new TextField();
            preHash.setPrefColumnCount(24);
            preHash.setText(hash);
            preHash.setEditable(false);

            Button preCopyButton = new Button();
            preCopyButton.setText("Copy");

            HBox preBox = new HBox();
            preBox.setAlignment(BASELINE_RIGHT);
            preBox.getChildren().addAll(preLabel, preHash,preCopyButton);
            preBox.setSpacing(10);

            hashes.getChildren().add(preBox);


        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }

        try {
            hash = getFileHash(postFileLocation);

            Label postLabel = new Label("POST.jar");

            TextField postHash = new TextField();
            postHash.setPrefColumnCount(24);
            postHash.setText(hash);
            postHash.setEditable(false);

            Button postCopyButton = new Button();
            postCopyButton.setText("Copy");

            HBox postBox = new HBox();
            postBox.setAlignment(BASELINE_RIGHT);
            postBox.getChildren().addAll(postLabel, postHash,postCopyButton);
            postBox.setSpacing(10);

            hashes.getChildren().add(postBox);


        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }

        try {
            hash = getFileHash(bcFileLocation);

            Label bcLabel = new Label("Business Composition.jar");

            TextField bcHash = new TextField();
            bcHash.setPrefColumnCount(24);
            bcHash.setText(hash);
            bcHash.setEditable(false);

            Button bcCopyButton = new Button();
            bcCopyButton.setText("Copy");

            HBox postBox = new HBox();
            postBox.setAlignment(BASELINE_RIGHT);
            postBox.getChildren().addAll(bcLabel, bcHash,bcCopyButton);
            postBox.setSpacing(10);

            hashes.getChildren().add(postBox);


        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }


        try {
            hash = getFileHash(bcdmFileLocation);

            Label dbdmLabel = new Label("Business Composition Database.jar");

            TextField dbdmHash = new TextField();
            dbdmHash.setPrefColumnCount(24);
            dbdmHash.setText(hash);
            dbdmHash.setEditable(false);

            Button dbdmCopyButton = new Button();
            dbdmCopyButton.setText("Copy");

            HBox postBox = new HBox();
            postBox.setAlignment(BASELINE_RIGHT);
            postBox.getChildren().addAll(dbdmLabel, dbdmHash,dbdmCopyButton);
            postBox.setSpacing(10);

            hashes.getChildren().add(postBox);

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }

        topContainer.getChildren().add(hashes);

        Scene scene = new Scene(root, 625, 250, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
        return DatatypeConverter
                .printHexBinary(digest).toUpperCase();
    }


    public static void main(String[] args) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("application.properties");

            // load a properties file
            prop.load(input);

            preFileLocation=prop.getProperty("file.location.pre");
            postFileLocation=prop.getProperty("file.location.post");
            bcFileLocation=prop.getProperty("file.location.bc");
            bcdmFileLocation=prop.getProperty("file.location.bcdm");


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        launch(args);
    }
}