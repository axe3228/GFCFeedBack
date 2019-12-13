package com.mis.gfcfeedback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.mis.gfcfeedback.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GFCAdvertisement extends AppCompatActivity {

    ArrayList<Bitmap> arrAds;
    ViewFlipper viewFlipper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gfcadvertisement);

        arrAds = new ArrayList<>();
        viewFlipper = findViewById(R.id.v_flipper);

        for (int i=1; i != 4; i++){

            File imgFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Uri file = Uri.fromFile(new File(imgFile, "pic"+i+".jpg"));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , file);
                arrAds.add(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Bitmap image: arrAds){
            flipperAds(image);
        }
    }

    public void flipperAds(Bitmap image){
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(image);
        imageView.setBackgroundResource(R.color.colorBlack);

        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);

        viewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
