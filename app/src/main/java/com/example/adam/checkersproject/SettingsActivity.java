package com.example.adam.checkersproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    public int currentPlayerColor = 1;
    public int currentGameMode = 1;
    public int currentGameDifficulty = 1;

    private int textSize = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Checkers by Adam Ziolecki");

        currentPlayerColor = getIntent().getIntExtra("playAs", 1);
        currentGameMode = getIntent().getIntExtra("gameMode", 1);
        currentGameDifficulty = getIntent().getIntExtra("gameDifficulty", 1);

        System.out.println("CurrentGameDifficulty: " + currentGameDifficulty);
        System.out.println("CurrentGameMode: " + currentGameMode);

        ConstraintLayout con = (ConstraintLayout) findViewById(R.id.con);
        con.setBackgroundResource(R.drawable.background);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        final TextView playAsTextView = new TextView(this);
        playAsTextView.setText("Play as");
        playAsTextView.setTextSize(textSize);
        playAsTextView.setTextColor(Color.WHITE);

        RadioButton playAsWhiteRadioButton = new RadioButton(this);
        playAsWhiteRadioButton.setId(1);
        playAsWhiteRadioButton.setText("White");
        playAsWhiteRadioButton.setTextSize(textSize);
        playAsWhiteRadioButton.setTextColor(Color.WHITE);

        RadioButton playAsBlackRadioButton = new RadioButton(this);
        playAsBlackRadioButton.setId(2);
        playAsBlackRadioButton.setText("Black");
        playAsBlackRadioButton.setTextSize(textSize);
        playAsBlackRadioButton.setTextColor(Color.WHITE);

        final RadioGroup playAsRadioGroup = new RadioGroup(this);
        playAsRadioGroup.addView(playAsWhiteRadioButton);
        playAsRadioGroup.addView(playAsBlackRadioButton);
        playAsRadioGroup.check(currentPlayerColor);

        final TextView difficultyTextView = new TextView(this);
        difficultyTextView.setText("Difficulty");
        difficultyTextView.setTextSize(textSize);
        difficultyTextView.setTextColor(Color.WHITE);

        final RadioButton easyRadio = new RadioButton(this);
        easyRadio.setId(1);
        easyRadio.setText("Very Easy");
        easyRadio.setTextSize(textSize);
        easyRadio.setTextColor(Color.WHITE);
        final RadioButton mediumRadio = new RadioButton(this);
        mediumRadio.setId(2);
        mediumRadio.setText("Easy");
        mediumRadio.setTextSize(textSize);
        mediumRadio.setTextColor(Color.WHITE);
        final RadioButton hardRadio = new RadioButton(this);
        hardRadio.setId(3);
        hardRadio.setText("Medium");
        hardRadio.setTextSize(textSize);
        hardRadio.setTextColor(Color.WHITE);
        final RadioButton veryHardRadio = new RadioButton(this);
        veryHardRadio.setId(4);
        veryHardRadio.setText("Hard");
        veryHardRadio.setTextSize(textSize);
        veryHardRadio.setTextColor(Color.WHITE);
        veryHardRadio.setDrawingCacheBackgroundColor(Color.WHITE);

        if (currentGameMode == 1) {
            easyRadio.setEnabled(false);
            mediumRadio.setEnabled(false);
            hardRadio.setEnabled(false);
            veryHardRadio.setEnabled(false);
        }

        final RadioGroup difficultyRadioGroup = new RadioGroup(this);
        difficultyRadioGroup.addView(easyRadio);
        difficultyRadioGroup.addView(mediumRadio);
        difficultyRadioGroup.addView(hardRadio);
        difficultyRadioGroup.addView(veryHardRadio);
        difficultyRadioGroup.check(currentGameDifficulty);

        TextView modeTextView = new TextView(this);
        modeTextView.setText("Mode");
        modeTextView.setTextSize(textSize);
        modeTextView.setTextColor(Color.WHITE);

        RadioButton vsPlayer = new RadioButton(this);
        vsPlayer.setId(1);
        vsPlayer.setText("player vs player");
        vsPlayer.setTextSize(textSize);
        vsPlayer.setTextColor(Color.WHITE);
        RadioButton vsComputer = new RadioButton(this);
        vsComputer.setId(2);
        vsComputer.setText("player vs AI");
        vsComputer.setTextSize(textSize);
        vsComputer.setTextColor(Color.WHITE);

        final RadioGroup modeRadioGroup = new RadioGroup(this);
        modeRadioGroup.addView(vsPlayer);
        modeRadioGroup.addView(vsComputer);
        modeRadioGroup.check(currentGameMode);
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == 1) {
                    easyRadio.setEnabled(false);
                    mediumRadio.setEnabled(false);
                    hardRadio.setEnabled(false);
                    veryHardRadio.setEnabled(false);
                }
                else {
                    easyRadio.setEnabled(true);
                    mediumRadio.setEnabled(true);
                    hardRadio.setEnabled(true);
                    veryHardRadio.setEnabled(true);
                }
            }
        });


        layout.addView(playAsTextView, 0);
        layout.addView(playAsRadioGroup, 1);
        layout.addView(modeTextView, 2);
        layout.addView(modeRadioGroup, 3);
        layout.addView(difficultyTextView, 4);
        layout.addView(difficultyRadioGroup ,5);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int screenWidth = size.x - (size.x % 8);
        int screenHeight = size.y - (size.y % 8);

        con.setPadding(screenWidth / 10, 100, screenWidth / 10, 0);

        Button applySettingsButton = new Button(this);
        applySettingsButton.setText("Apply");
        applySettingsButton.setWidth(1000);
        applySettingsButton.setHeight(200);
        applySettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentPlayerColor = playAsRadioGroup.getCheckedRadioButtonId();
                currentGameMode = modeRadioGroup.getCheckedRadioButtonId();
                currentGameDifficulty = difficultyRadioGroup.getCheckedRadioButtonId();
                Intent output = new Intent();
                output.putExtra("playAs", currentPlayerColor);
                output.putExtra("gameMode", currentGameMode);
                output.putExtra("gameDifficulty", currentGameDifficulty);
                setResult(RESULT_OK, output);
                finish();
            }
        });

        layout.addView(applySettingsButton, 6);
    }
}
