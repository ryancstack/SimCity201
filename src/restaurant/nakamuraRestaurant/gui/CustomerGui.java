package restaurant.nakamuraRestaurant.gui;

import gui.Gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import restaurant.nakamuraRestaurant.NakamuraCustomerRole;

public class CustomerGui implements Gui{

	private NakamuraCustomerRole role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, WaitingForSeat, Entering, BeingSeated, GoToSeat, Waiting, Eating, LeaveRestaurant, Paying};
	private Command command=Command.noCommand;
	private String choice;

	public static final int xHost = 700;
	public static final int yHost = 35;
	public static final int xWaiting = 650;
	public static final int yWaiting = 35;
	public static final int xStart = 737;
	public static final int yStart = 35;
    public static final int xCashier = 768;
    public static final int yCashier = 100;
	
    BufferedImage customerImage;

	public CustomerGui(NakamuraCustomerRole c){ //HostAgent m) {
		role = c;
		xPos = xStart;
		yPos = yStart;
		xDestination = yStart;
		yDestination = xStart;

        try {
        	customerImage = ImageIO.read(getClass().getResource("nakamuraRestaurantCustomer.png"));
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
			if(command == Command.Entering) {
				role.msgAnimationFinishedEnter();
				command = Command.noCommand;
			}
			else if (command==Command.WaitingForSeat){
				role.msgAnimationFinishedWaiting();
				command = Command.noCommand;
			}
			else if (command==Command.BeingSeated){
				role.msgAnimationFinishedSitting();
				command = Command.noCommand;
			}
			else if (command==Command.GoToSeat){
				role.msgAnimationFinishedGoToSeat();
				command = Command.noCommand;
			}
			else if (command==Command.Paying){
				role.msgAnimationFinishedGoingToCashier();
				command = Command.noCommand;
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				isPresent = false;
				command = Command.noCommand;
			}
		}
	}

	public void draw(Graphics2D g) {
    	g.drawImage(customerImage, xPos, yPos, null);
    	
		if(command == Command.Waiting)
        	g.drawString(choice + "?", xPos, yPos);
			
		else if(command == Command.Eating)
        	g.drawString(choice, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		role.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoEnterRestaurant() {
		isPresent = true;
		xDestination = xHost;
		yDestination = yHost;
		command = Command.Entering;
	}
	
	public void DoGoToWaiting() {
		xDestination = xWaiting;
		yDestination = yWaiting;
		command = Command.WaitingForSeat;		
	}
	
	public void DoWaitAt(int seat) {
		xDestination = xWaiting;
		yDestination = yWaiting + seat*35;
		command = Command.WaitingForSeat;			
	}
	
	public void DoGoToHost() {
		xDestination = xHost;
		yDestination = yHost;
		command = Command.BeingSeated;
	}

	public void DoGoToSeat(int x, int y) {
		xDestination = x;
		yDestination = y;
		command = Command.GoToSeat;
	}
	
	public void DoSetChoice(String c) {
		choice = c;
		command = Command.Waiting;
	}
	
	public void DoEat() {
		command = Command.Eating;
	}
	
	public void DoGoToCashier() {
		xDestination = xCashier;
		yDestination = yCashier;
		command = Command.Paying;
	}

	public void DoExitRestaurant() {
		xDestination = xStart;
		yDestination = yStart;
		command = Command.LeaveRestaurant;
	}
}
