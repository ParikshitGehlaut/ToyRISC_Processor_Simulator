package processor.pipeline;

public class IF_OF_LatchType {

	boolean OF_enable;
	int instruction;
	boolean isOF_busy;

	public IF_OF_LatchType() {
		OF_enable = false;
		isOF_busy = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public boolean isOF_Busy() {
		return isOF_busy;
	}

	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	public void setOF_Busy(boolean isoF_busy) {
		isOF_busy = isoF_busy;
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

}
