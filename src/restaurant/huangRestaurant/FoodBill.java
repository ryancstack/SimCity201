package restaurant.huangRestaurant;

public class FoodBill {
	public String order;
	public int supply;
	public double price;
	public CheckState state;
	public FoodBill (String type, int quantity) {
		this.order = type;
		this.supply = quantity;
		if(order == "Chicken") {
			this.price = quantity * 3.00;
		}
		else if(order == "Steak") {
			this.price = quantity * 5.00;
		}
		else if(order == "Pizza") {
			this.price = quantity * 4.00;
		}
		else if(order == "Salad") {
			this.price = quantity * 2.00;
		}
	}
}
