package generic;

import java.io.FileInputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.io.IOException;

import generic.Operand.OperandType;

import javax.swing.*;

public class Simulator {

	static FileInputStream inputcodeStream = null;

	public static String toBinaryConv(int x, int leng) {
		if (leng > 0) {
			return String.format("%" + leng + "s",
					// Replacing " " to "0"
					Integer.toBinaryString(x)).replace(" ", "0");
		}
		return null;
	}

	public static String toBinary(Operand ins, int len) {
		int int_ins;
		if (ins == null) {
			int_ins = 0;
		} else if (ins.getOperandType() == OperandType.Label) {
			int_ins = ParsedProgram.symtab.get(ins.getLabelValue());
		} else {
			int_ins = ins.getValue();
		}

		return toBinaryConv(int_ins, len);
	}

	public static void setupSimulation(String assemblyProgramFile) {
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}

	public static void assemble(String objectProgramFile) {
		// String temp_value = "";
		try {
			// 1. open the objectProgramFile in binary mode (.out file)
			OutputStream outputFile = new FileOutputStream(objectProgramFile);
			BufferedOutputStream outputcodeStream = new BufferedOutputStream(outputFile);

			// 2. write the firstCodeAddress to the file
			byte[] byte_addressCode = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			outputcodeStream.write(byte_addressCode);

			// 3. write the data to the file
			for (Integer temp_data : ParsedProgram.data) {
				byte[] byte_data = ByteBuffer.allocate(4).putInt(temp_data).array();
				outputcodeStream.write(byte_data);
			}

			// 4. assemble one instruction at a time, and write to the file
			for (Instruction current_ins : ParsedProgram.code) {
				String binary_ins = "";
				boolean r3_type = false; // r3_type for normal instructions without immediate
				boolean r2i_type = false; // r2 when input has immediate value
				boolean ri_type = false; // ri only for end instructions
				String opCode;
				String opCode_alt;
				Instruction.OperationType ins_string = current_ins.getOperationType();
				switch (ins_string.name()) {

					case "add":
						r3_type = true;
						opCode = "00000";
						opCode_alt = "00000";
						break;

					case "sub":
						r3_type = true;
						opCode = "00010";
						opCode_alt = "00000";
						break;

					case "mul":
						r3_type = true;
						opCode = "00100";
						opCode_alt = "00000";
						break;

					case "div":
						r3_type = true;
						opCode = "00110";
						opCode_alt = "00000";
						break;

					case "and":
						r3_type = true;
						opCode = "01000";
						opCode_alt = "00000";
						break;

					case "or":
						r3_type = true;
						opCode = "01010";
						opCode_alt = "00000";
						break;

					case "xor":
						r3_type = true;
						opCode = "01100";
						opCode_alt = "00000";
						break;

					case "addi":
						r2i_type = true;
						opCode = "00001";
						opCode_alt = "00000";
						break;

					case "subi":
						r2i_type = true;
						opCode = "00011";
						opCode_alt = "00000";
						break;

					case "muli":
						r2i_type = true;
						opCode = "00101";
						opCode_alt = "00000";
						break;

					case "divi":
						r2i_type = true;
						opCode = "00111";
						opCode_alt = "00000";
						break;

					case "andi":
						r2i_type = true;
						opCode = "01001";
						opCode_alt = "00000";
						break;

					case "ori":
						r2i_type = true;
						opCode = "01011";
						opCode_alt = "00000";
						break;

					case "xori":
						r2i_type = true;
						opCode = "01101";
						opCode_alt = "00000";
						break;

					case "slt":
						r3_type = true;
						opCode = "01110";
						opCode_alt = "00000";
						break;

					case "slti":
						r2i_type = true;
						opCode = "01111";
						opCode_alt = "00000";
						break;

					case "sll":
						r3_type = true;
						opCode = "10000";
						opCode_alt = "00000";
						break;

					case "slli":
						r2i_type = true;
						opCode = "10001";
						opCode_alt = "00000";
						break;

					case "srl":
						r3_type = true;
						opCode = "10010";
						opCode_alt = "00000";
						break;

					case "srli":
						r2i_type = true;
						opCode = "10011";
						opCode_alt = "00000";
						break;

					case "sra":
						r3_type = true;
						opCode = "10100";
						opCode_alt = "00000";
						break;

					case "srai":
						r2i_type = true;
						opCode = "10101";
						opCode_alt = "00000";
						break;

					case "load":
						r2i_type = true;
						opCode = "10110";
						break;

					case "store":
						r2i_type = true;
						opCode = "10111";
						opCode_alt = "00000";
						break;

					case "jmp":
						ri_type = true;
						opCode = "11000";
						opCode_alt = "00000";
						break;

					case "beq":
						r2i_type = true;
						opCode = "11001";
						opCode_alt = "00000";
						break;

					case "bne":
						r2i_type = true;
						opCode = "11010";
						opCode_alt = "00000";
						break;

					case "blt":
						r2i_type = true;
						opCode = "11011";
						opCode_alt = "00000";
						break;

					case "bgt":
						r2i_type = true;
						opCode = "11100";
						opCode_alt = "00000";
						break;

					case "end":
						ri_type = true;
						opCode = "11101";
						opCode_alt = "00000";
						break;

					default:
						opCode = "";
						opCode_alt = "00000";
						break;

				}
				if (r3_type) {
					// binary_ins is a string = concatenation of opcode + rs1 + rs2 + rd + unused
					// bits
					binary_ins += opCode;
					Operand rs1 = current_ins.getSourceOperand1();
					Operand rs2 = current_ins.getSourceOperand2();
					Operand rd = current_ins.getDestinationOperand();

					String rd_value = toBinary(rd, 5);
					String rs1_value = toBinary(rs1, 5);
					String rs2_value = toBinary(rs2, 5);
					String unused_bits = toBinaryConv(0, 12); // as there are no immediate value we
					// assign 0s to all the 12 unused bits.

					binary_ins += (rs1_value + rs2_value + rd_value + unused_bits);
				} else if (r2i_type) {
					binary_ins += opCode;
					int pc = current_ins.getProgramCounter();
					Operand rs1 = current_ins.getSourceOperand1();
					Operand rs2 = current_ins.getSourceOperand2();
					Operand rd = current_ins.getDestinationOperand();

					String rs1_value = toBinary(rs1, 5);
					String imm_value;
					String rs2_value;
					String rd_value;
					String temp_value;

					// if the instruction is branch
					// syntax of branch in ToyRISC: beq %x6, %x3, <branchName>
					// 11001-beq; 11010-bne; 11011-bne; 11100-bgt
					if (opCode.equals("11001") || opCode.equals("11010") || opCode.equals("11011")
							|| opCode.equals("11100")) {
						rs2_value = toBinary(rs2, 5);
						imm_value = toBinary(rd, 5);
						assert imm_value != null;
						// imm_value is a binary string of length 5, so parseInt will convert binary to
						// Integer and assign the value to imm_value_int
						int imm_value_int = Integer.parseInt(imm_value, 2) - pc;
						String imm_temp = toBinaryConv(imm_value_int, 17);
						// temp_value = "Check";
						String imm_value2 = imm_temp.substring(imm_temp.length() - 17);
						binary_ins += (rs1_value + rs2_value + imm_value2);
					}

					// if instructions in load or store
					else {
						rs2_value = toBinary(rs2, 17);
						rd_value = toBinary(rd, 5);
						// temp_value = "Check";
						binary_ins += (rs1_value + rd_value + rs2_value);
					}
				} else if (ri_type) {
					binary_ins += opCode;
					Operand rd = current_ins.getDestinationOperand();
					// temp_value = "Check";
					int pc = current_ins.getProgramCounter();

					// if instruction is jmp in ToyRISC (branch in SimpleRISC)
					if (opCode.equals("11000")) {
						String unused_bits = toBinaryConv(0, 5);
						String rd_value = toBinary(rd, 5);
						assert rd_value != null;
						int rd_value_int = Integer.parseInt(rd_value, 2) - pc;
						String rd_temp = toBinaryConv(rd_value_int, 22);
						// temp_value = "Check";
						String rd_value2 = rd_temp.substring(rd_temp.length() - 22);
						binary_ins += (unused_bits + rd_value2);
					}

					// if Instruction is end
					else if (opCode.equals("11101")) {
						String unused_bits = toBinaryConv(0, 27);
						// temp_value = "Check";
						binary_ins += (unused_bits);
					}
				} else {
					continue;
				}
				int int_ins = (int) Long.parseLong(binary_ins, 2);
				byte[] instBinary = ByteBuffer.allocate(4).putInt(int_ins).array();
				// temp_value = "Check";
				outputcodeStream.write(instBinary);
			}
			// 5. close the file
			outputcodeStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
