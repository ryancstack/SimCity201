package market.gui;

import gui.GUIMarket;
import gui.Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import market.MarketWorkerRole;

public class MarketGui implements Gui {

	private MarketWorkerRole role = null;
	GUIMarket gui;
	private String info;
	
	private boolean isPresent = false;
	private int xPos, yPos;
	private int xDestination, yDestination;
	
	private enum state {NoCommand, GettingItem, GoingToCounter, Leaving};
	private state command = state.NoCommand;
	
	BufferedImage marketRoleLeft;
	BufferedImage marketRoleRight;
	BufferedImage marketRoleUp;
	BufferedImage marketRoleDown;
	
	public static final int xCounter = 118;
	public static final int yCounter = 170;
	public static final int xStart = 102;
	public static final int yStart = -50;
	public static final int xShelf = 39;
	public static final int yShelf = 46;
	
	
	public MarketGui(MarketWorkerRole mr) {
		role = mr;
//		gui = m;
		
		xPos = xStart;
		yPos = yStart;
		xDestination = xCounter;
		yDestination = yCounter;
		
	
		try {
			marketRoleLeft = ImageIO.read(getClass().getResource("GUIMarketRoleLeft.png"));
        	marketRoleRight = ImageIO.read(getClass().getResource("GUIMarketRoleRight.png"));
        	marketRoleUp = ImageIO.read(getClass().getResource("GUIMarketRoleUp.png"));
        	marketRoleDown = ImageIO.read(getClass().getResource("GUIMarketRoleDown.png"));
		}
		catch(IOException e) {
			System.out.println("Error w/ MarketRoleImage");
		}
		
		isPresent = false;
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
			if(command != state.NoCommand) {
				role.msgActionComplete();
				command = state.NoCommand;
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (xPos > xDestination) {
			g.drawImage(marketRoleLeft, xPos, yPos, null);
		}
		else if (xPos < xDestination) {
			g.drawImage(marketRoleRight,  xPos, yPos, null);
		}
		else if (yPos > yDestination) {
			g.drawImage(marketRoleUp, xPos, yPos, null);
		}
		else if (yPos < yDestination) {
			g.drawImage(marketRoleDown, xPos, yPos, null);
		}
		else {
			g.drawImage(marketRoleDown, xPos, yPos, null);		
		}
		
		info = role.getPersonAgent().getName();
		g.setColor(Color.magenta);
		g.drawString(info, xPos, yPos);
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	public void setPresent() {
		isPresent = true;
	}
	public void setIsNotPresent() {
		isPresent = false;
	}
	
	public void DoEnterMarket() {
		xDestination = xCounter;
		yDestination = yCounter;
		command = state.GoingToCounter;		
	}

	public void DoGetFood() {
		xDestination = xShelf;
		yDestination = yShelf;
		command = state.GettingItem;
	}
	
	public void DoGoToCounter() {
		xDestination = xCounter;
		yDestination = yCounter;
		command = state.GoingToCounter;
	}

	public void DoLeaveMarket() {
		xDestination = xStart;
		yDestination = yStart;
		command = state.Leaving;		
	}
	
}
