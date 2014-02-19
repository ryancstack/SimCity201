package gui;

import home.Apartment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import city.BusAgent;
import city.gui.BusGui;
import city.helpers.Clock;

public class MacroAnimationPanel extends JPanel implements ActionListener, MouseListener {
       
	BufferedImage cityImage;
	BufferedImage cityImageTop;
	BufferedImage cityImageNight;
	BufferedImage cityImageNightTop;

	private final int WINDOWX = 835;
    private final int WINDOWY = 406;
   
    private final int DELAY = 5;

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    ArrayList<Building> buildings = new ArrayList<Building>();
    BusAgent bus;
    BusGui busGui;
    
    //SimCityPanel
    protected SimCityGui city;
    protected Color background;
    protected Timer timer;
    
    //creating animation
    
    //number of grids in X and in Y
    static int gridX= 42; // 42 checking if works faster with less grids
    static int gridY= 21; // 21 to accommodate all pixels in blocks of 20
     
    
    Semaphore[][] grid = new Semaphore[gridX+1][gridY+1]; 

    public MacroAnimationPanel(SimCityGui city) { 
    	
    	this.city = city;
    	
    	//initializing semaphore grid
    	for (int i= 0; i<gridX+1; i++){
    		for (int j= 0; j<gridY+1; j++){
    			grid[i][j]= new Semaphore(1,true);
    		}
    	}
    	try{
    		for(int i=0; i<gridY+1; i++){
    			grid[0][0+i].acquire();
    		}
    		for(int i=1; i<gridX+1; i++){ //because 0th one is already taken
    			grid[0+i][0].acquire();	
    		}
    		
    		for(int i=1;i<gridX+1; i++){
    			grid[0+i][1].acquire();
    			grid[0+i][2].acquire();
    			grid[0+i][3].acquire();
    			grid[0+i][4].acquire();

    		}
    		
    		//x=5, y=18 grids
    		
    		for(int i=1; i<=5; i++){ //first 5 columns
    			for(int j=5; j<= 18; j++){ //start from 5 bc first top 4 rows were already acquired
    				grid[i][j].acquire();
    			}
    		}
  		
    	}
    	catch(Exception e){
    		System.out.println("Caught unexpected exception during grid setup: "+ e);
    	}
    	    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        setBackground(Color.LIGHT_GRAY);
        
    	timer = new Timer(DELAY, this);
    	timer.start();
    	
    	//buildings = new ArrayList<Building>();

    //RESTAURANT
    	Building stackRestaurant = new Building(0, 222, 105, 150);
    	stackRestaurant.setName("StackRestaurant");
    	buildings.add(stackRestaurant);
    	
    	Building huangRestaurant = new Building(162, 8, 285, 92);
    	huangRestaurant.setName("HuangRestaurant");
    	buildings.add(huangRestaurant);
    	
    	Building nakamuraRestaurant = new Building(287, 9, 127, 73);
    	nakamuraRestaurant.setName("NakamuraRestaurant");
    	buildings.add(nakamuraRestaurant);
    	
    	Building phillipsRestaurant = new Building(724, 276, 108, 96);
    	phillipsRestaurant.setName("PhillipsRestaurant");
    	buildings.add(phillipsRestaurant);
    	
    	Building shehRestaurant = new Building(572, 333, 123, 81);
    	shehRestaurant.setName("ShehRestaurant");
    	buildings.add(shehRestaurant);
    	
    	Building tanRestaurant = new Building(240, 332, 159, 83);
    	tanRestaurant.setName("TanRestaurant");
    	buildings.add(tanRestaurant);
    
    //HOUSES
    	Building house1 = new Building(275, 212, 105, 94);
    	house1.setName("House1");
    	buildings.add(house1);
    	
    	Building house2 = new Building(275, 133, 105, 83);
    	house2.setName("House2");
    	buildings.add(house2);
    	
    	Building house3 = new Building(448, 138, 98, 80);
    	house3.setName("House3");
    	buildings.add(house3);
    	
    	Building house4 = new Building(551, 137, 126, 55);
    	house4.setName("House4");
    	buildings.add(house4);
    	
    	Building house5 = new Building(570, 192, 105, 117);
    	house5.setName("House5");
    	buildings.add(house5);

    	Building house6 = new Building(449, 214, 120, 91);
    	house6.setName("House6");
    	buildings.add(house6);
    	
    //MARKET
    	Building market1 = new Building(432, 8, 136, 82);
    	market1.setName("Market");
    	buildings.add(market1);
    	
    	Building market2 = new Building(432, 333, 137, 81);
    	market2.setName("Market2");
    	buildings.add(market2);
    	
    //APARTMENT
    	
    	Building apartmentA = new Building(162,136,113, 166);
    	apartmentA.setName("ApartmentA");
    	buildings.add(apartmentA);
    	
    	Building apartmentB = new Building(570, 0, 145, 84);
    	apartmentB.setName("ApartmentB");
    	buildings.add(apartmentB);
    	
    	Building apartmentC = new Building(729, 127,106, 150);
    	apartmentC.setName("ApartmentC");
    	buildings.add(apartmentC);
    	
    	
    //BANKS 
    	Building bank = new Building(0, 0, 104, 214);
    	bank.setName("Bank");
    	buildings.add(bank);
    	
    	Building bank2 = new Building(718, 0, 117, 101);
    	bank2.setName("Bank2");
    	buildings.add(bank2);

    	addMouseListener(this);
    	
    	//CITY BACKGROUND
    	try {
        	cityImage = ImageIO.read(getClass().getResource("SIMCITYBOTTOM.png"));
        	cityImageTop = ImageIO.read(getClass().getResource("SIMCITYTOP.png"));
        	cityImageNight = ImageIO.read(getClass().getResource("SIMCITYBOTTOMNIGHT.png"));
        	cityImageNightTop = ImageIO.read(getClass().getResource("SIMCITYTOPNIGHT.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
		
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
		this.repaint();
		synchronized(buildings) {
			for(Building b : this.buildings) {
				if(b.hasBuildingPanel()) {
					b.getBuildingPanel().updateGui();
				}
			}
		}
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.black);
        
        //PLACED ONTOP FOR BUILDING REFERENCE PURPOSES
       
	    synchronized(buildings) {
	        for(int i = 0; i < buildings.size(); i++) {
	        	Building b = buildings.get(i);
	        	g2.fill(b);
	        }
        }
        
        if(Clock.sharedInstance().isDay()) 
        	g2.drawImage(cityImage, 0, 0, null);
        else 
        	g2.drawImage(cityImageNight, 0, 0, null);
        
        synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        }

        synchronized(guis) {
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        }
        
        //moveComponents();
        //drawComponents(g);
        
        if(Clock.sharedInstance().isDay()) 
        	g2.drawImage(cityImageTop, 0, 0, null);
        else 
        	g2.drawImage(cityImageNightTop, 0, 0, null);
    }
    
    public ArrayList<Building> getBuildings() {
    	return buildings;
    }

    public void addGui(Gui gui) {
        guis.add(gui);
    }

    //if click on building, then building displayed
	public void mouseClicked(MouseEvent e) {
		synchronized(buildings) {
			for(int i = 0; i < buildings.size(); i++) {
				Building b = buildings.get(i);
				if(b.contains(e.getX(), e.getY())) {
					b.displayBuilding();
					city.setUniqueBuildingPanel(b.getName());
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void removeGui(Gui gui) {
		guis.remove(gui);
	}
	
	public void setSpeed(int speed) {
		timer.setDelay(speed);
	}
}
