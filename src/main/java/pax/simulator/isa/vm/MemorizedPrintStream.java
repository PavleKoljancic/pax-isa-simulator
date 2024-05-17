package pax.simulator.isa.vm;


import java.io.PrintStream;

import java.util.ArrayList;

public class MemorizedPrintStream extends PrintStream{

    ArrayList<String> past;
    public MemorizedPrintStream() {
        super(System.out);
        this.past = new ArrayList<>();
    }

    @Override
    public void println() {
        past.add("");
        super.println();
    }
    @Override
    public void println(String x) 
    {
        past.add(x);
        super.println(x);
    }

    public void printPast() 
    {
        for (String pastString : this.past) {
            System.out.println(pastString);
        }
    }
}
