package restaurant.phillipsRestaurant.gui;

import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.interfaces.Customer;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CustomerGui implements Gui{

	private Customer agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	

	//private HostAgent host;
	//RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 240, xTable2 = 380, xTable3 = 310;
    public static final int yTable12 = 115, yTable3 = 230;
	
	private static final int CASHIERX = 165, CASHIERY = 35;
	
	BufferedImage customerImage;
	
	//public ArrayList<Boolean> tableOccupied = new ArrayList<Boolean>();

	public CustomerGui(Customer c, int customerNum){ //HostAgent m) {
		agent = c;
		switch(customerNum%6){
        case 0:
        	xPos = 0;
        	yPos = 180;
        	xDestination = 0;
        	yDestination = 180;
        	break;
        case 1:
        	xPos = 25;
        	yPos = 180;
        	xDestination = 25;
        	yDestination = 180;
        	break;
        case 2:
        	xPos = 50;
        	yPos = 180;
        	xDestination = 50;
        	yDestination = 180;
        	break;
        case 3:
        	xPos = 0;
        	yPos = 210;
        	xDestination = 0;
        	yDestination = 210;
        	break;
        case 4:
        	xPos = 25;
        	yPos = 210;
        	xDestination = 25;
        	yDestination = 210;
        	break;
        case 5:
        	xPos = 50;
        	yPos = 210;
        	xDestination = 50;
        	yDestination = 210;
        	break;
        
		}
        try {
        	customerImage = ImageIO.read(getClass().getResource("richardRestaurantCustomer.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }  
		//this.gui = gui;
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

		if(xPos == xDestination && yPos == yDestination
        		& (xDestination == CASHIERX) & (yDestination == CASHIERY)) {
            agent.msgAtCashier();
        }
		
		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat){
				agent.msgAnimationFinishedGoToSeat();
			}
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(customerImage,xPos,yPos,null);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void chooseTable(){
	//	if (gui.(0) == true)
		//xDestination = xTable1;
		//yDestination = 
		
	}
	
	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if (seatnumber==1)
        {
    		xDestination = xTable;
    		yDestination = yTable12;
        }
    	if (seatnumber==2)
        {
    		xDestination = xTable2;
    		yDestination = yTable12;
        }
    	if (seatnumber==3)
        {
    		xDestination = xTable3;
    		yDestination = yTable3;
        }
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier() {
        xDestination = CASHIERX;
        yDestination = CASHIERY;
    }

	public void DoExitRestaurant() {
		xDestination = -20;
		yDestination = 300;
		command = Command.LeaveRestaurant;
	}
}
