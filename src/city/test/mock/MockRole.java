package city.test.mock;

import market.MarketWorkerRole;
import agent.Agent;
import city.PersonAgent;
import city.interfaces.*;


public class MockRole extends Mock implements RoleInterface {

	public MockRole(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void msgJobDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPerson(PersonAgent p) {
		// TODO Auto-generated method stub
		
	}

	
}
