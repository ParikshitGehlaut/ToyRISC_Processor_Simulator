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

	public static void setupSimulation(String assemblyProgramFile, Processor p) {
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);

		simulationComplete = false;
	}

	static void loadProgram(String assemblyProgramFile) {
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 * in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 * x0 = 0
		 * x1 = 65535
		 * x2 = 65535
		 */
		// Getting the PC value and setting PC to it
		// Setting up the Register File
		processor.getRegisterFile().setValue(0, 0);
		processor.getRegisterFile().setValue(1, 65535);
		processor.getRegisterFile().setValue(2, 65535);

		// Reading the .obj file and putting all the instructions in memory
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

	public static void simulate() {
		Statistics.setNumberOfInstructions(0);
		Statistics.setNumberOfCycles(0);

		while (simulationComplete == false) {
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			processor.getOFUnit().performOF();
			Clock.incrementClock();
			processor.getEXUnit().performEX();
			Clock.incrementClock();
			processor.getMAUnit().performMA();
			Clock.incrementClock();
			processor.getRWUnit().performRW();
			Clock.incrementClock();

			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}
	}

	public static void setSimulationComplete(boolean value) {
		simulationComplete = value;
	}
}
