package restaurant.phillipsRestaurant;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import restaurant.Restaurant;
import restaurant.phillipsRestaurant.*;
import city.PersonAgent;


public class PhillipsRestaurant extends Restaurant {

	private String name;
	PhillipsHostAgent host;
	PhillipsCashierAgent cashier;
	double till = 10000;
	
	

	public PhillipsRestaurant(String name) {
		super();
		this.name = name;
		host = new PhillipsHostAgent("Richard");
		cashier = new PhillipsCashierAgent("Marty");
		host.startThread();
		cashier.startThread();	
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public PhillipsHostAgent getHost() {
		return host;
	}
	
	public PhillipsCashierAgent getCashier() {
		return cashier;
	}
	
	public double getTill() {
		return till;
	}

	public void setTill(double till) {
		this.till = till;
	}

}
