package trace;

/**
 * These enums represent tags that group alerts together.  <br><br>
 * 
 * This is a separate idea from the {@link AlertLevel}.
 * A tag would group all messages from a similar source.  Examples could be: BANK_TELLER, RESTAURANT_ONE_WAITER,
 * or PERSON.  This way, the trace panel can sort through and identify all of the alerts generated in a specific group.
 * The trace panel then uses this information to decide what to display, which can be toggled.  You could have all of
 * the bank tellers be tagged as a "BANK_TELLER" group so you could turn messages from tellers on and off.
 * 
 * @author Keith DeRuiter
 *
 */
public enum AlertTag {
	/**
	 * This level could be used to print specific waiter information.
	 */
	WAITER,
	
	/**
	 * This level could be used to print specific cook information.
	 */
	COOK,
	
	/**
	 * This level could be used to print specific restaurant customer information.
	 */
	RESTAURANTCUSTOMER,
	
	/**
	 * This level could be used to print specific cashier information.
	 */
	CASHIER,
	
	/**
	 * This level could be used to print specific host information.
	 */
	HOST,
	
	/**
	 * This level could be used to print specific market worker information.
	 */
	MARKETWORKER,
	
	/**
	 * This level could be used to print specific market customer information.
	 */
	MARKETCUSTOMER,
	
	/**
	 * This level could be used to print specific cook information.
	 */
	BANKTELLER,
	
	/**
	 * This level could be used to print specific cook information.
	 */
	BANKMANAGER,
	
	/**
	 * This level could be used to print specific cook information.
	 */
	BANKCUSTOMER,
	
	/**
	 * This level could be used to print specific person information.
	 */
	PERSON,
	
	/**
	 * This level could be used to print specific cook information.
	 */
	LANDLORD
}
