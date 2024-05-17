package pax.simulator.isa.vm;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import pax.simulator.isa.InstructionSet;

public class PaxVirtualProcessorDebugMode extends PaxVirtualProcessor {

    private boolean stopAtNextInstruction;
    private ArrayList<Long> breakpoints;
    Scanner scan;
    MemorizedPrintStream memorizedPrintStream;
    ArrayList<String> SourceCode;
    long watchAddress;
    int watchLength;

    public PaxVirtualProcessorDebugMode(PaxMemory memory, ArrayList<String> SourceCode) {
        super(memory, System.in, new MemorizedPrintStream());
        this.breakpoints = new ArrayList<>();
        stopAtNextInstruction = true;
        scan = new Scanner(System.in);
        memorizedPrintStream = (MemorizedPrintStream) super.stdOut;
        this.SourceCode = SourceCode;
    }

    @Override
    public void run() throws PaxVirtualMachineRunTimeException {
        running = true;
        this.ProgramCounter = 0;
        ByteBuffer fetched = ByteBuffer.allocate(PaxVirtualMachine.INSTRUCTION_SIZE);
        while (running) {
            printGeneral();
            menuPrint();
            if (this.stopAtNextInstruction || breakpoints.contains(ProgramCounter)) {

                stopAtNextInstruction = false;

                menu();
            }
            // Fetching instruction
            super.fetch(fetched);
            // DECODE
            super.decode(fetched);
            // EXECUTE
            super.execute(fetched);

        }

    }

    private void menuPrint() {
        System.out.println("1.Next instruction.");
        System.out.println("2.Add breakpoint.");
        System.out.println("3.Remove breakpoint.");
        System.out.println("4.Set memory watch.");
        System.out.println("5.Continue.");
        System.out.print("Choice:");

    }

    private void menu() {
        int choice = -1;

        boolean exit = false;
        while (!exit) {


            choice = scan.nextInt();
            switch (choice) {
                case 1:
                    exit = true;
                    this.stopAtNextInstruction = true;
                    break;

                case 2:
                    addBreakpoint();
                    break;

                case 3:
                    removeBreakpoint();
                    break;

                case 4:
                    setMemoryWatch();
                    break;

                case 5:
                    exit = true;
                    break;

                default:
                    System.out.println("Unknown option!");
                    break;
            }
            if (!exit) {
                printGeneral();
                menuPrint();
            }
        }

    }

    private void printMemory() {
        if(this.watchLength>0)
        System.out.println("Memory Address:"+this.watchAddress+
        " " + Arrays.toString(this.memory.read(this.watchAddress, this.watchLength))
        );
        else System.out.println("No memory is being watched!");
        
    }
    private void setMemoryWatch() {
        scan.nextLine();
        System.out.print("Memory Address:");
        this.watchAddress = Long.parseUnsignedLong(scan.nextLine());
        System.out.print("Memory length:");
        this.watchLength = scan.nextInt();
    }
    private void printGeneral() {
        System.out.print("\033\143");
        System.out.println("Program counter:" + this.ProgramCounter);
        System.out.println("Comparison flag:" + this.ComparisonFlag);
        String binFlags = String.format("%8s", Integer.toBinaryString(this.DataRegister & 0xFF)).replace(' ', '0');
        System.out.println("Data flags:0b" + binFlags);

        System.out.println("Operation register:" + this.OperationRegister);
        printGeneralRegisters();
        System.out.println();
        printSourceCode();

        System.out.println();
        printMemory();
        System.out.println();
        System.out.println("Output so far:");
        this.memorizedPrintStream.printPast();
        System.out.println("===========================");

    }

    private void addBreakpoint() {
        System.out.print("Breakpoint:");
        this.breakpoints.add(scan.nextLong());
    }

    private void removeBreakpoint() {

        System.out.print("Breakpoint to remove:");

        this.breakpoints.remove(scan.nextLong());

    }

    private void printGeneralRegisters() {
        System.out.println("R1:" + this.GeneralPurposeRegisters[0] + " R2:"
                + this.GeneralPurposeRegisters[1] + " R3:" + this.GeneralPurposeRegisters[2] +
                " R4:" + this.GeneralPurposeRegisters[3] + " R5:" + this.GeneralPurposeRegisters[4]);
    }

    private void printSourceCode() {
        int line = 0;
        for (int i = 1; i < this.SourceCode.size() - 1; i++) {
            if (this.SourceCode.get(i).startsWith(InstructionSet.LABEL_DEF) || this.SourceCode.get(i).isBlank())
                System.out.println(this.SourceCode.get(i));
            else if (line == this.ProgramCounter) {
                System.out.println("\033[41m" + line + " " + this.SourceCode.get(i)); // RED BOLD COLOR
                System.out.print("\033[0m"); // RESET
                line++;
            } else if (this.breakpoints.contains(((long) line))) {
                System.out.println("\033[0;34m" + line + " " + this.SourceCode.get(i)); // BLUE COLOR
                System.out.print("\033[0m"); // RESET
                line++;
            } else {
                System.out.println(line + " " + this.SourceCode.get(i));
                line++;
            }
        }
    }

}
