package com.example.memory;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Shai on 2/8/2017.
 */





    import android.app.Activity;
    import android.content.Context;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.util.Log;
    import android.widget.Toast;

    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    import java.io.OutputStreamWriter;
    import java.net.URL;
    import java.net.URLConnection;
    import java.net.URLEncoder;
    import java.util.Iterator;

    /**
     * Created by Shai on 2/1/2017.
     * This class connects to the server and waits until a second player was found
     */

    public class Waiting {

        String username;
        int gameID,returnFromServer;
        Context waitingActivityContext;


        public Waiting(String usernameString,Context context) {
            this.username = usernameString;
            this.waitingActivityContext = context;
            new SendPostRequest().execute();
            gameID=0;
        }

        public class SendPostRequest extends AsyncTask<String, Void, Integer> {

            protected void onPreExecute(){}

            protected Integer doInBackground(String... arg0) {


                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("username", username);
                    URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome/waiting/");
                    URLConnection connection = url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setConnectTimeout(0);
                    connection.setReadTimeout(0);
                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                    out.write(jsonObj.toString());
                    out.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        System.out.println("\n"+line);
                    }
                    in.close();
                   returnFromServer= Integer.parseInt(sb.toString());

                } catch (Exception e) {
                    System.out.println("\nError while calling REST Service");
                    returnFromServer= 0;
                    ((Activity)waitingActivityContext).finish();
                    System.out.println(e);
                }

                return new Integer(returnFromServer);
            }

            @Override
            protected void onPostExecute(Integer result) {

                //result= game's ID
                if (result>0)
                {
                   Intent intent = new Intent(waitingActivityContext, GameActivity.class);
                   intent.putExtra("username", username);
                   intent.putExtra("gameID", result);
                   waitingActivityContext.startActivity(intent); //start game
                 }

                else
                {  //error
                    Toast.makeText(waitingActivityContext, result,
                            Toast.LENGTH_LONG).show();

                }
                ((Activity)waitingActivityContext).finish();
            }

        }

    }




