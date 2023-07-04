package Prefetcher

import scala.collection.mutable.Map
import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
class RememberStridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  val io = IO(new RememberStridePrefetcherIO(addressWidth, pcWidth))
  val Max_Size = 128
  val Rem_Pc_Register = new Register(pcWidth, addressWidth,Max_Size.U)
  val Before = RegInit(0.U(addressWidth.W))
  val Stride = Wire(SInt(addressWidth.W))
  val prefetch = Wire(SInt(addressWidth.W))
  val prefetch_add = Wire(SInt(addressWidth.W))
  prefetch_add := 0.S
  Stride := io.address.asSInt - Before.asSInt
  prefetch := Rem_Pc_Register.Method.Prefetch(io.pc,Stride,Rem_Pc_Register.Length)
  when(prefetch === -1.S){
    io.prefetch_address := 404.U
    io.prefetch_valid := false.B
  }.otherwise{
    prefetch_add := prefetch + io.address.asSInt
    when(prefetch_add < 0.S){
      io.prefetch_address := 404.U
      io.prefetch_valid := false.B
    }.otherwise {
      io.prefetch_address := prefetch_add.asUInt
      io.prefetch_valid := true.B
    }
  }
  //io.test := Rem_Pc_Register.Pc_Register
  Before := io.address
}
class Register(pcWidth:Int,addressWidth:Int,Max_Size:UInt){
  val Pc_Register = Reg(Vec(Max_Size.litValue.toInt,new LU_Part(pcWidth, addressWidth)))
  val Length = RegInit(0.U(32.W))
  val Method = new Lookup_method(pcWidth, addressWidth,Pc_Register)
  Method.Reset_Count(Length,Max_Size)
}
class LU_Part(pcWidth:Int,addressWidth:Int)extends Bundle {
  val PC = UInt(pcWidth.W)
  var Stride = SInt(addressWidth.W)
}
class Lookup_method(pcWidth:Int,addressWidth:Int,File:Vec[LU_Part]){
  def Find_Pc(Pc:UInt):SInt={
    val Index = File.zipWithIndex.foldRight(-1.S) {
      case ((element, nowIndex), pc_Index:SInt) =>
        val Dect_Pc = element.PC
        val Dect_Re = Mux(Dect_Pc === Pc,nowIndex.S,pc_Index)
        Dect_Re
    }
    Index
  }
  def add(PC:UInt,Stride:SInt,Length:UInt): UInt = {
    File(Length).PC := PC
    File(Length).Stride := Stride
    Length := Length + 1.U
    0.U
  }
  def Replace_Stride(Index:UInt,Stride:SInt): UInt = {
    File(Index).Stride := Stride
    0.U
  }
  def Prefetch(PC:UInt,Stride:SInt,Length:UInt):SInt = {
    val Index = Wire(SInt(pcWidth.W))
    Index := Find_Pc(PC)
    when(Index === -1.S){
      add(PC,Stride,Length)
    }.otherwise{
     Replace_Stride(Index.asUInt,Stride)
    }
    Mux(Index === -1.S,-1.S,File(Index.asUInt).Stride)
  }
  def Reset_Count(Length:UInt,Max_Size:UInt): Unit = {
    Length := Length % Max_Size
  }

}


