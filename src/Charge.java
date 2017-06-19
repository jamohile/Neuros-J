import java.util.Vector;

public class Charge {
	//The maximum length of any charge's history
	static Integer historyLength;
	//the unique id posessed by this charge
	Integer id;
	//The next id which will be aasigned to an instance
	static int NEXT_ID = 1;
	//the current location of this charge
	Neuron currentLocation;
	//A list of the past locations of this charge
	Vector<Neuron> history;
	//True if the charge has arrived, and is not in transit
	boolean hasArrived = false;
	//The time of this charge's creation
	long startTime;

	public Charge(Neuron start) {
		// Instantiate charge at root neuron
		currentLocation = start;
		history = new Vector<Neuron>();
		startTime = System.currentTimeMillis();
	}
	//getter for current location
	public Neuron getCurrentLocation() {
		return currentLocation;
	}
	//all administrative code run whenever the charge arrives at a neuron
	public void arrive(boolean hardTrim) {
		currentLocation.hasCharge = true;
		//Send the charge's past locations to the current location
		currentLocation.updateBranches(history);
		//run a hard trim if desired by the runtime
		if (hardTrim) {
			currentLocation.hardTrim();
		}
		//If the history is full, remove the oldest element
		if (history.size() == Neuros.CHARGE_HISTORY_MAX_LENGTH) {
			history.remove(0);
		}
		//Add the current location to the history buffer
		history.addElement(currentLocation);

	}
	//close the network, since the charge has been removed
	public void removeCharge() {
		Neuros.closeNetwork(startTime);
	}
	//move to a new neuron, after calculating where to go
	public Neuron move() {
		Branch destination = currentLocation.getProbableDestinationBranch();
		if (destination != null) {
			removeCharge();
			return null;
		} else {
			return move(destination.neuron);
		}
	}
	//Move to an arbitrary neuron
	public Neuron move(Neuron neuron) {
		//Remove reference of this charge from current location
		currentLocation.hasCharge = false;
		//at this point, the charge has officially moved
		currentLocation = neuron; 
		if (currentLocation == null) {
			removeCharge();
		}
		return neuron;
	}
}
