package home;

import java.util.*;

import trace.AlertLog;
import trace.AlertTag;
import city.helpers.Directory;
import city.interfaces.Person;
import agent.Role;
import gui.Building;
import home.gui.LandlordGui;
import home.helpers.TenantList;
import home.helpers.TenantList.Tenant;
import home.interfaces.HomePerson;
import home.interfaces.Landlord;
import home.*;

public class LandlordRole extends Role implements Landlord {
    
	private List<Tenant> tenants = new ArrayList<Tenant>();//Collections.synchronizedList(new ArrayList<MyTenant>());
	public enum PayState {NeedsToPay,WaitingForPayment,OwesMoney,PayLater,NothingOwed};
	int apartmentNum;
	String myLocation;
	boolean timeToCollectRent = false;
	
	LandlordGui gui;
	
	public LandlordRole(String location,int apartment){
    	myLocation = location;
    	this.apartmentNum = apartment;
    	tenants = TenantList.sharedInstance().getTenants(apartment);
    	
    	List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
    	gui = new LandlordGui(this);
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(gui);
			}
		}
	}
	
	//Messages
	public void msgHereIsRent(Person person, double money){
		for(int i=0;i<TenantList.sharedInstance().getTenants(apartmentNum).size();i++){
			if(TenantList.sharedInstance().getTenant(i,apartmentNum).getPerson() == person){
				if(money == TenantList.sharedInstance().getTenant(i,apartmentNum).getMoneyOwed()){
					AlertLog.getInstance().logMessage(AlertTag.LANDLORD, getPersonAgent().getName(), "Got rent for $" + money);
					getPersonAgent().setFunds(getPersonAgent().getFunds() + money);
					TenantList.sharedInstance().getTenant(i,apartmentNum).setMoneyOwed(0);
					TenantList.sharedInstance().getTenant(i,apartmentNum).setState(PayState.NothingOwed);
				}
				else{
					getPersonAgent().setFunds(getPersonAgent().getFunds() + money);
					TenantList.sharedInstance().getTenant(i,apartmentNum).setMoneyOwed(
							TenantList.sharedInstance().getTenant(i,apartmentNum).getMoneyOwed() - money);
					TenantList.sharedInstance().getTenant(i,apartmentNum).setState(PayState.OwesMoney);
                }
			}
		}
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		if(getPersonAgent().getCurrentDay() == 7 && timeToCollectRent == false){
			timeToCollectRent = true;
		}
		if(timeToCollectRent == true){
			timeToCollectRent();
		}
		synchronized(TenantList.sharedInstance().getTenants(apartmentNum)){
			for(int i=0;i<TenantList.sharedInstance().getTenants(apartmentNum).size();i++){
				if(TenantList.sharedInstance().getTenant(i,apartmentNum).getState().equals("NeedsToPay")){
					payRent(TenantList.sharedInstance().getTenant(i,apartmentNum));
					return true;
				}
			}
		}
		return false;
	}
	//Actions
	public void timeToCollectRent(){
		timeToCollectRent = false;
		for(int i=0;i<TenantList.sharedInstance().getTenants(apartmentNum).size();i++) {
			AlertLog.getInstance().logMessage(AlertTag.LANDLORD, getPersonAgent().getName(), "Time to collect rent from my peasants");
			if(TenantList.sharedInstance().getTenant(i,apartmentNum).getState() == "NothingOwed"
				|| TenantList.sharedInstance().getTenant(i,apartmentNum).getState() == "OwesMoney"){
				TenantList.sharedInstance().getTenant(i,apartmentNum).setState(PayState.NeedsToPay);
				TenantList.sharedInstance().getTenant(i,apartmentNum).setMoneyOwed(50);
			}
		}
	}
	public void payRent(Tenant tenant) {
		AlertLog.getInstance().logMessage(AlertTag.LANDLORD, getPersonAgent().getName(), "Peasant paid $" + tenant.getMoneyOwed());
		tenant.getPerson().msgPayRent(this,tenant.getMoneyOwed());
		tenant.setState(PayState.WaitingForPayment);
		stateChanged();
	}

}