
package com.deitel.restjson;


/**
 *This class is for a new player, saving his information in the jdbc by using DataBaseConnection object
 * @author Shai
 * 
 */
public class NewUser 
{
    
    private String username, password, email, result ;
    private DataBaseConnection dbConnection;
    

    public NewUser(String username,String password, String email) {
        this.username = username.toLowerCase();
        this.password = password;
        this.email = email.toLowerCase();
        dbConnection = new DataBaseConnection();
        dbConnection.connectToDB();
        result= dbConnection.insertToTable(username, password, email);
        dbConnection.closeDBConnection();
    }

    //Returns insertion status
     public String getResult() 
     {
        return result;
     }
    
    
}
