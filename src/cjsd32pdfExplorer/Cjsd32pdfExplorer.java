/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cjsd32pdfExplorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Colin Stevens
 */
public class Cjsd32pdfExplorer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {     
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UI.fxml"));
        Parent root = (Parent)loader.load();
        UIController controller = (UIController)loader.getController();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("PDF Explorer");
        stage.setScene(scene);
        stage.show();
        
        controller.ready(stage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
