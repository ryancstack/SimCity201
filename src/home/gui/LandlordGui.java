package home.gui;

import gui.Gui;
import home.interfaces.Landlord;

import java.awt.Color;
import java.awt.Graphics2D;

public class LandlordGui implements Gui {

	Landlord agent;
	
	public LandlordGui(Landlord landlord){
		this.agent = landlord;
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g) {

	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

}
