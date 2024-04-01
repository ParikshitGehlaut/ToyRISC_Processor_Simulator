package processor.pipeline;

import java.util.HashMap;
import java.util.Map;
import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	private Map<OperationType, Runnable> operationMap;

	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch,
			IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;

		// Initialize the operation map
		operationMap = new HashMap<>();
		operationMap.put(OperationType.store, () -> {
		});
		operationMap.put(OperationType.jmp, () -> {
		});
		operationMap.put(OperationType.beq, () -> {
		});
		operationMap.put(OperationType.bne, () -> {
		});
		operationMap.put(OperationType.bgt, () -> {
		});
		operationMap.put(OperationType.blt, () -> {
		});
		// if it is end instruction, we end simulation
		operationMap.put(OperationType.end, () -> Simulator.setSimulationComplete(true));
		// Load instructions
		operationMap.put(OperationType.load, () -> {
			int load_value = MA_RW_Latch.getLdResult();
			int rd = MA_RW_Latch.getInstruction().getDestinationOperand().getValue();
			containingProcessor.getRegisterFile().setValue(rd, load_value);
			System.out.println("Storing = " + load_value + " at register = " + rd);
		});
		operationMap.put(null, () -> {
			int rd = MA_RW_Latch.getInstruction().getDestinationOperand().getValue();
			int aluResult = MA_RW_Latch.getAluResult();
			containingProcessor.getRegisterFile().setValue(rd, aluResult);
			System.out.println("Storing = " + aluResult + " at register = " + rd);
		});
	}

	public void performRW() {
		if (MA_RW_Latch.isRW_Lock()) {
			System.out.println("RW unit is locked");
			MA_RW_Latch.setRW_Lock(false);
		} else if (MA_RW_Latch.isRW_enable()) {
			Instruction CI = MA_RW_Latch.getInstruction();// here CI is Current Inst.

			OperationType CO = CI.getOperationType(); // here CO is current operation.
			System.out.println("\tRW unit");

			// Execute the operation using the map
			operationMap.getOrDefault(CO, operationMap.get(null)).run();

			IF_EnableLatch.setIF_enable(true);
			MA_RW_Latch.setRW_enable(false);

		}
	}
}
