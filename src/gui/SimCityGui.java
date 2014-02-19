package gui;

import java.awt.CardLayout;
import java.awt.EventQueue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import agent.Role;
import city.BusAgent;
import city.PersonAgent;
import city.TrafficAgent;
import city.gui.BusGui;
import city.helpers.Clock;
import city.helpers.Directory;
import city.helpers.WalkLoopHelper;
import city.helpers.XMLReader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import restaurant.nakamuraRestaurant.gui.NakamuraRestaurantAnimationPanel;
import restaurant.Restaurant;
import restaurant.huangRestaurant.gui.HuangRestaurantAnimationPanel;
import restaurant.phillipsRestaurant.gui.PhillipsRestaurantAnimationPanel;
import restaurant.shehRestaurant.ShehCookRole;
import restaurant.shehRestaurant.ShehWaiterNormalRole;
import restaurant.shehRestaurant.ShehWaiterRole;
import restaurant.shehRestaurant.ShehWaiterSharedRole;
import restaurant.shehRestaurant.gui.ShehRestaurantAnimationPanel;
import restaurant.tanRestaurant.TanCookRole;
import restaurant.tanRestaurant.TanCustomerRole;
import restaurant.tanRestaurant.TanWaiterNormalRole;
import restaurant.tanRestaurant.TanWaiterSharedRole;
import restaurant.tanRestaurant.gui.TanRestaurantAnimationPanel;
import restaurant.stackRestaurant.gui.StackRestaurantAnimationPanel;
import trace.AlertTag;
import trace.TracePanel;

import java.awt.Font;

public class SimCityGui {
    
	//NEW STUFF
	JPanel buildingPanels;
	Random rand = new Random();
	CardLayout cardLayout;
	MacroAnimationPanel macroAnimationPanel;
	private JFrame frame;
	private Map<String, String> roles = new HashMap<String, String>();
	private HashMap<String, CityCard> cards = new HashMap<String, CityCard>();
	BusAgent bus;
	BusAgent bus2;
	BusGui busGui;
	BusGui busGui2;
	TrafficAgent trafficLight;
	TrafficAgent trafficLight1;
	TrafficAgent trafficLight2;
	private JPanel panel;
	private JTabbedPane tabbedPane;
	private JLabel lblTime;
	private JPanel logPanel;
	private TracePanel tracePanel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimCityGui window = new SimCityGui();
					window.frame.setVisible(true);
					
					URL url = new File("src/gui/12-new-bark-town.wav").toURI().toURL();
					Clip audioClip = AudioSystem.getClip();
					AudioInputStream ais = AudioSystem.getAudioInputStream(url);
					audioClip.open(ais);
					audioClip.loop(Clip.LOOP_CONTINUOUSLY);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
    
	/**
	 * Create the application.
	 */
	public SimCityGui() {
		initialize();
		populateCards();
		Directory.sharedInstance().setCityGui(this);
		Clock.sharedInstance().setGui(this);
		runSuperNorm();
	}
    
	private void populateCards() {
		ArrayList<Building> buildings = macroAnimationPanel.getBuildings();
		for ( int i=0; i<buildings.size(); i++ ) {
			Building b = buildings.get(i);
			
			if(b.getName().toLowerCase().contains("house")) {
				b.setBuildingPanel(new GUIHome( b, i, this ));
			}
			else if(b.getName().toLowerCase().contains("market")) {
				b.setBuildingPanel(new GUIMarket( b, i, this ));
			}
			else if(b.getName().toLowerCase().contains("apartment")) {
				b.setBuildingPanel(new GUIApartment(b, i, this));
			}
			else if(b.getName().toLowerCase().contains("bank")) {
				b.setBuildingPanel(new GUIBank( b, i, this ));
			}
			else if(b.getName().toLowerCase().contains("bank2")) {
				b.setBuildingPanel(new GUIBank( b, i, this ));
			}
			else if(b.getName().toLowerCase().contains("stack")) {
				b.setBuildingPanel(new StackRestaurantAnimationPanel(b, i, this));
            }
			else if(b.getName().toLowerCase().contains("sheh")) {
				b.setBuildingPanel(new ShehRestaurantAnimationPanel(b, i, this));
			}
			else if(b.getName().toLowerCase().contains("nakamura")) {
				b.setBuildingPanel(new NakamuraRestaurantAnimationPanel(b, i, this));
			}
			else if(b.getName().toLowerCase().contains("phillips")) {
				b.setBuildingPanel(new PhillipsRestaurantAnimationPanel(b, i, this));
			}
			else if(b.getName().toLowerCase().contains("tan")) {
				b.setBuildingPanel(new TanRestaurantAnimationPanel(b, i, this));
			}
			else if(b.getName().toLowerCase().contains("huang")) {
				b.setBuildingPanel(new HuangRestaurantAnimationPanel(b, i, this));
			}
			else {
				b.setBuildingPanel(new GUIMarket( b, i, this ));
			}
			buildingPanels.add( b.myBuildingPanel, "" + i );
		}
		
		buildingPanels.setBounds(5, 425, 827, 406);
		frame.getContentPane().add(buildingPanels);
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() { //BUTTONS
		roles.put("None", "Unemployed");
		roles.put("Bank Teller", "BankTeller");
		roles.put("Bank 2 Teller", "BankTeller2");
        roles.put("Market 1 Seller", "Market");
        roles.put("Market 2 Seller", "Market2");
        
        roles.put("Landlord A", "LandlordA");
        roles.put("Landlord B", "LandlordB");
        roles.put("Landlord C", "LandlordC");
        
        roles.put("Stack's Restaurant Waiter Normal", "StackWaiterNormal");
        roles.put("Stack's Restaurant Waiter Shared", "StackWaiterShared");
        roles.put("Stack's Restaurant Cook", "StackCook");
        
        roles.put("Sheh's Restaurant Waiter Normal", "ShehWaiterNormal");
        roles.put("Sheh's Restaurant Waiter Shared", "ShehWaiterShared");
        roles.put("Sheh's Restaurant Cook", "ShehCook");
        
        roles.put("Huang's Restaurant Waiter Normal", "HuangWaiterNormal");
        roles.put("Huang's Restaurant Waiter Shared", "HuangWaiterShared");
        roles.put("Huang's Restaurant Cook", "HuangCook");
        
        roles.put("Nakamura's Restaurant Waiter Normal", "NakamuraWaiterNormal");
        roles.put("Nakamura's Restaurant Waiter Shared", "NakamuraWaiterShared");
        roles.put("Nakamura's Restaurant Cook", "NakamuraCook");
        
        roles.put("Tan's Restaurant Waiter Normal", "TanWaiterNormal");
        roles.put("Tan's Restaurant Waiter Shared", "TanWaiterShared");
        roles.put("Tan's Restaurant Cook", "TanCook");
        
        roles.put("Richard's Restaurant Waiter Normal", "RichardWaiterNormal");
        //roles.put("Richards's Restaurant Waiter Shared", "RichardWaiterShared");
        roles.put("Richard's Restaurant Cook", "RichardCook");
        
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(0, 0, 1133, 855);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		macroAnimationPanel = new MacroAnimationPanel(this);
		macroAnimationPanel.setBounds(5, 5, 827, 406);
		frame.getContentPane().add(macroAnimationPanel);
		
		//MicroAnimationPanel microAnimationPanel = new MicroAnimationPanel();
		buildingPanels = new JPanel();
		cardLayout = new CardLayout();
        //		microAnimationPanel.setLayout(cardLayout);
		buildingPanels.setLayout(cardLayout);
		
		
		//TAKES SQUARES FROM MACRO AND TURNS INTO PANELS IN MICRO
		
		
		SpringLayout springLayout = new SpringLayout();
		springLayout.putConstraint(SpringLayout.WEST, buildingPanels, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, macroAnimationPanel, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, macroAnimationPanel, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, macroAnimationPanel, -6, SpringLayout.NORTH, buildingPanels);
		springLayout.putConstraint(SpringLayout.NORTH, buildingPanels, 417, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, buildingPanels, -10, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().setLayout(springLayout);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.EAST, buildingPanels, -6, SpringLayout.WEST, tabbedPane);
		springLayout.putConstraint(SpringLayout.EAST, macroAnimationPanel, -6, SpringLayout.WEST, tabbedPane);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 843, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(tabbedPane);
		
		panel = new JPanel();
		tabbedPane.addTab("Create Person", null, panel, null);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		final JButton btnPopulateCity = new JButton("Populate City");
		btnPopulateCity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateCity("src/city/helpers/normative.xml");	//
				btnPopulateCity.setEnabled(false);
				
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnPopulateCity, 10, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, btnPopulateCity, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, btnPopulateCity, 249, SpringLayout.WEST, panel);
		panel.add(btnPopulateCity);
		
		JLabel lblName = new JLabel("Name:");
		sl_panel.putConstraint(SpringLayout.WEST, lblName, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblName);
		
		final JTextField nameTextField = new JTextField();
		sl_panel.putConstraint(SpringLayout.NORTH, lblName, 6, SpringLayout.NORTH, nameTextField);
		sl_panel.putConstraint(SpringLayout.NORTH, nameTextField, 6, SpringLayout.SOUTH, btnPopulateCity);
		sl_panel.putConstraint(SpringLayout.EAST, nameTextField, -10, SpringLayout.EAST, panel);
		panel.add(nameTextField);
		nameTextField.setColumns(10);
		
		JLabel lblOccupation = new JLabel("Occupation");
		sl_panel.putConstraint(SpringLayout.WEST, lblOccupation, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblOccupation);
		
		final JComboBox<String> occupationComboBox = new JComboBox<String>();
		sl_panel.putConstraint(SpringLayout.NORTH, lblOccupation, 4, SpringLayout.NORTH, occupationComboBox);
		sl_panel.putConstraint(SpringLayout.NORTH, occupationComboBox, 6, SpringLayout.SOUTH, nameTextField);
		sl_panel.putConstraint(SpringLayout.WEST, occupationComboBox, 0, SpringLayout.WEST, nameTextField);
		sl_panel.putConstraint(SpringLayout.EAST, occupationComboBox, 0, SpringLayout.EAST, btnPopulateCity);
		
		occupationComboBox.addItem("None");
		occupationComboBox.addItem("Bank Teller");
		occupationComboBox.addItem("Bank 2 Teller");
		occupationComboBox.addItem("Market 1 Seller");
		occupationComboBox.addItem("Market 2 Seller");
		
        occupationComboBox.addItem("Landlord A");
        occupationComboBox.addItem("Landlord B");
        occupationComboBox.addItem("Landlord C");
		
		//Sheh
		occupationComboBox.addItem("Sheh's Restaurant Waiter Normal");
		occupationComboBox.addItem("Sheh's Restaurant Waiter Shared");
		occupationComboBox.addItem("Sheh's Restaurant Cook");
		
		//Stack
		occupationComboBox.addItem("Stack's Restaurant Waiter Normal");
		occupationComboBox.addItem("Stack's Restaurant Waiter Shared");
		occupationComboBox.addItem("Stack's Restaurant Cook");
		
		//Huang
		occupationComboBox.addItem("Huang's Restaurant Waiter Normal");
		occupationComboBox.addItem("Huang's Restaurant Waiter Shared");
		occupationComboBox.addItem("Huang's Restaurant Cook");
		

        //		occupationComboBox.addItem("Philips's Restaurant Host");
        //		occupationComboBox.addItem("Philips's Restaurant Waiter");
        //		occupationComboBox.addItem("Philips's Restaurant Cook");
        //		occupationComboBox.addItem("Philips's Restaurant Cashier");
        
		//Tan
        occupationComboBox.addItem("Tan's Restaurant Host");
        occupationComboBox.addItem("Tan's Restaurant Waiter");
        occupationComboBox.addItem("Tan's Restaurant Cook");
        occupationComboBox.addItem("Tan's Restaurant Cashier");
        //
        //		occupationComboBox.addItem("Huang's Restaurant Host");
        //		occupationComboBox.addItem("Huang's Restaurant Waiter");
        //		occupationComboBox.addItem("Huang's Restaurant Cook");
        //		occupationComboBox.addItem("Huang's Restaurant Cashier");
        //Nakamura
        occupationComboBox.addItem("Nakamura's Restaurant Waiter Normal");
        occupationComboBox.addItem("Nakamura's Restaurant Waiter Shared");
        occupationComboBox.addItem("Nakamura's Restaurant Cook");
        
		panel.add(occupationComboBox);
		
		final JComboBox<String> transportationComboBox = new JComboBox<String>();
		sl_panel.putConstraint(SpringLayout.NORTH, transportationComboBox, 6, SpringLayout.SOUTH, occupationComboBox);
		sl_panel.putConstraint(SpringLayout.WEST, transportationComboBox, 0, SpringLayout.WEST, nameTextField);
		sl_panel.putConstraint(SpringLayout.EAST, transportationComboBox, 0, SpringLayout.EAST, btnPopulateCity);
		
		transportationComboBox.addItem("None");
		transportationComboBox.addItem("Owns A Car");
		transportationComboBox.addItem("Takes The Bus");
		
		panel.add(transportationComboBox);
		
		JLabel lblTransportation = new JLabel("Transportation");
		sl_panel.putConstraint(SpringLayout.NORTH, lblTransportation, 4, SpringLayout.NORTH, transportationComboBox);
		sl_panel.putConstraint(SpringLayout.WEST, lblTransportation, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblTransportation);
		
		JLabel lblInitialFunds = new JLabel("Initial Funds");
		sl_panel.putConstraint(SpringLayout.WEST, lblInitialFunds, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblInitialFunds);
		
		final JLabel lblMoney = new JLabel("$5000");
		sl_panel.putConstraint(SpringLayout.NORTH, lblInitialFunds, 0, SpringLayout.NORTH, lblMoney);
		sl_panel.putConstraint(SpringLayout.EAST, lblMoney, -10, SpringLayout.EAST, panel);
		panel.add(lblMoney);
		
		int beginningFundsMin = 0;
		int beginningFundsMax = 10000;
		int beginningFundsStart = 5000;
		final JSlider initialFundsSlider = new JSlider(beginningFundsMin, beginningFundsMax, beginningFundsStart);
		initialFundsSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblMoney.setText("$"+initialFundsSlider.getValue());
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, initialFundsSlider, 6, SpringLayout.SOUTH, lblInitialFunds);
		sl_panel.putConstraint(SpringLayout.WEST, initialFundsSlider, 0, SpringLayout.WEST, btnPopulateCity);
		sl_panel.putConstraint(SpringLayout.EAST, initialFundsSlider, 0, SpringLayout.EAST, btnPopulateCity);
		
		initialFundsSlider.setMajorTickSpacing(1000);
		initialFundsSlider.setMinorTickSpacing(200);
		initialFundsSlider.setPaintTicks(true);
		
		panel.add(initialFundsSlider);
		
		JLabel lblAggressiveness = new JLabel("Aggressiveness");
		sl_panel.putConstraint(SpringLayout.NORTH, lblAggressiveness, 6, SpringLayout.SOUTH, initialFundsSlider);
		sl_panel.putConstraint(SpringLayout.WEST, lblAggressiveness, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblAggressiveness);
		
		final JLabel lblAggressivenessMeter = new JLabel("2");
		sl_panel.putConstraint(SpringLayout.NORTH, lblAggressivenessMeter, 0, SpringLayout.NORTH, lblAggressiveness);
		sl_panel.putConstraint(SpringLayout.EAST, lblAggressivenessMeter, 0, SpringLayout.EAST, btnPopulateCity);
		panel.add(lblAggressivenessMeter);
		
		int beginningAggressivenessMin = 1;
		int beginningAggressivenessMax = 3;
		int beginningAggressivenessStart = 2;
		final JSlider aggressivenessSlider = new JSlider(beginningAggressivenessMin, beginningAggressivenessMax, beginningAggressivenessStart);
		aggressivenessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblAggressivenessMeter.setText(aggressivenessSlider.getValue()+"");
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, aggressivenessSlider, 6, SpringLayout.SOUTH, lblAggressiveness);
		sl_panel.putConstraint(SpringLayout.WEST, aggressivenessSlider, 0, SpringLayout.WEST, btnPopulateCity);
		sl_panel.putConstraint(SpringLayout.EAST, aggressivenessSlider, 249, SpringLayout.WEST, panel);
		
		aggressivenessSlider.setMajorTickSpacing(1);
		aggressivenessSlider.setPaintTicks(true);
		
		panel.add(aggressivenessSlider);
		
		JLabel lblHousing = new JLabel("Housing");
		sl_panel.putConstraint(SpringLayout.WEST, lblHousing, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblHousing);
		
		final JComboBox<String> housingComboBox = new JComboBox<String>();
		sl_panel.putConstraint(SpringLayout.NORTH, lblMoney, 6, SpringLayout.SOUTH, housingComboBox);
		sl_panel.putConstraint(SpringLayout.NORTH, lblHousing, 4, SpringLayout.NORTH, housingComboBox);
		sl_panel.putConstraint(SpringLayout.NORTH, housingComboBox, 6, SpringLayout.SOUTH, transportationComboBox);
		sl_panel.putConstraint(SpringLayout.WEST, housingComboBox, 0, SpringLayout.WEST, nameTextField);
		sl_panel.putConstraint(SpringLayout.EAST, housingComboBox, 0, SpringLayout.EAST, btnPopulateCity);
		
		/*
		//houses not necessary
		housingComboBox.addItem("House1");
		housingComboBox.addItem("House2");
		housingComboBox.addItem("House3");
		housingComboBox.addItem("House4");
		housingComboBox.addItem("House5");
		housingComboBox.addItem("House6");
		
		housingComboBox.addItem("LandLordA");
		housingComboBox.addItem("LandLordB");
		housingComboBox.addItem("LandLordC");
		*/
		
		housingComboBox.addItem("ApartmentA01");
		housingComboBox.addItem("ApartmentA02");
		housingComboBox.addItem("ApartmentA03");
		housingComboBox.addItem("ApartmentA04");
		housingComboBox.addItem("ApartmentA05");
		housingComboBox.addItem("ApartmentA06");
		housingComboBox.addItem("ApartmentA07");
		housingComboBox.addItem("ApartmentA08");
		housingComboBox.addItem("ApartmentA09");
		housingComboBox.addItem("ApartmentA10");
		housingComboBox.addItem("ApartmentA11");
		housingComboBox.addItem("ApartmentA12");
		housingComboBox.addItem("ApartmentA13");
		housingComboBox.addItem("ApartmentA14");
		//housingComboBox.addItem("ApartmentA15");
		
		housingComboBox.addItem("ApartmentB01");
		housingComboBox.addItem("ApartmentB02");
		housingComboBox.addItem("ApartmentB03");
		housingComboBox.addItem("ApartmentB04");
		housingComboBox.addItem("ApartmentB05");
		housingComboBox.addItem("ApartmentB06");
		housingComboBox.addItem("ApartmentB07");
		housingComboBox.addItem("ApartmentB08");
		housingComboBox.addItem("ApartmentB09");
		housingComboBox.addItem("ApartmentB10");
		housingComboBox.addItem("ApartmentB11");
		housingComboBox.addItem("ApartmentB12");
		housingComboBox.addItem("ApartmentB13");
		housingComboBox.addItem("ApartmentB14");
		//housingComboBox.addItem("ApartmentB15");
		
		housingComboBox.addItem("ApartmentC01");
		housingComboBox.addItem("ApartmentC02");
		housingComboBox.addItem("ApartmentC03");
		housingComboBox.addItem("ApartmentC04");
		housingComboBox.addItem("ApartmentC05");
		housingComboBox.addItem("ApartmentC06");
		housingComboBox.addItem("ApartmentC07");
		housingComboBox.addItem("ApartmentC08");
		housingComboBox.addItem("ApartmentC09");
		housingComboBox.addItem("ApartmentC10");
		housingComboBox.addItem("ApartmentC11");
		housingComboBox.addItem("ApartmentC12");
		housingComboBox.addItem("ApartmentC13");
		housingComboBox.addItem("ApartmentC14");
		//housingComboBox.addItem("ApartmentC15");
		
		panel.add(housingComboBox);
		
		
		JButton btnCreatePerson = new JButton("Create Person");
		
		btnCreatePerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(nameTextField.getText() != "" &&
                    transportationComboBox.getSelectedItem() != "None" &&
                    housingComboBox.getSelectedItem() != "None") {
					
						String role = roles.get(occupationComboBox.getSelectedItem());
						String name = nameTextField.getText();
						int aggressivenessLevel = aggressivenessSlider.getValue();
						double initialFunds = (double) initialFundsSlider.getValue();
						String housing = (String) housingComboBox.getSelectedItem();
						String transportMethod = (String) transportationComboBox.getSelectedItem();
						
						//CREATE CONDITION WHERE IFF OCCUPATION COMBO BOX IS LANDLORD THEN HOUSING IS LAND LORD
						if(role.contains("lord")) {
							if (role.contains("lordA")) {
								housing = "ApartmentA15";
							}
							else if (role.contains("lordB")) {
								housing = "ApartmentB15";
							}
							else if (role.contains("lordC")) {
								housing = "ApartmentC15";
							}
							//TODO you have to go through an entry set
							PersonAgent person = new PersonAgent(role, name, aggressivenessLevel, initialFunds, housing, transportMethod);
							/**
							 * Remove Landlord role from being made after one has been made.
							 */
							occupationComboBox.removeItemAt(occupationComboBox.getSelectedIndex());
						}
						else {
							PersonAgent person = new PersonAgent(role, name, aggressivenessLevel, initialFunds, housing, transportMethod);	
							housingComboBox.removeItemAt(housingComboBox.getSelectedIndex());
						}
					} 		
				}
			}
		);
		sl_panel.putConstraint(SpringLayout.NORTH, btnCreatePerson, 6, SpringLayout.SOUTH, aggressivenessSlider);
		sl_panel.putConstraint(SpringLayout.WEST, btnCreatePerson, 0, SpringLayout.WEST, btnPopulateCity);
		sl_panel.putConstraint(SpringLayout.EAST, btnCreatePerson, 0, SpringLayout.EAST, btnPopulateCity);
		panel.add(btnCreatePerson);	
		
		
		JLabel lblSpeed = new JLabel("Animation Speed");
		sl_panel.putConstraint(SpringLayout.NORTH, lblSpeed, 50, SpringLayout.SOUTH, btnCreatePerson);
		sl_panel.putConstraint(SpringLayout.WEST, lblSpeed, 0, SpringLayout.WEST, btnCreatePerson);
		panel.add(lblSpeed);
		
		final JLabel lblSpeedMeter = new JLabel("5");
		sl_panel.putConstraint(SpringLayout.NORTH, lblSpeedMeter, 0, SpringLayout.NORTH, lblSpeed);
		sl_panel.putConstraint(SpringLayout.EAST, lblSpeedMeter, 0, SpringLayout.EAST, btnCreatePerson);
		panel.add(lblSpeedMeter);
		
		int beginningSpeedMin = 1;
		int beginningSpeedMax = 10;
		int beginningSpeedStart = 5;
		final JSlider speedSlider = new JSlider(beginningSpeedMin, beginningSpeedMax, beginningSpeedStart);
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblSpeedMeter.setText(speedSlider.getValue()+"");
				macroAnimationPanel.setSpeed(speedSlider.getValue());
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, speedSlider, 6, SpringLayout.SOUTH, lblSpeedMeter);
		sl_panel.putConstraint(SpringLayout.WEST, speedSlider, 0, SpringLayout.WEST, btnCreatePerson);
		sl_panel.putConstraint(SpringLayout.EAST, speedSlider, 0, SpringLayout.EAST, btnCreatePerson);
		
		speedSlider.setMajorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		
		panel.add(speedSlider);
		
		JLabel lblDhh = new JLabel("D:HH");
		sl_panel.putConstraint(SpringLayout.NORTH, lblDhh, 6, SpringLayout.SOUTH, speedSlider);
		sl_panel.putConstraint(SpringLayout.WEST, lblDhh, 0, SpringLayout.WEST, btnPopulateCity);
		panel.add(lblDhh);
		
		lblTime = new JLabel(Clock.sharedInstance().getDay() + ":" + Clock.sharedInstance.getHour());
		sl_panel.putConstraint(SpringLayout.NORTH, lblTime, 6, SpringLayout.SOUTH, speedSlider);
		sl_panel.putConstraint(SpringLayout.EAST, lblTime, 0, SpringLayout.EAST, btnPopulateCity);
		panel.add(lblTime);
		
		JButton btnIncrementHour = new JButton("Increment Hour");
		sl_panel.putConstraint(SpringLayout.EAST, btnIncrementHour, 121, SpringLayout.WEST, panel);
		btnIncrementHour.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnIncrementHour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clock.sharedInstance().incrementHour();
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnIncrementHour, 6, SpringLayout.SOUTH, lblDhh);
		sl_panel.putConstraint(SpringLayout.WEST, btnIncrementHour, 10, SpringLayout.WEST, panel);
		panel.add(btnIncrementHour);
		
		JButton btnIncrementDay = new JButton("Increment Day");
		btnIncrementDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clock.sharedInstance().incrementDay();
			}
		});
		sl_panel.putConstraint(SpringLayout.NORTH, btnIncrementDay, 6, SpringLayout.SOUTH, lblTime);
		sl_panel.putConstraint(SpringLayout.WEST, btnIncrementDay, 128, SpringLayout.WEST, btnPopulateCity);
		sl_panel.putConstraint(SpringLayout.EAST, btnIncrementDay, 0, SpringLayout.EAST, btnPopulateCity);
		btnIncrementDay.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		panel.add(btnIncrementDay);
		
		
		
		logPanel = new JPanel();
		tabbedPane.addTab("Alert Log", null, logPanel, null);
		SpringLayout sl_logPanel = new SpringLayout();
		logPanel.setLayout(sl_logPanel);
		
		Restaurant restaurant = Directory.sharedInstance().getRestaurants().get(0);
		CurrentBuildingPanel restPanel = new CurrentBuildingPanel(restaurant);
		restaurant.setInfoPanel(restPanel);
		tabbedPane.addTab("Current Building", restPanel);
		
		tracePanel = new TracePanel();
		sl_logPanel.putConstraint(SpringLayout.NORTH, tracePanel, 10, SpringLayout.NORTH, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, tracePanel, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tracePanel, 249, SpringLayout.WEST, logPanel);
		logPanel.add(tracePanel);
		
		final JToggleButton tglbtnBankCustomerMsg = new JToggleButton("Bank Customer");
		tglbtnBankCustomerMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnBankCustomerMsg.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.BANKCUSTOMER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.BANKCUSTOMER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnBankCustomerMsg, 10, SpringLayout.WEST, logPanel);
		tglbtnBankCustomerMsg.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnBankCustomerMsg);
		
		final JToggleButton tglbtnBankTellerMessages = new JToggleButton("Bank Teller");
		tglbtnBankTellerMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnBankTellerMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.BANKTELLER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.BANKTELLER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnBankTellerMessages, -10, SpringLayout.EAST, logPanel);
		tglbtnBankTellerMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnBankTellerMessages);
		
		final JToggleButton tglbtnBankManagerMessages = new JToggleButton("Bank Manager");
		tglbtnBankManagerMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnBankManagerMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.BANKMANAGER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.BANKMANAGER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnBankManagerMessages, 139, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnBankManagerMessages, -10, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnBankCustomerMsg, -19, SpringLayout.WEST, tglbtnBankManagerMessages);
		sl_logPanel.putConstraint(SpringLayout.NORTH, tglbtnBankManagerMessages, 0, SpringLayout.NORTH, tglbtnBankCustomerMsg);
		tglbtnBankManagerMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnBankManagerMessages);
		
		final JToggleButton tglbtnCookMessages = new JToggleButton("Cook");
		tglbtnCookMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnCookMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.COOK);
				else
					tracePanel.hideAlertsWithTag(AlertTag.COOK);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnCookMessages, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnCookMessages, -139, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnBankTellerMessages, 19, SpringLayout.EAST, tglbtnCookMessages);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnBankCustomerMsg, -6, SpringLayout.NORTH, tglbtnCookMessages);
		sl_logPanel.putConstraint(SpringLayout.NORTH, tglbtnBankTellerMessages, 0, SpringLayout.NORTH, tglbtnCookMessages);
		tglbtnCookMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnCookMessages);
		
		final JToggleButton tglbtnWaiterMessages = new JToggleButton("Waiter");
		tglbtnWaiterMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnWaiterMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.WAITER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.WAITER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnWaiterMessages, -10, SpringLayout.EAST, logPanel);
		tglbtnWaiterMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnWaiterMessages);
		
		final JToggleButton tglbtnHostMessages = new JToggleButton("Host");
		tglbtnHostMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnHostMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.HOST);
				else
					tracePanel.hideAlertsWithTag(AlertTag.HOST);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnHostMessages, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnHostMessages, -139, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnWaiterMessages, 19, SpringLayout.EAST, tglbtnHostMessages);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnCookMessages, -6, SpringLayout.NORTH, tglbtnHostMessages);
		sl_logPanel.putConstraint(SpringLayout.NORTH, tglbtnWaiterMessages, 0, SpringLayout.NORTH, tglbtnHostMessages);
		tglbtnHostMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnHostMessages);
		
		final JToggleButton tglbtnRestaurantCustomerButton = new JToggleButton("Restaurant Customer");
		tglbtnRestaurantCustomerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnRestaurantCustomerButton.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.RESTAURANTCUSTOMER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.RESTAURANTCUSTOMER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnRestaurantCustomerButton, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnRestaurantCustomerButton, 0, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnHostMessages, -6, SpringLayout.NORTH, tglbtnRestaurantCustomerButton);
		tglbtnRestaurantCustomerButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnRestaurantCustomerButton);
		
		final JToggleButton tglbtnMarketWorkerMessages = new JToggleButton("Market Worker");
		tglbtnMarketWorkerMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnMarketWorkerMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.MARKETWORKER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.MARKETWORKER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnRestaurantCustomerButton, -6, SpringLayout.NORTH, tglbtnMarketWorkerMessages);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnMarketWorkerMessages, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnMarketWorkerMessages, 0, SpringLayout.EAST, tglbtnRestaurantCustomerButton);
		tglbtnMarketWorkerMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnMarketWorkerMessages);
		
		final JToggleButton tglbtnMarketCustomerMessages = new JToggleButton("Market Customer");
		tglbtnMarketCustomerMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnMarketCustomerMessages.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.MARKETCUSTOMER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.MARKETCUSTOMER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnMarketCustomerMessages, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnMarketCustomerMessages, 0, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnMarketWorkerMessages, -6, SpringLayout.NORTH, tglbtnMarketCustomerMessages);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnMarketCustomerMessages, -10, SpringLayout.SOUTH, logPanel);
		tglbtnMarketCustomerMessages.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnMarketCustomerMessages);
		
		final JToggleButton tglbtnLandlord = new JToggleButton("Landlord");
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tracePanel, -6, SpringLayout.NORTH, tglbtnLandlord);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnLandlord, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnLandlord, -173, SpringLayout.EAST, logPanel);
		tglbtnLandlord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnLandlord.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.LANDLORD);
				else
					tracePanel.hideAlertsWithTag(AlertTag.LANDLORD);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnLandlord, -6, SpringLayout.NORTH, tglbtnBankCustomerMsg);
		tglbtnLandlord.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnLandlord);
		
		final JToggleButton tglbtnPerson = new JToggleButton("Person");
		sl_logPanel.putConstraint(SpringLayout.NORTH, tglbtnPerson, 0, SpringLayout.NORTH, tglbtnLandlord);
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnPerson, -10, SpringLayout.EAST, logPanel);
		tglbtnPerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnPerson.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.PERSON);
				else
					tracePanel.hideAlertsWithTag(AlertTag.PERSON);
			}
		});
		tglbtnPerson.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnPerson);
		
		final JToggleButton tglbtnCashier = new JToggleButton("Cashier");
		tglbtnCashier.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnCashier.isEnabled())
					tracePanel.showAlertsWithTag(AlertTag.CASHIER);
				else
					tracePanel.hideAlertsWithTag(AlertTag.CASHIER);
			}
		});
		sl_logPanel.putConstraint(SpringLayout.EAST, tglbtnCashier, -92, SpringLayout.EAST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnPerson, 6, SpringLayout.EAST, tglbtnCashier);
		sl_logPanel.putConstraint(SpringLayout.WEST, tglbtnCashier, 91, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, tglbtnCashier, -6, SpringLayout.NORTH, tglbtnBankCustomerMsg);
		tglbtnCashier.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		logPanel.add(tglbtnCashier);
		
		
	}
	
	private void populateCity(String source) {
		XMLReader reader = new XMLReader();
		ArrayList<PersonAgent> people = reader.initializePeople(source);
		for(PersonAgent person : people) {
			person.startThread();
		}	
	}
	
	private void runSuperNorm() {
		/** 
		 * Start of Hard Code Scenario
		 * 
		 */
		if(Clock.sharedInstance().isDay()) {
			
		}		
	
		if(WalkLoopHelper.sharedInstance() == null) {
			
		}
/*
		//REID TEST CODE
		PersonAgent person1 = new PersonAgent("NakamuraCook", "Test Person 1", 3, 1000.00, "House1", "TakesTheBus");
		PersonAgent person2 = new PersonAgent("NakamuraWaiterNormal", "Test Person 2", 3, 1000.00, "House2", "TakesTheBus");
		PersonAgent person3 = new PersonAgent("Unemployed", "Test Person 3", 3, 1000.00, "House3", "TakesTheBus");
		PersonAgent person4 = new PersonAgent("BankTeller", "Test Person 4", 3, 1000.00, "House4", "TakesTheBus");
		PersonAgent person5 = new PersonAgent("BankTeller", "Test Person 5", 3, 1000.00, "House5", "TakesTheBus");
		PersonAgent person6 = new PersonAgent("Market", "Test Person 6", 3, 1000.00, "House6", "TakesTheBus");
*/
		
/*
		//RYAN TEST CODE
		String a = "ShehRestaurant";
		String b = "House1";
		String name = "WAITER";
		Role role;
		role = new ShehWaiterNormalRole("ShehRestaurant");

		PersonAgent p = new PersonAgent(role, a , b, name);
		role.setPerson(p);
		
		String a1 = "ShehRestaurant";
		String b1 = "House2";
		String name1 = "WAITER2";
		Role role1;
		role1 = new ShehWaiterSharedRole("ShehRestaurant");

		PersonAgent p1 = new PersonAgent(role1, a1 , b1, name1);
		role1.setPerson(p1);
		
		String a2 = "ShehRestaurant";
		String b2 = "House2";
		String name2 = "COOK";
		Role role2;
		role2 = new ShehCookRole("ShehRestaurant");

		PersonAgent p2 = new PersonAgent(role2, a2 , b2, name2);
		role2.setPerson(p2);
*/
		
/*
 		//TAN RESTAURANT CODE
		String a = "TanRestaurant";
		String b = "House1";
		String name = "Test Person 1";
		Role role;
		role= new TanWaiterNormalRole("TanRestaurant");
		PersonAgent p = new PersonAgent(role, a , b, name);
		p.msgWakeUp();
		role.setPerson(p);
		p.startThread();
		
		String a1 = "TanRestaurant";
		String b1 = "House2";
		String name1 = "Test Person 2";
		Role role1;
		role1= new TanCookRole("TanRestaurant");
		PersonAgent p1 = new PersonAgent(role, a1 , b1, name1);
		p1.msgWakeUp();
		role.setPerson(p);
		p1.startThread();
	
		String a2 = "TanRestaurant";
		String b2 = "House3";
		String name2 = "Test Person 3";
		Role role2;
		role2 = new TanCustomerRole("TanRestaurant");
		PersonAgent p2 = new PersonAgent(role2, a2 , b2, name2);
		role2.setPerson(p2);
		p2.msgWakeUp();
		p2.startThread();
*/		
		
		String a = "TanRestaurant";
		String b = "House1";
		String name = "Ben Test Waiter";
		Role role;
		role= new TanWaiterSharedRole("TanRestaurant");
		PersonAgent p = new PersonAgent(role, a , b, name);
		p.msgWakeUp();
		role.setPerson(p);
		p.startThread();
		
		String a1 = "TanRestaurant";
		String b1 = "House2";
		String name1 = "Ben Test Cook";
		Role role1;
		role1= new TanCookRole("TanRestaurant");
		PersonAgent p1 = new PersonAgent(role1, a1 , b1, name1);
		p1.msgWakeUp();
		role1.setPerson(p1);
		p1.startThread();
	
		String a2 = "TanRestaurant";
		String b2 = "House3";
		String name2 = "Test Person 3 Ben";
		Role role2;
		role2 = new TanCustomerRole("TanRestaurant");
		PersonAgent p2 = new PersonAgent(role2, a2 , b2, name2);
		p2.msgWakeUp();
		role2.setPerson(p2);
		p2.startThread();
		
		
		bus = new BusAgent(1);
		busGui = new BusGui(bus,1); //agent, starting StopNumber
		bus.setGui(busGui);
		macroAnimationPanel.addGui(busGui);
		bus.startThread();
		
		
		bus2 = new BusAgent(2);
		busGui2 = new BusGui(bus2,2);
		bus2.setGui(busGui2);
		macroAnimationPanel.addGui(busGui2);
		bus2.startThread();
		
//		/*
//		trafficLight = new TrafficAgent();
//		trafficLight.startThread();
//		trafficLight1 = new TrafficAgent();
//		trafficLight1.startThread();
//		trafficLight2 = new TrafficAgent();
//		trafficLight2.startThread();
////		*/
		
//		String a = "TanRestaurant";
//		String b = "House1";
//		String name = "Test Person 1";
//		Role role;
//		role= new TanWaiterNormalRole("TanRestaurant");
//		PersonAgent p = new PersonAgent(role, a , b, name);
//		p.msgWakeUp();
//		role.setPerson(p);
//		p.startThread();
//		
//		String a1 = "HuangRestaurant";
//		String b1 = "House2";
//		String name1 = "Test Person 2";
//		Role role1;
//		role1= new TanCookRole("TanRestaurant");
//		PersonAgent p1 = new PersonAgent(role, a1 , b1, name1);
//		p1.msgWakeUp();
//		role.setPerson(p);
//		p1.startThread();
//	
//		String a2 = "TanRestaurant";
//		String b2 = "House3";
//		String name2 = "Test Person 3";
//		Role role2;
//		role2 = new TanCustomerRole("TanRestaurant");
//		PersonAgent p2 = new PersonAgent(role2, a2 , b2, name2);
//		role2.setPerson(p2);
//		p2.msgWakeUp();
//		p2.startThread();

		/**
		 End of Hard Code SuperNorm
		 */
//		/**
//		 * Start of Fresh SuperNorm = All agents wake up and do normal stuff.
//		 */
//		String a = "HuangRestaurant";
//		String b = "House1";
//		String name = "Test Person 1";
//		Role role;
//		if(rand.nextInt()%2 == 0) {
//			role = new StackWaiterSharedRole("HuangRestaurant");
//		}
//		else {
//			role = new StackWaiterNormalRole("HuangRestaurant");
//		}
//		PersonAgent p = new PersonAgent(role, a , b, name);
////		p.msgWakeUp();
//		role.setPerson(p);
//		p.startThread();
//	
//		String a2 = "HuangRestaurant";
//		String b2 = "House2";
//		String name2 = "Test Person 2";
//		Role role2;
//		if(rand.nextInt()%2 == 0) {
//			role2 = new StackWaiterSharedRole("HuangRestaurant");
//		}
//		else {
//			role2 = new StackWaiterNormalRole("HuangRestaurant");
//		}
//		PersonAgent p2 = new PersonAgent(role2, a2 , b2, name2);
//		role2.setPerson(p2);
////		p2.msgWakeUp();
//		//p2.startThread();
//
//		String a3 = "HuangRestaurant";
//		String b3 = "House3";
//		String name3 = "Test Person 3";
//		Role role3 = new StackCookRole("HuangRestaurant");
//		PersonAgent p3 = new PersonAgent(role3, a3 , b3, name3);
//		role3.setPerson(p3);
////		p3.msgWakeUp();
//		//p3.startThread();
//
//		String a4 = "Bank";
//		String b4 = "House4";
//		String name4 = "Test Person 4";
//		Role role4 = new BankTellerRole("Bank");
//		PersonAgent p4 = new PersonAgent(role4, a4 , b4, name4);
//		role4.setPerson(p4);
////		p4.msgWakeUp();
//		//p4.startThread();
//		
//		String a5 = "Bank";
//		String b5 = "House5";
//		String name5 = "Test Person 5";
//		Role role5 = new BankTellerRole("Bank");
//		PersonAgent p5 = new PersonAgent(role5, a5 , b5, name5);
//		role5.setPerson(p5);
////		p5.msgWakeUp();
//		//p5.startThread();
//        
//		MarketRole role6 = new MarketRole("Market");
//		Directory.sharedInstance().marketDirectory.get("Market").setWorker(role6);
//		
//		String a6 = "Market";
//		String b6 = "House6";
//		String name6 = "Test Person 6";
//		PersonAgent p6 = new PersonAgent(role6, a6 , b6, name6);
//		role6.setPerson(p6);
////		p6.msgWakeUp();
//		//p6.startThread();
//		
//		String a7 = "Bank";
//		String b7 = "House2";
//		String name7 = "Test Person 7";
//		Role role7 = new BankTellerRole("Bank");
//		PersonAgent p7 = new PersonAgent(role7, a7 , b7, name7);
//		role7.setPerson(p7);
////		p7.msgWakeUp();
//		//p7.startThread();
//		
//		bus = new BusAgent(1);
//		busGui = new BusGui(bus,1); //agent, starting StopNumber
//		bus.setGui(busGui);
//		macroAnimationPanel.addGui(busGui);
//		bus.startThread();
//		
//		
//		bus2 = new BusAgent(2);
//		busGui2 = new BusGui(bus2,2);
//		bus2.setGui(busGui2);
//		macroAnimationPanel.addGui(busGui2);
//		bus2.startThread();
//		
////		Clock.sharedInstance().notifyTimeToWakeUp();
	}
	
	public void displayBuildingPanel( BuildingPanel buildingPanel ) { //How is this tied in with the Micro Panel?
		cardLayout.show( buildingPanels, buildingPanel.getName());
	}
	
	public MacroAnimationPanel getMacroAnimationPanel() {
		return macroAnimationPanel;
	}
	
	public BusAgent getBus() {
		return bus;
	}
	
	public TrafficAgent getTrafficLight1(){
		return trafficLight1;
	}
	
	public TrafficAgent getTrafficLight2(){
		return trafficLight2;
	}

	public void setUniqueBuildingPanel(String name) {
		for(Restaurant restaurant : Directory.sharedInstance().getRestaurants()) {
			if(restaurant.getName().contains(name)) {
				if(tabbedPane.getTabCount() == 3) {
					tabbedPane.remove(2);
				}
				CurrentBuildingPanel restPanel = new CurrentBuildingPanel(restaurant);
				restaurant.setInfoPanel(restPanel);
				tabbedPane.addTab("Current Building", restPanel);
			}
		}
		if(name.equals("Market")) {
			if(tabbedPane.getTabCount() == 3) {
				tabbedPane.remove(2);
			}
			CurrentBuildingPanel restPanel = new CurrentBuildingPanel(Directory.sharedInstance().getMarkets().get(0));
			Directory.sharedInstance().getMarkets().get(0).setInfoPanel(restPanel);
			tabbedPane.addTab("Current Building", restPanel);
		}
		else if(name.equals("Market2")) {
			if(tabbedPane.getTabCount() == 3) {
				tabbedPane.remove(2);
			}
			CurrentBuildingPanel restPanel = new CurrentBuildingPanel(Directory.sharedInstance().getMarkets().get(1));
			Directory.sharedInstance().getMarkets().get(1).setInfoPanel(restPanel);
			tabbedPane.addTab("Current Building", restPanel);
		}
		else if(name.equals("Bank")) {
			if(tabbedPane.getTabCount() == 3) {
				tabbedPane.remove(2);
			}
			CurrentBuildingPanel restPanel = new CurrentBuildingPanel(Directory.sharedInstance().getBanks().get(0));
			Directory.sharedInstance().getBanks().get(0).setInfoPanel(restPanel);
			tabbedPane.addTab("Current Building", restPanel);
		}
		else if(name.equals("Bank2")) {
			if(tabbedPane.getTabCount() == 3) {
				tabbedPane.remove(2);
			}
			CurrentBuildingPanel restPanel = new CurrentBuildingPanel(Directory.sharedInstance().getBanks().get(1));
			Directory.sharedInstance().getBanks().get(1).setInfoPanel(restPanel);
			tabbedPane.addTab("Current Building", restPanel);
		}
	}

	public void setTime(int day, int hour) {
		lblTime.setText(String.valueOf(day) + ":" + String.valueOf(hour));
		
	}
}
