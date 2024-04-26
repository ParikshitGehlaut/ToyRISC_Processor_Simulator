package processor.pipeline;

import generic.Instruction;

public class MA_RW_LatchType {

	boolean RW_enable;
	Instruction instruction;
	int aluResult;
	int ldResult;

	public MA_RW_LatchType() {
		RW_enable = false;
	}

	// getting value load value from MA unit
	public void setLdResult(int ld_Result) {
		this.ldResult = ld_Result;
	}

	// RW unit uses this function to get load value
	public int getLdResult() {
		return this.ldResult;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}

	// set aluresult
	public void setAluResult(int Alu_result) {
		this.aluResult = Alu_result;
	}

	// function to get aluresult
	public int getAluResult() {
		return this.aluResult;
	}

	// set instructions
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	// function to get instructions
	public Instruction getInstruction() {
		return this.instruction;
	}

}
