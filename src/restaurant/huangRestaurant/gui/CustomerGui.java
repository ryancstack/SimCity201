package restaurant.huangRestaurant.gui;

import restaurant.huangRestaurant.HuangCustomerRole;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CustomerGui implements Gui{

	private HuangCustomerRole agent = null;
	private boolean isPresent = true;
	private boolean isHungry = false;


	private int xPos = 35, yPos = 450;
	private int xDestination = 35, yDestination = 450;
    private static final int hostX = 27, hostY = 48;
	private int xWait = 35;
	private int yWait = 150;
	public int currentSpot = 0;
	private static final int cashierX = 780; 
	private static final int cashierY = 40;
	private int xExit = 0, yExit = 450;//Exit
	private enum Command {noCommand, GoToSeat, GoToPay, LeaveRestaurant, GoToHost};
	private Command command=Command.noCommand;
	BufferedImage customerImage;

	public CustomerGui(HuangCustomerRole c){ //HostAgent m) {
		 this.agent = c;

	        try {
	        	customerImage = ImageIO.read(getClass().getResource("huangRestaurantCustomer.png"));
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
			if (command == Command.GoToSeat){
				agent.msgAnimationFinishedGoToSeat();
			}
			else if (command == Command.GoToPay) {
				agent.msgAnimationFinishedPay();
			}
			else if (command == Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
			}
			else if (command == Command.GoToHost) {
				agent.msgAnimationAtHost();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
			}
			command = Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.drawImage(customerImage, xPos, yPos, null);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int xTable, int yTable) {//later you will map seatnumber to table coordinates.
		xDestination = xTable;
		yDestination = yTable;
		command = Command.GoToSeat;
		
	}
	public void DoGoToPay() {
		xDestination = cashierX;
		yDestination = cashierY;
		command = Command.GoToPay;
	}

	public void DoExitRestaurant() {
		xDestination = xExit;
		yDestination = yExit;
		command = Command.LeaveRestaurant;
	}

	public void FullCaseExitRestaurant() {
		xDestination = xExit;
		yDestination = yExit;
		command = Command.LeaveRestaurant;
	}

	public void setWaitingPos(int iterate) {
		currentSpot = iterate;
		yWait = yWait + iterate * 40;
		yPos = yWait;
		yDestination = yWait;
		xPos = xWait;
		xDestination = xWait;
	}
	public void moveUpQueue() {
		currentSpot = currentSpot - 1;
		yWait = currentSpot * 40;
		yPos = yWait;
		yDestination = yWait;
		xPos = xWait;
		xDestination = xWait;
	}
	public void DoGoToHost() {
		command = Command.GoToHost;
		xDestination = hostX;
		yDestination = hostY;
	}
}
