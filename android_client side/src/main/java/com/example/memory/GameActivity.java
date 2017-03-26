package com.example.memory;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Shai on 2/17/2017.
 * This is the main game's Activity. It loads the game's board, and sets actions on board by the user and the other player
 */

public class GameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{

    final int NUMBER_OF_IMAGES= 46;
    final int NUMBER_OF_COLUMNS=4;
    final int NUMBER_OF_ROWS=5;
    final int NUMBER_OF_SQUARES= NUMBER_OF_COLUMNS *NUMBER_OF_ROWS;
    final int POINTS_WHEN_MATCH=5;  //The player will get this number of points when finds matching cards.
    final int MAX_POINTS= ((NUMBER_OF_SQUARES/2)*POINTS_WHEN_MATCH); //The sum of players points when the game is over
    int[] imageArr;
    GridView gridView;
    ArrayAdapter<Square> adapter;
    ArrayList<Square> list;

    String username;
    int gameID,scoresPlr1,scoresPlr2;
    Game game; // This object connects to the server to get the game's board
    SendMoves sendMoves; //This object sends the server the user's actions (clicks)
    AnimatorSet setRightOut; //Cards flipping's animation
    AnimatorSet setLeftIn;
    int flippedCards=0; //0= no flipped cards, 1= one flipped cards, waiting for the second, 2=maximum flipped cards
    Square firstCard, secondCard;
    TextView ply1TxtView,ply2TxtView,scoresPlr1View,scoresPlr2View;
    ImageView turnLeft, turnRight;
    int positionFirstCard,positionSecondCard;
    Random rand;
    boolean[] usedPicture;
    boolean[] gridRefresh;//To makes sure the gridview's adapter checks every cell only once
    boolean myTurn;
    String firstPlayer,secondPlayer;
    JSONObject jsnMovesToSend;
    GetMoves getMoves;//Object that gets the actions of the other player from the server
    WaitingForMovesToSend  wtngForMvsToSnd;//Object that counts down and tells the server if the user skipped its turn
    int myScores, otherScores;
    UpdateScores updateScores;
    Button newGameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        scoresPlr1=0;
        scoresPlr2=0;
        myTurn=false;
        otherScores=0;
        myScores=0;
        firstCard= new Square();
        secondCard= new Square();
        turnRight= (ImageView) findViewById(R.id.right);
        turnLeft= (ImageView) findViewById(R.id.left);
        ply1TxtView = (TextView) findViewById(R.id.player1);
        ply2TxtView = (TextView) findViewById(R.id.player2);
        scoresPlr1View = (TextView) findViewById(R.id.scoresPlr1);
        scoresPlr2View = (TextView) findViewById(R.id.scoresPlr2);
        gridView = (GridView) findViewById(R.id.gridView);
        newGameButton= (Button) findViewById(R.id.startNewGame);
        imageArr = new int[NUMBER_OF_IMAGES ];
        list = new ArrayList<>();
        rand = new Random();
        Intent intent= getIntent();
        usedPicture = new boolean[NUMBER_OF_IMAGES];
        username =intent.getStringExtra("username");
        gameID = intent.getIntExtra("gameID",0);
        sendMoves = new SendMoves(username,gameID,GameActivity.this);
        getMoves = new GetMoves(username,gameID,GameActivity.this);
        wtngForMvsToSnd = new WaitingForMovesToSend(username,gameID,GameActivity.this);
        jsnMovesToSend= new JSONObject();
        gridRefresh= new boolean[NUMBER_OF_SQUARES];
        setFalse();// sets every index of the gridRefresh's array to false
         setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                R.animator.flip_right_out);
          setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                R.animator.flight_left_in);

        for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
            imageArr[i] = this.getResources().getIdentifier("p"+String.valueOf(i+1), "drawable", this.getPackageName());
        }

        game= new Game(username,gameID, GameActivity.this);
        setFalse();
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_game);
    }

    //Loads the game's board by getting a json object from Game
    void loadGame(JSONObject jsn)
    {
        int picNum=0;
        for (int i=0; i<NUMBER_OF_SQUARES; i++)
        {
           Square sq1 = new Square();
           sq1.selected=0;
           sq1.match=false;
           try {
               System.out.println("before get");
               firstPlayer= (String) jsn.get("player1");
               secondPlayer= (String) jsn.get("player2");
               jsnMovesToSend.put("firstPlayer", firstPlayer);//default parameters
               jsnMovesToSend.put("secondPlayer", secondPlayer);//default parameters
               jsnMovesToSend.put("gameID", gameID);//default parameters
               picNum= (int) jsn.get("sq"+(i+1));
               sq1.id= imageArr[picNum];
               myTurn= (boolean) jsn.get("yourTurn");
               ply1TxtView.setText(firstPlayer);
               ply2TxtView.setText(secondPlayer);
               } catch (JSONException e)
                 {
                 e.printStackTrace();
                 onStop();
                 }
           list.add(sq1);
        }
           wtngForMvsToSnd.setFirstPlayer(firstPlayer);
           wtngForMvsToSnd.setSecondPlayer(secondPlayer);
           adapter = new CustomAdapter(GameActivity.this,list);
           gridView.setAdapter(adapter);
           gridView.setOnItemClickListener(this);
           adapter.notifyDataSetChanged();
           setFalse();
           if (myTurn==false)
           { //The other user starts the game, get his actions
               getMoves.startGettingMoves();
               getMoves.setMove(1);// The "move's number" will be increased by one with each turn
           }
           else
           {
               wtngForMvsToSnd.setMove(1);
               wtngForMvsToSnd.startWaitingForMovesToSend();
           }

       }


    @Override
    protected void onStop()
    {
        super.onStop();
        getMoves.stop();
        wtngForMvsToSnd.stop();
        finish();
    }

    public void changeArrowDirection()
    {
        if (turnLeft.getVisibility() == View.VISIBLE)
        {
            turnLeft.setVisibility(View.INVISIBLE);
            turnRight.setVisibility(View.VISIBLE);

        }
        else
        {
            turnRight.setVisibility(View.INVISIBLE);
            turnLeft.setVisibility(View.VISIBLE);
        }
    }

    //sets every index of the gridRefresh's array to false
     void setFalse()
     {
        for (int i=0; i<gridRefresh.length; i++)
         this.gridRefresh[i]=false;
     }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (view!=newGameButton)
        {
            if (myTurn)
                makeAmove(position);
        }
    }

    //Makes a change on the board by the user or the other player
     void makeAmove(int position)
     {
        Square s1;
        s1= list.get(position);
        if ((s1.selected!=1)&&(s1.selected!=2)&&(s1.selected!=3))
            if (true)
            {
                //First card to flip (of 2)
                if (flippedCards==0)
                {    try
                    {
                        if (myTurn)
                        {
                            jsnMovesToSend.put("firstCard",position);
                            jsnMovesToSend.put("firstCardPic",list.get(position).id);
                            jsnMovesToSend.put("position",position);
                            jsnMovesToSend.put("sendingPlayer", username);
                            jsnMovesToSend.put("flippedCards", 1);
                            sendMoves.setMoves(wtngForMvsToSnd.getMoveNum());
                            sendMoves.sendMovesToServer(jsnMovesToSend);
                        }
                    }
                     catch (JSONException e)
                     {
                      e.printStackTrace();
                     }

                    s1.selected=1;
                    firstCard=s1;
                    positionFirstCard=position;
                    list.set(position, s1);
                    adapter.notifyDataSetChanged();
                    setFalse();
                    flippedCards=1;
                }

                else if (flippedCards==1)
                {
                    try
                    {
                        if (myTurn)
                        {
                            jsnMovesToSend.put("secondCard",position);
                            jsnMovesToSend.put("sendingPlayer", username);
                            jsnMovesToSend.put("secondCardPic",list.get(position).id);
                            jsnMovesToSend.put("flippedCards", 2);
                            jsnMovesToSend.put("position",position);
                            sendMoves.sendMovesToServer(jsnMovesToSend);
                        }

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    secondCard = s1;
                    s1.selected = 1;
                    flippedCards = -1;
                    positionSecondCard = position;
                    list.set(position, s1);
                    adapter.notifyDataSetChanged();
                    setFalse();

                    new Handler()
                            .postDelayed(new Runnable() {
                                public void run() {

                                    //Match-  first flipped card = second flipped card
                                    if (secondCard.id == firstCard.id)
                                    {
                                        firstCard.match = true;
                                        secondCard.match = true;
                                        secondCard.selected = 2;
                                        firstCard.selected = 2;
                                        String otherPlayer;

                                        if (myTurn)
                                                 {
                                                    myScores= myScores+ POINTS_WHEN_MATCH;
                                                    if (firstPlayer.equals(username))
                                                    {
                                                         scoresPlr1View.setText(Integer.toString(myScores));
                                                         otherPlayer=secondPlayer;
                                                    }
                                                    else
                                                    { scoresPlr2View.setText(Integer.toString(myScores));
                                                      otherPlayer=firstPlayer;
                                                    }

                                                 }
                                        else
                                               {
                                                   otherScores=  otherScores + POINTS_WHEN_MATCH;
                                                   if (firstPlayer.equals(username))
                                                      {
                                                          otherPlayer=secondPlayer;
                                                          scoresPlr2View.setText(Integer.toString(otherScores));
                                                      }
                                                   else {scoresPlr1View.setText(Integer.toString(otherScores));otherPlayer=firstPlayer;}
                                               }

                                        //Maximum points. Game is over
                                        if ((myScores+otherScores)== MAX_POINTS)
                                        {
                                            if (myScores>otherScores)
                                            {
                                                Toast.makeText(GameActivity.this,"YOU WON!!!",
                                                        Toast.LENGTH_LONG).show();
                                                updateScores= new UpdateScores(username,gameID,GameActivity.this,username,myScores);
                                            }

                                            if (myScores<otherScores) {
                                                Toast.makeText(GameActivity.this, "YOU LOST!!!",
                                                        Toast.LENGTH_LONG).show();
                                            }

                                            if (myScores==otherScores) {
                                                Toast.makeText(GameActivity.this, "TIE!!!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            onStop();
                                        }

                                    }
                                    else
                                    {
                                        secondCard.selected = 3;
                                        firstCard.selected = 3;
                                    }
                                    list.set(positionFirstCard, firstCard);
                                    list.set(positionSecondCard, secondCard);
                                    adapter.notifyDataSetChanged();setFalse();
                                    flippedCards = 0;
                                }
                            }, 1000);


                }
        }

    }

    //Button for starting a new game
    public void startNewGame(View view)
    {
        onStop();
    }

 }

