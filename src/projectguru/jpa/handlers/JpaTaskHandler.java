/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
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
import projectguru.handlers.exceptions.BusyWorkersException;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.handlers.exceptions.UnfinishedSubtaskException;
import projectguru.handlers.exceptions.UserNotTaskMemberException;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.ActivityJpaController;
import projectguru.jpa.controllers.ClosureTasksJpaController;
import projectguru.jpa.controllers.TaskJpaController;
import projectguru.jpa.controllers.TimetableJpaController;
import projectguru.jpa.controllers.UserJpaController;
import projectguru.jpa.controllers.WorksOnTaskJpaController;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;

/**
 *
 * @author ZM
 */
public class JpaTaskHandler implements TaskHandler {

    private LoggedUser loggedUser;

    public JpaTaskHandler(LoggedUser user) {
        this.loggedUser = user;
    }

    //TODO: dobro testirati ovu funkciju, pogotovo ovaj JPQL upit
    @Override
    public boolean checkTaskChefPrivileges(Task task) {

        //direktna provjera:
        if (loggedUser.getUser().equals(getChef(task))) {
            return true;
        }

        //nije proslo, znaci ostaje jos mogucnost da je trenutni korisnik indirektno sef, tj. da je direktni sef nekog nadzadatka
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        Query q = em.createQuery("SELECT case when (count(wot) > 0)  then true else false end "
                + "FROM Task t, IN(t.closureTasksParents) p, IN(p.parent.worksOnTaskList) wot "
                + "WHERE t.id = :taskid AND wot.privileges = :privileges "
                + "AND wot.worksOnTaskPK.username = :username", Long.class);

        q.setParameter("taskid", task.getId());
        q.setParameter("privileges", Privileges.CHEF.ordinal());
        q.setParameter("username", loggedUser.getUser().getUsername());

        Boolean result = ((Long) q.getSingleResult() > 0);

        return result;
    }

    @Override
    public boolean checkMemberPrivileges(Task task) {
        return isMember(task, loggedUser.getUser());
    }

    @Override
    public boolean checkInsightPrivileges(Task task) {

        // WorksOnTaskList je generisana i popunice se podacima kad prvi put zatrazio nesto od nje.
        for (WorksOnTask wot : task.getWorksOnTaskList()) {
            if (wot.getWorksOnTaskPK().getUsername().equals(loggedUser.getUser().getUsername())
                    && wot.getPrivileges() >= Privileges.MEMBER.ordinal()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addSubtask(Task task, Task subtask) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try { // try-finally blok za zatvaranje em

            //na ovaj nacin provjeravamo da li ovaj zadatak vec postoji u bazi, jer je greska ako ne postoji
            //Ujedno ga popunimo da bude azuriran i da mu liste rade kako treba (up-to-date).
            //Ne mozemo korisiti em.refresh(task) jer ovaj task objekat ne zivi u PersistanceContext-u naseg lokalnog em.
            //to je tako napravljeno da se podrzi veca konkurentnost.
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Parent task does not exist.");
            }

            //moramo provjeriti da li trenutni korisnik ima pravo da uradi ovo
            if (!checkTaskChefPrivileges(task)) {
                throw new InsuficientPrivilegesException();
            }

            //Kad je sve spremno otvorimo try-catch blok da bi sprijecili na neodgovarajuca greska ode u GUI
            try {
                //Unutar try-catch bloka otvorimo transakciju, a zatvaramo je kad sve uspjesno zavrsi, jer nema smisla da se zavrsi parcijalno
                em.getTransaction().begin();

                //ako podzadatak ne postji u bazi (to provjeravamo ovako), onda ima smisla da ga dodamo, to ova metoda radi
                if (subtask.getId() == null || em.find(Task.class, subtask.getId()) == null) {
                    //metoda persist dodaje novi objekat u bazu, da li ce se i ostali objekti koji su povezani za njega dodati u bazu zavisi
                    //od CascadeType podesavanja, 
                    //ali o tome ne trebate sad razmisljati. Vi ih uvijek dodavajte posebno.
                    em.persist(subtask);
                    //Necemo cekati da zatvorimo transakciju da bismo dobili autogenerisane kljuceve, jer nam trebaju
                    em.flush(); //u ovom trenutku subtask je dobio kljuc, rollback moze ovo ponisiti
                }

                //svakom nadzadatku trebamo dodati ovaj podzadatak sa razlicitim dubinama, tako funkcionise closure tabela
                //ja sam dao ovo ime Parents, jer je originalno generisani naziv bio task1 ili tako nesto, ali mozda je bolji naziv Ancestors
                for (ClosureTasks ct : task.getClosureTasksParents()) {

                    int depth = ct.getDepth();
                    Task pt = ct.getParent();
                    ClosureTasks nct = new ClosureTasks(pt.getId(), subtask.getId());
                    nct.setDepth(depth + 1);
                    em.persist(nct);

                }

                //dodali smo za sve nadzadtake zadatka 'task'
                //i jos da dodamo za zadatak 'task' unos u closure tabelu
                ClosureTasks nct = new ClosureTasks(task.getId(), subtask.getId());
                nct.setDepth(1);
                em.persist(nct);

                //zavrsila je transakcija, komitujemo izmjene
                em.getTransaction().commit();

                // e sad ovaj dio je malo triki
                //Posto smo unijeli izmjene u tabelu Closure_Tasks, te izmjen imaju uticaj na liste closureTasksParents i closureTasksChildren
                //Ali te dve liste nece biti update-ovane, dok ne uradimo ovo:
                //Posto se izmjene odnose na ova dva zadatka, onda njih refresh-ujemo (samo poslije commit-a )
                em.refresh(task);
                em.refresh(subtask);
                //To morate, uraditi svaki put kad unosite neke indirekte izmjene nad nekim entitetom

                return true;
            } catch (Exception ex) {
                //Ako je doslo do greske ponistavamo izmjene
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                ex.printStackTrace();
                // javim GUI-ju da je doslo do greske pri sacuvavanju u bazu.
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            //zatvorimo EntityManager
            em.close();
        }

    }

    
    @Override
    public boolean editSubtask(Task task) throws EntityDoesNotExistException, StoringException {
        if (checkTaskChefPrivileges(task)) {
            EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
            EntityManager em = emf.createEntityManager();
            try {
                if (task.getId() == null || em.find(Task.class, task.getId()) == null) {
                    throw new EntityDoesNotExistException("Task does not exist in database.");
                }
                try {
                    em.getTransaction().begin();
                    em.merge(task); //merge kreira novi entitet a sacuva u isti red u bazu,
                    em.getTransaction().commit();

                    return true;
                } catch (Exception ex) {
                    Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                    throw new StoringException(ex.getLocalizedMessage());
                }
            } finally {
                em.close();
            }
        }

        return false;
    }

    @Override
    public boolean deleteSubtask(Task task) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        
        if (!checkTaskChefPrivileges(task)) {
            throw new InsuficientPrivilegesException("Not enough privileges to delete task");
        }
        
        EntityManager em = emf.createEntityManager();
        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }

            try {
                em.getTransaction().begin();
                
                deleteTaskHelper(task, em);
                
                em.remove(task);
                
                em.getTransaction().commit();
           
                
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
                        
            em.close();
        }
       
        
        return false;
    }
    
    private void deleteTaskHelper(Task task, EntityManager em){
        
        
        for( ClosureTasks ct :task.getClosureTasksParents()){
            em.remove(ct);
        }
        
        task.getClosureTasksParents().clear();

        for(ClosureTasks ct : task.getClosureTasksChildren()){
            
            deleteTaskHelper(ct.getChild(), em);
                        
            em.remove(ct.getChild());
            
            em.remove(ct);
    
        }
        
        task.getClosureTasksChildren().clear();
        
        for(WorksOnTask wot : task.getWorksOnTaskList()){
            
            for(Activity a : wot.getActivityList()){
                em.remove(a);
            }
            
            wot.getActivityList().clear();
            
            em.remove(wot);
        }
        
        task.getWorksOnTaskList().clear();
        
        //Query q = em.createQuery("DELETE FROM Activity act WHERE act.worksOnTask.worksOnTaskPK.iDTask = :taskid ");
        
        //q.setParameter("taskid", task.getId());
        
        //q.executeUpdate();
                
    }

    @Override
    public boolean setChef(Task task, User user) throws EntityDoesNotExistException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();

        if (!checkTaskChefPrivileges(task)) {
            return false;
        }

        boolean previousChefFound = false;
        boolean chefSet = false;
        
        EntityManager em = emf.createEntityManager();
        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }

            if (user.getUsername() == null || (user = em.find(User.class, user.getUsername())) == null) {
                throw new EntityDoesNotExistException("User does not exists in database.");
            }
            try {
                em.getTransaction().begin();
                for (WorksOnTask wot : task.getWorksOnTaskList()) {
                    if (wot.getWorksOnTaskPK().getUsername().equals(user.getUsername())) {
                        
                        wot.setPrivileges(Privileges.CHEF.ordinal());

                        
                        em.merge(wot);
                        
                       chefSet = true;
                       
                       if(previousChefFound){
                           break;
                       }
                       
                    }else if(wot.getPrivileges() == Privileges.CHEF.ordinal()){
                        
                        wot.setPrivileges(Privileges.MEMBER.ordinal());
                        
                        previousChefFound = true;
                        
                        if(chefSet){
                            break;
                        }
                    }
                }
            
                if(chefSet){
                    em.getTransaction().commit();
                }else{
                    em.getTransaction().rollback();
                }
                
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
                        
            em.close();
        }
        //TODO: razmisliti da li da omogucimo da se za sefa postavi neko ko nije clan
        //pa da se on ovde onda prvo postavi za clana pa onda za sefa?
        //Meni se to ne svidja. To u GUI-ju moze izgledati kao jedna operacija, ali zapravo da budu dve.
        return false;
    }

    //TODO: razmisliti da u ovakvim situacijama bacimo izuzetak
    //npr: InsuficientPrivilegesException, UserNotMemberOfProjectException
    @Override
    public boolean addMember(Task task, User user) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }

            if (user.getUsername() == null || (user = em.find(User.class, user.getUsername())) == null) {
                throw new EntityDoesNotExistException("User does not exists in database.");
            }

            if (!checkTaskChefPrivileges(task)) {
                throw new InsuficientPrivilegesException();
            }

            String username = user.getUsername();
            WorksOnTask nwot = null;

            try {
                em.getTransaction().begin();

                //ako je u pitanju root task
                if (task.getProjectList().size() > 0) {
                    //zbog bug-a u JPA nove metode koje su dodane u Java 8 Stream API ne rade. Mozda i nije bug ali je greska u arhitekturi.
                    //Da bi omogucio stream-ove moramo pravo konstruisati odgovarajucu ArrayListu, ili neku drugu.
                    //sad jednostavno provjerimo da li je user clan ovog projekta
                    if (new ArrayList<>(task.getProjectList().get(0).getWorksOnProjectList()).stream().anyMatch(
                            (wop) -> wop.getWorksOnProjectPK().getUsername().equals(username)
                    )) {
                        nwot = new WorksOnTask(
                                new WorksOnTaskPK(task.getId(), username, task.getProjectList().get(0).getId()),
                                Privileges.MEMBER.ordinal(), false);
                    } else {
                        throw new Exception("User is not member of project.");
                    }
                } else {
                    //nije root task, treba naci parenta
                    for (ClosureTasks ct : task.getClosureTasksParents()) {
                        if (ct.getDepth() == 1) {

                            Task parent = ct.getParent();

                            if (isMember(parent, user)) {

                                int projectId = parent.getWorksOnTaskList().get(0).getWorksOnTaskPK().getIDProject();
                                nwot = new WorksOnTask(
                                        new WorksOnTaskPK(task.getId(), user.getUsername(), projectId),
                                        Privileges.MEMBER.ordinal(), false);
                            } else {
                                throw new Exception("User is not member of parent task.");
                            }
                        }
                    }

                }

                if (nwot == null) {
                    throw new Exception("Task is not root task nor it has parent!");
                }

                em.persist(nwot);
                em.getTransaction().commit();
                em.refresh(task);
                em.refresh(user);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }

    }

    @Override
    public boolean addActivity(Task task, Activity activity) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }

            if (!checkMemberPrivileges(task)) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();
                //prvi korak je da nadjem WorksOnTask za trenutnog korisnika  i ovaj task
                WorksOnTask wot = getMyWorksOnTask(task);

                assert (wot != null);

                activity.setWorksOnTask(wot);

                if (activity.getId() == null || em.find(Activity.class, activity.getId()) == null) {
                    em.persist(activity);
                } else {
                    em.merge(activity);
                }
                em.getTransaction().commit();
                em.refresh(task);
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public boolean addTimetable(Timetable timetable) throws StoringException {

        //prvi korak je da nedjem zadatak koji je trenutno aktivan
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {

            Task task = getActiveTask();

            if (task == null) {
                //nema trenutno aktivnog zadatka
                //TODO: razmisliti - treba dati mogucnost da se dodaje vrijeme na neki default zadatak, jer covjek radi i kad
                //mu nije dodjeljen nikakav zadatak. Predlazem da se to dodaje u root task projekta.
                //Ili mozda da napravimo da kad se zavrsi jedan zadatak, da se on odmah prebaci na nadzadatak.
                //To je lakse za implementirati
                return false;

            }
            em.getTransaction().begin();
            try {
                //drugi korak je da nadjem WorksOnTask za trenutnog korisnika  i ovaj task
                WorksOnTask wot = getMyWorksOnTask(task);

                assert (wot != null);

                timetable.setWorksOnTask(wot);

                if (timetable.getTimetablePK() == null || (timetable = em.find(Timetable.class, timetable.getTimetablePK())) == null) {
                    em.persist(timetable);
                } else {
                    em.merge(timetable);
                }
                em.getTransaction().commit();
                em.refresh(task);
                return true;
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Double getWorkedManHoursOfTaskSubtree(Task task) throws EntityDoesNotExistException {
        /*  
         double base = new ArrayList<>(task.getClosureTasksChildren())
         .stream()
         .map(ClosureTasks::getChild)
         .map(Task::getWorksOnTaskList)
         .flatMap((list) -> new ArrayList<>(list).stream())
         .map(WorksOnTask::getTimetableList)
         .flatMap((list) -> new ArrayList<>(list).stream())
         .mapToDouble((t) -> (t.getEndTime().getTime()
         - t.getTimetablePK().getStartTime().getTime()) / (1000.0 * 60.0 * 60.0))
         .sum();
         System.err.println("base="+base);
         //izracunati su sati za pod stablo, jos korjen da uracunamo
         return base + new ArrayList<>(task.getWorksOnTaskList())
         .stream()
         .map(WorksOnTask::getTimetableList)
         .flatMap((list) -> new ArrayList<>(list).stream())
         .mapToDouble((t) -> (t.getEndTime().getTime()
         - t.getTimetablePK().getStartTime().getTime()) / (1000.0 * 60.0 * 60.0))
         .sum();
         */

        EntityManager em = ((JpaAccessManager) JpaAccessManager.getInstance()).getFactory().createEntityManager();
        
        if(task.getId() == null || (task = em.find(Task.class, task.getId())) == null){
            throw new EntityDoesNotExistException("Task does not exist.");
        }
        
        Query q = em.createQuery("SELECT  tt FROM Timetable tt WHERE tt.timetablePK.iDTask = :taskid OR tt.timetablePK.iDTask IN (SELECT ct.closureTasksPK.iDChild FROM ClosureTasks ct WHERE  ct.closureTasksPK.iDParent = :taskid  ) ");
        q.setParameter("taskid", task.getId());

        List<Timetable> ttlist = q.getResultList();
        double result = 0.0;
        for (Timetable tt : ttlist) {
            result += (tt.getEndTime().getTime()
                    - tt.getTimetablePK().getStartTime().getTime()) / (1000.0 * 60.0 * 60.0);
        }

        return result;

    }

    @Override
    public User getChef(Task task) {

        assert (task.getWorksOnTaskList() != null);

        for (WorksOnTask wot : task.getWorksOnTaskList()) {
            if (wot.getPrivileges() == Privileges.CHEF.ordinal()) {
                EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
                EntityManager em = emf.createEntityManager();
                try {
                    return em.find(User.class, wot.getWorksOnTaskPK().getUsername());
                } finally {
                    em.close();
                }
            }
        }
        return null;
    }

    @Override
    public boolean isMember(Task task, User user) {

        for (WorksOnTask wot : task.getWorksOnTaskList()) {
            if (wot.getWorksOnTaskPK().getUsername().equals(user.getUsername())
                    && wot.getPrivileges() >= Privileges.MEMBER.ordinal()) {
                return true;
            }
        }

        return false;

    }

    @Override
    public Task getActiveTask() {
        try {
            return getActiveTask(loggedUser.getUser());
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Task getActiveTask(User user) throws EntityDoesNotExistException {
        return getActiveTask(user.getUsername());
    }

    @Override
    public Task getActiveTask(String username) throws EntityDoesNotExistException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            if (username == null || (user = em.find(User.class, username)) == null) {
                throw new EntityDoesNotExistException("User doesn't exist in database.");
            }

            Query q = em.createQuery("SELECT t "
                    + "FROM Task t, IN(t.worksOnTaskList) wot "
                    + "WHERE wot.worksOnTaskPK.username = :username "
                    + "AND wot.working = true", Task.class);

            q.setParameter("username", username);
            Task res = null;
            try {
                res = (Task) q.getSingleResult();
            } catch (NoResultException nre) {

            }

            return res;
        } finally {
            em.close();
        }
    }

    // vraca WorksOnTask objekat koji veznog tipa, a koji povezuje dati zadatak 'task'
    // sa trenutnim korisnikom.
    protected WorksOnTask getMyWorksOnTask(Task task) {
        return getWorksOnTask(task, loggedUser.getUser());
    }

    protected WorksOnTask getWorksOnTask(Task task, User user) {
        return new ArrayList<>(task.getWorksOnTaskList()).stream().filter(
                (w) -> w.getWorksOnTaskPK().getUsername().equals(user.getUsername())
        ).findFirst().orElse(null);
    }

    //Ova metod kao i addTimetable je nesigurna (ali mocna), jedino sto osiguravaju jest da se
    //izmjene prave nad Timetable unosima koji su vezani za trenutno aktivni zadatak i korisnika.
    //Ne provjerava da li je sve u redu sa vremenom.
    //preporucujem da se ne poziva direktno iz GUI.
    //Treba je pozivati iz LoggedUser koda koji je zaduzen za to.
    @Override
    public boolean editTimetable(Timetable timetable) throws StoringException, EntityDoesNotExistException {
        Task task = getActiveTask();
        if (task == null) {
            return false;
        }

        WorksOnTask wot = getMyWorksOnTask(task);

        //imamo aktivni zadatak, jos da vidimo da li se timetable odnosi na njega i na naseg korisnika.
        if (timetable.getWorksOnTask().equals(wot)) {

            EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
            EntityManager em = emf.createEntityManager();
            try {
                if (timetable.getTimetablePK() == null || em.find(Timetable.class, timetable.getTimetablePK()) == null) {
                    throw new EntityDoesNotExistException("Timetable does not exist.");
                }
                try {
                    em.getTransaction().begin();

                    em.merge(timetable);

                    em.getTransaction().commit();
                    em.refresh(task);
                    return true;
                } catch (Exception ex) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    throw new StoringException(ex.getLocalizedMessage());
                }
            } finally {
                em.close();
            }

        }

        return false;
    }

    @Override
    public List<TaskNode> getTaskNodeChildren(Task task) {

        List<TaskNode> children = new ArrayList<>(task.getClosureTasksChildren())
                .stream()
                .filter((e) -> e.getDepth() == 1)
                .map(ClosureTasks::getChild)
                .map((t) -> new TaskNode(getUpdatedTask(t)))
                .collect(Collectors.toList());

        return children;

    }

    @Override
    public TaskTree getTaskTree(Task task) {
        TaskNode root = new TaskNode(getUpdatedTask(task));
        recursiveTreeGeneration(root);
        return new TaskTree(root);
    }

    private void recursiveTreeGeneration(TaskNode root) {
        List<TaskNode> children = getTaskNodeChildren(root.getTask());
        root.setChildren(children);
        for (TaskNode tn : children) {
            recursiveTreeGeneration(tn);
        }
        double part = 1.0 / (children.size() + 1);
        double partDone = 0.0;
        
        for(TaskNode ch : children){
            partDone += part*ch.getPartDone();
        }
        
        //ako je ovaj zadatak uradjen, treba i njega dodati u racun
        if(root.getTask().getEndDate() != null){
            partDone += part;
        }
        
        root.setPartDone(partDone);

    }

    @Override
    public boolean startTask(Task task) throws EntityDoesNotExistException, StoringException {
        try{
            return startTask(task, OnBusyWorkers.SIMPLE);
        }catch(BusyWorkersException bwe){
            
        }
        return false;
    }

    @Override
    public boolean startTask(Task task, OnBusyWorkers onBusyWorkers) throws EntityDoesNotExistException, BusyWorkersException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }

            if (task.getStartDate() != null) {
                return false;
            }

            //da vidimo da li su svi radnici spremini.
            BusyWorkersException bwe = new BusyWorkersException("Some workers are busy doing other tasks");

            List<WorksOnTask> availableFromParentWots = new ArrayList<>();
            List<WorksOnTask> availableNoActiveWots = new ArrayList<>();
            try {
                em.getTransaction().begin();

                for (WorksOnTask wot : task.getWorksOnTaskList()) {
                    Task active = getActiveTask(wot.getWorksOnTaskPK().getUsername());
                    
                    //ako nema aktivnog
                    if (active == null) {
                            availableNoActiveWots.add(wot);
                            continue;
                    }
                    
                    //ako je otac aktivni
                    if (new ArrayList<>(task.getClosureTasksParents())
                            .stream()
                            .anyMatch((a) -> a.getParent().equals(active) && a.getDepth() == 1)) {
                        //ovaj radnik je raspoloziv
   
                        if(onBusyWorkers == OnBusyWorkers.TRANSFER_FROM_PARENT_TASK){
                            availableFromParentWots.add(wot);
                            //moram naci wot koji povezuje active i wot.getUser(), i postaviti da je working false
                            WorksOnTask wot1 = getWorksOnTask(active, wot.getUser());
                            wot1.setWorking(false);
                            em.merge(wot1);
                        }
                        
                        continue;
                    } 
                    
                    
                    bwe.add(wot.getUser());
                    
                }

                if (bwe.getList().size() > 0) {
                    if (onBusyWorkers == OnBusyWorkers.THROW_EXCEPTION) {
                        throw bwe;
                    }
                }

                if(onBusyWorkers == OnBusyWorkers.TRANSFER_FROM_PARENT_TASK){
                    //postavim svima raspolozivim da je aktivni ovaj task
                    //a vec je postavljeno da dosadasnji vise nije aktivan
                    for (WorksOnTask wot : availableFromParentWots) {
                        wot.setWorking(true);
                        em.merge(wot);

                    }
                }
                
                //postavim svima raspolozivim da je aktivni ovaj task
                //a vec je postavljeno da dosadasnji vise nije aktivan
                for (WorksOnTask wot : availableNoActiveWots) {
                    wot.setWorking(true);
                    em.merge(wot);

                }
                
                //Ovde koristim Java 8 time API, iz paketa java.time, on je navodno bolji i nije bagovit ko ovi do sada.
                LocalDateTime ld = LocalDateTime.now();
                task.setStartDate(java.util.Date.from(ld.atZone(ZoneId.systemDefault()).toInstant()));
                em.merge(task);

                em.getTransaction().commit();
                em.refresh(task);
            } catch (BusyWorkersException bew) {
                throw bwe;
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            em.close();
        }
        return true;
    }

        @Override
    public boolean endTask(Task task) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException, UnfinishedSubtaskException {
        return endTask(task, false);
    }
    
    @Override
    public boolean endTask(Task task, boolean endSubtasks) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException,UnfinishedSubtaskException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {

            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Task does not exists in database.");
            }
            
            try {
                em.getTransaction().begin();
                ArrayList<Task> unfinished = new ArrayList<>();
                //ako ima nezavrsenih podzadataka
                if(new ArrayList<>(task.getClosureTasksChildren()).stream().anyMatch(
                        (ct) ->  ct.getChild().getEndDate() == null
                )){
                    if(endSubtasks){
                        new ArrayList<>(task.getClosureTasksChildren()).stream().forEach((ct) -> {
                            if(ct.getChild().getEndDate() == null){
                                try{
                                    endTask(ct.getChild(), true);
                                }catch(Exception ex){
                                    
                                }
                            }
                        });
                    } else {
                        throw new UnfinishedSubtaskException("There are unfinished subtasks");
                    }
                }
                
                Task prnt1 = null;
                for (ClosureTasks ct : task.getClosureTasksParents()) {
                    if (ct.getDepth() == 1) {
                        prnt1 = ct.getParent();
                        break;
                    }
                }
                if (prnt1 == null) {
                    //uraditi nesto kad je root task zavrsen
                    for (WorksOnTask wot : task.getWorksOnTaskList()) {
                        wot.setWorking(false);
                        em.merge(wot);
                    }
                } else {

                    Task prnt = prnt1;

                    new ArrayList<>(task.getWorksOnTaskList())
                            .stream()
                            .filter((wot) -> wot.getWorking())
                            .forEach((wot) -> {

                                wot.setWorking(false);
                                WorksOnTask pwot = getWorksOnTask(prnt, wot.getUser());
                                pwot.setWorking(true);
                                em.merge(wot);
                                em.merge(pwot);

                            });

                }

                em.getTransaction().commit();
                if (prnt1 != null) {
                    em.refresh(prnt1);
                }
                em.refresh(task);
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            em.close();
        }
        return true;
    }

    //pronalazi trenutni zadatak i za trenutni zadatak i korisnika dodaje novi unos satnice
    //tj. otvara novu sesiju
    @Override
    public boolean createNewTimetableEntry(Date startTime, Date endTime) throws StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            try {
                em.getTransaction().begin();
                Query q = em.createQuery(
                        "SELECT wot FROM  WorksOnTask wot  WHERE wot.worksOnTaskPK.username = :username AND wot.working = true  ",
                        WorksOnTask.class);

                q.setParameter("username", loggedUser.getUser().getUsername());
                WorksOnTask wot = (WorksOnTask) q.getSingleResult();
                Timetable tt = new Timetable(
                        startTime,
                        wot.getWorksOnTaskPK().getIDTask(),
                        loggedUser.getUser().getUsername(),
                        wot.getWorksOnTaskPK().getIDProject()
                );
                tt.setEndTime(endTime);
                em.persist(tt);
                em.getTransaction().commit();
            } catch (Exception ex) {
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return false;
    }

    //Dodaje novo zavrsno vrijeme kraja u aktivnu sesiju, ako nije otvorena nova sesija, produzice staru.
    @Override
    public boolean updateActiveTime(Date endTime) throws StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            try {
                em.getTransaction().begin();
                Query q = em.createQuery(
                        "SELECT t FROM  WorksOnTask wot,  IN(wot.timetableList) t WHERE wot.worksOnTaskPK.username = :username AND wot.working = true AND t.timetablePK.startTime = (  SELECT MAX(t1.timetablePK.startTime)  FROM WorksOnTask wot1,  IN(wot1.timetableList) t1 WHERE wot1.worksOnTaskPK.username = :username AND wot1.working = true ) ",
                        Timetable.class);

                q.setParameter("username", loggedUser.getUser().getUsername());
                Timetable tt = (Timetable) q.getSingleResult();
                tt.setEndTime(endTime);
                em.merge(tt);
                em.getTransaction().commit();
            } catch (Exception ex) {
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return false;
    }

    public boolean setActiveTask(Task task) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException, UserNotTaskMemberException {
        return setActiveTask(task, loggedUser.getUser());
    }

    public boolean setActiveTask(Task task, User user) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException, UserNotTaskMemberException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (task.getId() == null || (task = em.find(Task.class, task.getId())) == null) {
                throw new EntityDoesNotExistException("Parent task does not exist.");
            }

            if (user.getUsername() == null || (user = em.find(User.class, user.getUsername())) == null) {
                throw new EntityDoesNotExistException("User does not exists in database.");
            }

            if (task.getStartDate() == null || task.getEndDate() != null) {
                return false;
            }

            try {
                em.getTransaction().begin();

                if (!checkTaskChefPrivileges(task)) {
                    throw new InsuficientPrivilegesException("User does not have sufficient privileges over task :" + task.getName() + ".");
                }

                Task active = getActiveTask(user.getUsername());
                if (active != null && !checkTaskChefPrivileges(active)) {
                    throw new InsuficientPrivilegesException("User does not have sufficient privileges over task :" + active.getName() + ".");
                }
                if (active != null) {
                    WorksOnTask wota = getWorksOnTask(active, user);
                    wota.setWorking(false);
                    em.merge(wota);
                }
                WorksOnTask wot = getWorksOnTask(task, user);
                if(wot == null){
                    throw new UserNotTaskMemberException();
                }
                wot.setWorking(true);

                em.merge(wot);

                em.getTransaction().commit();
                em.refresh(task);
                em.refresh(user);
                if (active != null) {
                    active = em.getReference(Task.class, active.getId());
                    em.refresh(active);
                }
            } catch (InsuficientPrivilegesException ipe) {
                throw   ipe;
            }catch(UserNotTaskMemberException uex){
                throw uex;
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            em.close();
        }
        return true;
    }

    //Ova metoda sluzi da se dobije zadtak iz baze, koji je azuriran novim podacima, na osnovu vec postojeceg objekta.
    //Kontao sam da bi tako nesto bilo korisno.
    @Override
    public Task getUpdatedTask(Task task) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Task.class, task.getId());
        } finally {
            em.close();
        }

    }

    @Override
    public List<User> getAllMembers(Task task) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            TypedQuery<User> query = em.createQuery("SELECT wot.user FROM Task t, IN(t.worksOnTaskList) wot WHERE t.id = :id", User.class);
            
            query.setParameter("id", task.getId());

            return query.getResultList();

        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return new ArrayList<User>();
    }

    @Override
    public List<User> getAddableMembers(Task task) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            task = em.find(Task.class, task.getId());
            
            Task parent = new ArrayList<>(task.getClosureTasksParents()).stream().filter((cp)->cp.getDepth() == 1).findFirst().map((cp)->cp.getParent()).orElse(null);

            Query q = null;
            
            if(parent == null){
                /* root task, trebam naci one koji su u  projektu, a nisu u zadataku*/
                q = em.createQuery("SELECT wop.user FROM Project pr, IN(pr.worksOnProjectList) wop WHERE pr.id = :idproject ");
                q.setParameter("idproject", task.getProjectList().get(0).getId());
            }else{
                /* trebam naci one koji su u roditelju a nisu u ovom zadatku */
                q = em.createQuery("SELECT wop.user FROM Task p, IN(p.worksOnTaskList) wop WHERE p.id = :idparent ");
                q.setParameter("idparent", parent.getId());
            }
            

            List<User> allAbove = q.getResultList();
            
            q = em.createQuery("SELECT wot.user FROM Task t, IN(t.worksOnTaskList) wot WHERE t.id = :idtask");
            
            q.setParameter("idtask", task.getId());
            
            List<User> members = q.getResultList();
            
            List<User> resultList = allAbove
                    .stream()
                    .filter((ua) -> !members.stream().anyMatch((um) -> um.equals(ua)))
                    .collect(Collectors.toList());
            
            return resultList;        

        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return new ArrayList<User>();
    }

    
    
    
}
