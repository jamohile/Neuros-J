public class Branch {
	//Used to ensure unique id across all branch instances.
	static int LastID;
	Integer id;
	//The neuron this connection is TO
	Neuron neuron;
	//The strength of this connection, by default 1
	int strength = 1;

	public Branch(Neuron destinationNeuron, int strength) {
		this.id = LastID + 1;
		LastID = this.id;
		this.neuron = destinationNeuron;
		this.strength = strength;
	}

	public Neuron getNeuron() {
		return this.neuron;
	}
	public int getStrength() {
		return strength;
	}
	//overload to increase strength by 1
	public void strengthen() {
		this.strength += 1;
	}
	//increase strength of connection by arbitrary amount
	public void strengthen(int increment) {
		this.strength += increment;
	}
	//Generates string represenation of branch
	@Override
	public String toString() {
		return "" + String.valueOf(neuron.getId() + ":" + strength);
	}
}
