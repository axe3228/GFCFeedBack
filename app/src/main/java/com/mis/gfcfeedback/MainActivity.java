package com.mis.gfcfeedback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.mis.gfcfeedback.Class.BackgroundWorker;
import com.mis.gfcfeedback.Class.DBHelper;
import com.mis.gfcfeedback.Class.clsFeedBack;
import com.mis.gfcfeedback.Class.clsHomeWatcher;
import com.mis.gfcfeedback.Class.clsSettings;
import com.mis.gfcfeedback.Class.clsUploader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    BackgroundWorker backgroundWorker;
    clsUploader uploader;

    AnimationDrawable animation;
    AlertDialog dialog;

    ArrayList<clsFeedBack> feedBacks;

    ImageView ivFloydsIcon;
    ImageView ivExcellent;
    ImageView ivGood;
    ImageView ivAverage;
    ImageView ivPoor;

    String sDate;
    String sMonth;
    String sTime;

    int iFoodQuality, iCleanliness, iService, iAtmosphere;
    int iEmoji;
    int iDay, iMonthControl;

    boolean passToggle = false;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliSeconds = 600000; //10 mins
    private boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(getApplicationContext());
        uploader = new clsUploader(this);

        ivFloydsIcon = findViewById(R.id.actMain_ivStoreLogo);
        ivExcellent = findViewById(R.id.actMain_ivExcellent);
        ivGood = findViewById(R.id.actMain_ivGood);
        ivAverage = findViewById(R.id.actMain_ivAverage);
        ivPoor = findViewById(R.id.actMain_ivPoor);


        is60DaysUp(); //<---Check if device data is 60 days old
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y hh:mm");
        sDate = sdf.format(currentTime);

        File root = new File(Environment.getExternalStorageDirectory() + File.separator, "GFCFeedback");

        if (!root.exists()) {
            root.mkdirs();
        }
       // dlgUpdater();
//        startTimer();
//        countdown();
    }

    @Override
    protected void onStart() {
        super.onStart();

        clsHomeWatcher mHomeWatcher = new clsHomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new clsHomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...

            }

            @Override
            public void onHomeLongPressed() {

            }
        });
        mHomeWatcher.startWatch();

        ivFloydsIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               // showMenu();
                showPassword();
                return false;
            }
        });

        ivExcellent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlgLoading("Thank you for your feedback.", "Submitting Feedback");
                dbHelper.SaveFeedBack(iMonthControl + " " + sMonth, 4, 0, getDateTime(), sTime, 0, 0, 0, 0);

                if (timerRunning){
                    stopTimer();

                    timeLeftInMilliSeconds = 600000;
                    startTimer();
                }


            }
        });

        ivGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlgLoading("Thank you for your feedback.", "Submitting Feedback");
                dbHelper.SaveFeedBack(iMonthControl + " " + sMonth, 3, 0, getDateTime(), sTime, 0, 0, 0, 0);

                if (timerRunning){
                    stopTimer();

                    timeLeftInMilliSeconds = 600000;
                    startTimer();
                }


            }
        });

        ivAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSuggestion("What can we improve on?");
                iEmoji = 2;

                if (timerRunning){
                    stopTimer();

                    timeLeftInMilliSeconds = 600000;
                    startTimer();
                }


            }
        });

        ivPoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSuggestion("What are you not satisfied?");
                iEmoji = 1;

                if (timerRunning){
                    stopTimer();

                    timeLeftInMilliSeconds = 600000;
                    startTimer();
                }

            }
        });

    }

    private void is60DaysUp() {
//        Calendar dummyDate = Calendar.getInstance();
//        dummyDate.set(2019, 7, 15);
//        Date tempDate = dummyDate.getTime();

        String currentime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Date currentdate = Calendar.getInstance().getTime();

        iDay = Integer.parseInt((String) DateFormat.format("dd", currentdate));
        sMonth = (String) DateFormat.format("MMM", currentdate);


        if (iDay <= 15) {
            iMonthControl = 1;

        } else {
            iMonthControl = 2;

        }

        //long days = dayBetweenCalculator(dbHelper.getFirstFeedbackEntryDate(), currentdate);

        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y hh:mm");
        String today = sdf.format(currentdate);
        //String td = sdf.format(dbHelper.getFirstFeedbackEntryDate());
        //Log.d("WorkPlace", days + " days from today's date of " + today + " until " + td);

//        if (days > 30) {
//            Toast.makeText(this, "Uploading Data", Toast.LENGTH_SHORT).show();
//            uploader.uploadFeedback(dbHelper.getFeedback());
//        }

    }

    private String getDateTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y");

        sTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        return sdf.format(currentTime);
    }

    private long dayBetweenCalculator(Date one, Date two) {
        long diff = (one.getTime() - two.getTime()) / 86400000;
        return Math.abs(diff);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    public void dlgLoading(final String msg, String label) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_loading, null);

        ImageView ivFloydsGIF = v.findViewById(R.id.dlgLoading_ivFloydsGIF);
        TextView tvLabel = v.findViewById(R.id.dlgLoading_tvLabel);
        animation = (AnimationDrawable) ivFloydsGIF.getDrawable();

        tvLabel.setText(label);

        animation.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation.stop();
                dialog.dismiss();

                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, 3000);

        mBuilder.setView(v);
        dialog = mBuilder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showDialogSuggestion(String Q) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_good_suggestion);

        TextView tvQuestion = dialog.findViewById(R.id.dlgGoodSuggest_tvQuestion);
        tvQuestion.setText(Q);

        final ImageView ivFoodCheck = dialog.findViewById(R.id.dlgGoodSuggest_ivFoodCheck);
        final ImageView ivCleanlinessCheck = dialog.findViewById(R.id.dlgGoodSuggest_ivCleanlinessCheck);
        final ImageView ivServiceCheck = dialog.findViewById(R.id.dlgGoodSuggest_ivServiceCheck);
        final ImageView ivAtmosphereCheck = dialog.findViewById(R.id.dlgGoodSuggest_ivAtmosphereCheck);

        LinearLayout llFood = dialog.findViewById(R.id.dlgGoodSuggest_llFood);
        LinearLayout llCleanliness = dialog.findViewById(R.id.dlgGoodSuggest_llCleanliness);
        LinearLayout llService = dialog.findViewById(R.id.dlgGoodSuggest_llService);
        LinearLayout llAtmosphere = dialog.findViewById(R.id.dlgGoodSuggest_llAtmosphere);

        Button btnSubmit = dialog.findViewById(R.id.dlgGoodSuggest_btnSubmit);

        llFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivFoodCheck.getVisibility() != View.VISIBLE) {
                    ivFoodCheck.setVisibility(View.VISIBLE);

                    iFoodQuality = 1;

                } else {
                    ivFoodCheck.setVisibility(View.INVISIBLE);

                    iFoodQuality = 0;
                }

//                dialog.dismiss();
//                dlgLoading();
            }
        });

        llCleanliness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivCleanlinessCheck.getVisibility() != View.VISIBLE) {
                    ivCleanlinessCheck.setVisibility(View.VISIBLE);

                    iCleanliness = 1;
                } else {
                    ivCleanlinessCheck.setVisibility(View.INVISIBLE);

                    iCleanliness = 0;
                }

            }
        });

        llService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivServiceCheck.getVisibility() != View.VISIBLE) {
                    ivServiceCheck.setVisibility(View.VISIBLE);

                    iService = 1;
                } else {
                    ivServiceCheck.setVisibility(View.INVISIBLE);

                    iService = 0;
                }

            }
        });

        llAtmosphere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivAtmosphereCheck.getVisibility() != View.VISIBLE) {
                    ivAtmosphereCheck.setVisibility(View.VISIBLE);

                    iAtmosphere = 1;
                } else {
                    ivAtmosphereCheck.setVisibility(View.INVISIBLE);

                    iAtmosphere = 0;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (iFoodQuality + iCleanliness + iService + iAtmosphere == 0) {
                    Toast.makeText(MainActivity.this, "Please select from the following.", Toast.LENGTH_SHORT).show();
                } else {

                    if (dbHelper.SaveFeedBack(iMonthControl + " " + sMonth, iEmoji, 1, getDateTime(), sTime, iFoodQuality, iCleanliness, iService, iAtmosphere)) {
                        iEmoji = 0;
                        iFoodQuality = 0;
                        iCleanliness = 0;
                        iService = 0;
                        iAtmosphere = 0;

                        dialog.dismiss();
                        dlgLoading("Thank you for your feedback.", "Submitting Feedback");
                    } else {
                        Toast.makeText(MainActivity.this, "Saving Feedback failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void showMenu() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_menu);

        LinearLayout llExportCSV = dialog.findViewById(R.id.dlgMenu_llExportCSV);
        LinearLayout llSettings = dialog.findViewById(R.id.dlgMenu_llSetting);
        LinearLayout llSlideShow = dialog.findViewById(R.id.dlgMenu_llSlideShow);
        LinearLayout llTruncateTableFb = dialog.findViewById(R.id.dlgMenu_llTruncateTable);
        LinearLayout llDownload_ads = dialog.findViewById(R.id.dlgMenu_llDownloadAd);
        final TextView tvAdsSwitch = dialog.findViewById(R.id.dlgMenu_AdsSwitch);
        final ImageView ivAdsSwitch = dialog.findViewById(R.id.dlgMenu_ivAdsSwitch);

        llExportCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploader.uploadFeedback(dbHelper.getFeedback(), dbHelper.loadSettings());
              //dialog.dismiss();

            }
        });

        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettings();
            }
        });

        llSlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning){
                    stopTimer();
                    tvAdsSwitch.setTextColor(Color.parseColor("#ff0000"));
                    ivAdsSwitch.setImageResource(R.drawable.adsiconoff);

                }else {
                    startTimer();
                    tvAdsSwitch.setTextColor(Color.parseColor("#50C878"));
                    ivAdsSwitch.setImageResource(R.drawable.adsicon);
                }
            }
        });

        llTruncateTableFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesornodialog();
            }
        });

        llDownload_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlgUpdater();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void yesornodialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                       dbHelper.truncateTable("TBLFeedBack");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void showPassword() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_mis_password);

        final EditText etMISP = dialog.findViewById(R.id.dlgMISP_etPassword);
        ImageView ivSubmit = dialog.findViewById(R.id.dlgMISP_ivSubmit);
        final ImageView ivPasswordToggle = dialog.findViewById(R.id.dlgMISP_ivPasswordToggle);

        ivPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passToggle){
                    passToggle = false;
                    ivPasswordToggle.setImageResource(R.drawable.passwordtoggleoff);
                    etMISP.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else {
                    passToggle = true;
                    ivPasswordToggle.setImageResource(R.drawable.passwordtoggleon);
                    etMISP.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMISP.getText().toString().equals("")){ dialog.dismiss(); return;}

                if (etMISP.getText().toString().equals("1199322426")) {
                    dialog.dismiss();
                    showMenu();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect Password. Contact MIS Department", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void showSettings() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_settings);

        final ArrayList<clsSettings> settings = dbHelper.loadSettings();
        final EditText etUrl = dialog.findViewById(R.id.dlgSettings_etURL);
        final EditText etBUC = dialog.findViewById(R.id.dlgSettings_etBUCode);
        final EditText etBU = dialog.findViewById(R.id.dlgSettings_etBU);
        final EditText etStoreID = dialog.findViewById(R.id.dlgSettings_etIDStore);
        final EditText etBN = dialog.findViewById(R.id.dlgSettings_etBN);
        Button btnConfirm = dialog.findViewById(R.id.dlgSettings_btnConfirm);

        hideKeyboard(this);

        if (settings.size() != 0) {

            etUrl.setText(settings.get(0).getsUrl());
            etBUC.setText(settings.get(0).getsBuCode());
            etBU.setText(settings.get(0).getsBU());
            etStoreID.setText(settings.get(0).getIdStore());
            etBN.setText(settings.get(0).getsBN());

        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settings.size() == 0) {
                    Toast.makeText(MainActivity.this, dbHelper.saveSettings(etUrl.getText().toString(), etBUC.getText().toString(), etBU.getText().toString(), etStoreID.getText().toString(),  etBN.getText().toString()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, dbHelper.updateSettings(etUrl.getText().toString(), etBUC.getText().toString(), etBU.getText().toString(), etStoreID.getText().toString(),  etBN.getText().toString()), Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliSeconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliSeconds = l;
               //countdown();
            }

            @Override
            public void onFinish() {
                Intent i = new Intent(MainActivity.this, GFCAdvertisement.class );
                startActivityForResult(i, 1);
            }
        }.start();

        timerRunning = true;
    }

    public void stopTimer(){
        countDownTimer.cancel();
        timerRunning = false;
    }

    private void countdown(){
        int mins = (int) timeLeftInMilliSeconds / 60000;
        int sec = (int) timeLeftInMilliSeconds % 60000 / 1000;

        String timeLeftText;

        timeLeftText = "" + mins;
        timeLeftText += ":";
        if (sec < 10)timeLeftText += "0";
        timeLeftText += sec;

       // Log.d("popo", "countdown: "+timeLeftText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                timeLeftInMilliSeconds = 600000;
                startTimer();
            }
        }
    }

    private void dlgUpdater(){
//        new AppUpdater(this)
//                .setUpdateFrom(UpdateFrom.GITHUB)
//                .setGitHubUserAndRepo("axe3228", "GFCFeedBack")
//                .showAppUpdated(true)
//                .start();

        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("axe3228", "GFCFeedBack")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        Log.d("Latest Version", update.getLatestVersion());
                        Log.d("URL", update.getUrlToDownload().toString());
                        Log.d("Is update available?", Boolean.toString(isUpdateAvailable));
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                        Log.d("AppUpdater Error", "Something went wrong");
                    }
                });
        appUpdaterUtils.start();
    }
}
