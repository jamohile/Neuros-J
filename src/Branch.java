public class Branch {
	static int LastID;
	Neuron neuron;
	int strength = 1;
	Integer id;

	public Branch(Neuron destinationNeuron, int strength) {
		// TODO Auto-generated constructor stub
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
	public void strengthen() {
		this.strength += 1;
	}
	public void strengthen(int increment) {
		this.strength += increment;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + String.valueOf(neuron.getId() + ":" + strength);
	}
}
