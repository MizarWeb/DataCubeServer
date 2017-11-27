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
import ucar.ma2.ArrayFloat;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

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

	public JSONObject getHeader(String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		properties.put("fileType", getType().toString());

		JSONArray md = getHeader().getMetadata();

		// Récupération des dimensions
		properties.put("dimensions", getHeader().getDimensions());

		if (pattern != null) {
			// Sélection des metadata
			properties.put("metadata", getHeader().getMetadata(md, pattern));
		} else {
			// toutes les metadata
			properties.put("metadata", getHeader().getMetadata(md));
		}
		return properties;
	}

	public JSONObject getSlide(int posZ, String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject slide = new JSONObject();

		logger.trace("ENTER getSlide({}, {})", posZ, pattern);

		try {
			// Search first variable with 3 dimensions
			Variable cubeVar = null;
			for (Variable variable : ncfile.getVariables()) {
				int[] dims = variable.getShape();
				if (dims.length == 3) {
					cubeVar = variable;
				}
			}

			if (cubeVar == null)
				throw new CubeExplorerException("exception.notFound", "datacube");

			int[] cubeShape = cubeVar.getShape();

			if (posZ < 0 || posZ >= cubeShape[0]) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "posZ", posZ, 0, cubeShape[0] - 1);
			}

			int[] cubeOrigin = new int[3];
			cubeShape[0] = 1; // only one rec per read
			cubeOrigin[0] = posZ; // read this index

			// read 3D array for that index => 2D
			ArrayFloat.D2 slideArray = (ArrayFloat.D2) (cubeVar.read(cubeOrigin, cubeShape).reduce());

			// Copy metadata without comment
			JSONArray md = getHeader().getMetadata();
			metadata = getHeader().selectMetadata(md, pattern);

			Float value;
			JSONArray tabValues = new JSONArray();
			for (int idxPosY = 0; idxPosY < cubeShape[1]; idxPosY++) {
				JSONArray lineValues = new JSONArray();
				for (int idxPosX = 0; idxPosX < cubeShape[2]; idxPosX++) {
					value = slideArray.get(idxPosY, idxPosX);
					lineValues.put(value.isNaN() ? null : value);
				}
				tabValues.put(lineValues);
			}

			// Store data to json
			slide.put("value", tabValues);
			properties.put("metadata", metadata);
			properties.put("slide", slide);

		} catch (CubeExplorerException ce) {
			throw ce;
		} catch (Exception exc) {
			throw new CubeExplorerException(exc);
		}
		return properties;
	}

	public JSONObject getSpectrum(int posX, int posY, String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject spectrum = new JSONObject();

		logger.trace("ENTER getSpectrum({}, {}, {})", posX, posY, pattern);

		try {
			// Search first variable with 3 dimensions
			Variable cubeVar = null;
			for (Variable variable : ncfile.getVariables()) {
				int[] dims = variable.getShape();
				if (dims.length == 3) {
					cubeVar = variable;
				}
			}

			if (cubeVar == null)
				throw new CubeExplorerException("exception.notFound", "datacube");

			int[] cubeShape = cubeVar.getShape();

			if (posX < 0 || posX >= cubeShape[2]) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "posX", posX, 0, cubeShape[2] - 1);
			}

			if (posY < 0 || posY >= cubeShape[1]) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "posY", posY, 0, cubeShape[1] - 1);
			}
			
			int[] cubeOrigin = new int[3];
			cubeShape[1] = 1; // only one value per level
			cubeShape[2] = 1;
			cubeOrigin[1] = posY; // read this index
			cubeOrigin[2] = posX; // read this index

			// read 3D array for that index => 1D
			ArrayFloat.D1 cubeNetcdf = (ArrayFloat.D1) (cubeVar.read(cubeOrigin, cubeShape).reduce());

			// Copy metadata without comment
			JSONArray md = getHeader().getMetadata();

			JSONArray wavelength = new JSONArray();
			for (int idxPosZ = 0; idxPosZ < cubeShape[0]; idxPosZ++) {
				wavelength.put((double) idxPosZ);
			}
			
			metadata = getHeader().selectMetadata(md, pattern);
			spectrum.put("wavelength", wavelength);
			spectrum.put("value", cubeNetcdf.getStorage());
			properties.put("metadata", metadata);
			properties.put("spectrum", spectrum);

		} catch (CubeExplorerException ce) {
			throw ce;
		} catch (Exception exc) {
			throw new CubeExplorerException(exc);
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
