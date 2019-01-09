package app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonValue;

import common.exceptions.CubeExplorerException;
import common.exceptions.Messages;
import common.exceptions.SimpleException;
import fr.cnes.cubeExplorer.resources.AbstractDataCube;
import fr.cnes.cubeExplorer.resources.GeoJsonResponse;;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cubeExplorer/rest")
// @Path("/rest")
// @Consumes(MediaType.APPLICATION_JSON)
public class RestServices {

    // Initialize logger (see conf/log4j2.xml).
    final Logger LOGREST = LoggerFactory.getLogger(RestServices.class);

    String workspace = null;

    /**
     * @return the workspace
     */
    public String getWorkspace() {
        return workspace;
    }
    
    /**
     * @return the workspace
     */
    public void setWorkspace(String dataPath) {
    	LOGREST.info("Set Workspace : {}", dataPath);
        this.workspace = dataPath;
    }

    private void initService(String logLevel) throws CubeExplorerException {
        // Log level
        if (logLevel != null && Level.getLevel((logLevel = logLevel.toUpperCase())) != null) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.getLevel(logLevel));
            LOGREST.info("LEVEL : {}", logLevel);
        }

        // Properties
        workspace = CubeExplorer.getProperty("workspace", ".");
        LOGREST.info("initService - Set Workspace : {}", workspace);
        Locale lang = new Locale(CubeExplorer.getProperty("lang", Locale.getDefault().toString()));

        // loading application messages
        Messages.load("conf/messages", lang);
    }

    @RequestMapping(value = "/listFiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListFiles(@QueryParam("logLlevel") String logLevel, @QueryParam("pathData") String pathData) {
       

        JSONObject response = new JSONObject();
        JSONArray files;
        HttpStatus status = HttpStatus.OK;
        
       
        try {
        	LOGREST.info("Call getListFiles({}, {})",logLevel, pathData);
        	logLevel = (logLevel==null)?"INFO":logLevel;
        	LOGREST.info("getListFiles - logLevel ({})",logLevel);
        	initService(logLevel);            

        	LOGREST.info("getListFiles - logLevel ({})",logLevel);
            files = getAllFiles();
            LOGREST.info("files ({})",files.length());
            response.put("public_files", files.get(0));
            response.put("private_files", files.get(1));
           
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            LOGREST.error("getListFiles : {}", message);
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }

    private JSONArray getAllFiles() {
    	JSONArray response = new JSONArray();
    	File dir = null;
        dir = new File(workspace+"/public/");

        // create new filter
        // Filter to fits or netCdf files
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".fits") || name.endsWith(".nc");
            }
        };
    	 // array of files and directory
        List<String> dirList = new ArrayList<String>(Arrays.asList(dir.list(filter)));     
        response.put(0,  dirList.toArray());
        dir = new File(workspace+"/private/");
        List<String> dirList2 = new ArrayList<String>(Arrays.asList(dir.list(filter)));          
        response.put(1,  dirList2.toArray());
    	return response;
    }
    
    @RequestMapping(value = "/header", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getHeader(@QueryParam("entry") String entry, @QueryParam("metadata") String metadata,
        @QueryParam("logLevel") String logLevel,@QueryParam("pathData") String pathData) {

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSlide = null;
        AbstractDataCube adc = null;

        try {
        	logLevel = (logLevel==null)?"INFO":logLevel;
            initService(logLevel);
            LOGREST.info("Call getHeader({}, {}, {})", entry, metadata, pathData);

            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                LOGREST.info("exception.rest.header.syntax {}", se.getMessage());
                throw new CubeExplorerException(se, "exception.rest.header.syntax");
            }
            System.out.println(pathData);
            if(pathData!= "undefined" && !pathData.equals("null") && !pathData.isEmpty()) {
            	LOGREST.info("change data path {}", pathData);
            	setWorkspace(pathData);
            }
           
            File dir = new File(workspace+"/public/");
             FilenameFilter filter = new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return name.endsWith(".fits") || name.endsWith(".nc");
	            }
             };
			List<String> dirList = new ArrayList<String>(Arrays.asList(dir.list(filter)));  
			CubeExplorer ce;
           if(dirList.contains(entry)) {
            	// read file
                ce = new CubeExplorer(workspace + "/public/" + entry);
            }else {
            	ce = new CubeExplorer(workspace + "/private/" + entry);
            }
            
            LOGREST.info("Call getCube()");
            adc = ce.getCube();
            //LOGREST.info(adc.getCubeExplorer());
            
            JSONObject properties = adc.getHeader(metadata);
            //LOGREST.info(properties);
            properties.put("fileType", adc.getType().toString());

            // Format json response
            geoJsonSlide = new GeoJsonResponse(0, 0, properties);
            response.put("response", geoJsonSlide.getGeoJson());

            adc.close();
        }
        catch (SimpleException se) {
            status = HttpStatus.BAD_REQUEST;
            ArrayList<String> listMessage = se.getMessages();
            String message="";
            for (String s : listMessage)
            {
            	message += s + " ";
            }
            response.put("message", message);
            LOGREST.error("getHeader : {}", message);
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            LOGREST.error("getHeader : {}", message);
        }
        finally {
            if (adc != null) adc.close();
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }
 
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
        @QueryParam("posZ") int posZ, @QueryParam("logLevel") String logLevel, @QueryParam("pathData") String pathData) {

        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSlide = null;
        AbstractDataCube fc = null;

        try {
        	logLevel = (logLevel==null)?"INFO":logLevel;
            initService(logLevel);
            LOGREST.info("Call getFitsSlide({}, {}, {})", entry, metadata, posZ);


            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                LOGREST.error("getSlide : {}", se.getMessage());
                throw new CubeExplorerException(se, "exception.rest.slide.syntax");
            }

            if(!pathData.equals("undefined") && !pathData.equals("null") && !pathData.isEmpty()) {
            	LOGREST.info("getSlide - change data path {}", pathData);
            	setWorkspace(pathData);
            }
            File dir = new File(workspace+"/public/");
            FilenameFilter filter = new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return name.endsWith(".fits") || name.endsWith(".nc");
	            }
            };
			List<String> dirList = new ArrayList<String>(Arrays.asList(dir.list(filter)));  
            CubeExplorer ce;
            if(dirList.contains(entry)) {
             	// read file
                 ce = new CubeExplorer(workspace + "/public/" + entry);
             }else {
             	ce = new CubeExplorer(workspace + "/private/" + entry);
             }
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
            ArrayList<String> listMessage = se.getMessages();
            String message="";
            for (String s : listMessage)
            {
            	message += s + " ";
            }
            response.put("message", message);
            LOGREST.error("getSlide : {}", message); 
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;            
            String message = e.getMessage();
            response.put("message", e.getStackTrace());
            LOGREST.error("getSlide : {}", e.getMessage()); 
        }
        finally {
            if (fc != null) fc.close();
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }

    /**
     * Get a spectre from a plot
     * 
     * @param entry Name of Fits file
     * @param metadata Pattern of metadata to retrieve
     * @param posX Plot X from datacube
     * @param posY Plot Y from datacube
     * @return A slide
     * @throws SimpleException
     */
    @RequestMapping(value = "/spectrum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSpectrum(@QueryParam("entry") String entry,
        @QueryParam("metadata") String metadata, @QueryParam("posX") int posX, @QueryParam("posY") int posY,
        @QueryParam("logLevel") String logLevel, @QueryParam("pathData") String pathData) {

        
        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.OK;

        GeoJsonResponse geoJsonSpectrum = null;
        AbstractDataCube fc = null;

        try {
        	logLevel = (logLevel==null)?"INFO":logLevel;
            initService(logLevel);
            // Initialize logger (voir conf/log4j2.xml).
            LOGREST.info("Call getFitsSpectrum({}, {}, {}, {}, {})", entry, metadata, posX, posY, pathData);

            if (entry == null) {
                SimpleException se = new SimpleException("exception.parameterMissing", "entry");
                LOGREST.error("getSpectrum : {}", se.getMessage()); 
                throw new CubeExplorerException(se, "exception.rest.spectrum.syntax");
            }
            if(pathData!="undefined" && !pathData.equals("null") && !pathData.isEmpty()) {
            	LOGREST.info("getSpectrum - change data path {}", pathData);
            	setWorkspace(pathData);
            }
            
            File dir = new File(workspace+"/public/");
            FilenameFilter filter = new FilenameFilter() {
	            @Override
	            public boolean accept(File dir, String name) {
	                return name.endsWith(".fits") || name.endsWith(".nc");
	            }
            };
        	List<String> dirList = new ArrayList<String>(Arrays.asList(dir.list(filter)));  
            CubeExplorer ce;
            if(dirList.contains(entry)) {
             	// read file
                 ce = new CubeExplorer(workspace + "/public/" + entry);
             }else {
             	ce = new CubeExplorer(workspace + "/private/" + entry);
             }
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
            ArrayList<String> listMessage = se.getMessages();
            String message="";
            for (String s : listMessage)
            {
            	message += s + " ";
            }
            response.put("message", message);
            LOGREST.error("getSpectrum : {}", message); 
        }
        catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = e.getMessage();
            response.put("message", message);
            LOGREST.error("getSpectrum : {}", message); 
        }
        finally {
            if (fc != null) fc.close();
        }

        response.put("status", status.name());
        return new ResponseEntity<String>(response.toString(), status);
    }
    
    /**
     * User identification
     * 
     * @param username username
     * @param password password
     * @return yes or non
     * @throws SimpleException
     */
    @RequestMapping(value = "/identification", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> identification(@RequestBody UserCube user) {
    	
    	HttpStatus status = HttpStatus.OK;
        Boolean identification = false;
        JSONObject response = new JSONObject();
        try {
        	String logLevel = "INFO";
        	 initService(logLevel);
             LOGREST.info("Call identification()");

        	 workspace = CubeExplorer.getProperty("workspace", ".");    		       	
        	
			String usernameKnown = CubeExplorer.getProperty("username", null);
			String passwordKnown = CubeExplorer.getProperty("password", null);
			
			String usernamePublic = CubeExplorer.getProperty("username_public", null);
			String passwordPublic = CubeExplorer.getProperty("password_public", null);
			
			if((usernameKnown.equals(user.getUsername()) && passwordKnown.equals(user.getPassword())) 
					|| (usernamePublic.equals(user.getUsername()) && passwordPublic.equals(user.getPassword()))
					) {
				identification = true;
				response.put("message", identification);
				if(usernameKnown=="admin") {
					response.put("role",  CubeExplorer.getProperty("data_roles_admin", null));
				}else {
					response.put("role",  CubeExplorer.getProperty("data_roles_public", null));
				}
			}
			else {
				status = HttpStatus.FORBIDDEN;
				identification = false;
				response.put("message", identification);
			}
		} catch (CubeExplorerException e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			ArrayList<String> listMessage = e.getMessages();
	        String message="";
	        for (String s : listMessage)
	        {
	        	message += s + " ";
	        }
	        response.put("message", message);
            LOGREST.error("identification : {}", message); 

		}

        return new ResponseEntity<String>(response.toString(), status);
    }
    
    public String worldfile(String entry) {
    	
        JSONObject response = new JSONObject();
        
        //get size width and height of raster
        try {
			BufferedImage bimg = ImageIO.read(new File(workspace+"/"+entry));
			int width          = bimg.getWidth();
			int height         = bimg.getHeight();
			int[][] pixels = new int[width][height];

			for( int i = 0; i < width; i++ )
			    for( int j = 0; j < height; j++ ) {
			        pixels[i][j] = bimg.getRGB( i, j );
			        LOGREST.info("i - j - value : {} {] {}",i, j, pixels[i][j]); 
			    }
        
			   //get left top and bottom right of image
	        
	        //taille pixel
	        //ppx = (x2-x1)/rasterx
	        //ppy = (y2-y1)/rastery
	        
	        //coordonnée du centre du pixel
	        //xcentre = x1 + (ppx * .5)
	        //ycentre = y1 + (ppy * .5) #puisque ppy est négatif
	        
	        response.put("A",  "");
	        response.put("D",  "");
	        response.put("B",  "");
	        response.put("E",  "");
	        response.put("C",  "");
	        response.put("F",  "");
			
			
			
		} catch (IOException e) {
            String message = e.getMessage();
            response.put("message", message);
            LOGREST.error("getSpectrum : {}", message); 
		}
            
    	
    	return response.toString();
    	
    }
    


}
