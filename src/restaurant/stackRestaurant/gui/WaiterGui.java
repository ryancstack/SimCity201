package restaurant.stackRestaurant.gui;

import restaurant.stackRestaurant.StackWaiterRole;
import restaurant.stackRestaurant.helpers.TableList;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WaiterGui implements Gui {

    private StackWaiterRole agent = null;
    private TableList tableList = new TableList();
    private String choice = "";
    BufferedImage waiterImage;
    BufferedImage chickenImage;
    BufferedImage pizzaImage;
    BufferedImage saladImage;
    BufferedImage steakImage;
    
    //827 x 406 y
    
    private static final int xHome = 413, yHome = 20;
    private int xPos = 850, yPos = 450;//default waiter position
    private int xDestination = 850, yDestination = 450;//default start position
    private int xTable = -20, yTable = -20;
    private static final int xCook = 485, yCook = 70;
    private static final int xBreak = 413, yBreak = 22;
    private static final int WAITINGX = 725, WAITINGY = 333;
    private static final int xCashier = 460, yCashier = 34;
	private static final int xExit = 850, yExit = 450;
  
    
    private static final int PERSONSIZEX = 32, PERSONSIZEY = 40;

    public WaiterGui(StackWaiterRole agent) {
        this.agent = agent;
        
        try {
        	waiterImage = ImageIO.read(getClass().getResource("stackRestaurantWaiter.png"));
        	chickenImage = ImageIO.read(getClass().getResource("chicken.png"));
            pizzaImage = ImageIO.read(getClass().getResource("pizza.png"));
            saladImage = ImageIO.read(getClass().getResource("salad.png"));
            steakImage = ImageIO.read(getClass().getResource("steak.png"));
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

        if (xPos == xDestination && yPos == yDestination && xDestination == WAITINGX + PERSONSIZEX && yDestination == WAITINGY - PERSONSIZEY) {
        	agent.msgAtCustomer();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + PERSONSIZEX) & (yDestination == yTable - PERSONSIZEY)) {
        	agent.msgAtTable();
        	DoGoHome();
        }
        if (xPos == xCook && yPos == yCook 
        		&& (xDestination == xCook && yDestination == yCook)) {
        	agent.msgAtCook();
        	DoGoHome();	
        }
        if (xPos == xCashier && yPos == yCashier
        		&& (xDestination == xCashier && yDestination == yCashier)) {
        	agent.msgAtCashier();
        	DoGoHome();	
        }
//        if(xPos == xExit && yPos == yExit
//        		&& (xDestination == xExit && yDestination == yExit)) {
////        	agent.msgAnimationFinishedLeavingRestaurant();
//        }
    }

    public void updateGui(String choice) {
    	this.choice = choice;
    }
    
    public void setWantsToGoOnBreak() {
    	agent.msgIWantToGoOnBreak();
    }
    
    public void setWaiterCheckOff() {
//    	gui.setWaiterBreakOff(agent);
    }

    public void setGoingOffBreak() {
    	agent.msgImComingOffBreak();
    	DoGoHome();
    	
    }
    
    public void draw(Graphics2D g) {
    	
    	if(choice.equals("")) {
			g.drawImage(waiterImage, xPos, yPos, null);
		}
		else if(choice.equals("St")) {
			g.drawImage(waiterImage, xPos, yPos, null);
			g.drawImage(steakImage, xPos + 26, yPos + 20, null);
		}
		else if(choice.equals("Ch")) {
			g.drawImage(waiterImage, xPos, yPos, null);
			g.drawImage(chickenImage, xPos + 26, yPos + 20, null);
		}
		else if(choice.equals("Pi")) {
			g.drawImage(waiterImage, xPos, yPos, null);
			g.drawImage(pizzaImage, xPos + 26, yPos + 20, null);
		}
		else if(choice.equals("Sa")) {
			g.drawImage(waiterImage, xPos, yPos, null);
			g.drawImage(saladImage, xPos + 26, yPos + 20, null);
		}
		else {
			g.drawImage(waiterImage, xPos, yPos, null);
		}
    	String info = agent.getName() + "(" + agent.getStringState() + ")";
    	g.setColor(Color.white);
		g.drawString(info, xPos - 40, yPos - 5);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(int table) {
    	
    	xTable = (int)tableList.getTables().get(table-1).getX();
		yTable = (int)tableList.getTables().get(table-1).getY();
        xDestination = xTable + PERSONSIZEX;
        yDestination = yTable - PERSONSIZEY;
    }
    
    public void DoGoToCustomer() {
    	xDestination = WAITINGX + PERSONSIZEX;
    	yDestination = WAITINGY - PERSONSIZEY;
    }
  

    public void DoGoHome() {
        xDestination = xHome;
        yDestination = yHome;
        
    }
    
    public void DoGoOnBreak() {
    	xDestination = xBreak;
    	yDestination = yBreak;
    }
    
    public void DoGoToCook() {
    	xDestination = xCook;
    	yDestination = yCook;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoExitRestaurant() {
		xDestination = xExit;
    	yDestination = yExit;
		
	}

	public void DoGoToPaycheck() {
		xDestination = xCashier;
    	yDestination = yCashier;
		
	}
}
