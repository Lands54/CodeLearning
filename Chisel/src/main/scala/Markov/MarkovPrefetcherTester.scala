package Markov

import chisel3._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._

import scala.collection.mutable.Set
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.stage.ChiselStage

import scala.util.Random

class MarkovPrefetcherTester(dut: MarkovPrefetcher,Length:Int)extends PeekPokeTester(dut) {
  private val array_method = new Generate_Array(Length)
  private val Times:Int = 2
  for(j <- 0 until 4){
    val input_array = array_method.Produce_Array(j)
    var fill_number:Float = 0
    var hits_number:Float = 0
    var total_number:Float = 0
    poke(dut.io.Re_Set,true.B)
    step(1)
    poke(dut.io.Re_Set,false.B)
    for(_ <-0 until Times){
      for (i <- 0 until Length) {
        poke(dut.io.Pc, input_array(i).U)
        poke(dut.io.Address, input_array(i).U)
        total_number += 1
        step(1)
        if (peek(dut.io.Prefetch_vaild) == 1) {
          fill_number += 1
          if (peek(dut.io.Prefetch_address) == input_array(i + 1)) {
            hits_number += 1
          }
        }
      }
    }
    print("(Fillrate,Accurate) = %6.3f,%6.3f\n".format(fill_number/total_number*100,hits_number/fill_number*100))
  }
}
class Generate_Array(Length:Int) {
  def Produce_Array(Control:Int):Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(Length)
      case 1 => Strided_pattern(0,2*Length,2)
      case 2 => Interleaved_Pattern(Sequential_pattern(Length),Strided_pattern(0,2*Length,2))
      case 3 => RandomArray(Length,0,1024)
    }
  }
  private def Sequential_pattern(End: Int): Array[Int] = {
    Strided_pattern(0, End, 1)
  }
  private def Strided_pattern(Begin: Int, End: Int, Num_Gap: Int): Array[Int] = {
    Range(Begin, End + Num_Gap, Num_Gap).toArray
  }
  private def Interleaved_Pattern(Part_Array_1: Array[Int], Part_Array_2: Array[Int]): Array[Int] = {
    val maxLength = math.max(Part_Array_1.length, Part_Array_2.length)
    val pattern = for {
      i <- 0 until maxLength
      value1 = Part_Array_1(i)
      value2 = Part_Array_2(i)
    } yield {
      List(value1, value2)
    }
    pattern.flatten.toArray
  }
  private def RandomArray(length: Int, minValue: Int, maxValue: Int): Array[Int] = {
    val random = new Random()
    Array.fill(length + 1)(random.nextInt(maxValue - minValue + 1) + minValue)
  }
}

object MarkovPrefetcherTester extends App {
  chisel3.iotesters.Driver.execute(args, () => new MarkovPrefetcher(128, 32)) {
    dut => {
      new MarkovPrefetcherTester(dut, 64)
    }
  }
}