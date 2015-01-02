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
import projectguru.entities.ClosureTasks;
import projectguru.entities.ClosureTasksPK;
import projectguru.entities.Task;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author ZM
 */
public class ClosureTasksJpaController implements Serializable {

    public ClosureTasksJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ClosureTasks closureTasks) throws PreexistingEntityException, Exception {
        if (closureTasks.getClosureTasksPK() == null) {
            closureTasks.setClosureTasksPK(new ClosureTasksPK());
        }
        closureTasks.getClosureTasksPK().setIDParent(closureTasks.getParent().getId());
        closureTasks.getClosureTasksPK().setIDChild(closureTasks.getChild().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task task = closureTasks.getParent();
            if (task != null) {
                task = em.getReference(task.getClass(), task.getId());
                closureTasks.setParent(task);
            }
            Task task1 = closureTasks.getChild();
            if (task1 != null) {
                task1 = em.getReference(task1.getClass(), task1.getId());
                closureTasks.setChild(task1);
            }
            em.persist(closureTasks);
            if (task != null) {
                task.getClosureTasksChildren().add(closureTasks);
                task = em.merge(task);
            }
            if (task1 != null) {
                task1.getClosureTasksChildren().add(closureTasks);
                task1 = em.merge(task1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findClosureTasks(closureTasks.getClosureTasksPK()) != null) {
                throw new PreexistingEntityException("ClosureTasks " + closureTasks + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ClosureTasks closureTasks) throws NonexistentEntityException, Exception {
        closureTasks.getClosureTasksPK().setIDParent(closureTasks.getParent().getId());
        closureTasks.getClosureTasksPK().setIDChild(closureTasks.getChild().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClosureTasks persistentClosureTasks = em.find(ClosureTasks.class, closureTasks.getClosureTasksPK());
            Task taskOld = persistentClosureTasks.getParent();
            Task taskNew = closureTasks.getParent();
            Task task1Old = persistentClosureTasks.getChild();
            Task task1New = closureTasks.getChild();
            if (taskNew != null) {
                taskNew = em.getReference(taskNew.getClass(), taskNew.getId());
                closureTasks.setParent(taskNew);
            }
            if (task1New != null) {
                task1New = em.getReference(task1New.getClass(), task1New.getId());
                closureTasks.setChild(task1New);
            }
            closureTasks = em.merge(closureTasks);
            if (taskOld != null && !taskOld.equals(taskNew)) {
                taskOld.getClosureTasksChildren().remove(closureTasks);
                taskOld = em.merge(taskOld);
            }
            if (taskNew != null && !taskNew.equals(taskOld)) {
                taskNew.getClosureTasksChildren().add(closureTasks);
                taskNew = em.merge(taskNew);
            }
            if (task1Old != null && !task1Old.equals(task1New)) {
                task1Old.getClosureTasksChildren().remove(closureTasks);
                task1Old = em.merge(task1Old);
            }
            if (task1New != null && !task1New.equals(task1Old)) {
                task1New.getClosureTasksChildren().add(closureTasks);
                task1New = em.merge(task1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ClosureTasksPK id = closureTasks.getClosureTasksPK();
                if (findClosureTasks(id) == null) {
                    throw new NonexistentEntityException("The closureTasks with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ClosureTasksPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClosureTasks closureTasks;
            try {
                closureTasks = em.getReference(ClosureTasks.class, id);
                closureTasks.getClosureTasksPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The closureTasks with id " + id + " no longer exists.", enfe);
            }
            Task task = closureTasks.getParent();
            if (task != null) {
                task.getClosureTasksChildren().remove(closureTasks);
                task = em.merge(task);
            }
            Task task1 = closureTasks.getChild();
            if (task1 != null) {
                task1.getClosureTasksChildren().remove(closureTasks);
                task1 = em.merge(task1);
            }
            em.remove(closureTasks);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ClosureTasks> findClosureTasksEntities() {
        return findClosureTasksEntities(true, -1, -1);
    }

    public List<ClosureTasks> findClosureTasksEntities(int maxResults, int firstResult) {
        return findClosureTasksEntities(false, maxResults, firstResult);
    }

    private List<ClosureTasks> findClosureTasksEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ClosureTasks.class));
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

    public ClosureTasks findClosureTasks(ClosureTasksPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ClosureTasks.class, id);
        } finally {
            em.close();
        }
    }

    public int getClosureTasksCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ClosureTasks> rt = cq.from(ClosureTasks.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
