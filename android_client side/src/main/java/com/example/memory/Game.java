package com.example.memory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Shai on 2/10/2017.
 * This class connects to the server to get the game's board.
 */

public class Game {

    String username, returnFromServer;
    GameActivity gameActivityContext;
    int gameID;
    JSONObject returnedJSN;


    public Game(String username, int gameID, Context context) {

        this.gameActivityContext = (GameActivity)context;
        this.username = username;
        this.gameID = gameID;
        new SendPostRequest().execute();
    }

    public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {

        protected void onPreExecute(){}

        protected JSONObject doInBackground(String... arg0) {


            try {
                JSONObject jsonObj = new JSONObject();
                 jsonObj.put("username", username);
                 jsonObj.put("gameID", gameID);

                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome/playersTurn/");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonObj.toString());
                out.close();
                System.out.println("\nBEFORE IN");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                System.out.println("\nAFTER IN");

                String line = "";
                String jsonData= "";
                while ((line = in.readLine()) != null) {
                    jsonData += line +"\n";
                }
                in.close();
                returnedJSN= new JSONObject(jsonData);

            } catch (Exception e) {
                returnFromServer= "errorAndroid";
                System.out.println(e);
                gameActivityContext.finish();
            }


            return returnedJSN; //Game's board- tells the gridview what images should be in each cell
        }

        @Override
        protected void onPostExecute(JSONObject resultJSN)
        {  System.out.println("here:" +resultJSN.toString());
            boolean error;
            try {
                error = resultJSN.getBoolean("error");
                if (error==false)
                gameActivityContext.loadGame(resultJSN);
                else
                {gameActivityContext.finish();
                    Toast.makeText(gameActivityContext, "Sorry, the other player quit. \n let's find you a new player!",
                            Toast.LENGTH_LONG).show();}
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
