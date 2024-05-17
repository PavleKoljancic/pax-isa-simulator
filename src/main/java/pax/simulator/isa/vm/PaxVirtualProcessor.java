package pax.simulator.isa.vm;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import pax.simulator.isa.InstructionSet;

class PaxVirtualProcessor {

    protected final static int OPERATION_POS = 0;
    protected final static int DATA_REGISTER_POS = 1;
    protected final static int UPPER_VAL_POS = 2;
    protected final static int LOWER_VAL_POS = 3;

    protected long[] GeneralPurposeRegisters; // General Purpose Registers
    protected byte OperationRegister;
    protected long ProgramCounter;
    protected byte DataRegister;
    protected byte ComparisonFlag;
    protected boolean running;
    protected PaxMemory memory;
    Scanner scanner;
    PrintStream stdOut;

    public PaxVirtualProcessor(PaxMemory memory) {
        this(memory, System.in, System.out);

    }

    public PaxVirtualProcessor(PaxMemory memory, InputStream stdIn, PrintStream stdOut) {
        this.memory = memory;
        this.GeneralPurposeRegisters = new long[5];
        this.scanner = new Scanner(stdIn);
        this.ComparisonFlag = 0;
        this.stdOut = stdOut;

    }

    public void run() throws PaxVirtualMachineRunTimeException {
        running = true;
        this.ProgramCounter = 0;
        ByteBuffer fetched = ByteBuffer.allocate(PaxVirtualMachine.INSTRUCTION_SIZE);
        while (running) {
            // Fetching instruction
            fetch(fetched);
            // DECODE
            decode(fetched);
            // EXECUTE
            execute(fetched);

        }
    }

    protected final void execute(ByteBuffer fetched) throws PaxVirtualMachineRunTimeException {
        switch (OperationRegister) {
            case InstructionSet.ADD_CODE:// ADD
                ADD(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.SUB_CODE:// SUB
                SUB(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.MUL_CODE:// MUL
                MUL(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.DIV_CODE:// "DIV"
                DIV(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.AND_CODE:// "AND"
                AND(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.OR_CODE:// "OR"
                OR(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.XOR_CODE:// XOR
                XOR(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.MOV_CODE:// MOV
                MOV(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.SYS_CALL_CODE: // SYS_CALL
                SYS_CALL();
                break;
            case InstructionSet.CMP_CODE:
                CMP(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.JLE_CODE:
                JLE(decodeLowerValue(fetched));
                break;
            case InstructionSet.JL_CODE:
                JL(decodeLowerValue(fetched));
                break;
            case InstructionSet.JE_CODE:
                JE(decodeLowerValue(fetched));
                break;
            case InstructionSet.JNE_CODE:
                JNE(decodeLowerValue(fetched));
                break;
            case InstructionSet.JGE_CODE:
                JGE(decodeLowerValue(fetched));
                break;
            case InstructionSet.JG_CODE:
                JG(decodeLowerValue(fetched));
                break;
            case InstructionSet.JMP_CODE:
                JMP(decodeLowerValue(fetched));
                break;
            case InstructionSet.LOAD_CODE:
                LOAD(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            case InstructionSet.STORE_CODE:
                STORE(fetched.get(UPPER_VAL_POS), decodeLowerValue(fetched));
                break;
            default:

                throw new PaxVirtualMachineRunTimeException("Unknown command exiting program");
        }
    }

    protected final void decode(ByteBuffer fetched) {
        OperationRegister = fetched.get(OPERATION_POS);
        DataRegister = fetched.get(DATA_REGISTER_POS);
    }

    protected final void fetch(ByteBuffer fetched) {
        fetched.clear();
        fetched.put(memory.read(this.ProgramCounter * PaxVirtualMachine.INSTRUCTION_SIZE,
                PaxVirtualMachine.INSTRUCTION_SIZE));
        this.ProgramCounter++;
    }

    private void STORE(int pos, long address) throws PaxVirtualMachineRunTimeException {

        ByteBuffer value = ByteBuffer.allocate(8);
        value.putLong(this.GeneralPurposeRegisters[pos]);
        int size;
        byte[] data;
        switch (this.DataRegister & 0b0000_1111) {
            case 1:
                size = 1;
                data = Arrays.copyOfRange(value.array(), 8-size, 8);
                break;
            case 3:
                size = 2;
                data = Arrays.copyOfRange(value.array(), 8-size, 8);
                break;
            case 5:
                size = 4;
                data = Arrays.copyOfRange(value.array(), 8-size, 8);
                break;
            case 9:
                size = 8;
                data = Arrays.copyOfRange(value.array(), 8-size, 8);
                break;
            default:
                throw new PaxVirtualMachineRunTimeException("Unknown data size");
        } 
        memory.write(address, data);

    }

    private void LOAD(int pos, long address) throws PaxVirtualMachineRunTimeException {

        int size;
        long value;
        switch (this.DataRegister & 0b0000_1111) {
            case 1:
                size = 1;
                value = ByteBuffer.wrap(memory.read(address, size)).get(0);
                break;
            case 3:
                size = 2;
                value = ByteBuffer.wrap(memory.read(address, size)).getShort(0);
                break;
            case 5:
                size = 4;
                value = ByteBuffer.wrap(memory.read(address, size)).getInt(0);
                break;
            case 9:
                size = 8;
                value = ByteBuffer.wrap(memory.read(address, size)).getLong(0);
                break;
            default:
                throw new PaxVirtualMachineRunTimeException("Unknown data size");
        }

        this.GeneralPurposeRegisters[pos] = value;
    }

    private void JMP(long decodeUpperValue) {
        this.ProgramCounter = decodeUpperValue;
    }

    private void JNE(long decodeUpperValue) {
        if (this.ComparisonFlag != 0)
            JMP(decodeUpperValue);
    }

    private void JE(long decodeUpperValue) {
        if (this.ComparisonFlag == 0)
            JMP(decodeUpperValue);
    }

    private void JL(long decodeUpperValue) {
        if (this.ComparisonFlag == -1)
            JMP(decodeUpperValue);
    }

    private void JG(long decodeUpperValue) {
        if (this.ComparisonFlag == 1)
            JMP(decodeUpperValue);
    }

    private void JLE(long decodeUpperValue) {
        if (this.ComparisonFlag != 1)
            JMP(decodeUpperValue);
    }

    private void JGE(long decodeUpperValue) {
        if (this.ComparisonFlag != -1)
            JMP(decodeUpperValue);
    }

    private void CMP(int pos, long decodeLowerValue) {
        if (this.GeneralPurposeRegisters[pos] > decodeLowerValue)
            this.ComparisonFlag = 1;
        else if (this.GeneralPurposeRegisters[pos] < decodeLowerValue)
            this.ComparisonFlag = -1;
        else
            this.ComparisonFlag = 0;
    }

    public void ADD(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] + v2;
    }

    public void SUB(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] - v2;
    }

    public void MUL(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] * v2;
    }

    public void DIV(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] / v2;
    }

    public void AND(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] & v2;
    }

    public void OR(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] | v2;
    }

    public void XOR(int pos, long v2) {
        this.GeneralPurposeRegisters[pos] = this.GeneralPurposeRegisters[pos] ^ v2;
    }

    public void MOV(int pos, long value) {
        this.GeneralPurposeRegisters[pos] = value;
    }

    private void SYS_CALL() {
        if (GeneralPurposeRegisters[0] == 0) { // EXIT
            this.running = false;

        }
        if (GeneralPurposeRegisters[0] == 1) { // STD_OUT
            long lengthInBytes = GeneralPurposeRegisters[1];
            long pos = GeneralPurposeRegisters[2];
            byte[] data = memory.read(pos, (int) lengthInBytes);
            this.stdOut.println(new String(data, StandardCharsets.US_ASCII));

        }
        if (GeneralPurposeRegisters[0] == 2) {

            long address = GeneralPurposeRegisters[1];

            byte[] data = scanner.nextLine().getBytes(StandardCharsets.US_ASCII);
            this.memory.write(address, ByteBuffer.allocate(4).putInt(data.length).array());
            this.memory.write(address + 4, data);
        }
        if (GeneralPurposeRegisters[0] > 2) {
            System.out.println("UNKNOWN SYS_CALL");
        }
    }

    public long decodeLowerValue(ByteBuffer fetched) throws PaxVirtualMachineRunTimeException {
        long val;
        switch (DataRegister & 0b1111_0000) {
            case PaxVirtualMachine.EMPTY_FLAG:
                throw new PaxVirtualMachineRunTimeException("Attempted to access data on EMPTY_FLAG flag!");
            case PaxVirtualMachine.LOWER_LITERAL_FLAG:
                val = fetched.getLong(LOWER_VAL_POS);
                break;
            case PaxVirtualMachine.LOWER_GENERAL_REGISTER_FLAG:
                val = GeneralPurposeRegisters[(int) fetched.getLong(LOWER_VAL_POS)];
                break;
            default:
                throw new PaxVirtualMachineRunTimeException("Unknown data flag!");
        }
        return val;
    }

}
