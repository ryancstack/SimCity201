package city.helpers;


import java.util.HashMap;
import java.util.Map;

import city.gui.BusStop;
import city.gui.Walkway;
import gui.SimCityGui;


public class WalkLoopHelper {
	public static WalkLoopHelper sharedInstance;
	
	private SimCityGui cityGui;
	
	WalkLoopHelper() {
		
	}
	
	public static WalkLoopHelper sharedInstance() {
		if(sharedInstance == null) {
    		sharedInstance = new WalkLoopHelper();
    	}
    	return sharedInstance;
	}
	
	

		
	//RESTAURANT Instantiations
		private Walkway stackRestaurant = new Walkway("StackRestaurant"); //restaurant 1
		Coordinate stackRestaurantLocation = new Coordinate(108,320);
		
		private Walkway huangRestaurant = new Walkway("HuangRestaurant"); //restaurant 2
		Coordinate huangRestaurantLocation = new Coordinate(230,83);
		
		private Walkway nakamuraRestaurant = new Walkway("NakamuraRestaurant"); //restaurant 3
		Coordinate nakamuraRestaurantLocation = new Coordinate(348,83);

		/*
		private Walkway phillipsRestaurant = new Walkway("PhillipsRestaurant"); //restaurant 4
		Coordinate phillipsRestaurantLocation = new Coordinate(719,323);
		*/
		private Walkway shehRestaurant = new Walkway("ShehRestaurant"); //restaurant 5
		Coordinate shehRestaurantLocation = new Coordinate(624,353);
		
		
		private Walkway tanRestaurant = new Walkway("TanRestaurant"); //restaurant 6
		Coordinate tanRestaurantLocation = new Coordinate(327,353);
		
		
	//HOUSES
		private Walkway house1 = new Walkway("House1");
		Coordinate house1Location = new Coordinate(290,308);
		
		private Walkway house2 = new Walkway("House2");
		Coordinate house2Location = new Coordinate(311,127);

		private Walkway house3 = new Walkway("House3");
		Coordinate house3Location = new Coordinate(449,127);
		
		private Walkway house4 = new Walkway("House4");
		Coordinate house4Location = new Coordinate(597,127);
		
		private Walkway house5 = new Walkway("House5");
		Coordinate house5Location = new Coordinate(600,308);//480,283
		
		private Walkway house6 = new Walkway("House6");
		Coordinate house6Location = new Coordinate(480,308);	
		
		
	//MARKETS
		private Walkway market1 = new Walkway("Market1"); //priority market
		Coordinate market1Location = new Coordinate(492,83); //done changing
		
		private Walkway market2 = new Walkway("Market2"); //secondary market
		Coordinate market2Location = new Coordinate(488,353);
		
		public Map<String, Walkway> marketWalkwayDirectory = new HashMap<String, Walkway>(); {
			marketWalkwayDirectory.put("Market1", market1);
			marketWalkwayDirectory.put("Market2", market2);
		}
		
		
	//APARTMENTS
		private Walkway apartmentA = new Walkway("ApartmentA"); //smaller limited apartment
		Coordinate apartmentALocation = new Coordinate(214,308);
		
		private Walkway apartmentB = new Walkway("ApartmentB"); //larger infinite apartment
		Coordinate apartmentBLocation = new Coordinate(605,83);
		
		private Walkway apartmentC = new Walkway("ApartmentC"); //larger infinite apartment
		Coordinate apartmentCLocation = new Coordinate(719,218);

	//BANKS
		private Walkway bank = new Walkway("Bank");
		Coordinate bankLocation = new Coordinate(108,213); //d
		
		private Walkway bank2 = new Walkway("Bank2");
		Coordinate bankLocation2 = new Coordinate(719,99);
	
		private Walkway busStop1 = new Walkway("BusStop1"); //bottom left
		Coordinate busStop1Location = new Coordinate(163,353);
		
		private Walkway busStop2 = new Walkway("BusStop2"); //bottom right
		Coordinate busStop2Location = new Coordinate(700,353);
		
		private Walkway busStop3 = new Walkway("BusStop3"); //top right
		Coordinate busStop3Location = new Coordinate(719, 102);//370);
		
		private Walkway busStop4 = new Walkway("BusStop4"); //top left
		Coordinate busStop4Location = new Coordinate(118,83);//(737,107);
		
		
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
			
			//BUs stops
			locationDirectory.put(busStop1.getName(), busStop1Location);
			locationDirectory.put(busStop2.getName(), busStop2Location);
			locationDirectory.put(busStop3.getName(), busStop3Location);
			locationDirectory.put(busStop4.getName(), busStop4Location);
			/*
			locationDirectory.put(phillipsRestaurant.getName(), phillipsRestaurantLocation);
			*/
			
			
		}
		
		public Map<String, String> loopEvaluator = new HashMap<String, String>(); {
			loopEvaluator.put("Bank", "OuterLoop");
			loopEvaluator.put("Bank2", "OuterLoop");
			loopEvaluator.put("House1", "InnerLeftLoop");
			loopEvaluator.put("House2", "InnerLeftLoop");
			loopEvaluator.put("House3", "InnerRightLoop");
			loopEvaluator.put("House4", "InnerRightLoop");
			loopEvaluator.put("House5", "InnerRightLoop");
			loopEvaluator.put("House6", "InnerRightLoop");
			
			loopEvaluator.put("ApartmentA", "InnerLeftLoop");
			loopEvaluator.put("ApartmentB", "OuterLoop");
			loopEvaluator.put("ApartmentC", "OuterLoop");
			
			loopEvaluator.put("Market", "OuterLoop");
			loopEvaluator.put("Market2", "OuterLoop");
			
			loopEvaluator.put("StackRestaurant", "OuterLoop");
			loopEvaluator.put("ShehRestaurant", "OuterLoop");
			loopEvaluator.put("HuangRestaurant", "OuterLoop");
			loopEvaluator.put("TanRestaurant","OuterLoop");
			loopEvaluator.put("NakamuraRestaurant","OuterLoop");
			
			loopEvaluator.put("BusStop1", "OuterLoop");
			loopEvaluator.put("BusStop2", "OuterLoop");
			loopEvaluator.put("BusStop3", "OuterLoop");
			loopEvaluator.put("BusStop4","OuterLoop");
		}
		
		public Map<String, Coordinate> coordinateEvaluator = new HashMap<String, Coordinate>(); {
			coordinateEvaluator.put("Bank", bankLocation);
			coordinateEvaluator.put("Bank2", bankLocation2);
			coordinateEvaluator.put("House1", house1Location);
			coordinateEvaluator.put("House2", house2Location);
			coordinateEvaluator.put("House3", house3Location);
			coordinateEvaluator.put("House4", house4Location);
			coordinateEvaluator.put("House5", house5Location);
			coordinateEvaluator.put("House6", house6Location);
			
			coordinateEvaluator.put("ApartmentA", apartmentALocation);
			coordinateEvaluator.put("ApartmentB", apartmentBLocation);
			coordinateEvaluator.put("ApartmentC", apartmentCLocation);
			
			coordinateEvaluator.put("Market", market1Location);
			coordinateEvaluator.put("Market2", market2Location);
			
			coordinateEvaluator.put("StackRestaurant", stackRestaurantLocation);
			coordinateEvaluator.put("ShehRestaurant", shehRestaurantLocation);
			coordinateEvaluator.put("HuangRestaurant", huangRestaurantLocation);
			coordinateEvaluator.put("TanRestaurant", tanRestaurantLocation);
			coordinateEvaluator.put("NakamuraRestaurant", nakamuraRestaurantLocation);
			
			coordinateEvaluator.put("BusStop1", busStop1Location);
			coordinateEvaluator.put("BusStop2", busStop2Location);
			coordinateEvaluator.put("BusStop3", busStop3Location);
			coordinateEvaluator.put("BusStop4", busStop4Location);
		}
		
		public Map<String, String> getloopEvaluator(){
			return loopEvaluator;
		}
		
		public Map<String, Coordinate> getCoordinateEvaluator(){
			return coordinateEvaluator;
		}
		
}
