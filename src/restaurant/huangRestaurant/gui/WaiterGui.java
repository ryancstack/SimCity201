package restaurant.huangRestaurant.gui;

import gui.Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import restaurant.huangRestaurant.HuangWaiterRole;
import restaurant.huangRestaurant.interfaces.Customer;



public class WaiterGui implements Gui {
	private HuangWaiterRole agent = null;
	private boolean seatingNew = false;
	private boolean carryingFood = false;
	private boolean wantsBreak = false;
	private boolean onBreak = false;
	private String currentDish;
	private int xPos = 0, yPos = 450;//default cook position
	private int xDestination = 0, yDestination = 450;//default start position
	private int xExit = 0, yExit = 450;//Exit
    private int xHome = 120, yHome = 30;//waiter Home positions
    private int xCWaitArea = 120, yCWaitArea = 30;//Customer Waiting area.
	private static final int cookX = 640; 
	private static final int cookY = 270;
	private static final int cashierX = 780; 
	private static final int cashierY = 40;
	private static final int hostX = 27, hostY = 48;
    public int xTable = 160;
    public int yTable = 170;
    private static final int tableSpawnX = 160;
	private static final int tableSpawnY = 170;
	private static final int tableOffSetX = 180;
    
    private class ServedFood {;
    	String food;
    	int xPos;
    	int yPos;
    	
    	
    	ServedFood(String food, int x, int y) {
    		this.food = food;
    		this.xPos = x;
    		this.yPos = y;
    	}
    }
    private List<ServedFood>foodOnTable = Collections.synchronizedList(new ArrayList<ServedFood>());


	//private HostAgent host;
	BufferedImage waiterImage;

    public WaiterGui(HuangWaiterRole huangWaiterRole) {
		this.agent = huangWaiterRole;
        try {
        	waiterImage = ImageIO.read(getClass().getResource("huangRestaurantWaiter.png"));
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
        	if(carryingFood == true) {
        		synchronized(foodOnTable) {
        			foodOnTable.add(new ServedFood(currentDish, xTable + 20, yTable + 20));
        		}
        		carryingFood = false;
        		currentDish ="";
        	}
           agent.msgAtTable();
        }
        if (xPos == cookX && yPos == cookY) {
        	carryingFood = false;
        	currentDish ="";
        	agent.msgAtCook();
        }
        if (xPos == cashierX && yPos == cashierY) {
        	agent.msgAtCashier();
        }
        if (xPos == hostX && yPos == hostY) {
        	agent.msgAtHost();
        }
    	if (xPos == xCWaitArea && yPos == yCWaitArea && seatingNew == true) {
    		agent.msgCanSeatNew();
    		seatingNew = false;
    	}
    }

    public void draw(Graphics2D g) {
    	if (carryingFood == true) {
    		g.drawImage(waiterImage, xPos, yPos, null);
	        g.drawString(currentDish, xPos, yPos);
	        synchronized(foodOnTable) {
		        if(!foodOnTable.isEmpty()) {
	    			for(ServedFood served : foodOnTable) {
	    				g.drawString(served.food, served.xPos, served.yPos);
	    			}
	    		}
	        }
    	}
    	else {
    		synchronized(foodOnTable) {
	    		if(!foodOnTable.isEmpty()) {
	    			for(ServedFood served : foodOnTable) {
	    				g.drawString(served.food, served.xPos, served.yPos);
	    			}
	    		}
    		}
    		g.drawImage(waiterImage, xPos, yPos, null);
    	}
    }
    public boolean isPresent() {
        return true;
    }
    public void DoBringToTable(Customer customer, int table) {
    	this.xTable = tableSpawnX + (tableOffSetX * table);
    	this.yTable = tableSpawnY;
    	customer.getGui().DoGoToSeat(xTable, yTable);
        xDestination = xTable + 20;
        yDestination = yTable - 20;
       
    }
    public void DoGoToCustomer(int table) {
    	this.xTable = tableSpawnX + (tableOffSetX * table);
    	this.yTable = tableSpawnY;
        xDestination = xTable + 20;
        yDestination = yTable - 20;
    	
    }
    public void DoGoToCook(String choice) {
    	currentDish = (String) choice.subSequence(0,2);
    	currentDish = currentDish + '?';
    	carryingFood = true;
    	xDestination = cookX;
    	yDestination = cookY;
    }
    public void DoGoToCook() {
    	carryingFood = true;
    	xDestination = cookX;
    	yDestination = cookY;
    }
    public void DoGoToCashier() {
    	xDestination = cashierX;
    	yDestination = cashierY;
    }
    public void drawOrder(String s) {
    	carryingFood = true;
    	currentDish = (String) s.subSequence(0, 2);
    }
    public void DoCleanTable(int table) {
		this.xTable = tableSpawnX + (tableOffSetX * table);
    	this.yTable = tableSpawnY;
    	synchronized(foodOnTable) {
	    	if(!foodOnTable.isEmpty()) {
	    		for(ServedFood served : foodOnTable) {
	    			if(served.xPos == xTable + 20 && served.yPos == yTable + 20) {
	    				foodOnTable.remove(served);
	    				break;
	    			}
	    		}
	    	}
    	}
    }
    public void setHome(int iterate) {
    	xHome = xHome + 30 * iterate;
    	xDestination = xHome;
//    	xPos = xHome;
//    	xDestination = xPos;
//    	yPos = yHome;
//    	yDestination = yPos;
    }
    public void DoLeaveCustomer() {
        xDestination = xHome;
        yDestination = yHome;
    }
    public void DoGoSeatNew() {
    	xDestination = xCWaitArea;
    	yDestination = yCWaitArea;
    	seatingNew = true;
    }
    public boolean isBreak() {
    	if (wantsBreak == true) {
    		return wantsBreak;
    	}
    	return onBreak;
    }
    public void setBreak() {
    	wantsBreak = true;
		agent.msgWantsBreak();
    }
    public void enableBreak() {
    	wantsBreak = false;
    	onBreak = true;
    }
    public void endBreakSequence() {
    	wantsBreak = false;
    	onBreak = false;
    }
    public int getXPos() {
        return xPos;
    }
    public int getYPos() {
        return yPos;
    }
    public void DoGoToHost() {
    	xDestination = hostX;
    	yDestination = hostY;
    }
	public void DoLeaveRestaurant() {
    	xDestination = xExit;
    	yDestination = yExit;
		
	}
	public void DoGoHome() {
		xDestination = xHome;
		yDestination = yHome;
		
	}
}
