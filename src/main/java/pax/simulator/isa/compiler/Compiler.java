package pax.simulator.isa.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Compiler {
    private Path inputFile;
    private ArrayList<byte[]> byteCodeArrayList;
    private Parser parser;
    private ByteCodeEncoder encoder;

    public Compiler(Path inputPath) {
        this.inputFile = inputPath;
    }

    public void compile() throws CompilerException, IOException {
        
        List<String> lines = null;
        lines = Files.readAllLines(this.inputFile);

        // Parsiranje

        parser = new Parser(lines);

        // Enkodovanje
        this.encoder = new ByteCodeEncoder(parser.getProcessedMainSection(), parser.getDataSection(),
                parser.getDefinedAddresses());
        this.byteCodeArrayList = this.encoder.buildByteCode();

    }

    public HashMap<String, Long> getDefinedAddresses() {
        return this.parser.getDefinedAddresses();
    }

    public byte[] getByteCode() {

        byte[] program = new byte[byteCodeArrayList.stream().mapToInt(b -> b.length).sum()];
        int offset = 0;
        for (int i = 0; i < byteCodeArrayList.size(); i++) {
            int j = 0;
            for (; j < byteCodeArrayList.get(i).length; j++)
                program[offset + j] = byteCodeArrayList.get(i)[j];
            offset += j;

        }
        return program;

    }

    public ArrayList<String>  getUnprocessedMainSection()
    {
       return this.parser.getMainSection();
    }


    public static void main(String args[]) throws IOException, CompilerException {
        Compiler compiler = new Compiler(Paths.get(args[0]));
        compiler.compile();
        Files.write(Paths.get(args[1]), compiler.getByteCode());
    }

}
