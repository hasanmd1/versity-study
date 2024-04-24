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