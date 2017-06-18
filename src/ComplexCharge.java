import java.util.Random;

public class ComplexCharge extends Charge implements Runnable {
	static int total_phases; // how many phases have all complex charges run?
	static int running_charges;
	int phases; // how many phases has this charge run?
	long startTime; // when did this start?
	long runningTime; // how long has this charge been running for? stored in ms, updated once each phase.
	int stability = 1000000; // how likely is this charge to randomly collapse? stability ranges from 1 - 1 million, lower = more likely to collapse
	int excitement = 2; // how likely is this charge to randomly split? out of one million

	enum CHARGE_LIMITS {// by what method will this charge die? induvidual phase, total phase, lifetime...
		PERSONAL_PHASE, TOTAL_PHASE, LIFETIME
	}

	CHARGE_LIMITS limitingFactor;
	long limit;
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

	public void run() {
		// TODO Auto-generated method stub
		while (shouldRun()) {
			// charge activities
			while (currentLocation.hasCharge) {
				try {
					thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// if the phase is synced up with "NEW NEURON EVERY" add a new neuron
			if (phases % Neuros.NEURON_EVERY == 0) {
				Neuron neuron = Neuros.addNewNeuron(this.getCurrentLocation()); // adds a new neuron, stemming from current location
				if (Neuros.ARTIFICIALLY_MOVE_CHARGE) {
					this.move(neuron);// artifically move the charge to the new neuron
				}
			} else {
				this.move();
			}
			randomActions(); // calculate whether to run random events such as collapse, split, etc.
			phases += 1;
			total_phases += 1;
			runningTime = System.currentTimeMillis() - startTime;
		}
		removeCharge();
	}
	@Override
	public void removeCharge() {
		System.out.println("Charge &" + this.id + " is being removed.");
		running_charges -= 1;
		if (running_charges == 0) {
			Neuros.closeNetwork(startTime);
		}
		Neuros.charges.remove(this);
	}

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

	public static void createComplexCharge() {
		createComplexCharge(Neuros.neurons.elementAt(0));
	}

	public static void createComplexCharge(Neuron neuron) {
		ComplexCharge complexCharge = new ComplexCharge(Neuros.neurons.elementAt(0),
				ComplexCharge.CHARGE_LIMITS.LIFETIME, 2000);
		Neuros.charges.add(complexCharge);
		complexCharge.start();
	}

	public void start() {
		if (thread == null) {
			System.out.println("Charge &" + this.id + " has started running.");
			thread = new Thread(this, "CHARGE_" + this.id);
			thread.start();
		}
	}

	private boolean shouldRun() {
		// keeps track of whether the charge should still be running, based on basic predictable factors such as time and phases.
		// TODO add spontaneous death or recovery
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
