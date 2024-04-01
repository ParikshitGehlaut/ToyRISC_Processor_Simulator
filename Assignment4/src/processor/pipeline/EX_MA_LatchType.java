package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {

	boolean MA_enable;
	Instruction instruction;
	int aluResult;
	boolean MA_Lock;

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return this.instruction;
	}

	public void setAluResult(int ALU_Result) {
		this.aluResult = ALU_Result;
	}

	public int getAluResult() {
		return this.aluResult;
	}

	public EX_MA_LatchType() {
		MA_enable = false;
		MA_Lock = false;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}

	public boolean isMA_Lock() {
		return MA_Lock;
	}

	public void setMA_Lock(boolean mA_Lock) {
		this.MA_Lock = mA_Lock;
	}

}