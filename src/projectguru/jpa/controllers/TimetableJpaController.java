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
import projectguru.entities.Timetable;
import projectguru.entities.TimetablePK;
import projectguru.entities.WorksOnTask;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author marko
 */
public class TimetableJpaController implements Serializable {

    public TimetableJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Timetable timetable) throws PreexistingEntityException, Exception {
        if (timetable.getTimetablePK() == null) {
            timetable.setTimetablePK(new TimetablePK());
        }
        timetable.getTimetablePK().setIDTask(timetable.getWorksOnTask().getWorksOnTaskPK().getIDTask());
        timetable.getTimetablePK().setUsername(timetable.getWorksOnTask().getWorksOnTaskPK().getUsername());
        timetable.getTimetablePK().setIDProject(timetable.getWorksOnTask().getWorksOnTaskPK().getIDProject());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnTask worksOnTask = timetable.getWorksOnTask();
            if (worksOnTask != null) {
                worksOnTask = em.getReference(worksOnTask.getClass(), worksOnTask.getWorksOnTaskPK());
                timetable.setWorksOnTask(worksOnTask);
            }
            em.persist(timetable);
            if (worksOnTask != null) {
                worksOnTask.getTimetableList().add(timetable);
                worksOnTask = em.merge(worksOnTask);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTimetable(timetable.getTimetablePK()) != null) {
                throw new PreexistingEntityException("Timetable " + timetable + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Timetable timetable) throws NonexistentEntityException, Exception {
        timetable.getTimetablePK().setIDTask(timetable.getWorksOnTask().getWorksOnTaskPK().getIDTask());
        timetable.getTimetablePK().setUsername(timetable.getWorksOnTask().getWorksOnTaskPK().getUsername());
        timetable.getTimetablePK().setIDProject(timetable.getWorksOnTask().getWorksOnTaskPK().getIDProject());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Timetable persistentTimetable = em.find(Timetable.class, timetable.getTimetablePK());
            WorksOnTask worksOnTaskOld = persistentTimetable.getWorksOnTask();
            WorksOnTask worksOnTaskNew = timetable.getWorksOnTask();
            if (worksOnTaskNew != null) {
                worksOnTaskNew = em.getReference(worksOnTaskNew.getClass(), worksOnTaskNew.getWorksOnTaskPK());
                timetable.setWorksOnTask(worksOnTaskNew);
            }
            timetable = em.merge(timetable);
            if (worksOnTaskOld != null && !worksOnTaskOld.equals(worksOnTaskNew)) {
                worksOnTaskOld.getTimetableList().remove(timetable);
                worksOnTaskOld = em.merge(worksOnTaskOld);
            }
            if (worksOnTaskNew != null && !worksOnTaskNew.equals(worksOnTaskOld)) {
                worksOnTaskNew.getTimetableList().add(timetable);
                worksOnTaskNew = em.merge(worksOnTaskNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TimetablePK id = timetable.getTimetablePK();
                if (findTimetable(id) == null) {
                    throw new NonexistentEntityException("The timetable with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TimetablePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Timetable timetable;
            try {
                timetable = em.getReference(Timetable.class, id);
                timetable.getTimetablePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The timetable with id " + id + " no longer exists.", enfe);
            }
            WorksOnTask worksOnTask = timetable.getWorksOnTask();
            if (worksOnTask != null) {
                worksOnTask.getTimetableList().remove(timetable);
                worksOnTask = em.merge(worksOnTask);
            }
            em.remove(timetable);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Timetable> findTimetableEntities() {
        return findTimetableEntities(true, -1, -1);
    }

    public List<Timetable> findTimetableEntities(int maxResults, int firstResult) {
        return findTimetableEntities(false, maxResults, firstResult);
    }

    private List<Timetable> findTimetableEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Timetable.class));
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

    public Timetable findTimetable(TimetablePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Timetable.class, id);
        } finally {
            em.close();
        }
    }

    public int getTimetableCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Timetable> rt = cq.from(Timetable.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
