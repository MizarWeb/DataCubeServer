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

	abstract public void close();
	
	/**
	 * Return a slide from datacube with metadata and rad/dec coordinates
	 * 
	 * @param naxis3
	 * @param pattern Select metadata to return
	 * @param radec return rad/dec coordinates if true
	 * @return GEOJson slide
	 * @throws CubeExplorerException
	 */
	public JSONObject getSlide(int naxis3, String pattern, boolean radec) throws CubeExplorerException {
		return getSlide(0, naxis3, pattern, radec);
	};
	
	/**
	 * Return a slide from datacube with metadata and rad/dec coordinates

	 * @param indexHdu
	 * @param naxis3
	 * @param pattern Select metadata to return
	 * @param radec return rad/dec coordinates if true
	 * @return GEOJson slide
	 * @throws CubeExplorerException
	 */
	abstract public JSONObject getSlide(int indexHdu, int naxis3, String pattern, boolean radec) throws CubeExplorerException;

	/**
	 * @param naxis1
	 * @param naxis2
	 * @param pattern
	 * @return GEOJson spectrum
	 * @throws CubeExplorerException
	 */

	public JSONObject getSpectrum(int naxis1, int naxis2, String pattern) throws CubeExplorerException {
		return getSpectrum(0, naxis1, naxis2, pattern);
	}
	
	/**
	 * @param indexHdu
	 * @param naxis1
	 * @param naxis2
	 * @param pattern
	 * @return GEOJson spectrum
	 * @throws CubeExplorerException
	 */
	abstract public JSONObject getSpectrum(int indexHdu, int naxis1, int naxis2, String pattern) throws CubeExplorerException;

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
