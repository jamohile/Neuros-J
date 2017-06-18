import java.util.Random;
import java.util.Vector;

public class Neuron {
	static int lastID; // provides a way for neurons to always have
						// unique
						// id's
	private Integer id;
	private BranchReferenceManager branchReferenceManager;
	int totalBranches; //stores all branches, inbound and outbound
	boolean hasCharge = false; //does the neuron CURRENTLY have a complex charge? used to manage concurrency

	public Neuron() {
		// TODO Auto-generated constructor stub
		id = lastID + 1;
		lastID = id; // these lines ensure that ID is always unique

		branchReferenceManager = new BranchReferenceManager();
	}
	public int getAverageStrength() { // returns the "average" branch strength
		return getTotalStrength() / (getNumberOfBranches() + 1);// add one to
																// compensate for 0
	}
	public int getTotalStrength() {
		return branchReferenceManager.getTotalStrength();
	}
	public void incrementConnection(Neuron neuron) {
		incrementConnection(neuron, 1);
	}
	public void incrementConnection(Neuron neuron, int strength) {
		incrementConnection(neuron, strength, false, false);
	}
	public void incrementConnection(Neuron neuron, int strength,
			boolean forceBidirectional, boolean forceEqualBidirectional) { // forceBIDIRECTIONAL
																			// will
																			// be
																			// used
																			// when
		// creating new stem neurons, that
		// require a connection to the root
		// if this neuron is already connected to the argument neuron,
		// strengthen the connection. Otherwise, create a new branch with
		// strength "1".
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
