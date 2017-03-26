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
 * Created by Shai on 2/21/2017.
 * This class connects to the sever in order to update new high scores
 */

public class UpdateScores {

    String username, returnFromServer,winner;
    GameActivity gameActivityContext;
    int gameID,scores;
    JSONObject returnedJSN;
    JSONObject jsnObj;

 public UpdateScores(String username, int gameID, Context context, String winner, int scores) {

        this.gameActivityContext = (GameActivity)context;
        this.username = username;
        this.gameID = gameID;
        jsnObj= new JSONObject();
        this.winner= winner;
        this.scores=scores;
        new UpdateScores.SendPostRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){}

        protected JSONObject doInBackground(String... arg0) {


            try {
                jsnObj.put("winner",winner);
                jsnObj.put("username",username);
                jsnObj.put("gameID",gameID);
                jsnObj.put("scores",scores);

                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome3/updateScores/");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsnObj.toString());
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                String jsonData= "";
                while ((line = in.readLine()) != null) {
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
        protected void onPostExecute(JSONObject resultJSN) {


        }



    }






}
