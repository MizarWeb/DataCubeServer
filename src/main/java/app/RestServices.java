package app;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import common.exceptions.CubeExplorerException;
import common.exceptions.Messages;
import common.exceptions.SimpleException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import fr.cnes.cubeExplorer.resources.GeoJsonResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cubeExplorer/rest")
// @Path("/rest")
// @Consumes(MediaType.APPLICATION_JSON)
public class RestServices {

    // Initialize logger (see conf/log4j2.xml).
    final Logger LOGREST = LogManager.getLogger("restServices");

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

        // loading application messages
        Messages.load("conf/messages", lang);
    }

    @RequestMapping(value = "/listFiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListFiles(@QueryParam("logLlevel") String logLevel) {

        LOGREST.info("Call getListFiles()");

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        try {
            initService(logLevel);

            File dir = null;

            // create new file
            dir = new File(workspace);

            // create new filter
            // Filter to fits or netCdf files
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".fits") || name.endsWith(".nc");
                }
            };

            // array of files and directory
            List<String> dirList = Arrays.asList(dir.list(filter));
            dirList.add("testMizar");
            response.put("response", dirList.toArray());
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }

    @RequestMapping(value = "/header", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHeader(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
        @QueryParam("logLevel") String logLevel) {

        LOGREST.info("Call getHeader({}, {})", entry, metadata);

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSlide = null;
        AbstractDataCube adc = null;

        try {
            initService(logLevel);

            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                throw new CubeExplorerException(se, "exception.rest.header.syntax");
            }

            // read file
            CubeExplorer ce = new CubeExplorer(workspace + "/" + entry);
            adc = ce.getCube();

            JSONObject properties = adc.getHeader(metadata);
            properties.put("fileType", adc.getType().toString());

            // Format json response
            geoJsonSlide = new GeoJsonResponse(0, 0, properties);
            response.put("response", geoJsonSlide.getGeoJson());

            adc.close();
        }
        catch (SimpleException se) {
            status = HttpStatus.BAD_REQUEST;
            String message = se.getMessages().toString();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        finally {
            if (adc != null) adc.close();
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
     * @param entry Name of Fits file
     * @param metadata Pattern of metadata to retrieve
     * @param posZ Deep of slide from datacube
     * @return A slide
     * @throws SimpleException
     */
    @RequestMapping(value = "/slide", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSlide(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
        @QueryParam("posZ") int posZ, @QueryParam("logLevel") String logLevel) {

        LOGREST.info("Call getFitsSlide({}, {}, {})", entry, metadata, posZ);

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSlide = null;
        AbstractDataCube fc = null;

        try {
            initService(logLevel);

            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                throw new CubeExplorerException(se, "exception.rest.slide.syntax");
            }

            // read file
            CubeExplorer ce = new CubeExplorer(workspace + "/" + entry);
            fc = ce.getCube();

            JSONObject properties = fc.getSlide(posZ, metadata);
            properties.put("fileType", fc.getType().toString());

            // Format json response
            geoJsonSlide = new GeoJsonResponse(1, posZ, properties);
            response.put("response", geoJsonSlide.getGeoJson());

            fc.close();
        }
        catch (SimpleException se) {
            status = HttpStatus.BAD_REQUEST;
            String message = se.getMessages().toString();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        finally {
            if (fc != null) fc.close();
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }

    @RequestMapping(value = "/spectrum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSpectrum(@QueryParam("entry") String entry,
        @QueryParam("metadata") String metadata, @QueryParam("posX") int posX, @QueryParam("posY") int posY,
        @QueryParam("logLevel") String logLevel) {

        // Initialize logger (voir conf/log4j2.xml).
        LOGREST.info("Call getFitsSpectrum({}, {}, {}, {})", entry, metadata, posX, posY);

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSpectrum = null;
        AbstractDataCube fc = null;

        try {
            initService(logLevel);

            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                throw new CubeExplorerException(se, "exception.rest.spectrum.syntax");
            }

            // read file
            CubeExplorer ce = new CubeExplorer(workspace + "/" + entry);
            fc = ce.getCube();

            JSONObject properties = fc.getSpectrum(posX, posY, metadata);
            properties.put("fileType", fc.getType().toString());

            // Format json response
            geoJsonSpectrum = new GeoJsonResponse(posX, posY, properties);
            response.put("response", geoJsonSpectrum.getGeoJson());

            fc.close();
        }
        catch (SimpleException se) {
            status = HttpStatus.BAD_REQUEST;
            String message = se.getMessages().toString();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            // response.put("messageHtml", Text2Html.replace(message));
        }
        finally {
            if (fc != null) fc.close();
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }

}
