
package com.deitel.restjson;

import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *This class builds a new memory game board and also decides which play should start the game 
 * @author Shai
 */
public class NewBoard 
{

    private DataBaseConnection dbConnection;
    String username,player1,player2;
    int gameID;
    boolean yourTurn=false;
    final int NUMBER_OF_IMAGES= 46; //Number of images on the client side
    final int NUMBER_OF_COLUMNS=4;  
    final int NUMBER_OF_ROWS=5;
    final int NUMBER_OF_SQUARES= NUMBER_OF_COLUMNS *NUMBER_OF_ROWS;
    Random rand;
    boolean[] usedPicture,initialized;
    
    NewBoard(String username, int gameID) 
    {
      this.username= username;
      this.gameID= gameID;
      usedPicture= new boolean[NUMBER_OF_IMAGES];
      initialized = new boolean[NUMBER_OF_SQUARES];
      player1="";
      player2="";
      rand=new Random();
      dbConnection = new DataBaseConnection();
      dbConnection.connectToDB();
      
      //Connecting to the jdbc, and getting the playes name from ACTIVE_GAMES table, searching by given gameID. Returning the starting player.
      
      try {
            player1= dbConnection.findValue(Integer.toString(gameID),"GAME_ID","USER1","ACTIVE_GAMES");
            player2= dbConnection.findValue(Integer.toString(gameID),"GAME_ID","USER2","ACTIVE_GAMES");
            dbConnection.closeDBConnection();
            System.out.println("username is "+username+ "in playerturndb, player1="+player1+" player2=  "+player2+ " ,gameID: "  + gameID);
            } catch (SQLException ex) 
            {
            Logger.getLogger(NewBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
      if (username.equals(player1))
            this.yourTurn=true;
    }
    
    boolean yourTurn ()
    {
        return yourTurn;
    }

    public String getPlayer1() 
    {
        return player1;
    }
    
    public String getPlayer2() {
        return player2;
    }

   //Creats a random board. Number of images should be half the size of the total grid's cells.
   JSONObject newBoard()
   {

   JSONObject jsnObj = new JSONObject();
 
        for (int i = 0; i < NUMBER_OF_IMAGES; i++) 
         {
            usedPicture[i] = false;
              if (i< NUMBER_OF_SQUARES)
            initialized[i]=false;
         }
        
        int picNum,squareNum1,squareNum2;       
        for (int i = 0; i < NUMBER_OF_SQUARES/2; i++)
        {
       
            picNum= randomInt(usedPicture,NUMBER_OF_IMAGES);
            usedPicture[picNum]=true;
            squareNum1= randomInt(initialized,NUMBER_OF_SQUARES);
            initialized[squareNum1]=true;
            squareNum2= randomInt(initialized,NUMBER_OF_SQUARES);
            initialized[squareNum2]=true;
            
            try 
            { 
                jsnObj.put("sq"+(squareNum1+1),picNum);
                jsnObj.put("sq"+(squareNum2+1),picNum);
            } 
            catch (JSONException ex) 
            {
            Logger.getLogger(NewBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
   return jsnObj;
   }

    private int randomInt(boolean[] usedArray, int max) {
        int  n;
        int loopNumber=0;
        int j=max-1;
     
        do {loopNumber++;
       
             n= rand.nextInt(max) ;
        
             if (loopNumber%100==0)  //making sure the loop won't run forever
             { n=j;
                   j--;}}
            while (usedArray[n]==true);
          
            return n;
    }

}