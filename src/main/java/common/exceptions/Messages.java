/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package common.exceptions;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Gestionnaire des message d'erreurs contenues dans un fichier.
 * <p>
 * 
 * @author Vincent Cephirins
 * @version 1.0
 */
public abstract class Messages {

	/**
	 * Affecte la locale par défaut à celle de l'environnement
	 */
	private static Locale localeDefault = Locale.getDefault();

	/**
	 * Le gestionnaire des messages utilisateur
	 */
	private static ResourceBundle resource = null;
	private static ResourceBundle resourceDefault = null;

	/**
	 * Le gestionnaire des messages internes par défaut
	 */
	private static ResourceBundle resourceInterne = ResourceBundle.getBundle("conf/messagesException", Locale.ENGLISH);
	private static ResourceBundle resourceInterneDefault = ResourceBundle.getBundle("conf/messagesException",
			Locale.ENGLISH);

	/**
	 * Constructeur privé
	 */
	private Messages() {
	}

	/**
	 * Charge en mémoire les messages.
	 * <p>
	 * La langue sélectionnée est la locale par défaut.
	 * 
	 * @param file
	 */
	public static void load(String file) {
		load(file, Locale.getDefault());
	}

	/**
	 * Charge en mémoire les messages.
	 * <p>
	 * La langue sélectionnée est la locale fournie.
	 * 
	 * @param basenameBundle
	 * @param locale
	 * @throws NullPointerException
	 *             si le nom de base ou la locale est null
	 * @throws MissingResourceException
	 *             si la resource basée sur nom du bundle est non trouvé
	 */
	public static void load(String basenameBundle, Locale locale) {
		if (resource == null) {
			try {
				resource = ResourceBundle.getBundle(basenameBundle, locale);
			} catch (MissingResourceException mre) {
				resourceDefault = ResourceBundle.getBundle(basenameBundle, localeDefault);
			}
		}
	}

	/**
	 * Force le rechargement en mémoire des messages.
	 * <p>
	 * 
	 * @throws NullPointerException
	 *             si le nom de base ou la locale est null
	 * @throws MissingResourceException
	 *             si la resource basée sur nom du bundle est non trouvé
	 */
	public static void reload() {
		if (resource != null) {
			ResourceBundle.clearCache();
			load(resource.getBaseBundleName(), resource.getLocale());
		}
	}

	/**
	 * Force le chargement en mémoire des messages.
	 * <p>
	 * La langue sélectionnée est la locale fournie.
	 * 
	 * @param basenameBundle
	 * @throws NullPointerException
	 *             si le nom de base ou la locale est null
	 * @throws MissingResourceException
	 *             si la resource basée sur nom du bundle est non trouvé
	 */
	public static void reload(String basenameBundle) {
		if (resource != null) {
			ResourceBundle.clearCache();
			resource = null;
		}
		load(basenameBundle);
	}

	/**
	 * Force le chargement en mémoire des messages.
	 * <p>
	 * La langue sélectionnée est la locale fournie.
	 * 
	 * @param basenameBundle
	 * @param locale
	 * @throws NullPointerException
	 *             si le nom de base ou la locale est null
	 * @throws MissingResourceException
	 *             si la resource basée sur nom du bundle est non trouvé
	 */
	public static void reload(String basenameBundle, Locale locale) {
		if (resource != null) {
			ResourceBundle.clearCache();
			resource = null;
		}
		load(basenameBundle, locale);
	}

	/**
	 * Récupère le message dans le fichier de configuration
	 * 
	 * @param ident
	 *            le code du message
	 * @param args
	 *            les arguments du message
	 * @return le message format�
	 */
	public static String getMessage(String ident, Object... args) {
		String message = "";

		try {
			// recherche dans les messages applicatifs
			if (resource != null) {
				message = Messages.resource.getString(ident);
			} else if (resourceDefault != null) {
				message = Messages.resourceDefault.getString(ident);
			} else {
				message = "(Resource default for messages file not available) ";
				throw new MissingResourceException(message, "Messages", ident);
			}
		} catch (MissingResourceException mrei) {
			// recherche dans les messages internes
			try {
				if (resourceInterne != null) {
					message = Messages.resourceInterne.getString(ident);
				} else if (resourceInterneDefault != null) {
					message = Messages.resourceInterneDefault.getString(ident);
				} else {
					message = "(Internal resource default for messages file not available) ";
					throw new MissingResourceException(message, "Messages", ident);
				}

			} catch (MissingResourceException e) {
				// Si on ne trouve pas le message
				message = "Message ident ''" + ident + "'' not found in message files.";
			}
		}
		return MessageFormat.format(message, args);
	}
}
