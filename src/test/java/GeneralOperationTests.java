import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import pax.simulator.isa.compiler.CompilerException;
import pax.simulator.isa.vm.PaxVirtualMachine;
import pax.simulator.isa.vm.PaxVirtualMachineRunTimeException;
import pax.simulator.isa.compiler.Compiler;
public class GeneralOperationTests {
    
        @Test
    public void SelfModifyingTest() throws CompilerException, IOException, PaxVirtualMachineRunTimeException {
        Compiler compiler = new Compiler(Paths.get(
                "src/test/java/samoModifikujuciPrimjer.pax"));
        compiler.compile();
        PaxVirtualMachine vm = new PaxVirtualMachine();
        vm.run(compiler.getByteCode());
        
    }
}
