package generic;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import processor.Clock;
import processor.Processor;

public class Simulator {

	static Processor processor;
	static boolean simulationComplete;
	static EventQueue eventQueue;
	public static int number_Of_inst;

	public static void setupSimulation(String assemblyProgramFile, Processor p) {
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);

		// adding eventQueue to constructor.
		eventQueue = new EventQueue();
		number_Of_inst = 0;
		simulationComplete = false;
	}

	static void loadProgram(String assemblyProgramFile) {
		processor.getRegisterFile().setValue(0, 0);
		processor.getRegisterFile().setValue(1, 65535);
		processor.getRegisterFile().setValue(2, 65535);

		// Reading the .out/.obj file and putting all the instructions in memory
		File file = new File(assemblyProgramFile);
		try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {

			int pc_val = dis.readInt();
			processor.getRegisterFile().setProgramCounter(pc_val);

			int index = 0;
			while (true) {
				int value = dis.readInt();
				processor.getMainMemory().setWord(index, value);
				index++;
			}

		} catch (EOFException eof) {
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static EventQueue getEventQueue() {
		return eventQueue;
	}

	// Getter and Setter function for number of cycles
	public static void setNoOfInstructions(int number) {
		number_Of_inst = number;
	}

	public static int getNoOfInstructions() {
		return number_Of_inst;
	}

	public static void simulate() {
		Statistics.setNumberOfInstructions(0);
		Statistics.setNumberOfCycles(0);

		while (simulationComplete == false) {
			// perfrom RW
			processor.getRWUnit().performRW();

			// perform MA
			processor.getMAUnit().performMA();

			// perform EX
			processor.getEXUnit().performEX();

			// process Events
			eventQueue.processEvents();

			// perform OF
			processor.getOFUnit().performOF();

			// perform IF
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}

		Statistics.setNumberOfInstructions(number_Of_inst);
		Statistics.setthroughPut();
	}

	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}
}
