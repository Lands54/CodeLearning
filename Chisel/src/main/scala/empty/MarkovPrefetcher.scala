package madd

import chisel3.{Wire, _}
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

class MarkovPrefetcher(val addressWidth: Int,val pcWidth: Int) extends Module {
  val io = IO(new MarkovPrefetcherIO(addressWidth,pcWidth))
  private val Before = Reg(SInt(addressWidth.W))
  private val Register_Length = 128
  val address_Register = Reg(Vec(Register_Length,new S_Part(addressWidth)))
  private val Method = new File_process_method(address_Register,Before,Register_Length,addressWidth)
  private val prefetch = Wire(SInt(addressWidth.W))
  println("Module Create")
  Method.Insert(Before,io.address.asSInt)
  prefetch := Method.Prefech(io.address.asSInt)
  when(prefetch === -1.S){
    io.prefetch_valid := false.B
    io.prefetch_address := 404.U
  }.otherwise{
    io.prefetch_valid := true.B
    io.prefetch_address := prefetch.asUInt
  }
  Before := io.address.asSInt
  io.test := address_Register
}

  class S_Part(val addressWidth:Int) extends Bundle {
    val Origin = SInt(addressWidth.W)
    val Purpose = SInt(addressWidth.W)
    val Amount = UInt(32.W)
    val Vaild = Bool()
    def S_Part() = {
      Origin := -1.S
      Purpose := -1.S
      Amount := 0.U
      Vaild := false.B
    }
  }
  class File_process_method(File:Vec[S_Part] , Before:SInt , length:Int, addressWidth:Int){
    def Find_Origin_Max(Origin:SInt):SInt={
      val (_, maxIndex) = File.zipWithIndex.foldLeft((0.U, -1.S)) {
        case ((currMax, currIndex), (element, currentIndex)) =>
          val Dect_element = element.Amount
          val Dect_Origin = element.Origin
          val newMaxValue = Mux(Dect_element > currMax && Dect_Origin === Origin, Dect_element   , currMax  )
          val newMaxIndex = Mux(Dect_element > currMax && Dect_Origin === Origin, currentIndex.S , currIndex)
          (newMaxValue, newMaxIndex)
      }
      maxIndex
    }

        
    def Find_Origin_Purpose(Origin:SInt,Purpose:SInt):SInt={
        val Index = File.zipWithIndex.foldRight((-1.S)) {
        case ( (element, currentIndex),(pur_Index:SInt)) =>
          val Dect_Purpose = element.Purpose
          val Dect_Origin = element.Origin
          val Dect_Vaild = element.Vaild
          val newIndex = Mux(Dect_Vaild === true.B && Dect_Origin === Origin && Dect_Purpose === Purpose, currentIndex.S, pur_Index)
          newIndex
      }
      Index
    }

    def Find_Min_Unused:SInt={
      val Unused_Index = File.zipWithIndex.foldRight((-1.S)) {
        case ((element, currentIndex),(unused_Index: SInt)) =>
          val Dect_Vaild = element.Vaild
          val UNused_Index = Mux(Dect_Vaild === false.B, currentIndex.S, unused_Index)
          UNused_Index
      }
      Unused_Index
    }


    def Delete(Position: SInt): UInt = {
      val Times = (0 until length-1).foldLeft(0.U) {
        case((time:UInt),(index)) =>
          when(index.S >= Position) {
            File(index) := File(index+1)
          }
          val newtime = time +1.U
          newtime
      }
      return Times
    }

    def Insert(Origin:SInt,Purpose:SInt):UInt={
      val Dect_unused = Wire(SInt(16.W))
      val Dect_Exist = Wire(SInt(16.W))
      Dect_unused := Find_Min_Unused
      Dect_Exist := Find_Origin_Purpose(Origin,Purpose)
      when(Dect_Exist === -1.S || (Origin === 0.S && Purpose===0.S && File(Dect_Exist.asUInt).Vaild ===false.B)) {
        when(Dect_unused === -1.S){
          Delete(0.S)
          File(length-1).Origin := Origin
          File(length-1).Purpose := Purpose
          File(length-1).Amount := 1.U
        }.otherwise{
          File(Dect_unused.asUInt).Origin := Origin
          File(Dect_unused.asUInt).Purpose := Purpose
          File(Dect_unused.asUInt).Amount := 1.U
          File(Dect_unused.asUInt).Vaild := true.B
        }
      }.otherwise{
        File(Dect_Exist.asUInt).Vaild := true.B
        val Mem_Element = Wire(new S_Part(addressWidth))
        Mem_Element := File(Dect_Exist.asUInt)
        Mem_Element.Amount := File(Dect_Exist.asUInt).Amount + 1.U
        Delete(Dect_Exist)
        when(Dect_unused === -1.S){
          File(length-1) := Mem_Element
        }.otherwise{
          File(Dect_unused.asUInt-1.U) := Mem_Element
        }
      }
      return 0.U
    }

    def Prefech(Origin:SInt):SInt = {
      var Position = Wire(SInt(16.W))
      Position := -1.S
      Position = Find_Origin_Max(Origin)
      return Mux(Position === -1.S,-1.S,File(Find_Origin_Max(Origin).asUInt).Purpose)
    }

  }