/**
 * 
 */
package com.example.weathermodels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * The Weather object holds connection and validation information for our GET request and is able to parse the xml response received from 
 * the WorldWeatherOnline API.  It stores the weather information in a model specified by the data requested. 
 * @author Luis Palacios
 */
public class Weather {
	
	// The key given to me by WorldWeatherOnline.
	private static final String KEY = "cvqfx4zj3xj5257exkbrtjy4";
	// The API url.
	public static final String ENDPOINT = "http://api.worldweatheronline.com/free/v1/weather.ashx";
	
	// String containing entire query string that will be passed to URL object.
	protected String query;
	// Model to be filled from parsing Response from world weather.
	protected Conditions[] conditions;
	// InputStream to read the response from the web api.
	private InputStream in;
	
	/**
	 * Only constructor for the weather object.
	 * @param city String to look up.
	 * @param country String to look up.
	 */
	public Weather(String city, String country) {			
		int numDays = 5;
		setQuery(city,country, numDays);
		conditions = null;	
	}
	
	/**
	 * Creates the final query that will be used to ping WorldWeatherOnline.
	 * @param city portion of the string.
	 * @param country String
	 * @param numDays Default is set to 5 as every query to WWO will ask for 5 day forecast.
	 */
	private void setQuery(String city, String country, int numDays) {
		
		String q = ENDPOINT + "?key=" + KEY + "&q=";
		 
		q += reformatLocationQ(city, ",");
		q += reformatLocationQ(country, "&");
		q += "num_of_days=" + numDays + "&format=xml";
		
		query = q;
	}
	
	/**
	 * Helper method for SetQuery that deals with whitespaces.
	 * @param location String of the Geographical location.
	 * @param end Character that will be appended to the end of location.
	 * @return complete string.
	 */
	public String reformatLocationQ(String location, String end){
		String q = "";
		String[] pieces = location.split("\\s+");
		
		for(int i = 0; i< pieces.length; i++){
			if(i == pieces.length-1){
				q += pieces[i] + end;
			}
			else{
				q += pieces[i] + "+";
			}
		}
		
		return q;
	}
	
	/**
	 * Getter for the Conditions array.
	 * @return array of Conditions objects.
	 */
	public Conditions[] getConditions(){
		return conditions;
	}
	
	/**
	 * Beginning of query to WWO, uses a separate Thread for networking as to not affect UI, and also because
	 * Android now requires network tasks to get off the main thread.
	 * @throws Exception for various networking errors.
	 */
	public void callAPI() throws Exception {
		
		// Create new thread.
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {

				try {
					URL url = new URL(query);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setInstanceFollowRedirects(false);
					conn.setReadTimeout(10000 /* milliseconds */);
					conn.setConnectTimeout(15000 /* milliseconds */);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.connect();
					in = conn.getInputStream();
					conditions = getLocalWeatherData(in);
				} catch (MalformedURLException e) {
					
				} catch (IOException e) {

				} catch (Exception e){
					
				}
				finally {
					if(in != null){
						try{
							in.close();
							return;
						}catch(IOException e){
							e.printStackTrace();
						}
						
					}
				}
			}
			
		});
		
		thread.start(); 
		// Wait for thread to finish.
		try{
			thread.join();
		}
		catch(InterruptedException e){
			
		}
	}

	/**
	 * Initializes a XmlPullParser object to begin parsing the InputStream from the HttpURLConnection.
	 * @param is from HttpUrlConneciton 
	 * @return array of Conditions objects that represent 5 days of forecasts.
	 * @throws Exception for IOException and XMLpullParserException. 
	 */
	public Conditions[] getLocalWeatherData(InputStream is) throws Exception {
		int i = 0;
		XmlPullParser xpp = getXmlPullParser(is);;
		String currentTemp;
		String location = "";
		Conditions[] conditionsLocal = new Conditions[5];
		
		// Parse the XML response for the 5 days worth of weather forecasts.
		while(i < 5){
		try {
			
			if(i == 0){
				location = getTextForTag(xpp, "query");
				currentTemp = getTextForTag(xpp, "temp_F");
			}
			else{
				currentTemp = null;
			}
			String date = getTextForTag(xpp, "date");
			String tempMaxF = getTextForTag(xpp, "tempMaxF");
			String tempMinF = getTextForTag(xpp, "tempMinF");
			String weatherIconUrl = getDecode(getTextForTag(xpp, "weatherIconUrl"));
			String weatherDesc = getDecode(getTextForTag(xpp, "weatherDesc"));
			String precipMM = getTextForTag(xpp, "precipMM");
			
			// Generate instance of Conditions object that models the data we are parsing for.
			conditionsLocal[i] = new Conditions(location, date, tempMaxF,tempMinF, currentTemp ,weatherDesc,weatherIconUrl,precipMM);
			i++;
			
		} catch (Exception e) {
			conditions = null;
			throw new Exception("failed to parse input stream" + e.getLocalizedMessage());
		}
		}

		return conditionsLocal;
	}
	
	/**
	 * Helper method that initializes an XmlPullParser object.
	 * @param is InputStream to parse from.
	 * @return new instance of XmlPullParser.
	 */
	public XmlPullParser getXmlPullParser(InputStream is) {
		XmlPullParser xpp = null;

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			xpp = factory.newPullParser();
			xpp.setInput( rd );
		} catch (Exception e) {

		}

		return xpp;
	}
	
	/**
	 * Helper method to parse through the string character data tag in XML.
	 * @param value
	 * @return String in between the character data brackets.
	 */
	public String getDecode(String value) {
		if (value.startsWith("<![CDATA["))
			value = value.substring(9, value.length() - 3);
		return value;
	}
	
	/**
	 * Tag traversal method used with XMLPullParser that keeps travesing tags until a new tag is found or the name of the current element
	 * matches the string we are looking for.
	 * @param xpp
	 * @param tag
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public String getTextForTag(XmlPullParser xpp, String tag) throws XmlPullParserException, IOException {
		while( xpp.next() != xpp.START_TAG || !xpp.getName().equals(tag)){
		}
		//myXppNextTag(xpp)
		xpp.require(xpp.START_TAG, "", tag);
		String text = xpp.nextText();		
		xpp.require(xpp.END_TAG, "", tag);

		return text;
	}
	
}
