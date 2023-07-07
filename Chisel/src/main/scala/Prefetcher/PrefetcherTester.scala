package Prefetcher
import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

import scala.util.Random
import java.io._
import scala.collection.mutable.ArrayBuffer
class T_StridePrefetcherTester(dut:StridePrefetcher,Times:Int,Control_Pc:Int,Control_ad:Int)extends PeekPokeTester(dut){
  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  class PrefetcherDetecter(Input_PC: Array[Int], Input_Address: Array[Int]) {
    private var Undetected_Array = Input_PC.zip(Input_Address)
    private var prefetch_address: BigInt = 0
    private var prefetch_vaild: Boolean = false
    private var Current_PC: Int = 0
    private var Current_Address: Int = 0
    private var Accu_Number: Int = 0
    private var Fill_Number: Int = 0
    private var Total_Number: Int = 0
    private var Accurate: Double = 0
    private var Fillrate: Double = 0
    var Table: String = {
      "|Num|In_address|Pr_address|Pr_vaild|Real_address|Accurate|\n"
    }
    def init(): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
      prefetch_address = 0
      prefetch_vaild = false
      Current_PC = 0
      Current_Address = 0
    }
    def incount(): Unit = {
      Accu_Number = 0
      Fill_Number = 0
      Total_Number = 0
      Accurate = 0
      Fillrate = 0
    }
    def ChangeArray(Input_PC: Array[Int], Input_Address: Array[Int]): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
    }
    private def Dectecter(): Unit = {
      for (i <- 0 until Undetected_Array.length - 1) {
        Current_PC = Undetected_Array(i)._1
        Current_Address = Undetected_Array(i)._2
        poke(dut.io.pc, Current_PC.U)
        poke(dut.io.address, Current_Address.U)
        prefetch_address = peek(dut.io.prefetch_address)
        prefetch_vaild = peek(dut.io.prefetch_valid).B.litToBoolean
        Table += "|%3s|%10s|%10s|%8s|%12s|".format(i, Current_Address, prefetch_address, prefetch_vaild, Undetected_Array(i + 1)._2)
        if (prefetch_vaild) {
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
    def Tester(Times: Int): Unit = {
      for (_ <- 0 until Times) {
        Dectecter()
      }
    }
    private def Output_Result: (Double, Double) = {
      (Fillrate,Accurate)
    }
    def print_Result(): Unit = {
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    def PRINT(Control_Pc:Int,Control_Ad:Int): String = {
      ("StridePrefetcher:Mode :"+Control_Pc+"|"+Control_Ad)+("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    def Output_Table(): Unit = {
      print(Table)
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
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
  /*  private def Test_Register(): Unit = {
    print("|   Vaild|  Amount| Purpose|  Origin|Num|\n")
    for (i <- 0 until 128 * 4) {
      print("|%8s".format(peek(dut.io.test)(i)))
      if ((i + 1) % 4 == 0) {
        print(peek(dut.io.te).toString())
        print("|%3s|\n".format(i / 4))
      }
    }
  }*/
  private def Tree_Array(Length: Int, Head: Int): Array[Int] = {
    var buffer = new ArrayBuffer[Int](64)
    buffer += Head
    for (i <- 0 until Length) {
      buffer += (2 * (buffer(i) - Head) + Random.nextInt(2) + 1)
    }
    buffer.toArray
  }
  private def Linear_Recurrence_Array(Pc_Array: Array[Int], Range: Int, Head: Int): Array[Int] = {
    var Out_Array: Array[Int] = Array[Int](Pc_Array.length)
    val Pc: Int = 0
    var RandomInt: Int = 0
    for (Pc <- Pc_Array) {
      RandomInt = Random.nextInt(Range) - Range / 2 + Pc + Head
      if (RandomInt < 0) {
        Out_Array = Out_Array :+ 0
      } else {
        Out_Array = Out_Array :+ RandomInt
      }
    }
    Out_Array
  }
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
      case 4 => Linear_Recurrence_Array(Sequential_pattern(64), 3, 0)
      case 5 => Tree_Array(16, 0)
      case _ => Array.fill(65)(0)
    }
  }
  def DoingTest(Times: Int, Control_Pc: Int, Control_Ad: Int): Unit = {
    print("StridePrefetcher:Mode :" + Control_Pc + "|" + Control_Ad)
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
    Array_PC = Produce_Array(Control_Pc)
    Array_Adddress = Produce_Array(Control_Ad)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
  }
  def Main(Times: Int,Control_Pc:Int,Control_ad:Int): Unit = {
    val Record_File = new File("Record_StridePrefetcher.txt")
    val Writer = new BufferedWriter(new FileWriter(Record_File))
    DoingTest(Times, Control_Pc, Control_ad:Int)
    Writer.write(PrefetcherDetecter.Table)
    Writer.flush()
  }
  def pr(Control_Pc:Int,Control_ad:Int):String = {
    PrefetcherDetecter.PRINT(Control_Pc,Control_ad)
  }
  Main(Times,Control_Pc,Control_ad)
}
class T_MarkovPrefetcherTester(dut:MarkovPrefetcher,Times:Int,Control_Pc:Int,Control_ad:Int)extends PeekPokeTester(dut){
  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  private class PrefetcherDetecter(Input_PC: Array[Int], Input_Address: Array[Int]) {
    private var Undetected_Array = Input_PC.zip(Input_Address)
    private var prefetch_address: BigInt = 0
    private var prefetch_vaild: Boolean = false
    private var Current_PC: Int = 0
    private var Current_Address: Int = 0
    private var Accu_Number: Int = 0
    private var Fill_Number: Int = 0
    private var Total_Number: Int = 0
    private var Accurate: Double = 0
    private var Fillrate: Double = 0
    var Table: String = {
      "|Num|In_address|Pr_address|Pr_vaild|Real_address|Accurate|\n"
    }

    def init(): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
      prefetch_address = 0
      prefetch_vaild = false
      Current_PC = 0
      Current_Address = 0
    }

    def incount(): Unit = {
      Accu_Number = 0
      Fill_Number = 0
      Total_Number = 0
      Accurate = 0
      Fillrate = 0
    }
    def ChangeArray(Input_PC: Array[Int], Input_Address: Array[Int]): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
    }
    private def Dectecter(): Unit = {
      for (i <- 0 until Undetected_Array.length - 1) {
        Current_PC = Undetected_Array(i)._1
        Current_Address = Undetected_Array(i)._2
        poke(dut.io.pc, Current_PC.U)
        poke(dut.io.address, Current_Address.U)
        prefetch_address = peek(dut.io.prefetch_address)
        prefetch_vaild = peek(dut.io.prefetch_valid).B.litToBoolean
        Table += "|%3s|%10s|%10s|%8s|%12s|".format(i, Current_Address, prefetch_address, prefetch_vaild, Undetected_Array(i + 1)._2)
        if (prefetch_vaild) {
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
    def Tester(Times: Int): Unit = {
      for (_ <- 0 until Times) {
        Dectecter()
      }
    }
    private def Output_Result: (Double, Double) = {
      (Fillrate,Accurate)
    }
    def PRINT(Control_Pc: Int, Control_Ad: Int): String = {
      ("MarkovPrefetcher:Mode :" + Control_Pc + "|" + Control_Ad) + ("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    def print_Result(): Unit = {
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    def Output_Table(): Unit = {
      print(Table)
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
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
  /*  private def Test_Register(): Unit = {
    print("|   Vaild|  Amount| Purpose|  Origin|Num|\n")
    for (i <- 0 until 128 * 4) {
      print("|%8s".format(peek(dut.io.test)(i)))
      if ((i + 1) % 4 == 0) {
        print(peek(dut.io.te).toString())
        print("|%3s|\n".format(i / 4))
      }
    }
  }*/
  private def Tree_Array(Length: Int, Head: Int): Array[Int] = {
    var buffer = new ArrayBuffer[Int](64)
    buffer += Head
    for (i <- 0 until Length) {
      buffer += (2 * (buffer(i) - Head) + Random.nextInt(2) + 1)
    }
    buffer.toArray
  }
  private def Linear_Recurrence_Array(Pc_Array: Array[Int], Range: Int, Head: Int): Array[Int] = {
    var Out_Array: Array[Int] = Array[Int](Pc_Array.length)
    val Pc: Int = 0
    var RandomInt: Int = 0
    for (Pc <- Pc_Array) {
      RandomInt = Random.nextInt(Range) - Range / 2 + Pc + Head
      if (RandomInt < 0) {
        Out_Array = Out_Array :+ 0
      } else {
        Out_Array = Out_Array :+ RandomInt
      }
    }
    Out_Array
  }
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
      case 4 => Linear_Recurrence_Array(Sequential_pattern(64), 3, 0)
      case 5 => Tree_Array(16, 0)
      case _ => Array.fill(65)(0)
    }
  }
  def DoingTest(Times: Int, Control_Pc: Int, Control_Ad: Int): Unit = {
    print("MarkovPrefetcher:Mode :" + Control_Pc + "|" + Control_Ad)
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
    Array_PC = Produce_Array(Control_Pc)
    Array_Adddress = Produce_Array(Control_Ad)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
  }
  def Main(Times:Int,Control_Pc:Int,Control_ad:Int):Unit = {
    val Record_File = new File("Record_MarkovPrefetcher.txt")
    val Writer = new BufferedWriter(new FileWriter(Record_File))
    DoingTest(Times, Control_Pc, Control_ad)
    Writer.write(PrefetcherDetecter.Table)
    Writer.flush()
  }
  def pr(Control_Pc:Int,Control_ad:Int):String= {
    PrefetcherDetecter.PRINT(Control_Pc,Control_ad)
  }

  Main(Times,Control_Pc,Control_ad)
}
class T_RememberStridePrefetcherTester(dut:RememberStridePrefetcher,Times:Int,Control_Pc:Int,Control_ad:Int)extends PeekPokeTester(dut){
  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  private class PrefetcherDetecter(Input_PC: Array[Int], Input_Address: Array[Int]) {
    private var Undetected_Array = Input_PC.zip(Input_Address)
    private var prefetch_address: BigInt = 0
    private var prefetch_vaild: Boolean = false
    private var Current_PC: Int = 0
    private var Current_Address: Int = 0
    private var Accu_Number: Int = 0
    private var Fill_Number: Int = 0
    private var Total_Number: Int = 0
    private var Accurate: Double = 0
    private var Fillrate: Double = 0
    var Table: String = {
      "|Num|In_address|Pr_address|Pr_vaild|Real_address|Accurate|\n"
    }
    def init(): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
      prefetch_address = 0
      prefetch_vaild = false
      Current_PC = 0
      Current_Address = 0
    }
    def incount(): Unit = {
      Accu_Number = 0
      Fill_Number = 0
      Total_Number = 0
      Accurate = 0
      Fillrate = 0
    }
    def ChangeArray(Input_PC: Array[Int], Input_Address: Array[Int]): Unit = {
      Undetected_Array = Input_PC.zip(Input_Address)
    }
    private def Dectecter(): Unit = {
      for (i <- 0 until Undetected_Array.length - 1) {
        Current_PC = Undetected_Array(i)._1
        Current_Address = Undetected_Array(i)._2
        poke(dut.io.pc, Current_PC.U)
        poke(dut.io.address, Current_Address.U)
        prefetch_address = peek(dut.io.prefetch_address)
        prefetch_vaild = peek(dut.io.prefetch_valid).B.litToBoolean
        Table += "|%3s|%10s|%10s|%8s|%12s|".format(i, Current_Address, prefetch_address, prefetch_vaild, Undetected_Array(i + 1)._2)
        if (prefetch_vaild) {
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
    def Tester(Times: Int): Unit = {
      for (_ <- 0 until Times) {
        Dectecter()
      }
    }
    def PRINT(Control_Pc: Int, Control_Ad: Int): String = {
      ("RemStrPrefetcher:Mode :" + Control_Pc + "|" + Control_Ad) + ("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    private def Output_Result: (Double, Double) = {
      (Fillrate,Accurate)
    }
    def print_Result(): Unit = {
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
    }
    def Output_Table(): Unit = {
      print(Table)
      print("(FILLRATE,ACCURATE)=" + Output_Result + "\n")
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
  /*  private def Test_Register(): Unit = {
    print("|   Vaild|  Amount| Purpose|  Origin|Num|\n")
    for (i <- 0 until 128 * 4) {
      print("|%8s".format(peek(dut.io.test)(i)))
      if ((i + 1) % 4 == 0) {
        print(peek(dut.io.te).toString())
        print("|%3s|\n".format(i / 4))
      }
    }
  }*/
  private def Tree_Array(Length:Int,Head:Int):Array[Int] = {
    var buffer = new ArrayBuffer[Int](64)
    buffer += Head
    for(i <- 0 until  Length){
      buffer += (2*(buffer(i) - Head) + Random.nextInt(2) + 1)
    }
    buffer.toArray
  }
  private def Linear_Recurrence_Array(Pc_Array:Array[Int],Range:Int,Head:Int):Array[Int] = {
    var Out_Array:Array[Int] = Array[Int]()
    val Pc:Int =0
    var RandomInt:Int = 0
    for(Pc <- Pc_Array){
      RandomInt = Random.nextInt(Range) - Range/2 + Pc + Head
      if(RandomInt<0){
        Out_Array = Out_Array :+ 0
      }else{
        Out_Array = Out_Array :+ RandomInt
      }
    }
    Out_Array
  }
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
      case 4 => Linear_Recurrence_Array(Sequential_pattern(64),3,0)
      case 5 => Tree_Array(16,0)
      case _ => Array.fill(65)(0)
    }
  }
  def DoingTest(Times: Int, Control_Pc: Int,Control_Ad:Int): Unit = {
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
    print("RememberPrefetcher:Mode :"+Control_Pc+"|"+Control_Ad)
    Array_PC = Produce_Array(Control_Pc)
    Array_Adddress = Produce_Array(Control_Ad)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
  }
  def Main(Times: Int,Control_Pc:Int,Control_ad:Int): Unit = {
    val Record_File = new File("Record_RememberStridePrefetcher.txt")
    val Writer = new BufferedWriter(new FileWriter(Record_File))
    DoingTest(Times, Control_Pc, Control_ad)
    Writer.write(PrefetcherDetecter.Table)
    Writer.flush()
  }
  def pr(Control_Pc:Int,Control_ad:Int):String = {
    PrefetcherDetecter.PRINT(Control_Pc,Control_ad)
  }
  Main(Times, Control_Pc, Control_ad)
}
object PrefetcherTester extends App {
  val TIMES:Int = 10
  var Control_Pc:Int = 0
  val Action_ad:Int = 0
  val Writer = new BufferedWriter(new FileWriter("Record_Result.txt"))
  for(Action_ad <- 0 until 6) {
    val tester1 = chisel3.iotesters.Driver.execute(args, () => new StridePrefetcher(32, 32)) {
      c => {
        val tester = new T_StridePrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        Writer.write(tester.pr(Control_Pc, Action_ad))
        //new T_StridePrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        tester
      }
    }
    val tester2 = chisel3.iotesters.Driver.execute(args, () => new MarkovPrefetcher(32, 32)) {
      c => {
        val tester = new T_MarkovPrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        Writer.write(tester.pr(Control_Pc, Action_ad))
        //new T_MarkovPrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        tester
      }
    }
    val tester3 = chisel3.iotesters.Driver.execute(args, () => new RememberStridePrefetcher(32, 32)) {
      c => {
        val tester = new T_RememberStridePrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        Writer.write(tester.pr(Control_Pc, Action_ad))
        //new T_RememberStridePrefetcherTester(c, TIMES, Control_Pc, Action_ad)
        tester
      }
    }
  }
  Writer.flush()
}