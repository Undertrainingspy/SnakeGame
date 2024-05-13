package com.gamecodeschool.snakeysnakegame;

import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;

public class SnakeActivity extends Activity {

    // Declare an instance of SnakeGame
    SnakeGame mSnakeGame;

    //going to use mediaplayer instead of soundpool for game music
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeGame class
        mSnakeGame = new SnakeGame(this, size);

        // Make SnakeGame the view of the Activity
        setContentView(mSnakeGame);

        //assign media player the audio file for game play
        mediaPlayer = MediaPlayer.create(this, R.raw.game_music);

        //mediaplayer looping enabled
        mediaPlayer.setLooping(true);
    }


    // Start the thread in snakeGame

    @Override
    protected void onResume() {
        super.onResume();
        mSnakeGame.resume();
        mediaPlayer.start();
    }
    // Start the thread in snakeGame

    @Override
    protected void onPause() {
        super.onPause();
        mSnakeGame.pause();
        mediaPlayer.pause();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Pass the event to the mSnakeGame instance
        if (mSnakeGame != null) {
            return mSnakeGame.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    //freeup resources availble to implement when needed
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

}
