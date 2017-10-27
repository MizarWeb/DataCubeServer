/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package common.exceptions;

/**
 * Exception si une ressource n'est pas disponible.
 * <p>
 * <br>
 * History: 1.0 creation <br>
 * <br>
 * 
 * @author Vincent Cephirins
 * @version 1.0
 */
public class UnavailableResourceException extends LoggerException {

    /**
     * 
     */
    private static final long serialVersionUID = 3044699714143131981L;

    /**
     * Constructeur.
     * <p>
     * 
     * @param pArguments précisions sur la ressource.
     */
    public UnavailableResourceException(Object... pArguments) {
        super("exception.unavailableResource", pArguments);
    }

    /**
     * Constructeur.
     * <p>
     * 
     * @param e L'exception d'origine.
     * @param pArguments précisions sur la ressource.
     */
    public UnavailableResourceException(Exception e, Object... pArguments) {
        super(e, "exception.unavailableResource", pArguments);
    }

}
