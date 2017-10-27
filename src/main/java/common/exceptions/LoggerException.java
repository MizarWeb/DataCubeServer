/**
 * 
 */
package common.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Vincent Cephirins
 * @version 1.0
 *
 */
public class LoggerException extends SimpleException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Initialise un logger (voir log4j.xml).
    private static final Logger LOGGER = LogManager.getLogger(LoggerException.class);
    protected Logger logger = LOGGER;

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Constructeur
     */
    public LoggerException() {
        super();
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error � afficher
     * @param arguments les arguments du message d'erreur.
     */
    public LoggerException(Throwable e, String ident, Object... arguments) {
        super(e, ident, arguments);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error � afficher
     */
    public LoggerException(Throwable e, String ident) {
        super(e, ident);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     */
    public LoggerException(Throwable e) {
        super(e);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error � afficher
     * @param arguments les arguments du message d'erreur.
     */
    public LoggerException(String ident, Object... arguments) {
        super(ident, arguments);
    }

    /**
     * Constructeur
     * 
     * @param ident l'identifiant de l'error � afficher
     */
    public LoggerException(String ident) {
        super(ident);
    }

    /**
     * Affiche sur le logger tous les messages
     * <p>
     * 
     * @see SimpleException.exceptions.ExceptionSimple#printMessages()
     */
    @Override
    public void printMessages() {
        if (logger == null) super.printMessages();

        if (logger.isDebugEnabled()) {
            printStackTrace();
        }
        else {
            for (String message : getMessages()) {
                logger.error(message, "");
            }
        }
    }

}
