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
 * @author marko
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
        if (task.getClosureTasksChildren() == null) {
            task.setClosureTasksChildren(new ArrayList<ClosureTasks>());
        }
        if (task.getClosureTasksParents() == null) {
            task.setClosureTasksParents(new ArrayList<ClosureTasks>());
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
            List<ClosureTasks> attachedClosureTasksChildren = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksChildrenClosureTasksToAttach : task.getClosureTasksChildren()) {
                closureTasksChildrenClosureTasksToAttach = em.getReference(closureTasksChildrenClosureTasksToAttach.getClass(), closureTasksChildrenClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksChildren.add(closureTasksChildrenClosureTasksToAttach);
            }
            task.setClosureTasksChildren(attachedClosureTasksChildren);
            List<ClosureTasks> attachedClosureTasksParents = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksParentsClosureTasksToAttach : task.getClosureTasksParents()) {
                closureTasksParentsClosureTasksToAttach = em.getReference(closureTasksParentsClosureTasksToAttach.getClass(), closureTasksParentsClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksParents.add(closureTasksParentsClosureTasksToAttach);
            }
            task.setClosureTasksParents(attachedClosureTasksParents);
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
            for (ClosureTasks closureTasksChildrenClosureTasks : task.getClosureTasksChildren()) {
                Task oldParentOfClosureTasksChildrenClosureTasks = closureTasksChildrenClosureTasks.getParent();
                closureTasksChildrenClosureTasks.setParent(task);
                closureTasksChildrenClosureTasks = em.merge(closureTasksChildrenClosureTasks);
                if (oldParentOfClosureTasksChildrenClosureTasks != null) {
                    oldParentOfClosureTasksChildrenClosureTasks.getClosureTasksChildren().remove(closureTasksChildrenClosureTasks);
                    oldParentOfClosureTasksChildrenClosureTasks = em.merge(oldParentOfClosureTasksChildrenClosureTasks);
                }
            }
            for (ClosureTasks closureTasksParentsClosureTasks : task.getClosureTasksParents()) {
                Task oldChildOfClosureTasksParentsClosureTasks = closureTasksParentsClosureTasks.getChild();
                closureTasksParentsClosureTasks.setChild(task);
                closureTasksParentsClosureTasks = em.merge(closureTasksParentsClosureTasks);
                if (oldChildOfClosureTasksParentsClosureTasks != null) {
                    oldChildOfClosureTasksParentsClosureTasks.getClosureTasksParents().remove(closureTasksParentsClosureTasks);
                    oldChildOfClosureTasksParentsClosureTasks = em.merge(oldChildOfClosureTasksParentsClosureTasks);
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
            List<ClosureTasks> closureTasksChildrenOld = persistentTask.getClosureTasksChildren();
            List<ClosureTasks> closureTasksChildrenNew = task.getClosureTasksChildren();
            List<ClosureTasks> closureTasksParentsOld = persistentTask.getClosureTasksParents();
            List<ClosureTasks> closureTasksParentsNew = task.getClosureTasksParents();
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
            for (ClosureTasks closureTasksChildrenOldClosureTasks : closureTasksChildrenOld) {
                if (!closureTasksChildrenNew.contains(closureTasksChildrenOldClosureTasks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ClosureTasks " + closureTasksChildrenOldClosureTasks + " since its parent field is not nullable.");
                }
            }
            for (ClosureTasks closureTasksParentsOldClosureTasks : closureTasksParentsOld) {
                if (!closureTasksParentsNew.contains(closureTasksParentsOldClosureTasks)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ClosureTasks " + closureTasksParentsOldClosureTasks + " since its child field is not nullable.");
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
            List<ClosureTasks> attachedClosureTasksChildrenNew = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksChildrenNewClosureTasksToAttach : closureTasksChildrenNew) {
                closureTasksChildrenNewClosureTasksToAttach = em.getReference(closureTasksChildrenNewClosureTasksToAttach.getClass(), closureTasksChildrenNewClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksChildrenNew.add(closureTasksChildrenNewClosureTasksToAttach);
            }
            closureTasksChildrenNew = attachedClosureTasksChildrenNew;
            task.setClosureTasksChildren(closureTasksChildrenNew);
            List<ClosureTasks> attachedClosureTasksParentsNew = new ArrayList<ClosureTasks>();
            for (ClosureTasks closureTasksParentsNewClosureTasksToAttach : closureTasksParentsNew) {
                closureTasksParentsNewClosureTasksToAttach = em.getReference(closureTasksParentsNewClosureTasksToAttach.getClass(), closureTasksParentsNewClosureTasksToAttach.getClosureTasksPK());
                attachedClosureTasksParentsNew.add(closureTasksParentsNewClosureTasksToAttach);
            }
            closureTasksParentsNew = attachedClosureTasksParentsNew;
            task.setClosureTasksParents(closureTasksParentsNew);
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
            for (ClosureTasks closureTasksChildrenNewClosureTasks : closureTasksChildrenNew) {
                if (!closureTasksChildrenOld.contains(closureTasksChildrenNewClosureTasks)) {
                    Task oldParentOfClosureTasksChildrenNewClosureTasks = closureTasksChildrenNewClosureTasks.getParent();
                    closureTasksChildrenNewClosureTasks.setParent(task);
                    closureTasksChildrenNewClosureTasks = em.merge(closureTasksChildrenNewClosureTasks);
                    if (oldParentOfClosureTasksChildrenNewClosureTasks != null && !oldParentOfClosureTasksChildrenNewClosureTasks.equals(task)) {
                        oldParentOfClosureTasksChildrenNewClosureTasks.getClosureTasksChildren().remove(closureTasksChildrenNewClosureTasks);
                        oldParentOfClosureTasksChildrenNewClosureTasks = em.merge(oldParentOfClosureTasksChildrenNewClosureTasks);
                    }
                }
            }
            for (ClosureTasks closureTasksParentsNewClosureTasks : closureTasksParentsNew) {
                if (!closureTasksParentsOld.contains(closureTasksParentsNewClosureTasks)) {
                    Task oldChildOfClosureTasksParentsNewClosureTasks = closureTasksParentsNewClosureTasks.getChild();
                    closureTasksParentsNewClosureTasks.setChild(task);
                    closureTasksParentsNewClosureTasks = em.merge(closureTasksParentsNewClosureTasks);
                    if (oldChildOfClosureTasksParentsNewClosureTasks != null && !oldChildOfClosureTasksParentsNewClosureTasks.equals(task)) {
                        oldChildOfClosureTasksParentsNewClosureTasks.getClosureTasksParents().remove(closureTasksParentsNewClosureTasks);
                        oldChildOfClosureTasksParentsNewClosureTasks = em.merge(oldChildOfClosureTasksParentsNewClosureTasks);
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
            List<ClosureTasks> closureTasksChildrenOrphanCheck = task.getClosureTasksChildren();
            for (ClosureTasks closureTasksChildrenOrphanCheckClosureTasks : closureTasksChildrenOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Task (" + task + ") cannot be destroyed since the ClosureTasks " + closureTasksChildrenOrphanCheckClosureTasks + " in its closureTasksChildren field has a non-nullable parent field.");
            }
            List<ClosureTasks> closureTasksParentsOrphanCheck = task.getClosureTasksParents();
            for (ClosureTasks closureTasksParentsOrphanCheckClosureTasks : closureTasksParentsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Task (" + task + ") cannot be destroyed since the ClosureTasks " + closureTasksParentsOrphanCheckClosureTasks + " in its closureTasksParents field has a non-nullable child field.");
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
