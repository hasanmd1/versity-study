/*
*This task is for a single cycle CPU design
*the operations includes, add, addi, and, sub,
*slt, div, rem, beq, blt, lw, sw, lui, jai, jalr
* additionally: auipc, sll, srl, sra
*/

/*
* Using the template from course pages
*
*/

`default_nettype none
module processor( input         clk, reset,     // Clock, RESET signal
                  output [31:0] PC,             // Program Counter
                  input  [31:0] instruction,    // Input instruction
                  output        WE,             // Write Enable signal
                  output [31:0] address_to_mem, // Address to memory
                  output [31:0] data_to_mem,    // Data to memory
                  input  [31:0] data_from_mem   // Data from memory
                );
    //... write your code here ...
    wire [31:0] rs1, rs2;
    // reg [31:0] four = 'b100;

    wire Branch_Beq_Control, Branch_Jal_Control, Branch_Jalr_Control, RegWrite, MemToReg, MemWrite, ALUSrc, BranchLui, Auipc, BranchOutcome, BranchJalx, zero;
    wire [3:0] ALUControl;
    wire [2:0]immControl;
    wire [31:0] afterPlus, PCPlus4, BranchTarget, PCn, ALUOut, ourPC, res, ImmOp, afterMux, toWD3, afterMuxImmRes;

    wire [31:0] SrcA, SrcB;

    //handling control signal branching
    assign BranchOutcome = ( ( Branch_Beq_Control & zero ) | BranchJalx);        
    assign BranchJalx = ( Branch_Jalr_Control | Branch_Jal_Control );       


    // Create instances of various modules and wires
    mux_2_to_1 mux_PC_n( PCPlus4[31:0], BranchTarget [31:0], BranchOutcome, PCn [31:0] );
    register regCLK_PC ( clk, reset, PCn[31:0], PC[31:0] );

    assign ourPC = PC;

    add_4 addFour ( ourPC, PCPlus4[31:0] );

    register_32bit reg32__ (clk, RegWrite, instruction[19:15], instruction[24:20], instruction[11:7], toWD3[31:0], SrcA[31:0], rs2 );
    imm_decoder imm_decoder__ ( instruction [31:7], immControl, ImmOp[31:0] );  
    ControlUnit ControlUnit__ ( instruction[31:0], Branch_Beq_Control, Branch_Jal_Control, Branch_Jalr_Control, RegWrite, MemToReg, WE, ALUSrc,  BranchLui, Auipc, ALUControl [3:0], immControl [2:0] );


    mux_2_to_1 Branch_mux_AfterMuxImmRes ( res[31:0], ImmOp[31:0], BranchLui, afterMuxImmRes [31:0] );
    mux_2_to_1 Branch_mux__WD3 ( afterMuxImmRes [31:0], BranchTarget [31:0], Auipc, toWD3[31:0] );

    assign data_to_mem = rs2;

    mux_2_to_1 Branch_mux__SrcB ( rs2, ImmOp[31:0], ALUSrc, SrcB[31:0]);

    ALU ALUXY( SrcA[31:0], SrcB [31:0], ALUControl [3:0], address_to_mem[31:0], zero );
    add sum__AfterPlus ( ourPC, ImmOp[31:0], afterPlus[31:0] );


    mux_2_to_1 Branch_mux_BranchTarget ( afterPlus[31:0], address_to_mem[31:0], Branch_Jalr_Control, BranchTarget[31:0] );
    mux_2_to_1 Branch_mux__AfterMux ( address_to_mem[31:0], PCPlus4[31:0], BranchJalx, afterMux[31:0] );
    mux_2_to_1 Branch_mux__Res ( afterMux[31:0], data_from_mem[31:0], MemToReg, res[31:0] );

endmodule

//... add new modules here ...

// Module: ALU
// This module implements an Arithmetic Logic Unit (ALU) with various arithmetic and logical operations.
module ALU (
    input  [31:0] SrcA, SrcB,   // Input operand A, B
    input  [3:0] ALUControl,    // ALU control signal
    output reg [31:0] ALUResult,    // ALU output result
    output reg zero                  // Zero flag
);

    always @(*) begin
        zero = 0;
        // Perform ALU operation based on ALUControl
        case ( ALUControl )
            'b0001: ALUResult = SrcA + SrcB;    // Addition
            'b0010: ALUResult = SrcA & SrcB;    // Bitwise AND
            'b0011: ALUResult = SrcA - SrcB;    // Subtraction
            'b0100: ALUResult = $signed(SrcA) < $signed(SrcB);  // Set if SrcA < SrcB
            'b0101: ALUResult = SrcA / SrcB;    // Division
            'b0110: ALUResult = SrcA % SrcB;    // Remainder
            'b0111: ALUResult = $signed(SrcA) >>> SrcB;  // Arithmetic right shift
            'b1000: ALUResult = SrcA << SrcB;   // Logical Left shift
            'b1001: ALUResult = SrcA >> SrcB;   // Logical right shift
            'b1111: ALUResult = 0;              // Zero operation
        endcase;

        if ( ALUResult == 0 ) begin
            zero = 1;   // Set zero flag if ALU result is zero
        end
    end 
endmodule
// End of Module: ALU

// Module: ControlUnit
// This module implements an Control Unit which assigns control signals based on instructions
module ControlUnit (
    input [31:0] instruction,
    output reg Branch_Beq_Control, Branch_Jal_Control, Branch_Jalr_Control, RegWrite, MemToReg, MemWrite, ALUSrc, BranchLui, Auipc,
    output reg [3:0] ALUControl,
    output reg [2:0]immControl
);

    always @(*) begin 

        if (instruction [6:0] == 'b0110011) begin   // 7 instruction
            if ( instruction [31:25] == 'b0000000) begin    // add, and, slt, sll, srl
                if ( instruction [14:12] == 'b000 ) begin   // add 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0001; // +
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end 
                else if ( instruction [14:12] == 'b111 ) // and
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0010; // &
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end 
                else if ( instruction [14:12] == 'b010 ) // slt
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0100; // <
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end 
                else if ( instruction [14:12] == 'b001 ) // sll
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b1000; // <<
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end 
                else if ( instruction [14:12] == 'b101 ) // srl
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b1001; // >>
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end
            end 
            else if ( instruction [31:25] == 'b0100000 ) // sub, sra
            begin
                if ( instruction [14:12] == 'b000 ) // sub
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0011; // -
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end 
                else if ( instruction [14:12] == 'b101 ) // sra
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0111; // >>>
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end
            end 
            else if ( instruction [31:25] == 'b0000001 ) // div, rem
            begin
                if ( instruction [14:12] == 'b100 ) // div
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0101; // /
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end else if ( instruction [14:12] == 'b110 ) // rem
                begin 
                Branch_Beq_Control   = 'b0;
                Branch_Jal_Control   = 'b0;
                Branch_Jalr_Control  = 'b0;
                RegWrite    = 'b1;
                MemToReg    = 'b0;
                MemWrite    = 'b0;
                ALUControl  = 'b0110; // %
                ALUSrc      = 'b0;
                immControl  = 'b001; // R
                BranchLui   = 'b0;
                Auipc       = 'b0;
                end
            end

        end 
        else if ( instruction [6:0] == 'b0010011 ) // addi
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b1;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b0001; // +
            ALUSrc      = 'b1;
            immControl  = 'b010; // I
            BranchLui   = 'b0;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b1100011 ) // beq, blt
        begin 
            if ( instruction [14:12] == 'b000 )        // beq
            begin
            Branch_Beq_Control   = 'b1;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b0;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b0011; // -
            ALUSrc      = 'b0;
            immControl  = 'b100;  // B
            BranchLui   = 'b0;
            Auipc       = 'b0;
            end 
            else if ( instruction [14:12] == 'b100) // blt
            begin
            Branch_Beq_Control   = 'b1;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b0;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b0100; // <
            ALUSrc      = 'b0;
            immControl  = 'b100;  // B
            BranchLui   = 'b0;
            Auipc       = 'b0;
            end

        end 
        else if ( instruction [6:0]  == 'b0000011) // lw
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b1;
            MemToReg    = 'b1;
            MemWrite    = 'b0;
            ALUControl  = 'b0001; // +
            ALUSrc      = 'b1;
            immControl  = 'b010;  // I
            BranchLui   = 'b0;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b0100011 ) // sw
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b0;
            MemToReg    = 'b1;
            MemWrite    = 'b1;
            ALUControl  = 'b0001; // +
            ALUSrc      = 'b1;
            immControl  = 'b011;  // S
            BranchLui   = 'b0;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b0110111 ) // lui
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b1;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b1111; // nothing
            ALUSrc      = 'b0;
            immControl  = 'b101;  // U
            BranchLui   = 'b1;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b1101111 ) // jal
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b1;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b1;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b1111; // nothing
            ALUSrc      = 'b0;
            immControl  = 'b110;  // J
            BranchLui   = 'b0;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b1100111 ) // jalr
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b1;
            RegWrite    = 'b1;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b0001; // +
            ALUSrc      = 'b1;
            immControl  = 'b010;  // I
            BranchLui   = 'b0;
            Auipc       = 'b0;
        end 
        else if ( instruction [6:0] == 'b0010111 ) // auipc
        begin
            Branch_Beq_Control   = 'b0;
            Branch_Jal_Control   = 'b0;
            Branch_Jalr_Control  = 'b0;
            RegWrite    = 'b1;
            MemToReg    = 'b0;
            MemWrite    = 'b0;
            ALUControl  = 'b1111; // nothing
            ALUSrc      = 'b0;
            immControl  = 'b101;  // U
            BranchLui   = 'b0;
            Auipc       = 'b1;
            end  
    end  
//end of this module
endmodule



// Module: register_32bit
// This module implements a 32 bit register set
module register_32bit (
    input clk,      // Clock signal
    input WE3,      // Write enable signal
    input [4:0] A1, A2, A3, // Address for reading RD1, RD2, rd ...
    input [31:0] WD3,       // Data to be written

    output reg [31:0] RD1,  // Data read from RD1
    output reg [31:0] RD2   // Data read from RD2

);

    // Define a 32-bit register file with 32 registers
    reg [31:0] registers [31:0];

    // Initialize the first register to 0
    initial 
    registers [ 0 ] = 0;

    // Logic for reading data from registers
    always @ (*) begin
        RD1[31:0] = registers [A1[4:0]];
       RD2[31:0] = registers [A2[4:0]];
    end

    // Logic for writing data to registers on the rising edge of the clock
    always @(posedge clk) begin
        // Reset the first register to 0 (or leave it as it is)
        registers [0] <= 0;  

        // Write data to the specified address if writeEnable is active
        if (WE3) begin
            registers [A3[4:0]] <= WD3[31:0];
        end
        // Reset the first register to 0 (or leave it as it is)
        registers [0] <= 0; 
    end
endmodule
//end of this module


// Module: mux_2_to_1
// This module implements a 2-to-1 multiplexer based on the control signal.
module mux_2_to_1 (
    input [31:0] sourceA,  // Input data for source A
    input [31:0] sourceB,  // Input data for source B
    input control,          // Select signal
    output reg [31:0] outputResult  // Output of the multiplexer
);

    always @ (*) begin
        // Use a ternary operator to control the output based on the control signal
        outputResult = (control == 1) ? sourceB : sourceA;
    end

    // End of Module: mux_2_to_1

endmodule

// Module: register
// This module registers an input value on the rising edge of the clk signal and can be reset with a reset signal.

module register (
    input clk,                  // Clock signal
    input reset,                // Reset signal
    input [31:0] input_data,    // Input data to be registered

    output reg [31:0] output_data  // Registered output data
);

    always @ (posedge clk) begin
        output_data = input_data;     // Register the input_data on the rising edge of clk
        if (reset == 1)
            output_data = 0;          // Reset the output to 0 if reset signal is asserted
    end
// End of Module: register
endmodule

// Module: add
// This module adds a sourceB to the input sourceA.

module add (
    input [31:0] sourceA,  // Input A, a 32-bit signed integer
    input [31:0] sourceB,  // Input B, a 32-bit signed integer
    output [31:0] destination  // Output, the result of adding sourceA and sourceB
);

    assign destination = sourceA + sourceB;  // Calculate the sum of sourceA and sourceB and assign it to the destination

endmodule
// End of Module: add


// Module: add_4
// This module adds a constant value of 4 to the input sourceA.

module add_4 (
    input [31:0] sourceA,     // Input sourceA, a 32-bit signed integer
    output [31:0] destination  // Output, the result of adding 4 to sourceA
);

    assign destination = sourceA + 4;  // Calculate the add of sourceA and 4 and assign it to the destination

endmodule
// End of Module: add_4


// Module: imm_decoder
// This module decodes immediate values based on the immControl signal for various RISC-V instruction types.
module imm_decoder (
        input [31:7] instruction,    // Instruction bits
        input [2:0]  immControl,    // Immediate type control signal
        output reg [31:0] imm       // Decoded immediate value
);

    always @(*) begin
        case ( immControl )
        'b001: imm = 0;               // R - type ( add, sub...)
        'b010: imm = { {21{instruction [31]}}, instruction [30:25], instruction [24:21], instruction [20] };    // I - type ( addi, lw, jalr... )
        'b011: imm = { {21{instruction [31]}}, instruction [30:25], instruction [11:8], instruction [7] }; // S - type
        'b100: imm = { {20{instruction [31]}}, instruction [7], instruction [30:25], instruction [11:8], 1'b0 }; // B  - type
        'b101: imm = { instruction [31], instruction [30:20], instruction [19:12], {12{1'b0}} }; // U - type
        'b110: imm = { {12{instruction[31]}}, instruction [19:12], instruction [20], instruction [30:25], instruction [24:21], 1'b0 }; // J - type 
        endcase;
    end     
endmodule
// End of Module: imm_decoder

`default_nettype wire