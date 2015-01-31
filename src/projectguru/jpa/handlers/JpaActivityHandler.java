/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import projectguru.AccessManager;
import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Task;
import projectguru.handlers.ActivityHandler;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.JpaAccessManager;

/**
 *
 * @author ZM
 */
public class JpaActivityHandler implements ActivityHandler {

    private LoggedUser user;

    public JpaActivityHandler(LoggedUser user) {
        this.user = user;
    }

    //TODO

    @Override
    public boolean checkActivityChefPrivileges(Activity activity) {

        try {
            if (user.getUser().getUsername().equals(getUpdatedActivity(activity).getWorksOnTask().getUser().getUsername())) {
                return true;
            }
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaActivityHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

   //@Override
    //public boolean addActivity(Task task, Activity activity) {
    //}
    /*
     *  Activity objekat treba da je azuriran !
     *
     */
    @Override
    public boolean editActivity(Activity activity) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (activity.getId() == null || em.find(Activity.class, activity.getId()) == null) {
                throw new EntityDoesNotExistException("Activity does not exist in database.");
            }
            if (!checkActivityChefPrivileges(activity)) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();
                em.merge(activity);
                em.getTransaction().commit();

            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return true;
    }

    @Override
    public boolean deleteActivity(Activity activity) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            if (activity.getId() == null || (activity = em.find(Activity.class, activity.getId())) == null) {
                throw new EntityDoesNotExistException("Activity does not exist.");
            }
            if (checkActivityChefPrivileges(activity)) {
                throw new InsuficientPrivilegesException();
            }
            try {

                em.getTransaction().begin();
                em.remove(activity);
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
        return true;

    }

    @Override
    public boolean addExpense(Activity activity, Expense exp) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (activity.getId() == null || (activity = em.find(Activity.class, activity.getId())) == null) {
                throw new EntityDoesNotExistException("Activity does not exist.");
            }
            if (checkActivityChefPrivileges(activity)) {
                throw new InsuficientPrivilegesException();
            }
            try {

                em.getTransaction().begin();
                exp.setIDActivity(activity);

                if (exp.getId() == null || (em.find(Expense.class, exp.getId())) == null) {
                    em.persist(exp);
                    em.flush();
                }
                em.getTransaction().commit();
                em.refresh(activity);
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
    public boolean addIncome(Activity activity, Income inc) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (activity.getId() == null || (activity = em.find(Activity.class, activity.getId())) == null) {
                throw new EntityDoesNotExistException("Activity does not exist.");
            }
            if (checkActivityChefPrivileges(activity)) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();

                inc.setIDActivity(activity);
                if (inc.getId() == null || (em.find(Income.class, inc.getId())) == null) {
                    em.persist(inc);
                    em.flush();
                }
                em.getTransaction().commit();
                em.refresh(activity);

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
    public Activity getUpdatedActivity(Activity activity) throws EntityDoesNotExistException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (activity.getId() == null || (activity = em.find(Activity.class, activity.getId())) == null) {
            throw new EntityDoesNotExistException("Activity doesn't exist !");
        }
        return activity;

    }

    @Override
    public List<Activity> findActivitiesForTask(Task task, boolean includeSubtasks, boolean onlyForCurrentUser) throws InsuficientPrivilegesException, StoringException {
        
        if(!user.getTaskHandler().checkInsightPrivileges(task)){
            throw new InsuficientPrivilegesException("User does not have sufficient privileges for this task");
        }
        
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try{
        Query q = null;
        
        String qstring = "SELECT act FROM Activity act, IN(act.worksOnTask.task) t subtaskstr1 WHERE (t.id = :idtask subtaskstr2) onlystr1";
        
        String subtaskstr1 = ", IN(t.closureTasksParents) clpr";
        String subtaskstr2 = "OR clpr.closureTasksPK.iDParent = :idtask ";
        
        String onlystr1 = "AND act.worksOnTask.username = :username";

        if(onlyForCurrentUser){
            qstring = qstring.replace("onlystr1", onlystr1);
        }else{
            qstring = qstring.replace("onlystr1", "");
        }
        
        if(includeSubtasks){
            qstring = qstring.replace("subtaskstr1", subtaskstr1);
            qstring = qstring.replace("subtaskstr2", subtaskstr2);
        }else{
            qstring = qstring.replace("subtaskstr1", "");
            qstring = qstring.replace("subtaskstr2", "");
        }
        
        q = em.createQuery(qstring, Activity.class);
        
        if(onlyForCurrentUser){
            q.setParameter("username", user.getUser().getUsername());
        }
        
        if(includeSubtasks){
            q.setParameter("idtask", task.getId());
        }
        
        List<Activity> ret = q.getResultList();
        
        return ret;
        
        }catch(Exception e){
            throw new StoringException(e.getLocalizedMessage());
        }finally{
            em.close();
        }
        
    }
}
