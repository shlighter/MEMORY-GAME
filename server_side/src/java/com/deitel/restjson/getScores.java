
package com.deitel.restjson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 * This class gets the high scores list from the jdbc using DataBaseConnection object
 * @author Shai
 */
@Path("/welcome4")
public class getScores {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of getScores
     */
    public getScores() {
    }

    
     
    @POST
    @Path("/getScores")
    @Consumes("application/json")
    public String getScores (InputStream incomingData) throws JSONException {
        
           JSONObject jsonToReturn = new JSONObject();
        
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
			while ((line = in.readLine()) != null) {
				jsonData += line +"\n";
			}
                        in.close();
                        JSONObject jsnObj = new JSONObject(jsonData);
      
                         DataBaseConnection dbConnection = new DataBaseConnection();
                         dbConnection.connectToDB(); 
                         jsonToReturn =dbConnection.getHighScores();
                         dbConnection.closeDBConnection();
                       
                               
		    } catch (Exception e) 
                    {}
		
 	  
    return jsonToReturn.toString();
    }
    
    /**
     * Retrieves representation of an instance of com.deitel.restjson.getScores
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of getScores
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
