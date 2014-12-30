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
import projectguru.entities.Income;
import projectguru.entities.Project;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class IncomeJpaController implements Serializable {

    public IncomeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Income income) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity IDActivity = income.getIDActivity();
            if (IDActivity != null) {
                IDActivity = em.getReference(IDActivity.getClass(), IDActivity.getId());
                income.setIDActivity(IDActivity);
            }
            Project IDProject = income.getIDProject();
            if (IDProject != null) {
                IDProject = em.getReference(IDProject.getClass(), IDProject.getId());
                income.setIDProject(IDProject);
            }
            em.persist(income);
            if (IDActivity != null) {
                IDActivity.getIncomeList().add(income);
                IDActivity = em.merge(IDActivity);
            }
            if (IDProject != null) {
                IDProject.getIncomeList().add(income);
                IDProject = em.merge(IDProject);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Income income) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Income persistentIncome = em.find(Income.class, income.getId());
            Activity IDActivityOld = persistentIncome.getIDActivity();
            Activity IDActivityNew = income.getIDActivity();
            Project IDProjectOld = persistentIncome.getIDProject();
            Project IDProjectNew = income.getIDProject();
            if (IDActivityNew != null) {
                IDActivityNew = em.getReference(IDActivityNew.getClass(), IDActivityNew.getId());
                income.setIDActivity(IDActivityNew);
            }
            if (IDProjectNew != null) {
                IDProjectNew = em.getReference(IDProjectNew.getClass(), IDProjectNew.getId());
                income.setIDProject(IDProjectNew);
            }
            income = em.merge(income);
            if (IDActivityOld != null && !IDActivityOld.equals(IDActivityNew)) {
                IDActivityOld.getIncomeList().remove(income);
                IDActivityOld = em.merge(IDActivityOld);
            }
            if (IDActivityNew != null && !IDActivityNew.equals(IDActivityOld)) {
                IDActivityNew.getIncomeList().add(income);
                IDActivityNew = em.merge(IDActivityNew);
            }
            if (IDProjectOld != null && !IDProjectOld.equals(IDProjectNew)) {
                IDProjectOld.getIncomeList().remove(income);
                IDProjectOld = em.merge(IDProjectOld);
            }
            if (IDProjectNew != null && !IDProjectNew.equals(IDProjectOld)) {
                IDProjectNew.getIncomeList().add(income);
                IDProjectNew = em.merge(IDProjectNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = income.getId();
                if (findIncome(id) == null) {
                    throw new NonexistentEntityException("The income with id " + id + " no longer exists.");
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
            Income income;
            try {
                income = em.getReference(Income.class, id);
                income.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The income with id " + id + " no longer exists.", enfe);
            }
            Activity IDActivity = income.getIDActivity();
            if (IDActivity != null) {
                IDActivity.getIncomeList().remove(income);
                IDActivity = em.merge(IDActivity);
            }
            Project IDProject = income.getIDProject();
            if (IDProject != null) {
                IDProject.getIncomeList().remove(income);
                IDProject = em.merge(IDProject);
            }
            em.remove(income);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Income> findIncomeEntities() {
        return findIncomeEntities(true, -1, -1);
    }

    public List<Income> findIncomeEntities(int maxResults, int firstResult) {
        return findIncomeEntities(false, maxResults, firstResult);
    }

    private List<Income> findIncomeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Income.class));
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

    public Income findIncome(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Income.class, id);
        } finally {
            em.close();
        }
    }

    public int getIncomeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Income> rt = cq.from(Income.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
