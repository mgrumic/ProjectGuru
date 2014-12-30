/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.UserHandler;

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
    public boolean editUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setActivated(User user, boolean flag) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
