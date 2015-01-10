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
public class UserNotTaskMemberException extends Exception {

    /**
     * Creates a new instance of <code>UserNotTaskMemberException</code> without
     * detail message.
     */
    public UserNotTaskMemberException() {
    }

    /**
     * Constructs an instance of <code>UserNotTaskMemberException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UserNotTaskMemberException(String msg) {
        super(msg);
    }
}
