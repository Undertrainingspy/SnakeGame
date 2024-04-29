package com.gamecodeschool.snakeysnakegame;

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
import android.media.SoundPool;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

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

    private GameRenderer gameRenderer;
    private final Portal mPortal;

    // Game over flag
    private boolean gameOver = false;

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
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

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
        mObstacle = new Obstacle(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mPortal = new Portal(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);

        // background bitmap
        mBackgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.level1);
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap, size.x, size.y, false);

        //pause
        mPauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        int pauseButtonSize = 100;
        mPauseBitmap = Bitmap.createScaledBitmap(mPauseBitmap, pauseButtonSize, pauseButtonSize, false);
        mPauseButtonRect = new Rect(20, 20, 20 + pauseButtonSize, 20 + pauseButtonSize);

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
        scoreForNextLevel = 5; // Initial score needed to level up

        // Ensure the game over flag is reset
        gameOver = false;

        // Setup mNextFrameTime so an update can be triggered immediately
        mNextFrameTime = System.currentTimeMillis();

        // If current level is high enough, spawn portal
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
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

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
// Update all the game objects
    public void update() {
        mSnake.move();
        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn();
            mScore += 1; // Increment score by 1 for each apple
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);

            if (mScore >= scoreForNextLevel) {
                currentLevel++; // Level up
                scoreForNextLevel += (int)(scoreForNextLevel * 0.2); // Increase requirement by 20%
                mScore = 0;

                // Check if the current level is high enough to spawn the portal
                if (currentLevel >= 2) {
                    Point portalLocation = new Point(rand.nextInt(NUM_BLOCKS_WIDE), rand.nextInt(mNumBlocksHigh));
                    mPortal.spawn(portalLocation);
                }
            }
        }

        if (mSnake.detectDeath()) {
            mPaused = true;
            gameOver = true;
            mSP.play(mCrashID, 1, 1, 0, 0, 1);
        }
    }


    // Do all the drawing
    public void draw() {
        //move to GameRenderer class
        gameRenderer.draw(mSnake, mApple, mObstacle, mPortal, mPaused, mScore, gameOver, currentLevel, scoreForNextLevel);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPauseButtonRect.contains(x, y)) {
                    // pause without reset
                    mPaused = !mPaused;
                    //resume the thread
                    if (!mPlaying) {
                        resume();
                    }
                    return true;
                } else if (mPaused) {
                    //new game
                    mPaused = false;
                    newGame();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                // Let the Snake class handle the input
                if (!mPaused) {
                    mSnake.switchHeading(motionEvent);
                }
                return true;
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