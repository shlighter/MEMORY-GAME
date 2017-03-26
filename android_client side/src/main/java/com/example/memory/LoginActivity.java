package com.example.memory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *Login Activity. This Activity displays a login form in order to start the game, it also contains register button.
 * @author Shai
 */

public class LoginActivity extends AppCompatActivity {

    LoginCheck login;
    EditText name, password;
    boolean buttonClicked;
    String buttonText;
    Button findPlrButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.editTextName);
        password = (EditText) findViewById(R.id.editTextPassword);
        findPlrButton= (Button) findViewById(R.id.findPlayerBtn);
        buttonClicked=false;
        buttonText= findPlrButton.getText().toString();
    }

    public void register(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {

        if (buttonClicked==false)
        {
            String nameString = this.name.getText().toString();
            String passwordString = this.password.getText().toString();
            findPlrButton.setText("WAIT. Checking your information...");
            login = new LoginCheck(nameString, passwordString, LoginActivity.this);
        }
        buttonClicked=true;

    }

    public void setButtonClicked (boolean b)
    {
        buttonClicked=b;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        done();

    }

   public void done(){
        setButtonClicked(false);
        findPlrButton.setText(buttonText);
    }
}
