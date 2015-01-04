/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projectguru.*;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.controllers.UserJpaController;
import projectguru.jpa.handlers.JpaLoggedUser;
/**
 *
 * @author ZM
 */
public class JpaAccessManager extends AccessManager{
    
    protected EntityManagerFactory factory;
    
    public JpaAccessManager(String persistanceUnitName){
       this.factory = Persistence.createEntityManagerFactory(persistanceUnitName);     
    }

    public EntityManagerFactory getFactory() {
        return factory;
    }
    
    

    @Override
    public LoggedUser logUserIn(String username, String password) {
        UserJpaController userCtrl = new UserJpaController(factory);
        User u = userCtrl.findUser(username);
        if(u != null && u.getPassword().equals(password)){
            return new JpaLoggedUser(u);
        }
        
        return null;
    }
    
}
