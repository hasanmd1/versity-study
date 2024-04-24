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

//default nettype wires is set to none
`default_nettype none
module processor( input         clk, reset,
                  output [31:0] PC,
                  input  [31:0] instruction,
                  output        WE,
                  output [31:0] address_to_mem,
                  output [31:0] data_to_mem,
                  input  [31:0] data_from_mem
                );
    //... write your code here ...
	wire [0:0] MemToReg, RegWrite, Branch_Jalr_Control, Branch_Jal_Control, Branch_Beq_Control, Branch_Blt_Control, Alu_Src_Control, Imm_Control, zero, less;
    wire [4:0] rd, rs1, rs2;
	wire [0:0] Branch_Beq_first_out, Branch_Jalx, Branch_Beq_Outcome, Branch_Blt_Outcome;
    wire [31:0] srcA, srcB, pc_in, pc_out;
	wire [3:0] Imm_Type_Specifier, Alu_Control;
	wire [31:0] Imm_Operation, Branch_Jalx_Out, PC_add_4, Branch_Target, PC_add_Imm_Operation, Data_To_Reg;

	//collect A1, A2, A3
	assign rs2 = instruction[19:15];
	assign rs1 = instruction[24:20];
	assign rd = instruction[11:7];

	//control signal updating
	assign Branch_Beq_first_out = Branch_Beq_Control & zero; 	//and of beq control and zero from alu unit
	assign Branch_Jalx = Branch_Jal_Control | Branch_Jalr_Control; //or of jal jalx control
	assign Branch_Beq_Outcome = Branch_Beq_first_out | Branch_Jalx; //or of branch jalx, branch beq or ed with zero
	assign Branch_Blt_Outcome = Branch_Blt_Control & less;	//and of branch_blt control and less

	//reset counters
    counter_reseter reset_all(reset, clk, pc_in, pc_out);

	//this is control unit which will fix all necessary operations and branches
    instruction_control_handler controller(instruction, Imm_Type_Specifier, Alu_Control, Alu_Src_Control, WE, MemToReg, RegWrite, Branch_Jalr_Control, Branch_Jal_Control, Branch_Beq_Control, Branch_Blt_Control);

	// imm operation decoder,
	imm_decoder immediate_operation(instruction[31:7], Imm_Type_Specifier, Imm_Operation);

	// BRANCH_OUTCOME_MUX - Select between PC_add_4 and Branch_Target
	mux2_to_1 mux_Branch_Beq_Outcome(Branch_Beq_Outcome, PC_add_4, Branch_Target, pc_in);

	// MUX_ALUSRC - Select between data_to_mem and Imm_Operation as the second ALU operand
	mux2_to_1 mux_Branch_Alu_src(Alu_Src_Control, data_to_mem, Imm_Operation, srcB);
	//mux2_to_1 mux_AluSrc();

	// MUX_JALR - Select between PC_add_Imm_Operation and address_to_mem
	mux2_to_1 mux_Branch_Jalr(Branch_Jalr_Control, PC_add_Imm_Operation, address_to_mem, Branch_Target);

	// MUX_JALX - Select between address_to_mem and PC_Plus_4
	mux2_to_1 mux_Branch_Jalx(Branch_Jalx, address_to_mem, PC_add_4, Branch_Jalx_Out);

	// MUX_MEM_TO_REG - Select between Branch_Jalx_Out and data_from_mem as the data to write to registers
	mux2_to_1 mux_Mem_To_Reg(MemToReg, Branch_Jalx_Out, data_from_mem, Data_To_Reg);

	// COUNTER_PLUS_4 - Increment the Program Counter by 4
  	counter counter_pc_add_4(4, pc_out, PC_add_4);

	// COUNTER_IMM_OP - Increment the Program Counter based on ImmOp
	counter counter_Imm_Operations(Imm_Operation, pc_out, PC_add_Imm_Operation);
	
	// ALU - Perform arithmetic operations
	alunit alu(srcA, srcB, Alu_Control, zero, less, address_to_mem);

	//Register File
	register_File registry(rs2, rs1, rd, RegWrite, Data_To_Reg, clk, srcA, data_to_mem);
	
	assign PC = pc_out;

endmodule


//module which will work as imm. code decoder
module imm_decoder(
	input [31:7] op_code, 
	input [3:0] Imm_Type_Specifier,
	output reg [31:0] Out
);
	always@ (*) begin
	  case(Imm_Type_Specifier)
	  	//0000 -> nothing R types
		//0001 ->  lw + addi
		// 0010 -> 	sw
		// 0011 ->	beq + blt
		// 0100 ->	auipc + lui
		// 0101 ->	jal
		// 0110 ->	jalr
	  	4'b0000: begin //R type filled with 0
		  Out = 32'b0;
		end
		4'b0001: begin //lw + addi + jalr
		  Out = {{21{op_code[31]}}, op_code[30:25], op_code[24:21], op_code[20]};
		end
		4'b0010: begin //sw
		  Out = {{21{op_code[31]}}, op_code[30:25], op_code[11:8], op_code[7]};
		end
		4'b0011: begin //beq+blt
		  Out = {{20{op_code[31]}}, op_code[7], op_code[30:25], op_code[11:8], 1'b0};
		end
		4'b0100: begin //auipc + lui
		  Out = {op_code[31], op_code[30:20], op_code[19:12], 12'b0};
		end
		4'b0101: begin //jal
		  Out = {{12{op_code[31]}}, op_code[19:12], op_code[20], op_code[30:25], op_code[24:21], 1'b0};
		end
	  endcase
	end
endmodule

//our controller
//instruction_control_handler controller(instruction, Imm_Type_Specifier, Alu_Control, Alu_Src_Control, WE, MemToReg, RegWrite, Branch_Jalr_Control, Branch_Jal_Control, Branch_Beq_Control, Branch_Blt_Control);

module instruction_control_handler(
	input[31:0] instruction,
	output reg [3:0] imm, Alu_Control, 
	output reg [0:0] Alu_Src_Control, MemWrite, MemToReg, RegWrite, Branch_Jalr_Control, Branch_Jal_Control, Branch_Beq_Control, Branch_Blt_Control
);

	wire [6:0] op_code = instruction[6:0];
	wire [2:0] func3 = instruction[14:12];
	wire [6:0] func5 = instruction[31:25];

    always@ (*) begin
	  	case(op_code)
			7'b0110011: begin
		  	//R(.) add, sub, sll, slt, srl, sra, div, rem, and... operations
				case (func3)
					3'b000: begin //add+sub
						if (func5 == 7'b0000000) begin 
							Alu_Control = 4'b0000; 		//add
							Alu_Src_Control = 1'b0;
							MemWrite = 1'b0;
							MemToReg = 1'b0;
							RegWrite = 1'b1;
							Branch_Jal_Control = 1'b0;
							Branch_Jalr_Control = 1'b0;
							Branch_Beq_Control = 1'b0;
							Branch_Blt_Control = 1'b0;
							imm = 4'b0000;
						end
						else begin 
							Alu_Control = 4'b0001; 		//sub
							Alu_Src_Control = 1'b0;
							MemWrite = 1'b0;
							MemToReg = 1'b0;
							RegWrite = 1'b1;
							Branch_Jal_Control = 1'b0;
							Branch_Jalr_Control = 1'b0;
							Branch_Beq_Control = 1'b0;
							Branch_Blt_Control = 1'b0;
							imm = 4'b0000; 
						end
					end
					3'b111: begin
						Alu_Control = 4'b0010; 		//and
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b1;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0000;	
					end
					3'b010: begin 
						Alu_Control = 4'b0011; 		//slt
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b1;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0000;
					end
					3'b100: begin
						Alu_Control = 4'b0100; 		//div
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b1;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0000;
					end
					3'b110: begin
						Alu_Control = 4'b0101; 		//rem
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b1;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0000;
					end
					3'b001: begin
						Alu_Control = 4'b0110; 		//sll
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b1;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0000;
					end
					3'b101: begin
						if(func5 == 7'b0000000) begin
							Alu_Control = 4'b0111; 		//srl
							Alu_Src_Control = 1'b0;
							MemWrite = 1'b0;
							MemToReg = 1'b0;
							RegWrite = 1'b1;
							Branch_Jal_Control = 1'b0;
							Branch_Jalr_Control = 1'b0;
							Branch_Beq_Control = 1'b0;
							Branch_Blt_Control = 1'b0;
							imm = 4'b0000;
						end
						else begin
							Alu_Control = 4'b1000; 		//sra
							Alu_Src_Control = 1'b0;
							MemWrite = 1'b0;
							MemToReg = 1'b0;
							RegWrite = 1'b1;
							Branch_Jal_Control = 1'b0;
							Branch_Jalr_Control = 1'b0;
							Branch_Beq_Control = 1'b0;
							Branch_Blt_Control = 1'b0;
							imm = 4'b0000; 
						end
					end
				endcase
			end
			7'b0000011: begin
			  //I - load operations
			  //lw operations
				Alu_Control = 4'b0000; 		//lw
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b0;
				MemToReg = 1'b1;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0001;
			end
			7'b0010011: begin
			  //I - addi operation
				Alu_Control = 4'b0000; 		//addi
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b0;
				MemToReg = 1'b0;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0001;
			end
			7'b0100011: begin
			  //S sw operations
				Alu_Control = 4'b0000; 		//sw
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b1;
				MemToReg = 1'b0;
				RegWrite = 1'b0;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0010;
			end
			7'b1100011: begin
			  //SB operations
			  //beq, blt
			  	case(func3) 
					3'b000: begin
					  	Alu_Control = 4'b1001; 		//beq
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b0;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b1;
						Branch_Blt_Control = 1'b0;
						imm = 4'b0011;
					end
					3'b100: begin
					  	Alu_Control = 4'b1010; 		//blt
						Alu_Src_Control = 1'b0;
						MemWrite = 1'b0;
						MemToReg = 1'b0;
						RegWrite = 1'b0;
						Branch_Jal_Control = 1'b0;
						Branch_Jalr_Control = 1'b0;
						Branch_Beq_Control = 1'b0;
						Branch_Blt_Control = 1'b1;
						imm = 4'b0011;
					end
				endcase
			end
			7'b0010111: begin
			  //U operations
				Alu_Control = 4'b0000; 		//auipc
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b0;
				MemToReg = 1'b0;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0100;
			end
			7'b0110111: begin
			  //U lui operation
				Alu_Control = 4'b0000; 		//lui
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b0;
				MemToReg = 1'b0;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0100;
			end
			7'b1101111: begin
		 	 //UJ jal operations
			 	Alu_Control = 4'b0000; 		//jal
				Alu_Src_Control = 1'b0;
				MemWrite = 1'b0;
				MemToReg = 1'b0;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b1;
				Branch_Jalr_Control = 1'b0;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0101;
			end
			7'b1100111: begin
		  	//UJ jalr operation
				Alu_Control = 4'b0000; 		//jalr
				Alu_Src_Control = 1'b1;
				MemWrite = 1'b0;
				MemToReg = 1'b0;
				RegWrite = 1'b1;
				Branch_Jal_Control = 1'b0;
				Branch_Jalr_Control = 1'b1;
				Branch_Beq_Control = 1'b0;
				Branch_Blt_Control = 1'b0;
				imm = 4'b0001;
			end
		endcase
	end
endmodule

//ALU module
module alunit(
	input signed [31:0] srcA, srcB,
	input [3:0] Alu_Control,
	output reg [0:0] zero, hero, 
	output reg signed [31:0] result
);

    always@ (*) begin
      	case(Alu_Control)
		  	4'b0000: begin
				//add
				result = srcA + srcB;
			end
			4'b0001: begin
				//sub
				result = srcA - srcB;
			end
			4'b0010: begin
				//and operations
				result = srcA & srcB;
			end
			4'b0011: begin
			  	//slt operations
				if(srcA < srcB) begin
					result = 1;
				end
				else begin
					result = 0;
				end
			end
			4'b0100: begin
			  	//div operations
				result = srcA / srcB;
			end
			4'b0101: begin
			  	//rem operations
				result = srcA % srcB;
			end
			4'b0110: begin
			  	//sll operations rd ← [rs1] << [rs2];
				result = srcA << srcB;
			end
			4'b0111: begin
			  	//srl operations rd ← (unsigned)[rs1] >> [rs2];
				result = srcA >> srcB;
			end
			4'b1000: begin
			  	//sra operations
				result = $signed(srcA) >>> srcB;
			end
			4'b1001: begin
			  	//beq operations
				if(srcA == srcB) begin
				  	zero = 1;
				end
				else begin
				  	zero = 0;
				end
			end
			4'b1010: begin
			  	//blt operations
				if(srcA < srcB) begin
					hero = 1;
				end
				else begin
				  	hero = 0;
				end
			end
        endcase
    end
endmodule


//counter resert module
module counter_reseter( input[0:0]   reset, clk, 
                        input[31:0]  in, 
                        output reg [31:0] out);

    //always will check if new clk signal/reset signal is provided.
    always @(posedge clk) begin
        out =  (!reset) ? in : 0;
    end

endmodule

//counter module
module counter(
	input [31:0] srcA, srcB,
	output [31:0] out
);

	assign out = srcA + srcB;

endmodule

//multiplexor module
module mux2_to_1(
	input [0:0] condition,
	input[31:0] srcA, srcB, 
	output reg [31:0] result
);

    always@(*) begin
        result = condition ? srcB : srcA;
    end

endmodule

//GPR Set registerFile module
module register_File(
	input [4:0] A1, A2, A3,
	input [0:0] WE3,
	input [31:0] WD3,
	input [0:0] clk,
	output reg [31:0] RD1, RD2
);

	reg [31:0] rf[31:0];

	always @(posedge clk) begin
	  RD1 = (A1 != 0) ? rf[A1] : 32'b0;
	  RD2 = (A2 != 0) ? rf[A2] : 32'b0;

	  if(WE3 == 1) begin
		if(A3 != 0) begin
		  rf[A3] <= WD3;
		end
	  end
	  rf[0] <= 0;
	end

endmodule
//... add new Verilog modules here ...
`default_nettype wire
