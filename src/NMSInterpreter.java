import java.util.Vector;

public class NMSInterpreter {
	public static String createNMS(Vector<Neuron> neurons) {
		System.out.println("NMS is beginning...");
		long startTime = System.currentTimeMillis();
		String output = "!FileName" + "!" + String.valueOf(startTime) + "!" + neurons.size();
		for (Neuron neuron : neurons) {
			output += createNeuronNMS(neuron);
			if ((neuron.getId()) % (float)(neurons.size() / 10) == 0) {
				System.out.println(
						"NMS is " + (int) (100 * ((double) neuron.getId() / (double) neurons.size())) + "% complete.");
			}
		}
		System.out.println("NMS Interpretation has completed, at an average rate of " + (float)neurons.size()/(float)((System.currentTimeMillis() - startTime)/1000)+  " neurons/second.");
		return output;
	}

	public static String createNeuronNMS(Neuron neuron) {
		String output = "@" + neuron.getId();
		for (Branch branch : neuron.getBranches()) {
			output += createBranchNMS(branch);
		}
		return output;
	}

	public static String createBranchNMS(Branch branch) {
		String output = "#" + branch.getNeuron().getId();
		output += "$" + branch.getStrength();
		return output;
	}
}
