package com.example.adam.checkersproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Display display;
    private TableLayout mainBoardLayout;
    private ConstraintLayout con;

    private int screenWidth;
    private int screenHeight;

    private int currentPieceId = 0;
    private int currentPieceX = 0;
    private int currentPieceY = 0;

    Drawable whiteOnSquareImg;
    Drawable blackOnSquareImg;
    Drawable whiteDameOnSquareImg;
    Drawable blackDameOnSquareImg;
    Drawable whiteOnSquareImgFocused;
    Drawable blackOnSquareImgFocused;
    Drawable whiteDameOnSquareImgFocused;
    Drawable blackDameOnSquareImgFocused;
    Drawable darkSquareInRedFrame;

    List<ImageButton> imgList = new ArrayList<ImageButton>();

    Checkers checkers = new Checkers();

    public int gameDifficulty = 1;
    public int gameMode = 1;

    public Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, int xShift, int yShift) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, xShift, yShift, null);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Checkers by Adam Ziolecki");

        mainBoardLayout = (TableLayout) findViewById(R.id.boardLayout);
        con = (ConstraintLayout) findViewById(R.id.con);
        con.setBackgroundResource(R.drawable.background);
        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // programmatically

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x - (size.x % 8);
        screenHeight = size.y - (size.y % 8);

        Bitmap darkSquareImg = BitmapFactory.decodeResource(getResources(), R.drawable.dark_square);
        Bitmap whitePieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece);
        Bitmap blackPieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece);
        Bitmap whiteDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_dame);
        Bitmap blackDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_dame);
        Bitmap redFrame = BitmapFactory.decodeResource(getResources(), R.drawable.red_frame);
        whiteOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whitePieceImg, 25, 25));
        blackOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackPieceImg, 25, 25));
        whiteDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whiteDameImg, 25, 25));
        blackDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackDameImg, 25, 25));
        darkSquareInRedFrame = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, redFrame, 0, 0));

        whiteOnSquareImgFocused = whiteOnSquareImg.getConstantState().newDrawable().mutate();
        whiteOnSquareImgFocused.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        blackOnSquareImgFocused = blackOnSquareImg.getConstantState().newDrawable().mutate();
        blackOnSquareImgFocused.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        whiteDameOnSquareImgFocused = whiteDameOnSquareImg.getConstantState().newDrawable().mutate();
        whiteDameOnSquareImgFocused.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        blackDameOnSquareImgFocused = blackDameOnSquareImg.getConstantState().newDrawable().mutate();
        blackDameOnSquareImgFocused.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);

        startGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucontext, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newGame:
                checkers.newGame();
                onClick(imgList.get(0));
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("playAs", checkers.playerColor);
                intent.putExtra("gameMode", checkers.mode);
                intent.putExtra("gameDifficulty", checkers.difficulty);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    checkers.playerColor = data.getIntExtra("playAs", 1);
                    checkers.mode = data.getIntExtra("gameMode", 1);
                    checkers.difficulty = data.getIntExtra("gameDifficulty", 1);
                    if (checkers.playerColor == 2) {
                        mainBoardLayout.setRotation(180);
                    }
                    else {
                        mainBoardLayout.setRotation(0);
                    }
                    checkers.newGame();
                    onClick(imgList.get(0));
                }
                break;
            }
        }
    }

    public void startGame() {
        for (int i = 0; i < 8; i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            for (int j = 0; j < 8; j++) {
                ImageButton imgButton = new ImageButton(this);
                imgButton.setLayoutParams(new TableRow.LayoutParams(screenWidth / 8, screenWidth / 8));
                imgButton.setId(i * 8 + j);
                imgButton.setOnClickListener(this);

                if (checkers.board[i][j] == -1) {
                    imgButton.setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgButton.setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgButton.setBackground(whiteOnSquareImg);
                }
                else
                {
                    imgButton.setBackground(blackOnSquareImg);
                }

                tableRow.addView(imgButton);
                imgList.add(imgButton);
            }

            mainBoardLayout.addView(tableRow, i);
        }
        onClick(imgList.get(0));
    }

    public void onClick(final View v) {
        if (checkers.mode == 2) {
            new Thread(new Runnable() {
                public void run() {
                    // a potentially time consuming task
                    checkers.run();
                    imgList.get(1).post(new Runnable() {
                        public void run() {
                            System.out.println("Click!");
                            if (currentPieceId == 0 || checkers.board[((int) v.getId()) / 8][((int) v.getId()) % 8] != 0) {
                                try {
                                    for (int i = 0; i < checkers.bestCaptures.size(); ++i) {
                                        if ((((int) v.getId()) / 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 2))) && (((int) v.getId()) % 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 1)))) {
                                            checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                                        }
                                    }
                                } catch (NullPointerException ex) {
                                }
                                currentPieceId = v.getId();
                                currentPieceX = currentPieceId / 8;
                                currentPieceY = currentPieceId % 8;
                            } else {
                                checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                                currentPieceId = 0;
                                currentPieceX = 0;
                                currentPieceY = 0;
                                System.out.println("currentPieceId = 0");
                            }
                            drawBoard();

                            if (checkers.isMaximizingTurnNow() == (checkers.playerColor == 2)) {
                                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                                onClick(imgList.get(0));
                            } else {
                                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }).start();
        } else {      // Two real players
            if (currentPieceId == 0 || checkers.board[((int) v.getId()) / 8][((int) v.getId()) % 8] != 0) {
                try {
                    for (int i = 0; i < checkers.bestCaptures.size(); ++i) {
                        if ((((int) v.getId()) / 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 2))) && (((int) v.getId()) % 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 1)))) {
                            checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                        }
                    }
                } catch (NullPointerException ex) {
                }
                currentPieceId = v.getId();
                currentPieceX = currentPieceId / 8;
                currentPieceY = currentPieceId % 8;
            } else {
                checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                currentPieceId = 0;
                currentPieceX = 0;
                currentPieceY = 0;
            }

            drawBoard();
        }
    }

    void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] == -1) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgList.get(i*8+j).setBackground(whiteOnSquareImg);
                }
                else if (checkers.board[i][j] == 2)
                {
                    imgList.get(i*8+j).setBackground(blackOnSquareImg);
                }
                else if (checkers.board[i][j] == 3)
                {
                    imgList.get(i*8+j).setBackground(whiteDameOnSquareImg);
                }
                else if (checkers.board[i][j] == 4)
                {
                    imgList.get(i*8+j).setBackground(blackDameOnSquareImg);
                }
            }
        }
        if (currentPieceId != 0) {
            if (checkers.board[currentPieceX][currentPieceY] == 1)
                imgList.get(currentPieceId).setBackground(whiteOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 2)
                imgList.get(currentPieceId).setBackground(blackOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 3)
                imgList.get(currentPieceId).setBackground(whiteDameOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 4)
                imgList.get(currentPieceId).setBackground(blackDameOnSquareImgFocused);
        }
        try {
            checkers.checkBoard();
            for (int k = 0; k < checkers.bestCaptures.size(); ++k) {
                if ((currentPieceId/8) == Character.getNumericValue(checkers.bestCaptures.get(k).charAt(0)) && (currentPieceId%8) == Character.getNumericValue(checkers.bestCaptures.get(k).charAt(1))) {
                    int x = Character.getNumericValue(checkers.bestCaptures.get(k).charAt(checkers.bestCaptures.get(k).length()-2));
                    int y = Character.getNumericValue(checkers.bestCaptures.get(k).charAt(checkers.bestCaptures.get(k).length()-1));
                    imgList.get(x*8+y).setBackground(darkSquareInRedFrame);
                }
            }
        }
        catch (Exception e){ }

        if (checkers.isEnd()) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage(checkers.winningCommunicate);
            dlgAlert.setTitle("Checkers");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

            checkers.newGame();
            drawBoard();
        }
    }
}
