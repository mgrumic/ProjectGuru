/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru.jpa.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projectguru.entities.WorksOnTask;
import projectguru.entities.Income;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author marko
 */
public class ActivityJpaController implements Serializable {

    public ActivityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Activity activity) {
        if (activity.getIncomeList() == null) {
            activity.setIncomeList(new ArrayList<Income>());
        }
        if (activity.getExpenseList() == null) {
            activity.setExpenseList(new ArrayList<Expense>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnTask worksOnTask = activity.getWorksOnTask();
            if (worksOnTask != null) {
                worksOnTask = em.getReference(worksOnTask.getClass(), worksOnTask.getWorksOnTaskPK());
                activity.setWorksOnTask(worksOnTask);
            }
            List<Income> attachedIncomeList = new ArrayList<Income>();
            for (Income incomeListIncomeToAttach : activity.getIncomeList()) {
                incomeListIncomeToAttach = em.getReference(incomeListIncomeToAttach.getClass(), incomeListIncomeToAttach.getId());
                attachedIncomeList.add(incomeListIncomeToAttach);
            }
            activity.setIncomeList(attachedIncomeList);
            List<Expense> attachedExpenseList = new ArrayList<Expense>();
            for (Expense expenseListExpenseToAttach : activity.getExpenseList()) {
                expenseListExpenseToAttach = em.getReference(expenseListExpenseToAttach.getClass(), expenseListExpenseToAttach.getId());
                attachedExpenseList.add(expenseListExpenseToAttach);
            }
            activity.setExpenseList(attachedExpenseList);
            em.persist(activity);
            if (worksOnTask != null) {
                worksOnTask.getActivityList().add(activity);
                worksOnTask = em.merge(worksOnTask);
            }
            for (Income incomeListIncome : activity.getIncomeList()) {
                Activity oldIDActivityOfIncomeListIncome = incomeListIncome.getIDActivity();
                incomeListIncome.setIDActivity(activity);
                incomeListIncome = em.merge(incomeListIncome);
                if (oldIDActivityOfIncomeListIncome != null) {
                    oldIDActivityOfIncomeListIncome.getIncomeList().remove(incomeListIncome);
                    oldIDActivityOfIncomeListIncome = em.merge(oldIDActivityOfIncomeListIncome);
                }
            }
            for (Expense expenseListExpense : activity.getExpenseList()) {
                Activity oldIDActivityOfExpenseListExpense = expenseListExpense.getIDActivity();
                expenseListExpense.setIDActivity(activity);
                expenseListExpense = em.merge(expenseListExpense);
                if (oldIDActivityOfExpenseListExpense != null) {
                    oldIDActivityOfExpenseListExpense.getExpenseList().remove(expenseListExpense);
                    oldIDActivityOfExpenseListExpense = em.merge(oldIDActivityOfExpenseListExpense);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Activity activity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity persistentActivity = em.find(Activity.class, activity.getId());
            WorksOnTask worksOnTaskOld = persistentActivity.getWorksOnTask();
            WorksOnTask worksOnTaskNew = activity.getWorksOnTask();
            List<Income> incomeListOld = persistentActivity.getIncomeList();
            List<Income> incomeListNew = activity.getIncomeList();
            List<Expense> expenseListOld = persistentActivity.getExpenseList();
            List<Expense> expenseListNew = activity.getExpenseList();
            if (worksOnTaskNew != null) {
                worksOnTaskNew = em.getReference(worksOnTaskNew.getClass(), worksOnTaskNew.getWorksOnTaskPK());
                activity.setWorksOnTask(worksOnTaskNew);
            }
            List<Income> attachedIncomeListNew = new ArrayList<Income>();
            for (Income incomeListNewIncomeToAttach : incomeListNew) {
                incomeListNewIncomeToAttach = em.getReference(incomeListNewIncomeToAttach.getClass(), incomeListNewIncomeToAttach.getId());
                attachedIncomeListNew.add(incomeListNewIncomeToAttach);
            }
            incomeListNew = attachedIncomeListNew;
            activity.setIncomeList(incomeListNew);
            List<Expense> attachedExpenseListNew = new ArrayList<Expense>();
            for (Expense expenseListNewExpenseToAttach : expenseListNew) {
                expenseListNewExpenseToAttach = em.getReference(expenseListNewExpenseToAttach.getClass(), expenseListNewExpenseToAttach.getId());
                attachedExpenseListNew.add(expenseListNewExpenseToAttach);
            }
            expenseListNew = attachedExpenseListNew;
            activity.setExpenseList(expenseListNew);
            activity = em.merge(activity);
            if (worksOnTaskOld != null && !worksOnTaskOld.equals(worksOnTaskNew)) {
                worksOnTaskOld.getActivityList().remove(activity);
                worksOnTaskOld = em.merge(worksOnTaskOld);
            }
            if (worksOnTaskNew != null && !worksOnTaskNew.equals(worksOnTaskOld)) {
                worksOnTaskNew.getActivityList().add(activity);
                worksOnTaskNew = em.merge(worksOnTaskNew);
            }
            for (Income incomeListOldIncome : incomeListOld) {
                if (!incomeListNew.contains(incomeListOldIncome)) {
                    incomeListOldIncome.setIDActivity(null);
                    incomeListOldIncome = em.merge(incomeListOldIncome);
                }
            }
            for (Income incomeListNewIncome : incomeListNew) {
                if (!incomeListOld.contains(incomeListNewIncome)) {
                    Activity oldIDActivityOfIncomeListNewIncome = incomeListNewIncome.getIDActivity();
                    incomeListNewIncome.setIDActivity(activity);
                    incomeListNewIncome = em.merge(incomeListNewIncome);
                    if (oldIDActivityOfIncomeListNewIncome != null && !oldIDActivityOfIncomeListNewIncome.equals(activity)) {
                        oldIDActivityOfIncomeListNewIncome.getIncomeList().remove(incomeListNewIncome);
                        oldIDActivityOfIncomeListNewIncome = em.merge(oldIDActivityOfIncomeListNewIncome);
                    }
                }
            }
            for (Expense expenseListOldExpense : expenseListOld) {
                if (!expenseListNew.contains(expenseListOldExpense)) {
                    expenseListOldExpense.setIDActivity(null);
                    expenseListOldExpense = em.merge(expenseListOldExpense);
                }
            }
            for (Expense expenseListNewExpense : expenseListNew) {
                if (!expenseListOld.contains(expenseListNewExpense)) {
                    Activity oldIDActivityOfExpenseListNewExpense = expenseListNewExpense.getIDActivity();
                    expenseListNewExpense.setIDActivity(activity);
                    expenseListNewExpense = em.merge(expenseListNewExpense);
                    if (oldIDActivityOfExpenseListNewExpense != null && !oldIDActivityOfExpenseListNewExpense.equals(activity)) {
                        oldIDActivityOfExpenseListNewExpense.getExpenseList().remove(expenseListNewExpense);
                        oldIDActivityOfExpenseListNewExpense = em.merge(oldIDActivityOfExpenseListNewExpense);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = activity.getId();
                if (findActivity(id) == null) {
                    throw new NonexistentEntityException("The activity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity activity;
            try {
                activity = em.getReference(Activity.class, id);
                activity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The activity with id " + id + " no longer exists.", enfe);
            }
            WorksOnTask worksOnTask = activity.getWorksOnTask();
            if (worksOnTask != null) {
                worksOnTask.getActivityList().remove(activity);
                worksOnTask = em.merge(worksOnTask);
            }
            List<Income> incomeList = activity.getIncomeList();
            for (Income incomeListIncome : incomeList) {
                incomeListIncome.setIDActivity(null);
                incomeListIncome = em.merge(incomeListIncome);
            }
            List<Expense> expenseList = activity.getExpenseList();
            for (Expense expenseListExpense : expenseList) {
                expenseListExpense.setIDActivity(null);
                expenseListExpense = em.merge(expenseListExpense);
            }
            em.remove(activity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Activity> findActivityEntities() {
        return findActivityEntities(true, -1, -1);
    }

    public List<Activity> findActivityEntities(int maxResults, int firstResult) {
        return findActivityEntities(false, maxResults, firstResult);
    }

    private List<Activity> findActivityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Activity.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Activity findActivity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Activity.class, id);
        } finally {
            em.close();
        }
    }

    public int getActivityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Activity> rt = cq.from(Activity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
