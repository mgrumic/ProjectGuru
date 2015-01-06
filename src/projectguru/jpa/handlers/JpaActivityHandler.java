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
import projectguru.AccessManager;
import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Income;
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

        if (user.getUser().equals(activity.getWorksOnTask().getUser())) {
            return true;
        }

        return false;
    }

    //@Override
    //public boolean addActivity(Task task, Activity activity) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}
    @Override
    public boolean editActivity(Activity activity) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (activity.getId() == null || em.find(Activity.class, activity.getId()) == null) {
                throw new EntityDoesNotExistException("Activity does not exist in database.");
            }
            if (checkActivityChefPrivileges(activity)) {
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

    //TODO
    @Override
    public boolean deleteActivity(Activity activity) {
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
                if (exp.getId() == null || (exp = em.find(Expense.class, exp.getId())) == null) {
                    em.persist(exp);
                    em.flush();
                }

                activity.getExpenseList().add(exp);
                exp.setIDActivity(activity);

                em.getTransaction().commit();
                em.refresh(activity);
                em.refresh(exp);
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
                if (inc.getId() == null || (inc = em.find(Income.class, inc.getId())) == null) {
                    em.persist(inc);
                    em.flush();
                }

                activity.getIncomeList().add(inc);
                inc.setIDActivity(activity);

                em.getTransaction().commit();
                em.refresh(activity);
                em.refresh(inc);
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

}
