/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru;

import projectguru.handlers.LoggedUser;

/**
 *
 * @author ZM
 */
public abstract class AccessManager {
    
    protected static AccessManager manager;
          
    protected AccessManager(){

    }
    public static void setInstance(AccessManager mng){
        manager = mng;
    }
    public static AccessManager getInstance()
    {
        return manager;
    }
    
    public abstract LoggedUser logUserIn(String username, String password);
    
}
