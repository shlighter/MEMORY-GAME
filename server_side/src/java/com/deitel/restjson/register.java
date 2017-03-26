
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
 * This class receives new user's information (username,password,email) and stores it on jdbc.
 *
 * @author Shai
 */
@Path("/welcome1")
public class register {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of register
     */
    public register() {
    }

    @POST
    @Path("/register")
    @Produces("application/json")
    @Consumes("application/json")
    public String register(InputStream incomingData) throws JSONException 
    {
        //TODO return proper representation object
        String returnedString;
        
        //receives json from client
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
			while ((line = in.readLine()) != null)
                              {
				jsonData += line +"\n";
		              }
                        in.close();
                        JSONObject jsnObj = new JSONObject(jsonData);
                        
         //adding new user to the data base, returns insertion status               
                         returnedString= addToDB(jsnObj.getString("username"),jsnObj.getString("password"), jsnObj.getString("email"));
		    } catch (Exception e) {
			                   returnedString="ERROR IN REGISTERTION";
		                           }
	
       return new Gson().toJson(returnedString);
    }
    
    //adding new user to the data base, returns insertion status 
     private String addToDB(String username, String pass, String email) 
     {
        NewUser user= new NewUser(username,pass,email);
        return (user.getResult());
     }
    
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of register
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
