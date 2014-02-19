package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class GUIHome extends BuildingPanel {
	
	List<Gui> guis = new ArrayList<Gui>();
    BufferedImage homeImage;
    private final int DELAY = 20;

	public GUIHome( Rectangle2D r, int i, SimCityGui sc) {
		super(r, i, sc);
    	
    	try {
        	homeImage = ImageIO.read(getClass().getResource("Home.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    	
    	Timer timer = new Timer(DELAY, this );
    	timer.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	
	public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(homeImage, 0, 0, null);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
	}
	
	public void updateGui() {
        for(Gui gui : guis) {
            if (gui.isPresent())
                gui.updatePosition();
        }
	}
	
	public void addGui(Gui gui) {
		guis.add(gui);
	}
	
	public void displayBuildingPanel() {
		myCity.displayBuildingPanel( this );
	}

}