package com.mjm.hello.navar_mrigaya;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.mjm.hello.navar_mrigaya.CheckPermissions.RequestPermissionCode;

public class SplashScreen extends AppCompatActivity {

    Animation fromtop,frombottom,fadein,fadeout;
    ImageView nav,ar;
    TextView itex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadeinfast, R.anim.fadeinfast);
        setContentView(R.layout.activity_splash_screen);

        Context context;

        nav=findViewById(R.id.nav);
        ar=findViewById(R.id.ar);
        itex=findViewById(R.id.iText);

        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fadein=AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout=AnimationUtils.loadAnimation(this,R.anim.fadeout);

        nav.setAnimation(fromtop);
        ar.setAnimation(frombottom);
        itex.setAnimation(fadein);
        //itex.setAnimation(fadeout);

                //Toast.makeText(
                        //SplashScreen.this,
                        //"Initializing...",
                       // Toast.LENGTH_SHORT
                //).show();

        new Timer().schedule(new TimerTask(){
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivityInterlude.class));
                finish();
            }
        }, 2000);

    }
}
