import java.util.Random;

public class ComplexCharge extends Charge implements Runnable {
	//The total number of phases run by all complex charges
	static int total_phases;
	//The number of complex charges currently running
	static int running_charges;
	//The number of phases this charge has running
	int phases; 
	//The start time of this charge in ms
	long startTime;
	//the total runtime of this charge, updated each phase
	long runningTime;
	//The inverted likeliness of charge to collapse
	int stability = 1000000; 
	//likeliness of charge to split
	int excitement = 2; 
	//The limiting factor for this charge's life
	enum CHARGE_LIMITS {
		PERSONAL_PHASE, TOTAL_PHASE, LIFETIME
	}
	//The limiting factor used by this charge instance
	CHARGE_LIMITS limitingFactor;
	//the time limit for this charge, if lifetime limited
	long limit;
	//The thread on which this charge will run
	Thread thread;

	public ComplexCharge(Neuron start, CHARGE_LIMITS limiting_factor, long limit) {
		super(start);
		this.limitingFactor = limiting_factor;
		this.limit = limit;
		this.id = NEXT_ID;
		NEXT_ID += 1;
		startTime = System.currentTimeMillis();
		running_charges += 1;
	}
	//all charge runtime will happen in this threaded function
	public void run() {
		while (shouldRun()) {
			// charge activities
			while (currentLocation.hasCharge) {
				//prevent charge from moving to quickly
				try {
					thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// if the phase is synced up with "NEW NEURON EVERY" add a new neuron
			if (phases % Neuros.NEURON_EVERY == 0) {
				Neuron neuron = Neuros.addNewNeuron(this.getCurrentLocation());
				if (Neuros.ARTIFICIALLY_MOVE_CHARGE) {
					this.move(neuron);
				}
			} else {
				this.move();
			}
			//Caculate the probability of random events such as collapse and split
			randomActions();
			//update markers
			phases += 1;
			total_phases += 1;
			runningTime = System.currentTimeMillis() - startTime;
		}
		removeCharge();
	}
	//remove this charge, and close netwrok if all charges are removed
	@Override
	public void removeCharge() {
		System.out.println("Charge &" + this.id + " is being removed.");
		running_charges -= 1;
		if (running_charges == 0) {
			Neuros.closeNetwork(startTime);
		}
		Neuros.charges.remove(this);
	}
	//Calculate the probability of random events, to add spontanaiety.
	public void randomActions() {
		Random random = new Random();
		int probabilitySplit = random.nextInt(1000000);
		int probabilityCollapse = random.nextInt(1000000);

		if (probabilitySplit < excitement) {
			// create new charge
			createComplexCharge(currentLocation);
		}
		if (probabilityCollapse > stability) {
			// collapse charge
			removeCharge();
		}
	}
	//create and setup a new instance of the complex charge, overloaded for root neuron
	public static void createComplexCharge() {
		createComplexCharge(Neuros.neurons.elementAt(0));
	}
	//create and setup complex charge at arbitrary neuron
	public static void createComplexCharge(Neuron neuron) {
		ComplexCharge complexCharge = new ComplexCharge(Neuros.neurons.elementAt(0),
				ComplexCharge.CHARGE_LIMITS.LIFETIME, 2000);
		Neuros.charges.add(complexCharge);
		complexCharge.start();
	}
	//start the thread, activating the neuron
	public void start() {
		if (thread == null) {
			System.out.println("Charge &" + this.id + " has started running.");
			thread = new Thread(this, "CHARGE_" + this.id);
			thread.start();
		}
	}
	// keeps track of whether the charge should still be running
	private boolean shouldRun() {
		switch (limitingFactor) {
		case PERSONAL_PHASE:
			if (phases <= limit) {
				return true;
			}
			break;
		case TOTAL_PHASE:
			if (phases <= total_phases) {
				return true;
			}
			break;
		case LIFETIME:
			if (runningTime < limit) {
				return true;
			}
			break;
		}
		return false;
	}
}
