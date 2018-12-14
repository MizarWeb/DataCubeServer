/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.mizar;

import app.CubeExplorer;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCubeHeader;

/**
 * @author vincent.cephirins
 *
 */
public class MizarHeader extends AbstractDataCubeHeader {

    private MizarCube cube = null;

    /**
     * construct a header JSONArray from file
     * 
     * @param cube Cube of data
     * @throws CubeExplorerException
     */
    public MizarHeader(CubeExplorer ce, MizarCube cube) throws CubeExplorerException {
        super(ce);

        logger.trace("NEW MizarHeader({})", cube);

        this.cube = cube;
        if (this.cube == null || this.cube.getMizar() == null) {
        	logger.error("exception.file.null");
            throw new CubeExplorerException("exception.file.null");
        }
    }
}
