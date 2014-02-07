package com.example.weatherappalpha;

import com.example.weathermodels.Conditions;
import com.example.weathermodels.Weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The ResultsActivity is the common footing on which the parsed data is laid upon.  It is designed dynamically to hold the necessary 
 * weather forecast information that the user requested in the previous Activity.
 * @author Luis Palacios
 *
 */
public class ResultsActivity extends Activity {
	
	// The query the user entered for the city/zipcode.
	private TextView cityResults;
	// The users query for the country.
	private TextView countryResults;
	// Weather object instantiated upon verification of the response from WWO.
	public static Weather searchWeather;
	// Button that allow the user to saved weather information to the main activity.
	private Button setAsHomeButton;
	
	/** UI elements designed and initialized dynamically **/
	private LinearLayout finalLayout;
	private ConditionsView[] allConditionsViews;
	private ConditionsView singleCondition;
	private LinearLayout singleDay;
	private LinearLayout fiveDay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		// Get the passed primitives from the previous Activity.
		String city = getIntent().getStringExtra("city");
		String country = getIntent().getStringExtra("country");
		
		finalLayout = new LinearLayout(this);
		finalLayout.setOrientation(LinearLayout.VERTICAL);
		finalLayout.setWeightSum(1.0f);
		finalLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		LinearLayout locLayout = new LinearLayout(this);
		locLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.2f));
		cityResults = new TextView(this);
		cityResults.setText("Your search : " + city );
		
		countryResults = new TextView(this);
		countryResults.setText(", " + country + "  ");
		locLayout.addView(cityResults);
		locLayout.addView(countryResults);

		allConditionsViews = new ConditionsView[5];
		TextView actualQuery = new TextView(this);
		actualQuery.setText("Actual Query: " + searchWeather.getConditions()[0].location);
		locLayout.addView(actualQuery);
		
		// Store weather information for one day to be displayed.
		singleCondition = new ConditionsView(this, searchWeather.getConditions()[0]);
		
		// Store weather information for five days. 
		for(int i = 0; i < searchWeather.getConditions().length; i++){		
			allConditionsViews[i] = new ConditionsView(this, searchWeather.getConditions()[i]);
		}
			
		FrameLayout centerView = new FrameLayout(this);
		centerView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.6f));
		centerView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showFullForecast();
				
			}
		});
		
		singleDay = new LinearLayout(this);
		singleDay.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		singleCondition.getView().setGravity(Gravity.CENTER);
		singleDay.addView(singleCondition.getView());
		
		fiveDay = new LinearLayout(this);
		fiveDay.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		for(ConditionsView newView : allConditionsViews){
			fiveDay.addView(newView.getView());
		}
		
		centerView.addView(singleDay);
		centerView.addView(fiveDay);
		fiveDay.setVisibility(LinearLayout.GONE);

		setAsHomeButton = new Button(this);
		setAsHomeButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.2f));
		setAsHomeButton.setText("Set As Home");
		setAsHomeButton.setOnClickListener(new View.OnClickListener() {
			// Using class variables to update the main menu weather information.
			@Override
			public void onClick(View v) {
				Conditions currentC = searchWeather.getConditions()[0];
				WeatherMainActivity.homeCity.setText(currentC.location + "\n" + currentC.currentTemp + "F" + "\n" + currentC.description);			
			}
		});
		
		finalLayout.addView(locLayout);
		finalLayout.addView(centerView);
		finalLayout.addView(setAsHomeButton);
		finalLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showFullForecast();				
			}
		});
		
		setContentView(finalLayout);
	}
	/**
	 * Simple method that swaps views from the view of the user.
	 */
	public void showFullForecast(){
		singleDay.setVisibility(View.GONE);
		fiveDay.setVisibility(View.VISIBLE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	public void invalidateViews() {
		findViewById(android.R.id.content).invalidate();
	}

}
