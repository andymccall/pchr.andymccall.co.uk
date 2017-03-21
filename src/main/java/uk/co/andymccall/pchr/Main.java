package uk.co.andymccall.pchr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
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
    private static String dbpdmFileLocation;

    Label preLabel = new Label("PRE.jar");
    Label postLabel = new Label("POST.jar");
    Label bcLabel = new Label("Business Process Composition.bpc.zip");
    Label dbpdmLabel = new Label("Business Process Composition.dbpdm.zip");

    TextField preHash = new TextField();
    TextField postHash = new TextField();
    TextField bcHash = new TextField();
    TextField dbpdmHash = new TextField();

    @Override
    public void start(Stage primaryStage) {

        final FileChooser fileChooser = new FileChooser();


        primaryStage.setTitle("Powercurve Hash Reporter");

        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();


        MenuBar mainMenu = new MenuBar();
        mainMenu.prefWidthProperty().bind(primaryStage.widthProperty());
        topContainer.getChildren().add(mainMenu);

        Menu fileMenu = new Menu("File");
        MenuItem refreshMenuItem = new MenuItem("Refresh");
        refreshMenuItem.setOnAction(actionEvent -> updateAllHashes());

        MenuItem exportMenuItem = new MenuItem("Export to File...");
        exportMenuItem.setOnAction(actionEvent -> exportAllHashes(primaryStage));

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(refreshMenuItem, exportMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");
        helpMenu.getItems().add(aboutMenuItem);
        aboutMenuItem.setOnAction(actionEvent -> aboutDialog());

        mainMenu.getMenus().addAll(fileMenu, helpMenu);

        ToolBar toolBar = new ToolBar();

        Button refreshButton = new Button();
        refreshButton.setGraphic(new ImageView("/icons/refresh.png"));
        refreshButton.setTooltip(new Tooltip("Refresh the hashes"));
        refreshButton.setOnAction(actionEvent -> updateAllHashes());
        toolBar.getItems().add(refreshButton);

        Button copyButton = new Button();
        copyButton.setGraphic(new ImageView("/icons/copy.png"));
        copyButton.setTooltip(new Tooltip("Copy the hashes to the clipboard"));
        copyButton.setOnAction(actionEvent -> copyToClipBoard(getAllHashes()));
        toolBar.getItems().add(copyButton);

        Button exportButton = new Button();
        exportButton.setGraphic(new ImageView("/icons/export.png"));
        exportButton.setTooltip(new Tooltip("Export the hashes to a file..."));
        exportButton.setOnAction(actionEvent -> exportAllHashes(primaryStage));
        toolBar.getItems().add(exportButton);

        toolBar.prefWidthProperty().bind(primaryStage.widthProperty());
        topContainer.getChildren().add(toolBar);

        root.setTop(topContainer);

        VBox hashes = new VBox();
        hashes.setPadding(new Insets(10, 10, 10, 10));
        hashes.setSpacing(10);


        preHash.setPrefColumnCount(24);
        preHash.setEditable(false);
        updateHash(preHash, preFileLocation);

        Button preCopyButton = new Button();
        preCopyButton.setText("Copy");
        preCopyButton.setTooltip(new Tooltip("Copy the PRE.jar hash"));
        preCopyButton.setOnAction(actionEvent -> copyToClipBoard(preLabel.getText() + " : " + preHash.getText()));

        HBox preBox = new HBox();
        preBox.setAlignment(BASELINE_RIGHT);
        preBox.getChildren().addAll(preLabel, preHash, preCopyButton);
        preBox.setSpacing(10);

        hashes.getChildren().add(preBox);

        postHash.setPrefColumnCount(24);
        postHash.setEditable(false);
        updateHash(postHash, postFileLocation);

        Button postCopyButton = new Button();
        postCopyButton.setText("Copy");
        postCopyButton.setTooltip(new Tooltip("Copy the POST.jar hash"));
        postCopyButton.setOnAction(actionEvent -> copyToClipBoard(postLabel.getText() + " : " + postHash.getText()));


        HBox postBox = new HBox();
        postBox.setAlignment(BASELINE_RIGHT);
        postBox.getChildren().addAll(postLabel, postHash, postCopyButton);
        postBox.setSpacing(10);

        hashes.getChildren().add(postBox);


        bcHash.setPrefColumnCount(24);
        bcHash.setEditable(false);
        updateHash(bcHash, bcFileLocation);

        Button bcCopyButton = new Button();
        bcCopyButton.setText("Copy");
        bcCopyButton.setTooltip(new Tooltip("Copy the Business Process Composition.bpc.zip hash"));
        bcCopyButton.setOnAction(actionEvent -> copyToClipBoard(bcLabel.getText() + " : " + bcHash.getText()));


        HBox bcBox = new HBox();
        bcBox.setAlignment(BASELINE_RIGHT);
        bcBox.getChildren().addAll(bcLabel, bcHash, bcCopyButton);
        bcBox.setSpacing(10);

        hashes.getChildren().add(bcBox);

        dbpdmHash.setPrefColumnCount(24);
        dbpdmHash.setEditable(false);
        updateHash(dbpdmHash, dbpdmFileLocation);

        Button dbpdmCopyButton = new Button();
        dbpdmCopyButton.setText("Copy");
        dbpdmCopyButton.setTooltip(new Tooltip("Copy the Business Process Composition.dbpdm.zip hash"));
        dbpdmCopyButton.setOnAction(actionEvent -> copyToClipBoard(dbpdmLabel.getText() + " : " + dbpdmHash.getText()));

        HBox dbpdmBox = new HBox();
        dbpdmBox.setAlignment(BASELINE_RIGHT);
        dbpdmBox.getChildren().addAll(dbpdmLabel, dbpdmHash, dbpdmCopyButton);
        dbpdmBox.setSpacing(10);

        hashes.getChildren().add(dbpdmBox);

        topContainer.getChildren().add(hashes);

        Scene scene = new Scene(root, 665, 250, Color.WHITE);
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

    public void updateHash(TextField textField, String fileLocation) {
        String hash;
        try {
            hash = getFileHash(fileLocation);
            textField.setText(hash);
        } catch (IOException e) {
            textField.setText("Unable to read file!");
        } catch (NoSuchAlgorithmException e) {
            textField.setText("Unable to generate hash!");
        }
    }

    public void updateAllHashes() {
        updateHash(preHash, preFileLocation);
        updateHash(postHash, postFileLocation);
        updateHash(bcHash, bcFileLocation);
        updateHash(dbpdmHash, dbpdmFileLocation);
    }

    public void copyToClipBoard(String string) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    public String getAllHashes() {
        String copy;

        copy = preLabel.getText() + " : " + preHash.getText() + "\r\n" +
                postLabel.getText() + " : " + postHash.getText() + "\r\n" +
                bcLabel.getText() + " : " + bcHash.getText() + "\r\n" +
                dbpdmLabel.getText() + " : " + dbpdmHash.getText();

        return copy;
    }

    public void exportAllHashes(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null && !file.exists() && file.getParentFile().isDirectory()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write(getAllHashes());
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }
        }
    }

    public static void main(String[] args) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("application.properties");

            prop.load(input);

            preFileLocation = prop.getProperty("file.location.pre");
            postFileLocation = prop.getProperty("file.location.post");
            bcFileLocation = prop.getProperty("file.location.bc");
            dbpdmFileLocation = prop.getProperty("file.location.dbpdm");


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