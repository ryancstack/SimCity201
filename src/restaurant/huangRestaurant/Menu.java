package restaurant.huangRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Menu {
	public List<String> foods = new ArrayList<String>();
	Menu() {
		foods.add("Steak");
		foods.add("Pizza");
		foods.add("Chicken");
		foods.add("Salad");
	}
	public String randomChoice() {
		//Stub for true randomness
		Random rng = new Random();
		int r = rng.nextInt();
		if ((r % 4) == 0) {
			return "Steak";
		}
		if ((r % 4) == 1) {
			return "Pizza";
		}
		if ((r % 4) == 2) {
			return "Chicken";
		}
		else {
			return "Salad";
		}
	}
}
