package restaurant.tanRestaurant;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import restaurant.Restaurant;
import restaurant.tanRestaurant.*;
//import restaurant.stackRestaurant.*;
import city.PersonAgent;


public class TanRestaurant extends Restaurant {

	private String name;
	TanHostAgent host;
	TanCashierAgent cashier;
	double till = 10000;
	
	

	public TanRestaurant(String name) {
		super();
		this.name = name;
		host = new TanHostAgent("Host");
		cashier = new TanCashierAgent("Cashier");
		//System.out.println(host.toString() + "This Is Ben's Host!!");
		//System.out.println(cashier.toString() + "I obviously exist too.");
		cashier.setRestaurant(this);
		host.setRestaurant(this);
		host.startThread();
		cashier.startThread();	
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public TanHostAgent getHost() {
		return host;
	}
	
	public TanCashierAgent getCashier() {
		return cashier;
	}
	
	public double getTill() {
		return till;
	}

	public void setTill(double till) {
		this.till = till;
	}

}
