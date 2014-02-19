package restaurant.tanRestaurant;

import city.helpers.Directory;
import restaurant.Restaurant;
import restaurant.stackRestaurant.Order;
//import restaurant.stackRestaurant.StackWaiterRole.CustomerState;
import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.interfaces.Cook;
import restaurant.stackRestaurant.interfaces.Customer;
import restaurant.stackRestaurant.interfaces.Waiter;
import restaurant.tanRestaurant.TanWaiterRole;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer.state;
import trace.AlertLog;
import trace.AlertTag;

public class TanWaiterSharedRole extends TanWaiterRole implements Waiter{

		public TanWaiterSharedRole(String location) {
			super(location);
			
		}

		
		@Override
		
		protected void PassOrderToCook(MyCustomer myc){
			/*
			 * waiterGui.DoGoToCook();
			try {
				atCook.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//print("Cook, please cook "+ o.getName());
			myc.waiter.cook.msgHereIsAnOrder(myc.table, myc.o, this);
			myc.s=state.waitingForFood;
			waiterGui.DoLeaveCustomer();
			 */
			/*
			 * print("Adding " + customer.c + "'s order to shared data for cook");
		customer.s = state.waitingforfood;
		
		restaurant.getMyMonitor().insert(new Order(this, customer.choice, customer.tableNumber));
			 */
			
			AlertLog.getInstance().logMessage(AlertTag.WAITER, getName(),"Adding " + myc.c + "'s order to shared data for cook");
			myc.s= state.waitingForFood;
			Directory.sharedInstance().getRestaurants().get(5).getMonitor().insert(new Order(this, myc.o.getName(), myc.table, 0));;
		}


		@Override
		public void msgHereIsCheck(Check check) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgCheckPlease(Customer customer) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgYouCanGoOnBreak(boolean canGoOnBreak) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgReadyToOrder(Customer customer) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgGiveOrder(Customer customer, String choice) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgSeatCustomer(Customer customer, int tableNumber,
				int seatNumber) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgOrderDone(String choice, int table, int seat) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgDoneEating(Customer customer) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgFoodEmpty(String choice, int table, int seat) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgIWantToGoOnBreak() {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgImComingOffBreak() {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgCookHere(Cook cook) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void setRestaurant(Restaurant restaurant) {
			// TODO Auto-generated method stub
			
		}
		
		/*
		protected void takeOrderToCook(MyCustomer customer) {
			host.msgWaiterBusy(this);
			DoGoToCook();
			print("Taking " + customer.customer + "'s order to cook");
			try {
				doneAnimation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			customer.state = CustomerState.AtCook;
			cook.msgCookOrder(this, customer.choice, customer.table, customer.seatNum);
		}*/

}
