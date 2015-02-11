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
import projectguru.entities.User;
import projectguru.entities.Project;
import projectguru.entities.WorksOnTask;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.WorksOnProject;
import projectguru.entities.WorksOnProjectPK;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author marko
 */
public class WorksOnProjectJpaController implements Serializable {

    public WorksOnProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorksOnProject worksOnProject) throws PreexistingEntityException, Exception {
        if (worksOnProject.getWorksOnProjectPK() == null) {
            worksOnProject.setWorksOnProjectPK(new WorksOnProjectPK());
        }
        if (worksOnProject.getWorksOnTaskList() == null) {
            worksOnProject.setWorksOnTaskList(new ArrayList<WorksOnTask>());
        }
        worksOnProject.getWorksOnProjectPK().setIDProject(worksOnProject.getProject().getId());
        worksOnProject.getWorksOnProjectPK().setUsername(worksOnProject.getUser().getUsername());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user = worksOnProject.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUsername());
                worksOnProject.setUser(user);
            }
            Project project = worksOnProject.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                worksOnProject.setProject(project);
            }
            List<WorksOnTask> attachedWorksOnTaskList = new ArrayList<WorksOnTask>();
            for (WorksOnTask worksOnTaskListWorksOnTaskToAttach : worksOnProject.getWorksOnTaskList()) {
                worksOnTaskListWorksOnTaskToAttach = em.getReference(worksOnTaskListWorksOnTaskToAttach.getClass(), worksOnTaskListWorksOnTaskToAttach.getWorksOnTaskPK());
                attachedWorksOnTaskList.add(worksOnTaskListWorksOnTaskToAttach);
            }
            worksOnProject.setWorksOnTaskList(attachedWorksOnTaskList);
            em.persist(worksOnProject);
            if (user != null) {
                user.getWorksOnProjectList().add(worksOnProject);
                user = em.merge(user);
            }
            if (project != null) {
                project.getWorksOnProjectList().add(worksOnProject);
                project = em.merge(project);
            }
            for (WorksOnTask worksOnTaskListWorksOnTask : worksOnProject.getWorksOnTaskList()) {
                WorksOnProject oldWorksOnProjectOfWorksOnTaskListWorksOnTask = worksOnTaskListWorksOnTask.getWorksOnProject();
                worksOnTaskListWorksOnTask.setWorksOnProject(worksOnProject);
                worksOnTaskListWorksOnTask = em.merge(worksOnTaskListWorksOnTask);
                if (oldWorksOnProjectOfWorksOnTaskListWorksOnTask != null) {
                    oldWorksOnProjectOfWorksOnTaskListWorksOnTask.getWorksOnTaskList().remove(worksOnTaskListWorksOnTask);
                    oldWorksOnProjectOfWorksOnTaskListWorksOnTask = em.merge(oldWorksOnProjectOfWorksOnTaskListWorksOnTask);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findWorksOnProject(worksOnProject.getWorksOnProjectPK()) != null) {
                throw new PreexistingEntityException("WorksOnProject " + worksOnProject + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorksOnProject worksOnProject) throws IllegalOrphanException, NonexistentEntityException, Exception {
        worksOnProject.getWorksOnProjectPK().setIDProject(worksOnProject.getProject().getId());
        worksOnProject.getWorksOnProjectPK().setUsername(worksOnProject.getUser().getUsername());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnProject persistentWorksOnProject = em.find(WorksOnProject.class, worksOnProject.getWorksOnProjectPK());
            User userOld = persistentWorksOnProject.getUser();
            User userNew = worksOnProject.getUser();
            Project projectOld = persistentWorksOnProject.getProject();
            Project projectNew = worksOnProject.getProject();
            List<WorksOnTask> worksOnTaskListOld = persistentWorksOnProject.getWorksOnTaskList();
            List<WorksOnTask> worksOnTaskListNew = worksOnProject.getWorksOnTaskList();
            List<String> illegalOrphanMessages = null;
            for (WorksOnTask worksOnTaskListOldWorksOnTask : worksOnTaskListOld) {
                if (!worksOnTaskListNew.contains(worksOnTaskListOldWorksOnTask)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorksOnTask " + worksOnTaskListOldWorksOnTask + " since its worksOnProject field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUsername());
                worksOnProject.setUser(userNew);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                worksOnProject.setProject(projectNew);
            }
            List<WorksOnTask> attachedWorksOnTaskListNew = new ArrayList<WorksOnTask>();
            for (WorksOnTask worksOnTaskListNewWorksOnTaskToAttach : worksOnTaskListNew) {
                worksOnTaskListNewWorksOnTaskToAttach = em.getReference(worksOnTaskListNewWorksOnTaskToAttach.getClass(), worksOnTaskListNewWorksOnTaskToAttach.getWorksOnTaskPK());
                attachedWorksOnTaskListNew.add(worksOnTaskListNewWorksOnTaskToAttach);
            }
            worksOnTaskListNew = attachedWorksOnTaskListNew;
            worksOnProject.setWorksOnTaskList(worksOnTaskListNew);
            worksOnProject = em.merge(worksOnProject);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getWorksOnProjectList().remove(worksOnProject);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getWorksOnProjectList().add(worksOnProject);
                userNew = em.merge(userNew);
            }
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getWorksOnProjectList().remove(worksOnProject);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getWorksOnProjectList().add(worksOnProject);
                projectNew = em.merge(projectNew);
            }
            for (WorksOnTask worksOnTaskListNewWorksOnTask : worksOnTaskListNew) {
                if (!worksOnTaskListOld.contains(worksOnTaskListNewWorksOnTask)) {
                    WorksOnProject oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask = worksOnTaskListNewWorksOnTask.getWorksOnProject();
                    worksOnTaskListNewWorksOnTask.setWorksOnProject(worksOnProject);
                    worksOnTaskListNewWorksOnTask = em.merge(worksOnTaskListNewWorksOnTask);
                    if (oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask != null && !oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask.equals(worksOnProject)) {
                        oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask.getWorksOnTaskList().remove(worksOnTaskListNewWorksOnTask);
                        oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask = em.merge(oldWorksOnProjectOfWorksOnTaskListNewWorksOnTask);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorksOnProjectPK id = worksOnProject.getWorksOnProjectPK();
                if (findWorksOnProject(id) == null) {
                    throw new NonexistentEntityException("The worksOnProject with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(WorksOnProjectPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorksOnProject worksOnProject;
            try {
                worksOnProject = em.getReference(WorksOnProject.class, id);
                worksOnProject.getWorksOnProjectPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The worksOnProject with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorksOnTask> worksOnTaskListOrphanCheck = worksOnProject.getWorksOnTaskList();
            for (WorksOnTask worksOnTaskListOrphanCheckWorksOnTask : worksOnTaskListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This WorksOnProject (" + worksOnProject + ") cannot be destroyed since the WorksOnTask " + worksOnTaskListOrphanCheckWorksOnTask + " in its worksOnTaskList field has a non-nullable worksOnProject field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User user = worksOnProject.getUser();
            if (user != null) {
                user.getWorksOnProjectList().remove(worksOnProject);
                user = em.merge(user);
            }
            Project project = worksOnProject.getProject();
            if (project != null) {
                project.getWorksOnProjectList().remove(worksOnProject);
                project = em.merge(project);
            }
            em.remove(worksOnProject);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorksOnProject> findWorksOnProjectEntities() {
        return findWorksOnProjectEntities(true, -1, -1);
    }

    public List<WorksOnProject> findWorksOnProjectEntities(int maxResults, int firstResult) {
        return findWorksOnProjectEntities(false, maxResults, firstResult);
    }

    private List<WorksOnProject> findWorksOnProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorksOnProject.class));
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

    public WorksOnProject findWorksOnProject(WorksOnProjectPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorksOnProject.class, id);
        } finally {
            em.close();
        }
    }

    public int getWorksOnProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorksOnProject> rt = cq.from(WorksOnProject.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
