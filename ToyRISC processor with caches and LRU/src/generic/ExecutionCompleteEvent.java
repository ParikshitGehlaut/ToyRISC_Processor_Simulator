package generic;

public class ExecutionCompleteEvent extends Event {

	int ALU_Result;

	public ExecutionCompleteEvent(long eventTime, Element requestingElement, Element processingElement,
			int ALU_Result) {
		super(eventTime, EventType.ExecutionComplete, requestingElement, processingElement);
		this.ALU_Result = ALU_Result;
	}

	public int getALU_Result() {
		return ALU_Result;
	}
}
