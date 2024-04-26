package processor.pipeline;

import generic.Instruction;
import generic.Instruction.OperationType;
import processor.Processor;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch,
			IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_Lock()) {
			System.out.println("MA unit is locked");
			MA_RW_Latch.setRW_Lock(true);
			EX_MA_Latch.setMA_Lock(false);
			MA_RW_Latch.setInstruction(null);
		} else if (EX_MA_Latch.isMA_enable()) {
			Instruction CI = EX_MA_Latch.getInstruction();// here CI is Current Inst.
			// if (CI == null) {
			// // Check if instructions is null that is nop, move to next unit
			// System.out.println("current inst in MA is nop");
			// EX_MA_Latch.setMA_enable(false);
			// MA_RW_Latch.setRW_enable(true);
			// } else {
			OperationType currentOperation = CI.getOperationType();
			int aluResult = EX_MA_Latch.getAluResult();
			System.out.println("\tMA Unit");
			System.out.println("Current operation is " + currentOperation.name());

			// if it is an end instruction
			if (currentOperation == OperationType.end) {
				IF_EnableLatch.setIF_enable(false);
			}
			// If it is a store instruction
			if (currentOperation == OperationType.store) {
				int store_value = containingProcessor.getRegisterFile()
						.getValue(CI.getSourceOperand1().getValue());
				containingProcessor.getMainMemory().setWord(aluResult, store_value);
				System.out.println(store_value + "is stored in memory at address" + aluResult);
			}

			// If it is a load instruction
			if (currentOperation == OperationType.load) {
				int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
				MA_RW_Latch.setLdResult(ldResult);
				System.out.println("loading value from memory");
			}

			MA_RW_Latch.setInstruction(CI);
			MA_RW_Latch.setAluResult(aluResult);
			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
			// }

		}
	}

}
