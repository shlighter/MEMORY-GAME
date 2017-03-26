
package com.deitel.restjson;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;


/**
 *This class connects between Login class to the jdbc
 * @author Shai
 */
public class LoginUser 
{
    
    String usernameJsn,passwordJsn;
    LoginUser(String usernameJsn,String passwordJsn)
    {this.usernameJsn = usernameJsn.toLowerCase();;
    this.passwordJsn = passwordJsn;
    }

    
    //Checks that the given info is correct (username and password)
    String checkTrueDetails() throws JSONException 
    {
                        String result;
                         DataBaseConnection db= new DataBaseConnection();
                         db.connectToDB();
        try {
                if (db.checkIfExist(db.checkUser,usernameJsn ))
                {
                   String userPassword= db.findValue(usernameJsn, "USERNAME","PASSWORD","PLAYERS");
                   if (userPassword.equals(passwordJsn))
                   result= "Success";
                   else
                   result= "WRONG PASSWORD";
                }
            
               else result= "user doesn't exist";
        } catch (SQLException ex) {
            Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
            result="error";
        }
        db.closeDBConnection();
        return result;
    }
}
