package com.mjm.hello.navar_mrigaya;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivityInterlude extends AppCompatActivity {

    Animation fadein;
    ImageView navar;
    Button btn,exp;
//AIzaSyBc1LPI_Tc9PoujwEIE5J8SgDaMftaXAho
//AIzaSyC-3slHSRDKD60zhVNyrMs5WocKT3tax5Q
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadeinfast, R.anim.fadeinfast);
        setContentView(R.layout.activity_main_interlude);

        btn=findViewById(R.id.getstarted);
        exp=findViewById(R.id.experimental);
        fadein=AnimationUtils.loadAnimation(this, R.anim.fadeinfast);
        btn.setAnimation(fadein);


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityInterlude.this, MapsActivity.class));
                //finish();
            }
        });

        exp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityInterlude.this, MainActivity.class));
                //finish();
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press 'BACK' again to exit app...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
