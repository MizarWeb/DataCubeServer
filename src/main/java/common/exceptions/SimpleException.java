/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package common.exceptions;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Exception standard renvoyée à l'utilisateur.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0
 */
public class SimpleException extends Exception {
    /**
     * 
     */

    private static final long serialVersionUID = 1L;

    // Mémorise l'identifant du message d'erreur
    private String identMessage = null;
    // Mémorise le message
    private static ArrayList<String> messages = new ArrayList<String>();;

    /**
     * Constructeur
     */
    public SimpleException() {
        this(null, "exception.main");
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     */

    public SimpleException(Throwable e) {
        this(e, "exception.main");
    }

    /**
     * Constructeur
     * 
     * @param ident l'identifiant de l'error à afficher
     */
    public SimpleException(String ident) {
        this(null, ident);
    }

    /**
     * Constructeur
     * 
     * @param ident l'identifiant du message à afficher
     * @param arguments les arguments du message d'erreur.
     */
    public SimpleException(String ident, Object... arguments) {
        this(null, ident, arguments);
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error à afficher
     */
    public SimpleException(Throwable e, String ident) {
        this(e, ident, "");
    }

    /**
     * Constructeur
     * 
     * @param e La cause de l'exception
     * @param ident l'identifiant de l'error à afficher
     * @param arguments les arguments du message d'erreur.
     */
    public SimpleException(Throwable e, String ident, Object... arguments) {
        super(Messages.getMessage(ident, arguments));
        // créé le message détail
        this.initCause(e);
        identMessage = ident;
    }

    /**
     * Archive les messages détails de l'exception SQL et de ses causes.
     * <p>
     */
    private void getAllMessages(SQLException e) {
        // Affiche toutes les exceptions
        messages.add(e.getMessage());

        while ((e = e.getNextException()) != null) {
            messages.add(e.getMessage());
        }
    }

    /**
     * archive les messages détails de l'exception et de ses causes.
     * <p>
     * 
     * @return List of messages.
     */
    private void getAllMessages() {

        messages.add(this.getMessage());

        // récupère toutes les causes
        Throwable cause = this;
        if ((cause = cause.getCause()) != null) {
            if (cause instanceof SQLException) {
                getAllMessages((SQLException) cause);
            }
            else {
                try {
                    ((SimpleException) cause).getAllMessages();
                }
                catch (ClassCastException cce) {
                    String detail = cause.getMessage();
                    if (detail == null)
                        messages.add(cause.toString());
                    else messages.add(cause.toString());
                }
            }
        }
    }

    /**
     * Retourne les messages détails de l'exception et de ses causes.
     * <p>
     */
    public final ArrayList<String> getMessages() {
        messages.clear();
        getAllMessages();
        return messages;
    }

    /**
     * Affiche sur la sortie standard tous les messages.
     * <p>
     */
    public void printMessages() {
        for (String message : getMessages()) {
            System.out.println(message);
        }
    }

    /**
     * Retourne l'identifiant du message d'erreur.
     * <p>
     * 
     * @return l'identifiant ou null.
     */
    public final String getIdentMessage() {
        return identMessage;
    }
}
