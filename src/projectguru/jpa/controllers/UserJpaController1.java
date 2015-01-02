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
import projectguru.entities.WorksOnProject;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.User;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;
import projectguru.jpa.controllers.exceptions.PreexistingEntityException;

/**
 *
 * @author marko
 */
public class UserJpaController1 implements Serializable {

    public UserJpaController1(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getWorksOnProjectList() == null) {
            user.setWorksOnProjectList(new ArrayList<WorksOnProject>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<WorksOnProject> attachedWorksOnProjectList = new ArrayList<WorksOnProject>();
            for (WorksOnProject worksOnProjectListWorksOnProjectToAttach : user.getWorksOnProjectList()) {
                worksOnProjectListWorksOnProjectToAttach = em.getReference(worksOnProjectListWorksOnProjectToAttach.getClass(), worksOnProjectListWorksOnProjectToAttach.getWorksOnProjectPK());
                attachedWorksOnProjectList.add(worksOnProjectListWorksOnProjectToAttach);
            }
            user.setWorksOnProjectList(attachedWorksOnProjectList);
            em.persist(user);
            for (WorksOnProject worksOnProjectListWorksOnProject : user.getWorksOnProjectList()) {
                User oldUserOfWorksOnProjectListWorksOnProject = worksOnProjectListWorksOnProject.getUser();
                worksOnProjectListWorksOnProject.setUser(user);
                worksOnProjectListWorksOnProject = em.merge(worksOnProjectListWorksOnProject);
                if (oldUserOfWorksOnProjectListWorksOnProject != null) {
                    oldUserOfWorksOnProjectListWorksOnProject.getWorksOnProjectList().remove(worksOnProjectListWorksOnProject);
                    oldUserOfWorksOnProjectListWorksOnProject = em.merge(oldUserOfWorksOnProjectListWorksOnProject);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getUsername()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getUsername());
            List<WorksOnProject> worksOnProjectListOld = persistentUser.getWorksOnProjectList();
            List<WorksOnProject> worksOnProjectListNew = user.getWorksOnProjectList();
            List<String> illegalOrphanMessages = null;
            for (WorksOnProject worksOnProjectListOldWorksOnProject : worksOnProjectListOld) {
                if (!worksOnProjectListNew.contains(worksOnProjectListOldWorksOnProject)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorksOnProject " + worksOnProjectListOldWorksOnProject + " since its user field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<WorksOnProject> attachedWorksOnProjectListNew = new ArrayList<WorksOnProject>();
            for (WorksOnProject worksOnProjectListNewWorksOnProjectToAttach : worksOnProjectListNew) {
                worksOnProjectListNewWorksOnProjectToAttach = em.getReference(worksOnProjectListNewWorksOnProjectToAttach.getClass(), worksOnProjectListNewWorksOnProjectToAttach.getWorksOnProjectPK());
                attachedWorksOnProjectListNew.add(worksOnProjectListNewWorksOnProjectToAttach);
            }
            worksOnProjectListNew = attachedWorksOnProjectListNew;
            user.setWorksOnProjectList(worksOnProjectListNew);
            user = em.merge(user);
            for (WorksOnProject worksOnProjectListNewWorksOnProject : worksOnProjectListNew) {
                if (!worksOnProjectListOld.contains(worksOnProjectListNewWorksOnProject)) {
                    User oldUserOfWorksOnProjectListNewWorksOnProject = worksOnProjectListNewWorksOnProject.getUser();
                    worksOnProjectListNewWorksOnProject.setUser(user);
                    worksOnProjectListNewWorksOnProject = em.merge(worksOnProjectListNewWorksOnProject);
                    if (oldUserOfWorksOnProjectListNewWorksOnProject != null && !oldUserOfWorksOnProjectListNewWorksOnProject.equals(user)) {
                        oldUserOfWorksOnProjectListNewWorksOnProject.getWorksOnProjectList().remove(worksOnProjectListNewWorksOnProject);
                        oldUserOfWorksOnProjectListNewWorksOnProject = em.merge(oldUserOfWorksOnProjectListNewWorksOnProject);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = user.getUsername();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUsername();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorksOnProject> worksOnProjectListOrphanCheck = user.getWorksOnProjectList();
            for (WorksOnProject worksOnProjectListOrphanCheckWorksOnProject : worksOnProjectListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the WorksOnProject " + worksOnProjectListOrphanCheckWorksOnProject + " in its worksOnProjectList field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
