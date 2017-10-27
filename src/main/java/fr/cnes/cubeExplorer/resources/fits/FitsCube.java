/*******************************************************************************
 * Copyright 2017 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 ******************************************************************************/

package fr.cnes.cubeExplorer.resources.fits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import app.CubeExplorer;
import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import fr.cnes.cubeExplorer.resources.fits.FitsHeader;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.Header;
import nom.tam.fits.TableHDU;

/**
 *
 */
public class FitsCube extends AbstractDataCube {

	private File fitsFile = null;
	private Fits fits = null;

	/**
	 * @param fileEntry
	 * @throws CubeExplorerException
	 */
	public FitsCube(CubeExplorer ce, File fileEntry) throws CubeExplorerException {
		super(ce, CubeType.FITS);

		logger.trace("NEW FitsCube({})", fileEntry);
		
		this.fitsFile = fileEntry;

		// Lecture du fichier fits
		this.fits = readFits(fileEntry);

		// Get header
		this.header = new FitsHeader(ce, this);
	}

	/**
	 * @return the fitsFile
	 */
	public File getFitsFile() {
		return fitsFile;
	}

	/**
	 * @return the fits
	 */
	public Fits getFits() {
		return fits;
	}

	private Fits readFits(File entry) throws CubeExplorerException {
		Fits fits = null;

		logger.trace("ENTER readFits({})", entry);
		
		try {
			// get the file path
			fits = new Fits(entry);
			FitsFactory.setUseHierarch(true);

			// read fits file
			fits.read();

		} catch (FitsException fe) {
			// free resources
			close();
			throw new CubeExplorerException(fe);
		}

		return fits;
	}

	public JSONObject getSlide(int indexHdu, int pNaxis3, String pattern, boolean radec) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject slide = new JSONObject();

		logger.trace("ENTER getSlide({}, {}, {})", pNaxis3, pattern, radec);
		
		try {
			if (indexHdu < 0 || indexHdu >= fits.getNumberOfHDUs()) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHdu, 0, fits.getNumberOfHDUs() - 1);
			}

			JSONArray md = getHeader().getMetadata().getJSONArray(indexHdu);

			// Recherche des axes
			String naxis1Value = getHeader().getValue(md, "NAXIS1");
			String naxis2Value = getHeader().getValue(md, "NAXIS2");
			String naxis3Value = getHeader().getValue(md, "NAXIS3");

			int naxis1 = (naxis1Value == null) ? 0 : Integer.parseInt(naxis1Value);
			int naxis2 = (naxis2Value == null) ? 0 : Integer.parseInt(naxis2Value);
			int naxis3 = (naxis3Value == null) ? 0 : Integer.parseInt(naxis3Value);

			if (pNaxis3 < 0 || pNaxis3 >= naxis3) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "naxis3", pNaxis3, 0, naxis3 - 1);
			}

			// Copie des metadata demandées sans les commentaires
			metadata = getHeader().selectMetadata(md, pattern);

			double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHdu).getData().getData());

			Double value;
			JSONArray longitudes = new JSONArray();
			JSONArray latitudes = new JSONArray();
			JSONArray values = new JSONArray();
			for (int idxNaxis1 = 0; idxNaxis1 < naxis1; idxNaxis1++) {
				JSONArray lineLon = new JSONArray();
				JSONArray lineLat = new JSONArray();
				JSONArray lineValues = new JSONArray();
				for (int idxNaxis2 = 0; idxNaxis2 < naxis2; idxNaxis2++) {
					value = cubeFits[pNaxis3][idxNaxis2][idxNaxis1];
					lineValues.put(value.isNaN() ? null : value);
					if (radec) {
						// TODO : Calcul des coordonnées
						lineLon.put((double) idxNaxis1);
						lineLat.put((double) idxNaxis2);
					}
				}
				if (radec) {
					longitudes.put(lineLon);
					latitudes.put(lineLat);
				}
				values.put(lineValues);
			}
			if (radec) {
				slide.put("longitude", longitudes);
				slide.put("latitude", latitudes);
			}
			slide.put("value", values);
			properties.put("metadata", metadata);
			properties.put("slide", slide);

		} catch (FitsException fe) {
			throw new CubeExplorerException(fe);
		} catch (IOException ioe) {
			throw new CubeExplorerException(ioe);
		}
		return properties;
	}

	public JSONObject getSpectrum(int indexHdu, int pNaxis1, int pNaxis2, String pattern) throws CubeExplorerException {
		JSONObject properties = new JSONObject();
		JSONArray metadata = new JSONArray();
		JSONObject spectrum = new JSONObject();

		logger.trace("ENTER getSpectrum({}, {}, {})", pNaxis1, pNaxis2, pattern);
		
		// Lecture des données du fichier fits
		try {
			if (indexHdu < 0 || indexHdu >= fits.getNumberOfHDUs()) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHdu, 0, fits.getNumberOfHDUs() - 1);
			}

			JSONArray md = getHeader().getMetadata().getJSONArray(indexHdu);

			// Recherche des axes
			String naxis1Value = getHeader().getValue(md, "NAXIS1");
			String naxis2Value = getHeader().getValue(md, "NAXIS2");
			String naxis3Value = getHeader().getValue(md, "NAXIS3");

			int naxis1 = (naxis1Value == null) ? 0 : Integer.parseInt(naxis1Value);
			int naxis2 = (naxis2Value == null) ? 0 : Integer.parseInt(naxis2Value);
			int naxis3 = (naxis3Value == null) ? 0 : Integer.parseInt(naxis3Value);

			if (pNaxis1 < 0 || pNaxis1 >= naxis1) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "naxis1", 0, pNaxis1, naxis1 - 1);
			}

			if (pNaxis2 < 0 || pNaxis2 >= naxis2) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "naxis2", 0, pNaxis2, naxis2 - 1);
			}

			// Copie des metadata demandées sans les commentaires
			metadata = getHeader().selectMetadata(md, pattern);

			double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHdu).getData().getData());

			Double value;
			JSONArray waveslength = new JSONArray();
			JSONArray values = new JSONArray();
			for (int idxNaxis3 = 0; idxNaxis3 < naxis3; idxNaxis3++) {
				value = cubeFits[idxNaxis3][pNaxis2][pNaxis1];
				values.put(value.isNaN() ? null : value);
				waveslength.put((double) idxNaxis3);
			}
			spectrum.put("wavelength", waveslength);
			spectrum.put("value", values);
			properties.put("metadata", metadata);
			properties.put("spectrum", spectrum);

		} catch (FitsException fe) {
			throw new CubeExplorerException(fe);
		} catch (IOException ioe) {
			throw new CubeExplorerException(ioe);
		}

		return properties;
	}

	// A SUPPRIMER
	private JSONArray readFitsDataSpectro(int indexHdu) throws CubeExplorerException {
		JSONArray data = new JSONArray();

		try {
			int nbrHdu = fits.getNumberOfHDUs();

			if (indexHdu < 0 || indexHdu >= nbrHdu) {
				// OutOfBound
				throw new CubeExplorerException("exception.outOfBound", "Hdu", indexHdu, 0, nbrHdu - 1);
			}

			int indexImage = getHeader().getIndexImage();

			Header header = fits.getHDU(indexHdu).getHeader();

			logger.info("Getting Wave...");
			int hhdduu = -1;
			for (int i = 0; i < nbrHdu; i++) {
				String a = fits.getHDU(i).getHeader().findKey("CDELT3");
				if (a != null) {
					logger.trace("CDELT3 find in " + i);
					hhdduu = i;
					break;
				}
			}
			if (hhdduu == -1) {
				logger.trace("CDELT3 not found in WCS cube");
			}

			// Wave from fits
			// if (fits.getHDU(hdu).getHeader().findKey("CDELT3")!=null) {
			float[] wave;
			if (hhdduu != -1) {
				logger.info("data from WCS cube");

				int naxis3 = header.getIntValue("NAXIS3");
				float crval3 = header.getFloatValue("CRVAL3");
				float cdelt3 = header.getFloatValue("CDELT3");
				logger.info(naxis3 + " " + crval3 + " " + cdelt3);
				wave = new float[naxis3];
				for (int i = 0; i < naxis3; i++) {
					wave[i] = crval3 + (i * cdelt3);
				}
			} else {
				logger.info("No wave data in the cube WCS");
				if (indexImage != -1) {
					logger.info("Using wave array from ImageIndex HDU table");
					TableHDU<?> cols = (TableHDU<?>) fits.getHDU(indexImage);
					double[] waveD = (double[]) cols.getColumn(0);
					wave = new float[waveD.length];
					for (int i = 0; i < waveD.length; i++) {
						wave[i] = (float) waveD[i];
					}
				} else {
					throw new CubeExplorerException("exception.fits.WaveNotFound");
				}
			}
			JSONObject jsonSpectrum = new JSONObject();
			jsonSpectrum.put("WAVE", wave);
			logger.info(" Wave - Done !");

			logger.info("Getting Cube Data...");
			// Spectrum from fits
			for (int m = 1; m <= 2; m++) {
				jsonSpectrum.put("NAXIS" + m, header.getDoubleValue("NAXIS" + m));
				jsonSpectrum.put("CRPIX" + m, header.getDoubleValue("CRPIX" + m));
				jsonSpectrum.put("CRVAL" + m, header.getDoubleValue("CRVAL" + m));
				jsonSpectrum.put("CDELT" + m, header.getDoubleValue("CDELT" + m));
				jsonSpectrum.put("CTYPE" + m, header.getStringValue("CTYPE" + m));
			}

			jsonSpectrum.put("NAXIS3", header.getDoubleValue("NAXIS3"));

			jsonSpectrum.put("INFO_QTTY", header.getStringValue("INFO____"));
			jsonSpectrum.put("UNIT_QTTY", header.getStringValue("QTTY____"));
			jsonSpectrum.put("INFO_WAVE", header.getStringValue("CTYPE3"));
			jsonSpectrum.put("UNIT_WAVE", header.getStringValue("CUNIT3"));

			double[][][] cubeFits = ((double[][][]) fits.getHDU(indexHdu).getData().getData());
			int naxis1 = cubeFits[0][0].length;
			int naxis2 = cubeFits[0].length;
			int naxis3 = cubeFits.length;

			// Reorganizing cube
			List<List<List<Float>>> cube3DL = new ArrayList<List<List<Float>>>(naxis1);
			// float cube[][][] = new float[naxis1][naxis2][naxis3];
			for (int x = 0; x < naxis1; x++) {
				List<List<Float>> list2 = new ArrayList<List<Float>>(naxis2);
				for (int y = 0; y < naxis2; y++) {
					List<Float> list3 = new ArrayList<Float>(naxis3);
					for (int z = 0; z < naxis3; z++) {
						// cube[x][y][z] = (float) cubeFits[z][y][x];
						if (cubeFits[z][y][x] != Double.NaN) {
							list3.add((float) cubeFits[z][y][x]);
						} else {
							list3.add(null);
						}
					}
					list2.add(list3);
				}
				cube3DL.add(list2);
			}

			JSONArray cubeJL = new JSONArray(cube3DL.toString());
			data.put(cubeJL);

			logger.trace("Header done !");
		} catch (FitsException fe) {
			throw new CubeExplorerException(fe);
		} catch (IOException ioe) {
			throw new CubeExplorerException(ioe);
		}
		return data;
	}

	public void close() {
		try {
			if (this.fits != null) {
				this.fits.close();
				this.fits = null;
				logger.trace("Resource fits " + this.fitsFile.getName() + " closed.");
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
