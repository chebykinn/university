module Prescaler(ins_o,ins_i,icm_i,rst_i,clk_i);
output ins_o;
input ins_i;
input [2:0] icm_i;
input rst_i;
input clk_i;

reg ins_o;

reg next_ins_o_reg;
reg [4:0] threshold_reg;
reg [4:0] next_counter_reg;
reg [4:0] counter_reg;


//threshold:
always @(icm_i )
   begin

   case(icm_i )
      'h1, 'h2, 'h3 :
      begin
      threshold_reg  =('h1);
      end

      'h4, 'h6 :
      begin
      threshold_reg  =('h4);
      end

      'h5, 'h7 :
      begin
      threshold_reg  =('h10);
      end

      default:
      begin

      threshold_reg  =('h0);
      end


   endcase


   end
//on_change:
always @(threshold_reg or counter_reg or ins_i )
   begin

   if (counter_reg ==threshold_reg )
      begin

      next_ins_o_reg  =(0);
      next_counter_reg  =(0);

      end
   else if (ins_i )
      begin

      next_ins_o_reg  =(1);
      next_counter_reg  =(counter_reg +1);

      end


   end
//registers:
always @(posedge clk_i or posedge rst_i )
   begin

   if (!rst_i &&clk_i )
      begin

      counter_reg  <=(next_counter_reg );
      ins_o  <=(next_ins_o_reg );

      end
   else 
      begin

      counter_reg  <=(0);
      ins_o  <=(0);

      end


   end

endmodule
