package market;

public class MarketItemInformation {
	protected int quantity;
	protected String name;
	protected double price;
	
	public MarketItemInformation(String name, int inventory, double price) {
		this.name = name;
		quantity = inventory;
		this.price = price;
	}
	
	public void setSupply(int quantity) {
		this.quantity = quantity;
	}

	public int getSupply() {
		return quantity;
	}
}
