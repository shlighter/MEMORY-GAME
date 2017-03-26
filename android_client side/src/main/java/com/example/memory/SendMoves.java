package com.example.memory;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Shai on 2/15/2017.
 *
 */

public class SendMoves
{

    String username, returnFromServer;
    GameActivity gameActivityContext;
    int gameID;
    JSONObject returnedJSN;
    JSONObject jsnMoves;
    int movesNum,flipped;// The "move's number" will be increased by one with each turn

    public SendMoves(String username, int gameID, Context context)
    {
        this.gameActivityContext = (GameActivity)context;
        this.username = username;
        this.gameID = gameID;
        jsnMoves= new JSONObject();
        this.movesNum=0;
    }

    void incMoves()
    {
        movesNum++;
    }

    void setMoves(int m)
    {
        movesNum=m;
    }

    void sendMovesToServer(JSONObject jsnMoves)
    {
        this.jsnMoves = jsnMoves;

        try {
            this.jsnMoves.put("movesNum",movesNum);
            flipped=jsnMoves.getInt("flippedCards");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendMoves.SendPostRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){ System.out.println("in pre");}

        protected JSONObject doInBackground(String... arg0) {

               try {
                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome/sendMoves/");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsnMoves.toString());
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
           movesNum++;
           int flipped=0;
           boolean skipped=false;
           int firstCard,secondCard;

            try {
                Square s1,s2;
                flipped = resultJSN.getInt("flippedCards");
                firstCard= resultJSN.getInt("firstCard");
                skipped= resultJSN.getBoolean("skipped");

                //Time is over, flip cards back
                if (skipped)
                {
                    if (flipped == 1)
                    {
                        s1= gameActivityContext.list.get(firstCard);
                        s1.selected = 3;
                        gameActivityContext.list.set(firstCard, s1);
                        gameActivityContext.adapter.notifyDataSetChanged();
                        gameActivityContext.setFalse();
                        gameActivityContext.flippedCards = 0;
                    }
                    else if (flipped == 2)
                    {
                        secondCard=resultJSN.getInt("secondCard");
                        s2= gameActivityContext.list.get(secondCard);
                        s1= gameActivityContext.list.get(firstCard);
                        s1.selected = 3;
                        s2.selected =3;
                        gameActivityContext.list.set(firstCard, s1);
                        gameActivityContext.list.set(secondCard, s2);
                        gameActivityContext.adapter.notifyDataSetChanged();
                        gameActivityContext.setFalse();
                        gameActivityContext.flippedCards = 0;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    }



}
