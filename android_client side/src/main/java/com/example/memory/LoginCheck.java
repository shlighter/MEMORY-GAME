package com.example.memory;

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
 * Created by Shai
 * This class sends the user's login info to the server and verify that the info is correct and the user exists.
 */

public class LoginCheck {

    String username, password, returnFromServer;
    LoginActivity loginActivityContext;

 public LoginCheck(String usernameString, String passwordString, Context context) {
        this.username = usernameString;
        this.password = passwordString;
        this.loginActivityContext = (LoginActivity) context;
        new SendPostRequest().execute();
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("username", username);
                jsonObj.put("password", password);
                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome2/login/");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonObj.toString());
                out.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                in.close();
                returnFromServer = sb.toString();


            } catch (Exception e) {
                returnFromServer= "error";
                System.out.println(e);
            }


            return new String(returnFromServer);
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(loginActivityContext, result,
                    Toast.LENGTH_LONG).show();

            if (result.equals("Success"))
            {
                Intent intent = new Intent(loginActivityContext, WaitingForPlayerActivity.class);
                intent.putExtra("username", username);
                loginActivityContext.startActivity(intent);
            }

            else
            {
                loginActivityContext.done();
            }




        }



    }



}

