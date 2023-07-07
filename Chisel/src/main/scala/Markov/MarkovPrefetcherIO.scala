package Markov

import chisel3._
class MarkovPrefetcherIO(addressWidth:Int) extends Bundle{
  val Pc = Input(UInt(addressWidth.W))
  val Address = Input(UInt(addressWidth.W))
  val Re_Set = Input(Bool())
  val Prefetch_address = Output(UInt(addressWidth.W))
  val Prefetch_vaild = Output(Bool())
}
