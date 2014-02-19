package restaurant.nakamuraRestaurant.gui;

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

import gui.BuildingPanel;
import gui.Gui;
import gui.SimCityGui;

public class NakamuraRestaurantAnimationPanel extends BuildingPanel implements ActionListener {

	private final int WINDOWX = 827;
    private final int WINDOWY = 406;
    private static final int xTable1 = 126;
    private static final int yTable1 = 286;
    private static final int xTable2 = 286;
    private static final int yTable2 = 286;
    private static final int xTable3 = 455;
    private static final int yTable3 = 286;
    private static final int xTable4 = 608;
    private static final int yTable4 = 286;
    //private static final int TableSize = 50;
    
    private static final int xWaiting = 669;
    private static final int yWaiting = 131;
    private static final int WaitingSize = 25;
    
    private static final int xKitchen = 56;
    private static final int yKitchen = 57;

    private static final int xCooking = 55;
    private static final int yCooking = 55;

    private static final int xPlating = 86;
    private static final int yPlating = 137;
    
    private Image bufferImage;
    private Dimension bufferSize;
    
    BufferedImage restaurantImage;

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());

	
    public NakamuraRestaurantAnimationPanel(Rectangle2D r, int i, SimCityGui sc) {
    	super(r, i, sc);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
    	try {
        	restaurantImage = ImageIO.read(getClass().getResource("nakamuraRestaurant.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

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
        guis.add(gui);
    }
    
    public void removeGui(Gui gui) {
    	guis.remove(gui);
    }

	public void displayBuildingPanel() {
		myCity.displayBuildingPanel(this);	
		
	}
}
