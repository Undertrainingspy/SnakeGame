package com.gamecodeschool.snakeysnakegame;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;
import android.content.Intent;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

class SnakeGame extends SurfaceView implements Runnable {

    private Random rand = new Random();

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    // for playing sound effects
    private final SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;


    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 30;
    private final int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    private int currentLevel;
    private int scoreForNextLevel;

    private final Paint mPaint;

    // A snake ssss

    private final Snake mSnake;
    // And an apple
    private final Apple mApple;
    private final Obstacle mObstacle; // Declare the Obstacle

    private Bitmap mBackgroundBitmap; // hold background image
    private Bitmap mPauseBitmap; // Bitmap for the pause button
    private final Rect mPauseButtonRect; //touch detection
    private final SurfaceHolder mSurfaceHolder;

    private Bitmap mAbilityOneBitmap;  //ability
    private final Rect mAbilityOneRect;

    private boolean cd_one = true;
    private Bitmap mAbilityTwoBitmap;
    private final Rect mAbilityTwoRect;
    private boolean cd_two = true;


    private GameRenderer gameRenderer;
    private final Portal mPortal;

    // Game over flag
    private boolean gameOver = false;

    private int map = R.drawable.level1;

    // Goblin obstacle timers
    private long prevGoblinMoveTimer;
    private long currentTimer;
    private static long GOBLIN_MOVE_DELAY = 1000;
    private long goblinSpeedTimer = 0;
    private Point mAppleLocation;

    // Run at 10 frames per second
    private long TARGET_FPS = 10;
    // There are 1000 milliseconds in a second
    final long MILLIS_PER_SECOND = 1000;

    private int lives = 3;
    private long lastAbilityUseTime = 0;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);
        mSurfaceHolder = getHolder();

        gameRenderer = new GameRenderer(context, mSurfaceHolder, size, 100);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("Minecraft_Eating.ogg");
            mEat_ID = mSP.load(descriptor, 1);

            descriptor = assetManager.openFd("explosion.ogg");
            mCrashID = mSP.load(descriptor, 1);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);
        mAppleLocation = mApple.getLocation();
        mObstacle = new Obstacle(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize, mAppleLocation);
        mPortal = new Portal(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);

        // background bitmap
        mBackgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.level1);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, size.x, size.y, false);

        //pause
        mPauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        int pauseButtonSize = 100;
        mPauseBitmap = Bitmap.createScaledBitmap(mPauseBitmap, pauseButtonSize, pauseButtonSize, false);
        mPauseButtonRect = new Rect(20, 20, 20 + pauseButtonSize, 20 + pauseButtonSize);


        int abilityOneSize = 100;
        mAbilityOneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ability_one);
        mAbilityOneBitmap = Bitmap.createScaledBitmap(mAbilityOneBitmap, abilityOneSize, abilityOneSize, false);
        mAbilityOneRect = new Rect(20, 150, 20 + abilityOneSize, 150 + abilityOneSize);


        int abilityTwoSize = 100;
        mAbilityTwoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ability_two);
        mAbilityTwoBitmap = Bitmap.createScaledBitmap(mAbilityTwoBitmap, abilityTwoSize, abilityTwoSize, false);
        mAbilityTwoRect = new Rect(20, 300, 20 + abilityTwoSize, 300 + abilityTwoSize);

    }


    // Called to start a new game
    public void newGame() {
        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple and obstacles ready for the game
        mApple.spawn();
        mObstacle.spawn();

        // Reset the score and level details
        mScore = 0;
        currentLevel = 1;
        scoreForNextLevel = 5; // initial exp
        goblinSpeedTimer = 0;
        GOBLIN_MOVE_DELAY = 1000;
        lives=3;

        gameOver = false;
        map = R.drawable.level1;
        updateBackground();
        // Setup mNextFrameTime so an update can be triggered immediately
        mNextFrameTime = System.currentTimeMillis();

        if (currentLevel >= 2) {
            Point portalLocation = new Point(rand.nextInt(NUM_BLOCKS_WIDE), rand.nextInt(mNumBlocksHigh));
            mPortal.spawn(portalLocation);
        } else {
            mPortal.deactivate();
        }
    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
            goblinSpeedTimer += System.currentTimeMillis() - currentTimer;

            // these handle the timer for the goblin to move speed so long as the game runs
            currentTimer = System.currentTimeMillis();
            if (currentTimer - prevGoblinMoveTimer >= GOBLIN_MOVE_DELAY && !mPaused) {
                mObstacle.move();
                prevGoblinMoveTimer = currentTimer;

                if (goblinSpeedTimer >= 1000 && GOBLIN_MOVE_DELAY > 75) {
                    GOBLIN_MOVE_DELAY -= 50;
                    goblinSpeedTimer = 0;
                }
            }
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {


        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update() {
        mSnake.move();

        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn();
            mSP.play(mEat_ID, 1, 1, 1, 0, 1);
            mScore += 1;

            if (mScore >= scoreForNextLevel) {
                levelUp();
            }
        }

        if (mPortal.getLocation().x != -1 && mPortal.getLocation().y != -1) {
            if (mSnake.checkCollision(mPortal.getLocation())) {
                updateBackground(); // update background
                mPortal.deactivate();
                scoreForNextLevel += (int)(scoreForNextLevel * 0.2);
                mScore = 0;
                goblinSpeedTimer = 0;
                GOBLIN_MOVE_DELAY = 1000;
                if (currentLevel==4)
                {
                    pauseGameAndShowEndingScene();
                    mPaused = !mPaused;

                    if (mPaused) {
                        pause();
                    }

                }

            }
        }

        // check if the snake collided with goblins
        if (mSnake.checkGoblin((mObstacle.getLocation()))) {
            mObstacle.spawn();

            mScore = mScore - 1;
            lives--;
            mSP.play(mCrashID,1,1,1,0,1);
        }

        if (mSnake.detectDeath() || mScore < 0 || lives <=0) {
            gameOver = true;
            mPaused = true;
            mSP.play(mCrashID, 1, 1, 1, 0, 1);
            if (gameOver) {
                Intent gameOverIntent = new Intent(getContext(), game_over_screen.class);
                getContext().startActivity(gameOverIntent);
                newGame();

            }

        }

    }
    private void pauseGameAndShowEndingScene() {
        fadeToEndingScene();
    }
    private void fadeToEndingScene() {
        final ImageView endingScene = new ImageView(getContext());
        endingScene.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        endingScene.setScaleType(ImageView.ScaleType.CENTER_CROP);
        endingScene.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.endingscene));

        ((Activity) getContext()).runOnUiThread(() -> {
            ((FrameLayout) getParent()).addView(endingScene);
            Animation fade = new AlphaAnimation(0, 1);
            fade.setInterpolator(new DecelerateInterpolator());
            fade.setDuration(3000);
            endingScene.startAnimation(fade);
        });
    }

    private void levelUp() {
        currentLevel++;
        scoreForNextLevel += (int)(scoreForNextLevel * 0.2);
        mScore = 0;

        // Check if the current level should have a portal
        if (currentLevel >= 2) {
            Point portalLocation = new Point(rand.nextInt(NUM_BLOCKS_WIDE), rand.nextInt(mNumBlocksHigh));
            mPortal.spawn(portalLocation);
        } else {
            mPortal.deactivate();
        }
    }

    private void updateBackground() {
        switch (currentLevel) {
            case 2:
                map = R.drawable.level2;
                break;
            case 3:
                map = R.drawable.level3;
                break;
        }
        mBackgroundBitmap = BitmapFactory.decodeResource(getContext().getResources(), map);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, mSurfaceHolder.getSurfaceFrame().width(), mSurfaceHolder.getSurfaceFrame().height(), false);
        gameRenderer.setBackgroundBitmap(mBackgroundBitmap);
    }

    // Do all the drawing
    public void draw() {
        //move to GameRenderer class
        gameRenderer.draw(mSnake, mApple, mObstacle, mPortal, mPaused, mScore, gameOver, currentLevel, scoreForNextLevel, cd_one, cd_two, lives);

    }
    private void changeTargetFPS(int newTargetFPS) {
        TARGET_FPS = newTargetFPS;
        updateRequired();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPauseButtonRect.contains(x, y)) {
                    mPaused = !mPaused;
                    return true;
                } else if (mAbilityOneRect.contains(x, y)) {
                    cd_one=false;
                    changeTargetFPS(5);
                    new Handler().postDelayed(() -> {

                        changeTargetFPS(10);
                            cd_one = true;

                    }, 10000);

                    return true;
                }
                else if (mAbilityTwoRect.contains(x, y)) {
                    cd_two=false;
                    lives++;
                    new Handler().postDelayed(() -> cd_two = true, 10000);

                    return true;
                }
            break;


            case MotionEvent.ACTION_UP:
                if (!mPauseButtonRect.contains(x, y)&&!mAbilityOneRect.contains(x, y)&&!mAbilityTwoRect.contains(x, y)) {
                    if (mPaused) {
                        // if paused and release is outside the pause button, start a new game
                        mPaused = false;
                        newGame();
                    } else {
                        mSnake.switchHeading(motionEvent);
                    }
                    return true;
                }
                break;
        }
        return true;
    }
    @Override //for keyboard
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    mSnake.changeDirection(Snake.Heading.UP);
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mSnake.changeDirection(Snake.Heading.DOWN);
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    mSnake.changeDirection(Snake.Heading.LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    mSnake.changeDirection(Snake.Heading.RIGHT);
                    break;

                case KeyEvent.KEYCODE_SPACE :
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // Toggle the pause state
                        mPaused = !mPaused;

                        // Optionally, handle the game's running state
                        if (!mPlaying && !mPaused) {
                            resume();
                        } else if (mPaused) {
                            pause();
                        }

                        return true; // Key event handled

                    }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}