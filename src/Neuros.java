import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;

public class Neuros {
	//PROGRAM FLAGS, values adjusted before runtime to control execution
 	// the initial number of neurons to seed with.
	static int NUMBER_OF_NEURONS = 3;
	// the number of cycles the charge will travel
	static int NUMBER_OF_PHASES = 100000000;
 	// artificially add a neuron every this many phases
	static int NEURON_EVERY = 1000;
 	// The length of a charge's history
	static int CHARGE_HISTORY_MAX_LENGTH = 3;
	//Whether or not history recency should be weighted
	static boolean HISTORICAL_WEIGHTING = true;
	//Whether or not branches should be created bidirectionally
	static boolean BIDIRECTIONAL_BRANCHES = true;
	//Whether or not branches should have equal strength when created bidirectionally
	static boolean EQUAL_BIDIRECTIONAL_STRENGTH = false;
	//Whether or not charges should be artificially moved to newly created neurons
	static boolean ARTIFICIALLY_MOVE_CHARGE = false;
	//The number of phases between hard trims
	static int HARD_TRIM_EVERY = 1000;
	//If false, Neuros will run only one charge - synchrously. Else multiple asynchrous.
	static boolean SIMPLE_CHARGE = false;
	// the current phase number
	static int phase;
	// a container for neurons, which through sub-objects holds all components of the runtime
	static Vector<Neuron> neurons;
	// only used when SIMPLE_CHARGE is false, and multiple threaded charges are used
	static Vector<ComplexCharge> charges;
	
	public static void main(String[] args) {
		// mark the system's start time
		long startTime = System.currentTimeMillis();
		neurons = new Vector<Neuron>();
		charges = new Vector<ComplexCharge>();
		buildNetwork(NUMBER_OF_NEURONS);
		System.out.println("The network has been generated, and it's initial state has been saved.");
		System.out.println("A charge will be started on neuron 1, and will run for " + NUMBER_OF_PHASES + " phases.");
		//If only simple charges are being used
		if (SIMPLE_CHARGE) {
			Charge charge = new Charge(neurons.elementAt(0));
			//Run the number of phases specified by the flag
			while (phase < NUMBER_OF_PHASES) {
				//Run arrival code
				charge.arrive(phase % HARD_TRIM_EVERY == 0);
				// if the phase is synced up with "NEW_NEURON_EVERY" add a new neuron
				if (phase % NEURON_EVERY == 0) {
					Neuron neuron = addNewNeuron(charge.getCurrentLocation()); // adds a new neuron, stemming from current location
					//artificially move charge to new neuron, if flagged
					if (ARTIFICIALLY_MOVE_CHARGE) {
						charge.move(neuron);
					}
				} else {
					//If NEW_NEURON_EVERY not satisfied, simply move charge
					charge.move();
				}
				phase += 1;
				if ((phase) % (NUMBER_OF_PHASES / 10) == 0) {
					System.out.println("Phasing is " + (int) (100 * ((double) phase / (double) NUMBER_OF_PHASES))
							+ "% complete. " + neurons.size() + " neurons have been created.");
				}
			}
			//Notify of charge phase completion
			System.out.println("Charge phasing has completed, at an average rate of "
					+ (float) NUMBER_OF_PHASES / (float) ((System.currentTimeMillis() - startTime) / 1000)
					+ " phases/second. The network will now be analyzed and saved.");
			closeNetwork(startTime);
		} else {
			//If complex charges are used, all charge runtime will be handled by them
			ComplexCharge.createComplexCharge();
		}
	}
	//Called to notify the user of network completiton, and save file
	public static void closeNetwork(long startTime) {
		System.out.println("Neuros activity has finished. The network will now be analyzed and saved.");
		//Save, by default as a .nhr and .nms file
		logNetwork(startTime, true);
		logNetwork(startTime, false);
		System.out.println("Network interpretation has completed, the network's current state has been saved");
	}
	//Add a new neuron, stemming from the parameter neuron
	public static Neuron addNewNeuron(Neuron root) {
		Neuron neuron = new Neuron();
		neurons.addElement(neuron);
		//Set strength of new neuron to average of root's connections
		int startingStrength = root.getAverageStrength();
		neuron.incrementConnection(root, startingStrength, true, true);
		return neuron;
	}
	//Build and seed a network with the desired number of neurons
	private static void buildNetwork(int numNeurons) {
		for (int x = 0; x < numNeurons; x++) {
			//Create a new neuron, add to container
			Neuron neuron = new Neuron();
			neurons.addElement(neuron);
			int connections = 0;
			//Randomly choose an index from neurons.
			Random random = new Random();
			int startingBound;
			if (neurons.size() > 2) {
				startingBound = random.nextInt(neurons.size() - 2);
			} else {
				startingBound = 0;
			}
			//iterate neurons (from startingBound), randomly make connections with up to 20 (at least 1)
			for (int y = startingBound; y < neurons.size() - 1; y++) { 
				if (connections == 0) {
					neuron.incrementConnection(neurons.elementAt(y));
					connections += 1;
				// maximum 20 connections for now
				} else if (connections <= 20) { 
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
	//Function to control saving of network, into nms and nhrs files
	public static void logNetwork(String name, long startTime, Boolean machineFile) {
		// toggles between a NeurosMachineState and NeurosHumanState file
		String logFileName = "LOGS/STATE_" + name + String.valueOf(System.currentTimeMillis())
				+ ((machineFile) ? ".nms" : ".nhrs");
		try {
			PrintWriter writer = new PrintWriter(logFileName, "UTF-8");
			 // human readable file desired
			if (!machineFile) {
				writer.println(neurons);
			} else {
				// log a compressed nms file, to be machine interpreted
				writer.print(NMSInterpreter.createNMS(neurons));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	//Overloaded function, to use if name is not needed
	public static void logNetwork(long startTime, Boolean machineFile) {
		logNetwork("", startTime, machineFile);
	}

}
