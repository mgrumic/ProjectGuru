/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import projectguru.AccessManager;
import projectguru.entities.Activity;
import projectguru.entities.ClosureTasks;
import projectguru.entities.Privileges;
import projectguru.entities.Task;
import projectguru.entities.Timetable;
import projectguru.entities.User;
import projectguru.entities.WorksOnTask;
import projectguru.entities.WorksOnTaskPK;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.TaskHandler;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.ActivityJpaController;
import projectguru.jpa.controllers.ClosureTasksJpaController;
import projectguru.jpa.controllers.TaskJpaController;
import projectguru.jpa.controllers.TimetableJpaController;
import projectguru.jpa.controllers.UserJpaController;
import projectguru.jpa.controllers.WorksOnTaskJpaController;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author ZM
 */
public class JpaTaskHandler implements TaskHandler{

    private LoggedUser loggedUser;
    
    public JpaTaskHandler(LoggedUser user) {
        this.loggedUser = user;
    }
    
    //TODO: dobro testirati ovu funkciju, pogotovo ovaj JPQL upit
    @Override
    public boolean checkTaskChefPrivileges(Task task) {
        
        
        if(loggedUser.getUser().equals(getChef(task))){
            return true;
        }
        
        EntityManagerFactory emf = ((JpaAccessManager)(AccessManager.getInstance())).getFactory();
        EntityManager em = emf.createEntityManager();
        TaskJpaController taskCtrl = new TaskJpaController(emf);
        ClosureTasksJpaController ctCtrl = new ClosureTasksJpaController(emf);
        
        Query q = em.createQuery("SELECT case when (count(wot) > 0)  then true else false end "
                                + "FROM Task t, IN(t.closureTasksParents) p, IN(p.parent.worksOnTaskList) wot "
                                + "WHERE t.id = :taskid AND wot.privileges = :privileges "
                                + "AND wot.worksOnTaskPK.username = :username", Boolean.class);
        
        q.setParameter("taskid", task.getId());
        q.setParameter("privileges", Privileges.CHEF.ordinal());
        q.setParameter("username", loggedUser.getUser().getUsername());
        
        Boolean result = (Boolean) q.getSingleResult();
        
        return result;
    }

    @Override
    public boolean checkMemberPrivileges(Task task) {
       return isMember(task, loggedUser.getUser());
    }

    @Override
    public boolean checkInsightPrivileges(Task task) {
  
       //Mala fora pomocu Java 8 Stream API-ja ;)
       return task.getWorksOnTaskList().stream().anyMatch(
               (wot) -> (wot.getWorksOnTaskPK().getUsername().equals(loggedUser.getUser().getUsername())
               && wot.getPrivileges() >= Privileges.INSIGHT.ordinal()));
    }

    @Override
    public boolean addSubtask(Task task, Task subtask) {
        
        //moramo provjeriti da li trenutni korisnik ima pravo da uradi ovo
        if(checkTaskChefPrivileges(task)){
            try {
                EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
                ClosureTasksJpaController ctCtrl = new ClosureTasksJpaController(emf);
                //svakom nadzadatku trebamo dodati ovaj podzadatak sa razlicitim dubinama
                for (ClosureTasks ct : task.getClosureTasksParents()) {

                    int depth = ct.getDepth();
                    Task pt = ct.getParent();
                    ClosureTasks nct = new ClosureTasks(pt.getId(), subtask.getId());
                    nct.setDepth(depth + 1);

                    ctCtrl.create(nct);

                }

                //dodali smo za sve nadzadtake zadatka 'task'
                //i jos da dodamo za zadatak 'task' unos u closure tabelu
                ClosureTasks nct = new ClosureTasks(task.getId(), subtask.getId());
                nct.setDepth(1);

                ctCtrl.create(nct);

                return true;
            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        return false;
    }

    @Override
    public boolean editSubtask(Task task) {
        if (checkTaskChefPrivileges(task)) {
            try {
                EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
                TaskJpaController taskCtrl = new TaskJpaController(emf);
                taskCtrl.edit(task);

                return true;
            //TODO: razmisliti da li neke od ovih izuzetaka treba 
                //proslijediti zbog informativnijih poruka o gresci
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }

    @Override
    public boolean deleteSubtask(Task task) {
        //pitanje je da li nam ovo treba
        
        return false;
    }

    @Override
    public boolean setChef(Task task, User user) {
        for(WorksOnTask wot : task.getWorksOnTaskList()){
            if(wot.getWorksOnTaskPK().getUsername().equals(user.getUsername())){
                try {
                    wot.setPrivileges(Privileges.CHEF.ordinal());
                    EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
                    WorksOnTaskJpaController wotCtrl = new WorksOnTaskJpaController(emf);
                    wotCtrl.edit(wot);
                    return true;
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //TODO: razmisliti da li da omogucimo da se za sefa postavi neko ko nije clan
        //pa da se on ovde onda prvo postavi za clana pa onda za sefa?
        //Meni se to ne svidja.
        return false;
    }

    //TODO: razmisliti da u ovakvim situacijama bacimo izuzetak
    //npr: InsuficientPrivilegesException, UserNotMemberOfProjectException
    //bilo bi dosta informativno, u GUI-ju bi onda znali zbog cega je nastala greska,
    //i to bi mogli prikazati uporuci.
    //Ali opet sa druge strane GUI bi tako nesto treba sprjeciti da ne moze ni doci do toga.
    //Pa u tom slucaju ako zelimo baciti izuzetak najvise ima smisla da bacimo RuntimeException.
    //Ali i ovako je mozda OK?
    @Override
    public boolean addMember(Task task, User user) {
        
        if (checkTaskChefPrivileges(task)) {
            EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
            WorksOnTaskJpaController wotCtrl = new WorksOnTaskJpaController(emf);

            //ako je u pitanju root task
            if (task.getProjectList().size() > 0) {
                if (task.getProjectList().get(0).getWorksOnProjectList().stream().anyMatch(
                        (wop) -> wop.getWorksOnProjectPK().getUsername().equals(user.getUsername())
                )) {
                    try {
                        WorksOnTask nwot = new WorksOnTask(
                                new WorksOnTaskPK(task.getId(), user.getUsername(), task.getProjectList().get(0).getId()),
                                Privileges.MEMBER.ordinal(), false);
                        wotCtrl.create(nwot);
                    } catch (Exception ex) {
                        Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return false;
                } else {
                    //nije clan projekta
                    return false;
                }
            } else {

                //nije root task, treba naci parenta
                for (ClosureTasks ct : task.getClosureTasksParents()) {
                    if (ct.getDepth() == 1) {
                        Task parent = ct.getParent();
                        if (isMember(parent, user)) {
                            try {
                                int projectId = parent.getWorksOnTaskList().get(0).getWorksOnTaskPK().getIDProject();
                                 
                                WorksOnTask nwot = new WorksOnTask(
                                        new WorksOnTaskPK(task.getId(), user.getUsername(), projectId),
                                        Privileges.MEMBER.ordinal(), false);
                                wotCtrl.create(nwot);
                            } catch (Exception ex) {
                                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return false;
                        } else {
                            return false;
                        }
                    }
                }

            }

        }

        return false;
    }

    @Override
    public boolean addActivity(Task task, Activity activity) {
        if(checkMemberPrivileges(task)){
            //prvi korak je da nadjem WorksOnTask za trenutnog korisnika  i ovaj task
            WorksOnTask wot = getMyWorksOnTask(task);
            
            assert(wot != null);
            
            activity.setWorksOnTask(wot);
            
            EntityManagerFactory emf = ((JpaAccessManager)AccessManager.getInstance()).getFactory();
            ActivityJpaController actCtrl = new ActivityJpaController(emf);
            actCtrl.create(activity);
            
        }
        
        return false;
    }

    @Override
    public boolean addTimetable(Timetable timetable) {
        
        //prvi korak je da nedjem zadatak koji je trenutno aktivan
        EntityManagerFactory emf = ((JpaAccessManager)AccessManager.getInstance()).getFactory();

        Task task = getActiveTask();
        
        if(task == null){
            //nema trenutno aktivnog zadatka
            //TODO: razmisliti - treba dati mogucnost da se dodaje vrijeme na neki default zadatak, jer covjek radi i kad
            //mu nije dodjeljen nikakav zadatak. Predlazem da se to dodaje u root task projekta.
            //Ili mozda da napravimo da kad se zavrsi jedan zadatak, da se on odmah prebaci na nadzadatak.
            //To je lakse za implementirati
            return false;
            
        } 
        
        try {
            //if(checkMemberPrivileges(task)){

            //drugi korak je da nadjem WorksOnTask za trenutnog korisnika  i ovaj task
            WorksOnTask wot = getMyWorksOnTask(task);

            assert(wot != null);

            timetable.setWorksOnTask(wot);

            TimetableJpaController ttCtrl = new TimetableJpaController(emf);
            ttCtrl.create(timetable);

            return true;
            //}
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
        
        
        
        return false;
    }

    @Override
    public Double getWorkedManHoursOfTaskSubtree(Task task) {
        
        double base  = task.getClosureTasksChildren()
                .stream()
                .map(ClosureTasks::getChild)
                .map(Task::getWorksOnTaskList)
                .flatMap((list)-> list.stream())
                .map(WorksOnTask::getTimetableList)
                .flatMap((list)->list.stream())
                .mapToDouble((t)-> (t.getTimetablePK().getStartTime().getTime()
                                    - t.getEndTime().getTime()) / 1000.0*60.0*60.0 )
                .sum();
        
        //izracunati su sati za pod stablo, jos korjen da uracunamo
        return base + task.getWorksOnTaskList()
                .stream()
                .map(WorksOnTask::getTimetableList)
                .flatMap((list) -> list.stream())
                .mapToDouble((t) -> (t.getTimetablePK().getStartTime().getTime()
                                    - t.getEndTime().getTime()) / 1000.0*60.0*60.0)
                .sum();
    }

    @Override
    public User getChef(Task task) {
        
        for (WorksOnTask wot : task.getWorksOnTaskList()){
            if(wot.getPrivileges() == Privileges.CHEF.ordinal()){
                UserJpaController userCtrl = new UserJpaController(
                        ((JpaAccessManager)AccessManager.getInstance()).getFactory());
                
               return  userCtrl.findUser(wot.getWorksOnTaskPK().getUsername());
            }
        }
        return null;
    }

    @Override
    public boolean isMember(Task task, User user) {
        
        for(WorksOnTask wot : task.getWorksOnTaskList()){
           if(wot.getWorksOnTaskPK().getUsername().equals(user.getUsername())
              && wot.getPrivileges() >= Privileges.MEMBER.ordinal()){
               return true;
           }
       }
       
       return false;
        
    }

    @Override
    public Task getActiveTask() {
        EntityManagerFactory emf = ((JpaAccessManager)AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT t "
                + "FROM Task t, IN(t.worksOnTaskList) wot "
                + "WHERE wot.worksOnTaskPK.username = :username "
                + "AND wot.working = true", Task.class);
        
        
        return (Task) q.getSingleResult();
    }
    
    // vraca WorksOnTask objekat koji veznog tipa, a koji povezuje dati zadatak 'task'
    // sa trenutnim korisnikom.
    protected WorksOnTask getMyWorksOnTask(Task task){
        return task.getWorksOnTaskList().stream().filter(
                        (w) -> w.getWorksOnTaskPK().getUsername().equals(loggedUser.getUser().getUsername())
                ).findFirst().orElse(null);
    }

    //Ova metod kao i addTimetable je nesigurna (ali mocna), jedino sto osiguravaju jest da se
    //izmjene prave nad Timetable unosima koji su vezani za trenutno aktivni zadatak i korisnika.
    //Ne provjerava da li je sve u redu sa vremenom.
    //preporucujem da se ne poziva direktno iz GUI.
    //Treba je pozivati iz LoggedUser koda koji je zaduzen za to.
    @Override
    public boolean editTimetable(Timetable timetable) {
        Task task = getActiveTask();
        if(task == null){
            return false;
        }
        
        WorksOnTask wot = getMyWorksOnTask(task);
        //imamo aktivni zadatak, jos da vidimo da li se timetable odnosi na njega i na naseg korisnika.
        if(timetable.getWorksOnTask().equals(wot)){
            try {
                EntityManagerFactory emf = ((JpaAccessManager)AccessManager.getInstance()).getFactory();
                //EntityManager em = emf.createEntityManager();
                TimetableJpaController ttCtrl = new TimetableJpaController(emf);
                ttCtrl.edit(timetable);
                
                return true;
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        return false;
    }
}
