/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.controllers;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Project;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class ExpenseJpaController implements Serializable {

    public ExpenseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Expense expense) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity IDActivity = expense.getIDActivity();
            if (IDActivity != null) {
                IDActivity = em.getReference(IDActivity.getClass(), IDActivity.getId());
                expense.setIDActivity(IDActivity);
            }
            Project IDProject = expense.getIDProject();
            if (IDProject != null) {
                IDProject = em.getReference(IDProject.getClass(), IDProject.getId());
                expense.setIDProject(IDProject);
            }
            em.persist(expense);
            if (IDActivity != null) {
                IDActivity.getExpenseList().add(expense);
                IDActivity = em.merge(IDActivity);
            }
            if (IDProject != null) {
                IDProject.getExpenseList().add(expense);
                IDProject = em.merge(IDProject);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Expense expense) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Expense persistentExpense = em.find(Expense.class, expense.getId());
            Activity IDActivityOld = persistentExpense.getIDActivity();
            Activity IDActivityNew = expense.getIDActivity();
            Project IDProjectOld = persistentExpense.getIDProject();
            Project IDProjectNew = expense.getIDProject();
            if (IDActivityNew != null) {
                IDActivityNew = em.getReference(IDActivityNew.getClass(), IDActivityNew.getId());
                expense.setIDActivity(IDActivityNew);
            }
            if (IDProjectNew != null) {
                IDProjectNew = em.getReference(IDProjectNew.getClass(), IDProjectNew.getId());
                expense.setIDProject(IDProjectNew);
            }
            expense = em.merge(expense);
            if (IDActivityOld != null && !IDActivityOld.equals(IDActivityNew)) {
                IDActivityOld.getExpenseList().remove(expense);
                IDActivityOld = em.merge(IDActivityOld);
            }
            if (IDActivityNew != null && !IDActivityNew.equals(IDActivityOld)) {
                IDActivityNew.getExpenseList().add(expense);
                IDActivityNew = em.merge(IDActivityNew);
            }
            if (IDProjectOld != null && !IDProjectOld.equals(IDProjectNew)) {
                IDProjectOld.getExpenseList().remove(expense);
                IDProjectOld = em.merge(IDProjectOld);
            }
            if (IDProjectNew != null && !IDProjectNew.equals(IDProjectOld)) {
                IDProjectNew.getExpenseList().add(expense);
                IDProjectNew = em.merge(IDProjectNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = expense.getId();
                if (findExpense(id) == null) {
                    throw new NonexistentEntityException("The expense with id " + id + " no longer exists.");
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
            Expense expense;
            try {
                expense = em.getReference(Expense.class, id);
                expense.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The expense with id " + id + " no longer exists.", enfe);
            }
            Activity IDActivity = expense.getIDActivity();
            if (IDActivity != null) {
                IDActivity.getExpenseList().remove(expense);
                IDActivity = em.merge(IDActivity);
            }
            Project IDProject = expense.getIDProject();
            if (IDProject != null) {
                IDProject.getExpenseList().remove(expense);
                IDProject = em.merge(IDProject);
            }
            em.remove(expense);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Expense> findExpenseEntities() {
        return findExpenseEntities(true, -1, -1);
    }

    public List<Expense> findExpenseEntities(int maxResults, int firstResult) {
        return findExpenseEntities(false, maxResults, firstResult);
    }

    private List<Expense> findExpenseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Expense.class));
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

    public Expense findExpense(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Expense.class, id);
        } finally {
            em.close();
        }
    }

    public int getExpenseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Expense> rt = cq.from(Expense.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
