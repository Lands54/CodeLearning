package madd

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  
  val io = IO(new StridePrefetcherIO(addressWidth,pcWidth))
  
  class List extends Bundle {
    val PCS = UInt(pcWidth.W)
    val ADS = UInt(addressWidth.W)
    val PDS = UInt(addressWidth.W)
    def List() = {
      PCS := 0.U
      ADS := 0.U
      PDS := 0.U
  }
  }
  val file = Reg(Vec(1024, new List))
  val count = RegInit(0.U(32.W))
  when(count >= 1024.U) {
    count := 0.U
  }
  val data_in = Wire(new List)
  data_in.ADS := io.address
  data_in.PCS := io.pc
  

  
  when(count > 0.U) {
    data_in.PDS := data_in.ADS - file(count-1.U).ADS
  }.otherwise {
    data_in.PDS := data_in.ADS - file(1023).ADS
  }

  file(count) := data_in

  when(count > 0.U) {                           //本程序中对Stride Prefetcher的理解为，当检测到连续两次相同间隔时则预取当前地址+间隔地址。
    when(data_in.PDS === file(count-1.U).PDS) {
      io.prefetch_address := data_in.ADS + data_in.PDS
      io.prefetch_valid := true.B
    }.otherwise {
      io.prefetch_address := 0.U
      io.prefetch_valid := false.B
    }
  }.otherwise {
    when(data_in.PDS === file(1023).PDS){
    io.prefetch_address := data_in.ADS + data_in.PDS
    io.prefetch_valid := true.B
    }.otherwise{
      io.prefetch_address := 0.U
      io.prefetch_valid := false.B
    }
  }
  count := count + 1.U
}
