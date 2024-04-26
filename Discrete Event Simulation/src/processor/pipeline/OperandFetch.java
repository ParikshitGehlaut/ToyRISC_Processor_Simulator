package processor.pipeline;

import generic.Instruction;
import generic.Operand;
import generic.Statistics;

import processor.Processor;
import java.util.*;
import generic.*;
import configuration.Configuration;
import generic.Instruction.OperationType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch,
			EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	// Method to find two's complement
	public static String twoscomplement(StringBuffer str) {
		int n = str.length();

		int i;
		for (i = n - 1; i >= 0; i--)
			if (str.charAt(i) == '1')
				break;

		if (i == -1)
			return "1" + str;

		for (int k = i - 1; k >= 0; k--) {
			// Flipping the values
			if (str.charAt(k) == '1')
				str.replace(k, k + 1, "0");
			else
				str.replace(k, k + 1, "1");
		}

		// return the 2's complement
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

	public void conflictObserved() {
		System.out.println("Conflict and Lock");
		IF_EnableLatch.setIF_enable(false);
		OF_EX_Latch.setEX_Lock(true);
	}

	public boolean isDataHazard(int rs1, int rs2) {

		boolean result;

		// Creating Conflict Operations Set
		Set<String> hazardInstructions = new HashSet<String>();
		// Adding Instructions that can cause conflict
		hazardInstructions.add("add");
		hazardInstructions.add("addi");
		hazardInstructions.add("sub");
		hazardInstructions.add("subi");
		hazardInstructions.add("mul");
		hazardInstructions.add("muli");
		hazardInstructions.add("div");
		hazardInstructions.add("divi");
		hazardInstructions.add("and");
		hazardInstructions.add("andi");
		hazardInstructions.add("or");
		hazardInstructions.add("ori");
		hazardInstructions.add("xor");
		hazardInstructions.add("xori");
		hazardInstructions.add("slt");
		hazardInstructions.add("slti");
		hazardInstructions.add("sll");
		hazardInstructions.add("slli");
		hazardInstructions.add("srl");
		hazardInstructions.add("srli");
		hazardInstructions.add("sra");
		hazardInstructions.add("srai");
		hazardInstructions.add("load");
		hazardInstructions.add("store");

		// Creating set of division instructions
		Set<String> DivisionInstructions = new HashSet<String>();
		// Adding the division instructions
		DivisionInstructions.add("div");
		DivisionInstructions.add("divi");

		Instruction EX_Stage_Ins = OF_EX_Latch.getInstruction();
		Instruction MA_Stage_Ins = EX_MA_Latch.getInstruction();
		Instruction RW_Stage_Ins = MA_RW_Latch.getInstruction();

		// Conflict check due to RAW Hazard
		int ex_rd = -1;
		int ma_rd = -1;
		int rw_rd = -1;
		boolean ex_div = false;
		boolean ma_div = false;
		boolean rw_div = false;
		if (EX_Stage_Ins != null) {
			String ins = EX_Stage_Ins.getOperationType().name();
			if (hazardInstructions.contains(ins)) {
				if (EX_Stage_Ins.getDestinationOperand() != null) {
					ex_rd = EX_Stage_Ins.getDestinationOperand().getValue();
				}
				if (DivisionInstructions.contains(ins)) {
					ex_div = true;
				}
			}
		}
		if (MA_Stage_Ins != null) {
			String ins = MA_Stage_Ins.getOperationType().name();
			if (hazardInstructions.contains(ins)) {
				if (MA_Stage_Ins.getDestinationOperand() != null) {
					ma_rd = MA_Stage_Ins.getDestinationOperand().getValue();
				}
				if (DivisionInstructions.contains(ins)) {
					ma_div = true;
				}
			}
		}
		if (RW_Stage_Ins != null) {
			String ins = RW_Stage_Ins.getOperationType().name();
			if (hazardInstructions.contains(ins)) {
				if (RW_Stage_Ins.getDestinationOperand() != null) {
					rw_rd = RW_Stage_Ins.getDestinationOperand().getValue();
				}
				if (DivisionInstructions.contains(ins)) {
					rw_div = true;
				}
			}
		}
		if (rs1 == ex_rd || rs1 == ma_rd || rs1 == rw_rd || rs2 == ex_rd || rs2 == ma_rd || rs2 == rw_rd) {
			result = true;
		} else {
			result = false;
		}

		// Conflict check due to Division
		if (ex_div || ma_div || rw_div) {
			if (rs1 == 31 || rs2 == 31) {
				System.out.println("Conflict due to Division");
				result = true;
			} else if (!result) {
				result = false;
			}
		}
		System.out.println("Hazard Checking");
		if (ex_rd != -1)
			System.out.println("EX rd = " + ex_rd);
		if (ma_rd != -1)
			System.out.println("MA rd = " + ma_rd);
		if (rw_rd != -1)
			System.out.println("RW rd = " + rw_rd);
		System.out.println("rs1 = " + rs1 + "rs2 = " + rs2);
		return result;
	}

	public void performOF() {
		if (!IF_OF_Latch.isOF_Busy()) {
			if (IF_EnableLatch.isIF_Busy()) {
				OF_EX_Latch.setEX_Busy(true);
			} else {
				if (IF_OF_Latch.isOF_enable()) {

					int Instruction = IF_OF_Latch.getInstruction();
					int currentPC = containingProcessor.getRegisterFile().getProgramCounter() -
							1;
					String binaryInstruction = toBinary(Instruction, 32);

					OperationType[] operationTypes = OperationType.values();
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

					System.out.println("\nOF: " + CO);

					// Creating set of branch instructions
					Set<String> BranchInstructions = new HashSet<String>();
					// Adding the branch instructions
					BranchInstructions.add("jmp");
					BranchInstructions.add("beq");
					BranchInstructions.add("bne");
					BranchInstructions.add("blt");
					BranchInstructions.add("bgt");

					if (BranchInstructions.contains(CO.name())) {
						IF_EnableLatch.setIF_enable(false);
					}

					switch (CO) {
						// below till break correspond to R3I type instructions
						case add:
						case sub:
						case mul:
						case div:
						case and:
						case or:
						case xor:
						case slt:
						case sll:
						case srl:
						case sra:
							rs1.setOperandType(Operand.OperandType.Register);
							registerSource1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
							rs1.setValue(registerSource1);

							rs2.setOperandType(Operand.OperandType.Register);
							registerSource2 = Integer.parseInt((binaryInstruction.substring(10, 15)), 2);
							rs2.setValue(registerSource2);

							rd.setOperandType(Operand.OperandType.Register);
							registerDestination = Integer.parseInt((binaryInstruction.substring(15, 20)),
									2);
							rd.setValue(registerDestination);

							if (isDataHazard(registerSource1, registerSource2)) {
								Statistics.setNumberOfOFUnitStalls(Statistics.getNumberOfOFUnitStalls() + 1);
								this.conflictObserved();
								break;
							}

							CI.setSourceOperand1(rs1);
							CI.setSourceOperand2(rs2);
							CI.setDestinationOperand(rd);
							break;
						// below code till break corresponds to RI type, specifically for jmp
						case jmp:
							registerDestination = Integer.parseInt((binaryInstruction.substring(5, 10)),
									2);
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
						// below code till break corresponds to RI type, specifically for end
						case end:
							IF_EnableLatch.setIF_enable(false);
							break;

						case beq:
						case bne:
						case blt:
						case bgt:
							rs1.setOperandType(Operand.OperandType.Register);
							registerSource1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
							rs1.setValue(registerSource1);

							rs2.setOperandType(Operand.OperandType.Register);
							registerSource2 = Integer.parseInt((binaryInstruction.substring(10, 15)), 2);
							rs2.setValue(registerSource2);

							imm.setOperandType(Operand.OperandType.Immediate);
							immediate = toInteger(binaryInstruction.substring(15, 32));
							imm.setValue(immediate);

							if (isDataHazard(registerSource1, registerSource2)) {
								Statistics.setNumberOfOFUnitStalls(Statistics.getNumberOfOFUnitStalls() + 1);
								this.conflictObserved();
								break;
							}

							CI.setSourceOperand1(rs1);
							CI.setSourceOperand2(rs2);
							CI.setDestinationOperand(imm);
							break;
						// below code till break corresponds to all remaining R2I type instructions
						default:
							rs1.setOperandType(Operand.OperandType.Register);
							registerSource1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
							rs1.setValue(registerSource1);

							rs2.setOperandType(Operand.OperandType.Immediate);
							immediate = toInteger(binaryInstruction.substring(15, 32));
							rs2.setValue(immediate);

							rd.setOperandType(Operand.OperandType.Register);
							registerDestination = Integer.parseInt((binaryInstruction.substring(10, 15)),
									2);
							rd.setValue(registerDestination);

							if (isDataHazard(registerSource1, registerSource1)) {
								Statistics.setNumberOfOFUnitStalls(Statistics.getNumberOfOFUnitStalls() + 1);
								this.conflictObserved();
								break;
							}

							CI.setSourceOperand1(rs1);
							CI.setSourceOperand2(rs2);
							CI.setDestinationOperand(rd);
							break;
					}

					OF_EX_Latch.setInstruction(CI);
					OF_EX_Latch.setEX_enable(true);

					if (!OF_EX_Latch.isEX_Lock()) {
						IF_OF_Latch.setOF_enable(false);
					}

				}
			}
		}
	}
}
