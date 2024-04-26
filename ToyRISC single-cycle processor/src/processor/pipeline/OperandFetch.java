package processor.pipeline;

import generic.Instruction;
import generic.Operand;
import processor.Processor;
import generic.*;
import generic.Instruction.OperationType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;

	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

	// Method to find two's complement
	public static String twoscomplement(StringBuffer str) {
		int len = str.length();

		int i;
		for (i = len - 1; i >= 0; i--)
			if (str.charAt(i) == '1')
				break;

		if (i == -1)
			return "1" + str;

		for (int k = i - 1; k >= 0; k--) {
			if (str.charAt(k) == '1')
				str.replace(k, k + 1, "0");
			else
				str.replace(k, k + 1, "1");
		}
		return str.toString();
	}

	public static String toBinary(int x, int len) {
		if (len > 0) {
			return String.format("%" + len + "s",
					Integer.toBinaryString(x)).replace(" ", "0");
		}
		return null;
	}

	public static int toInteger(String binary) {
		if (binary.charAt(0) == '1') {
			StringBuffer bufferBinary = new StringBuffer();
			bufferBinary.append(binary);
			binary = "-" + twoscomplement(bufferBinary);
		} else {
			binary = "+" + binary;
		}
		return Integer.parseInt(binary, 2);
	}

	public void performOF() {
		if (IF_OF_Latch.isOF_enable()) {
			int Instruction = IF_OF_Latch.getInstruction();
			int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
			String binaryInstruction = toBinary(Instruction, 32);

			OperationType[] operationTypes = OperationType.values(); // getting all operation types from OperationType
																		// enum
			int opCodeInt = Integer.parseInt(binaryInstruction.substring(0, 5), 2);
			OperationType CO = operationTypes[opCodeInt];

			Instruction CI = new Instruction();
			Operand rs1 = new Operand();
			Operand rs2 = new Operand();
			Operand rd = new Operand();
			Operand jump = new Operand();
			Operand imm = new Operand();
			int registerSource1 = -1;
			int registerSource2 = -1;
			int registerDestination = -1;
			int immediate = -1;
			CI.setProgramCounter(currentPC);
			CI.setOperationType(CO);
			switch (CO) {
				// below code till break corresponds to RI type, specifically for end
				case end:
					break;
				// below till break correspond to R3I type instructions
				case div:
				case and:
				case add:
				case sub:
				case sll:
				case srl:
				case sra:
				case or:
				case xor:
				case slt:
				case mul:
					rs1.setOperandType(Operand.OperandType.Register);
					rs2.setOperandType(Operand.OperandType.Register);
					rd.setOperandType(Operand.OperandType.Register);

					String b1 = (binaryInstruction.substring(5, 10));
					registerSource1 = Integer.parseInt(b1, 2);
					rs1.setValue(registerSource1);

					String b2 = (binaryInstruction.substring(10, 15));
					registerSource2 = Integer.parseInt(b2, 2);
					rs2.setValue(registerSource2);

					String b3 = (binaryInstruction.substring(15, 20));
					registerDestination = Integer.parseInt(b3, 2);
					rd.setValue(registerDestination);

					CI.setSourceOperand1(rs1);
					CI.setSourceOperand2(rs2);
					CI.setDestinationOperand(rd);
					break;
				case beq:
				case blt:
				case bgt:
				case bne:
					rs1.setOperandType(Operand.OperandType.Register);
					rs2.setOperandType(Operand.OperandType.Register);
					imm.setOperandType(Operand.OperandType.Immediate);

					String S1 = (binaryInstruction.substring(5, 10));
					registerSource1 = Integer.parseInt(S1, 2);
					rs1.setValue(registerSource1);

					String S2 = (binaryInstruction.substring(10, 15));
					registerSource2 = Integer.parseInt(S2, 2);
					rs2.setValue(registerSource2);

					String S3 = binaryInstruction.substring(15, 32);
					immediate = toInteger(S3);
					imm.setValue(immediate);

					CI.setSourceOperand1(rs1);
					CI.setSourceOperand2(rs2);
					CI.setDestinationOperand(imm);
					break;
				// below code till break corresponds to all remaining R2I type instructions
				case jmp:
					registerDestination = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
					immediate = toInteger(binaryInstruction.substring(10, 32));
					if (immediate != 0) {
						jump.setOperandType(Operand.OperandType.Immediate);
						jump.setValue(immediate);
					} else {
						jump.setOperandType(Operand.OperandType.Register);
						jump.setValue(registerDestination);
					}
					CI.setDestinationOperand(jump);
					break;

				default:
					rs1.setOperandType(Operand.OperandType.Register);
					String C1 = (binaryInstruction.substring(5, 10));
					registerSource1 = Integer.parseInt(C1, 2);
					CI.setSourceOperand1(rs1);

					rs2.setOperandType(Operand.OperandType.Immediate);
					String C2 = binaryInstruction.substring(15, 32);
					immediate = toInteger(C2);
					CI.setSourceOperand2(rs2);

					rd.setOperandType(Operand.OperandType.Register);
					String C3 = (binaryInstruction.substring(10, 15));
					registerDestination = Integer.parseInt(C3, 2);
					CI.setDestinationOperand(rd);

					rs1.setValue(registerSource1);
					rs2.setValue(immediate);
					rd.setValue(registerDestination);
					break;
			}

			OF_EX_Latch.setInstruction(CI);
			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);

		}
	}

}