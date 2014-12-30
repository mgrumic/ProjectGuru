/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import projectguru.*;
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
    
}
