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
public class StoringException extends Exception {

    /**
     * Creates a new instance of <code>StoringException</code> without detail
     * message.
     */
    public StoringException() {
    }

    /**
     * Constructs an instance of <code>StoringException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public StoringException(String msg) {
        super(msg);
    }
}
