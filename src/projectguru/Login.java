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
        
        Parent root = FXMLLoader.load(getClass().getResource("/projectguru/fxml/Login.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void pl(Object o){
        System.err.println(o);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        //launch(args);
        JpaAccessManager jam = new JpaAccessManager("ProjectGuruPU");
        AccessManager.setInstance(jam);
        
        LoggedUser luser = AccessManager.getInstance().logUserIn("raja", "etf");
        Task task = new TaskJpaController(jam.getFactory()).findTask(72);
        
        TaskHandler th = luser.getTaskHandler();
        
        pl(th.checkTaskChefPrivileges(task));
        pl(th.checkMemberPrivileges(task));
        pl(th.checkInsightPrivileges(task));
        
        pl(Privileges.NO_PRIVILEGES.ordinal());
        pl(Privileges.INSIGHT.ordinal());
        pl(Privileges.MEMBER.ordinal());
        pl(Privileges.CHEF.ordinal());
        
        
        
//        Task nt1 = new Task();
//        nt1.setAssumedManHours(15);
//        nt1.setDeadline(java.sql.Date.valueOf("2015-1-5"));
//        nt1.setStartDate(java.sql.Date.valueOf("2015-1-2"));
//        nt1.setDescription("Имплементирати JpaTaskHandler");
//        nt1.setName("JpaTaskHandler имплементација");
//        
//        pl(nt1);
//        pl(th.addSubtask(task, nt1));
//        
//        
//        pl(th.addMember(nt1, luser.getUser()));
        
        UserJpaController userCtrl = new UserJpaController(jam.getFactory());
        EntityManager em = jam.getFactory().createEntityManager();
        
        User u1 = userCtrl.findUser("nedjo");
        
        User gruma = userCtrl.findUser("raja");
        
        
        
        //System.err.println(th.setChef(task, u1));

//        System.err.println(th.getChef(task));
//        try{
//            System.err.println(th.addMember(task, gruma));
//        }catch(StoringException se){
//           System.err.println("Ne moze se dodati.");
//        }
        
        
        ///task.setDescription(task.getDescription()+" Ово ће ми живце моје појести.");
         //System.err.println(th.editSubtask(task));
         
         //Task taske = new TaskJpaController(jam.getFactory()).findTask(72);

        //System.err.println(taske.getDescription());
        //System.err.println(task.getDescription());
        
//        pl(th.addMember(task, u1));
//        pl(th.addMember(nt1, u1));
        
//        Activity pac = em.find(Activity.class, 2);
//        pac.setWorksOnTask(em.find(WorksOnTask.class, new WorksOnTaskPK(72,"nedjo",1)));
//        th.addActivity(task, pac);
        
//        Activity ac =new Activity(null, "neki opis");
//        th.addActivity(task, ac);
        
        Timetable tt = new Timetable(null, iDTask, STYLESHEET_MODENA, iDProject);
        
        System.exit(0);
        
        
        
    }
    
}
