package processor.pipeline;

import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;

public class InstructionFetch implements Element {

	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;

	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch,
			IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch,
			EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}

	public void performIF() {
		if (!IF_EnableLatch.isIF_Busy()) {
			if (IF_EnableLatch.isIF_enable()) {
				if (EX_IF_Latch.isEX_IF_enable()) {
					int branch_PC = EX_IF_Latch.getPC();
					containingProcessor.getRegisterFile().setProgramCounter(branch_PC);
					EX_IF_Latch.setEX_IF_enable(false);
				}
				Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() + 1);
				int current_PC = containingProcessor.getRegisterFile().getProgramCounter();

				Simulator.getEventQueue().addEvent(
						new MemoryReadEvent(
								Clock.getCurrentTime() + Configuration.mainMemoryLatency,
								this,
								containingProcessor.getMainMemory(),
								containingProcessor.getRegisterFile().getProgramCounter()));
				IF_EnableLatch.setIF_Busy(true);
				containingProcessor.getRegisterFile().setProgramCounter(current_PC + 1);
			}
		}
	}

	@Override
	public void handleEvent(Event e) {

		if (IF_OF_Latch.isOF_Busy()) {
			System.out.println("IF_OF Latch is Busy");
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		}

		else if (e.getEventType() == Event.EventType.MemoryResponse) {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			IF_OF_Latch.setInstruction(event.getValue());

			IF_EnableLatch.setIF_Busy(false);
			IF_OF_Latch.setOF_enable(true);
		}
	}
}
