package processor.pipeline;

public class IF_EnableLatchType {

	boolean IF_enable;
	boolean isIF_Busy;

	public IF_EnableLatchType() {
		IF_enable = true;
	}

	public boolean isIF_enable() {
		return IF_enable;
	}

	public void setIF_enable(boolean iF_enable) {
		IF_enable = iF_enable;
	}

	public boolean isIF_Busy() {
		return isIF_Busy;
	}

	public void setIF_Busy(boolean if_busy) {
		isIF_Busy = if_busy;
	}
}
