package home.helpers;
import home.LandlordRole.PayState;
import home.interfaces.HomePerson;

import java.util.*;

import city.interfaces.Person;



public class TenantList {
	
	private List<Tenant> tenants1 = Collections.synchronizedList(new ArrayList<Tenant>());
	private List<Tenant> tenants2 = Collections.synchronizedList(new ArrayList<Tenant>());
	private List<Tenant> tenants3 = Collections.synchronizedList(new ArrayList<Tenant>());
    
    private static TenantList sharedInstance = null;
    
    public class Tenant{
		Person inhabitant;
	 	double moneyOwed;
	 	PayState state;
	 	
	 	public Tenant(Person tenant) {
	 		this.inhabitant = tenant;
	 		this.moneyOwed = 0;
	 		this.state = PayState.NothingOwed;
	 	}
	 	
	 	public void setMoneyOwed(double money){
	 		this.moneyOwed = money;
	 	}
	 	public double getMoneyOwed(){
	 		return moneyOwed;
	 	}
	 	public String getState(){
	 		return state.toString();
	 	}
	 	
	 	public Person getPerson(){
	 		return inhabitant;
	 	}
	 	
	 	public void setState(PayState state){
	 		this.state = state;
	 	}
	 	
	 	public void deductMoney(double money){
	 		inhabitant.setFunds(inhabitant.getFunds()-money);
	 	}
	}
    public TenantList() {
    	
    }
    
    public static TenantList sharedInstance() {
    	if(sharedInstance == null) {
    		sharedInstance = new TenantList();
    	}
    	return sharedInstance;
    }

    public void addTenant(Person person,int apartmentNum){
    	if(apartmentNum == 1){
    		tenants1.add(new Tenant(person));
    	}
    	if(apartmentNum == 2){
    		tenants2.add(new Tenant(person));
    	}
    	if(apartmentNum == 3){
    		tenants3.add(new Tenant(person));
    	}
    }
    
    public List<Tenant> getTenants(int apartmentNum) {
    	if(apartmentNum == 1){
    		return tenants1;
    	}
    	else if(apartmentNum == 2){
    		return tenants2;
    	}
    	else if(apartmentNum == 3){
    		return tenants3;
    	}
		return null;
    }
    
    public Tenant getTenant(int index, int apartmentNum) {
    	if(apartmentNum == 1){
    		return tenants1.get(index);
    	}
    	else if(apartmentNum == 2){
    		return tenants2.get(index);
    	}
    	else if(apartmentNum == 3){
    		return tenants3.get(index);
    	}
		return null;
    }
 
	
}
