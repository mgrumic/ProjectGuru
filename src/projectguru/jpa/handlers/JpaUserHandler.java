/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.AccessManager;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
   public boolean editUser(User user) throws EntityDoesNotExistException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (user.getUsername() == null || em.find(User.class, user.getUsername()) == null) {
                throw new EntityDoesNotExistException("User does not exist in database.");
            }
            try {
                em.getTransaction().begin();
                em.merge(user); //merge kreira novi entitet a sacuva u isti red u bazu,
                em.getTransaction().commit();

                return true;
            } catch (Exception ex) {
                Logger.getLogger(JpaUserHandler.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
            return false;
        }
    }

    @Override
    public void setActivated(User user, boolean flag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
