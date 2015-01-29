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
import org.controlsfx.dialog.Dialogs;
import projectguru.entities.Project;
import projectguru.entities.Task;

/**
 *
 * @author ZM
 */
public class FormLoader {
    
    public static void loadFormAddProject() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormAddProject.fxml"));
            
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FormLoader.class.getResource("/projectguru/css/formaddproject.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Нови пројекат");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }
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
    /**
     * Poziva se kada se dodaje podzadatak za neki zadatak
     * @param task
     * @throws IOException 
     */
    public static void loadFormAddTask(Project project, Task task) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormAddTask.fxml"));
            
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FormLoader.class.getResource("/projectguru/css/formaddtask.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Задаци");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    public static void showInformationDialog(String title, String message)
    {
        Dialogs.create()
            .owner(new Stage())
            .title(title)
            .masthead(null)
            .message(message)
            .showInformation();
    }
   
}
