package restaurant.phillipsRestaurant.gui;


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

public class PhillipsRestaurantAnimationPanel extends BuildingPanel implements ActionListener {

    private final int WINDOWX = 827;
    private final int WINDOWY = 406;
    BufferedImage restaurantImage;
    private final int DELAY = 10;
    Timer timer;
    
    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
  

    public PhillipsRestaurantAnimationPanel(Rectangle2D r, int i, SimCityGui sc) {
    	super(r, i, sc);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        setBackground(Color.lightGray);
        
        timer = new Timer(DELAY, this );
    	timer.start();
    	
    	try {
        	restaurantImage = ImageIO.read(getClass().getResource("RestaurantImage.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    	
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        //g2.setColor(getBackground());
        //g2.fillRect(0, 0, WINDOWX, WINDOWY );
        g2.drawImage(restaurantImage, 0, 0, null);

        /*
        //tables
        g2.setColor(Color.ORANGE);
        g2.fillRect(RECTXPOS, RECTYPOS, RECTX, RECTY);
        g2.setColor(Color.ORANGE);
        g2.fillRect(RECTXPOS+60, RECTYPOS, RECTX, RECTY);
        g2.setColor(Color.ORANGE);
        g2.fillRect(RECTXPOS+120, RECTYPOS, RECTX, RECTY);
        //Cook Area
        g.setColor(Color.BLACK);
        g.fillRect(240, 50, 50, 30);
        g2.setColor(Color.PINK);
        g2.fillRect(290, 50, 50, 30);
        //Cashier
        g2.setColor(Color.RED);
        g2.fillRect(RECTXPOS-70, RECTYPOS-200, RECTX-10, RECTY-20);
        //Kitchen
        g2.setColor(Color.CYAN);
        g2.fillRect(RECTXPOS+140, RECTYPOS-245, RECTX-20, RECTY+25);
        //Customer waiting area
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 180, RECTX+20, RECTY+20);
        */
        
        synchronized(this.guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        }
        synchronized(this.guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
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
       synchronized(this.guis){
    	   guis.add(gui);
       }
    }
    
    public void removeGui(Gui gui) {
    	 synchronized(this.guis){
      	   guis.remove(gui);
         }
    }

	public void displayBuildingPanel() {
		myCity.displayBuildingPanel(this);	
		
	}
}
