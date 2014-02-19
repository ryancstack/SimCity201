package restaurant.huangRestaurant.gui;

import gui.BuildingPanel;
import gui.Gui;
import gui.SimCityGui;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class HuangRestaurantAnimationPanel extends BuildingPanel implements ActionListener {

	private final int WINDOWX = 827;
    private final int WINDOWY = 406;
    BufferedImage restaurantImage;
    private final int DELAY = 10;
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
//    private static StackRestaurantAnimationPanel sharedInstance = null;
    
		
    public HuangRestaurantAnimationPanel(Rectangle2D r, int i, SimCityGui sc) {
    	super(r, i, sc);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        setBackground(Color.lightGray);
 
    	Timer timer = new Timer(DELAY, this );
    	timer.start();
    	
    	try {
        	restaurantImage = ImageIO.read(getClass().getResource("huangRestaurant.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    	
    }
    
//    public static StackRestaurantAnimationPanel sharedInstance() {
//    	if(sharedInstance == null) {
//    		sharedInstance = new StackRestaurantAnimationPanel(r, i, sc);
//    	}
//    	return sharedInstance;
//    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.drawImage(restaurantImage, 0, 0, null);

        synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	           gui.updatePosition();
	            }
	        }
        }
        synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	           gui.draw(g2);
	        }
	    }
    }
    }
	
	public void updateGui() {
		synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent())
	                gui.updatePosition();
	        }
		}
	}

    public void addGui(Gui gui) {
    	synchronized(guis) {
    		guis.add(gui);
    	}
    }
    
    public void removeGui(Gui gui) {
    	synchronized(guis) {
    		guis.remove(gui);
    	}
    }

	public void displayBuildingPanel() {
		myCity.displayBuildingPanel(this);	
		
	}
}
