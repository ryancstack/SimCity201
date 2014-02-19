package restaurant.shehRestaurant.gui;

import restaurant.shehRestaurant.ShehCustomerRole;
import restaurant.shehRestaurant.ShehWaiterRole; 
import restaurant.shehRestaurant.helpers.Table;
import restaurant.shehRestaurant.interfaces.Waiter;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class WaiterGui implements Gui {

    private ShehWaiterRole agent = null;

    private int xPos = 450, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private int agentSize = 20; //default agent size
    
    private final int XREGISTER = 642;
    private final int YREGISTER = 180;
    private final int XPLATING = 249;
    private final int YPLATING = 176;
    private final int XDOOR = 704;
    private final int YDOOR = 62;
    private final int XIDLE = 317;
    private final int YIDLE = 150;
    private final int XTABLE1 = 432;
    private final int YTABLE1 = 130;
    private final int XTABLE2 = 339;
    private final int YTABLE2 = 284;
    private final int XTABLE3 = 520;
    private final int YTABLE3 = 275;
    
    BufferedImage waiterImage;
     
    public int homePosition;
    public ArrayList<Table> table; //Declaration of Table
    private Boolean wantsBreak = false;

    public WaiterGui(ShehWaiterRole agent) {
        this.agent = agent;
        table = agent.getTables();
        
        try {
        	waiterImage = ImageIO.read(getClass().getResource("shehRestaurantWaiter.png"));
        	/*
        	chickenImage = ImageIO.read(getClass().getResource("chicken.png"));
            pizzaImage = ImageIO.read(getClass().getResource("pizza.png"));
            saladImage = ImageIO.read(getClass().getResource("salad.png"));
            steakImage = ImageIO.read(getClass().getResource("steak.png"));
       		*/
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
        
        //IMAGE STUFF
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == XTABLE1 + agentSize) & (yDestination == YTABLE1 - agentSize)) { 
           agent.msgAtTable();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == XTABLE2 + agentSize) & (yDestination == YTABLE2 - agentSize)) { 
           agent.msgAtTable();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == XTABLE3 + agentSize) & (yDestination == YTABLE3 - agentSize)) { 
           agent.msgAtTable();
        }
        
        if(xPos == XREGISTER){
        	if(yPos == YREGISTER) {
        		agent.msgAtKiosk();
        	}
        }
        if(xPos == XPLATING){
        	if(yPos == YPLATING) {
        		agent.msgAtKitchen();
        	}
        }
    }

    public void draw(Graphics2D g) {
		g.drawImage(waiterImage, xPos, yPos, null);
    }
    
    public void label(Graphics g, String label, int xLoc, int yLoc) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawString(label, xLoc, yLoc);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(ShehCustomerRole customer, Table table) { //SEATING AT TABLE
    		if(table.getTableNumber() == 1){
    			xDestination = XTABLE1 + agentSize;
    			yDestination = YTABLE1 - agentSize;
    		}
    		if(table.getTableNumber() == 2){
    			xDestination = XTABLE2 + agentSize;
    			yDestination = YTABLE2 - agentSize;
    		}
    		if(table.getTableNumber() == 3){
    			xDestination = XTABLE3 + agentSize;
    			yDestination = YTABLE3 - agentSize;
    		}
    }
    
    public void GoToTable(Table table) { //SEATING AT TABLE
		if(table.getTableNumber() == 1){
			xDestination = XTABLE1 + agentSize;
			yDestination = YTABLE1 - agentSize;
		}
		if(table.getTableNumber() == 2){
			xDestination = XTABLE2 + agentSize;
			yDestination = YTABLE2 - agentSize;
		}
		if(table.getTableNumber() == 3){
			xDestination = XTABLE3 + agentSize;
			yDestination = YTABLE3 - agentSize;
		}
    }
    
    public void DoGoToKitchen() {
    	xDestination = XPLATING;
    	yDestination = YPLATING;
    }
    
    public void DeliverFoodToTable(Table table, String order) {
    	//Graphics2D g2 = (Graphics2D)g;
    	//g2.setColor(Color.BLACK);
    	
		if(table.getTableNumber() == 1){
			xDestination = XTABLE1 + agentSize;
			yDestination = YTABLE1 - agentSize;
		}
		if(table.getTableNumber() == 2){
			xDestination = XTABLE2 + agentSize;
			yDestination = YTABLE2 - agentSize;
		}
		if(table.getTableNumber() == 3){
			xDestination = XTABLE3 + agentSize;
			yDestination = YTABLE3 - agentSize;
		}
    }
    
    public void DoGoToKiosk() {
        xDestination = XREGISTER;
        yDestination = YREGISTER;
    }
    
    public void DoGoOnBreak() {
    	xDestination = XDOOR;
    	yDestination = YDOOR;
    }
    
    public void DoStandby(int num) {
    	xDestination = XIDLE - (homePosition * 40);
    	yDestination = YIDLE;
    }
    
    public void setHome(int num) {
    	homePosition = num;
    }
    
	public void setOnBreak() {
		wantsBreak = true;
		agent.msgImTired();
		//setPresent(true);;
	}
	
	public void returnFromBreak() {
		agent.msgBackFromBreak();
		agent.msgAtKiosk();
	}
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public boolean isBreak() {
		return wantsBreak;
	}

}