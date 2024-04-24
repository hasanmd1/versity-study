module alunit_tb;

  reg signed [31:0] srcA;
  reg signed [31:0] srcB;
  reg [3:0] Alu_Control;
  wire [0:0] zero;
  wire hero;
  wire [31:0] result;

  alunit DUT (
    .srcA(srcA),
    .srcB(srcB),
    .Alu_Control(Alu_Control),
    .zero(zero),
    .hero(hero),
    .result(result)
  );

  initial begin
    // Test addition
    $dumpfile("test.vcd");
        $dumpvars;
        #100;
    srcA = 10;
    srcB = 20;
    Alu_Control = 4'b0000;
    #10;
    if (result !== (srcA + srcB)) $display("Addition Test Failed");
    else $display("Addition Test Passed");

    // Test subtraction
    srcA = 30;
    srcB = 15;
    Alu_Control = 4'b0001;
    #10;
    if (result !== (srcA - srcB)) $display("Subtraction Test Failed");
    else $display("Subtraction Test Passed");

    // Test bitwise AND
    srcA = 5'h1F;
    srcB = 5'h0A;
    Alu_Control = 4'b0010;
    #10;
    if (result !== (srcA & srcB)) $display("Bitwise AND Test Failed");
    else $display("Bitwise AND Test Passed");

    // Test Set Less Than (SLT)
    srcA = -10;
    srcB = 5;
    Alu_Control = 4'b0011;
    #10;
    if (result !== (srcA < srcB ? 1 : 0)) $display("SLT Test Failed");
    else $display("SLT Test Passed");

    // Test Division
    srcA = 50;
    srcB = 10;
    Alu_Control = 4'b0100;
    #10;
    if (result !== (srcA / srcB)) $display("Division Test Failed");
    else $display("Division Test Passed");

    // Test Remainder
    srcA = 31;
    srcB = 4;
    Alu_Control = 4'b0101;
    #10;
    if (result !== (srcA % srcB)) $display("Remainder Test Failed");
    else $display("Remainder Test Passed");

    // Test Left Shift Logical
    srcA = 8;
    srcB = 2;
    Alu_Control = 4'b0110;
    #10;
    if (result !== (srcA << srcB)) $display("Left Shift Logical Test Failed");
    else $display("Left Shift Logical Test Passed");

    // Test Right Shift Logical
    srcA = 16;
    srcB = 2;
    Alu_Control = 4'b0111;
    #10;
    if (result !== (srcA >> srcB)) $display("Right Shift Logical Test Failed");
    else $display("Right Shift Logical Test Passed");

    // Test Right Shift Arithmetic (sra)
    srcA = -16;
    srcB = 2;
    Alu_Control = 4'b1000;
    #20;
    if ($signed(result) !== ($signed(srcA) >>> srcB))
        $display("Right Shift Arithmetic Test Failed (Expected: %d, Got: %d)", $signed(srcA) >> srcB, $signed(result));
    else
       $display("Right Shift Arithmetic Test Passed");


    // Test Branch Equal (BEQ)
    srcA = 20;
    srcB = 20;
    Alu_Control = 4'b1001;
    #10;
    if (zero !== 1) $display("Branch Equal Test Failed");
    else $display("Branch Equal Test Passed");

    // Test Branch Less Than (BLT)
    srcA = -5;
    srcB = 10;
    Alu_Control = 4'b1010;
    #10;
    if (hero !== 1) $display("Branch Less Than Test Failed");
    else $display("Branch Less Than Test Passed");

    $finish;
  end
endmodule
