package processor.pipeline;

import generic.*;
import processor.Clock;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import java.util.*;

import configuration.Configuration;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public Instruction inst;

	private Map<OperationType, Runnable> operationMap;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch,
			EX_MA_LatchType eX_MA_Latch,
			EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;

		// Initialize the operation map
		operationMap = new HashMap<>();
		operationMap.put(OperationType.add, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 + op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.sub, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 - op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.mul, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 * op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.div, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 / op2;
			int remainder = op1 % op2;
			containingProcessor.getRegisterFile().setValue(31, remainder);
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.and, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 & op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.or, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 | op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.xor, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 ^ op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.slt, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = (op1 < op2) ? 1 : 0;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.sll, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 << op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.srl, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 >>> op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.sra, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int aluResult = op1 >> op2;
			EX_MA_Latch.setAluResult(aluResult);
		});
		// Add R2I instructions
		operationMap.put(OperationType.addi, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 + immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.subi, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 - immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.muli, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 * immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.divi, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 / immediate;
			int remainder = op1 % immediate;
			containingProcessor.getRegisterFile().setValue(31, remainder);
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.andi, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 & immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.ori, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 | immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.xori, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 ^ immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.slti, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = (op1 < immediate) ? 1 : 0;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.slli, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 << immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.srli, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 >>> immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.srai, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 >> immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		// Branch instructions
		operationMap.put(OperationType.beq, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int immediate = OF_EX_Latch.getInstruction().getDestinationOperand().getValue();
			if (op1 == op2) {
				int aluResult = OF_EX_Latch.getInstruction().getProgramCounter() + immediate;
				EX_IF_Latch.setEX_IF_enable(true, aluResult);
			}
		});
		operationMap.put(OperationType.bne, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int immediate = OF_EX_Latch.getInstruction().getDestinationOperand().getValue();
			if (op1 != op2) {
				int aluResult = OF_EX_Latch.getInstruction().getProgramCounter() + immediate;
				EX_IF_Latch.setEX_IF_enable(true, aluResult);
			}
		});
		operationMap.put(OperationType.bgt, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int immediate = OF_EX_Latch.getInstruction().getDestinationOperand().getValue();
			if (op1 > op2) {
				int aluResult = OF_EX_Latch.getInstruction().getProgramCounter() + immediate;
				EX_IF_Latch.setEX_IF_enable(true, aluResult);
			}
		});
		operationMap.put(OperationType.blt, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int op2 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand2().getValue());
			int immediate = OF_EX_Latch.getInstruction().getDestinationOperand().getValue();
			if (op1 < op2) {
				int aluResult = OF_EX_Latch.getInstruction().getProgramCounter() + immediate;
				EX_IF_Latch.setEX_IF_enable(true, aluResult);
			}
		});
		operationMap.put(OperationType.store, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getDestinationOperand().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 + immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.load, () -> {
			int op1 = containingProcessor.getRegisterFile()
					.getValue(OF_EX_Latch.getInstruction().getSourceOperand1().getValue());
			int immediate = OF_EX_Latch.getInstruction().getSourceOperand2().getValue();
			int aluResult = op1 + immediate;
			EX_MA_Latch.setAluResult(aluResult);
		});
		operationMap.put(OperationType.jmp, () -> {
			int immediate;
			OperandType jump = OF_EX_Latch.getInstruction().getDestinationOperand().getOperandType();
			if (jump == OperandType.Register) {
				immediate = containingProcessor.getRegisterFile()
						.getValue(OF_EX_Latch.getInstruction().getDestinationOperand().getValue());
			} else {
				immediate = OF_EX_Latch.getInstruction().getDestinationOperand().getValue();
			}
			int aluResult = OF_EX_Latch.getInstruction().getProgramCounter() + immediate;
			EX_IF_Latch.setEX_IF_enable(true, aluResult);
		});
		operationMap.put(OperationType.end, () -> {
		});

	}

	public void performEX() {
		if (OF_EX_Latch.isEX_Busy()) {
			IF_OF_Latch.setOF_Busy(true);
		} else {
			IF_OF_Latch.setOF_Busy(false);

			if (OF_EX_Latch.isEX_Lock()) {
				EX_MA_Latch.setMA_Lock(true);
				OF_EX_Latch.setEX_Lock(false);
				EX_MA_Latch.setInstruction(null);
				OF_EX_Latch.setEX_enable(false);
			} else if (OF_EX_Latch.isEX_enable()) {

				OF_EX_Latch.setEX_enable(false);
				Instruction CI = OF_EX_Latch.getInstruction();
				inst = CI;
				int currentPC = CI.getProgramCounter();
				OperationType CO = CI.getOperationType();

				ArrayList<String> branchInstruction = new ArrayList<>();

				branchInstruction.add("beq");
				branchInstruction.add("bne");
				branchInstruction.add("bgt");
				branchInstruction.add("blt");
				branchInstruction.add("jmp");

				if (branchInstruction.contains(CO.name())) {
					IF_EnableLatch.setIF_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.setEX_enable(false);
				}

				operationMap.getOrDefault(CO, () -> {
				}).run();

				// EX_MA_Latch.setAluResult(ALU_Result);
				EX_MA_Latch.setInstruction(CI);
				OF_EX_Latch.setEX_enable(false);
				EX_MA_Latch.setMA_enable(true);

			}
		}
	}

}