/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projectguru.entities.Task;

/**
 *
 * @author ZM
 */
public class FormLoader {
    
    public static void loadFormActivities(Task task) throws IOException
    {   
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormActivities.fxml"));
            
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FormLoader.class.getResource("/projectguru/css/formactivities.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Активност");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }
   
}
