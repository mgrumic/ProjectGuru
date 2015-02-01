/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.Activity;
import projectguru.entities.Document;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.entities.WorksOnProject;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.TaskHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.ProjectJpaController;
import projectguru.jpa.controllers.TaskJpaController;
import projectguru.jpa.handlers.JpaProjectHandler;

/**
 *
 * @author ZM
 */
class PersistanceTests {

    //private LoggedUser loggedUser;
    
    private static void pl(Object o) {
        System.out.println(o);
    }

    public PersistanceTests() {
        
    }

    public void taskHandlerTests() throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException {

        JpaAccessManager jam = new JpaAccessManager("ProjectGuruPU");
        AccessManager.setInstance(jam);

        LoggedUser luser = AccessManager.getInstance().logUserIn("boca", "etf");
        Task task = new TaskJpaController(jam.getFactory()).findTask(1);

        TaskHandler th = luser.getTaskHandler();
        task.setDescription(task.getDescription() + ".");
        th.editSubtask(task);
        task = th.getUpdatedTask(task);
        task.setDescription(task.getDescription() + ".");
        th.editSubtask(task);
        pl(th.getWorkedManHoursOfTaskSubtree(task));
        

    }

    
    public void projectHandlerTests() throws InsuficientPrivilegesException, StoringException, EntityDoesNotExistException {
        
        JpaAccessManager jam = new JpaAccessManager("ProjectGuruPU");
        AccessManager.setInstance(jam);
        
        LoggedUser loggedUser = AccessManager.getInstance().logUserIn("admin", "admin");
        
        User kors[] = new User[7];
        kors[0] = new User("boca", "etf", "Александар", "Вукотић", 4, true);
        kors[1] = new User("raja", "etf", "Марко", "Грумић", 2, true);
        kors[2] = new User("djudja", "etf", "Марко", "Васић", 2, true);
        kors[3] = new User("nedjo", "etf", "Марко", "Ивановић", 2, true);
        kors[4] = new User("mandja", "etf", "Милијана", "Анђић", 2, true);
        kors[5] = new User("geewid", "etf", "Огњен", "Видовић", 1, true);
        kors[6] = new User("badaboom", "badaboom", "Михајло", "Савић", 1, true);
        
        for(User u : kors){
            loggedUser.getUserHandler().addUser(u);
        }

        
        
        loggedUser = AccessManager.getInstance().logUserIn("boca", "etf");
        ProjectHandler jpa = loggedUser.getProjectHandler();
        
//        pl(project);
//        pl("Chef privileges: ");
//        pl(jpa.checkProjectChefPrivileges(project));
//        pl("Member privileges: ");
//        pl(jpa.checkMemberPrivileges(project));
//        pl("Insight privileges !");
//        pl(jpa.checkInsightPrivileges(project));
        
        
        Project novi = new Project();
        novi.setName("Наш пројекат!");
        novi.setBudget(new BigDecimal(10000.00));
        novi.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        novi.setEndDate(java.sql.Date.valueOf("2015-2-10"));
        novi.setDescription("Опааааа ! ");
        
        
        jpa.createProject(novi);
        novi = jpa.getUpdatedProject(novi);
        
        for(User u : kors){
            jpa.addMember(novi, u);
        }
        
        
        
        jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.CHEF);
        
        Document doc1 = new Document();
        doc1.setDescription("Нови документ тест1 ! ");
        doc1.setName("Нови документ и сад може да стане 50 карактера!");
        doc1.setPostedDate(java.sql.Date.valueOf("2015-1-2"));
        
        Document doc2 = new Document();
        doc2.setDescription("Нови документ!");
        doc2.setName("Нови документ");
        doc2.setPostedDate(java.sql.Date.valueOf("2015-1-2"));
        
        jpa.addDocument(novi, doc1);
        jpa.addDocument(novi, doc2);
        
        
        Income inc = new Income();
        
        inc.setCurrency("Euro");
        inc.setDescription("Добили на лутрији!");
        inc.setMoneyAmmount(new BigDecimal(10000));
        
        Expense exp = new Expense();
        
        exp.setCurrency("Euro");
        exp.setDescription("Зајебали нас па нам узели!");
        exp.setMoneyAmmount(new BigDecimal(10000));
        exp.setIDProject(novi);
        
        jpa.addExpense(novi, exp);
        jpa.addIncome(novi, inc);
        
        Task root = new Task();
        root.setAssumedManHours(20);
        root.setName("Корјенски задатак!");
        root.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        root.setEndDate(java.sql.Date.valueOf("2015-1-2"));
        root.setDeadline(java.sql.Date.valueOf("2015-1-2"));
        root.setDescription("Почетак рада!");
        
        try {
            jpa.setRootTask(novi, root);
            
            TaskHandler th = loggedUser.getTaskHandler();
            th.setChef(root, loggedUser.getUser());
            Activity ac = new Activity(null, "Нека активност", Date.from(Instant.now()), "Одмарали уз црно точено пиво!");
                      
            
            
            th.addActivity(root, ac);
            Task pod1 = new Task();
            pod1.setAssumedManHours(5);
            pod1.setDeadline(Date.valueOf("2015-2-2"));
            pod1.setDescription("Ма неам појма");
            pod1.setEndDate(null);
            pod1.setName("Подзадатак");
            pod1.setStartDate(Date.from(Instant.now()));
            
            th.addSubtask(root, pod1);
            
            ac = loggedUser.getActivityHandler().getUpdatedActivity(ac);
            ac.setDescription(ac.getDescription() + "Промјењена активност");
            loggedUser.getActivityHandler().editActivity(ac);
            
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(PersistanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            
             novi = jpa.getUpdatedProject(novi);
             novi.setDescription(novi.getDescription() + " Измјена!");
             jpa.editProject(novi);
        
        } catch (Exception ex) {
            Logger.getLogger(PersistanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
//
//        pl(novi);
//        pl("Chef privileges: " + Privileges.CHEF.ordinal());
//        pl(jpa.checkProjectChefPrivileges(novi));
//        pl("Member privileges: ");
//        pl(jpa.checkMemberPrivileges(novi));
//        pl("Insight privileges !");
//        pl(jpa.checkInsightPrivileges(novi));
//        
//        jpa.setChef(novi, loggedUser.getUser());
//        
//        pl("Chef privileges: " + Privileges.CHEF.ordinal());
//        pl(jpa.checkProjectChefPrivileges(novi));
//        pl("Member privileges: ");
//        pl(jpa.checkMemberPrivileges(novi));
//        pl("Insight privileges !");
//        pl(jpa.checkInsightPrivileges(novi));
//        
//        jpa.setChef(novi, loggedUser.getUser());
       
        
        
      //  EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
     //   EntityManager em = emf.createEntityManager();
        
        
       // jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.MEMBER);
        
      //  jpa.addMember(novi, loggedUser.getUser());
        
      //  jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.MEMBER);
        
     //   jpa.addMember(novi, loggedUser.getUser());
        
     //   jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.CHEF);
        
        
        pl("Chef privileges: " + Privileges.CHEF.ordinal());
        pl(jpa.checkProjectChefPrivileges(novi));
        pl("Member privileges: ");
        pl(jpa.checkMemberPrivileges(novi));
        pl("Insight privileges !");
        pl(jpa.checkInsightPrivileges(novi));
        pl("Postoji veza: ");
        pl(jpa.connectionExists(novi, loggedUser.getUser()));
        
        jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.CHEF);
        
        pl("Chef privileges: " + Privileges.CHEF.ordinal());
        pl(jpa.checkProjectChefPrivileges(novi));
        pl("Member privileges: ");
        pl(jpa.checkMemberPrivileges(novi));
        pl("Insight privileges !");
        pl(jpa.checkInsightPrivileges(novi));
        pl("Postoji veza: ");
        pl(jpa.connectionExists(novi, loggedUser.getUser()));
        
        try {
            for(Document doc: jpa.getUpdatedProject(novi).getDocumentList())
            {
                pl(doc);
            }
          //  jpa.deleteProject(novi);
        } catch (Exception ex) {
            Logger.getLogger(PersistanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        System.exit(0);  
        
    }
    
    
}
