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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Shai on 2/22/2017.
 * This class connects to the server in order to get the high scores list
 */

public class GetHighScores
{

    String returnFromServer;
    HighScoreActivity context;
    JSONObject jsnObj, returnedJSN;


    public GetHighScores(Context context)
    {
        returnedJSN = new JSONObject();
        jsnObj = new JSONObject();
        new GetHighScores.SendPostRequest().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.context= (HighScoreActivity) context;
    }

    public class SendPostRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
        }

        protected JSONObject doInBackground(String... arg0) {


            try {
                jsnObj.put("getScores", "getScores");//for future purpose
                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome4/getScores/");
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
                String jsonData = "";
                while ((line = in.readLine()) != null) {
                    jsonData += line + "\n";

                }
                in.close();
                returnedJSN = new JSONObject(jsonData);


            } catch (Exception e) {
                returnFromServer = "errorAndroid";
                System.out.println(e);
            }

            return returnedJSN;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSN) {

            ArrayList<UserScore> unsortedList= new ArrayList<>();
            String user;
            int score;
            Iterator<?> keys = resultJSN.keys();
            boolean done=false;
            int i=1;

            for ( i=1;done==false ; i++)
            {
            try {
            user= resultJSN.getString("user"+i);
            System.out.println("VALUE:" + user );
            score= resultJSN.getInt("score"+i);
            System.out.println("VALUE:" + score );
            unsortedList.add(new UserScore(user,score));

            } catch (JSONException e) {
            e.printStackTrace();
            done=true;
            }


            }
            //sorts the list- high to low scores
            if (i>2)
            {
                 while (unsortedList.size()>=1)
                {
                    int maxScore = unsortedList.get(0).getScore();
                    int maxIndex=0;
                    UserScore maxUS= unsortedList.get(0);
                    UserScore temp;

                    for (int j = 0; j < unsortedList.size(); j++)
                    {
                        temp = unsortedList.get(j);
                        if (temp.getScore() > maxScore)
                        {
                            maxUS = temp;
                            maxScore = maxUS.getScore();
                            maxIndex = j;
                        }

                    }
                    context.scoresList.add(maxUS);
                    unsortedList.remove(maxIndex);

                }

            }
            context.adapter.notifyDataSetChanged();

            }
        }


    }
