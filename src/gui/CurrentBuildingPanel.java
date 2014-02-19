package gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JSlider;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import market.Market;
import bank.Bank;
import restaurant.Restaurant;

@SuppressWarnings("serial")
public class CurrentBuildingPanel extends JPanel {
	String type;
	Restaurant restaurant;
	Bank bank;
	Market market;
	private JLabel steakNumber;
	private JLabel chickenNumber;
	private JLabel pizzaNumber;
	private JLabel saladNumber;
	private JSlider saladSlider;
	private JSlider pizzaSlider;
	private JSlider chickenSlider;
	private JSlider steakSlider;
	private JLabel lblRegister;
	private JLabel lblCar;
	private JLabel carNumber;
	private JSlider carSlider;
	
	public CurrentBuildingPanel(Object building) {
		super();
		
		if(building instanceof Restaurant) {
			restaurant = (Restaurant) building;
			initializeRestaurant();
		}
		else if(building instanceof Market) {
			market = (Market) building;
			initializeMarket();
		}
		else if(building instanceof Bank) {
			bank = (Bank) building;
			initializeBank();
		}
		
	}

	private void initializeBank() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblName_2 = new JLabel("Name:");
		if(bank != null) {
			lblName_2.setText("Name: " +  bank.getName());
		}
		springLayout.putConstraint(SpringLayout.NORTH, lblName_2, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName_2, 10, SpringLayout.WEST, this);
		add(lblName_2);
		
		final JButton btnCloseBuildingBank = new JButton();
		if(bank != null) {
			if(bank.isOpen()) {
				btnCloseBuildingBank.setText("Close Building");
			}
			else {
				btnCloseBuildingBank.setText("Open Building");
			}
		}
		btnCloseBuildingBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(bank != null) {
					if(bank.isOpen()) {
						bank.setOpen(false);
						btnCloseBuildingBank.setText("Open Building");
					}
					else {
						bank.setOpen(true);
						btnCloseBuildingBank.setText("Close Building");
					}
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnCloseBuildingBank, 6, SpringLayout.SOUTH, lblName_2);
		springLayout.putConstraint(SpringLayout.WEST, btnCloseBuildingBank, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, btnCloseBuildingBank, 249, SpringLayout.WEST, this);
		add(btnCloseBuildingBank);
	}

	private void initializeMarket() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblName_1 = new JLabel("Name: ");
		if(market != null) {
			lblName_1.setText("Name: " + market.getName());
		}
		springLayout.putConstraint(SpringLayout.NORTH, lblName_1, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName_1, 10, SpringLayout.WEST, this);
		add(lblName_1);
		
		JLabel lblInventory_1 = new JLabel("Inventory:");
		springLayout.putConstraint(SpringLayout.NORTH, lblInventory_1, 6, SpringLayout.SOUTH, lblName_1);
		springLayout.putConstraint(SpringLayout.WEST, lblInventory_1, 10, SpringLayout.WEST, this);
		add(lblInventory_1);
		
		JLabel lblSteak_1 = new JLabel("Steak:");
		springLayout.putConstraint(SpringLayout.NORTH, lblSteak_1, 6, SpringLayout.SOUTH, lblInventory_1);
		springLayout.putConstraint(SpringLayout.WEST, lblSteak_1, 0, SpringLayout.WEST, lblName_1);
		add(lblSteak_1);
		
		steakNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, steakNumber, 0, SpringLayout.NORTH, lblSteak_1);
		springLayout.putConstraint(SpringLayout.EAST, steakNumber, -10, SpringLayout.EAST, this);
		add(steakNumber);
		
		int beginningSteakMin = 0;
		int beginningSteakMax = 100;
		int beginningSteakStart = 50;
		steakSlider = new JSlider(beginningSteakMin, beginningSteakMax, beginningSteakStart);
		steakSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(market != null) {
					market.msgChangeFoodInventory("Steak", steakSlider.getValue());
					steakNumber.setText(String.valueOf(steakSlider.getValue()));
				}
			}
		});
		steakSlider.setMajorTickSpacing(5);
		steakSlider.setPaintTicks(true);
		
		springLayout.putConstraint(SpringLayout.NORTH, steakSlider, 6, SpringLayout.SOUTH, lblSteak_1);
		springLayout.putConstraint(SpringLayout.WEST, steakSlider, 0, SpringLayout.WEST, lblName_1);
		springLayout.putConstraint(SpringLayout.EAST, steakSlider, 0, SpringLayout.EAST, steakNumber);
		add(steakSlider);
		
		JLabel lblChicken_1 = new JLabel("Chicken:");
		springLayout.putConstraint(SpringLayout.NORTH, lblChicken_1, 6, SpringLayout.SOUTH, steakSlider);
		springLayout.putConstraint(SpringLayout.WEST, lblChicken_1, 0, SpringLayout.WEST, lblName_1);
		add(lblChicken_1);
		
		chickenNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, chickenNumber, 6, SpringLayout.SOUTH, steakSlider);
		springLayout.putConstraint(SpringLayout.EAST, chickenNumber, 0, SpringLayout.EAST, steakNumber);
		add(chickenNumber);
		
		int beginningChickenMin = 0;
		int beginningChickenMax = 100;
		int beginningChickenStart = 50;
		chickenSlider = new JSlider(beginningChickenMin, beginningChickenMax, beginningChickenStart);
		chickenSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(market != null) {
					market.msgChangeFoodInventory("Chicken", chickenSlider.getValue());
					chickenNumber.setText(String.valueOf(chickenSlider.getValue()));
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, chickenSlider, 6, SpringLayout.SOUTH, lblChicken_1);
		springLayout.putConstraint(SpringLayout.WEST, chickenSlider, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, chickenSlider, 249, SpringLayout.WEST, this);
		add(chickenSlider);
		
		chickenSlider.setMajorTickSpacing(5);
		chickenSlider.setPaintTicks(true);
		
		JLabel lblPizza_1 = new JLabel("Pizza:");
		springLayout.putConstraint(SpringLayout.NORTH, lblPizza_1, 6, SpringLayout.SOUTH, chickenSlider);
		springLayout.putConstraint(SpringLayout.WEST, lblPizza_1, 0, SpringLayout.WEST, lblName_1);
		add(lblPizza_1);
		
		int beginningPizzaMin = 0;
		int beginningPizzaMax = 100;
		int beginningPizzaStart = 50;
		pizzaSlider = new JSlider(beginningPizzaMin, beginningPizzaMax, beginningPizzaStart);
		pizzaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(market != null) {
					market.msgChangeFoodInventory("Pizza", pizzaSlider.getValue());
					pizzaNumber.setText(String.valueOf(pizzaSlider.getValue()));
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, pizzaSlider, 6, SpringLayout.SOUTH, lblPizza_1);
		springLayout.putConstraint(SpringLayout.WEST, pizzaSlider, 0, SpringLayout.WEST, lblName_1);
		springLayout.putConstraint(SpringLayout.EAST, pizzaSlider, 0, SpringLayout.EAST, steakNumber);
		add(pizzaSlider);
		
		pizzaSlider.setMajorTickSpacing(5);
		pizzaSlider.setPaintTicks(true);
		
		pizzaNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.SOUTH, pizzaNumber, 0, SpringLayout.SOUTH, lblPizza_1);
		springLayout.putConstraint(SpringLayout.EAST, pizzaNumber, 0, SpringLayout.EAST, steakNumber);
		add(pizzaNumber);

		JLabel lblSalad_1 = new JLabel("Salad:");
		springLayout.putConstraint(SpringLayout.NORTH, lblSalad_1, 6, SpringLayout.SOUTH, pizzaSlider);
		springLayout.putConstraint(SpringLayout.WEST, lblSalad_1, 0, SpringLayout.WEST, lblName_1);
		add(lblSalad_1);
		
		saladNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, saladNumber, 6, SpringLayout.SOUTH, pizzaSlider);
		springLayout.putConstraint(SpringLayout.EAST, saladNumber, 0, SpringLayout.EAST, steakNumber);
		add(saladNumber);
		
		int beginningSaladMin = 0;
		int beginningSaladMax = 100;
		int beginningSaladStart = 50;
		saladSlider = new JSlider(beginningSaladMin, beginningSaladMax, beginningSaladStart);
		saladSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(market != null) {
					market.msgChangeFoodInventory("Salad", saladSlider.getValue());
					saladNumber.setText(String.valueOf(saladSlider.getValue()));
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, saladSlider, 6, SpringLayout.SOUTH, lblSalad_1);
		springLayout.putConstraint(SpringLayout.WEST, saladSlider, 0, SpringLayout.WEST, lblName_1);
		springLayout.putConstraint(SpringLayout.EAST, saladSlider, 0, SpringLayout.EAST, steakNumber);
		add(saladSlider);
		
		saladSlider.setMajorTickSpacing(5);
		saladSlider.setPaintTicks(true);
		
		lblCar = new JLabel("Car:");
		springLayout.putConstraint(SpringLayout.NORTH, lblCar, 6, SpringLayout.SOUTH, saladSlider);
		springLayout.putConstraint(SpringLayout.WEST, lblCar, 0, SpringLayout.WEST, lblName_1);
		add(lblCar);
		
		carNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.SOUTH, carNumber, 0, SpringLayout.SOUTH, lblCar);
		springLayout.putConstraint(SpringLayout.EAST, carNumber, 0, SpringLayout.EAST, steakNumber);
		add(carNumber);
		
		int beginningCarMin = 0;
		int beginningCarMax = 100;
		int beginningCarStart = 50;
		carSlider = new JSlider(beginningCarMin, beginningCarMax, beginningCarStart);
		carSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				market.msgChangeFoodInventory("Car", carSlider.getValue());
				carNumber.setText(String.valueOf(carSlider.getValue()));
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, carSlider, 6, SpringLayout.SOUTH, lblCar);
		springLayout.putConstraint(SpringLayout.WEST, carSlider, 0, SpringLayout.WEST, lblName_1);
		springLayout.putConstraint(SpringLayout.EAST, carSlider, 0, SpringLayout.EAST, steakNumber);
		add(carSlider);
		
		carSlider.setMajorTickSpacing(5);
		carSlider.setPaintTicks(true);
		
		final JButton btnCloseBuildingMarket = new JButton();
		if(market != null) {
			if(market.isOpen()) {
				btnCloseBuildingMarket.setText("Close Building");
			}
			else {
				btnCloseBuildingMarket.setText("Open Building");
			}
		}
		btnCloseBuildingMarket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(market != null && btnCloseBuildingMarket.getText().contains("Close")) {
					market.setClosed();
					btnCloseBuildingMarket.setText("Open Building");
				}
				else if(market != null) {
					market.setOpen();
					btnCloseBuildingMarket.setText("Close Building");
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnCloseBuildingMarket, 6, SpringLayout.SOUTH, carSlider);
		springLayout.putConstraint(SpringLayout.WEST, btnCloseBuildingMarket, 0, SpringLayout.WEST, lblName_1);
		springLayout.putConstraint(SpringLayout.EAST, btnCloseBuildingMarket, 0, SpringLayout.EAST, steakNumber);
		add(btnCloseBuildingMarket);
		
	}

	private void initializeRestaurant() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		final JButton btnCloseBuilding = new JButton();
		if(restaurant != null) {
			if(restaurant.isOpen()) {
				btnCloseBuilding.setText("Close Building");
			}
			else {
				btnCloseBuilding.setText("Open Building");
			}
		}
		springLayout.putConstraint(SpringLayout.WEST, btnCloseBuilding, 10, SpringLayout.WEST, this);
		btnCloseBuilding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(restaurant != null && btnCloseBuilding.getText().contains("Close")) {
					restaurant.msgSetClosed();
					btnCloseBuilding.setText("Open Building");
				}
				else if(restaurant != null) {
					restaurant.msgSetOpen();
					btnCloseBuilding.setText("Close Building");
				}
			}
		});
		add(btnCloseBuilding);
		
		JLabel lblInventory = new JLabel("Inventory:");
		springLayout.putConstraint(SpringLayout.WEST, lblInventory, 10, SpringLayout.WEST, this);
		add(lblInventory);
		
		JLabel lblSteak = new JLabel("Steak:");
		springLayout.putConstraint(SpringLayout.WEST, lblSteak, 10, SpringLayout.WEST, this);
		add(lblSteak);
		
		JLabel lblChicken = new JLabel("Chicken:");
		springLayout.putConstraint(SpringLayout.WEST, lblChicken, 10, SpringLayout.WEST, this);
		add(lblChicken);
		
		JLabel lblPizza = new JLabel("Pizza:");
		springLayout.putConstraint(SpringLayout.WEST, lblPizza, 10, SpringLayout.WEST, this);
		add(lblPizza);
		
		JLabel lblSalad = new JLabel("Salad:");
		springLayout.putConstraint(SpringLayout.WEST, lblSalad, 10, SpringLayout.WEST, this);
		add(lblSalad);
		
		int beginningSteakMin = 0;
		int beginningSteakMax = 100;
		int beginningSteakStart = 50;
		steakSlider = new JSlider(beginningSteakMin, beginningSteakMax, beginningSteakStart);
		springLayout.putConstraint(SpringLayout.EAST, btnCloseBuilding, 0, SpringLayout.EAST, steakSlider);
		springLayout.putConstraint(SpringLayout.WEST, steakSlider, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, steakSlider, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSteak, -6, SpringLayout.NORTH, steakSlider);
		springLayout.putConstraint(SpringLayout.SOUTH, steakSlider, -6, SpringLayout.NORTH, lblChicken);
		steakSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(restaurant != null) {
					restaurant.msgChangeFoodInventory("Steak", steakSlider.getValue());
					steakNumber.setText(String.valueOf(steakSlider.getValue()));
				}
			}
		});
		add(steakSlider);
		
		steakSlider.setMajorTickSpacing(5);
		steakSlider.setPaintTicks(true);
		
		int beginningChickenMin = 0;
		int beginningChickenMax = 100;
		int beginningChickenStart = 50;
		chickenSlider = new JSlider(beginningChickenMin, beginningChickenMax, beginningChickenStart);
		springLayout.putConstraint(SpringLayout.WEST, chickenSlider, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, chickenSlider, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblChicken, -6, SpringLayout.NORTH, chickenSlider);
		springLayout.putConstraint(SpringLayout.SOUTH, chickenSlider, -6, SpringLayout.NORTH, lblPizza);
		chickenSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(restaurant != null) {
					restaurant.msgChangeFoodInventory("Chicken", chickenSlider.getValue());
					chickenNumber.setText(String.valueOf(chickenSlider.getValue()));
				}
			}
		});
		add(chickenSlider);
		
		chickenSlider.setMajorTickSpacing(5);
		chickenSlider.setPaintTicks(true);
		
		int beginningPizzaMin = 0;
		int beginningPizzaMax = 100;
		int beginningPizzaStart = 50;
		pizzaSlider = new JSlider(beginningPizzaMin, beginningPizzaMax, beginningPizzaStart);
		springLayout.putConstraint(SpringLayout.WEST, pizzaSlider, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, pizzaSlider, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblPizza, -6, SpringLayout.NORTH, pizzaSlider);
		springLayout.putConstraint(SpringLayout.SOUTH, pizzaSlider, -6, SpringLayout.NORTH, lblSalad);
		pizzaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(restaurant != null) {
					restaurant.msgChangeFoodInventory("Pizza", pizzaSlider.getValue());
					pizzaNumber.setText(String.valueOf(pizzaSlider.getValue()));
				}
			}
		});
		add(pizzaSlider);
		
		pizzaSlider.setMajorTickSpacing(5);
		pizzaSlider.setPaintTicks(true);
		
		int beginningSaladMin = 0;
		int beginningSaladMax = 100;
		int beginningSaladStart = 50;
		saladSlider = new JSlider(beginningSaladMin, beginningSaladMax, beginningSaladStart);
		springLayout.putConstraint(SpringLayout.NORTH, btnCloseBuilding, 16, SpringLayout.SOUTH, saladSlider);
		springLayout.putConstraint(SpringLayout.WEST, saladSlider, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, saladSlider, -10, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, saladSlider, 302, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSalad, -6, SpringLayout.NORTH, saladSlider);
		saladSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(restaurant != null) {
					restaurant.msgChangeFoodInventory("Salad", saladSlider.getValue());
					saladNumber.setText(String.valueOf(saladSlider.getValue()));
				}
			}
		});
		add(saladSlider);
		
		saladSlider.setMajorTickSpacing(5);
		saladSlider.setPaintTicks(true);
		
		saladNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, saladNumber, 0, SpringLayout.NORTH, lblSalad);
		springLayout.putConstraint(SpringLayout.EAST, saladNumber, -10, SpringLayout.EAST, this);
		add(saladNumber);
		
		pizzaNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, pizzaNumber, 0, SpringLayout.NORTH, lblPizza);
		springLayout.putConstraint(SpringLayout.EAST, pizzaNumber, -10, SpringLayout.EAST, this);
		add(pizzaNumber);
		
		chickenNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, chickenNumber, 0, SpringLayout.NORTH, lblChicken);
		springLayout.putConstraint(SpringLayout.EAST, chickenNumber, -10, SpringLayout.EAST, this);
		add(chickenNumber);
		
		steakNumber = new JLabel("50");
		springLayout.putConstraint(SpringLayout.NORTH, steakNumber, 0, SpringLayout.NORTH, lblSteak);
		springLayout.putConstraint(SpringLayout.EAST, steakNumber, -10, SpringLayout.EAST, this);
		add(steakNumber);
		
		JLabel lblName = new JLabel("Name: " + restaurant.getName());
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, this);
		add(lblName);
		
		lblRegister = new JLabel("Register: " + restaurant.getTill());
		springLayout.putConstraint(SpringLayout.WEST, lblRegister, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.NORTH, lblInventory, 6, SpringLayout.SOUTH, lblRegister);
		springLayout.putConstraint(SpringLayout.NORTH, lblRegister, 6, SpringLayout.SOUTH, lblName);
		add(lblRegister);
		
	}
	
	public void msgChangeSteakInventory(int steakInventory) {
		steakSlider.setValue(steakInventory);
		steakNumber.setText(String.valueOf(steakInventory));
	}
	
	public void msgChangeChickenInventory(int chickenInventory) {
		chickenSlider.setValue(chickenInventory);
		chickenNumber.setText(String.valueOf(chickenInventory));
	}

	public void msgChangePizzaInventory(int pizzaInventory) {
		pizzaSlider.setValue(pizzaInventory);
		pizzaNumber.setText(String.valueOf(pizzaInventory));
	}

	public void msgChangeSaladInventory(int saladInventory) {
		saladSlider.setValue(saladInventory);
		saladNumber.setText(String.valueOf(saladInventory));
	}
	
	public void msgChangeCarInventory(int quantity) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgChangeTillInformation(double till) {
		lblRegister.setText("Register: " + String.valueOf(till));
	}
}
