package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.*;

public abstract class BuildingPanel extends JPanel implements ActionListener{
	Rectangle2D myRectangle;
	String myName;
	protected SimCityGui myCity;
	private final int WINDOWX = 827;
    private final int WINDOWY = 406;
	
	public BuildingPanel( Rectangle2D r, int i, SimCityGui sc) {
		myRectangle = r;
		myName = "" + i;
		myCity = sc;
		
		JLabel j = new JLabel( myName );
		j.setForeground(Color.white);
		add( j );
		

    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        setBackground(Color.lightGray);
	}
	
	public abstract void paintComponent(Graphics g);
	
	public abstract void addGui(Gui gui);
	
	public abstract void updateGui();
	
	public String getName() {
		return myName;
	}

	public abstract void displayBuildingPanel();

}