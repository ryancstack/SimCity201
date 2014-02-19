package home;

public class Apartment {

	private String name;
	
	public Apartment() {
		
	}
	
	public Apartment(String buildingName) {
		name = buildingName;
	}
	
	public Object getOwner() {
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

}