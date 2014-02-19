package restaurant.shehRestaurant.gui;

import restaurant.shehRestaurant.ShehCookRole;
import restaurant.shehRestaurant.helpers.Table;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CookGui implements Gui {

    private ShehCookRole agent = null;

    private int xPos = 705;
    private int yPos = 63;//default waiter position
    private int XHOME = 72, YHOME = 157;
    private int xDestination = -700;
    private int yDestination = -100;//default start position

    private final int XPLATING = 245;
    private final int YPLATING = 262;
    private final int XCOOKING = 164;
    private final int YCOOKING = 8;
    private final int XDOOR = 705;
    private final int YDOOR = 63;
    
    public int xTable;
    public static final int yTable = 250; 
    public ArrayList<Table> table; //Declaration of Table

    BufferedImage cookImage;

    public CookGui(ShehCookRole agent) {
        this.agent = agent;
        
        //IMAGE STUFF
        try {
        	cookImage = ImageIO.read(getClass().getResource("shehRestaurantCook.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        } 
    }

	public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if(xPos == XPLATING && yPos == YPLATING){
        	agent.msgPlating();
    	}
   
        if(xPos == XCOOKING && yPos == YCOOKING){
    		agent.msgCooking();
        }
       
    }

    public void draw(Graphics2D g) {
       g.drawImage(cookImage, xPos, yPos, null);
    }
    
    public void label(Graphics g, String label, int xLoc, int yLoc) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawString(label, xLoc, yLoc);
    }
   
    public void DoCooking() {
        xDestination = XCOOKING;
        yDestination = YCOOKING;
    }
    
    public void DoPlating() {
    	xDestination = XPLATING;
    	yDestination = YPLATING;
    }
    
    public void DoStandby() {
    	//xDestination = xDestination + (num * 10);
    	xDestination = XHOME;
    	yDestination = YHOME;
    }
    
    public void DoLeave() {
    	xDestination = XDOOR;
    	yDestination = YDOOR;
    }
    
    public boolean isPresent() {
        return true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}