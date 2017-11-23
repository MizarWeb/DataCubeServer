/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.fits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import app.CubeExplorer;
import common.enums.FitsKeys;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCubeHeader;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.util.Cursor;

/**
 * @author vincent.cephirins
 *
 */
public class FitsHeader extends AbstractDataCubeHeader {

	private FitsCube cube = null;
	private List<Header> fitsHeaders = new ArrayList<Header>();
	protected int indexHeader = 0;

	/**
	 * construct a header fits JSONArray from fits file
	 * 
	 * @param cube
	 *            Cube of data fits
	 * @throws CubeExplorerException
	 */
	public FitsHeader(CubeExplorer ce, FitsCube cube) throws CubeExplorerException {
		super(ce);

		logger.trace("NEW FitsHeader({})", cube);

		this.indexHeader = 1;
		this.cube = cube;
		if (this.cube == null || this.cube.getFits() == null) {
			throw new CubeExplorerException("exception.fits.null");
		}

		// Read fits file
		readFitsHeaders(cube.getFits());
	}

	/**
	 * @return the fitsHeaders
	 */
	public List<Header> getFitsHeaders() {
		return fitsHeaders;
	}

	/**
	 * @return the Header
	 */
	public Header getFitsHeader(int idxHdu) {
		return fitsHeaders.get(idxHdu);
	}

	/**
	 * @return the indexHeader
	 */
	public int getIndexHeader() {
		return indexHeader;
	}

	private String StringInContinue(String val) {
		String result = val;
		if (val == null)
			return "";
		if (val.endsWith("&")) {
			result = val.substring(0, val.length() - 1);
		}
		return result;
	}

	private JSONArray parseMetadata(Header header) {
		JSONArray result = new JSONArray();
		Cursor<String, HeaderCard> cursor = header.iterator();
		
		logger.trace("ENTER retrieveMetadata({})", header.toString());
		
		while (cursor.hasNext()) {
			JSONArray jsonCard = new JSONArray();

			HeaderCard headerCard = cursor.next();
			String key = headerCard.getKey();
			String val = headerCard.getValue();
			String comment = headerCard.getComment();

			// Concaténation potentielle des lignes
			if (key != null) {
				switch (FitsKeys.getType(key)) {
				case COMMENT: {
					String prevComment = null;
					JSONArray card = jsonCard;
					// Reprise de la dernière valeur pour concaténation
					try {
						card = (JSONArray) result.opt(result.length() - 1);
						prevComment = (String) card.opt(2);
						prevComment = StringInContinue(prevComment) + comment;
						card.put(2, prevComment);
					} catch (JSONException e) {
						card.put(2, comment);
					}
					break;
				}
				case CONTINUE: {
					// Reprise de la dernière valeur pour concaténation
					JSONArray card = (JSONArray) result.opt(result.length() - 1);
					String prevValue = (String) card.opt(1);
					String prevComment = (String) card.opt(2);
					prevValue = StringInContinue(prevValue) + val;
					prevComment = StringInContinue(prevComment) + comment;
					card.put(1, prevValue);
					card.put(2, prevComment);
					break;
				}
				default:
					// Nouvelle valeur
					jsonCard.put(key);
					jsonCard.put(val);
					jsonCard.put(comment);

					// Enregistre la valeur
					result.put(jsonCard);
					break;
				}
			}
		}
		return result;
	}

	private void readFitsHeaders(Fits fits) throws CubeExplorerException {
		logger.trace("ENTER readFitsHeaders()");
		
		try {
			int nberHDUs = fits.getNumberOfHDUs();

			// Headers from fits
			logger.trace("Getting Headers...");
			logger.trace("Number of HDUs: " + nberHDUs);

			// Search Hdu image index
			JSONArray hduMetadata;
			String value;
			for (int idxHdu = 0; idxHdu < nberHDUs; idxHdu++) {
				logger.trace("Hdu : " + idxHdu);

				Header header = fits.getHDU(idxHdu).getHeader();
				fitsHeaders.add(header);
				hduMetadata = parseMetadata(header);
				jsonMetadata.put(hduMetadata);

				// Get references information for hdu
				// NAXIS n 	size of axis
				// CRVAL n 	coordinate value at reference point
				// CRPIX n 	array location of the reference point in pixels
				// CDELT n 	coordinate increment at reference point
				// CTYPE n 	axis type (8 characters)
				// CROTA n 	rotation from stated coordinate type.
				if (idxHdu == indexHeader) {
					String vX, vY, vZ;
					// Get size of axis

					vX = getValue(hduMetadata, "NAXIS1");
					vY = getValue(hduMetadata, "NAXIS2");
					vZ = getValue(hduMetadata, "NAXIS3");
					jsonDimensions.put("dimX", (vX == null)? 0.0 : Float.parseFloat(vX));
					jsonDimensions.put("dimY", (vY == null)? 0.0 : Float.parseFloat(vZ));
					jsonDimensions.put("dimZ", (vZ == null)? 0.0 : Float.parseFloat(vZ));

					// Get axis type (8 characters)
					vX = getValue(hduMetadata, "CTYPE1");
					vY = getValue(hduMetadata, "CTYPE2");
					vZ = getValue(hduMetadata, "CTYPE3");
					jsonDimensions.put("typeX", (vX == null)? "" : vX);
					jsonDimensions.put("typeY", (vY == null)? "" : vY);
					jsonDimensions.put("typeZ", (vZ == null)? "" : vZ);

					// Get array location of the reference point in pixels
					vX = getValue(hduMetadata, "CRPIX1");
					vY = getValue(hduMetadata, "CRPIX2");
					vZ = getValue(hduMetadata, "CRPIX3");
					jsonDimensions.put("refX", (vX == null)? 0.0 : Float.parseFloat(vX));
					jsonDimensions.put("refY", (vY == null)? 0.0 : Float.parseFloat(vY));
					jsonDimensions.put("refZ", (vZ == null)? 0.0 : Float.parseFloat(vZ));

					// Get coordinate values at reference point
					vX = getValue(hduMetadata, "CRVAL1");
					vY = getValue(hduMetadata, "CRVAL2");
					vZ = getValue(hduMetadata, "CRVAL3");
					jsonDimensions.put("refLon", (vX == null)? 0.0 : Float.parseFloat(vX));
					jsonDimensions.put("refLat", (vY == null)? 0.0 : Float.parseFloat(vY));
					jsonDimensions.put("refLevel", (vZ == null)? 0.0 : Float.parseFloat(vZ));

					// Get coordinate increments at reference point
					vX = getValue(hduMetadata, "CDELT1");
					vY = getValue(hduMetadata, "CDELT2");
					vZ = getValue(hduMetadata, "CDELT3");
					jsonDimensions.put("stepX", (vX == null)? 0.0 : Float.parseFloat(vX));
					jsonDimensions.put("stepY", (vY == null)? 0.0 : Float.parseFloat(vY));
					jsonDimensions.put("stepZ", (vZ == null)? 0.0 : Float.parseFloat(vZ));
				}
				
				// EXTNAME contains image index
				value = getValue(hduMetadata, "EXTNAME");
				if (value != null) {
					if (value.contains("ImageIndex")) {
						logger.trace("ImageIndex in HDU " + idxHdu);
						indexImage = idxHdu;
					}
				}
				
			}
			logger.trace("Header done !");
		} catch (FitsException fe) {
			throw new CubeExplorerException(fe);
		} catch (IOException ioe) {
			throw new CubeExplorerException(ioe);
		}
	}
}
