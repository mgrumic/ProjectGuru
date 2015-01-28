 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projectguru.controllers.LoginController;
import projectguru.entities.Activity;
import projectguru.entities.Privileges;
import projectguru.entities.Task;
import projectguru.entities.Timetable;
import projectguru.entities.User;
import projectguru.entities.WorksOnTask;
import projectguru.entities.WorksOnTaskPK;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.TaskHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.TaskJpaController;
import projectguru.jpa.controllers.UserJpaController;

/**
 *
 * @author marko
 */
public class Login extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        JpaAccessManager jam = new JpaAccessManager("ProjectGuruPU");
        AccessManager.setInstance(jam);
        AccessManager.getInstance().logUserIn("-", "-");
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/projectguru/fxml/Login.fxml"));
        
        Parent root = loader.load();
        
        LoginController log = (LoginController)loader.getController();
        log.setStage(primaryStage);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/projectguru/css/login.css").toExternalForm());
        
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        
        primaryStage.show();
    }

    private static void pl(Object o){
        System.err.println(o);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{

        launch(args);
        PersistanceTests test = new PersistanceTests();
        test.projectHandlerTests();
        test.taskHandlerTests();
        System.exit(0);
        
    }
    
}
