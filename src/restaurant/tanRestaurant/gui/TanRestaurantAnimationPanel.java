package restaurant.tanRestaurant.gui;

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
public class TanRestaurantAnimationPanel extends BuildingPanel implements ActionListener {

    private final int WINDOWX = 827;
    private final int WINDOWY = 406;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int time = 20;
    private final int framex=0;
    private final int framey=0;
    private final int tablex=200;
    private final int tabley=250;
    private final int tablewidth= 50;
    private final int tablelength= 50;
    
    BufferedImage restaurantImage;
    BufferedImage cashierImage;

    private final int delay = 10;

    private List<Gui> guis = new ArrayList<Gui>();

    /*
    public TanRestaurantAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(time, this );
    	timer.start();
    }*/
    
    public TanRestaurantAnimationPanel(Rectangle2D r, int i, SimCityGui sc) {
    	super(r, i, sc);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(delay, this );
    	timer.start();
    	
    	//IMAGE
    	try {
        	restaurantImage = ImageIO.read(getClass().getResource("TanRestaurant.png"));
        	cashierImage = ImageIO.read(getClass().getResource("TanCashierAgent.png"));

        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

	/*
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D g3 = (Graphics2D)g;
        Graphics2D g4 = (Graphics2D)g;
        Graphics2D g5 = (Graphics2D)g;


        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(framex, framey, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(tablex, tabley, tablewidth, tablelength);//200 and 250 need to be table params
        g2.fillRect(tablex+150, tabley-150, tablewidth, tablelength);
        
        
        g2.setColor(Color.RED);
        g2.fillRect(tablex+150, tabley, tablewidth, tablelength);
        //g2.fillRect(tablex, tabley+150, tablewidth, tablelength);

        g3.setColor(Color.BLACK);
        g3.fillRect(170, 50, 100, 20); //cashier counter
        g3.fillRect(170, 10, 20, 50);
        
        g3.fillRect(300, 50, 100, 20); //cook counter
        g3.fillRect(400, 10, 40, 60); //grill
        
        g4.setColor(Color.GRAY);
        g4.fillRect(410, 20, 20, 30);
        
        g5.setColor(Color.WHITE);
        g5.drawString("Plating Area",310, 65);

        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
                gui.draw(g3);
            }
        }
    }
*/
    public void displayBuildingPanel() {
        myCity.displayBuildingPanel(this);
    }
 
    
    public void addGui(CustomerGui gui) {
        synchronized(guis){
        	guis.add(gui);
        }
    }

    public void addGui(HostGui gui) {
    	synchronized(guis){
        	guis.add(gui);
        }
    }
    
    public void addGui(CashierGui gui) {
    	synchronized(guis){
        	guis.add(gui);
        }
    }
    
    public void addGui(CookGui gui) {
    	synchronized(guis){
        	guis.add(gui);
        }
    }
    
    public void addGui(WaiterGui gui){
    	synchronized(guis){
        	guis.add(gui);
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //Graphics2D g3 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.drawImage(restaurantImage, 0, 0, null);
        g2.drawImage(cashierImage, 460, 40, null);

        
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
		synchronized(guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent())
	                gui.updatePosition();
	        }
		}
	}

    public void addGui(Gui gui) {
        synchronized(guis){
    	guis.add(gui);
        }
    }

}
