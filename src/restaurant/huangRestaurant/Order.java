package restaurant.huangRestaurant;

public class Order {
	public enum OrderState {Pending, Cooking, Done, Plated, out};
	public HuangWaiterRole w;
	public String choice;
	public int table;
	public OrderState state;
	Order(HuangWaiterRole w, String choice, int table) {
		this.w = w;
		this.choice = choice;
		this.table = table;
		this.state = OrderState.Pending;
	}
}