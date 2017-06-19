import java.util.Iterator;
import java.util.Vector;

public class BranchReferenceManager {
	//Lowest and highest strengths are used to calculate hardTrim averages.
	static int lowestStrength;
	static int highestStrength;
	//The total strength of all instance branches, used during probability searches.
	int totalStrength;
	Vector<Branch> branches;

	public BranchReferenceManager() {
		// Instantiate container
		branches = new Vector<Branch>();
	}
	//Overload to increment branch to argument by 1
	public void incrementBranch(Neuron neuron) {
		incrementBranch(neuron, 1);
	}
	//Increment branch to argument neuron by arbitrary strength
	public void incrementBranch(Neuron neuron, int strength) {
		//Gets the branch index of argument neuron, or -1 if not connection exists
		int indexOfNeuron = indexOfBranchToNeuron(neuron);
		//If index is not -1, increment the existing connection. 
		if (indexOfNeuron != -1) {
			branches.elementAt(indexOfNeuron).strengthen(strength);
			if (branches.elementAt(indexOfNeuron).strength > highestStrength) {
				highestStrength = branches.elementAt(indexOfNeuron).strength;
			}
		} else {
			//the connection does not exist yet, make one.
			createBranch(neuron, strength);
		}
		totalStrength += strength;
	}
	//Get a branch, by using its strength in a weighted probability search with all other branches
	public Branch getBranchByStrength(int strength) {
		return searchByStrength(0, branches.size() - 1, strength);
	}
	//Recursively Binary-Tree-Search to find most likely branch, based on goal strength
	public Branch searchByStrength(int lower, int upper, int goal) {
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
	//Get the total strength of all desired elements from BTS
	public int getStrengthSum(int upper) {
		int sum = 0;
		for (int index = upper; index >= 0; index--) {
			sum += branches.elementAt(index).getStrength();
		}
		return sum;
	}
	//getter for number of branches
	public int getSize() {
		return branches.size();
	}
	//getter for total strength of all branches
	public int getTotalStrength() {
		return totalStrength;
	}
	//get the average strength of all branches
	public float getAverageStrength() {
		//add one to compensate for 0
		return (float) totalStrength / (float) (branches.size() + 1);
	} 
	//Create a new branch to argument neuron
	public void createBranch(Neuron neuron, int strength) {
		Branch branch = new Branch(neuron, strength);
		branches.add(branch);
	}
	//Remove a given branch from container
	public void removeBranch(Branch branch) {
		totalStrength -= branch.strength;
		branches.remove(branch);
	}
	//Get the average strength, and remove all branches lower than that strength. 
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
	}
	//Returns whether or not a connection exists to argument neuron.
	public boolean containsBranchToNeuron(Neuron neuron) {
		return (indexOfBranchToNeuron(neuron) != -1) ? true : false;
	}
	//Returns the index of branch connecting to argument neuron, or -1 if no connection
	public int indexOfBranchToNeuron(Neuron neuron) {
		Iterator<Branch> iterator = branches.iterator();
		while (iterator.hasNext()) {
			Branch branch = iterator.next();
			if ((branch.getNeuron()).equals(neuron)) {
				return branches.indexOf(branch);
			}
		}
		return -1;
	}
	//Return formatted string representation of object, primarily for .hmrs files.
	@Override
	public String toString() {
		if (branches.size() > 0) {
			return "\n\t\tBranches: " + branches.toString() + "\n";
		} else {
			return "\n\t\tBranches: no branches";
		}
	}
}
