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
     * @param cube Cube of data fits
     * @throws CubeExplorerException
     */
    public NetcdfHeader(CubeExplorer ce, NetcdfCube cube) throws CubeExplorerException {
        super(ce);

        logger.trace("NEW NetcdfHeader({})", cube);

        this.cube = cube;
        if (this.cube == null || this.cube.getNcfile() == null) {
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

        logger.trace("ENTER retrieveMetadata()");

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
        logger.trace("ENTER readNetcdfHeader()");
        try {
            // Get the latitude and longitude Variables.
            Variable lonVar = ncfile.findVariable("lon");
            Variable latVar = ncfile.findVariable("lat");
            Variable levelVar = ncfile.findVariable("level");

            if (lonVar == null) throw new CubeExplorerException("exception.cube.dimMissing", "lon");
            if (latVar == null) throw new CubeExplorerException("exception.cube.dimMissing", "lat");
            if (levelVar == null) throw new CubeExplorerException("exception.cube.dimMissing", "level");

            jsonMetadata = parseMetadata(ncfile.getVariables());

            // Get size of axis
            int lonDim = (int) lonVar.getSize();
            int latDim = (int) latVar.getSize();
            int levelDim = (int) levelVar.getSize();

            jsonDimensions.put("dimX", lonDim);
            jsonDimensions.put("dimY", latDim);
            jsonDimensions.put("dimZ", levelDim);

            // Get axis type (8 characters)
            jsonDimensions.put("typeX", (lonVar.getDescription() == null) ? "" : lonVar.getDescription());
            jsonDimensions.put("typeY", (latVar.getDescription() == null) ? "" : latVar.getDescription());
            jsonDimensions.put("typeZ", (levelVar.getDescription() == null) ? "" : levelVar.getDescription());

            // Retrieve first and last values from each axis to compute step
            ArrayFloat.D1 lonArray = (ArrayFloat.D1) lonVar.read();
            ArrayFloat.D1 latArray = (ArrayFloat.D1) latVar.read();
            ArrayFloat.D1 levelArray = (ArrayFloat.D1) levelVar.read();

            jsonDimensions.put("refX", 0.0);
            jsonDimensions.put("refY", 0.0);
            jsonDimensions.put("refZ", 0.0);

            // Get array location of the reference point in pixels
            Float lonRef = lonArray.get(0);
            Float latRef = latArray.get(0);
            Float levelRef = levelArray.get(0);

            // Get coordinate values at reference point
            jsonDimensions.put("refLon", lonRef);
            jsonDimensions.put("refLat", latRef);
            jsonDimensions.put("refLevel", levelRef);

            // Step = (lastValue - firstValue) / (length - 1)
            Float lonStep = ((lonArray.get(lonDim - 1) - lonRef) / (lonDim - 1));
            Float latStep = ((latArray.get(latDim - 1) - latRef) / (latDim - 1));
            Float levelStep = ((levelArray.get(levelDim - 1) - levelRef) / (levelDim - 1));

            // Get coordinate increments at reference point
            jsonDimensions.put("stepX", lonStep);
            jsonDimensions.put("stepY", latStep);
            jsonDimensions.put("stepZ", levelStep);
        }
        catch (Exception ioe) {
            throw new CubeExplorerException(ioe);
        }
    }
}
