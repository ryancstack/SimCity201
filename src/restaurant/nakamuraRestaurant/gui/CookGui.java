package restaurant.nakamuraRestaurant.gui;

import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import restaurant.nakamuraRestaurant.NakamuraCookRole;

public class CookGui implements Gui {

    private NakamuraCookRole agent = null;

    private static final int xStart = 737, yStart = 35;//default Cook position
    private ArrayList<String> Cooking = new ArrayList<String>();
    private ArrayList<String> Plating = new ArrayList<String>();
    enum Command {noCommand, moving, leaving};
    Command command;
    
    boolean isPresent;

    public int xPos = xStart;
    public int yPos = yStart;
    public int xDestination = xStart;
    public int yDestination = yStart;
    public static final int xCooking = 55;
    public static final int yCooking = 55;
    public static final int xPlating = 86;
    public static final int yPlating = 136;
    public static final int xCashier = 768;
    public static final int yCashier = 100;
    
    BufferedImage cookImage;

    public CookGui(NakamuraCookRole cook) {
        this.agent = cook;

        isPresent = false;
        try {
        	cookImage = ImageIO.read(getClass().getResource("nakamuraRestaurantCook.png"));
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

        if (xPos == xDestination && yPos == yDestination) {
        	if(command == Command.leaving) {
        		agent.msgActionComplete();
        		command = Command.noCommand;
        		isPresent = false;
        	}
        	else if(command == Command.moving) {
	        	agent.msgActionComplete();
	        	command = Command.noCommand;
        	}
        }
 
    }

    public void draw(Graphics2D g) {
    	g.drawImage(cookImage, xPos, yPos, null);
    	
        for(int i = 0; i < Cooking.size(); i++)
        	g.drawString(Cooking.get(i), xCooking - 25, yCooking + i*10);
        for(int i = 0; i < Plating.size(); i++)
        	g.drawString(Plating.get(i), xPlating - 25, yPlating + i*10);
    }

    public boolean isPresent() {
        return isPresent;
    }
    
    public void checkInventory() {
    	agent.msgCheckInventory();
    }

    public void DoGoToCooking() {
    	xDestination = xCooking;
    	yDestination = yCooking;
    	command = Command.moving;
    }

    public void DoGoToPlating() {
        xDestination = xPlating;
        yDestination = yPlating;
        command = Command.moving;
    }
    
    public void DoGoToCashier() {
        xDestination = xCashier;
        yDestination = yCashier;
        command = Command.moving;    	
    }
    
    public void DoLeaveRestaurant() {
    	xDestination = xStart;
    	yDestination = yStart;
    	command = Command.leaving;
    }
    
    public void AddCooking(String food) {
    	Cooking.add(food);
    }
    
    public void AddPlating(String food) {
    	Cooking.remove(food);
    	Plating.add(food);
    }
    
    public void RemovePlating(String food) {
    	Plating.remove(food);
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void setPresent() {
    	isPresent = true;
    }
}
