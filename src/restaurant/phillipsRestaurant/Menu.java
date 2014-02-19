package restaurant.phillipsRestaurant;
import java.util.ArrayList;
public class Menu {

	public ArrayList<String> choices = new ArrayList<String>();
	public ArrayList<Double> costs = new ArrayList<Double>();
	int choiceNum;
	public Menu(){
		choices.add("steak");
		choices.add("chicken");
		choices.add("salad");
		choices.add("pizza");
		costs.add(15.99);
		costs.add(10.99);
		costs.add(5.99);
		costs.add(8.99);
		choiceNum = 0;
	}
	public String pickRandFood(int rand){
		switch(rand){
		case 0:
			choiceNum = 0;
			return choices.get(0);
		case 1:
			choiceNum = 1;
			return choices.get(1);
		case 2:
			choiceNum = 2;
			return choices.get(2);
		case 3:
			choiceNum = 3;
			return choices.get(3);
		}
		return null;
	}
}

