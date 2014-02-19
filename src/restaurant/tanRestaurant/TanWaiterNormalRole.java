package restaurant.tanRestaurant;

import restaurant.tanRestaurant.TanWaiterRole;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer.state;

public class TanWaiterNormalRole extends TanWaiterRole{

		public TanWaiterNormalRole(String location) {
			super(location);
			
		}

		
		@Override
		
		protected void PassOrderToCook(MyCustomer myc){
			waiterGui.DoGoToCook();
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
