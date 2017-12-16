module BRAMInterconnect(rddata_bo,s1_addr_bo,s1_wrdata_bo,s1_en_o,s1_we_bo,s2_addr_bo,s2_wrdata_bo,s2_en_o,s2_we_bo,s3_addr_bo,s3_wrdata_bo,s3_en_o,s3_we_bo,addr_bi,clk_i,wrdata_bi,en_i,rst_i,we_bi,s1_rddata_bi,s2_rddata_bi,s3_rddata_bi);
output [31:0] rddata_bo;
output [12:0] s1_addr_bo;
output [31:0] s1_wrdata_bo;
output s1_en_o;
output [3:0] s1_we_bo;
output [12:0] s2_addr_bo;
output [31:0] s2_wrdata_bo;
output s2_en_o;
output [3:0] s2_we_bo;
output [12:0] s3_addr_bo;
output [31:0] s3_wrdata_bo;
output s3_en_o;
output [3:0] s3_we_bo;
input [12:0] addr_bi;
input clk_i;
input [31:0] wrdata_bi;
input en_i;
input rst_i;
input [3:0] we_bi;
input [31:0] s1_rddata_bi;
input [31:0] s2_rddata_bi;
input [31:0] s3_rddata_bi;

reg [31:0] rddata_bo;
reg [12:0] s1_addr_bo;
reg [31:0] s1_wrdata_bo;
reg s1_en_o;
reg [3:0] s1_we_bo;
reg [12:0] s2_addr_bo;
reg [31:0] s2_wrdata_bo;
reg s2_en_o;
reg [3:0] s2_we_bo;
reg [12:0] s3_addr_bo;
reg [31:0] s3_wrdata_bo;
reg s3_en_o;
reg [3:0] s3_we_bo;

reg next_en_o_reg;
reg en_o_reg;


//write:
always @(we_bi or en_i or wrdata_bi or addr_bi )
   begin

   if (en_i )
      begin


      case(addr_bi )
         'h0, 'h4, 'h8 :
         begin
         s1_addr_bo  =(addr_bi );
         s1_wrdata_bo  =(wrdata_bi );
         s1_en_o  =(en_i );
         s1_we_bo  =(we_bi );
         end

         'h0C, 'h10, 'h14 :
         begin
         s2_addr_bo  =(addr_bi -'h0C );
         s2_wrdata_bo  =(wrdata_bi );
         s2_en_o  =(en_i );
         s2_we_bo  =(we_bi );
         end

         'h18, 'h1C :
         begin
         s3_addr_bo  =(addr_bi -'h18);
         s3_wrdata_bo  =(wrdata_bi );
         s3_en_o  =(en_i );
         s3_we_bo  =(we_bi );
         end


      endcase


      end
   else 
      begin

      s1_addr_bo  =(0);
      s1_wrdata_bo  =(0);
      s1_en_o  =(0);
      s1_we_bo  =(0);
      s2_addr_bo  =(0);
      s2_wrdata_bo  =(0);
      s2_en_o  =(0);
      s2_we_bo  =(0);
      s3_addr_bo  =(0);
      s3_wrdata_bo  =(0);
      s3_en_o  =(0);
      s3_we_bo  =(0);

      end


   end
//read:
always @(s3_rddata_bi or s2_rddata_bi or s1_rddata_bi or en_o_reg )
   begin

   if (en_o_reg )
      begin


      case(addr_bi )
         'h0, 'h4, 'h8 :
         begin
         rddata_bo  =(s1_rddata_bi );
         end

         'h0C, 'h10, 'h14 :
         begin
         rddata_bo  =(s2_rddata_bi );
         end

         'h18, 'h1C :
         begin
         rddata_bo  =(s3_rddata_bi );
         end


      endcase


      end
   else 
      begin

      rddata_bo  =(0);

      end


   end
//neg_clk:
always @(negedge clk_i )
   begin

   next_en_o_reg  <=(en_i &&!we_bi );

   end
//registers:
always @(posedge clk_i or posedge rst_i )
   begin

   if (!rst_i &&clk_i )
      begin

      en_o_reg  <=(next_en_o_reg );

      end
   else 
      begin

      en_o_reg  <=(0);

      end


   end

endmodule
