package home;

import agent.Role;
import home.gui.HomePersonGui;
import home.interfaces.*;

public class HomePersonRole extends Role implements HomePerson {
	
	//data--------------------------------------------------------------------------------
	Landlord landlord;
	boolean needToPayRent;
	boolean cleanHouse = true;
	double debt;
	int dirtinessLevel;
	
	//messages----------------------------------------------------------------------------
	public void msgPayRent(double moneyOwed) {
		debt = moneyOwed;
		needToPayRent = true;
	}
	
	public void msgPayLater() {
		
	}
	
	public void msgCleanHouse() {
		cleanHouse = false;
	}
		
	//scheduler---------------------------------------------------------------------------
	public boolean pickAndExecuteAnAction() {
		if(needToPayRent == true) {
			PayRent();
		}
		
		if(cleanHouse = false) {
			Clean();
		}
		
		return false;
	}
	
	//actions-----------------------------------------------------------------------------
	private void PayRent() {
		if(getPersonAgent().getFunds() >= debt) {
			//landlord.msgHereIsRent(this, debt);
			getPersonAgent().setFunds(getPersonAgent().getFunds() - debt);
		}
		else 
//			TODO implement pay later system
//			landlord.msgCantPayRent();
		
		needToPayRent = false;
	}
	
	private void Clean() {
		DoClean();
		cleanHouse = true;
	}
	
	//GUI Actions-------------------------------------------------------------------------
	private void DoClean() {
		//gui
	}
}
