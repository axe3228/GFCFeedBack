package com.mis.gfcfeedback.Class;
//Solenya, eihcra

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 26;
    private static final String DB_NAME = "GFCFeedBack";
    private int idFeedback;
    clsUploader uploader;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        uploader = new clsUploader(context);
        this.getReadableDatabase();
        this.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE TBLFeedBack ( idFeedback INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, unFeedback TEXT, FBMonthControl TEXT, FBEmoji INTEGER, FBReason INTEGER, FoodQuality INTEGER, Cleanliness INTEGER, Service INTEGER, Atmosphere INTEGER, FBStatus INTEGER, FBTimeStamp TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE TBLSettings ( idSettings INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, sURL TEXT, sBusinessUnitCode TEXT, sBusinessUnit TEXT, idStore TEXT, sBranchName TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE TBLSession ( idSession INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timeStamp TEXT, timerunning INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBLFeedBack");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBLSettings");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TBLSession");
        onCreate(sqLiteDatabase);
    }

    public void truncateTable(String Table) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete(Table, null, null);
        sqLiteDatabase.close();
    }

    private int getUnFeedback() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int iCount;
        Cursor c = sqLiteDatabase.rawQuery("Select Count(idFeedback)+1 as idFeedbackCount From TBLFeedBack", null);
        if (c != null) {
            if (c.moveToFirst()) {
                iCount = c.getInt(c.getColumnIndex("idFeedbackCount"));
                return iCount;
            }
        }
        return 0;
    }

    public String saveSettings(String url, String buc, String bu, String idStore, String bn) {
        String result = "Saving settings failed.";

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put("sURL", url);
            values.put("sBusinessUnitCode", buc);
            values.put("sBusinessUnit", bu);
            values.put("idStore", idStore);
            values.put("sBranchName", bn);
            sqLiteDatabase.insert("TBLSettings", null, values);

            return "Settings Saved";
            // Log.d("oi", "SaveFeedBack: Clicked");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<clsSettings> loadSettings() {
        ArrayList<clsSettings> settings = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c;
        c = sqLiteDatabase.rawQuery("SELECT idSettings, sURL, sBusinessUnitCode, sBusinessUnit, idStore, sBranchName FROM TBLSettings", null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    clsSettings oSettings = new clsSettings();
                    oSettings.setIdSettings(c.getInt(c.getColumnIndex("idSettings")));
                    oSettings.setsUrl(c.getString(c.getColumnIndex("sURL")));
                    oSettings.setsBuCode(c.getString(c.getColumnIndex("sBusinessUnitCode")));
                    oSettings.setsBU(c.getString(c.getColumnIndex("sBusinessUnit")));
                    oSettings.setIdStore(c.getString(c.getColumnIndex("idStore")));
                    oSettings.setsBN(c.getString(c.getColumnIndex("sBranchName")));
                    settings.add(oSettings);
                } while (c.moveToNext());
            }
        }
        c.close();
        sqLiteDatabase.close();
        return settings;
    }

    public String updateSettings(String url, String buc, String bu, String idStore, String bn) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("sURL", url);
            values.put("sBusinessUnitCode", buc);
            values.put("sBusinessUnit", bu);
            values.put("idStore", idStore);
            values.put("sBranchName", bn);

            sqLiteDatabase.update("TBLSettings", values, "idSettings=1", null);
            sqLiteDatabase.close();

            return "Settings update successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Settings update failed";
    }

    public boolean SaveFeedBack(String fbMonthControl, int fbEmoji, int fbReason, String sDate, String sTime, int FoodQuality, int Cleanliness, int Service, int Atmosphere) {

        String result = getSession();

        SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sTimeStamp = sdfTimeStamp.format(new Date());

        if (!result.equals("Empty")) {

            String dateFromDB = result.substring(0 , 10);
            String currentDate = sTimeStamp.substring(0, 10);
//            Log.d("testlog", "dateFromDB: "+result.substring(0, 10));
//            Log.d("testlog", "currentDate: "+sTimeStamp.substring(0, 10));

            if (!dateFromDB.equals(currentDate)){
                updateSession();
                uploader.uploadFeedback(getFeedback(), loadSettings());
            }

        } else {
            startSession();
        }

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {

            ContentValues values = new ContentValues();
            idFeedback = getUnFeedback();
            String unFeedback = sDate + sTime;
            unFeedback = unFeedback.replace("/", "");
            unFeedback = unFeedback.replace(":", "");
            values.put("unFeedback", unFeedback);
            values.put("FBMonthControl", fbMonthControl);
            values.put("FBEmoji", fbEmoji);
            values.put("FBReason", fbReason);
            values.put("FoodQuality", FoodQuality);
            values.put("Cleanliness", Cleanliness);
            values.put("Service", Service);
            values.put("Atmosphere", Atmosphere);
            values.put("FBStatus", 1);
            values.put("FBTimeStamp", sTimeStamp);

            sqLiteDatabase.insert("TBLFeedBack", null, values);
            return true;
            // Log.d("oi", "SaveFeedBack: Clicked");

        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();

        return false;
    }

    public ArrayList<clsFeedBack> getFeedback() {
        ArrayList<clsFeedBack> feedBacks = new ArrayList<clsFeedBack>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c;

        c = sqLiteDatabase.rawQuery("SELECT idFeedback, unFeedback, FBMonthControl, FBEmoji, FBReason, FoodQuality, Cleanliness, Service, Atmosphere, FBStatus, FBTimeStamp FROM TBLFeedBack WHERE FBStatus = 1", null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    clsFeedBack oFeedback = new clsFeedBack();
                    oFeedback.setIdFeedback(c.getInt(c.getColumnIndex("idFeedback")));
                    oFeedback.setUnFeedback(c.getString(c.getColumnIndex("unFeedback")));
                    oFeedback.setFbMonthControl(c.getString(c.getColumnIndex("FBMonthControl")));
                    oFeedback.setFbEmoji(c.getInt(c.getColumnIndex("FBEmoji")));
                    oFeedback.setFbReason(c.getInt(c.getColumnIndex("FBReason")));
                    oFeedback.setFbFoodQuality(c.getInt(c.getColumnIndex("FoodQuality")));
                    oFeedback.setFbCleanliness(c.getInt(c.getColumnIndex("Cleanliness")));
                    oFeedback.setFbService(c.getInt(c.getColumnIndex("Service")));
                    oFeedback.setFbAtmosphere(c.getInt(c.getColumnIndex("Atmosphere")));
                    oFeedback.setFbStatus(c.getInt(c.getColumnIndex("FBStatus")));
                    oFeedback.setFbTimeStamp(c.getString(c.getColumnIndex("FBTimeStamp")));
                    feedBacks.add(oFeedback);
                } while (c.moveToNext());
            }
        }
        c.close();
        sqLiteDatabase.close();
        return feedBacks;
    }

    public String updateFeedbackStatus(String idFeedback) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("FBStatus", 0);

            sqLiteDatabase.update("TBLFeedBack", values, "unFeedback=?", new String[]{idFeedback});
            sqLiteDatabase.close();

            return "Settings update successfully";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Settings update failed";
    }

    public boolean updateSendDate(String sDate) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("DateSend", sDate);

            sqLiteDatabase.update("TBLFeedBack", values, null, null);
            //  sqLiteDatabase.update("TBLReason", values, null, null);
            sqLiteDatabase.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public void startSession() {
        SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sTimeStamp = sdfTimeStamp.format(new Date());

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put("timeStamp", sTimeStamp);

            sqLiteDatabase.insert("TBLSession", null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.close();

    }

    public String getSession() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c;

        c = sqLiteDatabase.rawQuery("SELECT timeStamp FROM TBLSession", null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    return c.getString(c.getColumnIndex("timeStamp"));
                } while (c.moveToNext());
            }
        }

        c.close();
        sqLiteDatabase.close();

        return "Empty";
    }

    public void updateSession() {
        SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sTimeStamp = sdfTimeStamp.format(new Date());

        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("timeStamp", sTimeStamp);

            sqLiteDatabase.update("TBLSession", values, null, null);
            sqLiteDatabase.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteFeedback(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TBLFeedBack WHERE FBStatus = 0");
        db.execSQL("VACUUM");
        db.close();
    }

//    public Date getFirstFeedbackEntryDate(){
//        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
//        Cursor c;
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y hh:mm");
//        String sDate;
//        Date feedbackDate = Calendar.getInstance().getTime();
//
//        c = sqLiteDatabase.rawQuery("SELECT FBDateStamp, FBTimeStamp FROM TBLFeedBack WHERE idFeedback = 1", null);
//
//        if (c != null) {
//            if (c.moveToFirst()) {
//                do {
//                    try {
//                        sDate = c.getString(c.getColumnIndex("FBDateStamp")) + " " + c.getString(c.getColumnIndex("FBTimeStamp"));
//                        feedbackDate = sdf.parse(sDate);
//
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                        Log.d("dbhelper", "testlog: " + e);
//                    }
//
//                } while (c.moveToNext());
//            }
//        }
//        c.close();
//
//        sqLiteDatabase.close();
//        return feedbackDate;
//    }
}