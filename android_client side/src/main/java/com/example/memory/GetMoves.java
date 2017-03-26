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
 * Created by Shai
 * This class connects to the server in order to get the other player's actions
 */

public class GetMoves {


    String username, returnFromServer;
    GameActivity gameActivityContext;
    int gameID;
    JSONObject returnedJSN;
    int moveNum;// The "Move's number" will be increased by one with each turn
    private boolean stop;


    public GetMoves(String username, int gameID, Context context)
    {
        this.gameActivityContext = (GameActivity)context;
        this.username = username;
        this.gameID = gameID;
        this.moveNum=0;
        this.stop=false;
    }

    void setMove(int m)
    {
        moveNum =m;
    }

    void startGettingMoves()

    {
        new SendPostRequest().execute("");
    }
    //Game is over
    public void stop()
    {
        this.stop=true;
    }


    public boolean isStop()
    {
        return stop;
    }

    public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {

        protected void onPreExecute(){}

        protected JSONObject doInBackground(String... arg0) {


            try {System.out.println(" moves is"+ moveNum);
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("username", username);
                jsonObj.put("gameID", gameID);
                jsonObj.put("movesNum", moveNum);
                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome/getMoves/");
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
                System.out.println("\nError while calling REST Service");
                returnFromServer= "errorAndroid";
                System.out.println(e);
            }

            return returnedJSN;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSN)
        {

            int position = -1;
            int flipped = 0;
            moveNum++;
            int firstCardPic = -1;
            int secondCardPic = -1;
            boolean skipped = false;
            boolean finish = false;

            try {
                skipped = resultJSN.getBoolean("skipped");
                position = resultJSN.getInt("position");
                flipped = resultJSN.getInt("flippedCards");
                firstCardPic = resultJSN.getInt("firstCardPic");
                finish = resultJSN.getBoolean("finish");
               } catch (JSONException e)
            {
                e.printStackTrace();
            }

            if ((finish == false)&&(isStop()==false))
            {

                //The other user skipped this turn
                if (skipped == true)
                {
                        //Time is over and the user skipped his turn, its flipped cards won't count
                        if (flipped >= 1)
                        {
                        Square s1 = gameActivityContext.list.get(position);
                        s1.selected = 3;
                        gameActivityContext.list.set(position, s1);
                        gameActivityContext.adapter.notifyDataSetChanged();
                        gameActivityContext.setFalse();
                        gameActivityContext.flippedCards = 0;
                        }

                        gameActivityContext.changeArrowDirection();
                        gameActivityContext.sendMoves.setMoves(moveNum);
                        gameActivityContext.myTurn = !gameActivityContext.myTurn;
                        gameActivityContext.wtngForMvsToSnd.setMove(moveNum);
                        gameActivityContext.wtngForMvsToSnd.startWaitingForMovesToSend();
                }

                else
                {
                    gameActivityContext.makeAmove(position);

                    if (flipped == 1)
                    {
                        new SendPostRequest().execute();  //get the second card
                    }
                    if (flipped == 2)
                    {
                        try {
                            secondCardPic = resultJSN.getInt("secondCardPic");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (firstCardPic != secondCardPic) {

                            gameActivityContext.sendMoves.setMoves(moveNum);
                            gameActivityContext.wtngForMvsToSnd.setMove(moveNum);
                            gameActivityContext.myTurn = true;
                            gameActivityContext.wtngForMvsToSnd.startWaitingForMovesToSend();
                            new Handler()
                                    .postDelayed(new Runnable() {
                                        public void run() {

                                            gameActivityContext.changeArrowDirection();
                                        }
                                    }, 600);


                        } else new SendPostRequest().execute();
                    }
                }

            }

            else
            {  if (stop==false)
                 {
                     Toast.makeText(gameActivityContext, "Sorry, the other player quit. \n let's find you a new player!",
                    Toast.LENGTH_LONG).show();
                    gameActivityContext.finish();
                 }

            }
        }
    }
}
