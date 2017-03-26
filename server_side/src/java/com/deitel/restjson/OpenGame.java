
package com.deitel.restjson;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This class adds a row of new game to the jdbc's ACTIVE_GAMES table
 * @author Shai
 */
public class OpenGame  {
    
    String player1,player2;
    DataBaseConnection dbConnection;
    private static int gameID= 0;
    
    //private Lock lock = new ReentrantLock();
    
    public OpenGame(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        dbConnection = new DataBaseConnection();
        dbConnection.connectToDB();
        
    }

     int createNewGameInTable() {
        
        int id=0;
       
        try 
        {
            id = dbConnection.createNewGame(player1, player2, getGameID());
            
        } catch (SQLException ex) 
           {
            Logger.getLogger(OpenGame.class.getName()).log(Level.SEVERE, null, ex);
           }
        
        dbConnection.closeDBConnection();
        
        return id;
       
    }
    
  
    
      public int getGameID(){
        
        while(true){
//         lock.lock();
       
        try { 
            int userGameId= gameID;
            
         // When we start the server, gameID=0; We have to check what was the last ID on the ACTIVE_GAME's table
        if (gameID==0)   
        {userGameId= dbConnection.getLastID();
         gameID= userGameId+1;
        }
        
        else gameID++;
            return gameID;
            } catch (SQLException ex) {
            Logger.getLogger(OpenGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
 //           lock.unlock();
        }}
    }
    
    
    
}
