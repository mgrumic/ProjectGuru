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
public class InsuficientPrivilegesException extends Exception {

    /**
     * Creates a new instance of <code>InsuficientPrivilegesException</code>
     * without detail message.
     */
    public InsuficientPrivilegesException() {
    }

    /**
     * Constructs an instance of <code>InsuficientPrivilegesException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InsuficientPrivilegesException(String msg) {
        super(msg);
    }
}
