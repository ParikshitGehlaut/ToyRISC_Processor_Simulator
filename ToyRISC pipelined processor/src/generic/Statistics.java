package generic;

import java.io.PrintWriter;

public class Statistics {

	static int numberOfInstructions;
	static int numberOfCycles;
	static int numberOfOFUnitStalls;
	static int numberOfWrongInstructions;

	public static void printStatistics(String statFile) {
		try {
			PrintWriter writer = new PrintWriter(statFile);

			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println("Number of OF unit stalls = " + numberOfOFUnitStalls);
			writer.println("Number of Wrong Branches Instructions = " + numberOfWrongInstructions);

			writer.close();
		} catch (Exception e) {
			Misc.printErrorAndExit(e.getMessage());
		}
	}

	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}

	public static void setNumberOfOFUnitStalls(int numberOfOFUnitStalls) {
		Statistics.numberOfOFUnitStalls = numberOfOFUnitStalls;
	}

	public static void setNumberOfWrongInstructions(int numberOfWrongInstructions) {
		Statistics.numberOfWrongInstructions = numberOfWrongInstructions;
	}

	public static int getNumberOfInstructions() {
		return numberOfInstructions;
	}

	public static int getNumberOfCycles() {
		return numberOfCycles;
	}

	public static int getNumberOfOFUnitStalls() {
		return numberOfOFUnitStalls;
	}

	public static int getNumberOfWrongInstructions() {
		return numberOfWrongInstructions;
	}
}
