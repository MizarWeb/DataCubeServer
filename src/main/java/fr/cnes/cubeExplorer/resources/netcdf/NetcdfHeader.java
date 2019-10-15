/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.netcdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

import app.CubeExplorer;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCubeHeader;
import ucar.ma2.ArrayFloat;
import ucar.nc2.Attribute;
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

		logger.info("NEW NetcdfHeader({})", cube);

		this.cube = cube;
		if (this.cube == null || this.cube.getNcfile() == null) {
			logger.error("exception.file.null");
			throw new CubeExplorerException("exception.file.null");
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

		logger.info("ENTER retrieveMetadata()");

		for (Variable variable : variables) {
			JSONArray jsonCard = new JSONArray();

			// New value
			String key = variable.getFullName();
			String value = variable.getDimensions().toString();
			String comment = "";

			// search units attribute
			for (Attribute attribute : variable.getAttributes()) {
				if (attribute.getFullName().equals("units")) {
					comment = attribute.getStringValue();
					break;
				}
			}

			// Store value
			jsonCard.put(key);
			jsonCard.put(value);
			jsonCard.put(comment);
			result.put(jsonCard);
		}

		return result;
	}

	private void readNetcdfHeader(NetcdfFile ncfile) throws CubeExplorerException {
		logger.info("ENTER readNetcdfHeader()");
		try {
			// Get the latitude and longitude Variables.
			Variable xVar = findVariable(ncfile, CubeExplorer.getVarX());
			Variable yVar = findVariable(ncfile, CubeExplorer.getVarY());
			Variable zVar = findVariable(ncfile, CubeExplorer.getVarZ());

			jsonMetadata = parseMetadata(ncfile.getVariables());

			// Get size of axis
			int xDim = (int) xVar.getSize();
			int yDim = (int) yVar.getSize();
			int zDim = (int) zVar.getSize();

			jsonDimensions.put("dimX", xDim);
			jsonDimensions.put("dimY", yDim);
			jsonDimensions.put("dimZ", zDim);

			// Get axis type (8 characters)
			jsonDimensions.put("typeX", (xVar.getDescription() == null) ? "" : xVar.getDescription());
			jsonDimensions.put("typeY", (yVar.getDescription() == null) ? "" : yVar.getDescription());
			jsonDimensions.put("typeZ", (zVar.getDescription() == null) ? "" : zVar.getDescription());

			// Retrieve first and last values from each axis to compute step
			ArrayFloat.D1 xArray = (ArrayFloat.D1) xVar.read();
			ArrayFloat.D1 yArray = (ArrayFloat.D1) yVar.read();
			ArrayFloat.D1 zArray = (ArrayFloat.D1) zVar.read();

			jsonDimensions.put("refX", 0.0);
			jsonDimensions.put("refY", 0.0);
			jsonDimensions.put("refZ", 0.0);

			// Get array location of the reference point in pixels
			Float xRef = xArray.get(0);
			Float yRef = yArray.get(0);
			Float zRef = zArray.get(0);

			// Get coordinate values at reference point
			jsonDimensions.put("refLon", xRef);
			jsonDimensions.put("refLat", yRef);
			jsonDimensions.put("refLevel", zRef);

			// Step = (lastValue - firstValue) / (length - 1)
			Float lonStep = ((xArray.get(xDim - 1) - xRef) / (xDim - 1));
			Float latStep = ((yArray.get(yDim - 1) - yRef) / (yDim - 1));
			Float levelStep = ((zArray.get(zDim - 1) - zRef) / (zDim - 1));

			// Get coordinate increments at reference point
			jsonDimensions.put("stepX", lonStep);
			jsonDimensions.put("stepY", latStep);
			jsonDimensions.put("stepZ", levelStep);
		} catch (Exception ioe) {
			logger.error("CubeExplorerException {}", ioe.getMessage());
			throw new CubeExplorerException(ioe);
		}
	}

	private Variable findVariable(NetcdfFile ncfile, String[] varNames) throws CubeExplorerException {
		Variable var = null;
		for (String varName : varNames) {
			var = ncfile.findVariable(varName);
			if(var != null){
				break;
			}
		}
		if (var == null) {
			throw new CubeExplorerException("exception.cube.dimMissing", "one of " + Arrays.toString(varNames));
		}
		return var;
	}
}
