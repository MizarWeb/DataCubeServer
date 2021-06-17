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
            
//			// Dummy 
//            JSONObject location = new JSONObject();
//            location.put("name", "m31");
//            location.put("constellation", "andromeda");
//            JSONObject object_type = new JSONObject();
//            object_type.put("class", "star");
//            
//            properties.put("location", location);
//            properties.put("object_type", object_type);
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

            logger.info("indexHeader with NAXIS3 {}", indexHeader);
            JSONArray md = getHeader().getMetadata().getJSONArray(indexHeader);

            // search axis
            String naxis1Value = getHeader().getValue(md, "NAXIS1");
            String naxis2Value = getHeader().getValue(md, "NAXIS2");
            String naxis3Value = getHeader().getValue(md, "NAXIS3");
            logger.info("naxis3Value {}",naxis3Value);
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
            logger.info("indexHeader {}",indexHeader);
            logger.info("fits.getHDU(indexHeader).getData() {}",fits.getHDU(indexHeader).getData());
            double[][][] cubeFits = null;
            float[][][] cubeFitsFloat = null;
            try {
            	cubeFitsFloat = (float[][][]) fits.getHDU(indexHeader).getData().getData();
            }catch(Exception e) {
            	 cubeFits = ((double[][][]) fits.getHDU(indexHeader).getData().getData());
            }
           
            JSONArray tabValues = new JSONArray();           

            Double value;
            float valueFloat;
            //JSONArray tabValues = new JSONArray();
            logger.info("naxis1 - naxis2 {} {}",naxis1, naxis2);
            for (int idxNaxis2 = 0; idxNaxis2 < naxis2; idxNaxis2++) {
                JSONArray lineValues = new JSONArray();
                for (int idxNaxis1 = 0; idxNaxis1 < naxis1; idxNaxis1++) {
                	 if(cubeFitsFloat != null) {
                         valueFloat = cubeFitsFloat[posZ][idxNaxis2][idxNaxis1];
                         lineValues.put(valueFloat);

                	 }else if(cubeFits != null) {
                         value = cubeFits[posZ][idxNaxis2][idxNaxis1];
                         lineValues.put(value.isNaN() ? null : value);
                	 }
                    
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
            double[][][] cubeFits = null;
            float[][][] cubeFitsFloat = null;
            try {
            	cubeFitsFloat = (float[][][]) fits.getHDU(indexHeader).getData().getData();
            }catch(Exception e) {
            	 cubeFits = ((double[][][]) fits.getHDU(indexHeader).getData().getData());
            }
            Double valuedouble;
            Float valuefloat;
            JSONArray waveslength = new JSONArray();
            JSONArray values = new JSONArray();
      
            String crval3_str = getHeader().getValue(md, "CRVAL3");
            String cdelt3_str = getHeader().getValue(md, "CDELT3");
            String crpix3_str = getHeader().getValue(md, "CRPIX3");
            float crval3 = 0;
	        float cdelt3 = 1;
	        float crpix3 = 0;
            if (crval3_str != null && cdelt3_str != null) {
                crval3 = Float.parseFloat(crval3_str);
    	        cdelt3 = Float.parseFloat(cdelt3_str);
    	        crpix3 = Float.parseFloat(crpix3_str);
            }
            for (int i = 0; i < naxis3; i++) {
            	if(cubeFits != null) {
                	valuedouble = cubeFits[i][posY][posX];
                	values.put(valuedouble.isNaN() ? null : valuedouble);
            	}else if(cubeFitsFloat != null) {
            		valuefloat = cubeFitsFloat[i][posY][posX];
                	values.put(valuefloat.isNaN() ? null : valuefloat);
            	}
            	waveslength.put((float)  crval3 + ((i - crpix3) * cdelt3));
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
