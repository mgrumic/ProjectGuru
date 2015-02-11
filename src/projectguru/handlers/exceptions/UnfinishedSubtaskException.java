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
public class UnfinishedSubtaskException extends Exception {

    /**
     * Creates a new instance of <code>UnfinishedSubtaskException</code> without
     * detail message.
     */
    public UnfinishedSubtaskException() {
    }

    /**
     * Constructs an instance of <code>UnfinishedSubtaskException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnfinishedSubtaskException(String msg) {
        super(msg);
    }
}
