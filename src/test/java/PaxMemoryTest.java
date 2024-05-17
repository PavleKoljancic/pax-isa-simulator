import org.junit.Assert;
import org.junit.Test;

import pax.simulator.isa.vm.PaxMemory;

public class PaxMemoryTest {
    @Test
    public void MemTest1() 
    {   
        PaxMemory memory = new PaxMemory();
        byte [] MeaningOfLife = new byte[64*1024];
        for(int i =0;i<MeaningOfLife.length;i++)
            MeaningOfLife[i]=42;
        memory.write(64*1024+5,MeaningOfLife );
        byte [] bytes69 = {69};
        memory.write(PaxMemory.MAX_ADDRESS, bytes69);
        byte [] readBytes69 = memory.read(PaxMemory.MAX_ADDRESS, 1);
        byte [] readMeaningOfLife = memory.read(64*1024+5, MeaningOfLife.length);

        Assert.assertArrayEquals(readBytes69,bytes69);
        Assert.assertArrayEquals(readMeaningOfLife,MeaningOfLife);
    }
}
