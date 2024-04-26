package processor.pipeline;

import generic.Instruction;
import generic.Instruction.OperationType;
import processor.Processor;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType EX_MA_Latch, MA_RW_LatchType MA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = EX_MA_Latch;
		this.MA_RW_Latch = MA_RW_Latch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_enable()) {
			int aluResult = EX_MA_Latch.getAluResult();
			Instruction CI = EX_MA_Latch.getInstruction();// here CI is Current Inst.
			OperationType currentOperation = CI.getOperationType();

			System.out.println("MA Unit");
			System.out.println("Current operation is " + currentOperation.name());
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
		}
	}

}
