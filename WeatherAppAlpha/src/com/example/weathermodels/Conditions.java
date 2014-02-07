/**
 * 
 */
package com.example.weathermodels;

/**
 * The model that will hold the data we need to parse from the response back from WorldWeatherOnline.  This data represents a day's weather forecast.  
 * @author Luis Palacios
 */
public class Conditions {

	public String currentTemp;
	public String maxTemp;
	public String minTemp;
	public String description;
	public String icon;
	public String precipitation;
	public String date;
	public String location;
	
	/**
	 * Default constructor.  Check for null on currentTemp;
	 * @param tempMaxF
	 * @param tempMinF
	 * @param currentTemp_
	 * @param weatherDesc
	 * @param weatherIconUrl
	 * @param precipMM
	 */
	public Conditions(String location_,String date_, String tempMaxF,String tempMinF, String currentTemp_ , String weatherDesc, String weatherIconUrl, String precipMM){
		location = location_;
		date = date_;
		currentTemp = currentTemp_;
		maxTemp = tempMaxF;
		minTemp = tempMinF;
		description = weatherDesc;
		icon = weatherIconUrl;
		precipitation = precipMM;
	
	}
		
}
