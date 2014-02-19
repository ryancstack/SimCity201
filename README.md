team02


SimCity201 Project Repository for CS 201 students

How TO play/Grade(?):
 * Run Program
 * Use Create a Person to create people to interact with the city.
 * Poor people (0 money) will take out loans first
 * Rich people will want to deposit
 * Everyone's starting food amount is randomized
 * Read below for limitations and known bugs.

Running Normative Scenario
 * Create a person works. Select None for occupation to create an unemployed person to see how he interacts
 * Can only instantiate people who take the bus as their transportation method. 
 * Initial funds sets their starting money. 
 * Aggressiveness sets their personality for how long they work and sleep.
 * When selecting occupation, market 2 seller does not work. 
 * Market Role1 and 2 have already set up person as its job. Do not reassign them in Create a person.
 * Only one restaurant.
 * Multiple people live in homes.
 * All guis for restaurants (other than functional Ryan Stack's designated restaurant) is using market GUI.
 * Potential inconsistencies w/ different computers 
 * (Ex. Busses sometimes stop stopping after running a long time on Mac computers)

Missing functionality in V1
 * Only one market running.
 * No intersections.
 * No cars.
 * No collision detection.
 * No A*
 * No apartments.
 * Populate City does not work.
 * No animation for markets to deliver.
 * No walking as sole form of transportation.
 * No bank robberies.
 * No weekends.
 
 Missing functionality in V2
 * Buying/Driving Car Scenario
 * Car Collisions
 * Richard Phillip's complete restaurant integration
 * Vehicles stop for pedestrians

Ryan Stack's Contribution:
 * Implemented restaurant with added shared data, market interactions, and payment from cashier
 * Implemented front-end GUI, created window and application, and actionListeners
 * Created graphics for city, restaurant, bank, and some agents (with Ryan Sheh)
 * Helped implement bank, bankGUI, and roles for bank
 * Created an XML reader to read in scripts for creating scenarios and populating the city (with Alex Huang)
 * Created initial setup for project with interfaces and stubs from all designs
 * Created initial global clock and role class (iterations followed from other team members)
 * Added animation speed changer to GUI
 * Added clock display and clock changer to GUI
 * Edited current buildings to take in opening and closing functionality
 * Created custom building panels for every building to include changing inventory functionality and closing functionality
 * Added log filter to clean up console and display proper log messages (2 clicks over from create person tab) 
 * Implemented more script files to populate city with 50 people

Richard Phillip's Contribution:
 * Implementation for bank roles
 * Implementation for bank GUI
 * Unit testing for bank roles for norm/some non-norm scenarios
 * Unit testing for bank GUI for norm/some non-norm scenarios
 * Missing functionality for V2: no robber interaction, no database (EC)
 * Implemented Second Bank
 * Implemented Bank Robbery
 * Implemented LandLord Role w/ Ryan 

Reid Nakamura's Contribution:
 * Implemented Market Roles
 * Implemented Market GUI
 * Implement Market Role Unit Test
 * Helped to debug Transportation GUI
 * Resolved issue with Ryan Sheh to set up CardLayout of building animation panels
 * Fully implemented personal restaurant w/ normal/shared waiter/cook interactions
 * Updated Market Role Agent to Handle Deliveries
 * Worked with other team members to integrate market interactions into restaurant
 * Implemented time (days/hours/weekends)

Ryan Sheh's Contribution
 * Main Window GUI (w/ help from Ryan Stack)
 * Bank GUI (w/ Richard & Ryan Stack )
 * Market GUI (w/ Reid)
 * Home GUI (w/ Alex)
 * Implemented CardLayout (w/ help from Reid)
 * Implemented Directory Class for References (w/ help from Alex)
 * Unit Testing Bank Roles (w/ Richard)
 * Helped Implement Bus Agent & GUI (w/ Ben Tan)
 * Implemented personal restaurant w/ normal/shared waiter/cook interactions
 * Traffic Agent Design Doc
 * Apartment implementation/GUI
 * LandLord GUI
 * Updated Sim City MacroAnimation Panel to incorporate intersections/signs/nighttime.
 * Helped Nakamura's restaurant GUI
 * Icon/Images have tracking information for references
 * Implemented LandLord Role w/ Richard
 
 Ben Tan's Contribution
 * Design of Car Agent & interaction with Transportation Role
 * Design and implementation of Bus Agent & GUI (w/ Ryan Sheh)
 * Unit testing for Bus Agent scenarios
 * Helped update Directory & Bus Helpers classes and supporting files (w/ Ryan Sheh & Alex Huang)
 * Helped hook up Transportation Role with Bus Agent (w/ Alex Huang)
 * Missing functionality: Cars don't stop for pedestrians
 * Implemented restaurant
 * Traffic Agent
 * Car Agent
 * Implemented vehicles through intersections w/ Alex
 * Helped implement Transportation GUI

Alex Huang's Contribution:
 * Designed and implemented Person Agent AI and PersonGUI
 * Integrated all roles and gui animations with Person Agent(contributions from respective implementers).
 * Debugged all Bank roles and fixed interactions within it as they were broken initially during integration.
 * Fixed Bank Role GUI's for interactions (with help from Stack, Sheh, Phillips).
 * Implemented Directory class and its data(contribution from whole team)
 * Polish superNorm test with help from Reid and Sheh
 * Implemented Transportation Role and TransportationRoleGUI
 * Missing Complete Unit Tests for PersonAgent and PersonGUI because of sharedInstance issues in Directory. 
 * Completed TransportationGUI upgrades and sidewalk pathing for all people(With help from Tan)
 * Integrated restaurant into city with a few bugs but for the most part functioning
 * Implemented conveyer loop for walking
 * Missing non norms for collision cases in city
 * Implemented workDetails and AI logic for workers to leave and start work


Pushing back this req for V2 delivery. 
 * Implemented personal restaurant w/ normal/shared waiter/cook interactions
 * Transportation Role (Walking) GUI w/ Ben
