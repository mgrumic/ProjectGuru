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
import projectguru.entities.Task;
import projectguru.entities.WorksOnProject;
import projectguru.entities.Timetable;
import projectguru.entities.Activity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.WorksOnTask;
import projectguru.entities.WorksOnTaskPK;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author marko
 */
public class WorksOnTaskJpaController1 implements Serializable {

    public WorksOnTaskJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorksOnTask worksOnTask) throws PreexistingEntityException, Exception {
        if (worksOnTask.getWorksOnTaskPK() == null) {
            worksOnTask.setWorksOnTaskPK(new WorksOnTaskPK());
        }
        if (worksOnTask.getActivityList() == null) {
            worksOnTask.setActivityList(new ArrayList<Activity>());
        }
        if (worksOnTask.getTimetableList() == null) {
            worksOnTask.setTimetableList(new ArrayList<Timetable>());
        }
        worksOnTask.getWorksOnTaskPK().setIDProject(worksOnTask.getWorksOnProject().getWorksOnProjectPK().getIDProject());
        worksOnTask.getWorksOnTaskPK().setUsername(worksOnTask.getWorksOnProject().getWorksOnProjectPK().getUsername());
        worksOnTask.getWorksOnTaskPK().setIDTask(worksOnTask.getTask().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task task = worksOnTask.getTask();
            if (task != null) {
                task = em.getReference(task.getClass(), task.getId());
                worksOnTask.setTask(task);
            }
            WorksOnProject worksOnProject = worksOnTask.getWorksOnProject();
            if (worksOnProject != null) {
                worksOnProject = em.getReference(worksOnProject.getClass(), worksOnProject.getWorksOnProjectPK());
                worksOnTask.setWorksOnProject(worksOnProject);
            }
            Timetable timetable = worksOnTask.getTimetable();
            if (timetable != null) {
                timetable = em.getReference(timetable.getClass(), timetable.getTimetablePK());
                worksOnTask.setTimetable(timetable);
            }
            List<Activity> attachedActivityList = new ArrayList<Activity>();
            for (Activity activityListActivityToAttach : worksOnTask.getActivityList()) {
                activityListActivityToAttach = em.getReference(activityListActivityToAttach.getClass(), activityListActivityToAttach.getId());
                attachedActivityList.add(activityListActivityToAttach);
            }
            worksOnTask.setActivityList(attachedActivityList);
            List<Timetable> attachedTimetableList = new ArrayList<Timetable>();
            for (Timetable timetableListTimetableToAttach : worksOnTask.getTimetableList()) {
                timetableListTimetableToAttach = em.getReference(timetableListTimetableToAttach.getClass(), timetableListTimetableToAttach.getTimetablePK());
                attachedTimetableList.add(timetableListTimetableToAttach);
            }
            worksOnTask.setTimetableList(attachedTimetableList);
            em.persist(worksOnTask);
            if (task != null) {
                task.getWorksOnTaskList().add(worksOnTask);
                task = em.merge(task);
            }
            if (worksOnProject != null) {
                worksOnProject.getWorksOnTaskList().add(worksOnTask);
                worksOnProject = em.merge(worksOnProject);
            }
            if (timetable != null) {
                WorksOnTask oldWorksOnTaskOfTimetable = timetable.getWorksOnTask();
                if (oldWorksOnTaskOfTimetable != null) {
                    oldWorksOnTaskOfTimetable.setTimetable(null);
                    oldWorksOnTaskOfTimetable = em.merge(oldWorksOnTaskOfTimetable);
                }
                timetable.setWorksOnTask(worksOnTask);
                timetable = em.merge(timetable);
            }
            for (Activity activityListActivity : worksOnTask.getActivityList()) {
                WorksOnTask oldWorksOnTaskOfActivityListActivity = activityListActivity.getWorksOnTask();
                activityListActivity.setWorksOnTask(worksOnTask);
                activityListActivity = em.merge(activityListActivity);
                if (oldWorksOnTaskOfActivityListActivity != null) {
                    oldWorksOnTaskOfActivityListActivity.getActivityList().remove(activityListActivity);
                    oldWorksOnTaskOfActivityListActivity = em.merge(oldWorksOnTaskOfActivityListActivity);
                }
            }
            for (Timetable timetableListTimetable : worksOnTask.getTimetableList()) {
                WorksOnTask oldWorksOnTaskOfTimetableListTimetable = timetableListTimetable.getWorksOnTask();
                timetableListTimetable.setWorksOnTask(worksOnTask);
                timetableListTimetable = em.merge(timetableListTimetable);
                if (oldWorksOnTaskOfTimetableListTimetable != null) {
                    oldWorksOnTaskOfTimetableListTimetable.getTimetableList().remove(timetableListTimetable);
                    oldWorksOnTaskOfTimetableListTimetable = em.merge(oldWorksOnTaskOfTimetableListTimetable);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findWorksOnTask(worksOnTask.getWorksOnTaskPK()) != null) {
                throw new PreexistingEntityException("WorksOnTask " + worksOnTask + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorksOnTask worksOnTask) throws IllegalOrphanException, NonexistentEntityException, Exception {
        worksOnTask.getWorksOnTaskPK().setIDProject(worksOnTask.getWorksOnProject().getWorksOnProjectPK().getIDProject());
        worksOnTask.getWorksOnTaskPK().setUsername(worksOnTask.getWorksOnProject().getWorksOnProjectPK().getUsername());
        worksOnTask.getWorksOnTaskPK().setIDTask(worksOnTask.getTask().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnTask persistentWorksOnTask = em.find(WorksOnTask.class, worksOnTask.getWorksOnTaskPK());
            Task taskOld = persistentWorksOnTask.getTask();
            Task taskNew = worksOnTask.getTask();
            WorksOnProject worksOnProjectOld = persistentWorksOnTask.getWorksOnProject();
            WorksOnProject worksOnProjectNew = worksOnTask.getWorksOnProject();
            Timetable timetableOld = persistentWorksOnTask.getTimetable();
            Timetable timetableNew = worksOnTask.getTimetable();
            List<Activity> activityListOld = persistentWorksOnTask.getActivityList();
            List<Activity> activityListNew = worksOnTask.getActivityList();
            List<Timetable> timetableListOld = persistentWorksOnTask.getTimetableList();
            List<Timetable> timetableListNew = worksOnTask.getTimetableList();
            List<String> illegalOrphanMessages = null;
            if (timetableOld != null && !timetableOld.equals(timetableNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Timetable " + timetableOld + " since its worksOnTask field is not nullable.");
            }
            for (Timetable timetableListOldTimetable : timetableListOld) {
                if (!timetableListNew.contains(timetableListOldTimetable)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Timetable " + timetableListOldTimetable + " since its worksOnTask field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (taskNew != null) {
                taskNew = em.getReference(taskNew.getClass(), taskNew.getId());
                worksOnTask.setTask(taskNew);
            }
            if (worksOnProjectNew != null) {
                worksOnProjectNew = em.getReference(worksOnProjectNew.getClass(), worksOnProjectNew.getWorksOnProjectPK());
                worksOnTask.setWorksOnProject(worksOnProjectNew);
            }
            if (timetableNew != null) {
                timetableNew = em.getReference(timetableNew.getClass(), timetableNew.getTimetablePK());
                worksOnTask.setTimetable(timetableNew);
            }
            List<Activity> attachedActivityListNew = new ArrayList<Activity>();
            for (Activity activityListNewActivityToAttach : activityListNew) {
                activityListNewActivityToAttach = em.getReference(activityListNewActivityToAttach.getClass(), activityListNewActivityToAttach.getId());
                attachedActivityListNew.add(activityListNewActivityToAttach);
            }
            activityListNew = attachedActivityListNew;
            worksOnTask.setActivityList(activityListNew);
            List<Timetable> attachedTimetableListNew = new ArrayList<Timetable>();
            for (Timetable timetableListNewTimetableToAttach : timetableListNew) {
                timetableListNewTimetableToAttach = em.getReference(timetableListNewTimetableToAttach.getClass(), timetableListNewTimetableToAttach.getTimetablePK());
                attachedTimetableListNew.add(timetableListNewTimetableToAttach);
            }
            timetableListNew = attachedTimetableListNew;
            worksOnTask.setTimetableList(timetableListNew);
            worksOnTask = em.merge(worksOnTask);
            if (taskOld != null && !taskOld.equals(taskNew)) {
                taskOld.getWorksOnTaskList().remove(worksOnTask);
                taskOld = em.merge(taskOld);
            }
            if (taskNew != null && !taskNew.equals(taskOld)) {
                taskNew.getWorksOnTaskList().add(worksOnTask);
                taskNew = em.merge(taskNew);
            }
            if (worksOnProjectOld != null && !worksOnProjectOld.equals(worksOnProjectNew)) {
                worksOnProjectOld.getWorksOnTaskList().remove(worksOnTask);
                worksOnProjectOld = em.merge(worksOnProjectOld);
            }
            if (worksOnProjectNew != null && !worksOnProjectNew.equals(worksOnProjectOld)) {
                worksOnProjectNew.getWorksOnTaskList().add(worksOnTask);
                worksOnProjectNew = em.merge(worksOnProjectNew);
            }
            if (timetableNew != null && !timetableNew.equals(timetableOld)) {
                WorksOnTask oldWorksOnTaskOfTimetable = timetableNew.getWorksOnTask();
                if (oldWorksOnTaskOfTimetable != null) {
                    oldWorksOnTaskOfTimetable.setTimetable(null);
                    oldWorksOnTaskOfTimetable = em.merge(oldWorksOnTaskOfTimetable);
                }
                timetableNew.setWorksOnTask(worksOnTask);
                timetableNew = em.merge(timetableNew);
            }
            for (Activity activityListOldActivity : activityListOld) {
                if (!activityListNew.contains(activityListOldActivity)) {
                    activityListOldActivity.setWorksOnTask(null);
                    activityListOldActivity = em.merge(activityListOldActivity);
                }
            }
            for (Activity activityListNewActivity : activityListNew) {
                if (!activityListOld.contains(activityListNewActivity)) {
                    WorksOnTask oldWorksOnTaskOfActivityListNewActivity = activityListNewActivity.getWorksOnTask();
                    activityListNewActivity.setWorksOnTask(worksOnTask);
                    activityListNewActivity = em.merge(activityListNewActivity);
                    if (oldWorksOnTaskOfActivityListNewActivity != null && !oldWorksOnTaskOfActivityListNewActivity.equals(worksOnTask)) {
                        oldWorksOnTaskOfActivityListNewActivity.getActivityList().remove(activityListNewActivity);
                        oldWorksOnTaskOfActivityListNewActivity = em.merge(oldWorksOnTaskOfActivityListNewActivity);
                    }
                }
            }
            for (Timetable timetableListNewTimetable : timetableListNew) {
                if (!timetableListOld.contains(timetableListNewTimetable)) {
                    WorksOnTask oldWorksOnTaskOfTimetableListNewTimetable = timetableListNewTimetable.getWorksOnTask();
                    timetableListNewTimetable.setWorksOnTask(worksOnTask);
                    timetableListNewTimetable = em.merge(timetableListNewTimetable);
                    if (oldWorksOnTaskOfTimetableListNewTimetable != null && !oldWorksOnTaskOfTimetableListNewTimetable.equals(worksOnTask)) {
                        oldWorksOnTaskOfTimetableListNewTimetable.getTimetableList().remove(timetableListNewTimetable);
                        oldWorksOnTaskOfTimetableListNewTimetable = em.merge(oldWorksOnTaskOfTimetableListNewTimetable);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorksOnTaskPK id = worksOnTask.getWorksOnTaskPK();
                if (findWorksOnTask(id) == null) {
                    throw new NonexistentEntityException("The worksOnTask with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(WorksOnTaskPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnTask worksOnTask;
            try {
                worksOnTask = em.getReference(WorksOnTask.class, id);
                worksOnTask.getWorksOnTaskPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The worksOnTask with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Timetable timetableOrphanCheck = worksOnTask.getTimetable();
            if (timetableOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This WorksOnTask (" + worksOnTask + ") cannot be destroyed since the Timetable " + timetableOrphanCheck + " in its timetable field has a non-nullable worksOnTask field.");
            }
            List<Timetable> timetableListOrphanCheck = worksOnTask.getTimetableList();
            for (Timetable timetableListOrphanCheckTimetable : timetableListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This WorksOnTask (" + worksOnTask + ") cannot be destroyed since the Timetable " + timetableListOrphanCheckTimetable + " in its timetableList field has a non-nullable worksOnTask field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Task task = worksOnTask.getTask();
            if (task != null) {
                task.getWorksOnTaskList().remove(worksOnTask);
                task = em.merge(task);
            }
            WorksOnProject worksOnProject = worksOnTask.getWorksOnProject();
            if (worksOnProject != null) {
                worksOnProject.getWorksOnTaskList().remove(worksOnTask);
                worksOnProject = em.merge(worksOnProject);
            }
            List<Activity> activityList = worksOnTask.getActivityList();
            for (Activity activityListActivity : activityList) {
                activityListActivity.setWorksOnTask(null);
                activityListActivity = em.merge(activityListActivity);
            }
            em.remove(worksOnTask);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorksOnTask> findWorksOnTaskEntities() {
        return findWorksOnTaskEntities(true, -1, -1);
    }

    public List<WorksOnTask> findWorksOnTaskEntities(int maxResults, int firstResult) {
        return findWorksOnTaskEntities(false, maxResults, firstResult);
    }

    private List<WorksOnTask> findWorksOnTaskEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorksOnTask.class));
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

    public WorksOnTask findWorksOnTask(WorksOnTaskPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorksOnTask.class, id);
        } finally {
            em.close();
        }
    }

    public int getWorksOnTaskCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorksOnTask> rt = cq.from(WorksOnTask.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
