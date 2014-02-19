package home.interfaces;
import city.PersonAgent;
import city.interfaces.Person;
import home.*;

public interface Landlord {
	public abstract void msgHereIsRent(Person person, double money);
}