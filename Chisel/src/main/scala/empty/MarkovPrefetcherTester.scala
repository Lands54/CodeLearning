package madd

import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import scala.util.Random

class MarkovPrefetcherTester(dut:MarkovPrefetcher)extends PeekPokeTester(dut) {
  class MarkovPrefetcherDetecter(Input_PC:Array[Int],Input_Address:Array[Int]){
    private var Undetected_Array = Input_PC.zip(Input_Address)
    private var prefetch_address:BigInt = 0
    private var prefetch_vaild:Boolean = false
    private var Current_PC:Int = 0
    private var Current_Address:Int = 0
    private var Accu_Number:Int = 0
    private var Fill_Number:Int = 0
    private var Total_Number:Int =0
    private var Accurate: Double = 0
    private var Fillrate: Double = 0
    private var Table:String = {"|Num|In_address|Pr_address|Pr_vaild|Real_address|Accurate|\n"}
    def Reset(Input_PC:Array[Int],Input_Address:Array[Int]): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
      prefetch_address = 0
      prefetch_vaild = false
      Current_PC = 0
      Current_Address = 0
      Accu_Number = 0
      Fill_Number = 0
      Total_Number = 0
      Accurate = 0
      Fillrate = 0
      Table = {"|Num|In_address|Pr_address|Pr_vaild|Real_address|Accurate|\n"}
    }
    def ChangeArray(Input_PC:Array[Int],Input_Address:Array[Int])={
      Undetected_Array = Input_PC.zip(Input_Address)
    }

    def Dectecter{
      for (i <- 0 until Undetected_Array.length - 1) {
        Current_PC = Undetected_Array(i)._1
        Current_Address = Undetected_Array(i)._2
        poke(dut.io.pc, Current_PC.U)
        poke(dut.io.address, Current_Address.U)
        prefetch_address = peek(dut.io.prefetch_address)
        prefetch_vaild = peek(dut.io.prefetch_valid).B.litToBoolean
        Table += "|%3s|%10s|%10s|%8s|%12s|".format(i,Current_Address,prefetch_address,prefetch_vaild,Undetected_Array(i+1)._2)
        if (prefetch_vaild == true) {
          Fill_Number += 1
          if (prefetch_address == Undetected_Array(i + 1)._2) {
            Table += "    true|\n"
            Accu_Number += 1
          }
          else {
            Table += "   false|\n"
          }
        }
        else {
          Table += "    None|\n"
        }
        Total_Number += 1
        step(1)
      }
      Accurate = (Accu_Number.toDouble / Fill_Number.toDouble) * 100
      Fillrate = (Fill_Number.toDouble / Total_Number.toDouble) * 100
    }

    private def Output_Result: (Double, Double) = {
      (Accurate, Fillrate)
    }
    def Output_Table{
      print(Table)
      print("(FILLRATE,ACCURATE)="+ Output_Result+"\n")
    }
  }
  private def Sequential_pattern(End: Int): Array[Int] = {
    Strided_pattern(0, End, 1)
  }
  private def Strided_pattern(Begin:Int,End:Int,Num_Gap: Int):Array[Int]= {
    Range(Begin,End,Num_Gap).toArray
  }
  private def Interleaved_Pattern(Part_Array_1:Array[Int], Part_Array_2:Array[Int]): Array[Int] = {
    val maxLength = math.max(Part_Array_1.length,Part_Array_2.length)
    val pattern = for {
      i <- 0 until maxLength
      value1 = Part_Array_1(i)
      value2 = Part_Array_2(i)
    }yield{
      List(value1,value2)
    }
    pattern.flatten.toArray
  }
  private def RandomArray(length: Int, minValue: Int, maxValue: Int):Array[Int] = {
    val random = new Random()
    Array.fill(length)(random.nextInt(maxValue - minValue + 1) + minValue)
  }
  val array:Array[Int] = RandomArray(1,0,0)
  var Tester:MarkovPrefetcherDetecter = new MarkovPrefetcherDetecter(Sequential_pattern(20),Sequential_pattern(20))
  Tester.Reset(array,array)
  for(i <- 0 until 1){
    Tester.Dectecter
  }
  Tester.Output_Table
  Test_Register
  def Test_Register{
    print("|   Vaild|  Amount| Purpose|  Origin|Num|\n")
    for (i <- 0 until 128 * 4) {
      print("|%8s".format(peek(dut.io.test)(i)))
      if ((i + 1) % 4 == 0) print("|%3s|\n".format(i/4))
    }
  }

}

/*class MarkovPrefetcherTesterSpec extends ChiselFlatSpec {
  private val addressWidth = 32
  private val pcWidth = 32
  private val backendName = "firrtl"

  "MarkovPrefetcherTester" should s"work correctly with $backendName backend" in {
    Driver(() => new MarkovPrefetcherTester(addressWidth, pcWidth), backendName) { dut =>
      new MarkovPrefetcherTester(dut)
    } should be(true)
  }
}
*/
object MarkovPrefetcherTester extends App {
  
  chisel3.iotesters.Driver.execute(args, () => new MarkovPrefetcher(32, 32)) {
    c => new MarkovPrefetcherTester(c)
  }
}
