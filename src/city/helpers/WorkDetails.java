package city.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import agent.Constants;
import city.interfaces.RoleInterface;


public class WorkDetails {
	public RoleInterface workRole;
	public String workLocation;
	public int workStartHour;
	public int workEndHour;
	public List<Integer> offDays = Collections.synchronizedList(new ArrayList<Integer>());
	public WorkDetails(RoleInterface job, String location) {
		this.workRole = job;
		this.workLocation = location;
		if (job.getClass().getName().contains("employ")) {
			this.workStartHour = 10;
			this.workEndHour = 24;
		}
		else if (location.contains("Stack")) {
			this.workStartHour = 8;
			this.workEndHour = 20;
			offDays.add(Constants.SUNDAY);
			offDays.add(Constants.SATURDAY);
		}
		else if (location.contains("Huang")) {
			this.workStartHour = 8;
			this.workEndHour = 20;
			offDays.add(Constants.SUNDAY);
			offDays.add(Constants.SATURDAY);
		}
		else if (location.contains("Nakamura")) {
			this.workStartHour = 8;
			this.workEndHour = 24;
			offDays.add(Constants.SUNDAY);
			offDays.add(Constants.SATURDAY);
		}
		else if (location.contains("Sheh")) {
			this.workStartHour = 9;
			this.workEndHour = 21;
			offDays.add(Constants.SUNDAY);
			offDays.add(Constants.SATURDAY);
		}
		else if (location.contains("Bank")) {
			this.workStartHour = 9;
			this.workEndHour = 18;
			offDays.add(Constants.SUNDAY);
			offDays.add(Constants.SATURDAY);
		}
		else if (location.equals("Market")) {
			this.workStartHour = 7;
			this.workEndHour = 24;
		}
		else if (location.equals("Market2")) {
			this.workStartHour = 7;
			this.workEndHour = 24;
			offDays.add(Constants.SATURDAY);
			offDays.add(Constants.SUNDAY);
		}
		else if (job.getClass().getName().contains("Lord")) {
			this.workStartHour = 12;
			this.workEndHour = 18;
		}
	}
};