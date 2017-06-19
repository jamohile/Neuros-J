import java.util.Random;
import java.util.Vector;

public class Neuron {
	//Stores the last ID used by any neuron
	static int lastID;
	private Integer id;
	//This object manages all branches for this instance
	private BranchReferenceManager branchReferenceManager;
	//Total number of inbound and outbound branches.
	int totalBranches;
	//Stores whether the neuron currently posesses a complex charge
	boolean hasCharge = false;

	public Neuron() {
		//Assign self a unique id
		id = lastID + 1;
		lastID = id;
		//instantiate container
		branchReferenceManager = new BranchReferenceManager();
	}
	//Calculates and return the average strength of all connections
	public int getAverageStrength() {
		return getTotalStrength() / (getNumberOfBranches() + 1);
	}
	//Gets the total strength of all branches
	public int getTotalStrength() {
		return branchReferenceManager.getTotalStrength();
	}
	//An overload function to increment strength to another neuron by 1.
	public void incrementConnection(Neuron neuron) {
		incrementConnection(neuron, 1);
	}
	//Overload function to increment connection to a specific neuron by an arbitrary value.
	public void incrementConnection(Neuron neuron, int strength) {
		incrementConnection(neuron, strength, false, false);
	}
	//Increment a connection, taking into account environment flags.
	public void incrementConnection(Neuron neuron, int strength, boolean forceBidirectional, boolean forceEqualBidirectional) {
		//no matter what, increment the branch for this neuron.
		branchReferenceManager.incrementBranch(neuron, strength);
		// create bidirectional branch, if setting is enabled
		if (Neuros.BIDIRECTIONAL_BRANCHES || forceBidirectional) {
			if (!neuron.connectedToNeuron(this)) {
				if (Neuros.EQUAL_BIDIRECTIONAL_STRENGTH) {
					neuron.incrementConnection(this, strength);
				} else {
					neuron.incrementConnection(this);
				}
			}
		}

	}
	//Returns whether or not a connection exists to argument neuron
	public boolean connectedToNeuron(Neuron neuron) {
		return branchReferenceManager.containsBranchToNeuron(neuron);
	}
	public Branch getBranchByStrength(int strength) {
		return branchReferenceManager.getBranchByStrength(strength);
	}
	public void hardTrim(){
		branchReferenceManager.hardTrim();
	}
	public Branch getProbableDestinationBranch() { // returns the likely branch that
													// a charge
		// will travel to, based on the weighted
		// probability search system
		Random random = new Random();
		int bound = branchReferenceManager.getTotalStrength();
		if (bound != 0) {
			int destinationStrengthBound = random.nextInt(bound);
			return getBranchByStrength(destinationStrengthBound);
		}else{
			return null;
		}
	}
	public void updateBranches(Vector<Neuron> history) {// a charge will deliver it's
														// history dump to this
														// function
		for (Neuron neuron : history) {
			if (Neuros.HISTORICAL_WEIGHTING) {// if connection inrecement will be
												// weighted based on how recent it is
				incrementConnection(neuron, history.indexOf(neuron)); // increment by
																		// overall
																		// position
																		// in history
			} else {
				incrementConnection(neuron); // increment by 1
			}
		}
	}

	public final Vector<Branch> getBranches(){
		return this.branchReferenceManager.branches;
	}
	public int getId() {
		return id;
	}
	public void logId() {
		System.out.println(String.valueOf(id));
	}
	public int getNumberOfBranches() {
		return branchReferenceManager.getSize();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\n\tNeuron: " + id + branchReferenceManager.toString();
	}
}
