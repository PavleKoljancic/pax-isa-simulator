package pax.simulator.isa.vm;

public class PaxMemory {
    private static final long ADDRESS_BLOCK_SIZE = 64 * 1024; // 2^16
    byte[][][][] Space;
    public final static long MAX_ADDRESS = 0xffff_ffff_ffff_ffffl;

    public PaxMemory() {
        Space = new byte[(int) ADDRESS_BLOCK_SIZE][][][];
    }

    public void write(long address, byte[] data) {
        processMemory(address, data.length, new IProcessMemoryByte() {

            @Override
            public void processByte(int dest1, int dest2, int dest3, int dest4, int processed) {
                Space[dest1][dest2][dest3][dest4] = data[processed];
            }
            
        });
    }

    public byte[] read(long address, int dataLength) {

        byte[] data = new byte[dataLength];
        processMemory(address, dataLength, new IProcessMemoryByte() {

            @Override
            public void processByte(int dest1, int dest2, int dest3, int dest4, int processed) {
                data[processed] = Space[dest1][dest2][dest3][dest4];
            }
            
        });
        return data;
    }

    private void processMemory(long address, int dataLength, IProcessMemoryByte processMemoryByte) {
        int processed = 0;
        while (processed != dataLength) {
            int dest1 = (int) Long.divideUnsigned(address,
                    ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE);
            if (this.Space[dest1] == null)
                this.Space[dest1] = new byte[(int) ADDRESS_BLOCK_SIZE][][];
            int dest2 = (int) Long.divideUnsigned(
                    Long.remainderUnsigned(address, ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE),
                    ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE);
            if (this.Space[dest1][dest2] == null)
                this.Space[dest1][dest2] = new byte[(int) ADDRESS_BLOCK_SIZE][];
            int dest3 = (int) Long.divideUnsigned(
                    Long.remainderUnsigned(address, ADDRESS_BLOCK_SIZE * ADDRESS_BLOCK_SIZE), ADDRESS_BLOCK_SIZE);
            if (this.Space[dest1][dest2][dest3] == null)
                this.Space[dest1][dest2][dest3] = new byte[(int) ADDRESS_BLOCK_SIZE];
            int dest4 = (int) Long.remainderUnsigned(address, ADDRESS_BLOCK_SIZE);
            for (int i = 0; dest4 + i < ADDRESS_BLOCK_SIZE && processed < dataLength; i++)
                processMemoryByte.processByte(dest1, dest2, dest3, dest4 + i, processed++);

            address += processed;

        }
    }

    @FunctionalInterface
    private interface IProcessMemoryByte {
        void processByte(int dest1, int dest2, int dest3, int dest4, int processed);
    }
}
