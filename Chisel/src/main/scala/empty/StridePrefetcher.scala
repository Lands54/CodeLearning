package madd

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  
  val io = IO(new StridePrefetcherIO)
  
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
  
  val count = RegInit(0.U(32.W))
  when(count >= 1024.U) {
    count := 0.U
  }

  val data_in = Wire(new List)
  data_in.ADS := io.address
  data_in.PCS := io.pc
  
  val file = Reg(Vec(1024, new List))
  
  when(count > 0.U) {
    data_in.PDS := data_in.ADS - file(count-1.U).ADS
  }.otherwise {
    data_in.PDS := 4.U
  }

  file(count) := data_in

  when(count > 0.U) {
    when(data_in.PDS === file(count-1.U).PDS) {
      io.prefetch_address := data_in.ADS + data_in.PDS
      io.prefetch_valid := true.B
    }.otherwise {
      io.prefetch_address := data_in.ADS + data_in.PDS
      io.prefetch_valid := false.B
    }
  }.otherwise {
    io.prefetch_address := 4.U
    io.prefetch_valid := true.B
  }
  count := count + 1.U
}
