package restaurant.tanRestaurant.gui;


import restaurant.tanRestaurant.TanCustomerRole;
import restaurant.tanRestaurant.TanHostAgent;
import restaurant.tanRestaurant.TanCookRole;
import restaurant.tanRestaurant.TanCustomerRole.OrderStatus;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CookGui implements Gui {

    private TanCookRole agent = null;

    private int xPos=291, yPos=410;//xPos = 291, yPos = 53;//default cook position
    private int xDestination = 291, yDestination = 410;//53;//default start position
    BufferedImage cookImage;

    public CookGui(TanCookRole agent) {
        this.agent = agent;
        try {
        	cookImage = ImageIO.read(getClass().getResource("stackRestaurantCook.png"));
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
    }

    public void draw(Graphics2D g) {
    	Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.drawImage(cookImage, xPos, yPos, null);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }
    
    public boolean isAtStart(){
    	if ((xPos == -20) &&(yPos==-20)){
    		return true;
    	}
    	else return false;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }


	public void DoGoToPost() {
		xDestination= 291;
		yDestination= 53;
		
	}
}
