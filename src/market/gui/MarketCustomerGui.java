package market.gui;

import gui.GUIMarket;
import gui.Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import market.MarketCustomerRole;

public class MarketCustomerGui implements Gui {

	private MarketCustomerRole role = null;
	GUIMarket gui;
	private String info;
	
	private boolean isPresent = false;
	private int xPos, yPos;
	private int xDestination, yDestination;
	
	private enum state {NoCommand, Entering, Leaving};
	private state command = state.NoCommand;
	
	public static final int xCounter = 100;
	public static final int yCounter = 237;
	public static final int xStart = 833;
	public static final int yStart = 359;
	
	BufferedImage customerLeft;
	BufferedImage customerRight;
	BufferedImage customerUp;
	BufferedImage customerDown;
	
	
	public MarketCustomerGui(MarketCustomerRole mcr) {
		role = mcr;
//		gui = m;
		
		xPos = xStart;
		yPos = yStart;
		xDestination = xStart;
		yDestination = yStart;
		
		try {
        	customerLeft = ImageIO.read(getClass().getResource("GUIPersonLeft.png"));
        	customerRight = ImageIO.read(getClass().getResource("GUIPersonRight.png"));
        	customerUp = ImageIO.read(getClass().getResource("GUIPersonUp.png"));
        	customerDown = ImageIO.read(getClass().getResource("GUIPersonDown.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Person assets");
        }
		
		isPresent = true;
	}
	
	@Override
	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if(xPos == xDestination && yPos == yDestination) {
			if(command == state.Entering || command == state.Leaving) {
				role.msgActionComplete();
				command = state.NoCommand;
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (xPos < xDestination) {
			g.drawImage(customerRight, xPos, yPos, null);
		}
		else if (xPos > xDestination) {
			g.drawImage(customerLeft,  xPos, yPos, null);
		}
		else if (yPos < yDestination) {
			g.drawImage(customerUp, xPos, yPos, null);
		}
		else if (yPos > yDestination) {
			g.drawImage(customerDown, xPos, yPos, null);
		}
		else {
			g.drawImage(customerDown, xPos, yPos, null);
		}
		
		info = role.getPersonAgent().getName() + "(" + role.getState() + ")";
		g.setColor(Color.magenta);
		g.drawString(info, xPos, yPos);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}

	public void DoEnterMarket() {
		xDestination = xCounter;
		yDestination = yCounter;
		command = state.Entering;
	}
	
	public void DoLeaveMarket() {
		xDestination = xStart;
		yDestination = yStart;
		command = state.Leaving;
	}
}
