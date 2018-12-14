/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 ******************************************************************************/
package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.NotFoundException;

import common.exceptions.CubeExplorerException;
import common.exceptions.LoggerException;
import common.exceptions.Messages;
import common.exceptions.SimpleException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import fr.cnes.cubeExplorer.resources.GeoJsonResponse;
import fr.cnes.cubeExplorer.resources.fits.FitsCube;
import fr.cnes.cubeExplorer.resources.mizar.MizarCube;
import fr.cnes.cubeExplorer.resources.netcdf.NetcdfCube;

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
     * @param filename fichier source
     * @throws DataCubeException si une erreur intervient.
     */
    public CubeExplorer(String filename) throws LoggerException {
        // Identification du type du fichier
        if (filename.endsWith(".fits")) {
            this.cube = new FitsCube(this, filename);
        }
        else if (filename.endsWith(".nc")) {
            this.cube = new NetcdfCube(this, filename);
        }
        else if (filename.endsWith("testMizar")) {
            this.cube = new MizarCube(this, filename);
        }
        else {
        	LOGGER.error("exception.file.unknown {}", filename);
            throw new CubeExplorerException("exception.file.unknown", filename);
        }
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
            InputStream input = null;
            OutputStream output = null;
            String messageFile = null;

            try {
                properties = new Properties();

                // Récupère le path de l'application
                URI classPath = CubeExplorer.class.getProtectionDomain().getCodeSource().getLocation().toURI();

                // Extrait le fichier de conf par défaut si celui-ci n'existe pas
                File filePath = new File(classPath.getPath(), "cubeExplorer.properties");

                if (!filePath.exists()) {
                    // Get de la ressource
                    messageFile = "conf/cubeExplorer.properties";
                    input = CubeExplorer.class.getClassLoader().getResourceAsStream(messageFile);

                    // Copie du fichier en local
                    messageFile = filePath.getAbsolutePath();
                    output = new FileOutputStream(filePath);

                    // On utilise une lecture bufférisé :
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = input.read(buf)) > 0) {
                        output.write(buf, 0, len);
                    }
                }

                messageFile = filePath.getAbsolutePath();
                properties.load(new FileInputStream(filePath));
            }
            catch (Exception e) {
                properties = null;
                LOGGER.error("exception.unavailableResource {}", messageFile);
                throw new CubeExplorerException(e, "exception.unavailableResource", messageFile);
            }
            finally {
                try {
                    if (input != null) input.close();
                    if (output != null) {
                        output.close();
                        output = null;
                    }
                }
                catch (Exception e) {
                    LOGGER.error("exception.libre {}", e.getMessage());
                    throw new CubeExplorerException(e, "exception.libre");
                }
            }
        }

        value = properties.getProperty(key);
        if (value == null) {
            if (defaultValue == null) {
            	LOGGER.error("property not found {}", key);
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
     * @param args Pour avoir une description des param�tres, lanc� la comande : <br>
     *            java -jar DataCube.jar -h<br>
     * @throws SimpleException
     * @throws DataCubeException Retourne la syntaxe de l'api
     */
    public static void main(String[] args) throws SimpleException {
        int nbMandatoryArgs = 1;
        int indMand = 0;
        String[] mandatoryArgs = new String[nbMandatoryArgs];

        // Level par défaut
        Level logLevel = Level.getLevel("INFO");

        String entry = null;

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
                    	LOGGER.error("exception.app.syntax : logLevel is NULL");
                        throw new SimpleException("exception.app.syntax");
                    }

                    continue;
                }

                if (args[indArg].equals("-f") || args[indArg].equals("--file")) {
                    entry = args[++indArg];
                    LOGGER.info("File : " + entry);
                    indMand++;
                    continue;
                }

                // Arguments obligatoires
                if (indMand < nbMandatoryArgs) {
                    if (args[indArg].startsWith("-")) {
                    	LOGGER.error("exception.app.syntax");
                        throw new SimpleException("exception.app.syntax");
                    }
                    mandatoryArgs[indMand] = args[indArg];
                }

                indMand++;
            }

            if (indMand != nbMandatoryArgs) {
            	LOGGER.error("exception.app.syntax");
                throw new SimpleException("exception.app.syntax");
            }

            // Appel de l'application
            FitsCube fc = (FitsCube) new CubeExplorer(entry).getCube();

            // toutes les metadata
            JSONArray metadata = fc.getHeader().getMetadata();
            LOGGER.debug(metadata.toString());

            // Recherche specific de metadata sur le hdu 1
            JSONArray hduMetadata = fc.getHeader().getMetadata().optJSONArray(1);
            String keyPattern = "EXTNAME|NAXIS3|CRVAL3|CDELT3|CTYPE3";
            LOGGER.debug(fc.getHeader().getMetadata(hduMetadata, keyPattern).toString());

            // Recherche specific de metadata sur le hdu contenant l'image
            keyPattern = "EXTNAME|BITPIX|NAXIS.$|CRPIX3$|CRVAL3$|CDELT3$|CTYP3$";
            LOGGER.debug(fc.getHeader().getMetadata(hduMetadata, keyPattern).toString());

            // Recherche d'une image
            keyPattern = "^NAXIS.$|^CDELT.$|^CTYP3$";
            JSONObject slide = fc.getSlide(10, keyPattern);
            LOGGER.debug(slide);
            GeoJsonResponse geoJsonSlide = new GeoJsonResponse(1, 10, slide);
            
            String workspace = CubeExplorer.getProperty("workspace_cube", null);
            File fos = new File(workspace + fc.getType() + "_slide_" + fc.getFitsFile() + ".json");
            FileOutputStream out = new FileOutputStream(fos);
            out.write(geoJsonSlide.getGeoJson().toString(2).getBytes());
            out.close();

            // Recherche d'un spectre
            keyPattern = "NAXIS3";
            JSONObject spectre = fc.getSpectrum(15, 15, keyPattern);
            GeoJsonResponse geoJsonSpectre = new GeoJsonResponse(15, 15, spectre);

            fos = new File(workspace + fc.getType() + "_spectre_" + fc.getFitsFile() + ".json");
            out = new FileOutputStream(fos);
            out.write(geoJsonSpectre.getGeoJson().toString(2).getBytes());
            out.close();
        }
        catch (SimpleException se) {
            if (logLevel.isInRange(Level.DEBUG, Level.TRACE)) {
            	LOGGER.error(se.getMessage());
                // Afficher la pile complète si le mode trace ou debug est actif
                throw se;
            }
            else {
                // N'afficher que les principaux messages
                se.printMessages();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
