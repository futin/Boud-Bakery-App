package com.example.futin.boudinbreakapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Futin on 7/7/2015.
 */
public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener{

    RadioButton radioYes;
    RadioButton radioNo;
    //game layout
    ViewGroup gameLayout;
    Button btnClickMe;
    TextView txtClicked;
    TextView txtWin;
    TextView txtTitle;
    RatingBar ratingBar;

    //YES layout
    RadioGroup radioGroupReminder;
    RadioButton radio10;
    RadioButton radio20;
    RadioButton radioMinute;

    Button btnBreakReminder;
    Button btnReset;
    Button btnStop;
    TextView breakTimeText;
    EditText pickerDate;
    TextView breakReminderTxt;
    EditText remindCounter;
    TextView txtReminderCount;

    //time picker and progressBar
    TimePickerDialog pickTime;
    ProgressBar progressBarTimer;
    Handler handler=new Handler();
    MediaPlayer mp;
    ObjectAnimator animateTrans;
    int progressStatus=0;

    //checking if break time is setup
    boolean timerIsOn=false;
    //check if one of reminders are picked
    boolean reminderIsPicked;
    //check  if onDestroy is called, and stop the loop for main Thread
    boolean requestToExit;
    //check if onPause if called, and it is called when you switch between more then 1 fragments
    //if it is called, use it to set maxProgressBar element, so it doesn't go to default 100
    boolean isOnPauseCalled;

    int backCounter;
    int gameCounter=0;
    int POST_DELAYED_TIME=3500;
    int startLevelCounter=5;
    float buttonX=0;


    //variable for setting reminder time, only when break ends
    long timeIsOut;
    //set reminder counter, on how many times you want reminder to repeat recursively
    int timeReminderCounter=0;
    //variable for setting and getting maxSeconds for progressBar
    int maxSeconds;

    CountDownTimer mCountDownTimer;

    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }
    public int getMaxSeconds() {
        return maxSeconds;
    }

    public void setTimeIsOut(long timeIsOut) {
        this.timeIsOut = timeIsOut;
    }
    public long getTimeIsOut() {
        return timeIsOut;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioYes= (RadioButton) findViewById(R.id.radioYes);
        radioNo= (RadioButton) findViewById(R.id.radioNo);
        radio10= (RadioButton) findViewById(R.id.radio10);
        radio20= (RadioButton) findViewById(R.id.radio20);
        radioMinute= (RadioButton) findViewById(R.id.radioMinute);

        //game layout
        gameLayout= (ViewGroup) findViewById(R.id.layoutGame);
        btnClickMe= (Button) gameLayout.findViewById(R.id.btnClickMe);
        txtClicked= (TextView) gameLayout.findViewById(R.id.txtClicked);
        txtWin = (TextView) gameLayout.findViewById(R.id.txtWin);
        txtTitle= (TextView) gameLayout.findViewById(R.id.txtTitle);
        ratingBar= (RatingBar) gameLayout.findViewById(R.id.ratingBar);

        //YES layout
        radioGroupReminder= (RadioGroup) findViewById(R.id.radioGroupReminder);

        btnBreakReminder= (Button) findViewById(R.id.btnBreakTime);
        btnReset= (Button) findViewById(R.id.btnReset);
        btnStop = (Button) findViewById(R.id.btnStop);
        pickerDate= (EditText) findViewById(R.id.pickerDate);
        breakTimeText= (TextView) findViewById(R.id.breakTimeText);
        breakReminderTxt= (TextView) findViewById(R.id.breakReminderTxt);
        remindCounter= (EditText) findViewById(R.id.remindCounter);
        remindCounter.setText("3");
        progressBarTimer= (ProgressBar) findViewById(R.id.progressBarTimer);
        txtReminderCount = (TextView) findViewById(R.id.txtReminderCount);

        //setting onClickListeners
        radio10.setOnClickListener(this);
        radioYes.setOnClickListener(this);
        radio20.setOnClickListener(this);
        radioNo.setOnClickListener(this);
        radioMinute.setOnClickListener(this);
        btnBreakReminder.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        breakReminderTxt.setOnClickListener(this);
        pickerDate.setOnClickListener(this);
        progressBarTimer.setOnClickListener(this);
        btnClickMe.setOnClickListener(this);
        btnClickMe.setOnTouchListener(this);
        pickerDate.setInputType(InputType.TYPE_NULL);
        radioNo.setChecked(true);

        miniGame();
        checkRadioButtons();
        //if timer is still not set to true, activate timePicker until it is true
        if (!timerIsOn)
            timePicker();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.radioYes:
                setVisibility(View.VISIBLE);
                radioNo.setEnabled(false);
                pickerDate.requestFocus();
                btnStop.setEnabled(false);
                btnStop.setAlpha(0.6f);
                gameLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.radioNo:
                setVisibility(View.INVISIBLE);
                break;
            case R.id.btnBreakTime:
                if(timerIsOn) {
                    reminderIsPicked=true;
                    if (radio10.isChecked()) {
                        setTimeIsOut(10);
                        makeToast("Selected 10 seconds reminder");
                    } else if (radio20.isChecked()) {
                        setTimeIsOut(20);
                        makeToast("Selected 20 seconds reminder");
                    } else {
                        setTimeIsOut(60);
                        makeToast("Selected 1 minute reminder");
                    }
                }else{
                    makeToast("Set break time end first");
                }
                break;
            case R.id.pickerDate:
                //show time picker
                pickTime.show();
                progressBarTimer.setMax(0);
                break;
            case R.id.btnReset:
                reset();
                break;
            case R.id.btnStop:
                new AlertDialog.Builder(this)
                        .setTitle("Clock in reminder")
                        .setMessage("Have you clocked in yet?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                                radioNo.setEnabled(true);
                                radioNo.setChecked(true);
                                radioYes.setChecked(false);
                                checkRadioButtons();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.radio10:
                btnBreakReminder.setEnabled(true);
                btnBreakReminder.setAlpha(1.0f);
                remindCounter.setEnabled(true);
                break;
            case R.id.radio20:
                btnBreakReminder.setEnabled(true);
                btnBreakReminder.setAlpha(1.0f);
                remindCounter.setEnabled(true);
                break;
            case R.id.radioMinute:
                btnBreakReminder.setEnabled(true);
                btnBreakReminder.setAlpha(1.0f);
                remindCounter.setEnabled(true);
                break;
            case R.id.btnClickMe:
                miniGame();
                break;
        }
    }
    //it is called when you switch more fragments, so memory and battery can be saved.
    @Override
    public void onPause() {
        isOnPauseCalled=true;
        super.onPause();
    }
    //it is called after onCreate, and when onPause is called
    @Override
    public void onResume() {
        checkRadioButtons();

        if(isOnPauseCalled && timerIsOn) {

        }else{
            pickerDate.setEnabled(true);
            progressBarTimer.setProgress(0);
        }
        super.onResume();
    }
    // it is called when you exit from the application
    @Override
    public void onDestroy() {
        requestToExit=true;
        cancelCountDownCounter();
        Log.i("","onDest called");
        if (mp !=null){
            mp.release();
            mp=null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backCounter++;
        if (backCounter==2){
            backCounter=0;
            super.onBackPressed();
        }else{
            makeToast("Press one more time to exit");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backCounter=0;
                }
            },2200);
        }
       // pressInfinitTimesToQuit(25);
    }

    //method used for managing visibility of views
    public void setVisibility(int visibility){
        radioGroupReminder.setVisibility(visibility);
        breakReminderTxt.setVisibility(visibility);
        breakTimeText.setVisibility(visibility);
        pickerDate.setVisibility(visibility);
        btnBreakReminder.setVisibility(visibility);
        progressBarTimer.setVisibility(visibility);
        btnStop.setVisibility(visibility);
        btnReset.setVisibility(visibility);
        txtReminderCount.setVisibility(visibility);
        remindCounter.setVisibility(visibility);
    }
    //use this method to check for last checked radio button, and setup layouts
    public void checkRadioButtons(){
        if(radioYes.isChecked()){
            setVisibility(View.VISIBLE);
            gameLayout.setVisibility(View.INVISIBLE);
         //   btnClickMe.setVisibility(View.INVISIBLE);
           // txtClicked.setVisibility(View.INVISIBLE);
            radioNo.setEnabled(false);
            if(!timerIsOn) {
                btnStop.setEnabled(false);
                btnStop.setAlpha(0.6f);
            }
        }else{
            setVisibility(View.INVISIBLE);
            gameLayout.setVisibility(View.VISIBLE);
           // btnClickMe.setVisibility(View.VISIBLE);
           // txtClicked.setVisibility(View.VISIBLE);
        }

        if (radio10.isChecked() || radio20.isChecked() || radioMinute.isChecked()){
            btnBreakReminder.setEnabled(true);
            btnBreakReminder.setAlpha(1.0f);
            remindCounter.setEnabled(true);
        }else{
            btnBreakReminder.setEnabled(false);
            btnBreakReminder.setAlpha(0.6f);
            remindCounter.setEnabled(false);


        }
    }
    //get seconds from current daytime
    public float getCurrentSeconds(){
        Calendar calendar=Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes=calendar.get(Calendar.MINUTE);
        int seconds=calendar.get(Calendar.SECOND);

        float minutesInSec=hour*60+minutes;
        float sec=minutesInSec*60+seconds;
        return sec;
    }

    //this is the method used for picking and setting time into pickerDate editText field
    public void timePicker(){
        Calendar newCalendar=Calendar.getInstance();
        pickTime=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //take all seconds, minutes and hours
                if (view.isShown()) {
                    float currentSeconds = getCurrentSeconds();
                    float minutesInSec = hourOfDay * 60 + minute;
                    float seconds = minutesInSec * 60;
                    //compare those seconds
                    if (currentSeconds < seconds) {
                        //set requestToExit to false so main thread continue with its work
                        requestToExit = false;
                        //time is setup.
                        timerIsOn = true;
                        Log.i("", "teststts");
                        //progressBar for current time
                        setProgressBarUsingTimer((int) (seconds - currentSeconds));
                        //set text depending on picked time, add 0 if it is less then 10
                        pickerDate.setText(new StringBuilder().append(hourOfDay < 10 ? "0" + hourOfDay : hourOfDay).append(":").append(minute < 10
                                ? "0" + minute : minute));
                        pickerDate.setEnabled(false);
                        btnStop.setEnabled(true);
                        btnStop.setAlpha(1f);
                    } else {
                        makeToast("Pick time after your break");
                        pickerDate.setText("");
                    }
                    //set current hour and minute to picker
                }
            }

        }, newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE), false);
    }

    //this is the method for reminder if it is picked, it is called recursively until countTimes
    //is not reached and onDestroy method is not called
    public void setTimeReminder(final long seconds, final int countTimes){
        timeReminderCounter++;
        radio10.setEnabled(false);
        radio20.setEnabled(false);
        radioMinute.setEnabled(false);
        txtReminderCount.setEnabled(false);
        btnBreakReminder.setEnabled(false);
        btnBreakReminder.setAlpha(0.6f);
        btnStop.setEnabled(true);
        btnStop.setAlpha(1f);
        Log.i("","timeReminder: "+timeReminderCounter );
        Log.i("","countTimes: "+countTimes );

        if (timeReminderCounter<=countTimes) {
            //countDownTimer is called as notification that tells us when the task is done
            mCountDownTimer= new CountDownTimer(seconds * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("","millis: "+millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    if (!requestToExit) {
                        makeVibration();
                        setTimeReminder(seconds, countTimes);
                    }
                }
            }.start();
        }else{
            reset();
        }
    }

    void cancelCountDownCounter() {
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
            Log.i("","stoped");
        }
    }

    //method for setting up progressBar
    public void setProgressBar(final int seconds){
        //set seconds that are set depending on difference between current and new time
        setMaxSeconds(seconds);
        mCountDownTimer.cancel();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus<progressBarTimer.getMax() && !requestToExit){
                    //increment status by 1 while it is not greater then maximum time
                    progressStatus+=1;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //these setting has to be setup inside of handler because it is called
                            // async
                            progressBarTimer.setMax(seconds);
                            progressBarTimer.setProgress(progressStatus);
                            progressBarTimer.setClickable(true);
                            Log.i("","seconds:"+progressStatus+"/"+progressBarTimer.getMax());

                            progressBarTimer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (timerIsOn) {
                                        int minutes=(progressBarTimer.getMax()-progressStatus)/60;
                                        int seconds=(progressBarTimer.getMax()-progressStatus)% 60;
                                        makeToast("Time left: "+String.valueOf(minutes<10?"0"+minutes:minutes)+":"+
                                                String.valueOf(seconds<10?"0"+seconds:seconds));
                                    }
                                }
                            });
                            //when progress bar reaches end
                            if (progressStatus == progressBarTimer.getMax()) {
                                makeVibration();
                                if (reminderIsPicked)
                                    setTimeReminder(getTimeIsOut(),Integer.parseInt(remindCounter.getText().toString()));
                                timerIsOn = false;
                                remindCounter.setEnabled(false);
                                btnBreakReminder.setEnabled(false);
                                btnBreakReminder.setAlpha(0.6f);
                                radio10.setEnabled(false);
                                radio20.setEnabled(false);
                                radioMinute.setEnabled(false);
                            }
                        }
                    });
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void setProgressBarUsingTimer(final int seconds){
        setMaxSeconds(seconds);
        progressStatus=1;
        mCountDownTimer=new CountDownTimer(seconds*1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!requestToExit) {
                    progressStatus++;
                    final int millisInt=(int) millisUntilFinished/1000;
                    Log.i("", "test: " + millisInt+" "+progressBarTimer.getMax());
                    progressBarTimer.setProgress(millisInt);
                    progressBarTimer.setMax(seconds);
                    progressBarTimer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (timerIsOn) {
                                int minutes = millisInt / 60;
                                int seconds = millisInt % 60;
                                if (minutes<60) {
                                    makeToast("Time left: " + String.valueOf(minutes < 10 ? "0" + minutes : minutes) + ":" +
                                            String.valueOf(seconds < 10 ? "0" + seconds : seconds));
                                }else{
                                    int hours=minutes/60;
                                    int newMinutes=minutes % 60;
                                    makeToast("Time left: "+ String.valueOf(hours < 10 ? "0" + hours : hours) + ":"
                                            + String.valueOf(newMinutes < 10 ? "0" + newMinutes : newMinutes) + ":" +
                                            String.valueOf(seconds < 10 ? "0" + seconds : seconds));
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                //when progress bar reaches end
                makeVibration();
                if (reminderIsPicked) {
                    setTimeReminder(getTimeIsOut(), Integer.parseInt(remindCounter.getText().toString()));
                }else{
                    reset();
                }
            }
        }.start();
    }



    //resets everything to default
    public void reset(){
        requestToExit=true;
        timerIsOn=false;
        reminderIsPicked=false;
        progressStatus=0;
        timeReminderCounter=0;
        progressBarTimer.setProgress(progressStatus);
        progressBarTimer.setMax(0);
        pickerDate.setText("");
        pickerDate.setEnabled(true);
        pickerDate.requestFocus();
        radio10.setChecked(false);
        radio20.setChecked(false);
        radioMinute.setChecked(false);
        radioGroupReminder.clearCheck();
        btnStop.setEnabled(false);
        btnStop.setAlpha(0.6f);
        btnBreakReminder.setEnabled(false);
        btnBreakReminder.setAlpha(0.6f);
        remindCounter.setText("3");
        remindCounter.setEnabled(false);
        radio10.setEnabled(true);
        radio20.setEnabled(true);
        radioMinute.setEnabled(true);
        cancelCountDownCounter();
        resetMiniGame(5);
    }

    // make vibration for 2 seconds
    public void makeVibration(){
        if (mp != null){
            mp.reset();
            mp=null;
        }
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1500);
        Uri notificationRingtone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri alarmRingtone= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (notificationRingtone != null){
            mp=MediaPlayer.create(this,notificationRingtone);
            mp.start();
        }else if (alarmRingtone != null){
            mp=MediaPlayer.create(this,alarmRingtone);
            mp.start();
        }else{
            return;
        }
    }
    //simple method for showing messages
    public void makeToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    void miniGame(){
        gameCounter++;
        txtWin.setVisibility(View.INVISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setEnabled(false);
        if (gameCounter==startLevelCounter){
            if (ratingBar.getRating()==0) {
                nextLevel(3000, 10, 1, "Just worming up!");
            }else if (ratingBar.getRating()==1){
                nextLevel(2500, 15, 2, "Up to Level 2!");
            }else if(ratingBar.getRating()==2){
                nextLevel(2200, 20, 3, "WOW Level 3 already!");
            }else if (ratingBar.getRating()==3){
                nextLevel(2000, 22, 4, "Does your finger hurts?!");
            }else if (ratingBar.getRating()==4){
                nextLevel(1800, 21, 5, "Level impossible!");
            }else{
                btnClickMe.setEnabled(false);
                txtTitle.setVisibility(View.INVISIBLE);
                txtWin.setVisibility(View.VISIBLE);
                txtWin.setText("If you haven't broken your finger/display so far, you may " +
                        "proceed with your Break Time!");
                txtClicked.setVisibility(View.INVISIBLE);
                btnClickMe.setText("WIN");
                btnClickMe.setGravity(Gravity.CENTER);
                animateButton();
            }
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (gameCounter > 0) {
                        gameCounter--;
                        txtClicked.setText(gameCounter + "/" + startLevelCounter);
                    }
                }
            }, POST_DELAYED_TIME);
            txtClicked.setGravity(Gravity.CENTER);
            txtClicked.setTextSize(25);
            txtClicked.setText(gameCounter+"/"+startLevelCounter);
        }
    }

    void resetMiniGame(int counter){
        gameCounter=0;
        startLevelCounter=5;
        ratingBar.setRating(0);
        btnClickMe.setEnabled(true);
        btnClickMe.setText("Click me for WIN");
        txtTitle.setText("Click as fast as possible");
        txtClicked.setText(gameCounter + "/" + counter);
        txtClicked.setVisibility(View.VISIBLE);
        txtWin.setVisibility(View.INVISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        if (animateTrans != null) {
            animateTrans.cancel();
            btnClickMe.setX(buttonX);
        }
    }
    void nextLevel(int postDelayedTime, int counter, int rating, String btnTxt){
        btnClickMe.setTextColor(Color.WHITE);
        txtTitle.setText(btnTxt);
        gameCounter=0;
        startLevelCounter=counter;
        txtClicked.setText(gameCounter+"/"+startLevelCounter);
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(rating);
        POST_DELAYED_TIME=postDelayedTime;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId()==R.id.btnClickMe){

            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                btnClickMe.setBackground(getResources().getDrawable(R.drawable.game_button_shape_action_down));
                btnClickMe.setAlpha(0.5f);
                // btnClickMe.setBackground(getDrawable(R.drawable.game_button_shape_action_down));
            }else if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                btnClickMe.setBackground(getResources().getDrawable(R.drawable.game_button_shape_action_up));
                btnClickMe.setAlpha(1f);
            }
        }
        return false;
    }
    //maybe for future needs, in case I want to set ActionMove out of circle
    PointF  pointOnCircle(float radius, float angleInDegrees, MotionEvent origin){
        float x=(float)(radius*Math.cos(angleInDegrees * Math.PI/180f)+origin.getX());
        float y= (float)(radius*Math.sin(angleInDegrees * Math.PI/180f) + origin.getY());

        return new PointF(x,y);
    }
    void animationMaking(float start, float end, int time){
        animateTrans = ObjectAnimator.ofFloat(btnClickMe, "translationX", start, end, end,start);
        animateTrans.setDuration(time);
        animateTrans.setRepeatCount(Animation.INFINITE);
        buttonX=btnClickMe.getX();
        animateTrans.start();
    }
    void animateButton(){
        animationMaking(btnClickMe.getX()-150,btnClickMe.getX()+110,1800);
    }

}
