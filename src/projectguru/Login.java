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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projectguru.entities.User;

/**
 *
 * @author marko
 */
public class Login extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                EntityManagerFactory factory = Persistence.createEntityManagerFactory("ProjectGuruPU");
                EntityManager em = factory.createEntityManager();
                
                User admin = new User("admin","admin", "God", "");
                em.getTransaction().begin();
                em.persist(admin);
                em.getTransaction().commit();
                
            }
        });
        
        Parent root = FXMLLoader.load(getClass().getResource("/projectguru/fxml/Login.fxml"));
        
       // StackPane root = new StackPane();
        //root.getChildren().add(btn);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
