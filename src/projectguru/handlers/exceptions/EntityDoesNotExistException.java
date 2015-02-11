/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru.handlers.exceptions;

/**
 *
 * @author marko
 */
public class EntityDoesNotExistException extends Exception {

    /**
     * Creates a new instance of <code>EntityDoesNotExistException</code>
     * without detail message.
     */
    public EntityDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>EntityDoesNotExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public EntityDoesNotExistException(String msg) {
        super(msg);
    }
}
