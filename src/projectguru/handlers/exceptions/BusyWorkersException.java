/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers.exceptions;

import java.util.ArrayList;
import java.util.List;
import projectguru.entities.User;

/**
 *
 * @author marko
 */
public class BusyWorkersException extends Exception {

    /**
     * Creates a new instance of <code>BusyWorkersException</code> without
     * detail message.
     */
    public BusyWorkersException() {
    }

    /**
     * Constructs an instance of <code>BusyWorkersException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BusyWorkersException(String msg) {
        super(msg);
    }
    
    private List<User> workers = new ArrayList<>();
    
    public List<User> getList(){
        return workers;
    }
    
    public void add(User u){
        workers.add(u);
    }
    
}
