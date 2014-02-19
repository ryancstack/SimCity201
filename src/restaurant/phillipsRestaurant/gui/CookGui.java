package restaurant.phillipsRestaurant.gui;


import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.interfaces.Cook;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CookGui implements Gui {

    public int xPos = 650, yPos = 200, xDestination = 650, yDestination = 200;
    boolean atDestination = false;
    
    private static final int PLATINGX = 560, PLATINGY = 200;
    private static final int COOKINGX = 770, COOKINGY = 225;
    private static final int FRIDGEX = 750, FRIDGEY = 60;
    
    Cook agent = null;
    BufferedImage cookImage;

    public CookGui(Cook agent) {
        this.agent = agent;
        
        try {
        	cookImage = ImageIO.read(getClass().getResource("richardRestaurantCook.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos = xPos+5;
        else if (xPos > xDestination)
            xPos = xPos-5;

        if (yPos < yDestination)
            yPos = yPos+5;
        else if (yPos > yDestination)
            yPos = yPos-5;
        
        if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == FRIDGEX) & (yDestination == FRIDGEY)) {
            atDestination = true;
            agent.msgAtFridge();
         }
        else if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == COOKINGX) & (yDestination == COOKINGY)) {
            atDestination = true;
            agent.msgAtCookingArea();
         }
        else if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == PLATINGX) & (yDestination == PLATINGY)) {
            atDestination = true;
            agent.msgAtCookingArea();
         }
    }

    public void draw(Graphics2D g) {
        g.drawImage(cookImage, xPos, yPos, null);
    }
    
    public void DoGoToFridge(){
    	atDestination = false;
    	xDestination = FRIDGEX;
		yDestination = FRIDGEY;
    }
    
    public void DoGoToCookingArea(){
    	atDestination = false;
    	xDestination = COOKINGX;
		yDestination = COOKINGY;
    }
    
    public void DoGoToPlatingArea(){
    	atDestination = false;
    	xDestination = PLATINGX;
		yDestination = PLATINGY;
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
