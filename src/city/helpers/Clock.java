package city.helpers;

import gui.SimCityGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import agent.Constants;
import city.PersonAgent;

public class Clock implements ActionListener{
	public static final Clock sharedInstance = new Clock();
	private final int DELAY = 15000;
	int hour;
	int day;
	Timer timer;
	SimCityGui gui;
	
	private Clock() {
		hour = 5;
		day = 1;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public static Clock sharedInstance() {
		return sharedInstance;
	}
	
	public void setDelay(int delay) {
		timer.setDelay(delay);		
	}
	
	public boolean isDay() {
		return hour > 6 && hour < 18;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		updateTime();
	}
	
	private void updateTime() {
		if(hour == 24) {
			hour = 1;
			
			if(day == 7)
				day = 1;
			else
				day++;
		}
		
		else
			hour++;
		
		
		for(PersonAgent person : Directory.sharedInstance().getPeople()) {
			person.msgCheckTime(hour, day);
		}
		gui.setTime(day, hour);
	}

	public void incrementHour() {
		updateTime();
	}
	public void incrementDay() {
		if(day == 7)
			day = 1;
		else
			day++;
		for(PersonAgent person : Directory.sharedInstance().getPeople()) {
			person.msgCheckTime(hour, day);
		}
		gui.setTime(day, hour);
	}
	
	public void setGui(SimCityGui gui) {
		this.gui = gui;
	}

	public int getHour() {
		return hour;
	}
	public int getDay() {
		return day;
	}

	
}
