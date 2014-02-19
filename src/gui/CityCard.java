package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

public class CityCard extends MacroAnimationPanel {

	public static final int CARD_WIDTH = 827, CARD_HEIGHT = 406;

	public CityCard(SimCityGui city) {
		super(city);
		this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		this.setVisible(true);
		addMouseListener(this);
		background = Color.cyan;
	}
	
	public CityCard(SimCityGui city, Color c) {
		super(city);
		this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		this.setVisible(true);
		addMouseListener(this);
		background = c;
	}


	public void mouseClicked(MouseEvent e) {
		
	}

	
	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
		
	}

	
	public void mousePressed(MouseEvent e) {
		
	}

	
	public void mouseReleased(MouseEvent e) {
		
	}
}