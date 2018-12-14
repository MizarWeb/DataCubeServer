/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.fits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import app.CubeExplorer;
import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.TableHDU;

/**
 *
 */
public class FitsCube extends AbstractDataCube {

    private String filename = null; // filename
    private Fits fits = null;

    /**
     * @param filename
     * @throws CubeExplorerException
     */
    public FitsCube(CubeExplorer ce, String filename) throws CubeExplorerException {
        super(ce, CubeType.FITS);

        logger.info("NEW FitsCube({})", filename);

        this.filename = filename;

        // Lecture du fichier fits
        this.fits = readFits(filename);

        // Get header
        this.header = new FitsHeader(ce, this);
    }

    /**
     * @return the filename
     */
    public String getFitsFile() {
        return filename;
    }

    /**
     * @return the fits
     */
    public Fits getFits() {
        return fits;
    }

    private Fits readFits(String filename) throws CubeExplorerException {
        Fits fits = null;

        logger.info("ENTER readFits({})", filename);

        try {
            // get the file path
            fits = new Fits(filename);
            FitsFactory.setUseHierarch(true);

            // read fits file
            fits.read();

        }
        catch (FitsException fe) {
            // free resources
            close();
            throw new CubeExplorerException(fe);
        }

        return fits;
    }

    public JSONObject getHeader(String pattern) throws CubeExplorerException {
        JSONObject properties = new JSONObject();
        properties.put("fileType", getType().toString());

        int indexHeader = ((FitsHeader) getHeader()).getIndexHeader();

        JSONArray md = getHeader().getMetadata().getJSONArray(indexHeader);
        logger.info("ENTER metadata({})", md);

        // Récupération des dimensions
        properties.put("dimensions", getHeader().getDimensions());

        if (pattern != null) {
            // Sélection des metadata
            properties.put("metadata", getHeader().getMetadata(md, pattern));
        }
        else {
            // toutes les metadata
            properties.put("metadata", getHeader().getMetadata(md));
            
            //TODO bouchon description
            JSONObject localisation = new JSONObject();
            localisation.put("name", "m31");
            localisation.put("constellation", "andromeda");
            JSONObject object_type = new JSONObject();
            object_type.put("class", "star");
            
            properties.put("localisation", localisation);
            properties.put("object_type", object_type);
        }
        return properties;
    }

    public JSONObject getSlide(int posZ, String pattern) throws CubeExplorerException {
        JSONObject properties = new JSONObject();
        JSONArray metadata = new JSONArray();
        JSONObject slide = new JSONObject();

        logger.trace("ENTER getSlide({}, {})", posZ, pattern);

        try {
            int indexHeader = ((FitsHeader) getHeader()).getIndexHeader();

            if (indexHeader < 0 || indexHeader >= fits.getNumberOfHDUs()) {
            	logger.error("exception.outOfBound : indexHeader {} , NumberOfHDUs {}", indexHeader, fits.getNumberOfHDUs());
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHeader, 0,
                    fits.getNumberOfHDUs() - 1);
            }

            JSONArray md = getHeader().getMetadata().getJSONArray(indexHeader);

            // search axis
            String naxis1Value = getHeader().getValue(md, "NAXIS1");
            String naxis2Value = getHeader().getValue(md, "NAXIS2");
            String naxis3Value = getHeader().getValue(md, "NAXIS3");

            int naxis1 = (naxis1Value == null) ? 0 : Integer.parseInt(naxis1Value);
            int naxis2 = (naxis2Value == null) ? 0 : Integer.parseInt(naxis2Value);
            int naxis3 = (naxis3Value == null) ? 0 : Integer.parseInt(naxis3Value);

            if (posZ < 0 || posZ >= naxis3) {
            	logger.error("exception.outOfBound : posZ {} , naxis3 {}", posZ, naxis3);
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "posZ", posZ, 0, naxis3 - 1);
            }

            // Copy metadata without comment
            metadata = getHeader().selectMetadata(md, pattern);

            double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHeader).getData().getData());

            Double value;
            JSONArray tabValues = new JSONArray();
            for (int idxNaxis2 = 0; idxNaxis2 < naxis2; idxNaxis2++) {
                JSONArray lineValues = new JSONArray();
                for (int idxNaxis1 = 0; idxNaxis1 < naxis1; idxNaxis1++) {
                    value = cubeFits[posZ][idxNaxis2][idxNaxis1];
                    lineValues.put(value.isNaN() ? null : value);
                }
                tabValues.put(lineValues);
            }

            // Store data to json
            slide.put("value", tabValues);
            properties.put("metadata", metadata);
            properties.put("slide", slide);

        }
        catch (CubeExplorerException ce) {
            throw ce;
        }
        catch (FitsException fe) {
        	logger.error("CubeExplorerException : {} ", fe.getMessage());
            throw new CubeExplorerException(fe);
        }
        catch (IOException ioe) {
        	logger.error("IOException : {} ", ioe.getMessage());
            throw new CubeExplorerException(ioe);
        }
        return properties;
    }

    public JSONObject getSpectrum(int posX, int posY, String pattern) throws CubeExplorerException {
        JSONObject properties = new JSONObject();
        JSONArray metadata = new JSONArray();
        JSONObject spectrum = new JSONObject();

        logger.info("ENTER getSpectrum({}, {}, {})", posX, posY, pattern);

        // Lecture des données du fichier fits
        try {
            int indexHeader = ((FitsHeader) getHeader()).getIndexHeader();

            if (indexHeader < 0 || indexHeader >= fits.getNumberOfHDUs()) {
            	logger.error("exception.outOfBound : indexHeader {} , NumberOfHDUs {}", indexHeader, fits.getNumberOfHDUs());
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHeader, 0,
                    fits.getNumberOfHDUs() - 1);
            }

            JSONArray md = getHeader().getMetadata().getJSONArray(indexHeader);

            // Recherche des axes
            String naxis1Value = getHeader().getValue(md, "NAXIS1");
            String naxis2Value = getHeader().getValue(md, "NAXIS2");
            String naxis3Value = getHeader().getValue(md, "NAXIS3");

            int naxis1 = (naxis1Value == null) ? 0 : Integer.parseInt(naxis1Value);
            int naxis2 = (naxis2Value == null) ? 0 : Integer.parseInt(naxis2Value);
            int naxis3 = (naxis3Value == null) ? 0 : Integer.parseInt(naxis3Value);

            if (posX < 0 || posX >= naxis1) {
            	logger.error("exception.outOfBound : posX {} , naxis1 {}", posX, naxis1);
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "posX", posX, 0, naxis1 - 1);
            }

            if (posY < 0 || posY >= naxis2) {
            	logger.error("exception.outOfBound : posY {} , naxis2 {}", posY, naxis2);
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "posY", posY, 0, naxis2 - 1);
            }

            // Copie des metadata demandées sans les commentaires
            metadata = getHeader().selectMetadata(md, pattern);

            double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHeader).getData().getData());

            Double value;
            JSONArray waveslength = new JSONArray();
            JSONArray values = new JSONArray();
            for (int idxNaxis3 = 0; idxNaxis3 < naxis3; idxNaxis3++) {
                value = cubeFits[idxNaxis3][posY][posX];
                values.put(value.isNaN() ? null : value);
                waveslength.put((double) idxNaxis3);
            }
            spectrum.put("wavelength", waveslength);
            spectrum.put("value", values);
            properties.put("metadata", metadata);
            properties.put("spectrum", spectrum);

        }
        catch (CubeExplorerException ce) {
            throw ce;
        }
        catch (FitsException fe) {
        	logger.error("FitsException : fe {}", fe.getMessage());
            throw new CubeExplorerException(fe);
        }
        catch (IOException ioe) {
        	logger.error("IOException : ioe {}", ioe.getMessage());
            throw new CubeExplorerException(ioe);
        }

        return properties;
    }

    // A SUPPRIMER
    private JSONArray readFitsDataSpectro(int indexHdu) throws CubeExplorerException {
        JSONArray data = new JSONArray();

        try {
            int nbrHdu = fits.getNumberOfHDUs();

            if (indexHdu < 0 || indexHdu >= nbrHdu) {
            	logger.error("exception.outOfBound : Hdu {} , nbrHdu {}", indexHdu, nbrHdu);
                // OutOfBound
                throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHdu, 0, nbrHdu - 1);
            }

            int indexImage = getHeader().getIndexImage();

            Header header = fits.getHDU(indexHdu).getHeader();

            logger.info("Getting Wave...");
            int hhdduu = -1;
            for (int i = 0; i < nbrHdu; i++) {
                String a = fits.getHDU(i).getHeader().findKey("CDELT3");
                if (a != null) {
                    logger.trace("CDELT3 find in " + i);
                    hhdduu = i;
                    break;
                }
            }
            if (hhdduu == -1) {
                logger.trace("CDELT3 not found in WCS cube");
            }

            // Wave from fits
            // if (fits.getHDU(hdu).getHeader().findKey("CDELT3")!=null) {
            float[] wave;
            if (hhdduu != -1) {
                logger.info("data from WCS cube");

                int naxis3 = header.getIntValue("NAXIS3");
                float crval3 = header.getFloatValue("CRVAL3");
                float cdelt3 = header.getFloatValue("CDELT3");
                logger.info(naxis3 + " " + crval3 + " " + cdelt3);
                wave = new float[naxis3];
                for (int i = 0; i < naxis3; i++) {
                    wave[i] = crval3 + (i * cdelt3);
                }
            }
            else {
                logger.info("No wave data in the cube WCS");
                if (indexImage != -1) {
                    logger.info("Using wave array from ImageIndex HDU table");
                    TableHDU<?> cols = (TableHDU<?>) fits.getHDU(indexImage);
                    double[] waveD = (double[]) cols.getColumn(0);
                    wave = new float[waveD.length];
                    for (int i = 0; i < waveD.length; i++) {
                        wave[i] = (float) waveD[i];
                    }
                }
                else {
                	logger.error("exception.fits.WaveNotFound");
                    throw new CubeExplorerException("exception.fits.WaveNotFound");
                }
            }
            JSONObject jsonSpectrum = new JSONObject();
            jsonSpectrum.put("WAVE", wave);
            logger.info(" Wave - Done !");

            logger.info("Getting Cube Data...");
            // Spectrum from fits
            for (int m = 1; m <= 2; m++) {
                jsonSpectrum.put("NAXIS" + m, header.getDoubleValue("NAXIS" + m));
                jsonSpectrum.put("CRPIX" + m, header.getDoubleValue("CRPIX" + m));
                jsonSpectrum.put("CRVAL" + m, header.getDoubleValue("CRVAL" + m));
                jsonSpectrum.put("CDELT" + m, header.getDoubleValue("CDELT" + m));
                jsonSpectrum.put("CTYPE" + m, header.getStringValue("CTYPE" + m));
            }

            jsonSpectrum.put("NAXIS3", header.getDoubleValue("NAXIS3"));

            jsonSpectrum.put("INFO_QTTY", header.getStringValue("INFO____"));
            jsonSpectrum.put("UNIT_QTTY", header.getStringValue("QTTY____"));
            jsonSpectrum.put("INFO_WAVE", header.getStringValue("CTYPE3"));
            jsonSpectrum.put("UNIT_WAVE", header.getStringValue("CUNIT3"));

            double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHdu).getData().getData());
            int naxis1 = cubeFits[0][0].length;
            int naxis2 = cubeFits[0].length;
            int naxis3 = cubeFits.length;

            // Reorganizing cube
            List<List<List<Float>>> cube3DL = new ArrayList<List<List<Float>>>(naxis1);
            // float cube[][][] = new float[naxis1][naxis2][naxis3];
            for (int x = 0; x < naxis1; x++) {
                List<List<Float>> list2 = new ArrayList<List<Float>>(naxis2);
                for (int y = 0; y < naxis2; y++) {
                    List<Float> list3 = new ArrayList<Float>(naxis3);
                    for (int z = 0; z < naxis3; z++) {
                        // cube[x][y][z] = (float) cubeFits[z][y][x];
                        if (cubeFits[z][y][x] != Double.NaN) {
                            list3.add((float) cubeFits[z][y][x]);
                        }
                        else {
                            list3.add(null);
                        }
                    }
                    list2.add(list3);
                }
                cube3DL.add(list2);
            }

            JSONArray cubeJL = new JSONArray(cube3DL.toString());
            data.put(cubeJL);

            logger.trace("Header done !");
        }
        catch (FitsException fe) {
        	logger.error("FitsException : fe {}", fe.getMessage());
            throw new CubeExplorerException(fe);
        }
        catch (IOException ioe) {
        	logger.error("IOException : ioe {}", ioe.getMessage());
            throw new CubeExplorerException(ioe);
        }
        return data;
    }

    public void close() {
        try {
            if (this.fits != null) {
                this.fits.close();
                logger.trace("Resource fits " + this.filename + " closed.");
                this.fits = null;
            }
        }
        catch (IOException ioe) {
        	logger.error("IOException : ioe {}", ioe.getMessage());
            // Erreur non bloquante
            new CubeExplorerException(ioe).printMessages();
        }
    }

    @Override
    public void finalize() {
        this.close();
    }
}
