package Prefetcher

import chisel3.{Wire, _}

class MarkovPrefetcher(val addressWidth: Int,val pcWidth: Int) extends Module {
  val io: MarkovPrefetcherIO = IO(new MarkovPrefetcherIO(addressWidth,pcWidth))
  private val Before = RegInit(-1.S(addressWidth.W))
  private val Register_Length = 128
  private val address_Register = Reg(Vec(Register_Length,new S_Part(addressWidth)))
  private val Initialize = RegInit(false.B)
  private val Method = new File_process_method(address_Register,Register_Length,addressWidth)
  private val prefetch = Wire(SInt(addressWidth.W))
  Method.initlize_Register(Initialize)
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
  //io.test := address_Register
}
  class S_Part(val addressWidth:Int) extends Bundle {
    var Origin: SInt = SInt(addressWidth.W)
    var Purpose: SInt = SInt(addressWidth.W)
    var Amount: SInt = SInt(32.W)
    var Vaild: Bool = Bool()
    def init(): S_Part = {
      this.Origin := -1.S(addressWidth.W)
      this.Purpose := -1.S(addressWidth.W)
      this.Amount := -1.S(32.W)
      this.Vaild := false.B
      this
    }
  }
  class File_process_method(File:Vec[S_Part] , length:Int, addressWidth:Int){
    def initlize_Register(Initlize:Bool): WhenContext ={
      when(Initlize === false.B) {
        Reset_Register()
        Initlize := true.B
      }
    }
    private def Reset_Register(): Unit = {
      for(i<-0 until length){
        File(i).init()
      }
    }
    private  def Find_Origin_Max(Origin:SInt):SInt={
      val (_, maxIndex) = File.zipWithIndex.foldLeft((-1.S, -1.S)) {
        case ((currMax, currIndex), (element, currentIndex)) =>
          val Dect_element = element.Amount
          val Dect_Origin = element.Origin
          val Dect_Vaild = element.Vaild
          val newMaxValue = Mux(Dect_Vaild && Dect_element > currMax && Dect_Origin === Origin, Dect_element   , currMax  )
          val newMaxIndex = Mux(Dect_Vaild && Dect_element > currMax && Dect_Origin === Origin, currentIndex.S , currIndex)
          (newMaxValue, newMaxIndex)
      }
      maxIndex
    }
    private def Find_Origin_Purpose(Origin:SInt,Purpose:SInt):SInt={
        val Index = File.zipWithIndex.foldRight(-1.S) {
        case ( (element, currentIndex), pur_Index:SInt) =>
          val Dect_Purpose = element.Purpose
          val Dect_Origin = element.Origin
          val Dect_Vaild = element.Vaild
          val newIndex = Mux((Dect_Vaild && Dect_Origin === Origin && Dect_Purpose === Purpose), currentIndex.S, pur_Index)
          newIndex
      }
      Index
    }
    private def Find_Min_Unused:SInt={
      val Unused_Index = File.zipWithIndex.foldRight(-1.S) {
        case ((element, currentIndex), unused_Index: SInt) =>
          val Dect_Vaild = element.Vaild
          val UNused_Index = Mux(Dect_Vaild === false.B, currentIndex.S, unused_Index)
          UNused_Index
      }
      Unused_Index
    }
    private def Delete(Position: SInt): UInt = {
      val Times = (0 until length-1).foldLeft(0.U) {
        case(time:UInt, index) =>
          when(index.S >= Position) {
            File(index) := File(index+1)
          }
          val newtime = time +1.U
          newtime
      }
      File(length-1).init()
      Times
    }

    def Insert(Origin:SInt,Purpose:SInt):UInt={
      val Dect_unused = Wire(SInt(16.W))
      val Dect_Exist = Wire(SInt(16.W))
      Dect_unused := Find_Min_Unused
      Dect_Exist := Find_Origin_Purpose(Origin,Purpose)
      when(Dect_Exist === -1.S) {
        when(Dect_unused === -1.S){
          Delete(0.S)
          File(length-1).Origin := Origin
          File(length-1).Purpose := Purpose
          File(length-1).Amount := 1.S
        }.otherwise{
          File(Dect_unused.asUInt).Origin := Origin
          File(Dect_unused.asUInt).Purpose := Purpose
          File(Dect_unused.asUInt).Amount := 1.S
          File(Dect_unused.asUInt).Vaild := true.B
        }

      }.otherwise{
        val Mem_Element = Wire(new S_Part(addressWidth))
        Mem_Element := File(Dect_Exist.asUInt)
        Mem_Element.Amount := File(Dect_Exist.asUInt).Amount + 1.S
        Delete(Dect_Exist)
        when(Dect_unused === -1.S){
          File(length-1) := Mem_Element
        }.otherwise{
          File(Dect_unused.asUInt-1.U) := Mem_Element
        }
      }
      0.U
    }

    def Prefech(Origin:SInt):SInt = {
      var Position = Wire(SInt(16.W))
      Position := -1.S
      Position = Find_Origin_Max(Origin)
      Mux(Position === -1.S,-1.S,File(Find_Origin_Max(Origin).asUInt).Purpose)
    }

  }