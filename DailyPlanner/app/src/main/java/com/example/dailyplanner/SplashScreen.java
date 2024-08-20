package com.example.dailyplanner;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;



public class SplashScreen extends AppCompatActivity {

    //declaring splash screen related components
    private ImageView ss_imageView;
    private ImageView timely_logo;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //initializing the components by Id
        ss_imageView =  (ImageView) findViewById(R.id.ss_imageView);
        timely_logo = (ImageView) findViewById(R.id.timely_logo);


        //setting the splash screen animation to the splash screen
        Animation splashScreenAnim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        ss_imageView.startAnimation(splashScreenAnim);

        // Setting up animation listener for the main logo animation
        splashScreenAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // This method is called when the animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // This method is called when the animation ends
                // Start animation for "timely" logo after main logo animation completes
                Animation slideIn = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.slide_in_from_right);
                timely_logo.setVisibility(View.VISIBLE); // Make "timely" logo visible before starting animation
                timely_logo.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // This method is called when the animation is repeated
            }
        });


        //creating an intent to continue with the menu screen activity
        final Intent intent = new Intent(this, MainScreen.class);

        //setting a timer to automatically continue to the main menu screen after splash screen
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}