package com.flipo.avivams.flipo.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.erezd.animative.R;
import com.example.erezd.animative.activities.MainActivity;
import com.flipo.avivams.flipo.activities.DoodlesActivity;

public class SplashScreen extends AppCompatActivity {

    private final int f_SPLASH_DELAY = 1000;
    private final Handler m_Handler = new Handler();
    private final Launcher m_Launcher = new Launcher();
    private boolean m_Visible = true;
    private boolean m_ForceStart = false;

    @Override
    protected void onStart() {
        super.onStart();

        m_Handler.postDelayed(m_Launcher, f_SPLASH_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onPause() {
        m_Visible = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_Visible = true;
        //checks if we returned from background and the handler timer has finished
        if(m_ForceStart){
            launch();
        }
    }

    private void launch(){
        if(!isFinishing()){
            //checks if the app on the foreground
            if(m_Visible) {
                startActivity(new Intent(this, DoodlesActivity.class));
                overridePendingTransition(R.anim.splashscreen_fade_in, R.anim.splashscreen_fade_out);
                finish();
            }
            else{
                //here the app is in the background but the handler finished
                m_ForceStart = true;
            }
        }
    }

    @Override
    protected void onStop() {
        m_Handler.removeCallbacks(m_Launcher);
        super.onStop();
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
