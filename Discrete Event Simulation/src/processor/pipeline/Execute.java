package processor.pipeline;

import generic.*;
import processor.Clock;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import java.util.*;

import configuration.Configuration;

public class Execute implements Element {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	public Instruction inst;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch,
			EX_MA_LatchType eX_MA_Latch,
			EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch) {
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;

	}

	public void performEX() {
		if (OF_EX_Latch.isEX_Busy()) {
			IF_OF_Latch.setOF_Busy(true);
		} else {
			IF_OF_Latch.setOF_Busy(false);

			if (OF_EX_Latch.isEX_Lock()) {
				EX_MA_Latch.setMA_Lock(true);
				OF_EX_Latch.setEX_Lock(false);
				EX_MA_Latch.setInstruction(null);
				OF_EX_Latch.setEX_enable(false);
			} else if (OF_EX_Latch.isEX_enable()) {

				OF_EX_Latch.setEX_enable(false);
				Instruction CI = OF_EX_Latch.getInstruction();
				inst = CI;
				System.out.println("Current Instruction in EX: " + CI);
				int currentPC = CI.getProgramCounter();
				int ALU_Result = -1;
				OperationType CO = CI.getOperationType();
				int Op1, Op2, immx, remainder;

				ArrayList<String> branchInstruction = new ArrayList<>();
				branchInstruction.add("beq");
				branchInstruction.add("bne");
				branchInstruction.add("bgt");
				branchInstruction.add("blt");
				branchInstruction.add("jmp");

				if (branchInstruction.contains(CO.name())) {
					IF_EnableLatch.setIF_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.setEX_enable(false);
				}

				if (CO == OperationType.add) {
					// Code for add case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 + Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.addi) {
					// Code for addi case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 + immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.sub) {
					// Code for sub case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 - Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.subi) {
					// Code for subi case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 - immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.mul) {
					// Code for mul case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 * Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.multiplier_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.muli) {
					// Code for muli case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 * immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.multiplier_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.div) {
					// Code for div case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 / Op2;
					remainder = (Op1 % Op2);
					containingProcessor.getRegisterFile().setValue(31, remainder);
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.divider_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.divi) {
					// Code for divi case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 / immx;
					remainder = (Op1 % immx);
					containingProcessor.getRegisterFile().setValue(31, remainder);
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.divider_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.and) {
					// Code for and case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 & Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.andi) {
					// Code for andi case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 & immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.or) {
					// Code for or case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 | Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.ori) {
					// Code for ori case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 | immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.xor) {
					// Code for xor case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 ^ Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.xori) {
					// Code for xori case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 ^ immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.slt) {
					// Code for slt case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					if (Op1 < Op2)
						ALU_Result = 1;
					else
						ALU_Result = 0;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.slti) {
					// Code for slti case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					if (Op1 < immx)
						ALU_Result = 1;
					else
						ALU_Result = 0;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.sll) {
					// Code for sll case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 << Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.slli) {
					// Code for slli case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 << immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.srl) {
					// Code for srl case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 >>> Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.srli) {
					// Code for srli case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 >>> immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.sra) {
					// Code for sra case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					ALU_Result = Op1 >> Op2;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.srai) {
					// Code for srai case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 >> immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.load) {
					// Code for load case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 + immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.store) {
					// Code for store case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getDestinationOperand().getValue());
					immx = CI.getSourceOperand2().getValue();
					ALU_Result = Op1 + immx;
					Simulator.getEventQueue().addEvent(
							new ExecutionCompleteEvent(Clock.getCurrentTime() + Configuration.ALU_latency, this,
									this, ALU_Result));
					OF_EX_Latch.setEX_Busy(true);
				} else if (CO == OperationType.jmp) {
					// Code for jmp case

					// Subtract 2 from number of instructions
					Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() - 2);
					// Writing number of wrong instruction that entered the pipeline
					Statistics.setNumberOfWrongInstructions(Statistics.getNumberOfWrongInstructions() + 2);
					OperandType jump = CI.getDestinationOperand().getOperandType();
					if (jump == OperandType.Register) {
						immx = containingProcessor.getRegisterFile()
								.getValue(CI.getDestinationOperand().getValue());
					} else {
						immx = CI.getDestinationOperand().getValue();
					}
					ALU_Result = currentPC + immx;
					EX_IF_Latch.setEX_IF_enable(true, ALU_Result);
					EX_MA_Latch.setMA_enable(true);
				} else if (CO == OperationType.beq) {
					// Code for beq case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					immx = CI.getDestinationOperand().getValue();
					if (Op1 == Op2) {
						// Writing number of wrong instruction that entered the pipeline
						Statistics.setNumberOfWrongInstructions(Statistics.getNumberOfWrongInstructions() + 2);
						// Subtract 2 from number of instructions
						Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() - 2);
						ALU_Result = currentPC + immx;
						EX_IF_Latch.setEX_IF_enable(true, ALU_Result);
					}
					EX_MA_Latch.setMA_enable(true);
				} else if (CO == OperationType.bne) {
					// Code for bne case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					immx = CI.getDestinationOperand().getValue();
					if (Op1 != Op2) {
						// Subtract 2 from number of instructions
						Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() - 2);
						// Writing number of wrong instruction that entered the pipeline
						Statistics.setNumberOfWrongInstructions(Statistics.getNumberOfWrongInstructions() + 2);
						ALU_Result = currentPC + immx;
						EX_IF_Latch.setEX_IF_enable(true, ALU_Result);
					}
					EX_MA_Latch.setMA_enable(true);
				} else if (CO == OperationType.blt) {
					// Code for blt case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					immx = CI.getDestinationOperand().getValue();
					if (Op1 < Op2) {
						// Subtract 2 from number of instructions
						Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() - 2);
						// Writing number of wrong instruction that entered the pipeline
						Statistics.setNumberOfWrongInstructions(Statistics.getNumberOfWrongInstructions() + 2);
						ALU_Result = currentPC + immx;
						EX_IF_Latch.setEX_IF_enable(true, ALU_Result);
					}
					EX_MA_Latch.setMA_enable(true);
				} else if (CO == OperationType.bgt) {
					// Code for bgt case
					Op1 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand1().getValue());
					Op2 = containingProcessor.getRegisterFile()
							.getValue(CI.getSourceOperand2().getValue());
					immx = CI.getDestinationOperand().getValue();
					if (Op1 > Op2) {
						// Subtract 2 from number of instructions
						Simulator.setNoOfInstructions(Simulator.getNoOfInstructions() - 2);
						// Writing number of wrong instruction that entered the pipeline
						Statistics.setNumberOfWrongInstructions(Statistics.getNumberOfWrongInstructions() + 2);
						ALU_Result = currentPC + immx;
						EX_IF_Latch.setEX_IF_enable(true, ALU_Result);
					}
					EX_MA_Latch.setMA_enable(true);
				} else if (CO == OperationType.end) {
					// Code for end case
					EX_MA_Latch.setMA_enable(true);
					System.out.println("\nEnd Instruction");
				} else {
					// Default case
					System.out.println("\nInstruction not defined");
				}

				// EX_MA_Latch.setAluResult(ALU_Result);
				EX_MA_Latch.setInstruction(CI);

				OF_EX_Latch.setEX_enable(false);
				System.out.println("\tEX" + CI);

			}
		}
	}

	@Override
	public void handleEvent(Event e) {
		if (EX_MA_Latch.isMA_Busy()) {
			System.out.println("MA Unit is Busy");
			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		} else if (e.getEventType() == Event.EventType.ExecutionComplete) {
			ExecutionCompleteEvent event = (ExecutionCompleteEvent) e;
			int alu_Result = event.getALU_Result();
			EX_MA_Latch.setAluResult(alu_Result);
			EX_MA_Latch.setInstruction(inst);

			OF_EX_Latch.setEX_Busy(false);
			EX_MA_Latch.setMA_enable(true);
			IF_OF_Latch.setOF_Busy(false);
			System.out.println("EX Event Handled");
			OF_EX_Latch.setEX_enable(false);
		}
	}
}