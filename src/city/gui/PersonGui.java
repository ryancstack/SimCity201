package city.gui;

import gui.Gui;
import city.helpers.ApartmentHelper;
import city.helpers.Clock;
import city.helpers.Directory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import city.PersonAgent;

public class PersonGui implements Gui {
	
	private PersonAgent agent = null;
	private int xMult = 0;
	private int yMult = 0;
	
	private int xPos, yPos;
	private int xDestination, yDestination;
	private int xBed, yBed, xKitchen, yKitchen, xTable, yTable, xDoor, yDoor, xFridge, yFridge;

	BufferedImage personLeft;
	BufferedImage personRight;
	BufferedImage personUp;
	BufferedImage personDown;
	String info;
	
	boolean isPresent = true;
	
	public enum CurrentAction {Cooking, Eating, Transition, Idle, Deciding, Leaving, Sleeping, CleanRoom1, CleanRoom2, CleanRoom3, CleanRoom4};
	CurrentAction currentAction = CurrentAction.Idle;
	public PersonGui(PersonAgent agent) {
		String address = agent.getAddress();
		xMult = ApartmentHelper.sharedInstance().getXMultiplier(address) * 245;
		yMult = ApartmentHelper.sharedInstance().getYMultiplier(address) * 103;
		
		//HOME
		if(address.toLowerCase().contains("house")) {
			xBed = 5;
			yBed = 135;
			xKitchen = 695;
			yKitchen = 160;
			xFridge = 800;
			yFridge = 130;
			xTable = 425;
			yTable = 250;
			xDoor = 380;
			yDoor = 160;
			xPos = xBed;
			yPos = yBed;
			xDestination = xBed;
			yDestination = yBed;
			
			try {
	        	personLeft = ImageIO.read(getClass().getResource("GUIPersonLeft.png"));
	        	personRight = ImageIO.read(getClass().getResource("GUIPersonRight.png"));
	        	personUp = ImageIO.read(getClass().getResource("GUIPersonUp.png"));
	        	personDown = ImageIO.read(getClass().getResource("GUIPersonDown.png"));
	        }
	        catch(IOException e) {
	        	System.out.println("Error w/ Person assets");
	        }
		}
		//APARTMENT
		else if(address.toLowerCase().contains("apartment")) {		
			xBed = 219 + xMult;
			yBed = 35 + yMult;
			xKitchen = 14 + xMult;
			yKitchen = 25 + yMult;
			xTable = 23 + xMult;
			yTable = 74 + yMult;
			xDoor = 87 + xMult;
			yDoor = 26 + yMult;
			xPos = xBed;
			yPos = yBed;
			xDestination = xBed;
			yDestination = yBed;
			
			try {
	        	personLeft = ImageIO.read(getClass().getResource("GUICITYPersonLeft.png"));
	        	personRight = ImageIO.read(getClass().getResource("GUICITYPersonRight.png"));
	        	personDown = ImageIO.read(getClass().getResource("GUICITYPersonDown.png"));
	        	personUp = ImageIO.read(getClass().getResource("GUICITYPersonUp.png"));
	        }
	        catch(IOException e) {
	        	System.out.println("Error w/ Person assets");
	        }
		}
		else if(address.toLowerCase().contains("landlord")) {
			xBed = 632;
			yBed = 314;
			xKitchen = 817;
			yKitchen = 208;
			xTable = 808;
			yTable = 252;;
			xDoor = 729;
			yDoor = 199;
			xPos = xBed;
			yPos = yBed;
			xDestination = xBed;
			yDestination = yBed;
			
			try {
	        	personLeft = ImageIO.read(getClass().getResource("GUICITYPersonLeft.png"));
	        	personRight = ImageIO.read(getClass().getResource("GUICITYPersonRight.png"));
	        	personDown = ImageIO.read(getClass().getResource("GUICITYPersonDown.png"));
	        	personUp = ImageIO.read(getClass().getResource("GUICITYPersonUp.png"));
	        }
	        catch(IOException e) {
	        	System.out.println("Error w/ Person assets");
	        }
		}

		this.agent = agent;
	}

	
	@Override
	public void updatePosition() {
		if (xPos < xDestination) {
			xPos+= 1;
		}
		else if (xPos > xDestination) {
			xPos-= 1;
		}
		
		if (yPos < yDestination) {
			yPos+= 1;
		}
		else if (yPos > yDestination) {
			yPos-= 1;
		}
		
		if(xPos == xKitchen && yPos == yKitchen && currentAction == CurrentAction.Cooking) {
			currentAction = CurrentAction.Transition;
			agent.msgActionComplete();
		}
		if(xPos == xTable && yPos == yTable && currentAction == CurrentAction.Eating) {
			currentAction = CurrentAction.Transition;
			agent.msgActionComplete();
		}
		if(xPos == xFridge && yPos == yFridge && currentAction == CurrentAction.Deciding) {
			currentAction = CurrentAction.Transition;
			agent.msgActionComplete();
		}
		if(xPos == xDoor && yPos == yDoor && currentAction == CurrentAction.Leaving) {
			currentAction = CurrentAction.Transition;
			agent.msgActionComplete();
		}
		/**
		 * Cleaning Block
		 */
		if(xPos == xKitchen && yPos == yKitchen && currentAction == CurrentAction.CleanRoom1) {
			currentAction = CurrentAction.CleanRoom2;
			xDestination = xTable;
			yDestination = yTable;
		}
		if(xPos == xTable && yPos == yTable && currentAction == CurrentAction.CleanRoom2) {
			currentAction = CurrentAction.CleanRoom3;
			xDestination = xBed;
			yDestination = yBed;
		}
		if(xPos == xBed && yPos == yBed && currentAction == CurrentAction.CleanRoom3) {
			currentAction = CurrentAction.Transition;
			agent.msgActionComplete();
		}
//		if(xPos == xBed && yPos == yBed && currentAction == CurrentAction.Sleeping) {
//			currentAction = CurrentAction.Transition;
//			agent.msgActionComplete();
//		}
	}

	@Override
	public void draw(Graphics2D g) {
		info = agent.getName() + "(" + agent.getPersonState() + ")";
		g.setColor(Color.magenta);
		g.drawString(info, xPos, yPos);
		
		if (xPos < xDestination) {
			g.drawImage(personRight, xPos, yPos, null);
		}
		else if (xPos > xDestination) {
			g.drawImage(personLeft,  xPos, yPos, null);
		}
		else if (yPos < yDestination) {
			g.drawImage(personDown, xPos, yPos, null);
		}
		else if (yPos > yDestination) {
			g.drawImage(personUp, xPos, yPos, null);
		}
		else if (xPos == xBed && yPos == yBed) {
			g.drawImage(personDown, xPos, yPos, null);
		}
		else if (xPos == xTable && yPos == yTable) {
			g.drawImage(personDown, xPos, yPos, null);
		}
		else if (xPos == xKitchen && yPos == yKitchen) {
			g.drawImage(personDown , xPos, yPos, null);
		}
		else {
			g.drawImage(personDown, xPos, yPos, null);
		}
	}

	public void setPresentFalse() {
		isPresent = false;
	}
	public void setPresentTrue() {
		isPresent = true;
	}
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return isPresent;
	}
	public void DoDecideEat() {
		currentAction = CurrentAction.Deciding;
		xDestination = xFridge;
		yDestination = yFridge;
	}
	public void DoCook() {
		currentAction = CurrentAction.Cooking;
		xDestination = xKitchen;
		yDestination = yKitchen;
	}
	public void DoEat() {
		currentAction = CurrentAction.Eating;
		xDestination = xTable;
		yDestination = yTable;
	}
	public void DoSleep() {
		currentAction = CurrentAction.Sleeping;
		xDestination = xBed;
		yDestination = yBed;
	}
	public void DoLeaveHouse() {
		currentAction = CurrentAction.Leaving;
		xDestination = xDoor;
		yDestination = yDoor;
	}
	public void DoEnterHouse() {
		xPos = xDoor;
		yPos = yDoor;
		xDestination = xBed;
		yDestination = yBed;
	}

	public void DoClean() {
		currentAction = CurrentAction.CleanRoom1;
		xDestination = xKitchen;
		yDestination = yKitchen;
	}
}
