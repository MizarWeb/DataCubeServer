/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package app;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.NotFoundException;

import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import common.exceptions.LoggerException;
import common.exceptions.Messages;
import common.exceptions.SimpleException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import fr.cnes.cubeExplorer.resources.GeoJsonResponse;
import fr.cnes.cubeExplorer.resources.fits.FitsCube;

/**
 * Explorateur de cube de données à partir de fichiers fits.
 * <p>
 * <br>
 * History: 1.0 creation <br>
 * <br>
 * 
 * @author Vincent Cephirins
 * @version 1.0
 */
public class CubeExplorer {

	// Initialise un logger (voir conf/log4j2.xml).
	private static final Logger LOGGER = LogManager.getLogger("cubeExplorer");

	static private Properties properties = null;
	private AbstractDataCube cube = null;

	/**
	 * Constructeur
	 * 
	 * @param type
	 *            Type du fichier [FITS]
	 * @param pFitsFile
	 *            fichier source
	 * @throws DataCubeException
	 *             si une erreur intervient.
	 */
	public CubeExplorer(CubeType type, File pFitsFile) throws LoggerException {
		FitsCube fitsCube = new FitsCube(this, pFitsFile);
		this.cube = fitsCube;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * @return the cube
	 */
	public AbstractDataCube getCube() {
		return cube;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "cube [" + cube + "]";
	}

	public void close() {
		cube.close();
	}

	static public String getProperty(String key) throws CubeExplorerException {
		return getProperty(key, null);
	}

	static public String getProperty(String key, String defaultValue) throws CubeExplorerException {
		String value = null;

		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(CubeExplorer.class.getClassLoader().getResourceAsStream("conf/cubeExplorer.properties"));
			} catch (Exception e) {
				properties = null;
				throw new CubeExplorerException(e, "exception.unavailableResource", "conf/cubeExplorer.properties");
			}
		}

		value = properties.getProperty(key);
		if (value == null) {
			if (defaultValue == null) {
				throw new NotFoundException("property " + key);
			}
			else value = defaultValue;
		}
		return value;
	}

	/**
	 * TITRE.
	 * <p>
	 * Description<br>
	 * <br>
	 * 
	 * @param args
	 *            Pour avoir une description des param�tres, lanc� la comande : <br>
	 *            java -jar DataCube.jar -h<br>
	 * @throws SimpleException
	 * @throws DataCubeException
	 *             Retourne la syntaxe de l'api
	 */
	public static void main(String[] args) throws SimpleException {
		int nbMandatoryArgs = 1;
		int indMand = 0;
		String[] mandatoryArgs = new String[nbMandatoryArgs];

		// Level par défaut
		Level logLevel = Level.getLevel("INFO");

		String entry = null;
		CubeType cubeType = CubeType.FITS;

		try {
			// Chargement des messages applicatifs
			Locale lang = new Locale(CubeExplorer.getProperty("lang"));
			Messages.load("conf/messages", lang);

			for (int indArg = 0; indArg < args.length; indArg++) {
				if (args[indArg].equals("-h") || args[indArg].equals("--help")) {
					throw new SimpleException("exception.app.syntax");
				}

				if (args[indArg].equals("-l") || args[indArg].equals("--level")) {
					entry = args[++indArg].toUpperCase();
					logLevel = Level.getLevel(entry);
					LOGGER.info("Level " + entry);
					Configurator.setAllLevels(LogManager.getRootLogger().getName(), logLevel);
					if (logLevel == null) {
						throw new SimpleException("exception.app.syntax");
					}

					continue;
				}

				if (args[indArg].equals("-f") || args[indArg].equals("--fits")) {
					entry = args[++indArg];
					LOGGER.info("Fits file : " + entry);
					indMand++;
					cubeType = CubeType.FITS;
					continue;
				}

				// Arguments obligatoires
				if (indMand < nbMandatoryArgs) {
					if (args[indArg].startsWith("-")) {
						throw new SimpleException("exception.app.syntax");
					}
					mandatoryArgs[indMand] = args[indArg];
				}

				indMand++;
			}

			if (indMand != nbMandatoryArgs) {
				throw new SimpleException("exception.app.syntax");
			}

			// Appel de l'application
			FitsCube fc = (FitsCube) new CubeExplorer(cubeType, new File(entry)).getCube();

			// toutes les metadata
			JSONArray metadata = fc.getHeader().getMetadata();
			LOGGER.trace(metadata.toString());

			// Recherche specific de metadata sur le hdu 1
			JSONArray hduMetadata = fc.getHeader().getMetadata().optJSONArray(1);
			String keyPattern = "EXTNAME|NAXIS3|CRVAL3|CDELT3|CTYPE3";
			LOGGER.trace(fc.getHeader().getMetadata(hduMetadata, keyPattern).toString());

			// Recherche specific de metadata sur le hdu contenant l'image
			keyPattern = "EXTNAME|BITPIX|NAXIS.$|CRPIX3$|CRVAL3$|CDELT3$|CTYP3$";
			LOGGER.trace(fc.getHeader().getMetadata(hduMetadata, keyPattern).toString());

			// Recherche d'une image
			keyPattern = "^NAXIS.$|^CDELT.$|^CTYP3$";
			JSONObject slide = fc.getSlide(1, 10, keyPattern, true);
			LOGGER.trace(slide);
			GeoJsonResponse geoJsonSlide = new GeoJsonResponse(1, 10, slide);

			File fos = new File(
					"D:\\temp\\cubeExplorer\\" + fc.getType() + "_slide_" + fc.getFitsFile().getName() + ".json");
			FileOutputStream out = new FileOutputStream(fos);
			out.write(geoJsonSlide.getGeoJson().toString(2).getBytes());
			out.close();

			// Recherche d'un spectre
			keyPattern = "NAXIS3";
			JSONObject spectre = fc.getSpectrum(1, 15, 15, keyPattern);
			LOGGER.trace(spectre);
			GeoJsonResponse geoJsonSpectre = new GeoJsonResponse(15, 15, spectre);

			fos = new File(
					"D:\\temp\\cubeExplorer\\" + fc.getType() + "_spectre_" + fc.getFitsFile().getName() + ".json");
			out = new FileOutputStream(fos);
			out.write(geoJsonSpectre.getGeoJson().toString(2).getBytes());
			out.close();
		} catch (SimpleException se) {
			if (logLevel.isInRange(Level.DEBUG, Level.TRACE)) {
				// Afficher la pile complète si le mode trace ou debug est actif
				throw se;
			} else {
				// N'afficher que les principaux messages
				se.printMessages();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
