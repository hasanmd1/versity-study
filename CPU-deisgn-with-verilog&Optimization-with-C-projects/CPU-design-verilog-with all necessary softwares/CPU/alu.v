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