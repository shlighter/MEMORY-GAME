/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deitel.restjson;

import com.google.gson.Gson;
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
 *This class updates the HIGHSCORE table by using a DataBaseConnection object to connect the jdbc 
 * @author Shai
 */
@Path("/welcome3")
public class updateScores {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of updateScores
     */
    public updateScores() {
    }

    @POST
    @Path("/updateScores")
    @Consumes("application/json")
 
    public String updateScores(InputStream incomingData) throws JSONException {
      
      		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
			while ((line = in.readLine()) != null) {
				jsonData += line +"\n";
			}
                        in.close();
                         JSONObject jsnObj = new JSONObject(jsonData);
      
                        String usernameJsn = jsnObj.getString("username");
                        int gameID = jsnObj.getInt("gameID");
                        String winner = jsnObj.getString("winner");
                        int scores = jsnObj.getInt("scores");
                         DataBaseConnection dbConnection = new DataBaseConnection();
                         dbConnection.connectToDB(); 
                         dbConnection.updateScores(gameID,winner,scores);
                         dbConnection.closeDBConnection();
         
		} catch (Exception e) {
			System.out.println("Error UPDATING SCORES");
                        
		}
       //For future purpose
       return new Gson().toJson("");
    }
    
    
    
    
    /**
     * Retrieves representation of an instance of com.deitel.restjson.updateScores
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of updateScores
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
