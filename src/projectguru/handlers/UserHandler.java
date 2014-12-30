/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.entities.User;

/**
 *
 * @author ZM
 */
public interface UserHandler {
    
     public boolean hasAdminPrivileges();
     
     public boolean addUser(User user);
     public boolean editUser(User user);
     public void setActivated(User user, boolean flag);
     
}
