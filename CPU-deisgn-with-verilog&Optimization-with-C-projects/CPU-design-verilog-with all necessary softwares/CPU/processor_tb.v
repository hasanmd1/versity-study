module testbench();

    reg clk;
    reg reset;
    reg [31:0] data_to_mem;
    reg write_enable;
    wire [31:0] data_from_mem;
    
    // Instantiate the processor module
    processor CPU(
        .clk(clk),
        .reset(reset),
        .instruction(instruction),
        .
        .data_to_mem(data_to_mem),
        .address_to_mem(address_to_mem),
        .write_enable(write_enable),
        .data_from_mem(data_from_mem)
    );
    
    // Clock generation
    always begin
        #5 clk = ~clk;
    end
    
    // Reset control signals
    initial begin
        $dumpfile("test.vcd");
        $dumpvars;
        reset <= 1;
        #2 reset <= 0;
        #100;
        data_to_mem = 32'h0;
        write_enable = 0;
        
        // Test the processor for different instructions
        
        // Test Case 1: R-type Add (add)
        $display("Test Case 1: R-type Add (add)");
        reset = 0;
        data_to_mem = 32'h0;  // Set data for memory write
        write_enable = 1;
        // Load the instruction (replace with actual encoding)
        CPU.instruction = 32'hADD_INSTRUCTION;
        // Add some delay
        #10;
        // Check the result and display messages
        // You should compare the results with the expected output
        if (data_from_mem !== 32'hEXPECTED_RESULT)
            $display("Test Case 1 Failed");
        else
            $display("Test Case 1 Passed");
        
        // Test Case 2: I-type Load (lw)
        // Similar structure to Test Case 1

        // Test Case 3: I-type Add Immediate (addi)
        // ...

        // Test Case 4: S-type Store (sw)
        // ...

        // Test Case 5: U-type AUIPC
        // ...

        // Test Case 6: J-type Jump and Link (jal)
        // ...
        
        // Finish simulation
        $finish;
    end
    always@ (*) begin
        #1 clk <= ~clk;
    end
endmodule
