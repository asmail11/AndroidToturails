package com.drmas.issam.quizzfrancais;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ReligionActivity extends AppCompatActivity {

    private Toolbar ReligionToolbar;
    private Button textCounterDownReligion;

    private ReligionQuestions religionQuestions = new ReligionQuestions();

    private TextView mQuestionViewReligion;
    private Button mButtonChoice1Religion, mButtonChoice2Religion;
    private Button mButtonChoice3Religion, mButtonChoice4Religion;

    private TextView textCorrectCountReligion, textWrongCountReligion;
    private TextView countQuestionDownReligion;
    /// ending of main page

    private Button btnTry, btnCancel;
    private TextView textCorrectCountResult, textWrongCountResult;

    /// Start Dialog Page
    private String mAnswer;
    private int mScore = 0;
    private int mQuestionNumber = 0;

    private MediaPlayer mp, mpc, mpCount;
    private CountDownTimer countDownTimer;
    private TextView mScoreView, textResultUser;

    private int correctCountCounter = 0;
    private int wrongCountCounter = 0;
    private int questionsDown = 100;

    private int heightScore;

    public RatingBar ratingBar;
    private Dialog myDialog;

    private AdView mAdView;
    private Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_religion );

        ReligionToolbar = findViewById( R.id.Religion_Toolbar);
        setSupportActionBar( ReligionToolbar );
        setTitle( null );

        myDialog = new Dialog( this );

        textCorrectCountReligion = (TextView) findViewById( R.id.textCorrectCountReligion);
        textWrongCountReligion = (TextView) findViewById( R.id.textWrongCountReligion );
        countQuestionDownReligion = (TextView) findViewById( R.id.countQuestionDownReligion);

        textCounterDownReligion = (Button) findViewById( R.id.counterDownReligion );
        countDownTimer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textCounterDownReligion.setText(""+ millisUntilFinished/1000 );
            }

            @Override
            public void onFinish() {
                textCounterDownReligion.setText( "done" );

                countDownTimer.cancel();
                mpCount.pause();
                ShowPopup();

            }
        };

        mp = MediaPlayer.create( this, R.raw.correct );
        mpc = MediaPlayer.create( this, R.raw.incorrect );
        mpCount = MediaPlayer.create( this, R.raw.timercount );

        mQuestionViewReligion = (TextView) findViewById( R.id.textQuestionReligion );

        mButtonChoice1Religion = (Button) findViewById( R.id.ButtonChoice1Religion );
        mButtonChoice2Religion = (Button) findViewById( R.id.ButtonChoice2Religion );
        mButtonChoice3Religion = (Button) findViewById( R.id.ButtonChoice3Religion );
        mButtonChoice4Religion = (Button) findViewById( R.id.ButtonChoice4Religion );

        updateQuestions();

        mAdView = (AdView) findViewById(R.id.adViewReligion);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }

    private void ShowPopup() {

        myDialog.setContentView(R.layout.custon_resulat);
        myDialog.setCancelable( false );

        textResultUser = (TextView) myDialog.findViewById( R.id.textResultUser );

        mScoreView = (TextView) myDialog.findViewById( R.id.textScore );
        mScoreView.setText( getString( R.string.score )+" "+ mScore+ "/" +religionQuestions.getLength()+ " ");

        textCorrectCountResult = (TextView) myDialog.findViewById( R.id.textCorrectCountResult );
        textWrongCountResult = (TextView) myDialog.findViewById( R.id.textWrongCountResult );

        textCorrectCountResult.setText( String.valueOf( correctCountCounter+ " " + getString( R.string.vrai ) ));
        textWrongCountResult.setText( String.valueOf( wrongCountCounter+" "+ getString( R.string.faux )));

        btnTry = (Button) myDialog.findViewById( R.id.custom_result_try );
        btnTry.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDialog.dismiss();
                countDownTimer.start();

                if (isPlaying == false){
                    mpCount.start();
                } else {
                    mpCount.pause();
                }

            }
        });

        btnCancel = (Button) myDialog.findViewById(R.id.custom_result_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( ReligionActivity.this, HeightScore.class );
                intent.putExtra( "scoreReligion", mScore );
                startActivity( intent );
            }
        });

        if (mQuestionNumber == 100){

            btnTry = (Button) myDialog.findViewById( R.id.custom_result_try );
            btnTry.setBackgroundResource( R.drawable.finished_score_normal );
            btnTry.setText( getString( R.string.les_réponses ));
            btnTry.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAnswer();

                }
            });

            btnCancel = (Button) myDialog.findViewById(R.id.custom_result_cancel);
            btnCancel.setBackgroundResource( R.drawable.not_stared_score_normal );
            btnCancel.setText( getString( R.string.score ));
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( ReligionActivity.this, HeightScore.class );
                    intent.putExtra( "scoreReligion", mScore );
                    startActivity( intent );
                }
            });

        }

        SharedPreferences sharedPreferences = getPreferences( MODE_PRIVATE );
        heightScore = sharedPreferences.getInt( "heightScore", 0);

        if (heightScore >= mScore){

            if (heightScore == 0){

                textResultUser.setText( getString( R.string.pas_de_répondre ));

            } else {

                textResultUser.setText( getString( R.string.height_score )+ " " +heightScore );
                btnCancel = (Button) myDialog.findViewById(R.id.custom_result_cancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent( ReligionActivity.this, HeightScore.class );
                        intent.putExtra( "scoreReligion", heightScore );
                        startActivity( intent );

                    }
                });
            }
        }
        else {

            textResultUser.setText( getString( R.string.new_score )+ " "+mScore );
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt( "heightScore", mScore );
            editor.commit();

            if (mScore >= 0 && mScore == 100){

                myDialog.setContentView(R.layout.custom_new_finish);

                Button btnAnswer = (Button) myDialog.findViewById( R.id.btnNewFinishAnswer) ;
                btnAnswer.setText( getString( R.string.les_réponses ));
                btnAnswer.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showAnswer();
                    }
                } );

                Button btnFinishScore = (Button) myDialog.findViewById( R.id.btnNewFinishScore);
                btnFinishScore.setText( getString( R.string.height_score ));
                btnFinishScore.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent( ReligionActivity.this, HeightScore.class );
                        intent.putExtra( "scoreReligion", mScore );
                        startActivity( intent );
                    }
                });
            }

        }
        if (heightScore == 100){

            myDialog.setContentView(R.layout.custom_finished);

            Button btnAnswer = (Button) myDialog.findViewById( R.id.btnFinishAnswer);
            btnAnswer.setText( getString( R.string.answer ));
            btnAnswer.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAnswerWithShare();
                }
            } );

            Button btnFinishScore = (Button) myDialog.findViewById( R.id.btnFinishScore );
            btnFinishScore.setText( getString( R.string.score ));
            btnFinishScore.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( ReligionActivity.this, HeightScore.class );
                    intent.putExtra( "scoreReligion", 100 );
                    startActivity( intent );
                }
            });

            Button btnFinishCancel = (Button) myDialog.findViewById( R.id.btnFinishCancel );
            btnFinishCancel.setText( getString( R.string.accueil ));
            btnFinishCancel.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity();
                }
            });
        }

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        myDialog.show();
    }

    public void correctCount(){

        correctCountCounter++;
        textCorrectCountReligion.setText(String.valueOf( correctCountCounter ));

    }

    public void wrongCount(){

        wrongCountCounter++;
        textWrongCountReligion.setText(String.valueOf( wrongCountCounter ));

    }

    public void countQuestionDown(){
        questionsDown--;
        countQuestionDownReligion.setText( String.valueOf( questionsDown ));
    }

    public void toggleSuondReligion(View view){

        Button toggleSuondArt = (Button) findViewById(R.id.toggleSuondRepligion);

        if (isPlaying == false){
            mpCount.pause();
            toggleSuondArt.setBackgroundResource(R.mipmap.ic_volume_up);

        } else {
            mpCount.start();
            toggleSuondArt.setBackgroundResource(R.mipmap.ic_volume_on);
        }

        isPlaying = !isPlaying;
    }

    private void playSoundCounter(View view){
        mpCount.start();
    }

    private void updateQuestions(){

        // Check if we are not outside array bounds for questions
        if (mQuestionNumber<religionQuestions.getLength()){

            mQuestionViewReligion.setText( religionQuestions.getQuestions( mQuestionNumber ));

            mButtonChoice1Religion.setText( religionQuestions.getChoice( mQuestionNumber, 1));
            mButtonChoice2Religion.setText( religionQuestions.getChoice( mQuestionNumber, 2));
            mButtonChoice3Religion.setText( religionQuestions.getChoice( mQuestionNumber, 3));
            mButtonChoice4Religion.setText( religionQuestions.getChoice( mQuestionNumber, 4));

            mAnswer = religionQuestions.getCorrectAnswer( mQuestionNumber );
            mQuestionNumber++;

        }
        else {

            mpCount.stop();
            toastLastQuestion();
            countDownTimer.onFinish();

        }
    }

    public void onClickBtnReligion(View view){


        // All logic for all answers buttons in one method
        Button answer = (Button) view;

        if (answer.getText() == mAnswer){
            mScore = mScore + 1;

            if (isPlaying == false){
                mp.start();
            } else {
                mp.pause();
            }

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate( R.layout.toast_correct, (ViewGroup) findViewById( R.id.toast_correct ));
            final Toast toast = new Toast( getApplicationContext() );
            toast.setGravity( Gravity.CENTER, 10, 10);
            toast.setDuration( toast.LENGTH_SHORT );
            toast.setView( layout );
            toast.show();
            new CountDownTimer(400, 100)
            {
                public void onTick(long millisUntilFinished) {toast.show();}
                public void onFinish() {toast.cancel();}
            }.start();

            correctCount();
            countQuestionDown();

            countDownTimer.start();
            mpCount.setLooping( true );

        }
        else {

            if (isPlaying == false){
                mpc.start();
            } else {
                mpc.pause();
            }

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate( R.layout.toas_incorrect, (ViewGroup) findViewById( R.id.toast_incorrect));
            final Toast toast = new Toast( getApplicationContext() );
            toast.setGravity( Gravity.CENTER, 10, 10);
            toast.setDuration( Toast.LENGTH_SHORT);
            toast.setView( layout );
            toast.show();
            new CountDownTimer(400, 100)
            {
                public void onTick(long millisUntilFinished) {toast.show();}
                public void onFinish() {toast.cancel();}
            }.start();

            wrongCount();
            countQuestionDown();

            countDownTimer.start();
            mpCount.setLooping( true );

        }

        updateQuestions();
    }

    public void goToBack(){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate( R.layout.custom_dialog, null );

        TextView titleTextDialog = (TextView) layout.findViewById( R.id.titleTextDialog );
        titleTextDialog.setText(getString( R.string.voulez_vous_annuler ));
        titleTextDialog.setTextSize( 18 );

        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setView( layout);
        builder.setCancelable( false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnStart = (Button) layout.findViewById( R.id.custom_dialog_btn_start );
        btnStart.setText( getString( R.string.essayer ));
        btnStart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                countDownTimer.start();

                if (isPlaying == false){
                    mpCount.start();
                } else {
                    mpCount.pause();
                }

            }
        });

        Button btnConcel = (Button) layout.findViewById( R.id.btnCancel );
        btnConcel.setText( getString( R.string.annuler ));
        btnConcel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpCount.stop();
                mainActivity();
                finish();
            }
        });

    }

    public void toastLastQuestion(){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_last_question,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
        image.setImageResource(R.mipmap.ic_main_logo);
        TextView text = (TextView) layout.findViewById(R.id.text_toast);
        text.setText(" C'est était dernier question ! ");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void mainActivity(){
        Intent intentMain = new Intent(ReligionActivity.this, MainActivity.class);
        startActivity( intentMain );
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate( R.layout.custom_dialog, null );

        TextView titleTextDialog = (TextView) layout.findViewById( R.id.titleTextDialog );
        titleTextDialog.setText(this.getString( R.string.quizz_religion ));

        final AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setView( layout);
        builder.setCancelable( false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnStart = (Button) layout.findViewById( R.id.custom_dialog_btn_start );
        btnStart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                countDownTimer.start();

                    mpCount.start();


            }
        });

        Button btnCancel = (Button) layout.findViewById( R.id.btnCancel );
        btnCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity();
                finish();
            }
        });


    }

    private void showAnswer() {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;

                }
            }
        };

        String religion = "religion";

        WebView webView = new WebView(this );
        webView.loadUrl("file:///android_asset/"+religion+".html");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(webView)
                .setNeutralButton("OK", dialogClickListener)
                .show();

    }

    private void showAnswerWithShare() {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        String txtShare = getString( R.string.app_name);
                        String shareLink = "https://play.google.com/store/apps/details?id=com.drmas.issam.quizzfrancais";

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");

                        share.putExtra(Intent.EXTRA_TEXT,txtShare + "\n" + shareLink);
                        startActivity(share);
                        break;

                }
            }
        };

        String religion = "religion";

        WebView webView = new WebView(this );
        webView.loadUrl("file:///android_asset/"+religion+".html");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(webView)
                .setNeutralButton("OK", dialogClickListener)
                .setPositiveButton( getString( R.string.partager ), dialogClickListener )
                .show();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        countDownTimer.cancel();
        showAnswer();

    }

    @Override
    protected void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
        countDownTimer.cancel();

        finish();

    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            countDownTimer.cancel();
            goToBack();
        }
        return true;
    }
}
