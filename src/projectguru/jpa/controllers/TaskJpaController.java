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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.ClosureTasks;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class TaskJpaController implements Serializable {

    public TaskJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Task task) {
        if (task.getWorksOnTaskList() == null) {
            task.setWorksOnTaskList(new ArrayList<WorksOnTask>());
        }
        if (task.getClosureTasksList() == null) {
            task.setClosureTasksList(new ArrayList<ClosureTasks>());
        }
        if (task.getClosureTasksList1() == null) {
            task.setClosureTasksList1(new ArrayList<ClosureTasks>());
        }
        if (task.getProjectList() == null) {
            task.setProjectList(new ArrayList<Project>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<WorksOnTask> attachedWorksOnTaskList = new ArrayList<WorksOnTask>();
            for (WorksOnTask worksOnTaskListWorksOnTaskToAttach : task.getWorksOnTaskList()) {
                worksOnTaskListWorksOnTaskToAttach = em.getReference(worksOnTaskListWorksOnTaskToAttach.getClass(), worksOnTaskListWorksOnTaskToAttach.getWorksOnTaskPK());
                attachedWorksOnTaskList.add(worksOnTaskListWorksOnTaskToAttach);
            }
            task.setWorksOnTaskList(attachedWorksOnTaskList);
            List<ClosureTasks> attachedClosureTasksList = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksListClosureTasksToAttach : task.getClosureTasksList()) {
                closureTasksListClosureTasksToAttach = em.getReference(closureTasksListClosureTasksToAttach.getClass(), closureTasksListClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksList.add(closureTasksListClosureTasksToAttach);
            }
            task.setClosureTasksList(attachedClosureTasksList);
            List<ClosureTasks> attachedClosureTasksList1 = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksList1ClosureTasksToAttach : task.getClosureTasksList1()) {
                closureTasksList1ClosureTasksToAttach = em.getReference(closureTasksList1ClosureTasksToAttach.getClass(), closureTasksList1ClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksList1.add(closureTasksList1ClosureTasksToAttach);
            }
            task.setClosureTasksList1(attachedClosureTasksList1);
            List<Project> attachedProjectList = new ArrayList<Project>();
            for (Project projectListProjectToAttach : task.getProjectList()) {
                projectListProjectToAttach = em.getReference(projectListProjectToAttach.getClass(), projectListProjectToAttach.getId());
                attachedProjectList.add(projectListProjectToAttach);
            }
            task.setProjectList(attachedProjectList);
            em.persist(task);
            for (WorksOnTask worksOnTaskListWorksOnTask : task.getWorksOnTaskList()) {
                Task oldTaskOfWorksOnTaskListWorksOnTask = worksOnTaskListWorksOnTask.getTask();
                worksOnTaskListWorksOnTask.setTask(task);
                worksOnTaskListWorksOnTask = em.merge(worksOnTaskListWorksOnTask);
                if (oldTaskOfWorksOnTaskListWorksOnTask != null) {
                    oldTaskOfWorksOnTaskListWorksOnTask.getWorksOnTaskList().remove(worksOnTaskListWorksOnTask);
                    oldTaskOfWorksOnTaskListWorksOnTask = em.merge(oldTaskOfWorksOnTaskListWorksOnTask);
                }
            }
            for (ClosureTasks closureTasksListClosureTasks : task.getClosureTasksList()) {
                Task oldTaskOfClosureTasksListClosureTasks = closureTasksListClosureTasks.getTask();
                closureTasksListClosureTasks.setTask(task);
                closureTasksListClosureTasks = em.merge(closureTasksListClosureTasks);
                if (oldTaskOfClosureTasksListClosureTasks != null) {
                    oldTaskOfClosureTasksListClosureTasks.getClosureTasksList().remove(closureTasksListClosureTasks);
                    oldTaskOfClosureTasksListClosureTasks = em.merge(oldTaskOfClosureTasksListClosureTasks);
                }
            }
            for (ClosureTasks closureTasksList1ClosureTasks : task.getClosureTasksList1()) {
                Task oldTask1OfClosureTasksList1ClosureTasks = closureTasksList1ClosureTasks.getTask1();
                closureTasksList1ClosureTasks.setTask1(task);
                closureTasksList1ClosureTasks = em.merge(closureTasksList1ClosureTasks);
                if (oldTask1OfClosureTasksList1ClosureTasks != null) {
                    oldTask1OfClosureTasksList1ClosureTasks.getClosureTasksList1().remove(closureTasksList1ClosureTasks);
                    oldTask1OfClosureTasksList1ClosureTasks = em.merge(oldTask1OfClosureTasksList1ClosureTasks);
                }
            }
            for (Project projectListProject : task.getProjectList()) {
                Task oldIDRootTaskOfProjectListProject = projectListProject.getIDRootTask();
                projectListProject.setIDRootTask(task);
                projectListProject = em.merge(projectListProject);
                if (oldIDRootTaskOfProjectListProject != null) {
                    oldIDRootTaskOfProjectListProject.getProjectList().remove(projectListProject);
                    oldIDRootTaskOfProjectListProject = em.merge(oldIDRootTaskOfProjectListProject);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Task task) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task persistentTask = em.find(Task.class, task.getId());
            List<WorksOnTask> worksOnTaskListOld = persistentTask.getWorksOnTaskList();
            List<WorksOnTask> worksOnTaskListNew = task.getWorksOnTaskList();
            List<ClosureTasks> closureTasksListOld = persistentTask.getClosureTasksList();
            List<ClosureTasks> closureTasksListNew = task.getClosureTasksList();
            List<ClosureTasks> closureTasksList1Old = persistentTask.getClosureTasksList1();
            List<ClosureTasks> closureTasksList1New = task.getClosureTasksList1();
            List<Project> projectListOld = persistentTask.getProjectList();
            List<Project> projectListNew = task.getProjectList();
            List<String> illegalOrphanMessages = null;
            for (WorksOnTask worksOnTaskListOldWorksOnTask : worksOnTaskListOld) {
                if (!worksOnTaskListNew.contains(worksOnTaskListOldWorksOnTask)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorksOnTask " + worksOnTaskListOldWorksOnTask + " since its task field is not nullable.");
                }
            }
            for (ClosureTasks closureTasksListOldClosureTasks : closureTasksListOld) {
                if (!closureTasksListNew.contains(closureTasksListOldClosureTasks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ClosureTasks " + closureTasksListOldClosureTasks + " since its task field is not nullable.");
                }
            }
            for (ClosureTasks closureTasksList1OldClosureTasks : closureTasksList1Old) {
                if (!closureTasksList1New.contains(closureTasksList1OldClosureTasks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ClosureTasks " + closureTasksList1OldClosureTasks + " since its task1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<WorksOnTask> attachedWorksOnTaskListNew = new ArrayList<WorksOnTask>();
            for (WorksOnTask worksOnTaskListNewWorksOnTaskToAttach : worksOnTaskListNew) {
                worksOnTaskListNewWorksOnTaskToAttach = em.getReference(worksOnTaskListNewWorksOnTaskToAttach.getClass(), worksOnTaskListNewWorksOnTaskToAttach.getWorksOnTaskPK());
                attachedWorksOnTaskListNew.add(worksOnTaskListNewWorksOnTaskToAttach);
            }
            worksOnTaskListNew = attachedWorksOnTaskListNew;
            task.setWorksOnTaskList(worksOnTaskListNew);
            List<ClosureTasks> attachedClosureTasksListNew = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksListNewClosureTasksToAttach : closureTasksListNew) {
                closureTasksListNewClosureTasksToAttach = em.getReference(closureTasksListNewClosureTasksToAttach.getClass(), closureTasksListNewClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksListNew.add(closureTasksListNewClosureTasksToAttach);
            }
            closureTasksListNew = attachedClosureTasksListNew;
            task.setClosureTasksList(closureTasksListNew);
            List<ClosureTasks> attachedClosureTasksList1New = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksList1NewClosureTasksToAttach : closureTasksList1New) {
                closureTasksList1NewClosureTasksToAttach = em.getReference(closureTasksList1NewClosureTasksToAttach.getClass(), closureTasksList1NewClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksList1New.add(closureTasksList1NewClosureTasksToAttach);
            }
            closureTasksList1New = attachedClosureTasksList1New;
            task.setClosureTasksList1(closureTasksList1New);
            List<Project> attachedProjectListNew = new ArrayList<Project>();
            for (Project projectListNewProjectToAttach : projectListNew) {
                projectListNewProjectToAttach = em.getReference(projectListNewProjectToAttach.getClass(), projectListNewProjectToAttach.getId());
                attachedProjectListNew.add(projectListNewProjectToAttach);
            }
            projectListNew = attachedProjectListNew;
            task.setProjectList(projectListNew);
            task = em.merge(task);
            for (WorksOnTask worksOnTaskListNewWorksOnTask : worksOnTaskListNew) {
                if (!worksOnTaskListOld.contains(worksOnTaskListNewWorksOnTask)) {
                    Task oldTaskOfWorksOnTaskListNewWorksOnTask = worksOnTaskListNewWorksOnTask.getTask();
                    worksOnTaskListNewWorksOnTask.setTask(task);
                    worksOnTaskListNewWorksOnTask = em.merge(worksOnTaskListNewWorksOnTask);
                    if (oldTaskOfWorksOnTaskListNewWorksOnTask != null && !oldTaskOfWorksOnTaskListNewWorksOnTask.equals(task)) {
                        oldTaskOfWorksOnTaskListNewWorksOnTask.getWorksOnTaskList().remove(worksOnTaskListNewWorksOnTask);
                        oldTaskOfWorksOnTaskListNewWorksOnTask = em.merge(oldTaskOfWorksOnTaskListNewWorksOnTask);
                    }
                }
            }
            for (ClosureTasks closureTasksListNewClosureTasks : closureTasksListNew) {
                if (!closureTasksListOld.contains(closureTasksListNewClosureTasks)) {
                    Task oldTaskOfClosureTasksListNewClosureTasks = closureTasksListNewClosureTasks.getTask();
                    closureTasksListNewClosureTasks.setTask(task);
                    closureTasksListNewClosureTasks = em.merge(closureTasksListNewClosureTasks);
                    if (oldTaskOfClosureTasksListNewClosureTasks != null && !oldTaskOfClosureTasksListNewClosureTasks.equals(task)) {
                        oldTaskOfClosureTasksListNewClosureTasks.getClosureTasksList().remove(closureTasksListNewClosureTasks);
                        oldTaskOfClosureTasksListNewClosureTasks = em.merge(oldTaskOfClosureTasksListNewClosureTasks);
                    }
                }
            }
            for (ClosureTasks closureTasksList1NewClosureTasks : closureTasksList1New) {
                if (!closureTasksList1Old.contains(closureTasksList1NewClosureTasks)) {
                    Task oldTask1OfClosureTasksList1NewClosureTasks = closureTasksList1NewClosureTasks.getTask1();
                    closureTasksList1NewClosureTasks.setTask1(task);
                    closureTasksList1NewClosureTasks = em.merge(closureTasksList1NewClosureTasks);
                    if (oldTask1OfClosureTasksList1NewClosureTasks != null && !oldTask1OfClosureTasksList1NewClosureTasks.equals(task)) {
                        oldTask1OfClosureTasksList1NewClosureTasks.getClosureTasksList1().remove(closureTasksList1NewClosureTasks);
                        oldTask1OfClosureTasksList1NewClosureTasks = em.merge(oldTask1OfClosureTasksList1NewClosureTasks);
                    }
                }
            }
            for (Project projectListOldProject : projectListOld) {
                if (!projectListNew.contains(projectListOldProject)) {
                    projectListOldProject.setIDRootTask(null);
                    projectListOldProject = em.merge(projectListOldProject);
                }
            }
            for (Project projectListNewProject : projectListNew) {
                if (!projectListOld.contains(projectListNewProject)) {
                    Task oldIDRootTaskOfProjectListNewProject = projectListNewProject.getIDRootTask();
                    projectListNewProject.setIDRootTask(task);
                    projectListNewProject = em.merge(projectListNewProject);
                    if (oldIDRootTaskOfProjectListNewProject != null && !oldIDRootTaskOfProjectListNewProject.equals(task)) {
                        oldIDRootTaskOfProjectListNewProject.getProjectList().remove(projectListNewProject);
                        oldIDRootTaskOfProjectListNewProject = em.merge(oldIDRootTaskOfProjectListNewProject);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = task.getId();
                if (findTask(id) == null) {
                    throw new NonexistentEntityException("The task with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task task;
            try {
                task = em.getReference(Task.class, id);
                task.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The task with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorksOnTask> worksOnTaskListOrphanCheck = task.getWorksOnTaskList();
            for (WorksOnTask worksOnTaskListOrphanCheckWorksOnTask : worksOnTaskListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Task (" + task + ") cannot be destroyed since the WorksOnTask " + worksOnTaskListOrphanCheckWorksOnTask + " in its worksOnTaskList field has a non-nullable task field.");
            }
            List<ClosureTasks> closureTasksListOrphanCheck = task.getClosureTasksList();
            for (ClosureTasks closureTasksListOrphanCheckClosureTasks : closureTasksListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Task (" + task + ") cannot be destroyed since the ClosureTasks " + closureTasksListOrphanCheckClosureTasks + " in its closureTasksList field has a non-nullable task field.");
            }
            List<ClosureTasks> closureTasksList1OrphanCheck = task.getClosureTasksList1();
            for (ClosureTasks closureTasksList1OrphanCheckClosureTasks : closureTasksList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Task (" + task + ") cannot be destroyed since the ClosureTasks " + closureTasksList1OrphanCheckClosureTasks + " in its closureTasksList1 field has a non-nullable task1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Project> projectList = task.getProjectList();
            for (Project projectListProject : projectList) {
                projectListProject.setIDRootTask(null);
                projectListProject = em.merge(projectListProject);
            }
            em.remove(task);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Task> findTaskEntities() {
        return findTaskEntities(true, -1, -1);
    }

    public List<Task> findTaskEntities(int maxResults, int firstResult) {
        return findTaskEntities(false, maxResults, firstResult);
    }

    private List<Task> findTaskEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Task.class));
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

    public Task findTask(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Task.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaskCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Task> rt = cq.from(Task.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
