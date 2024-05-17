package pax.simulator.isa;

import java.util.ArrayList;
import java.util.Arrays;

public class InstructionSet {
    private static String[] BinaryInstructionArray = { "ADD", "SUB", "MUL", "DIV", "AND", "OR", "XOR", "MOV", "CMP" };
    private static String[] UnaryInstructionArray = { "NOT" };
    private static String[] MemoryInstructionsArray = { "LOAD", "STORE" };
    private static String[] ControlFlowInstructionsArray = { "JMP", "JE", "JNE", "JGE", "JL", "JG", "JLE" };
    public static String sysCall = "SYS_CALL";
    private static String[] GeneralRegistersArray = { "R1", "R2", "R3", "R4", "R5" };
    private static String[] MemoryExtensionsArray = { "B", "W", "DW", "QW" };

    private static String[] dataNamesArray = { "b", "w", "dw", "qw" };

    public static ArrayList<String> BinaryInstructions = new ArrayList<>(Arrays.asList(BinaryInstructionArray));
    public static ArrayList<String> UnaryInstructions = new ArrayList<>(Arrays.asList(UnaryInstructionArray));
    public static ArrayList<String> MemoryInstructions = new ArrayList<>(Arrays.asList(MemoryInstructionsArray));
    public static ArrayList<String> ControlFlowInstructions = new ArrayList<>(
            Arrays.asList(ControlFlowInstructionsArray));
    public static ArrayList<String> GeneralRegisters = new ArrayList<>(Arrays.asList(GeneralRegistersArray));
    public static ArrayList<String> MemoryExtensions = new ArrayList<>(Arrays.asList(MemoryExtensionsArray));
    public static ArrayList<String> DataTypes = new ArrayList<>(Arrays.asList(dataNamesArray));

    public static final String dataSection = "DATA";
    public static final String mainSection = "MAIN";
    public static final String LABEL_DEF = "_LABEL:";
    public static final String sectionStart = "SECTION_START";
    public static final String sectionEnd = "SECTION_END";

    // Byte codes
    public static final byte ADD_CODE = 0x00;
    public static final byte SUB_CODE = 0x01;
    public static final byte MUL_CODE = 0x02;
    public static final byte DIV_CODE = 0x03;
    public static final byte AND_CODE = 0x04;
    public static final byte OR_CODE = 0x05;
    public static final byte XOR_CODE = 0x06;
    public static final byte MOV_CODE = 0x07;
    public static final byte NOT_CODE = 0x08;
    public static final byte LOAD_CODE = 0x09;
    public static final byte STORE_CODE = 0x0A;
    public static final byte JMP_CODE = 0x0B;
    public static final byte CMP_CODE = 0x0C;
    public static final byte JE_CODE = 0x0D;
    public static final byte JNE_CODE = 0x0E;
    public static final byte JGE_CODE = 0x0F;
    public static final byte JL_CODE = 0x10;
    public static final byte JG_CODE = 0x11;
    public static final byte JLE_CODE = 0x12;
    public static final byte SYS_CALL_CODE = (byte) 0xff;

    public static Byte OperationToByteCode(String operationString) {
        Byte instructionCode = null;
        switch (operationString) {
            case "ADD":
                instructionCode = ADD_CODE;
                break;
            case "SUB":
                instructionCode = SUB_CODE;
                break;
            case "MUL":
                instructionCode = MUL_CODE;
                break;
            case "DIV":
                instructionCode = DIV_CODE;
                break;
            case "AND":
                instructionCode = AND_CODE;
                break;
            case "OR":
                instructionCode = OR_CODE;
                break;
            case "XOR":
                instructionCode = XOR_CODE;
                break;
            case "MOV":
                instructionCode = MOV_CODE;
                break;
            case "NOT":
                instructionCode = NOT_CODE;
                break;
            case "LOAD":
                instructionCode = LOAD_CODE;
                break;
            case "STORE":
                instructionCode = STORE_CODE;
                break;
            case "JMP":
                instructionCode = JMP_CODE;
                break;
            case "CMP":
                instructionCode = CMP_CODE;
                break;
            case "JE":
                instructionCode = JE_CODE;
                break;
            case "JNE":
                instructionCode = JNE_CODE;
                break;
            case "JGE":
                instructionCode = JGE_CODE;
                break;
            case "JL":
                instructionCode = JL_CODE;
                break;
            case "JG":
                instructionCode = JG_CODE;
                break;
            case "JLE":
                instructionCode = JLE_CODE;
                break;
            case "SYS_CALL":
                instructionCode = SYS_CALL_CODE;
                break;

        }

        return instructionCode;
    }

    public static byte RegisterToByteCode(String Register) {

        return (byte) GeneralRegisters.indexOf(Register);

    }

    public static int getDataSize(String dataDef) {
        int DataSize = 0;
        if ("b".equals(dataDef))
            DataSize = 1;
        if ("w".equals(dataDef))
            DataSize = 2;
        if ("dw".equals(dataDef))
            DataSize = 4;
        if ("qw".equals(dataDef))
            DataSize = 8;
        return DataSize;
    }

}
