/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru;

import java.math.BigDecimal;
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

        pl(th.checkTaskChefPrivileges(task));
        pl(th.checkMemberPrivileges(task));
        pl(th.checkInsightPrivileges(task));

        pl(Privileges.NO_PRIVILEGES.ordinal());
        pl(Privileges.INSIGHT.ordinal());
        pl(Privileges.MEMBER.ordinal());
        pl(Privileges.CHEF.ordinal());

        Task nt1 = new Task();
        nt1.setAssumedManHours(15);
        nt1.setDeadline(java.sql.Date.valueOf("2015-1-5"));
        nt1.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        nt1.setEndDate(java.sql.Date.valueOf("2015-1-2"));
        nt1.setDescription("JpaTaskHandler");
        nt1.setName("JpaTaskHandler");

        Task nt2 = new Task();
        nt2.setAssumedManHours(15);
        nt2.setDeadline(java.sql.Date.valueOf("2015-1-5"));
        nt2.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        nt2.setEndDate(java.sql.Date.valueOf("2015-1-2"));
        nt2.setDescription("JpaTaskHandler");
        nt2.setName("JpaTaskHandler");
        
        pl(nt1);
        pl(nt2);
        pl(th.addSubtask(task, nt1));
        pl(th.addSubtask(nt1, nt2));

        pl(th.addMember(nt1, luser.getUser()));

       // UserJpaController userCtrl = new UserJpaController(jam.getFactory());
        //  EntityManager em = jam.getFactory().createEntityManager();
      //  User u1 = userCtrl.findUser("nedjo");
       // User gruma = userCtrl.findUser("raja");
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
        //       Activity pac = em.find(Activity.class, 2);
//        pac.setWorksOnTask(em.find(WorksOnTask.class, new WorksOnTaskPK(72,"nedjo",1)));
//        th.addActivity(task, pac);
        Activity ac = new Activity(null, "neki opis");
        th.addActivity(task, ac);
        //Timetable tt = new Timetable(null, iDTask, STYLESHEET_MODENA, iDProject);
        System.exit(0);

    }

    public void projectHandlerTests() throws InsuficientPrivilegesException, StoringException {
        
        JpaAccessManager jam = new JpaAccessManager("ProjectGuruPU");
        AccessManager.setInstance(jam);
        
        LoggedUser loggedUser = AccessManager.getInstance().logUserIn("djudja", "etf");
        Project project = new ProjectJpaController(jam.getFactory()).findProject(1);
        ProjectHandler jpa = loggedUser.getProjectHandler();
        
        pl(project);
        pl("Chef privileges: ");
        pl(jpa.checkProjectChefPrivileges(project));
        pl("Member privileges: ");
        pl(jpa.checkMemberPrivileges(project));
        pl("Insight privileges !");
        pl(jpa.checkInsightPrivileges(project));
        
        Project novi = new Project();
        novi.setName("Testni projekat !");
        novi.setBudget(new BigDecimal(10000.00));
        novi.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        novi.setEndDate(java.sql.Date.valueOf("2015-1-2"));
        novi.setDescription("Testni projekat da vidimo da li uspijeva ! ");
        
        jpa.createProject(novi);
      //  jpa.addMember(novi, loggedUser.getUser());
      //  jpa.setPrivileges(novi, loggedUser.getUser(), Privileges.CHEF);
        
        Document doc1 = new Document();
        doc1.setDescription("Novi dokument ! ");
        doc1.setName("Novi dokument i sad moze da stane 50 karaktera!");
        doc1.setPostedDate(java.sql.Date.valueOf("2015-1-2"));
        
        Document doc2 = new Document();
        doc2.setDescription("Novi dokument !");
        doc2.setName("Novi dokument");
        doc2.setPostedDate(java.sql.Date.valueOf("2015-1-2"));
        
        jpa.addDocument(novi, doc1);
        jpa.addDocument(novi, doc2);
        
        
        Income inc = new Income();
        
        inc.setCurrency("Euro");
        inc.setDescription("Dobili na lutriji !");
        inc.setMoneyAmmount(new BigDecimal(10000));
        
        Expense exp = new Expense();
        
        exp.setCurrency("Euro");
        exp.setDescription("Zajebali nas pa nam uzeli !");
        exp.setMoneyAmmount(new BigDecimal(10000));
        exp.setIDProject(novi);
        
        jpa.addExpense(novi, exp);
        jpa.addIncome(novi, inc);
        
        Task root = new Task();
        root.setAssumedManHours(20);
        root.setName("Root task za novi projekat !");
        root.setStartDate(java.sql.Date.valueOf("2015-1-2"));
        root.setEndDate(java.sql.Date.valueOf("2015-1-2"));
        root.setDeadline(java.sql.Date.valueOf("2015-1-2"));
        root.setDescription("Ovo je root task !");
        
        try {
            jpa.setRootTask(novi, root);
            
            TaskHandler th = loggedUser.getTaskHandler();
            Activity ac = new Activity(null, "Kreirana aktivnost !");
            
            th.addMember(root, loggedUser.getUser());
            th.setChef(root, loggedUser.getUser());
            th.addActivity(root, ac);
            
            
            ac = loggedUser.getActivityHandler().getUpdatedActivity(ac);
            ac.setDescription("Promjenjena aktivnost");
            loggedUser.getActivityHandler().editActivity(ac);
            
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(PersistanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            
             novi = jpa.getUpdatedProject(novi);
             novi.setDescription("Izmjenjen je opis !");
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
