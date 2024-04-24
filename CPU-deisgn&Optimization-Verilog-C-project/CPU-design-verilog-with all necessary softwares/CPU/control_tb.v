module test_instruction_control_handler();

    reg [31:0] instruction;
    wire [3:0] imm, Alu_Control; 
    wire [0:0] Alu_Src_Control, MemWrite, MemToReg, RegWrite, Branch_Jalr_Control, Branch_Jal_Control, Branch_Beq_Control, Branch_Blt_Control;

    // Instantiate the control unit
    instruction_control_handler control_unit(
        .instruction(instruction),
        .imm(imm),
        .Alu_Control(Alu_Control),
        .Alu_Src_Control(Alu_Src_Control),
        .MemWrite(MemWrite),
        .MemToReg(MemToReg),
        .RegWrite(RegWrite),
        .Branch_Jalr_Control(Branch_Jalr_Control),
        .Branch_Jal_Control(Branch_Jal_Control),
        .Branch_Beq_Control(Branch_Beq_Control),
        .Branch_Blt_Control(Branch_Blt_Control)
    );

    // Initializations and test cases
    initial begin
        // Test Case 1: R-type Add (add)
        instruction = 32'h007302B3;
        // Set other expected values here
        // ...
        
        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0000 || imm !== 4'b0000 || RegWrite !== 1'b1)
            $display("Test Case 1 Failed: RegWrite=%b, imm=%b, Alu_Src_Control=%b, Alu_Control=%b", RegWrite, imm, Alu_Src_Control, Alu_Control);
        else
            $display("Test Case 1 Passed");

        // Test Case 2: I-type Load (lw)
        instruction = 32'h0003a303;
;
        // Set other expected values here
        // ...

        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0000 || imm !== 4'b0001 || Alu_Src_Control !== 1'b1
            || MemToReg !== 1'b1 || RegWrite !== 1'b1)
            $display("Test Case 2 Failed: RegWrite=%b, imm=%b, Alu_Src_Control=%b, Alu_Control=%b", RegWrite, imm, Alu_Src_Control, Alu_Control);
        else
            $display("Test Case 2 Passed");

        // Test Case 3: I-type Add Immediate (addi)
        instruction = 32'h30202093;
        // Set other expected values here
        // ...

        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0000 || imm !== 4'b0001 || Alu_Src_Control !== 1'b1
            || RegWrite !== 1'b1)
            $display("Test Case 1 Failed: RegWrite=%b, imm=%b, Alu_Src_Control=%b, Alu_Control=%b", RegWrite, imm, Alu_Src_Control, Alu_Control);

        else
            $display("Test Case 3 Passed");

        // Add more test cases for different instruction types

        // Test Case 3: R-type AND (and)
        instruction = 32'h01c3f333;
        // Set other expected values here
    
        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0010 || imm !== 4'b0000 || RegWrite !== 1'b1)
            $display("Test Case 4 Failed: Alu_Control=%b, imm=%b, RegWrite=%b", Alu_Control, imm, RegWrite);
        else
            $display("Test Case 4 Passed");
        // ...

        // Test Case 3: R-type SUB (and)
        instruction = 32'h41c38333;
        // Set other expected values here
        
        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0001 || imm !== 4'b0000 || RegWrite !== 1'b1)
            $display("Test Case 5 Failed: Alu_Control=%b, imm=%b, RegWrite=%b", Alu_Control, imm, RegWrite);
        else
            $display("Test Case 5 Passed");
        
        // Test Case 3: R-type slt (and)
        instruction = 32'h01c3a333;
        // Set other expected values here

        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0011 || imm !== 4'b0000 || RegWrite !== 1'b1)
            $display("Test Case 6 Failed: Alu_Control=%b, imm=%b, RegWrite=%b", Alu_Control, imm, RegWrite);
        else
            $display("Test Case 6 Passed");
        
        // Test Case 3: R-type REM (and)
        instruction = 32'h03c3e333;
        // Set other expected values here

        // Add some delay
        #10;

        // Check the results and display messages
        if (Alu_Control !== 4'b0101 || imm !== 4'b0000 || RegWrite !== 1'b1)
            $display("Test Case 7 Failed: Alu_Control=%b, imm=%b, RegWrite=%b", Alu_Control, imm, RegWrite);
        else
            $display("Test Case 7 Passed");
        // Finish simulation
        $finish;
    end

endmodule
