/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.mizar;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import app.CubeExplorer;
import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;

/**
 *
 */
public class MizarCube extends AbstractDataCube {

    private String filename = null; // filename
    private URL url = null;

    /**
     * @param filename
     * @throws CubeExplorerException
     */
    public MizarCube(CubeExplorer ce, String filename) throws CubeExplorerException {
        super(ce, CubeType.MIZAR);

        logger.trace("NEW MizarCube({})", filename);

        this.filename = filename;

        // Lecture du fichier
        try {
            this.url = new URL("http://demonstrator.telespazio.com/wmspub");
        }
        catch (MalformedURLException mue) {
        	logger.error("MalformedURLException {}", mue.getMessage());
            throw new CubeExplorerException(mue);
        }

        // Get header
        this.header = new MizarHeader(ce, this);
    }

    /**
     * @return the filename
     */
    public String getMizarFile() {
        return filename;
    }

    /**
     * @return mizar
     */
    public URL getMizar() {
        return url;
    }

    public JSONObject getHeader(String pattern) throws CubeExplorerException {
        JSONObject properties = new JSONObject();
        properties.put("fileType", getType().toString());

        properties.put("url", this.url);
        return properties;
    }

    public JSONObject getSlide(int posZ, String pattern) throws CubeExplorerException {
        return getHeader(pattern);
    }

    public JSONObject getSpectrum(int posX, int posY, String pattern) throws CubeExplorerException {
        return getHeader(pattern);
    }

    public void close() {
    }

    @Override
    public void finalize() {
        this.close();
    }
}
