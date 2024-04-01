package processor.pipeline;

import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;
import generic.Instruction.OperationType;

public class MemoryAccess implements Element {
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	public EX_MA_LatchType EX_MA_Latch;
	public MA_RW_LatchType MA_RW_Latch;
	public Instruction inst;

	public MemoryAccess(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch,
			OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch,
			MA_RW_LatchType mA_RW_Latch) {
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}

	public void performMA() {
		if (EX_MA_Latch.isMA_Busy()) {
			OF_EX_Latch.setEX_Busy(true);
		} else {
			OF_EX_Latch.setEX_Busy(false);
			if (EX_MA_Latch.isMA_Lock()) {
				MA_RW_Latch.setRW_Lock(true);
				MA_RW_Latch.setInstruction(null);
				EX_MA_Latch.setMA_Lock(false);
			} else if (EX_MA_Latch.isMA_enable()) {

				Instruction CI = EX_MA_Latch.getInstruction(); // CI stands for Current Instruction
				inst = CI;
				int aluResult = EX_MA_Latch.getAluResult();
				MA_RW_Latch.setInstruction(CI);
				OperationType CO = CI.getOperationType(); // CO stands for Current operation

				if (CO == OperationType.load) {
					Simulator.getEventQueue().addEvent(
							new MemoryReadEvent(
									Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(),
									this,
									containingProcessor.getL1dCache(),
									aluResult));
					EX_MA_Latch.setMA_Busy(true);
					return;
				} else if (CO == OperationType.store) {
					// if it is store instruction
					int store_value = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Simulator.getEventQueue().addEvent(
							new MemoryWriteEvent(
									Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(),
									this,
									containingProcessor.getL1dCache(),
									EX_MA_Latch.getAluResult(),
									store_value));

					EX_MA_Latch.setMA_Busy(true);
					return;
				}
				if (CO == OperationType.end) {
					IF_EnableLatch.setIF_enable(false);
				}

				MA_RW_Latch.setAluResult(aluResult);
				MA_RW_Latch.setInstruction(CI);
				MA_RW_Latch.setRW_enable(true);
				EX_MA_Latch.setMA_enable(false);
			}
		}
	}

	@Override
	public void handleEvent(Event e) {
		if (e.getEventType() == Event.EventType.MemoryResponse) {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			int ldResult = event.getValue();
			MA_RW_Latch.setLdResult(ldResult);
			MA_RW_Latch.setInstruction(inst);

			EX_MA_Latch.setMA_Busy(false);
			MA_RW_Latch.setRW_enable(true);
			OF_EX_Latch.setEX_Busy(false);
			EX_MA_Latch.setMA_enable(false);
		}
	}
}
