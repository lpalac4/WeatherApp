package com.example.weatherappalpha;

import com.example.weathermodels.Weather;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Main activity used for the application.  Allows a user to enter their query into two different edit text boxes and will check if a WWO responds
 * with valid information before it transitions to the results activity.
 * @author Luis Palacios
 */
public class WeatherMainActivity extends FragmentActivity {
	// Final layout of activity.
	private LinearLayout finalLayout;
	// Button that can be updated with queried information.
	public static Button homeCity;
	// Edit box to enter city or zip code.
	private EditText cityInput;
	// Edit box to enter country if needed.
	private EditText countryInput;
	// Intent to transition to results activity.
	protected Intent resultsActivityIntent;
	// Text box with a prompt to guide user.
	private TextView prompt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		// Initialize final layout to be a vertical layout that spans the entire screen.
		finalLayout = new LinearLayout(this);
		finalLayout.setOrientation(LinearLayout.VERTICAL);
		finalLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		finalLayout.setWeightSum(1.0f);
		// Initialize intent to be used later on.
		resultsActivityIntent = new Intent(WeatherMainActivity.this, ResultsActivity.class);
		
		// Lower level views have their weights assigned accordingly.
		LinearLayout headerLayout = new LinearLayout(this);
		headerLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.2f));
		headerLayout.setWeightSum(1.0f);
		
		homeCity = new Button(this);
		homeCity.setText("The North Pole is warmer than Chicago.");
		homeCity.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.6f));
		
		Button settings = new Button(this);
		settings.setText("Exit");
		settings.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.4f));
		settings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exitApp();
				
			}
		});
		
		headerLayout.addView(homeCity);
		headerLayout.addView(settings);
		
		prompt = new TextView(this);
		prompt.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.3f));
		prompt.setText("Enter the city or zipcode and possibly the country to look up.");
		prompt.setGravity(Gravity.BOTTOM);
		
		/** 
		 * The edit text boxes required listeners for both on click (to erase the current entry) and on edit changes to 
		 * know when the user presser Enter on the keyboard.
		 */
		cityInput = new EditText(this);
		cityInput.setText("city");
		cityInput.setSingleLine();
		cityInput.setFocusableInTouchMode(true);
		cityInput.requestFocus();
		cityInput.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f));
		
		// On click listener to make deleting entries easier.
		cityInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cityInput.setText("");				
			}
		});
		
		// Listener that eats up events where the user hits enter on the edit box.
		cityInput.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                in.hideSoftInputFromWindow(countryInput.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);	               
	               return true;
	            }
				return false;
			}
		});
		
		countryInput = new EditText(this);
		countryInput.setText("country");
		countryInput.setFocusableInTouchMode(true);
		countryInput.requestFocus();
		countryInput.setSingleLine();
		countryInput.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f));
		// On click listener to make deleting entries easier.
		countryInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				countryInput.setText("");				
			}
		});
		// Listener that eats up events where the user hits enter on the edit box.
		countryInput.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                in.hideSoftInputFromWindow(countryInput.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);	               
	               return true;
	            }
				return false;
			}
		});
		/** end of edit boxes **/
		
		LinearLayout entryLayout = new LinearLayout(this);
		entryLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.3f));
		entryLayout.setWeightSum(1.0f);
		entryLayout.addView(cityInput);
		entryLayout.addView(countryInput);
		
		// The search button is what initializes the GET request to WWO and verifies a valid reponse.
		// Only then does it call the next activity.
		Button searchButton = new Button(this);
		searchButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 0.2f));
		searchButton.setText("Get Weather");
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			
			// The registered listener starts a validation process to insure the user enters proper information and 
			// that the server responds.
			@Override
			public void onClick(View v) {
				String city = cityInput.getText().toString();
				String country = countryInput.getText().toString();

				if(city.equals("") || city.equals("city")){
					prompt.setText("Make sure to enter the city/zipcode and if necessary country");					
				}
				else{
					resultsActivityIntent.putExtra("city", city);
					resultsActivityIntent.putExtra("country", country);
					try{
						ResultsActivity.searchWeather = new Weather(city, country);
						ResultsActivity.searchWeather.callAPI();
						if(ResultsActivity.searchWeather.getConditions() == null){
							prompt.setText("Entry did not return valid data");
						}
						else{
							startActivity(resultsActivityIntent);
						}
					}
					catch(Exception e){
						prompt.setText("Entry did not return valid data");
					}
				}

			}				

		});
		
		// Add views to the final layout view.
		finalLayout.addView(headerLayout);
		finalLayout.addView(prompt);
		finalLayout.addView(entryLayout);
		finalLayout.addView(searchButton);
		setContentView(finalLayout);
	}

	protected void exitApp() {
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather_main, menu);
		return true;
	}

	public void startWebServiceSearch(String city, String country) {
		
	}
	

}
