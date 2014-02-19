package restaurant.shehRestaurant.helpers;

import restaurant.shehRestaurant.ShehCustomerRole;


public class Table {
	ShehCustomerRole occupiedBy;
	int tableNumber;

	public Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public void setOccupant(ShehCustomerRole cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	public ShehCustomerRole getOccupant() {
		return occupiedBy;
	}

	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
	
	public int getTableNumber() {
		return tableNumber;
	}
}