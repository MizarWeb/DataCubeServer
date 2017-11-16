/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources;

import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import app.CubeExplorer;
import common.enums.CubeType;
import common.exceptions.CubeExplorerException;

/**
 *
 */
/**
 * @author vincent.cephirins
 *
 */
/**
 * @author vincent.cephirins
 *
 */
public abstract class AbstractDataCube {

	protected Logger logger = null;
	protected CubeExplorer cubeExplorer = null;
	protected CubeType type = null;
	protected AbstractDataCubeHeader header = null;
	protected int index = 0;

	/**
	 * @param ce cube explorer parent
	 * @param type
	 * @param header
	 */
	protected AbstractDataCube(CubeExplorer ce, CubeType type) {
		super();

		this.cubeExplorer = ce;
		this.type = type;
		this.logger = CubeExplorer.getLogger();

		logger.trace("NEW DataCube()");
	}

	/**
	 * @return the cubeExplorer
	 */
	public CubeExplorer getCubeExplorer() {
		return cubeExplorer;
	}

	/**
	 * @return the jsonMetadata
	 */
	public CubeType getType() {
		return type;
	};

	/**
	 * @return the jsonMetadata
	 */
	public AbstractDataCubeHeader getHeader() {
		return header;
	};

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	abstract public void close();
	
	/**
	 * Return a slide from datacube with metadata and rad/dec coordinates
	 * 
	 * @param posZ
	 * @param pattern Select metadata to return
	 * @return GEOJson slide
	 * @throws CubeExplorerException
	 */
	public JSONObject getSlide(int posZ, String pattern) throws CubeExplorerException {
		return getSlide(0, posZ, pattern);
	};
	
	/**
	 * Return a slide from datacube with metadata and rad/dec coordinates

	 * @param indexHdu
	 * @param posZ
	 * @param pattern Select metadata to return
	 * @return GEOJson slide
	 * @throws CubeExplorerException
	 */
	abstract public JSONObject getSlide(int indexHdu, int posZ, String pattern) throws CubeExplorerException;

	/**
	 * @param posX
	 * @param posY
	 * @param pattern
	 * @return GEOJson spectrum
	 * @throws CubeExplorerException
	 */

	public JSONObject getSpectrum(int posX, int posY, String pattern) throws CubeExplorerException {
		return getSpectrum(0, posX, posY, pattern);
	}
	
	/**
	 * @param indexHdu
	 * @param posX
	 * @param posY
	 * @param pattern
	 * @return GEOJson spectrum
	 * @throws CubeExplorerException
	 */
	abstract public JSONObject getSpectrum(int indexHdu, int posX, int posY, String pattern) throws CubeExplorerException;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + getHeader().toString() + "]";
	}
	
	public String toString(int indent) {
		return "[" + header.toString(indent) + "]";
	}
}
