package restaurant.nakamuraRestaurant;


public class NakamuraWaiterNormalRole extends NakamuraWaiterRole {
	
	public NakamuraWaiterNormalRole(String location) {
		super(location);
	}

	@Override
	protected void PlaceOrder(Cust customer) {
		print("Placing order");
		DoPlaceOrder(customer);
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.msgCookOrder(this, customer.choice, customer.tableNumber);
		customer.s = state.waitingforfood;
		stateChanged();
	}
}
