package pax.simulator.isa.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pax.simulator.isa.InstructionSet;
import pax.simulator.isa.vm.PaxVirtualMachine;

class Parser {

    private ArrayList<String> ProcessedMainSection;
    private ArrayList<String> MainSection;

    private ArrayList<String> dataSection;
    private HashMap<String, Long> DefinedAddresses;

    public ArrayList<String> getProcessedMainSection() {
        return ProcessedMainSection;
    }

    public ArrayList<String> getMainSection() {
        return MainSection;
    }

    public ArrayList<String> getDataSection() {
        return dataSection;
    }

    public HashMap<String, Long> getDefinedAddresses() {
        return DefinedAddresses;
    }

    Parser(List<String> lines) throws CompilerException {
        MainSection = null;
        if (lines.size() == 0)
            System.out.println("The provided file is empty");
        lines = lines.stream().filter(l -> !l.isEmpty()).toList();
        ArrayList<ArrayList<String>> Sections = this.getSections(lines);
        if (Sections.size() > 2)
            throw new CompilerException("More then 2 sections defined!");

        for (ArrayList<String> section : Sections) {
            if (InstructionSet.dataSection.equals(section.get(0).split(" ")[1]))
                dataSection = section;
            if (InstructionSet.mainSection.equals(section.get(0).split(" ")[1]))
                MainSection = section;
        }
        this.DefinedAddresses = new HashMap<>();
        if (MainSection == null)
            throw new CompilerException("Error no MAIN section defined!");
        this.ProcessedMainSection = this.processSection(MainSection);

        Long dataSegmentOffset = (long) (ProcessedMainSection.size() * PaxVirtualMachine.INSTRUCTION_SIZE);
        DefinedAddresses.putAll(getDataDefinitionAddresses(dataSegmentOffset));
    }

    private ArrayList<ArrayList<String>> getSections(List<String> lines) throws CompilerException {
        ArrayList<ArrayList<String>> section = new ArrayList<>();

        int lineSectionStart = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(InstructionSet.sectionStart)) {
                lineSectionStart = i;
                section.add(new ArrayList<String>());
                section.get(section.size() - 1).add(lines.get(i));
                boolean sectionEnd = false;
                i++;
                while (lines.size() > i && sectionEnd == false) {
                    section.get(section.size() - 1).add(lines.get(i));
                    if (lines.get(i).startsWith(InstructionSet.sectionEnd))
                        sectionEnd = true;
                    else
                        i++;
                }
                if (!sectionEnd) {
                    throw new CompilerException("Section error on line:" + lineSectionStart + " section:"
                            + section.get(section.size() - 1).get(0) + " started but didn't end!");
                }

            }
        }

        return section;
    }

    private ArrayList<String> processSection(ArrayList<String> section)
            throws CompilerException {
        ArrayList<String> processedSection = new ArrayList<>();

        for (int i = 1; i < section.size() - 1; i++) {
            String line = section.get(i);
            if (line.startsWith(InstructionSet.LABEL_DEF))

                this.DefinedAddresses.put(line.split(":")[1], (long) processedSection.size());
            else

                processedSection.add(processInstruction(line));

        }
        return processedSection;
    }

    private HashMap<String, Long> getDataDefinitionAddresses(long offset)
            throws CompilerException {
        HashMap<String, Long> DataDefinitionAddressesInCodeSegment = new HashMap<>();
        if (this.dataSection == null)
            return DataDefinitionAddressesInCodeSegment;
        if (!dataSection.get(0).startsWith("SECTION_START DATA"))
            throw new CompilerException("The section provided is not the data section!");

        Long startAddress = offset;
        for (int i = 1; i < dataSection.size() - 1; i++) {
            String[] parts = dataSection.get(i).split(",");
            // { "b", "w", "dw", "qw" };
            int DataSize = InstructionSet.getDataSize(parts[1]);
            if (DataSize == 0)
                throw new CompilerException("In DATA section on line:" + i + " data definition missing type");
            Long dataLength = Long.decode(parts[2]);
            if (dataLength < 1)
                throw new CompilerException("In DATA section on line:" + i + " improper data length");
            DataDefinitionAddressesInCodeSegment.put(parts[0], startAddress);

            startAddress += DataSize * (dataLength);

        }

        return DataDefinitionAddressesInCodeSegment;
    }

    private String processInstruction(String line) throws CompilerException {
        String parts[] = line.split(" ");

        String operator = parts[0];
        if (InstructionSet.sysCall.equals(operator)) {
            return processSysCall(parts, operator);

        }
        String operands[];
        if (parts.length > 1)
            operands = parts[1].split(",");
        else
            throw new CompilerException("Mising operands");
        if (InstructionSet.UnaryInstructions.contains(operator))
            return processUnaryOperator(operator, operands);

        if (InstructionSet.BinaryInstructions.contains(operator))
            return processBinaryOperator(operator, operands);

        if (InstructionSet.MemoryInstructions.contains(operator))
            return processMemoryInstruction(operator, operands);

        if (InstructionSet.ControlFlowInstructions.contains(operator))
            return processControlFlowOperation(operator, operands);
        throw new CompilerException(operator + " unknown operand ");
    }

    private String processControlFlowOperation(String operator, String[] operands) throws CompilerException {
        if (operands.length != 1)
            throw new CompilerException(
                    operator + " control flow operators can have one operand an address or label");
        return operator + "," + PaxVirtualMachine.LOWER_LITERAL_FLAG + ",0," + operands[0];

    }

    private String processMemoryInstruction(String operator, String[] operands) throws CompilerException {
        if (InstructionSet.GeneralRegisters.contains(operands[0].substring(0, 2))) {
            byte MEMORY_FLAG;
            switch (operands[0].substring(2)) {
                case "B":
                    MEMORY_FLAG = PaxVirtualMachine.MEMORY_BYTE_FLAG;
                    break;
                case "W":
                    MEMORY_FLAG = PaxVirtualMachine.MEMORY_WORD_FLAG;
                    break;
                case "DW":
                    MEMORY_FLAG = PaxVirtualMachine.MEMORY_DOUBLE_WORD_FLAG;
                    break;
                case "QW":
                    MEMORY_FLAG = PaxVirtualMachine.MEMORY_QUAD_WORD_FLAG;
                    break;
                default:
                    throw new CompilerException("Memory operation but no register extension provided.");
            }

            byte LOWER_FLAG;
            if (InstructionSet.GeneralRegisters.contains(operands[1]))
                LOWER_FLAG = PaxVirtualMachine.LOWER_GENERAL_REGISTER_FLAG;
            else
                LOWER_FLAG = PaxVirtualMachine.LOWER_LITERAL_FLAG;
            if (operands[1].startsWith("&"))
                operands[1] = operands[1].substring(1);

            return (operator + ","
                    + (PaxVirtualMachine.UPPER_GENERAL_REGISTER_FLAG | MEMORY_FLAG | LOWER_FLAG) + "," + operands[0]
                    + ","
                    + operands[1]);
        } else
            throw new CompilerException(
                    operator + "memory operators have to have a register and an address ");
    }

    private String processBinaryOperator(String operator, String[] operands) throws CompilerException {
        if (operands.length != 2)
            throw new CompilerException(operator + "is binary and must have 2 operands ");
        else if ((!InstructionSet.GeneralRegisters.contains(operands[0])))
            throw new CompilerException(operator + "first operand must be a register ");
        else {

            byte LOWER_FLAG;
            if (InstructionSet.GeneralRegisters.contains(operands[1]))
                LOWER_FLAG = PaxVirtualMachine.LOWER_GENERAL_REGISTER_FLAG;
            else if (operands[1].startsWith("&")) {
                LOWER_FLAG = PaxVirtualMachine.LOWER_LITERAL_FLAG;
                operands[1] = operands[1].substring(1);

            }

            else {
                LOWER_FLAG = PaxVirtualMachine.LOWER_LITERAL_FLAG;
                operands[1] = "" + Long.decode(operands[1]);
            }

            String temp = operator + "," + (LOWER_FLAG | PaxVirtualMachine.UPPER_GENERAL_REGISTER_FLAG) + ","
                    + operands[0] + ","
                    + operands[1];
            return (temp);

        }
    }

    private String processUnaryOperator(String operator, String[] operands) throws CompilerException {
        if (operands.length != 1)
            throw new CompilerException(operator + "is unary and must have 1 operand ");
        else if (!InstructionSet.GeneralRegisters.contains(operands[0]))
            throw new CompilerException(operator + "is unary and has no register provided ");
        else
            return (operator + "," + (PaxVirtualMachine.UPPER_GENERAL_REGISTER_FLAG) + "," + operands[0]);
    }

    private String processSysCall(String[] parts, String operator) throws CompilerException {
        if (parts.length > 1)
            throw new CompilerException(
                    "SYS_CALL cannot be followed by any operators line");
        return (operator + "," + PaxVirtualMachine.EMPTY_FLAG);
    }

}
