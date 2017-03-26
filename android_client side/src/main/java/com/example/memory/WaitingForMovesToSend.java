package com.example.memory;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Shai on 2/17/2017.
 * This class connects to the server to count down if the user is either going to skip its turn or not, notifies the other player.
 */

public class WaitingForMovesToSend {

    String username, returnFromServer,firstPlayer,secondPlayer;
    GameActivity gameActivityContext;
    int gameID;
    JSONObject returnedJSN;
    int moveNum;// The "move's number" will be increased by one with each turn
    boolean stop;

    public String getFirstPlayer()
    {
        return firstPlayer;
    }

    public void setFirstPlayer(String firstPlayer)
    {
        this.firstPlayer = firstPlayer;
    }

    public String getSecondPlayer()
    {
        return secondPlayer;
    }

    public void setSecondPlayer(String secondPlayer)
    {
        this.secondPlayer = secondPlayer;
    }

    public WaitingForMovesToSend(String username, int gameID, Context context) {

        this.gameActivityContext = (GameActivity) context;
        this.username = username;
        this.gameID = gameID;
        this.moveNum = 1;
        stop=false;

    }

    void setMove(int m)
    {
        moveNum = m;
    }

    int getMoveNum()
    {
        return moveNum;
    }

     void startWaitingForMovesToSend()
    {
        new WaitingForMovesToSend.SendPostRequest().execute("");
    }

    //game is over
    public void stop()
    {
        this.stop=true;
    }

    public boolean isStop()
    {
        return stop;
    }

    public class SendPostRequest extends AsyncTask<String, Void, JSONObject>
    {

        protected void onPreExecute()
        { }

        protected JSONObject doInBackground(String... arg0) {


            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("username", username);
                jsonObj.put("gameID", gameID);
                jsonObj.put("movesNum", moveNum);
                jsonObj.put("firstPlayer", firstPlayer);
                jsonObj.put("secondPlayer", secondPlayer);

                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome/waitingForMovesToSend/");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonObj.toString());
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                String jsonData= "";
                while ((line = in.readLine()) != null)
                {
                    jsonData += line +"\n";
                }
                in.close();
                returnedJSN= new JSONObject(jsonData);
            } catch (Exception e) {
                returnFromServer = "errorAndroid";
                System.out.println(e);
            }

            return returnedJSN;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSN) {

            int position = -1;
            int flipped = 0;
            boolean finish=false;
            int firstCardPic = -1;
            int secondCardPic = -1;
            boolean skipped = false;
            Square s1;

            try {
                skipped=resultJSN.getBoolean("skipped");
                flipped = resultJSN.getInt("flippedCards");
                firstCardPic=resultJSN.getInt("firstCardPic");
                position= resultJSN.getInt("position");
                moveNum= resultJSN.getInt("movesNum");
                finish=resultJSN.getBoolean("finish");
                moveNum++;

                if((finish == false)&&(isStop()==false))
                {

                    if (skipped)
                    {
                        if (flipped == 1)
                        {
                            s1 = gameActivityContext.list.get(position);
                            s1.selected = 3;
                            gameActivityContext.list.set(position, s1);
                            gameActivityContext.adapter.notifyDataSetChanged();
                            gameActivityContext.setFalse();
                            gameActivityContext.flippedCards = 0;
                        }
                    }

                    if (skipped)
                    {
                        gameActivityContext.changeArrowDirection();
                        gameActivityContext.getMoves.setMove(moveNum);
                        gameActivityContext.myTurn = !gameActivityContext.myTurn;
                        gameActivityContext.getMoves.startGettingMoves();
                    }
                    else
                    {
                        secondCardPic = resultJSN.getInt("secondCardPic");
                        if (firstCardPic != secondCardPic)
                        {
                                gameActivityContext.changeArrowDirection();
                                gameActivityContext.getMoves.setMove(moveNum);
                                gameActivityContext.myTurn = !gameActivityContext.myTurn;
                                gameActivityContext.getMoves.startGettingMoves();
                         }
                        else
                        {
                                new WaitingForMovesToSend.SendPostRequest().execute();
                        }

                    }

                }

                else
                {
                    if (stop==false)
                        {
                            Toast.makeText(gameActivityContext, "Sorry, the other player quit. \n let's find you a new player!",
                            Toast.LENGTH_LONG).show();
                            gameActivityContext.finish();
                        }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
}