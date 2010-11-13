/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.alma.hadl.fmk.exceptions;

/**
 *
 * @author judu
 */
public class ArchitectureException extends Exception {

    /**
     * Creates a new instance of <code>ArchitectureException</code> without detail message.
     */
    public ArchitectureException() {
    }


    /**
     * Constructs an instance of <code>ArchitectureException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ArchitectureException(String msg) {
        super(msg);
    }
}
