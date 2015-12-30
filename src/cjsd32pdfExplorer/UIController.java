package cjsd32pdfExplorer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author Colin Stevens
 */
public class UIController implements Initializable, Notifiable {
    private File sourceFile;
    private Stage stage;
    private HashMap metadata;
    private MetadataGrabber metadataGrabber;
    Alert about = new Alert(AlertType.INFORMATION);
    
    @FXML
    private TextArea metadataDisplay;
    
    @FXML
    private TextField statusDisplay;
    
    @FXML
    private TextField fileDisplay;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private Button exportButton;
    
    @FXML
    private MenuItem exportMenuItem;
    
    @FXML MenuItem generateMenuItem;
    
    @FXML
    private void handleAbout(ActionEvent event) {
        about.setTitle("About");
        about.setHeaderText("About");
        about.setContentText("PDF Explorer lets you see some of the metadata attributes that follow .pdf files around, which are generally unknown or unnoticed.\n\nThis program was written by Colin Stevens in December 2015, as a final project for MU CS3380.\n(It also certainly wasn't written all in one sitting on the last day)");
        about.showAndWait();
    }
    
    @FXML
    private void handleViewMetadata(ActionEvent event) {
        metadataGrabber = new MetadataGrabber(sourceFile, this);
        metadataGrabber.start();
    }
    
    @FXML
    private void handleExportMetadata(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save metadata export");
        fileChooser.setInitialFileName("metadata.dat");
        metadataGrabber = new MetadataGrabber(sourceFile, this);
        metadataGrabber.start();
        File saveFile = fileChooser.showSaveDialog(stage);
        if(saveFile != null) {
            try {
                FileWriter fileWriter = new FileWriter(saveFile);
                fileWriter.write(metadata.toString());
                fileWriter.close();
            } catch (IOException ex) {
                updateStatus("Failed to export metadata.");
            }
        }
    }
    
    @FXML
    private void handleOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a PDF to view metadata for");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PDF Files", "*.pdf"));
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            sourceFile = file;
            fileDisplay.setText(file.getPath());
            metadataDisplay.clear(); // Clear old metadata info
            generateButton.setDisable(false); // Enable buttons
            generateMenuItem.setDisable(false);
            exportButton.setDisable(false);
            exportMenuItem.setDisable(false);
            updateStatus("File loaded. Please select an action.");
        }
    }
    
    public void updateStatus(String status) {
        statusDisplay.clear();
        statusDisplay.setText(status);
    }
    
    public void ready(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        metadataDisplay.setEditable(false);
        statusDisplay.setEditable(false);
        fileDisplay.setEditable(false);
        generateButton.setDisable(true);
        generateMenuItem.setDisable(true);
        exportButton.setDisable(true);
        exportMenuItem.setDisable(true);
        updateStatus("Waiting.");
    }
    
    @Override
    public void notify(String str) {
        updateStatus(str);
    }
    
    @Override
    public void reportMetadata(HashMap metadata) {
        this.metadata = metadata;
        metadataDisplay.clear();
        Iterator iterator = metadata.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = (String)metadata.get(key.toString());
            if(value == null) {
                value = "None"; // Slightly more user friendly
            }
            metadataDisplay.appendText(key+" : "+value+"\n");
        }
    }
}
