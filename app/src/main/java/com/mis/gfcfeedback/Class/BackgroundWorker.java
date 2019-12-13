package com.mis.gfcfeedback.Class;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class BackgroundWorker extends AsyncTask<String,Void, String> {

    Context ctx;
    ArrayList<clsFeedBack> feedBacks;

    public BackgroundWorker(Context ctx, ArrayList<clsFeedBack> feedBacks){
        this.feedBacks = feedBacks;
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (feedBacks.size() != 0){
            Log.d("testlog", "onPreExecute: "+feedBacks.size());
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //String type = params[0];
        String localURL = "http://192.168.9.23/gfcfeedback/main.php";

        try {
            //Params set to insert in mysql
            String FBMonthControl = params[0];
            String FBEmoji = params[1];
            String FBReason = params[2];
            String FoodQuality = params[3];
            String Cleanliness = params[4];
            String Service = params[5];
            String Ambience = params[6];
            String FBDateStamp = params[7];
            String FBTimeStamp = params[8];
            String DateSend = params[9];
            URL url = new URL(localURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = URLEncoder.encode("FBMonthControl", "UTF-8")+"="+URLEncoder.encode(FBMonthControl, "UTF-8")+"&"
                    + URLEncoder.encode("FBEmoji", "UTF-8")+"="+URLEncoder.encode(FBEmoji, "UTF-8")+"&"
                    + URLEncoder.encode("FBReason", "UTF-8")+"="+URLEncoder.encode(FBReason, "UTF-8")+"&"
                    + URLEncoder.encode("FoodQuality", "UTF-8")+"="+URLEncoder.encode(FoodQuality, "UTF-8")+"&"
                    + URLEncoder.encode("Cleanliness", "UTF-8")+"="+URLEncoder.encode(Cleanliness, "UTF-8")+"&"
                    + URLEncoder.encode("Service", "UTF-8")+"="+URLEncoder.encode(Service, "UTF-8")+"&"
                    + URLEncoder.encode("Ambience", "UTF-8")+"="+URLEncoder.encode(Ambience, "UTF-8")+"&"
                    + URLEncoder.encode("FBDateStamp", "UTF-8")+"="+URLEncoder.encode(FBDateStamp, "UTF-8")+"&"
                    + URLEncoder.encode("FBTimeStamp", "UTF-8")+"="+URLEncoder.encode(FBTimeStamp, "UTF-8")+"&"
                    + URLEncoder.encode("DateSend", "UTF-8")+"="+URLEncoder.encode(DateSend, "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line;
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.d("Backgroundworker", "doInBackground: "+result);
            return result;


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
