/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import projectguru.controllers.TeamOfficeController;
import projectguru.handlers.LoggedUser;

/**
 *
 * @author ZM
 */
public class ProjectGuru {
    
    
    public void start(Stage primaryStage, LoggedUser user) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/projectguru/fxml/TeamOffice.fxml"));
        
        Parent root = loader.load();
        
        TeamOfficeController controller = (TeamOfficeController)loader.getController();
        controller.setUser(user);
        controller.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/projectguru/css/projectguru.css").toExternalForm());
        
        primaryStage.setTitle("Пројекат Гуру");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        
        primaryStage.setMaximized(true);
        primaryStage.show();
       
        controller.startActiveTask();
        
    }
}
