package com.mis.gfcfeedback.Class;
//Solenya, eihcra

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class clsUploader {

    private RequestQueue requestQueue;
    private Context ctx;

    //private String localURL = "http://10.1.1.3/gfcfeedback/main.php";
    private String sDate = getDateTime();
    private String result;

    public clsUploader (Context ctx){
        this.ctx = ctx;
    }

    public void uploadFeedback(final ArrayList<clsFeedBack> arrFeedbacks, final ArrayList<clsSettings> settings){

        final DBHelper dbHelper = new DBHelper(ctx);
        int c = 0;
        String url;

        requestQueue = Volley.newRequestQueue(ctx); //Is used to stack your request and handles your cache. - "https://developer.android.com/training/volley/requestqueue.html"

        if (dbHelper.loadSettings().size() == 0 || dbHelper.loadSettings().get(0).getsUrl().equals("")){
            Toast.makeText(ctx, "URL is needed. Contact MIS Department", Toast.LENGTH_SHORT).show();
            return;
        }else {
            url = dbHelper.loadSettings().get(0).getsUrl();
        }

        for (final clsFeedBack cuur: arrFeedbacks) {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() { //Connect to target php web
                @Override
                public void onResponse(String response) { //Web response to the request
                    result = response;

                    if (response.equals("Insert Successful")){
                       // dbHelper.deleteFeedback(cuur.getUnFeedback());
                        dbHelper.updateFeedbackStatus(cuur.getUnFeedback());
                        Log.d("testlog", "onResponse: "+response);

                    }else if (response.equals("Delete all 0 status")){
                        dbHelper.deleteFeedback();

                    }else {
                        Log.d("testlog", "onResponse: "+response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) { //Failed to connect response
                    Log.d("testlog", "onErrorResponse: "+error);
                }
            }){
                @Override
                protected Map<String, String> getParams(){ //Data to be insert in Database
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("unFeedback", cuur.getUnFeedback());
                    parameters.put("fbMonthControl", cuur.getFbMonthControl());
                    parameters.put("fbEmoji", String.valueOf(cuur.getFbEmoji()));
                    parameters.put("fbReason", String.valueOf(cuur.getFbReason()));
                    parameters.put("fbFoodQuality", String.valueOf(cuur.getFbFoodQuality()));
                    parameters.put("fbCleanliness", String.valueOf(cuur.getFbCleanliness()));
                    parameters.put("fbService", String.valueOf(cuur.getFbService()));
                    parameters.put("fbAmbience", String.valueOf(cuur.getFbAtmosphere()));
                    parameters.put("fbTimeStamp", cuur.getFbTimeStamp());
                    parameters.put("BUCode", settings.get(0).getsBuCode());
                    parameters.put("BUName", settings.get(0).getsBU());
                    parameters.put("idStore", settings.get(0).getIdStore());
                    parameters.put("SBranchName", settings.get(0).getsBN());

                    //dbHelper.updateFeedbackStatus(cuur.getIdFeedback());

                    return parameters;
                }
            };

            requestQueue.add(request); //If connection is successful send data

            c = c + 1;

            if (c == arrFeedbacks.size()){
                Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDateTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y");

        return sdf.format(currentTime);
    }
}
