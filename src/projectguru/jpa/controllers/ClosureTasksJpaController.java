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
 * @author marko
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
        closureTasks.getClosureTasksPK().setIDChild(closureTasks.getChild().getId());
        closureTasks.getClosureTasksPK().setIDParent(closureTasks.getParent().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task parent = closureTasks.getParent();
            if (parent != null) {
                parent = em.getReference(parent.getClass(), parent.getId());
                closureTasks.setParent(parent);
            }
            Task child = closureTasks.getChild();
            if (child != null) {
                child = em.getReference(child.getClass(), child.getId());
                closureTasks.setChild(child);
            }
            em.persist(closureTasks);
            if (parent != null) {
                parent.getClosureTasksChildren().add(closureTasks);
                parent = em.merge(parent);
            }
            if (child != null) {
                child.getClosureTasksChildren().add(closureTasks);
                child = em.merge(child);
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
        closureTasks.getClosureTasksPK().setIDChild(closureTasks.getChild().getId());
        closureTasks.getClosureTasksPK().setIDParent(closureTasks.getParent().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClosureTasks persistentClosureTasks = em.find(ClosureTasks.class, closureTasks.getClosureTasksPK());
            Task parentOld = persistentClosureTasks.getParent();
            Task parentNew = closureTasks.getParent();
            Task childOld = persistentClosureTasks.getChild();
            Task childNew = closureTasks.getChild();
            if (parentNew != null) {
                parentNew = em.getReference(parentNew.getClass(), parentNew.getId());
                closureTasks.setParent(parentNew);
            }
            if (childNew != null) {
                childNew = em.getReference(childNew.getClass(), childNew.getId());
                closureTasks.setChild(childNew);
            }
            closureTasks = em.merge(closureTasks);
            if (parentOld != null && !parentOld.equals(parentNew)) {
                parentOld.getClosureTasksChildren().remove(closureTasks);
                parentOld = em.merge(parentOld);
            }
            if (parentNew != null && !parentNew.equals(parentOld)) {
                parentNew.getClosureTasksChildren().add(closureTasks);
                parentNew = em.merge(parentNew);
            }
            if (childOld != null && !childOld.equals(childNew)) {
                childOld.getClosureTasksChildren().remove(closureTasks);
                childOld = em.merge(childOld);
            }
            if (childNew != null && !childNew.equals(childOld)) {
                childNew.getClosureTasksChildren().add(closureTasks);
                childNew = em.merge(childNew);
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
            Task parent = closureTasks.getParent();
            if (parent != null) {
                parent.getClosureTasksChildren().remove(closureTasks);
                parent = em.merge(parent);
            }
            Task child = closureTasks.getChild();
            if (child != null) {
                child.getClosureTasksChildren().remove(closureTasks);
                child = em.merge(child);
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
