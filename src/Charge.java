import java.util.Vector;

/**
 * 
 */

/**
 * @author Jay Mohile, Oxeolo
 *
 */
public class Charge {
	static Integer historyLength;
	Integer id;
	static int NEXT_ID = 1;
	Neuron currentLocation;
	Vector<Neuron> history;
	boolean hasArrived = false; // has the charge ARRIVED, and passed by the concurrency buffer?
	long startTime;

	public Charge(Neuron start) {
		// TODO Auto-generated constructor stub
		currentLocation = start;
		history = new Vector<Neuron>();
		startTime = System.currentTimeMillis();
	}

	public Neuron getCurrentLocation() {
		return currentLocation;
	}

	public void arrive(boolean hardTrim) {
		currentLocation.hasCharge = true;
		currentLocation.updateBranches(history); // send history to current neuron to
													// update
		if (hardTrim) {
			currentLocation.hardTrim();
		}
		if (history.size() == Neuros.CHARGE_HISTORY_MAX_LENGTH) { // if the history
																	// has reached
																	// max length
			history.remove(0); // remove the oldest element
		}
		history.addElement(currentLocation); // add the current location as the most
												// recent element

	}

	public void removeCharge() {
		Neuros.closeNetwork(startTime);
	}

	public Neuron move() {
		Branch destination = currentLocation.getProbableDestinationBranch();
		if (destination != null) {
			removeCharge();
			return null;
		} else {
			return move(destination.neuron);
		}
	}

	public Neuron move(Neuron neuron) {
		currentLocation.hasCharge = false;
		currentLocation = neuron; // at this point, THE CHARGE HAS // FINISHED
									// TRAVELLING!!!!!!
		if (currentLocation == null) {
			removeCharge();
		}
		return neuron;
	}
}
