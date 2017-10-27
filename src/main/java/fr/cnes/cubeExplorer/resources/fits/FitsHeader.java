/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.fits;

import java.io.IOException;

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

		this.cube = cube;
		if (this.cube == null || this.cube.getFits() == null) {
			throw new CubeExplorerException("exception.fits.null");
		}

		// Read fits file
		readFitsHeaders(cube.getFits());
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

	private JSONArray retrieveMetadata(Header header) {
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
		try {
			int nberHDUs = fits.getNumberOfHDUs();

			// Headers from fits
			logger.trace("Getting Headers...");
			logger.trace("Number of HDUs: " + nberHDUs);

			JSONArray hduMetadata;
			JSONArray card;
			String value;
			for (int idxHdu = 0; idxHdu < nberHDUs; idxHdu++) {
				logger.trace("Hdu : " + idxHdu);

				Header header = fits.getHDU(idxHdu).getHeader();
				hduMetadata = retrieveMetadata(header);
				jsonMetadata.put(hduMetadata);

				// Search HIERARCH mapping
				card = getMetadata(hduMetadata, "EXTNAME|HIERARCH.KEY.META.*");
				if (card.length() != 0) {
					logger.trace(card);
				}

				// search hdu image index
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
