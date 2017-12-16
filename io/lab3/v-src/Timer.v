module Timer(clk_i,rst_i,addr_bi,data_bi,data_bo,en_i,we_bi,tval_o);
input clk_i;
input rst_i;
input [12:0] addr_bi;
input [31:0] data_bi;
output [31:0] data_bo;
input en_i;
input [3:0] we_bi;
output [31:0] tval_o;

reg [31:0] data_bo;
reg [31:0] tval_o;

reg [31:0] next_data_bo_reg;
reg [31:0] next_tconf_reg;
reg [31:0] next_tval_reg;
reg [31:0] next_tmr_reg;
reg [31:0] data_bo_reg;
reg [31:0] tconf_reg;
reg [31:0] tval_reg;
reg [31:0] tmr_reg;


//read_write:
always @(tconf_reg or tval_reg or tmr_reg or we_bi or en_i or data_bi or addr_bi or rst_i )
   begin

   if (rst_i )
      begin

      next_tconf_reg  =(0);
      next_tmr_reg  =(0);
      next_data_bo_reg  =(0);

      end
   else if (en_i &&we_bi )
      begin


      case(addr_bi )
         'h0 :
         begin
         next_tmr_reg  =(data_bi );
         end

         'h8 :
         begin
         next_tconf_reg  =(data_bi );
         end


      endcase


      end
   else if (en_i )
      begin


      case(addr_bi )
         'h0 :
         begin
         next_data_bo_reg  =(tval_reg );
         end

         'h4 :
         begin
         next_data_bo_reg  =(tmr_reg );
         end

         'h8 :
         begin
         next_data_bo_reg  =(tconf_reg );
         end


      endcase


      end
   else 
      begin

      next_tconf_reg  =(tconf_reg );
      next_tmr_reg  =(tmr_reg );
      next_data_bo_reg  =(data_bo_reg );

      end


   end
//tick:
always @(data_bo_reg or tconf_reg or tval_reg or tmr_reg or rst_i )
   begin

   tval_o  =(tval_reg );
   data_bo  =(data_bo_reg );

   if (rst_i )
      begin

      next_tval_reg  =(0);

      end
   else if (tconf_reg =='h2)
      begin

      if (tval_reg <tmr_reg )
         begin

         next_tval_reg  =(tval_reg +1);

         end
      else 
         begin

         next_tval_reg  =(0);

         end


      end
   else if (tconf_reg =='h3)
      begin

      if (tval_reg >0)
         begin

         next_tval_reg  =(tval_reg -1);

         end
      else 
         begin

         next_tval_reg  =(tmr_reg );

         end


      end


   end
//registers:
always @(posedge clk_i or posedge rst_i )
   begin

   if (!rst_i &&clk_i )
      begin

      tconf_reg  <=(next_tconf_reg );
      tmr_reg  <=(next_tmr_reg );
      tval_reg  <=(next_tval_reg );
      data_bo_reg  <=(next_data_bo_reg );

      end
   else 
      begin

      tconf_reg  <=(0);
      tmr_reg  <=(0);
      tval_reg  <=(0);
      data_bo_reg  <=(0);

      end


   end

endmodule
