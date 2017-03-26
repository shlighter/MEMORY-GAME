
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
 * This class is a user's login to the system
 * @author Shai
 */
@Path("/welcome2")
public class login {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of login
     */
    public login() {
    }

     
    
    @POST
    @Path("/login")
    @Consumes("application/json")
   
    public String login(InputStream incomingData) throws JSONException {
      
        String returnedString;
        
        //Gets username and password strings from client and checks the info is correctby LoginUser object that connects to jdbc
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
                        String passwordJsn = jsnObj.getString("password");
                           System.out.println("user to check:" +usernameJsn);
                      
                        LoginUser lgnUser = new LoginUser (usernameJsn,passwordJsn);
                        returnedString = lgnUser.checkTrueDetails();
                      System.out.println("logined:" +returnedString);
                      
                                
		} catch (Exception e) {
			               returnedString="Error -login ";
		                      }
		
       return returnedString;
    }
    
    
    
    
    /**
     * Retrieves representation of an instance of com.deitel.restjson.login
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of login
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
}
