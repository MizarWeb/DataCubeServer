/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.netcdf;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import app.CubeExplorer;
import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import ucar.nc2.NetcdfFile;

/**
 *
 */
public class NetcdfCube extends AbstractDataCube {

	private String filename = null;
	private NetcdfFile ncfile = null;

	/**
	 * @param filename
	 * @throws CubeExplorerException
	 */
	public NetcdfCube(CubeExplorer ce, String filename) throws CubeExplorerException {
		super(ce, CubeType.NETCDF);
		index = 0;

		logger.trace("NEW NetcdfCube({})", filename);

		this.filename = filename;

		// Lecture du fichier netcdf
		this.ncfile = readNetcdf(filename);

		// Get header
		this.header = new NetcdfHeader(ce, this);
	}

	/**
	 * @return the netcdfFile
	 */
	public String getFilemane() {
		return filename;
	}

	/**
	 * @return the netcdf
	 */
	public NetcdfFile getNcfile() {
		return ncfile;
	}

	private NetcdfFile readNetcdf(String filename) throws CubeExplorerException {
		NetcdfFile ncfile = null;

		logger.trace("ENTER readNetcdf({})", filename);

		try {
			ncfile = NetcdfFile.open(filename);
		} catch (IOException ioe) {
			// free resources
			close();
			throw new CubeExplorerException(ioe);
		}

		return ncfile;
	}

	public JSONObject getSlide(int indexHdu, int pNaxis3, String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject slide = new JSONObject();

		logger.trace("ENTER getSlide({}, {})", pNaxis3, pattern);

		try {
			slide.put("value", "test");
			properties.put("metadata", metadata);
			properties.put("slide", slide);

		} catch (Exception exc) {
			throw new CubeExplorerException(exc);
		}
		return properties;
	}

	public JSONObject getSpectrum(int indexHdu, int pNaxis1, int pNaxis2, String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject spectrum = new JSONObject();

		logger.trace("ENTER getSpectrum({}, {}, {})", pNaxis1, pNaxis2, pattern);

		// Lecture des données du fichier netcdf
		try {
			spectrum.put("wavelength", "test");
			spectrum.put("value", "test");
			properties.put("metadata", metadata);
			properties.put("spectrum", spectrum);

		} catch (Exception ioe) {
			throw new CubeExplorerException(ioe);
		}

		return properties;
	}

	@Override
	public void close() {
		try {
			if (this.ncfile != null) {
				this.ncfile.close();
				logger.trace("Resource netcdf " + this.ncfile.getLocation() + " closed.");
				this.ncfile = null;
			}
		} catch (IOException ioe) {
			// Erreur non bloquante
			new CubeExplorerException(ioe).printMessages();
		}
	}
	
	@Override
	public void finalize() {
		this.close();
	}
}
