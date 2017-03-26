
package com.deitel.restjson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  This class works with jdbc, generating SQL statements.
 */
public class DataBaseConnection {
    
     private Connection myConnection;
     private PreparedStatement pStmt;
     final String checkUser= "SELECT count(*) from PLAYERS WHERE username= ?";
     final String checkEmail= "SELECT count(*) from PLAYERS WHERE email= ?";
     private Statement stmt;
    
     
    // Opens a connection to the DB
    void connectToDB() {
        try{
        
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        myConnection=DriverManager.getConnection(
                "jdbc:derby://localhost:1527/sdnaMemoryGameDB","memory","memory");
           }
        catch(Exception e){
            System.out.println("Failed to get connection");
            e.printStackTrace();
                          }
       
                       }
     //Inserts a new user to PLAYERS table
     String insertToTable(String username, String password, String email){
       
         String insertStr="";
         insertStr="INSERT INTO PLAYERS" + "(username, password, email, best_score) VALUES(?,?,?,?)";
         
         
         try {
              if (checkIfExist(checkUser, username))
              {
                 return "User existed";
              }
             else if (checkIfExist(checkEmail, email))
              { 
                return "Email existed";
              }
             else
            {
                pStmt = myConnection.prepareStatement(insertStr);
                pStmt.setString(1, username);
                pStmt.setString(2, password);
                pStmt.setString(3, email);
                pStmt.setString(4, "0");
                pStmt.executeUpdate();
                
                return "User was added";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "error-connecting to the db";
        }
    }
    
     //closes connection to the DB
     public void close(java.sql.Statement stmt)
     {
        
        if(stmt !=null)
        {
            try{
                stmt.close();
               }
            catch(Exception e){}
        }
     }

   
     
  public void destroy()
  {
  
    if(myConnection !=null)
    {
    
         try{
               myConnection.close();
            }
            catch(Exception e){}
    }
  }
  
  /** Checks if value exists in table
  * @param check- SQL statement
  * @param strToCheck- value to check in table
  */
  
  boolean checkIfExist(String check, String strToCheck) throws SQLException 
  {
         pStmt=myConnection.prepareStatement(check);
         pStmt.setString(1, strToCheck);
         ResultSet r1=pStmt.executeQuery();
        
         if(r1.next()) 
         {
             int count = r1.getInt(1);
             if (count!=0)
             {
             return true;
             }
         }
         
     return false;
    }
    
  
  /** Returns value by giving any statement
  * @param key- SQL WHERE=key string
  * @param column- SQL WHERE column string
  * @param strSelect- SQL SELECT string
  * @param strFrom- SQL FROM string
  * @param strToCheck- value to check in table
  */
   String findValue (String key, String column, String strSelect, String strFrom) throws SQLException
   {   
           
        String queryStr = "SELECT "+strSelect+" from "+strFrom+" WHERE " +column+"= ?";
        pStmt=myConnection.prepareStatement(queryStr);
        pStmt.setString(1, key);
        ResultSet rs=pStmt.executeQuery();
        String value="";
        while (rs.next())
         {
             value= rs.getString(strSelect);
         }
        System.out.println("USER PASSWORD IS:"  + value);
        return value;
   }
    
   
   //Closes and destroys opened DB connection
    void closeDBConnection()
       {
           close(pStmt);
           destroy();
       }

    //Inserts a new game row to ACTIVE_GAMES table
    int createNewGame(String p1, String p2, int id) throws SQLException 
    {
          String insertStr="INSERT INTO ACTIVE_GAMES" + "(game_id, user1, user2, date) VALUES(?,?,?,?)";
          pStmt = myConnection.prepareStatement(insertStr);
          java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
          pStmt.setInt(1, id);
          pStmt.setString(2, p1);
          pStmt.setString(3, p2);
          pStmt.setDate(4, date);
          pStmt.executeUpdate();
         
     return id;
    }

    //Returns a new game's ID, by checking what was the last new game's ID.
    int getLastID() throws SQLException 
    {
        int maxID=0;
        stmt = myConnection.createStatement();
        String findStr = "SELECT MAX(GAME_ID) FROM ACTIVE_GAMES";
        stmt.execute(findStr);    
        ResultSet rs = stmt.getResultSet();  
         if (rs.next()) 
         {
         maxID = rs.getInt(1);
         }        
        return maxID;
     }

    //Updates highscores table
    void updateScores(int gameID, String winner, int scores)
    {
        
         String insertStr="";
         insertStr="INSERT INTO HIGHSCORES" + "(username, score, date) VALUES(?,?,?)";
            
        try 
        {
           java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                pStmt = myConnection.prepareStatement(insertStr);
                pStmt.setString(1, winner);
                pStmt.setInt(2, scores);
                pStmt.setDate(3, date);
                pStmt.executeUpdate();
        }   catch (SQLException ex) 
                {
                 Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
                }

     }

    
    //Gets a list of the Highscores table- Returns it in JSON. 
    JSONObject getHighScores() 
    {
        
        JSONObject js= new JSONObject();
        String findStr = "SELECT USERNAME, SCORE FROM HIGHSCORES";
        try {    stmt = myConnection.createStatement();
            stmt.execute(findStr);
               ResultSet rs = stmt.getResultSet();
               int i=1;
               while (rs.next()) 
               {
                 js.put("user"+i, rs.getString("USERNAME"));
                 js.put("score"+i, rs.getString("SCORE"));
                 i++;
               }
            } 
        catch (SQLException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
     return js;
        
        
    }
}