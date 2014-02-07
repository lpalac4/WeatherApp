package com.example.weatherappalpha;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Launcher activity will transition to main activity after a timer runs out.
 * @author Luis Palacios 
 */
public class WeatherLauncherActivity extends Activity {
	
	private static final int STARTTIMER = 3000;
	public static int windowHeight;
	public static int windowWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Set some window settings.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		windowHeight = getResources().getDisplayMetrics().heightPixels;
		windowWidth = getResources().getDisplayMetrics().widthPixels;
		
		// Instantiate layouts that will be used in this activity.
		FrameLayout finalLayout = new FrameLayout(this);
		finalLayout.setBackgroundResource(R.drawable.background);
		
		LinearLayout titleLayout = new LinearLayout(this);
		titleLayout.setOrientation(LinearLayout.VERTICAL);
		titleLayout.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		
		
		// Instantiate TextView widgets.
		TextView name = new TextView(this);
		name.setText("Weather App by Luis Palacios");
		TextView poweredBy = new TextView(this);
		poweredBy.setText("This app uses the WorldWeatherOnline API");
		
		// Add views to proper hierarchy.
		titleLayout.addView(name);
		titleLayout.addView(poweredBy);
		finalLayout.addView(titleLayout);
		
		// Instantiate a handler that will transition to main activity after STARTTIMER(ms).
		Handler timer = new Handler();
        timer.postDelayed(
        		new Runnable() {				
        			@Override
        			public void run() {
        				startActivity(new Intent(WeatherLauncherActivity.this, WeatherMainActivity.class));
        				finish();
        			}
        		}, STARTTIMER);
		
		// Set the view to be displayed.
		setContentView(finalLayout);
	}

}
