package pax.simulator.isa.vm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import pax.simulator.isa.compiler.Compiler;
import pax.simulator.isa.compiler.CompilerException;

public class PaxVirtualMachine {
    public final static int INSTRUCTION_SIZE = 1 + 1 + 1 + 8; // 11
    public final static byte EMPTY_FLAG = 0b0000_0000;
    public final static byte UPPER_GENERAL_REGISTER_FLAG = 0b0000_0001;

    public final static byte MEMORY_BYTE_FLAG = 0b0000_0001;
    public final static byte MEMORY_WORD_FLAG = 0b0000_0010;
    public final static byte MEMORY_DOUBLE_WORD_FLAG = 0b0000_0100;
    public final static byte MEMORY_QUAD_WORD_FLAG = 0b0000_1000;

    public final static byte LOWER_GENERAL_REGISTER_FLAG = 0b0001_0000;
    public final static byte LOWER_LITERAL_FLAG = 0b0010_0000;

    PaxMemory memory;
    PaxVirtualProcessor processor;

    public PaxVirtualMachine() {
        memory = new PaxMemory();
        processor = new PaxVirtualProcessor(memory);
    }

    public PaxVirtualMachine(ArrayList<String> SourceCode) {
        memory = new PaxMemory();
        processor = new PaxVirtualProcessorDebugMode(memory, SourceCode);
    }

    public void run(byte[] program) throws PaxVirtualMachineRunTimeException {
        this.memory.write(0, program);
        this.processor.run();
    }

    public static void main(String[] args) throws PaxVirtualMachineRunTimeException, IOException, CompilerException {

       /* if (args.length == 1) {
            PaxVirtualMachine vm = new PaxVirtualMachine();
            vm.run(Files.readAllBytes(Paths.get(args[0])));
        } else if (args.length == 3 && args[0].equals("-d")) {
        */
            Compiler compiler = new Compiler(Paths.get("/media/pavle/ssd2/FAKS/3/AR - Arhitektura racunara/Projekat 1/pax/src/test/java/samoModifikujuciPrimjer.pax"));
            compiler.compile();
            PaxVirtualMachine vm = new PaxVirtualMachine(compiler.getUnprocessedMainSection());
            vm.run(Files.readAllBytes(Paths.get("/media/pavle/ssd2/FAKS/3/AR - Arhitektura racunara/Projekat 1/pax/res")));

        //}

    }

}