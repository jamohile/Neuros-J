import java.util.Iterator;
import java.util.Vector;

public class BranchReferenceManager {
	static int lowestStrength;
	static int highestStrength; // used to caluclate cutoff values for trimming
	int totalStrength;
	Vector<Branch> branches;

	public BranchReferenceManager() {
		// TODO Auto-generated constructor stub
		branches = new Vector<Branch>();
	}

	public void incrementBranch(Neuron neuron) {
		incrementBranch(neuron, 1);
	}

	public void incrementBranch(Neuron neuron, int strength) {
		int indexOfNeuron = indexOfBranchToNeuron(neuron); // stores the index, or
															// "-1" if not contained
		if (indexOfNeuron != -1) {
			// already contains neuron reference, therefore strengthen the connection
			branches.elementAt(indexOfNeuron).strengthen(strength);
			if (branches.elementAt(indexOfNeuron).strength > highestStrength) {
				highestStrength = branches.elementAt(indexOfNeuron).strength;
			}
		} else {
			createBranch(neuron, strength);
		}
		totalStrength += strength; // no matter what happened, the total strength was
		// increased by one
	}

	public Branch getBranchByStrength(int strength) {
		return searchByStrength(0, branches.size() - 1, strength);
	}

	public Branch searchByStrength(int lower, int upper, int goal) { // recursive
																		// binary-tree
																		// search to
																		// find
																		// corresponding
																		// branch to
																		// domain
																		// argument
		int midpoint = (int) Math.ceil((lower + upper) / 2);
		if (midpoint == upper && midpoint == lower) { // search is complete
			// System.out.println(midpoint);
			return branches.elementAt(midpoint);
		}
		// } else {
		if (getStrengthSum(midpoint) >= goal) {
			upper = midpoint;
		} else {
			lower = midpoint + 1;
		}
		return (searchByStrength(lower, upper, goal));
	}

	public int getStrengthSum(int upper) {
		int sum = 0;
		for (int index = upper; index >= 0; index--) {
			sum += branches.elementAt(index).getStrength();
		}
		return sum;
	}

	public int getSize() {
		return branches.size();
	}

	public int getTotalStrength() {
		return totalStrength;
	}

	public float getAverageStrength() { // returns the "average" branch strength
		return (float) totalStrength / (float) (branches.size() + 1);// add one to
	} // compensate for 0

	public void createBranch(Neuron neuron, int strength) {
		Branch branch = new Branch(neuron, strength);
		branches.add(branch);
	}

	public void removeBranch(Branch branch) {
		totalStrength -= branch.strength;
		branches.remove(branch);
	}

	public void hardTrim() {
		int trimmed = 0;
		Iterator<Branch> iterator = branches.iterator();
		while (iterator.hasNext()) {
			Branch branch = iterator.next();
			int branchStrength = branch.getStrength();
			if (branchStrength < getAverageStrength() && highestStrength > 10 && branchStrength != totalStrength) {
				// TODO: add random save probability later
				totalStrength -= branchStrength;
				iterator.remove();
				trimmed += 1;
			}
		}
		// System.out.println(trimmed);
	}

	public boolean containsBranchToNeuron(Neuron neuron) {
		return (indexOfBranchToNeuron(neuron) != -1) ? true : false; // returns true
																		// if the
																		// index of
																		// the neuron
																		// is not
																		// "-1"
	}

	public int indexOfBranchToNeuron(Neuron neuron) { // return the index of the
														// branch containing
														// reference to argument
														// neuron, "-1" means
														// reference is not contained
		Iterator<Branch> iterator = branches.iterator();
		while (iterator.hasNext()) {
			Branch branch = iterator.next();
			if ((branch.getNeuron()).equals(neuron)) { // equal, therefore it is
														// contained
				return branches.indexOf(branch);
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (branches.size() > 0) {
			return "\n\t\tBranches: " + branches.toString() + "\n";
		} else {
			return "\n\t\tBranches: no branches";
		}
	}
}
