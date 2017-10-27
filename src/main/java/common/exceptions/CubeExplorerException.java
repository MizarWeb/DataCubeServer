/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package common.exceptions;

/**
 * Exception principale de l'application.
 * <p>
 * <br>
 * History: 1.0 creation <br>
 * <br>
 * 
 * @author Vincent Cephirins
 * @version 1.0
 */
public class CubeExplorerException extends LoggerException {

    /**
     * 
     */
    private static final long serialVersionUID = 3044699714143131981L;

    /**
     * Constructeur
     */
    public CubeExplorerException() {
        super("exception.app");
    }

    /**
     * Constructeur
     * 
     * @param message L'identifiant du message à afficher.
     */
    public CubeExplorerException(String message) {
        super(message);
    }

    /**
     * Constructeur
     * 
     * @param message L'identifiant du message à afficher.
     * @param arguments les arguments du message d'erreur.
     */
    public CubeExplorerException(String message, Object... arguments) {
        super(message, arguments);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     */
    public CubeExplorerException(Exception e) {
        super(e, "exception.app");
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param message L'identifiant du message à afficher.
     */
    public CubeExplorerException(Exception e, String message) {
        super(e, message);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param message L'identifiant du message à afficher.
     * @param arguments les arguments du message d'erreur.
     */
    public CubeExplorerException(Exception e, String message, Object... arguments) {
        super(e, message, arguments);
    }
}
