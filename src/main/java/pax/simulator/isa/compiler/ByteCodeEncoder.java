package pax.simulator.isa.compiler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import pax.simulator.isa.InstructionSet;
import pax.simulator.isa.vm.PaxVirtualMachine;

public class ByteCodeEncoder {

    ArrayList<String> ProcessedMainSection;
    ArrayList<String> dataSection;
    HashMap<String, Long> DefinedAddresses;

    public ByteCodeEncoder(ArrayList<String> processedMainSection, ArrayList<String> dataSection,
            HashMap<String, Long> definedAddresses) {
        ProcessedMainSection = processedMainSection;
        this.dataSection = dataSection;
        DefinedAddresses = definedAddresses;
    }

    ArrayList<byte[]> buildByteCode() throws CompilerException {
        ArrayList<byte[]> res = codeSectionToByteCode(ProcessedMainSection, DefinedAddresses);
        if (dataSection != null)
            appendDataSection(res);
        return res;
    }

    private void appendDataSection(ArrayList<byte[]> res) {

        for (int k = 1; k < dataSection.size() - 1; k++) {
            String str = dataSection.get(k);
            String[] parts = str.split(",");
            int dataLength = Integer.valueOf(parts[2]);
            int dataSize = InstructionSet.getDataSize(parts[1]);
            byte[] data = new byte[dataLength * dataSize];
            int offset = 0;
            for (int i = 0; i < dataLength && i + 3 < parts.length; i++) {
                ByteBuffer temp = ByteBuffer.allocate(8);
                Long val = Long.decode(parts[i + 3]);
                temp.putLong(val);
                for (int j = 0; j < dataSize; j++)
                    data[j + offset] = temp.get(8 - dataSize + j);
                offset += dataSize;
            }
            res.add(data);
        }
    }

    private ArrayList<byte[]> codeSectionToByteCode(ArrayList<String> processedSection,
            HashMap<String, Long> DefinedAddresses) {
        ArrayList<byte[]> resultBytes = new ArrayList<>();
        for (String processedInstruction : processedSection) {

            String[] parts = processedInstruction.split(",");
            ByteBuffer buffer = ByteBuffer.allocate(PaxVirtualMachine.INSTRUCTION_SIZE);
            buffer.put(InstructionSet.OperationToByteCode(parts[0]));
            byte flag = Byte.valueOf(parts[1]);
            buffer.put(flag);
            byte v1 = 0;
            long v2 = 0;
            if (parts.length >= 3 && ((flag & 0b0000_1111) != PaxVirtualMachine.EMPTY_FLAG))
                v1 = InstructionSet.RegisterToByteCode(parts[2].substring(0, 2));
            if (parts.length == 4)
                v2 = encodePart(parts[3], DefinedAddresses);
            buffer.put(v1);
            buffer.putLong(v2);
            resultBytes.add(buffer.array());
        }

        return resultBytes;
    }

    private long encodePart(String part, HashMap<String, Long> DefinedAddresses) {
        long v1 = 0;
        if (InstructionSet.GeneralRegisters.contains(part))
            v1 = InstructionSet.RegisterToByteCode(part.substring(0, 2));
        else if (DefinedAddresses.containsKey(part))
            v1 = DefinedAddresses.get(part);
        else
            v1 = Long.valueOf(part);
        return v1;
    }
}
