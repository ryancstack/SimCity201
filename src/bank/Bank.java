package bank;

import javax.swing.JPanel;

import gui.CurrentBuildingPanel;


public class Bank {
	String name = "Bank";
	BankManagerAgent manager;
	boolean isOpen = true;
	CurrentBuildingPanel restPanel;

	public Bank() {
    	manager = new BankManagerAgent();
    	manager.startThread();
    }
    
    public Bank(String buildingName) {
    	name = buildingName;
    	manager = new BankManagerAgent();
    	manager.setBank(this);
    	manager.startThread();
    }
    
    public BankManagerAgent getManager() {
    	return manager;
    }
	
	public String getName() {
		return name;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	public JPanel getInfoPanel() {
		return restPanel;
	}

	public void setInfoPanel(CurrentBuildingPanel restPanel) {
		this.restPanel = restPanel;
	}
	
}
