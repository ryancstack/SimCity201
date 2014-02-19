package city.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import city.PersonAgent;
import city.TransportationRole;
import city.gui.BusStop;
import city.gui.Driveway;
import city.gui.StreetCorner;
import city.interfaces.Transportation;
import market.Market;
import bank.Bank;
import gui.MacroAnimationPanel;
import gui.SimCityGui;
import home.Apartment;
import home.Home;
import restaurant.Restaurant;
import restaurant.huangRestaurant.HuangRestaurant;
import restaurant.shehRestaurant.ShehRestaurant;
import restaurant.stackRestaurant.*;
import restaurant.tanRestaurant.TanRestaurant;

public class DrivewayHelper {
	public static DrivewayHelper sharedInstance;
	
	private SimCityGui cityGui;
	
	DrivewayHelper() {
		
	}
	
	public static DrivewayHelper sharedInstance() {
		if(sharedInstance == null) {
    		sharedInstance = new DrivewayHelper();
    	}
    	return sharedInstance;
	}
	

		
	//RESTAURANT Instantiations
		private Driveway stackRestaurant = new Driveway("StackRestaurant"); //restaurant 1
		Coordinate stackRestaurantLocation = new Coordinate(136,320);
		
		private Driveway huangRestaurant = new Driveway("HuangRestaurant"); //restaurant 2
		Coordinate huangRestaurantLocation = new Coordinate(227,110);
		
		private Driveway nakamuraRestaurant = new Driveway("NakamuraRestaurant"); //restaurant 3
		Coordinate nakamuraRestaurantLocation = new Coordinate(334,93);

		/*
		private Driveway phillipsRestaurant = new Driveway("PhillipsRestaurant"); //restaurant 4
		Coordinate phillipsRestaurantLocation = new Coordinate(685,320);
		*/
		private Driveway shehRestaurant = new Driveway("ShehRestaurant"); //restaurant 5
		Coordinate shehRestaurantLocation = new Coordinate(621,320);
		
		
		private Driveway tanRestaurant = new Driveway("TanRestaurant"); //restaurant 6
		Coordinate tanRestaurantLocation = new Coordinate(380,320);
		
		
	//HOUSES
		private Driveway house1 = new Driveway("House1");
		Coordinate house1Location = new Coordinate(290,320);
		
		private Driveway house2 = new Driveway("House2");
		Coordinate house2Location = new Coordinate(367,110);

		private Driveway house3 = new Driveway("House3");
		Coordinate house3Location = new Coordinate(449,110);
		
		private Driveway house4 = new Driveway("House4");
		Coordinate house4Location = new Coordinate(597,110);
		
		private Driveway house5 = new Driveway("House5");
		Coordinate house5Location = new Coordinate(480,320);//480,283
		
		private Driveway house6 = new Driveway("House6");
		Coordinate house6Location = new Coordinate(290,320);	
		
		
	//MARKETS
		private Driveway market1 = new Driveway("Market1"); //priority market
		Coordinate market1Location = new Coordinate(494,110); //done changing
		
		private Driveway market2 = new Driveway("Market2"); //secondary market
		Coordinate market2Location = new Coordinate(488,320);
		
		public Map<String, Driveway> marketDrivewayDirectory = new HashMap<String, Driveway>(); {
			marketDrivewayDirectory.put("Market1", market1);
			marketDrivewayDirectory.put("Market2", market2);
		}
		
		
	//APARTMENTS
		private Driveway apartmentA = new Driveway("ApartmentA"); //smaller limited apartment
		Coordinate apartmentALocation = new Coordinate(211,320);
		
		private Driveway apartmentB = new Driveway("ApartmentB"); //larger infinite apartment
		Coordinate apartmentBLocation = new Coordinate(668,110);
		
		private Driveway apartmentC = new Driveway("ApartmentC"); //larger infinite apartment
		Coordinate apartmentCLocation = new Coordinate(685,730);

	//BANKS
		private Driveway bank = new Driveway("Bank");
		Coordinate bankLocation = new Coordinate(136,160); //d
		
		private Driveway bank2 = new Driveway("Bank2");
		Coordinate bankLocation2 = new Coordinate(685,122);
		
		/*
		public Map<String, String> busStopEvaluator = new HashMap<String, String>(); {
			busStopEvaluator.put("Bank", busStop4Location);
			
			busStopEvaluator.put("House1", busStop1Location);
			busStopEvaluator.put("House2", busStop4Location);
			busStopEvaluator.put("House3", busStop3Location);
			busStopEvaluator.put("House4", busStop3Location);
			busStopEvaluator.put("House5", busStop2Location);
			busStopEvaluator.put("House6", busStop2Location);
			
			busStopEvaluator.put("ApartmentA", busStop1Location);
			busStopEvaluator.put("ApartmentB", busStop3Location);
			busStopEvaluator.put("ApartmentC", busStop2Location);
			
			busStopEvaluator.put("Market1", busStop3Location);
			busStopEvaluator.put("Market2", busStop2Location);
			
			busStopEvaluator.put("StackRestaurant", busStop1Location);
			busStopEvaluator.put("ShehRestaurant", busStop2Location);
			busStopEvaluator.put("HuangRestaurant", busStop4Location);
		}*/
		
		public Map<String, Coordinate> locationDirectory = new HashMap<String, Coordinate>(); {
			//Bank
			locationDirectory.put(bank.getName(), bankLocation);
			locationDirectory.put(bank2.getName(), bankLocation2);
			
			//Markets
			locationDirectory.put(market1.getName(), market1Location);
			locationDirectory.put(market2.getName(), market2Location);
			
			//Apartments
			locationDirectory.put(apartmentA.getName(), apartmentALocation);
			locationDirectory.put(apartmentB.getName(), apartmentBLocation);
			locationDirectory.put(apartmentC.getName(), apartmentCLocation);
			
			//Homes
			locationDirectory.put(house1.getName(), house1Location);
			locationDirectory.put(house2.getName(), house2Location);
			locationDirectory.put(house3.getName(), house3Location);
			locationDirectory.put(house4.getName(), house4Location);
			locationDirectory.put(house5.getName(), house5Location);
			locationDirectory.put(house6.getName(), house6Location);
			
			//Restaurants
			locationDirectory.put(stackRestaurant.getName(), stackRestaurantLocation);
			
			locationDirectory.put(shehRestaurant.getName(), shehRestaurantLocation);
			
			locationDirectory.put(huangRestaurant.getName(), huangRestaurantLocation);
			
			locationDirectory.put(tanRestaurant.getName(), tanRestaurantLocation);

			locationDirectory.put(nakamuraRestaurant.getName(), nakamuraRestaurantLocation);
			/*
			locationDirectory.put(phillipsRestaurant.getName(), phillipsRestaurantLocation);
			*/
			
		}
		
}
