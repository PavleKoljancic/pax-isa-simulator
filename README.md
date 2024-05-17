# Custom Instruction Set Architecture Simulator

## Overview

This project is a Java-based simulator for a custom instruction set architecture (ISA) designed to interpret and execute assembly language programs. The simulator includes the following features:

## Key Features

### Registers
- Four general-purpose registers, each 64 bits in size.
- Registers are implemented using Java's `long` data type to handle 64-bit values.

### Instruction Set
- **Arithmetic Operations:** `ADD`, `SUB`, `MUL`, `DIV`
- **Bitwise Logical Operations:** `AND`, `OR`, `NOT`, `XOR`
- **Data Movement:** `MOV` (Move data between registers)
- **Input/Output Instructions:**
  - Instructions to read from standard input.
  - Instructions to write to standard output.

### Debugger Support
- **Single-step Execution:** Allows the user to execute one instruction at a time.
- **Breakpoints:** Set and manage breakpoints in the code.
- **Inspection:** View the values of all registers and specified memory addresses at breakpoints.
- **Console Commands:**
  - `NEXT` or `STEP` to move to the next instruction.
  - `CONTINUE` to proceed to the next breakpoint.

### Memory Management
- 64-bit address space simulation.
- Byte-addressable memory (each address holds 1 byte).
- Direct and indirect addressing modes.
- Read and write access to all addresses using `MOV` or `LOAD/STORE` instructions.

### Control Flow Instructions
- Unconditional and conditional branching: `JMP`, `CMP`, `JE`, `JNE`, `JGE`, `JL`

### Assembler to Machine Code Translation
- Assembly instructions can be translated into guest machine code (bytecode) and stored in the guest's memory.
- Execution of instructions directly from guest memory.
- Program Counter (PC) register to point to the current instruction.
- Example of self-modifying code:
  - Demonstrates modifying and executing a portion of code before and after the modification.

### Unit Tests
- Comprehensive unit tests to demonstrate the functionalities specified in the project requirements.

## Implementation Details
- **Object-Oriented Design:** Follows OOP principles and SOLID design principles to ensure modularity and maintainability.
- **Clean Code Practices:** Adheres to clean code principles, including proper naming conventions for variables, functions, classes, and methods.
- **Java Conventions:** Follows Java language conventions and best practices.

## Usage

### Loading Assembly Code
- The simulator reads assembly code from a file.
- Performs lexical, syntactical, and semantic analysis to ensure correctness.

### Executing Instructions
- The interpreter executes the instructions sequentially or based on the control flow specified by the program.

### Debugging
- Users can step through the code, set breakpoints, and inspect the state of the machine at any point during execution.

This project aims to provide a comprehensive environment for simulating a custom ISA, offering both educational value and practical insights into the workings of low-level machine instructions and their execution.
