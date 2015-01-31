/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import java.util.List;
import projectguru.entities.User;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.StoringException;

/**
 *
 * @author ZM
 */
public interface UserHandler {

    public boolean hasAdminPrivileges();

    public List<User> getAllUser();

    public boolean addUser(User user) throws StoringException;

    public boolean editUser(User user) throws EntityDoesNotExistException, StoringException;

    public void setActivated(User user, boolean flag);
}
