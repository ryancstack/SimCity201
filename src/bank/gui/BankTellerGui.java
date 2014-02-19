package bank.gui;

import gui.Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import bank.BankTellerRole;


public class BankTellerGui implements Gui {
    
	private BankTellerRole agent = null;
	int tellerNum;
	String info;
    
    public int xPos = 0, yPos = 0;//default teller position
    public int xDestination = 0, yDestination = 0;//default start position
    
    private enum Command {noCommand, GoToManager, GoToRegister, LeaveBank};
	private Command tellerCommand =Command.noCommand;
    
    private static final int xManager = 400, yManager = 68;
	private static final int xExit = 100, yExit = 100;
	BufferedImage customerImage;
	
	private static final List<Point> tellerBench = new ArrayList<Point>() {{
		add(new Point(5, 51));
		add(new Point(5, 127));
		add(new Point(5, 210));
		add(new Point(778-30, 51));
		add(new Point(778, 127));
		add(new Point(778, 210));
	}};
	
	
	BufferedImage personLeft;
	BufferedImage personRight;
	BufferedImage personUp;
	BufferedImage personDown;
	
	public BankTellerGui(BankTellerRole tellerAgent) {
		agent = tellerAgent;
		xPos = 450;
		yPos = 450;
		xDestination = 450;
		yDestination = 450;
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
	

	public void setAgent(BankTellerRole agent){
		this.agent = agent;
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
		
		if(xPos == xDestination && yPos == yDestination
			&& tellerCommand == Command.GoToRegister) {
			agent.msgAtRegister();
			tellerCommand = Command.noCommand;
		}
		if(xPos == xDestination && yPos == yDestination
				&& (xDestination == xManager) && (yDestination == yManager) && tellerCommand == Command.GoToManager) {
			System.out.println("At bank manager, releasing.");
			agent.msgAtManager();
			tellerCommand = Command.noCommand;
		}

		if (xPos == xDestination && yPos == yDestination
				&& (xDestination == xExit) && (yDestination == yExit) && tellerCommand == Command.LeaveBank) {
			agent.msgAnimationFinishedLeavingBank();
			tellerCommand = Command.noCommand;
		}		
	}
    
	@Override
	public void draw(Graphics2D g) {
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
		else {
			g.drawImage(personDown, xPos, yPos, null);
		}
		
		//info = agent.getPersonAgent().getName() + "(" + agent.getState() + ")";
		//g.setColor(Color.magenta);
		//g.drawString(info, xPos, yPos);
	}
    
	@Override
	public boolean isPresent() {
		return true;
	}
	
	public void DoGoToRegister(int tellerNum) {
		this.tellerNum = tellerNum;
		xDestination = (int) tellerBench.get(tellerNum).getX();
		yDestination = (int) tellerBench.get(tellerNum).getY();

		tellerCommand = Command.GoToRegister;
	}
	
	public void DoGoToManager() {
		xDestination = xManager;
		yDestination = yManager;
		tellerCommand = Command.GoToManager;
	}

	public void DoLeaveBank() {
		xDestination = xExit;
		yDestination = yExit;
		tellerCommand = Command.LeaveBank;
	}
	
	/**
	 * @return the xDestination
	 */
	public int getxDestination() {
		return xDestination;
	}

	/**
	 * @return the yDestination
	 */
	public int getyDestination() {
		return yDestination;
	}

	/**
	 * @return the xmanager
	 */
	public static int getXmanager() {
		return xManager;
	}

	/**
	 * @return the ymanager
	 */
	public static int getYmanager() {
		return yManager;
	}

	/**
	 * @return the xexit
	 */
	public static int getXexit() {
		return xExit;
	}

	/**
	 * @return the yexit
	 */
	public static int getYexit() {
		return yExit;
	}
    
}
