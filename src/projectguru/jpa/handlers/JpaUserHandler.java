/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import projectguru.AccessManager;
import projectguru.entities.Privileges;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.UserHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;

import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.JpaAccessManager;

/**
 *
 * @author ZM
 */
public class JpaUserHandler implements UserHandler {

    private LoggedUser user;

    public JpaUserHandler(LoggedUser user) {
        this.user = user;
    }

    @Override
    public boolean hasAdminPrivileges() {
        if (user.getUser().getAppPrivileges() == Privileges.ADMIN.ordinal()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addUser(User user) throws StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        boolean result = false;
        try {
            if (em.find(User.class, user.getUsername()) == null) {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
                result = true;
            }
            return result;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public boolean editUser(User user) throws EntityDoesNotExistException, StoringException {
        //ko moze editovati Usera?
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (user.getUsername() == null || (em.find(User.class, user.getUsername())) == null) {
            throw new EntityDoesNotExistException("User does not exist.");
        }
        try {

            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
        return true;
    }

    @Override
    public void setActivated(User user, boolean flag) throws EntityDoesNotExistException, StoringException{
        user.setActivated(flag);
        editUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

}
