/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.netcdf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	// Initialise un logger (voir conf/log4j2.xml).
	private static final Logger LOGGER = LogManager.getLogger("netcdfHeader");

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
			// Get the cube values name and unit
			Variable cubeVar = this.cube.findCubeVar();
			String cubeVarUnit = cubeVar.getUnitsString();
			String cubeVarType = cubeVar.getShortName();
			 jsonDimensions.put("unitVal", (cubeVarUnit == null) ? " - " : cubeVarUnit);
             jsonDimensions.put("typeVal",  (cubeVarType == null) ? "Value" : cubeVarType);  
			
			// Get the 3D Variables.
			Variable xVar = findVariable(ncfile, getVarX());
			Variable yVar = findVariable(ncfile, getVarY());
			Variable zVar = findVariable(ncfile, getVarZ());

			jsonMetadata = parseMetadata(ncfile.getVariables());

			// Get size of axis
			int xDim = (int) xVar.getSize();
			int yDim = (int) yVar.getSize();
			int zDim = (int) zVar.getSize();

			jsonDimensions.put("dimX", xDim);
			jsonDimensions.put("dimY", yDim);
			jsonDimensions.put("dimZ", zDim);
			
			// Get axis unit
			String unitX = xVar.getUnitsString();
			String unitY = yVar.getUnitsString();
			String unitZ = zVar.getUnitsString();
			jsonDimensions.put("unitX", (unitX == null) ? "" : unitX);
			jsonDimensions.put("unitY", (unitY == null) ? "" : unitY);
			jsonDimensions.put("unitZ", (unitZ == null) ? "" : unitZ);

			// Get axis type (8 characters)
			String typeX = xVar.getShortName();
			String typeY = yVar.getShortName();
			String typeZ = zVar.getShortName();
			jsonDimensions.put("typeX", ( typeX == null) ? "" : typeX);
			jsonDimensions.put("typeY", ( typeY == null) ? "" : typeY);
			jsonDimensions.put("typeZ", ( typeZ == null) ? "" : typeZ);

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

	public Variable findVariable(NetcdfFile ncfile, String[] varNames) throws CubeExplorerException {
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
	
	public static String[] getVarX() {
		String dim = "dimX";
		String defaultName = "lon";
		return getdim(dim, defaultName);
	}
	
	public static String[] getVarY() {
		String dim = "dimY";
		String defaultName = "lat";
		return getdim(dim, defaultName);
	}

	public static String[] getVarZ() {
		String dim = "dimZ";
		String defaultName = "level";
		return getdim(dim, defaultName);
	}
	private static String[] getdim(String var, String defaultName) {
		String[] varNames = {defaultName};
		try {
			String varNamesProp = CubeExplorer.getProperty(var, defaultName);
			varNames = varNamesProp.split("\\|");
		} catch (CubeExplorerException e) {
			LOGGER.warn(e.toString());
			if(LOGGER.isDebugEnabled()){
				e.printStackTrace();
			}
		}
		return varNames;
	}
}
