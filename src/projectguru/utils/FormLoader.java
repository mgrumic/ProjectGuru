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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import projectguru.controllers.FormAddDocumentationController;
import projectguru.controllers.FormAddProjectController;
import projectguru.controllers.FormAddTaskController;
import projectguru.controllers.FormDocumentationController;
import projectguru.controllers.TeamOfficeController;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.handlers.LoggedUser;

/**
 *
 * @author ZM
 */
public class FormLoader {

    public static void loadFormAddProject(LoggedUser user, TeamOfficeController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormAddProject.fxml"));

        Parent root = loader.load();

        FormAddProjectController fapc = loader.getController();
        fapc.setUser(user);
        fapc.setController(controller); //treba mi za refresh projekata 
        Scene scene = new Scene(root);
        scene.getStylesheets().add(FormLoader.class.getResource("/projectguru/css/formaddproject.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Нови пројекат");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    public static void loadFormActivities(Task task) throws IOException {
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
     *
     * @param task
     * @throws IOException
     */
    public static void loadFormAddTask(Project project, Task task, LoggedUser user) throws IOException {
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormAddTask.fxml"));

        Parent root = loader.load();

        FormAddTaskController fatc = loader.getController();
        fatc.setUser(user);
        fatc.setProject(project);
        if (task == null) {
            task = project.getIDRootTask();
        }
        fatc.setTask(task);

        Scene scene = new Scene(root);
        //scene.getStylesheets().add(FormLoader.class.getResource("/projectguru/css/formaddtask.css").toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Задаци");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();
    }

    public static void loadFormDocumentation(Project project, LoggedUser user) throws Exception {
        FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormDocumentation.fxml"));
        Parent root = loader.load();

        FormDocumentationController fdc = loader.getController();
        fdc.setProject(project);
        fdc.setUser(user);

        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Документација");

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

    }

    public static void loadFormAddDocumentation(Project project, LoggedUser user) throws Exception {
        
            FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormAddDocumentation.fxml"));
            Parent root = loader.load();

            FormAddDocumentationController fdc = loader.getController();
            fdc.setProject(project);
            fdc.setUser(user);

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Нови документ");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
        

    }
    public static void loadFormReport() throws Exception {
        
            FXMLLoader loader = new FXMLLoader(FormLoader.class.getResource("/projectguru/fxml/FormReport.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Нови документ");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.show();
        

    }

    public static void showInformationDialog(String title, String message) {
        Dialogs.create()
                .owner(new Stage())
                .title(title)
                .masthead(null)
                .message(message)
                .showInformation();
    }

}
