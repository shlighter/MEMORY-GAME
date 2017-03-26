
package com.deitel.restjson;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 * This is the server's main web service. 
 * It includes methods that receives json by inputstream from clients, and returns answers by strings.
 * The methods are for: 
 * -Waiting to match 2 players in a game.
 * -Returning the same new game-board to the chosen 2 players.
 * -Sending and Receiving players actions in the game.
 * -2 static classes-  Player and GameBoard
 * @author Shai
 */
@Path("/welcome")
public class jsonResource {

    @Context
    private UriInfo context;
    public String txt;
    private static BlockingQueue<Player> queue = new ArrayBlockingQueue<>(999);//Waiting players to match for a new game
    private static ConcurrentHashMap<String, Player> playersWaitingForNewBoard= new ConcurrentHashMap<>();//This HashMap is for synchronizing player1 and player 2 before sending a new board
    private static ConcurrentHashMap<String, Player> playersWaitingForMoves= new ConcurrentHashMap<>();//This HashMap is for synchronizing player1 and player 2 before sending players actions
    private static ConcurrentHashMap<String, Player> WaitingForMovesToSend= new ConcurrentHashMap<>();//This HashMap is for synchronizing waiting players object (Current playing players- are waiting to get onclick actions)
    private static ConcurrentHashMap<Integer, GameBoard> activeBoards= new ConcurrentHashMap<>();//This HashMap contains all active games- uses GameBoard object
  

    /**
     * Creates a new instance of jsonResource
     */
    public jsonResource() {
    }

    //Waiting to match 2 players in a game. using a queue. Returns gameID to both client-players.
    @POST
    @Path("/waiting")
    @Consumes("application/json")
    
    public int waiting(InputStream incomingData) throws JSONException 
    {
      
        Player thisPlayer=new Player("0",0);
        Player opponent=new Player("0",0);
        
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
                        String usernameJsn = jsnObj.getString("username");
                        thisPlayer = new Player(usernameJsn,0);
                       
                        if (queue.isEmpty()) 
                        {
                            queue.add(thisPlayer);
                            synchronized (thisPlayer) 
                            {
                                thisPlayer.wait();
                            }
                        }
                        else 
                        {
                           opponent = queue.take();
                           opponent.setOpponent(thisPlayer);
                           thisPlayer.setOpponent(opponent);
                           synchronized (opponent) 
                           {
                            OpenGame openGame = new OpenGame(thisPlayer.name,thisPlayer.opponent.name); 
                            int id = openGame.createNewGameInTable();
                            thisPlayer.setGameID(id);
                            opponent.setGameID(id);
                            opponent.notify();
                           }
                
                     	} 
                
                    } catch (Exception e) 
                    {
			System.out.println("Error: -waiting ");
                        if (queue.contains(thisPlayer))
                        queue.remove(thisPlayer);
                         if (queue.contains(opponent))
                        queue.remove(thisPlayer);
                        
                    }
     return thisPlayer.gameID;
    }
  
    
    //Returns the same new game-board to the 2 players.
    @POST
    @Path("/playersTurn")
    @Consumes("application/json")
    @Produces("application/json")
    public String playersTurn(InputStream incomingData) throws JSONException 
    {
        Boolean yourTurn=false;
        String usernameJsn="";
        JSONObject jsnToRetrun= new JSONObject();
        GameBoard thisBoard;
        Player thisPlayer;
        String firstPlayer,secondPlayer;
        Player first,second;
                     
        //Gets information from player- username and gameID
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
                        usernameJsn = jsnObj.getString("username");
                        int gameID = jsnObj.getInt("gameID");
                        NewBoard pt = new NewBoard(usernameJsn,gameID);
                        yourTurn= pt.yourTurn(); //If this player is the first player- yourTurn gets true
                        thisPlayer = new Player(usernameJsn,gameID);
                        secondPlayer=pt.getPlayer2();
                        firstPlayer=pt.getPlayer1();
                        
                        
                        if (yourTurn)
                        {
                            jsnToRetrun= pt.newBoard();
                            //Default parameters
                            thisPlayer.setMovesNum(0);
                            jsnToRetrun.put("skipped", true);
                            jsnToRetrun.put("position",0);
                            jsnToRetrun.put("flippedCards",0);
                            jsnToRetrun.put("secondCardPic",0);
                            jsnToRetrun.put("firstCard",0);
                            jsnToRetrun.put("secondCard",0);
                            jsnToRetrun.put("firstCardPic",0);
                            jsnToRetrun.put("movesNum",0);
                            jsnToRetrun.put("error",false);
                            jsnObj.put("finish", false);
                            thisBoard= new GameBoard(gameID, usernameJsn,secondPlayer , jsnToRetrun);
                            thisBoard.setMoves(jsnToRetrun);
                            playersWaitingForNewBoard.put(usernameJsn, thisPlayer);//Tells the second player "I'm here"
              
                            //If the second player is not connected yet, wait.
                            if ((playersWaitingForNewBoard.get(secondPlayer)==null) || ((playersWaitingForNewBoard.get(secondPlayer)!=null)&& (playersWaitingForNewBoard.get(secondPlayer).getGameID()!=gameID)) )
                            {   synchronized(thisPlayer)
                                {
                                thisPlayer.wait(3000);
                                }
                            }
                        
                            second= playersWaitingForNewBoard.get(secondPlayer);
                            second.setMovesNum(0);
                            thisPlayer.setOpponent(second);
                            thisPlayer.setGameBoard(thisBoard);
                            second.setOpponent(thisPlayer);
                            second.setGameBoard(thisBoard);
                            thisBoard.setPlayer1obj(thisPlayer);
                            thisBoard.setPlayer2obj(second);
                            activeBoards.put(gameID, thisBoard);
                            synchronized(second){
                                                second.setNotified(true);
                                                second.notify();
                                                }
                            }
                        else //Second player
                            { 
                          
                            playersWaitingForNewBoard.put(usernameJsn,thisPlayer); //tell the first player that i'm here
                            first= playersWaitingForNewBoard.get(firstPlayer);
                            if ((first !=null)&&(first.getGameID()==gameID)) //if the first player connected first- wake it up
                               {
                                    synchronized(first)
                                    {
                                    first.notify();
                                    }
                                }
                                
                            Boolean notify= true;
                            if (activeBoards.get(gameID)==null)     
                                {   notify= false;
                                    synchronized (thisPlayer)
                                    {
                                    thisPlayer.wait(5000);
                                    notify=thisPlayer.isNotified();
                                    }
                                }
                            
                                                          
                            jsnToRetrun= thisPlayer.getGameBoard().getJsnBoard();
                            if (notify==false)
                                jsnToRetrun.put("error",true);
                        
                            }
                        
                        jsnToRetrun.put("yourTurn", yourTurn);
                        jsnToRetrun.put("player1", firstPlayer);
                        jsnToRetrun.put("player2", secondPlayer);
                        
                      System.out.println("json to ret:"+jsnToRetrun.toString());                                    
		} catch (Exception e) {
			System.out.println("Error"+usernameJsn+"playsturn");
                                      }
		
        return jsnToRetrun.toString();
    }  
 
       
   // When player uses onClick on the gameBoard, it sends its actions and save it on a Game Board object
  @POST
   @Path("/sendMoves")
   @Consumes("application/json")
   public String sendMoves(InputStream incomingData) throws JSONException {
       
        String returnedString;
        GameBoard thisBoard=new GameBoard();
        Player thisPlayer;
        String firstPlayer,secondPlayer,username;
        Player opponent;
        Boolean skipped=false;
        int gameID, movesNum,flipped;
        JSONObject copy= new JSONObject();
        JSONObject jsnObj = new JSONObject();
                //Gets player's actions
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
                      
			while ((line = in.readLine()) != null) 
                        {
				jsonData += line +"\n";
			}
                        in.close();
                        jsnObj = new JSONObject(jsonData);
                        movesNum = (int) jsnObj.get("movesNum");//Every turn gets a new number
                        gameID = (int) jsnObj.get("gameID");
                        username = (String) jsnObj.get("sendingPlayer");
                        firstPlayer = (String) jsnObj.get("firstPlayer");
                        secondPlayer = (String) jsnObj.get("secondPlayer");
                        flipped = jsnObj.getInt("flippedCards");
                        thisBoard= activeBoards.get(gameID);
                        if (username.equals(firstPlayer))
                            { thisPlayer= thisBoard.getPlayer1obj();}
                        else
                            { 
                                thisPlayer=thisBoard.getPlayer2obj();
                            }
                        thisPlayer.setMovesNum(movesNum);
                        // System.out.println( "MODE:SENDING, USER:"+username+" MOVES:" + movesNum+ " GAMEID:"+gameID+" position: "+ jsnObj.getInt("position"));
                        opponent= thisPlayer.getOpponent();
                        
                        //Let other know that I'm here
                        playersWaitingForMoves.put(username, thisPlayer);
                        
              //(Before the user sends its actions (onClick), it creats a "waiting for moves" object which is waiting on HashMap and counting 10 seconds until the user's turn is over)
              //We get this object from the HashMap and informs it about an onClick action
              Player p= WaitingForMovesToSend.get(username);
              int waitingNum;
              if (flipped==1)
                  waitingNum= movesNum;
              else
                  waitingNum=movesNum-1;
                 synchronized(thisPlayer)
              {    
                    //wait for "waiting for moves" object to be created on the HashMap
                    while ((p==null)||((p!=null)&&((p.getMovesNum()!=waitingNum)||(p.getGameID()!=gameID)||(p.isSleep()==false))))
                        {        thisPlayer.wait(10000); 
                                 break; 
                        }
                    //While the other player which now uses the getMoves method is not ready- wait for it- unless it more than 10 seconds and then abort the game
                    while  ((playersWaitingForMoves.get(opponent.getName())==null) || ((playersWaitingForMoves.get(opponent.getName())!=null)&& (((playersWaitingForMoves.get(opponent.getName()).getGameID()!=gameID)) || (playersWaitingForMoves.get(opponent.getName()).getMovesNum()!=movesNum)||(playersWaitingForMoves.get(opponent.getName()).isSleep()==false))))
                         { 
                             thisPlayer.wait(10000);
                             break;
                         }
               }
              
               copy = new JSONObject(jsnObj, JSONObject.getNames(jsnObj));
                
                 
              if (thisBoard.isFinish()==false)
              {thisBoard.setMoves(jsnObj); }
                        
                        //If it's too late to send the actions
                       if (p.getDone()==true)
                       {
                           skipped= true; 
                           jsnObj.put("skipped", true); 
                         
                       }
                       else
                        {
                           skipped=false; //If it's the first card to be flipped- notify the other player (in getMoves), don't notify "waiting for moves" object
                           //If it's the first card to be flipped- notify the other player (in getMoves), don't notify "waiting for moves" object
                            if (flipped==1) {
                                                synchronized(opponent)
                                                {   
                                                 opponent.setNotified(true);
                                                 opponent.notify();
                                                }  
                                            }
                       
                        else
                            {
                                synchronized(p)
                                {  
                                    p.setNotified(true);
                                    p.notify();
                                }  
                            }
                        jsnObj.put("skipped", false);
                       
                        }
                     
		} catch (Exception e) 
                {
		 returnedString="error";
		}
		
     return copy.toString();
   }
    
    //Getting the actions from the other player
    @POST
    @Path("/getMoves")
    @Consumes("application/json")
    public String getMoves(InputStream incomingData) throws JSONException {
       
        String returnedString;
        GameBoard thisBoard;
        Player thisPlayer;
        String firstPlayer,secondPlayer,username;
        Player opponent;
        int gameID,movesNum;
        JSONObject jsnToReturn=new JSONObject();
        JSONObject jsnObj=new JSONObject();
        boolean finish=false;
        
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
			while ((line = in.readLine()) != null) {
				jsonData += line +"\n";
			}
                        in.close();
                          jsnObj = new JSONObject(jsonData);
                       
                        username = jsnObj.getString("username");
                        gameID = jsnObj.getInt("gameID");
                        movesNum = jsnObj.getInt("movesNum");
                        //System.out.println( "MODE:getting, USER:"+username+" MOVES:" + movesNum+ " GAMEID:"+gameID);                 
                         thisBoard= activeBoards.get(gameID);
                         firstPlayer= thisBoard.getPlayer1();
                         secondPlayer= thisBoard.getPlayer2();
                         if (username.equals(firstPlayer))
                            {
                                thisPlayer= thisBoard.getPlayer1obj();
                            }
                         else
                            {
                                thisPlayer=thisBoard.getPlayer2obj();
                            }
                         thisPlayer.setNotified(false);
                         opponent=thisPlayer.getOpponent();
                         thisPlayer.setMovesNum(movesNum);
                         //"I'm here"
                         playersWaitingForMoves.put(username, thisPlayer );
                         boolean notified=true;
                         thisPlayer.setNotifiedByWaiting(false);
                         synchronized(thisPlayer) 
                            {  
                            thisPlayer.setSleep(true);
                            thisPlayer.wait(15000);//Wait until notified, unless the other user quits
                            notified=thisPlayer.isNotified();//When the first card flipped, sender notifies getter
                            }
                          thisPlayer.setSleep(false);
                          if ((thisPlayer.isNotifiedByWaiting()==false)&&(notified==false)) //2 ways to be notified- By getting flipped cards/After 10 seconds without any action
                            { 
                              finish=true;  //If both types of notify are false- game is over
                            }
                        
                          jsnObj= thisBoard.getMoves();
                          jsnObj.put("movesNum", movesNum);
                        
                          if (finish)
                          {
                              jsnObj.put("finish", true);
                              thisBoard.setFinish(true);}
                          else
                              jsnObj.put("finish", false);
                        
                          if (notified==false)
                          {
                              jsnObj.put("skipped", true);//skipped= the other user didn't perform any action but is still in the game
                        
                          }
                          else 
                          { 
                           jsnObj.put("skipped", false);
                           }
                         
                                
		} catch (Exception e) {
			System.out.println("Error Parsing2: -getmoves ");
                        
		}
		
 
       return jsnObj.toString();
    }
    
    
    //Current playing player- counts 10 second until action performed, then notify the other player if either skipped or not
    @POST
    @Path("/waitingForMovesToSend")
    @Consumes("application/json")
    public String waitingForMovesToSend(InputStream incomingData) throws JSONException {
        
      
        String returnedString="";
        GameBoard thisBoard=new GameBoard();
        Player thisPlayer;
        String firstPlayer,secondPlayer,username;
        Player opponent;
        int gameID, movesNum;
        boolean notified=true;
        boolean finish=false;
        JSONObject copy= new JSONObject();
        JSONObject jsnObj= new JSONObject();
        
         
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
                        String jsonData="";
                         
			while ((line = in.readLine()) != null) 
                        {
				jsonData += line +"\n";
			}
                        in.close();
                        jsnObj = new JSONObject(jsonData);
                        movesNum = (int) jsnObj.get("movesNum");
                        username = (String) jsnObj.get("username");
                        gameID = (int) jsnObj.get("gameID");
                        firstPlayer = (String) jsnObj.get("firstPlayer");
                        secondPlayer = (String) jsnObj.get("secondPlayer");
                        thisBoard= activeBoards.get(gameID);
                        copy= thisBoard.getMoves();
                        copy.put("flippedCards", 0);
                        thisBoard.setMoves(copy);
                        if (username.equals(firstPlayer))
                            {
                                thisPlayer= thisBoard.getPlayer1obj();
                            }
                        else
                            {   
                                thisPlayer=thisBoard.getPlayer2obj();
                            }
                           
                         opponent= thisPlayer.getOpponent();
                       //  System.out.println( "MODE:waiting, USER:"+username+" MOVES:" + movesNum+ " GAMEID:"+gameID+"op name:"+opponent.getName());
                         Player p=new Player(thisPlayer.getName(), gameID);
                         p.setMovesNum(movesNum);
                       
                         // "I'm here"
                        WaitingForMovesToSend.put(username, p);
                       
                        //Wait 10 seconds for player to perform actions
                        if ((playersWaitingForMoves.get(thisPlayer.getName())==null) || ((playersWaitingForMoves.get(thisPlayer.getName())!=null)&& (((playersWaitingForMoves.get(thisPlayer.getName()).getGameID()!=gameID)) || (playersWaitingForMoves.get(thisPlayer.getName()).getMovesNum()!=movesNum))))
                           {     synchronized(p) 
                                            {     
                                            p.setSleep(true);
                                            p.wait(10000);
                                            notified=p.isNotified();
                                            //After 10 seconds without notification               
                                            if (notified==false)
                                            p.setDone(true);
                                            //Waiting for the other player to be ready
                                            while  ((playersWaitingForMoves.get(opponent.getName())==null) || ((playersWaitingForMoves.get(opponent.getName())!=null)&& (((playersWaitingForMoves.get(opponent.getName()).getGameID()!=gameID)) || ((playersWaitingForMoves.get(opponent.getName()).getMovesNum())<movesNum))))
                                            {  
                                                p.wait(1000);//after 10 second- assume the other play quit
                                                break;
                                            }
                                            }   
                           
                                   if ((playersWaitingForMoves.get(opponent.getName())==null) || ((playersWaitingForMoves.get(opponent.getName())!=null)&& (((playersWaitingForMoves.get(opponent.getName()).getGameID()!=gameID)) || ((playersWaitingForMoves.get(opponent.getName()).getMovesNum())<movesNum))))
                                    {
                                       thisBoard.setFinish(true);
                                       finish=true;
                                    }
                           }
                           
                        if (finish==false)
                           {   synchronized(opponent)
                                {   
                                    opponent.setNotified(notified);
                                    opponent.setNotifiedByWaiting(true);
                                    opponent.notify();
                                }
                               
                             
                            }
                        jsnObj = thisBoard.getMoves();
                        if (notified==false)
                         { 
                             returnedString="skipped";
                             jsnObj.put("skipped",true)  ;
                             int flipped= jsnObj.getInt("flippedCards");
                             jsnObj.put("movesNum", movesNum+flipped);
                         }
                        else
                        {
                            returnedString="notSkipped";
                            jsnObj.put("skipped",false);
                        }
                                 
                        if (finish)
                            jsnObj.put("finish", true);
                        else
                            jsnObj.put("finish", false);
                       
		} catch (Exception e) {
			System.out.println("Error Parsing2: - waitingsendmobes");
                        returnedString="er23ror";
		}
		
       return jsnObj.toString();
    }
    
    //This is a static class for a game board object
    static class GameBoard 
    {
        
        private int gameID,movesNum;
        private String player1,player2;
        private JSONObject jsnBoard;//The pictures for the board
        JSONObject copy,moves;//moves= includes -the new flipped card
        private Player player1obj,player2obj;
        private boolean finish;

        public GameBoard(int gameID, String player1, String player2, JSONObject jsn) 
        {
            this.gameID = gameID;
            this.player1 = player1;
            this.player2 = player2;
            this.movesNum=0;
            this.finish=false;
           
            try {
                copy = new JSONObject(jsn, JSONObject.getNames(jsn));
                this.jsnBoard = copy;
                } catch (JSONException ex) {
                Logger.getLogger(jsonResource.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }

        private GameBoard() {
           //To change body of generated methods, choose Tools | Templates.
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }

        public JSONObject getJsnBoard() {
            return jsnBoard;
        }

        public void setMovesNum(int movesNum) {
            this.movesNum = movesNum;
        }

        public int getMovesNum() {
            return movesNum;
        }

        public void setJsnBoard(JSONObject jsnBoard) {
            this.jsnBoard = jsnBoard;
        }

        public int getGameID() {
            return gameID;
        }

        public String getPlayer1() {
            return player1;
        }

        public String getPlayer2() {
            return player2;
        }

        public void setGameID(int gameID) {
            this.gameID = gameID;
        }

        public void setStringPlayer1(String player1) {
            this.player1 = player1;
        }

        public void setStringPlayer2(String player2) {
            this.player2 = player2;
        }
        
        public void setPlayer1obj(Player player1) {
            this.player1obj = player1;
        }

        public void setPlayer2obj(Player player2) {
            this.player2obj = player2;
        }        

        public void setMoves(JSONObject moves) {
            this.moves = moves;
        }

        public JSONObject getMoves() {
            return moves;
        }

        public Player getPlayer1obj() {
            return player1obj;
        }

        public Player getPlayer2obj() {
            return player2obj;
        }
        
        
    }
    
//This static class represents a player object, and its current status (sleeps/notified...)
    static class Player 
    {

        private String name;
        private Player opponent;
        private int gameID, movesNum;
        private GameBoard gameBoard;
        private boolean notified, isDone,sleep, notifiedByWaiting;

              
        Player (String name, int gameID) 
        {
            this.name = name;
            this.gameID = gameID;
            notified=false;
            sleep=false;
            notifiedByWaiting=false;
        }

        public boolean isSleep() 
        {
            return sleep;
        }

        public boolean isNotifiedByWaiting() 
        {
            return notifiedByWaiting;
        }

        public void setNotifiedByWaiting(boolean notifiedByWaiting) {
            this.notifiedByWaiting = notifiedByWaiting;
        }

        public int getMovesNum() {
            return movesNum;
        }

        public boolean isNotified() {
            return notified;
        }

        public void setNotified(boolean notified) {
            this.notified = notified;
        }

        public void setMovesNum(int movesNum) {
            this.movesNum = movesNum;
        }
        
          public void setGameID(int gameID) {
            this.gameID = gameID;
        }

        public int getGameID() {
            return gameID;
        }
        
        public void setDone(boolean sDone)
        {isDone= sDone;}

        public GameBoard getGameBoard() {
            return gameBoard;
        }

        public void setGameBoard(GameBoard gameBoard) {
            this.gameBoard = gameBoard;
        }

        
        public String getName() {
            return name;
        }

        public Player getOpponent() {
            return opponent;
        }

        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        public boolean getDone() {
            return isDone;
        }

        private void setSleep(boolean b) {
             this.sleep = b;
        }
        
        
    }
}
