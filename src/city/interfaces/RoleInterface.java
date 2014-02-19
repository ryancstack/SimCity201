package city.interfaces;

import city.PersonAgent;

public interface RoleInterface {
	
	public boolean pickAndExecuteAnAction();

	public void msgJobDone();

	public void setPerson(PersonAgent personAgent);
}
