import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;
import org.json.simple.*;

public class Neuros {
	static int NUMBER_OF_NEURONS = 1000; // the initial number of neurons to seed
											// with.
	static int NUMBER_OF_PHASES = 10000; // the number of cycles the charge will
											// travel
	static int NEURON_EVERY = 2; // artificially add a neuron every this many phases
	static int CHARGE_HISTORY_MAX_LENGTH = 5; // how many neurons long should a
												// charge's
												// history be?
	static boolean HISTORICAL_WEIGHTING = true; // when analyzing a charge's
												// history, should more recent
												// destinations receive higher
												// incrementations?
	static boolean BIDIRECTIONAL_BRANCHES = true; // if a branch is created from
													// neuron A => B, should a branch
													// be created from B => A?
	static boolean EQUAL_BIDIRECTIONAL_STRENGTH = false; // assuming bidirectional is
															// true, should the other
															// branch be incremented
															// with the same value?
															// or just 1.

	static int phase; // the current phase number
	static Vector<Neuron> neurons;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		neurons = new Vector<Neuron>();
		buildNetwork(NUMBER_OF_NEURONS);
		logNetwork(startTime);
		System.out.println(
				"The network has been generated, and it's initial state has been saved.");
		System.out.println(
				"A charge will be started on neuron 1, and will run for "
						+ NUMBER_OF_PHASES + " phases.");
		Charge charge = new Charge(neurons.elementAt(0));

		while (phase < NUMBER_OF_PHASES) { // run the number of phases necessary
			charge.arrive(); // run arrival code
			// if the phase is synced up with "NEW NEURON EVERY" add a new neuron
			if (phase % NEURON_EVERY == 0) {
				Neuron neuron = addNewNeuron(charge.getCurrentLocation()); // adds a
																			// new
																			// neuron,
				// using the current
				// location as a root

				charge.move(neuron).logId();// artifically move the charge to the new
											// neuron
			} else {
				charge.move().logId();
			}

			phase += 1;
		}
		logNetwork(startTime);
		System.out.println(
				"Charge phasing has completed, the network's current state has been saved");
	}
	public static Neuron addNewNeuron(Neuron root) {// add a new neuron, stemming of
													// the root neuron
		Neuron neuron = new Neuron(); // create a new neuron
		neurons.addElement(neuron); // add the neuron to the registry vector
		int startingStrength = root.getAverageStrength(); // assign the strength to
															// the average of the
															// root neuron, to give
															// it a chance to grow
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
			for (int y = startingBound; y < neurons.size() - 1; y++) { // iterate
																		// through
																		// the
																		// neurons,
																		// and
																		// randomly
																		// establish
																		// connections
																		// with up to
																		// 10
				if (connections == 0) {
					neuron.incrementConnection(neurons.elementAt(y)); // add one
																		// connection,
																		// guaranteed
					connections += 1;
				} else if (connections <= 20) { // maximum 10 connections for now
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
