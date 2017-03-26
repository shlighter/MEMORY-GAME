package com.example.memory;

/**
 * Created by Shai on 2/21/2017.
 * This class represents an UserScore object to be shown in the high scores list (username and scores).
 */

public class UserScore {

    String name;
    int score;

    public UserScore(String name, int score) {

        this.name=name;
        this.score=score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getScore() {

        return score;
    }

    public void setScore(int score) {

        this.score = score;
    }
}
