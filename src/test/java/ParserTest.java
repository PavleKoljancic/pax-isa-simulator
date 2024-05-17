import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import pax.simulator.isa.compiler.Compiler;
import pax.simulator.isa.compiler.CompilerException;

public class ParserTest {

    @Test
    public void SectionTest1() throws CompilerException, IOException {
        Compiler compiler = new Compiler(Paths.get(
                "src/test/java/SectionTest1.pax"));
        compiler.compile();
        
    }

    @Test
    public void SectionTest2()  {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/SectionTest2.pax"));
                    compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertTrue(exceptionHappened);
    }

    @Test
    public void DobuleSectionName()  {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/SectionTest3.pax"));
                     compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertTrue(exceptionHappened);
    }


    @Test
    public void NoMain()  {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/SectionTest4.pax"));
                     compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertTrue(exceptionHappened);
    }

        @Test
    public void NoMemoryExtension1() {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/No MemoryExstension Provided.pax"));
                     compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertTrue(exceptionHappened);
    }

            @Test
    public void NoMemoryExtension2()  {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/MemoryExtensionsTest2.pax"));
                     compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertTrue(exceptionHappened);
    }
    @Test
    public void MemoryExtension()  {

        boolean exceptionHappened = false;
        try {
            Compiler compiler = new Compiler(Paths.get(
                    "src/test/java/MemoryExtensionsTest3.pax"));
                     compiler.compile();
        } catch (Exception e) {
            exceptionHappened = true;

        }
        Assert.assertFalse(exceptionHappened);
    }

}
