package Prefetcher

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
class StridePrefetcherIO (val addressWidth: Int, val pcWidth: Int)extends Bundle{
    val pc  = Input(UInt(pcWidth.W))
    val address = Input(UInt(addressWidth.W))
    val prefetch_address  = Output(UInt(addressWidth.W))
    val prefetch_valid  = Output(Bool())
}