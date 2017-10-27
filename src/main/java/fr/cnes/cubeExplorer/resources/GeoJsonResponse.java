/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/
package fr.cnes.cubeExplorer.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author vincent.cephirins
 *
 */
public class GeoJsonResponse {
	
	private JSONObject geoJson = new JSONObject();

	/**
	 * 
	 */
	public GeoJsonResponse(int coordx, int coordy, JSONObject properties) {
        JSONObject geoJsonFeature = new JSONObject();
        JSONObject geoJsonGeometry = new JSONObject();
        JSONArray geoJsonCoordinates = new JSONArray();
        
        geoJsonCoordinates.put(coordx);
        geoJsonCoordinates.put(coordy);
        try {
			geoJsonGeometry.put("type", "Point");
			geoJsonGeometry.put("coordinates", geoJsonCoordinates);
			geoJsonFeature.put("type", "Feature");
			geoJsonFeature.put("geometry", geoJsonGeometry);
			geoJsonFeature.put("properties", properties);
			geoJson.put("feature", geoJsonFeature);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the geoJson
	 */
	public JSONObject getGeoJson() {
		return geoJson;
	}

}
