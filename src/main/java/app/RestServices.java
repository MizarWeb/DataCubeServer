package app;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import common.enums.CubeType;
import common.exceptions.CubeExplorerException;
import common.exceptions.Messages;
import common.exceptions.SimpleException;
import fr.cnes.cubeExplorer.resources.GeoJsonResponse;
import fr.cnes.cubeExplorer.resources.fits.FitsCube;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cubeExplorer/rest")
//@Path("/rest")
//@Consumes(MediaType.APPLICATION_JSON)
public class RestServices {

	// Initialise un logger (voir conf/log4j2.xml).
	final Logger LOGREST = LogManager.getLogger("restServices");
	
	final static int indexHdu = 1;
	String workspace = null;

	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}

	private void initService(String logLevel) throws CubeExplorerException {
		// Log level
		if (logLevel != null && Level.getLevel((logLevel = logLevel.toUpperCase())) != null) {
			Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.getLevel(logLevel));
			LOGREST.trace("LEVEL : {}", logLevel);
		}

		// Properties
		workspace = CubeExplorer.getProperty("workspace", ".");
		Locale lang = new Locale(CubeExplorer.getProperty("lang", Locale.getDefault().toString()));

		// Chargement des messages applicatifs
		Messages.load("conf/messages", lang);
	}

	@RequestMapping(value = "/fits/listFiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getListFiles(@QueryParam("level") String logLevel) {

		LOGREST.info("Call getListFiles()");

		JSONObject response = new JSONObject();
		HttpStatus status = HttpStatus.OK;

		try {
			initService(logLevel);

			File dir = null;

			// create new file
			dir = new File(workspace);

			// create new filter
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".fits");
				}
			};

			// array of files and directory
			response.put("response", dir.list(filter));
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			String message = e.getMessage();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		}

		response.put("status", status.name());
		return new ResponseEntity<String>(response.toString(), status);
	}

	@RequestMapping(value = "/fits/header", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getHeader(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
			@QueryParam("level") String logLevel) {

		LOGREST.info("Call getHeader({}, {})", entry, metadata);

		JSONObject response = new JSONObject();
		HttpStatus status = HttpStatus.OK;

		GeoJsonResponse geoJsonSlide = null;
		FitsCube fc = null;

		try {
			initService(logLevel);

			if (entry == null) {
				SimpleException se = new SimpleException("exception.parameterMissing", "entry");
				throw new CubeExplorerException(se, "exception.rest.header.syntax");
			}

			// Lecture du fichier Fits
			fc = (FitsCube) new CubeExplorer(CubeType.FITS, new File(workspace, entry)).getCube();

			JSONObject header = new JSONObject();
			JSONArray md = fc.getHeader().getMetadata().getJSONArray(indexHdu);
			
			if (metadata != null) {
				header.put("metadata", fc.getHeader().getMetadata(md, metadata));
			} else {
				header.put("metadata", fc.getHeader().getMetadata(md));
			}
			geoJsonSlide = new GeoJsonResponse(0, 0, header);
			response.put("response", geoJsonSlide.getGeoJson());

			fc.close();
		} catch (SimpleException se) {
			status = HttpStatus.BAD_REQUEST;
			String message = se.getMessages().toString();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			String message = e.getMessage();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} finally {
			if (fc != null)
				fc.close();
		}

		response.put("status", status.name());
		return new ResponseEntity<String>(response.toString(), status);
	}

	// @GET
	// @Path("/testGet")
	// public Response getTest() {
	// return Response
	// .status(Status.OK)
	// .entity("Hello")
	// .build();
	// }
	//
	/**
	 * Get a slide from Fits File
	 * 
	 * @param entry
	 *            Name of Fits file
	 * @param metadata
	 *            Pattern of metadata to retrieve
	 * @param naxis3
	 *            Deep of slide from datacube
	 * @return A slide
	 * @throws SimpleException
	 */
	@RequestMapping(value = "/fits/slide", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFitsSlide(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
			@QueryParam("naxis3") int naxis3, @QueryParam("level") String logLevel) {

		LOGREST.info("Call getFitsSlide({}, {}, {})", entry, metadata, naxis3);

		JSONObject response = new JSONObject();
		HttpStatus status = HttpStatus.OK;

		GeoJsonResponse geoJsonSlide = null;
		FitsCube fc = null;

		try {
			initService(logLevel);

			if (entry == null) {
				SimpleException se = new SimpleException("exception.parameterMissing", "entry");
				throw new CubeExplorerException(se, "exception.rest.slide.syntax");
			}

			// Lecture du fichier Fits
			fc = (FitsCube) new CubeExplorer(CubeType.FITS, new File(workspace, entry)).getCube();

			JSONObject slide = fc.getSlide(indexHdu, naxis3, metadata, true);
			geoJsonSlide = new GeoJsonResponse(1, naxis3, slide);
			response.put("response", geoJsonSlide.getGeoJson());

			fc.close();
		} catch (SimpleException se) {
			status = HttpStatus.BAD_REQUEST;
			String message = se.getMessages().toString();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			String message = e.getMessage();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} finally {
			if (fc != null)
				fc.close();
		}

		response.put("status", status.name());
		return new ResponseEntity<String>(response.toString(), status);
	}

	@RequestMapping(value = "/fits/spectrum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFitsSpectrum(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
			@QueryParam("naxis1") int naxis1, @QueryParam("naxis2") int naxis2, @QueryParam("level") String logLevel) {

		// Initialise un logger (voir conf/log4j2.xml).
		LOGREST.info("Call getFitsSpectrum({}, {}, {}, {})", entry, metadata, naxis1, naxis2);

		JSONObject response = new JSONObject();
		HttpStatus status = HttpStatus.OK;

		GeoJsonResponse geoJsonSpectrum = null;
		FitsCube fc = null;

		try {
			initService(logLevel);

			if (entry == null) {
				SimpleException se = new SimpleException("exception.parameterMissing", "entry");
				throw new CubeExplorerException(se, "exception.rest.spectrum.syntax");
			}

			// Lecture du fichier Fits
			fc = (FitsCube) new CubeExplorer(CubeType.FITS, new File(workspace, entry)).getCube();

			JSONObject spectrum = fc.getSpectrum(indexHdu, naxis1, naxis2, metadata);
			geoJsonSpectrum = new GeoJsonResponse(naxis1, naxis2, spectrum);
			response.put("response", geoJsonSpectrum.getGeoJson());

			fc.close();
		} catch (SimpleException se) {
			status = HttpStatus.BAD_REQUEST;
			String message = se.getMessages().toString();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			String message = e.getMessage();
			response.put("message", message);
			// response.put("messageHtml", Text2Html.replace(message));
		} finally {
			if (fc != null)
				fc.close();
		}

		response.put("status", status.name());
		return new ResponseEntity<String>(response.toString(), status);
	}

}
