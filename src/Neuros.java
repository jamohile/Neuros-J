import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;

public class Neuros {
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
	static boolean BIDIRECTIONAL_BRANCHES = true; // if a branch is created from neuron A => B, should a branch be created from B => A?
	static boolean EQUAL_BIDIRECTIONAL_STRENGTH = false; // assuming bidirectional is true, should the other branch be incremented with the same value? or just 1.
	static boolean ARTIFICIALLY_MOVE_CHARGE = false; // should the charge be automatically moved to newly created neurons?
	static int HARD_TRIM_EVERY = 1000; // how often should a hard trim run, on the neuronal level? a hard trim deletes any branches below the average.
	static boolean SIMPLE_CHARGE = false; // should neuros run under a simple charge system? in simple, only one charge runs for 'X' phases. In a complex system, many charges will run concurrently,
											// for
											// a fixed time or total phases.
	static int phase; // the current phase number
	static Vector<Neuron> neurons; // a container for neurons, which through sub-objects holds all components of the runtime
	static Vector<ComplexCharge> charges; // only used when SIMPLE_CHARGE is off, and multiple threaded charges are used

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		neurons = new Vector<Neuron>();
		charges = new Vector<ComplexCharge>();
		buildNetwork(NUMBER_OF_NEURONS);
		System.out.println("The network has been generated, and it's initial state has been saved.");
		System.out.println("A charge will be started on neuron 1, and will run for " + NUMBER_OF_PHASES + " phases.");

		if (SIMPLE_CHARGE) {
			Charge charge = new Charge(neurons.elementAt(0));
			while (phase < NUMBER_OF_PHASES) { // run the number of phases necessary
				charge.arrive(phase % HARD_TRIM_EVERY == 0); // run arrival code
				// if the phase is synced up with "NEW NEURON EVERY" add a new neuron
				if (phase % NEURON_EVERY == 0) {
					Neuron neuron = addNewNeuron(charge.getCurrentLocation()); // adds a new neuron, stemming from current location
					if (ARTIFICIALLY_MOVE_CHARGE) {
						charge.move(neuron);// artifically move the charge to the new neuron
					}
				} else {
					charge.move();
				}
				phase += 1;
				if ((phase) % (NUMBER_OF_PHASES / 10) == 0) {
					System.out.println("Phasing is " + (int) (100 * ((double) phase / (double) NUMBER_OF_PHASES))
							+ "% complete. " + neurons.size() + " neurons have been created.");
				}
			}
			System.out.println("Charge phasing has completed, at an average rate of "
					+ (float) NUMBER_OF_PHASES / (float) ((System.currentTimeMillis() - startTime) / 1000)
					+ " phases/second. The network will now be analyzed and saved.");
			closeNetwork(startTime);
		} else {
			ComplexCharge.createComplexCharge();
		}
	}

	public static void closeNetwork(long startTime) {
		System.out.println("Neuros activity has finished. The network will now be analyzed and saved.");
		logNetwork(startTime, true);
		logNetwork(startTime, false);
		System.out.println("Network interpretation has completed, the network's current state has been saved");
	}

	public static Neuron addNewNeuron(Neuron root) {// add a new neuron, stemming of the root neuron
		Neuron neuron = new Neuron(); // create a new neuronD
		neurons.addElement(neuron); // add the neuron to the registry vector
		int startingStrength = root.getAverageStrength(); // assign the strength to the average of the root neuron, to give it a chance to grow
		neuron.incrementConnection(root, startingStrength, true, true);
		return neuron;
	}

	private static void buildNetwork(int numNeurons) {
		for (int x = 0; x < numNeurons; x++) {
			Neuron neuron = new Neuron();
			neurons.addElement(neuron);
			int connections = 0;
			Random random = new Random();
			int startingBound;
			if (neurons.size() > 2) {
				startingBound = random.nextInt(neurons.size() - 2);
			} else {
				startingBound = 0;
			}
			for (int y = startingBound; y < neurons.size() - 1; y++) { // iterate through neurons, and randomly establish connections with up to 20
				if (connections == 0) {
					neuron.incrementConnection(neurons.elementAt(y)); // add at least one connection, guaranteed
					connections += 1;
				} else if (connections <= 20) { // maximum 20 connections for now
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

	public static void logNetwork(String name, long startTime, Boolean machineFile) {
		String logFileName = "LOGS/STATE_" + name + String.valueOf(System.currentTimeMillis())
				+ ((machineFile) ? ".nms" : ".nhrs"); // toggles between a NeurosMachineState and NeurosHumanState file
		try {
			PrintWriter writer = new PrintWriter(logFileName, "UTF-8");
			if (!machineFile) { // human readable file desired
				writer.println(neurons);
			} else {
				// log a compressed nms file, to be machine interpreted
				writer.print(NMSInterpreter.createNMS(neurons));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void logNetwork(long startTime, Boolean machineFile) {
		logNetwork("", startTime, machineFile);
	}

}
