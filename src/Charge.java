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
	Neuron currentLocation;
	Vector<Neuron> history;

	public Charge(Neuron start) {
		// TODO Auto-generated constructor stub
		currentLocation = start;
		history = new Vector<Neuron>();
	}
	public Neuron getCurrentLocation() {
		return currentLocation;
	}
	public void arrive() {
		currentLocation.updateBranches(history); // send history to current neuron to
													// update
		if (history.size() == Neuros.CHARGE_HISTORY_MAX_LENGTH) { // if the history
																	// has reached
																	// max length
			history.remove(0); // remove the oldest element
		}
		history.addElement(currentLocation); // add the current location as the most
												// recent element

	}
	public Neuron move() {
		Branch destination = currentLocation.getProbableDestinationBranch();
		return move(destination.neuron);
	}
	public Neuron move(Neuron neuron) {

		currentLocation = neuron; // at this point, THE CHARGE HAS // FINISHED
									// TRAVELLING!!!!!!
		return neuron;
	}

}
