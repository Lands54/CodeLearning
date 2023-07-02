package madd

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class MarkovPrefetcher(val addressWidth: Int,val pcWidth: Int) extends Module {
  val io = IO(new MarkovPrefetcherIO(addressWidth,pcWidth))
  val address_Register = new Store_File(addressWidth)
  address_Register.Method.Insert((address_Register.Before),io.address)
  address_Register.Method.Save_now(io.address)
  io.prefetch_address := address_Register.Method.Prefech(io.address).asUInt
  when(io.prefetch_address.asSInt === -1.S){
    io.prefetch_valid := false.B
  }.otherwise{
    io.prefetch_valid := true.B
  }

}
  class S_Part(val addressWidth:Int) extends Bundle {
    val Origin = UInt(addressWidth.W)
    val Purpose = UInt(addressWidth.W)
    val Amount = UInt(32.W)
    val Vaild = Bool()
    def S_Part() = {
      Origin := 0.U
      Purpose := 0.U
      Amount := 0.U
      Vaild := false.B
    }
    def Check_Origin:UInt={
      return this.Origin
    }
    def Check_Purpose:UInt={
      return this.Purpose
    }
    def Check_Amount:UInt={
      return this.Amount
    }
    def Check_Vaild:Bool={
      return this.Vaild
    }
  }

  class Store_File(val addressWidth:Int) extends Bundle{
    val File = Reg(Vec(1024,new S_Part(addressWidth)))
    val Before = RegInit(0.U(addressWidth.W))
    val Method = new File_process_method(File,Before,addressWidth)
  }

  class File_process_method(File:Vec[S_Part] , Before:UInt , addressWidth:Int){
    def Save_now(Purpose:UInt):UInt={
      this.Before := Purpose
      return 0.U
    }
    def Out_Before:UInt={
      return Before.asUInt
    }
    def Find_Origin_Max(Origin:UInt):SInt={
        val max = Wire(UInt(addressWidth.W))
        val position = Wire(SInt(16.W))
        max:= 0.U
        position := -1.S
        for(i <- 0 until 1023){
            when(this.File(i).Check_Vaild){
              when(this.File(i).Check_Amount>max){
                when(this.File(i).Check_Origin === Origin){
                  position := i.S
                  max := this.File(i).Check_Amount}.otherwise{}
              }.otherwise{}
            }.otherwise{}
        }
        return position
    }
    def Find_Origin_Purpose(Origin:UInt,Purpose:UInt):SInt={
        val max = Wire(UInt(addressWidth.W))
        val position = Wire(SInt(16.W))
        max:= 0.U
        position := -1.S
        for(i <- 0 until 1023){
            when(this.File(i).Check_Vaild){
              when(this.File(i).Check_Amount>max){
                when(this.File(i).Check_Origin === Origin){
                  when(this.File(i).Check_Purpose === Purpose){
                    position := i.S
                    max := this.File(i).Check_Amount
                    }.otherwise{}
                  }.otherwise{}
              }.otherwise{}
            }.otherwise{}
        }
        return position
    }

    def Find_Min_Unused:SInt={
      val position = Wire(SInt(16.W))
      val dective = Wire(Bool())
      position := -1.S
      dective := false.B
      for(i <- 0 until 1023){
        when(!(this.File(i).Check_Vaild)){
          when(!dective){
            position := i.S
          }.otherwise{}
        }.otherwise{}
      }
      return position
    }

    def Delete(Position:SInt):UInt = {
      for(i <- 0 until 1022){
        when(i.S>=Position){
          this.File(i).Origin := this.File(i+1).Origin
          this.File(i).Purpose := this.File(i+1).Purpose
          this.File(i).Amount := this.File(i+1).Amount
        }.otherwise{}
      }
      return 0.U
    }

    def Insert(Origin:UInt,Purpose:UInt):UInt={
      val Dect_unused = Wire(SInt(16.W))
      val Dect_Exist = Wire(SInt(16.W))
      val Mem_Element = Wire(new S_Part(addressWidth))
      Dect_unused := Find_Min_Unused
      Dect_Exist := Find_Origin_Purpose(Origin,Purpose)
      when(Dect_Exist === -1.S){
        when(Dect_unused === -1.S){
          Delete(0.S)
          this.File(1023.U).Origin := Origin
          this.File(1023.U).Purpose := Purpose
          this.File(1023.U).Amount := 1.U
        }.otherwise{
          this.File(Dect_unused.asUInt).Origin := Origin
          this.File(Dect_unused.asUInt).Purpose := Purpose
          this.File(Dect_unused.asUInt).Amount := 1.U
          this.File(Dect_unused.asUInt).Vaild := true.B
        }
      }.otherwise{
        Mem_Element := this.File(Dect_Exist.asUInt)
        Delete(Dect_Exist)
        when(Dect_unused === -1.S){
          this.File(1023.U) := Mem_Element
        }.otherwise{
          this.File(Dect_unused.asUInt) := Mem_Element
        }
      }
      return 0.U
    }

    def Prefech(Origin:UInt):SInt = {
      val Position = Wire(SInt(16.W))
      Position := Find_Origin_Max(Origin)
      return Mux(Position === -1.S,-1.S,this.File(Find_Origin_Max(Origin).asUInt).Check_Purpose.asSInt)
    }
    }

