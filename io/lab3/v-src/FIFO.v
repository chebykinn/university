module FIFO(tval2_i,tval1_i,icov_o,icbne_o,ictmr_i,icconf_o,icconf_i,ins_i,we_bi,en_i,data_bo,data_bi,addr_bi,rst_i,clk_i);
input [31:0] tval2_i;
input [31:0] tval1_i;
output icov_o;
output icbne_o;
input [1:0] ictmr_i;
output [31:0] icconf_o;
input [31:0] icconf_i;
input ins_i;
input we_bi;
input en_i;
output [31:0] data_bo;
input [31:0] data_bi;
input [12:0] addr_bi;
input rst_i;
input clk_i;

reg icov_o;
reg icbne_o;
reg [31:0] icconf_o;
reg [31:0] data_bo;

reg next_icbne_reg;
reg icbne_reg;
reg next_icov_reg;
reg icov_reg;
reg [31:0] icbuf[15:0];
reg [3:0] next_fst_idx_reg;
reg [3:0] fst_idx_reg;
reg [3:0] next_idx_reg;
reg [3:0] idx_reg;
reg [31:0] next_data_bo;
reg [31:0] temp_value_reg;
reg [31:0] next_icconf_reg;
reg [31:0] icconf_reg;


//on_change:
always @(icbne_reg or icov_reg or fst_idx_reg or idx_reg or icconf_reg or tval2_i or tval1_i or icconf_i or ins_i or we_bi or en_i or data_bi or addr_bi )
   begin

   icconf_o  =(icconf_reg );
   icbne_o  =(icbne_reg );
   icov_o  =(icov_reg );
   if (ins_i &&ictmr_i !='h0&&icov_reg !='h1)
      begin


      case(ictmr_i )
         'h1, 'h2 :
         begin
         temp_value_reg  =(tval1_i );
         end

         'h3 :
         begin
         temp_value_reg  =(tval2_i <<16|(tval1_i &'hFFFF ));
         end


      endcase

      next_idx_reg  =(idx_reg +1);

      next_icbne_reg  =('h1);
      next_icov_reg  =((fst_idx_reg ==next_idx_reg )?'h1:'h0);

      end
   else 
      begin

      temp_value_reg  =(0);
      next_idx_reg  =(idx_reg );

      next_icbne_reg  =(icbne_reg );
      next_icov_reg  =(icov_reg );

      end

   if (en_i &&we_bi )
      begin


      case(addr_bi )
         'h0 :
         begin
         next_icconf_reg  =(data_bi );
         end


      endcase


      end
   else if (en_i )
      begin


      case(addr_bi )
         'h0 :
         begin
         next_data_bo  =(icconf_i );
         end

         'h4 :
         begin
         if (icbne_reg !='h0)
            begin

            next_data_bo  =(icbuf [fst_idx_reg ]);
            next_fst_idx_reg  =(fst_idx_reg +1);
            next_icov_reg  =('h0);

            end

         end


      endcase


      end
   else 
      begin

      next_fst_idx_reg  =(fst_idx_reg );
      next_data_bo  =(data_bo );
      next_icconf_reg  =(icconf_reg );

      end


   end
//registers:
always @(posedge clk_i or posedge rst_i )
   begin

   if (!rst_i &&clk_i )
      begin

      icconf_reg  <=(next_icconf_reg );
      icbne_reg  <=(next_icbne_reg );
      icov_reg  <=(next_icov_reg );

      if (ictmr_i )
         begin

         icbuf [idx_reg ] <=(temp_value_reg );

         end


      idx_reg  <=(next_idx_reg );
      fst_idx_reg  <=(next_fst_idx_reg );

      data_bo  <=(next_data_bo );

      end
   else 
      begin

      icconf_reg  <=(0);
      icbne_reg  <=(0);
      icov_reg  <=(0);

      idx_reg  <=(0);
      fst_idx_reg  <=(0);

      data_bo  <=(0);

      icbuf [0] <=(0);
      icbuf [1] <=(0);
      icbuf [2] <=(0);
      icbuf [3] <=(0);
      icbuf [4] <=(0);
      icbuf [5] <=(0);
      icbuf [6] <=(0);
      icbuf [7] <=(0);
      icbuf [8] <=(0);
      icbuf [9] <=(0);
      icbuf [10] <=(0);
      icbuf [11] <=(0);
      icbuf [12] <=(0);
      icbuf [13] <=(0);
      icbuf [14] <=(0);
      icbuf [15] <=(0);

      end


   end

endmodule
