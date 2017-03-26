package com.example.memory;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Shai on 2/1/2017.
 * This class sends the new user's information to the server
 */

public class Register {

    String username, email, password, returnFromServer;
    Context registerActivityContext;

    public Register(String usernameString, String emailString, String passwordString, Context context) {
        this.username = usernameString;
        this.email = emailString;
        this.password = passwordString;
        this.registerActivityContext = context;
        new SendPostRequest().execute();
    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {


            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("username", username);
                jsonObj.put("password", password);
                jsonObj.put("email", email);

                URL url = new URL("http://10.0.2.2:8080/JsonApp/webresources/welcome1/register/");
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
                    System.out.println("\n"+line);
                }
                in.close();
                returnFromServer = sb.toString().replace("\"", "");


            } catch (Exception e) {
                returnFromServer= "Register-ERROR";
                System.out.println(e);
            }

            return new String(returnFromServer);
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(registerActivityContext, result,
                    Toast.LENGTH_LONG).show();

            if (result.equals("User was added"))
            {
              ((Activity)registerActivityContext).finish();

            }






        }



    }




}

