import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;
import org.json.simple.*;

public class Neuros {
	//FLAGS used to control execution
	// the initial number of neurons to seed with.
	static int NUMBER_OF_NEURONS = 1000;
	// the number of cycles the charge will travel
	static int NUMBER_OF_PHASES = 10000; 
	// artificially add a neuron every this many phases
	static int NEURON_EVERY = 2;
	// how many neurons long should a charge's history be?
	static int CHARGE_HISTORY_MAX_LENGTH = 5;
	//Whether or not to take charge recency into account when incrementing strength
	static boolean HISTORICAL_WEIGHTING = true;
	//Whether or not branches should always be created bidirectionally.
	static boolean BIDIRECTIONAL_BRANCHES = true;
	//Whether or not created branches should have equal strength
	static boolean EQUAL_BIDIRECTIONAL_STRENGTH = false;
	//flag of current phase
	static int phase;
	//Neuron network container
	static Vector<Neuron> neurons;

	public static void main(String[] args) {
		//Start time of network building
		long startTime = System.currentTimeMillis();
		//initialize network container
		neurons = new Vector<Neuron>();
		buildNetwork(NUMBER_OF_NEURONS);
		logNetwork(startTime);
		System.out.println(
		"The network has been generated, and it's initial state has been saved.");
		System.out.println(
		"A charge will be started on neuron 1, and will run for "+ NUMBER_OF_PHASES + " phases.");
		Charge charge = new Charge(neurons.elementAt(0));
		//Run the necessary number of phases
		while (phase < NUMBER_OF_PHASES) {
			charge.arrive();
			// if the phase is synced up with "NEW NEURON EVERY" flag add a new neuron
			if (phase % NEURON_EVERY == 0) {
				//Add a new branching neuron, and artifically move the charge there
				Neuron neuron = addNewNeuron(charge.getCurrentLocation());
				charge.move(neuron).logId();
			} else {
				charge.move().logId();
			}
			phase += 1;
		}
		//Log the state of the network to file
		logNetwork(startTime);
		System.out.println(
		"Charge phasing has completed, the network's current state has been saved");
	}
	//Function to add new Neuron branching from a specific root neuron.
	public static Neuron addNewNeuron(Neuron root) {
		//Create neuron, and add to network container
		Neuron neuron = new Neuron();
		neurons.addElement(neuron);
		//Assign the new neuron a connection strength equal to the root's average
		int startingStrength = root.getAverageStrength();
		neuron.incrementConnection(root, startingStrength, true, true);
		return neuron;
	}
	//Generate a neuron network, and pseudorandomly build connections
	private static void buildNetwork(int numNeurons) {
		for (int x = 0; x < numNeurons; x++) {
			//Create a new neuron, and add it to the container
			Neuron neuron = new Neuron();
			neurons.addElement(neuron);
			//New neuron has no connections
			int connections = 0;
			//initialize the Random class
			Random random = new Random();
			//Randomly choose which index of the network to begin making connections from
			int startingBound;
			if (neurons.size() > 2) {
				startingBound = random.nextInt(neurons.size() - 2);
			} else {
				startingBound = 0;
			}
			//Iterate through neurons, forming a connection with up to 20 based on random booleans
			for (int y = startingBound; y < neurons.size() - 1; y++) {
				//Guarantee the addition of at least one connection
				if (connections == 0) {
					neuron.incrementConnection(neurons.elementAt(y));
					connections += 1;
				} else if (connections <= 20) {
					//Establish a maximum of twenty connections randomly
					if (random.nextBoolean()) {
						neuron.incrementConnection(neurons.elementAt(y));
						connections += 1;
					}
				} else {
					break;
				}
			}
		}
	}
	//Log network to file
	public static void logNetwork(long startTime) {
		String logFileName = "LOGS/NEUROS_NETWORK_"
				+ String.valueOf(System.currentTimeMillis()) + ".neuros";
		try {
			PrintWriter writer = new PrintWriter(logFileName, "UTF-8");
			writer.println(neurons);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void logJson(Vector<Neuron> neurons) {
		for (Neuron neuron : neurons) {

		}
	}

}
