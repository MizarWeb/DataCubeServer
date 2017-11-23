/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/
package fr.cnes.cubeExplorer.resources;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import app.CubeExplorer;
import common.exceptions.CubeExplorerException;

/**
 *
 */
public abstract class AbstractDataCubeHeader {

	protected Logger logger = null;
	protected CubeExplorer cubeExplorer = null;

	protected JSONArray jsonMetadata = new JSONArray();
	protected JSONObject jsonDimensions = new JSONObject();
	protected int indexImage = -1;

	/**
	 * @param cubeExplorer
	 */
	protected AbstractDataCubeHeader(CubeExplorer ce) {
		super();
		this.cubeExplorer = ce;
		this.logger = CubeExplorer.getLogger();

		logger.trace("NEW dataCubeHeader()");
	}

	/**
	 * @return the cubeExplorer
	 */
	public CubeExplorer getCubeExplorer() {
		return cubeExplorer;
	}

	/**
	 * Return index of image for fits file.
	 * 
	 * @return the indexImage or -1 if not found
	 */
	public int getIndexImage() {
		return indexImage;
	}

	/**
	 * Return complete metadata [key, value, label]
	 * 
	 * @return JSONArray metadata
	 * @throws CubeExplorerException
	 */
	public JSONArray getMetadata() throws CubeExplorerException {
		return jsonMetadata;
	}

	public JSONObject getDimensions() {
		return jsonDimensions;
	}

	private String parsePattern(String pattern) {
		StringBuilder result = new StringBuilder();

		for (String elt : pattern.split(",")) {
			if (result.length() > 0)
				result.append("|");
			result.append("^").append(elt).append("$");
		}

		return result.toString();
	}

	/**
	 * Return all [key, value, label] from metadata
	 *
	 * @param metadata
	 *            selected metadata
	 * @return JSONArray all metadata
	 * @throws CubeExplorerException
	 */
	public JSONArray getMetadata(JSONArray metadata) throws CubeExplorerException {
		return getMetadata(metadata, ".*");
	}

	/**
	 * Return selected [key, value, label] corresponding to key pattern from
	 * specific metadata
	 * 
	 * @param metadata
	 *            specific metadata
	 * @param pattern
	 *            Key to find
	 * @return JSONArray metadata found
	 * @throws CubeExplorerException
	 */
	public JSONArray getMetadata(JSONArray metadata, String pattern) throws CubeExplorerException {
		JSONArray result = new JSONArray();

		if (pattern == null || pattern.length() == 0)
			return result;

		Pattern keyPattern = null;
		keyPattern = Pattern.compile(parsePattern(pattern));

		Iterator<Object> iter = metadata.iterator();
		Matcher matcher;
		while (iter.hasNext()) {
			JSONArray card = (JSONArray) iter.next();

			matcher = keyPattern.matcher(card.optString(0));
			if (matcher.find()) {
				result.put(card);
			}
		}

		return result;
	}

	/**
	 * Return first value corresponding to key pattern
	 * 
	 * @param metadata
	 *            selected metadata
	 * @return String Value of metadata or null
	 * @throws CubeExplorerException
	 */
	public String getValue(JSONArray metadata) throws CubeExplorerException {
		return getValue(metadata, null);
	}

	/**
	 * Return first value corresponding to key pattern
	 * 
	 * @param metadata
	 *            selected metadata
	 * @param patternKey
	 *            to find
	 * @return String Value of metadata or null
	 * @throws CubeExplorerException
	 */
	public String getValue(JSONArray metadata, String pattern) throws CubeExplorerException {
		String result = null;

		if (pattern == null || pattern.length() == 0)
			return result;

		Pattern keyPattern = null;
		keyPattern = Pattern.compile(parsePattern(pattern));

		Iterator<Object> iter = metadata.iterator();
		Matcher matcher;
		while (iter.hasNext()) {
			JSONArray card = (JSONArray) iter.next();

			matcher = keyPattern.matcher(card.optString(0));
			if (matcher.find()) {
				result = card.optString(1);
				break;
			}
		}

		return result;
	}

	/**
	 * Return all [key, value] from metatadata.
	 * 
	 * @param metadata
	 *            selected metadata
	 * @param pattern
	 *            select list (pattern) of header to returns
	 * @return a JSONArray [key, value] of selected header
	 */
	public JSONArray selectMetadata(JSONArray metadata) {
		return selectMetadata(metadata, ".*");
	}

	/**
	 * Return selected [key, value] with list of patterns from metadata.
	 * 
	 * @param metadata
	 *            selected metadata
	 * @param pattern
	 *            select list (pattern) of header to returns
	 * @return a JSONArray [key, value] of selected header
	 */
	public JSONArray selectMetadata(JSONArray metadata, String pattern) {
		JSONArray result = new JSONArray();

		if (pattern == null || pattern.length() == 0)
			return result;

		Pattern keyPattern = null;
		keyPattern = Pattern.compile(parsePattern(pattern));

		Matcher matcher;
		Iterator<Object> iter = metadata.iterator();
		while (iter.hasNext()) {
			JSONArray card = (JSONArray) iter.next();
			if (keyPattern != null) {
				matcher = keyPattern.matcher(card.optString(0));
				if (!matcher.find())
					continue;
			}
			// Copie des header key/value
			JSONArray newCard = new JSONArray();
			newCard.put(card.opt(0));
			newCard.put(card.opt(1));
			result.put(newCard);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return jsonMetadata.toString();
	}

	public String toString(int indent) {
		return jsonMetadata.toString(indent);
	}
}
