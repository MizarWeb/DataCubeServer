/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.netcdf;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import app.CubeExplorer;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCubeHeader;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * @author vincent.cephirins
 *
 */
public class NetcdfHeader extends AbstractDataCubeHeader {

	private NetcdfCube cube = null;
	private List<Variable> netcdfHeader = new ArrayList<Variable>();

	/**
	 * construct a header fits JSONArray from netcdf file
	 * 
	 * @param cube
	 *            Cube of data fits
	 * @throws CubeExplorerException
	 */
	public NetcdfHeader(CubeExplorer ce, NetcdfCube cube) throws CubeExplorerException {
		super(ce);

		logger.trace("NEW NetcdfHeader({})", cube);

		this.cube = cube;
		if (this.cube == null || this.cube.getNcfile() == null) {
			throw new CubeExplorerException("exception.fits.null");
		}

		// Read netcdf file
		readNetcdfHeader(cube.getNcfile());
	}

	/**
	 * @return the Header
	 */
	public List<Variable> getNetcdfHeader() {
		return netcdfHeader;
	}

	private JSONArray parseMetadata(List<Variable> variables) {
		JSONArray result = new JSONArray();

		logger.trace("ENTER retrieveMetadata()");

		for (Variable variable : variables) {
			JSONArray jsonCard = new JSONArray();
			
			// New value
			jsonCard.put(variable.getFullName());
			jsonCard.put(variable.getDimensions().toString());
			
			// search units attribute
			for (Attribute attribute : variable.getAttributes()) {
				if (attribute.getFullName().equals("units")) {
					jsonCard.put(attribute.getStringValue());
					break;
				}
			}
			
			// Store value
			result.put(jsonCard);
		}
		return result;
	}

	private void readNetcdfHeader(NetcdfFile ncfile) throws CubeExplorerException {
		logger.trace("ENTER readNetcdfHeader()");
		try {
			JSONArray metadata;
			metadata = parseMetadata(ncfile.getVariables());
			jsonMetadata.put(metadata);

			// Get dimensions
			int posX = 0;
			int posY = 0;
			int posZ = 0;

			List<Dimension> dim = ncfile.getDimensions();

			for (Dimension dimension : dim) {
				String name = dimension.getFullName();
				switch (name) {
				case "lon":
					posX = dimension.getLength();
					break;
				case "lat":
					posY = dimension.getLength();
					break;
				case "level":
					posZ = dimension.getLength();
					break;

				default:
					break;
				}

			}
			jsonDimensions.put("posX", posX);
			jsonDimensions.put("posY", posY);
			jsonDimensions.put("posZ", posZ);

		} catch (Exception ioe) {
			throw new CubeExplorerException(ioe);
		}
	}
}
