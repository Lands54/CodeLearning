package Prefetcher
import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import scala.util.Random
import java.io._
class T_StridePrefetcherTester(dut:StridePrefetcher)extends PeekPokeTester(dut){
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
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
    }
  }

  def DoingTest(Times: Int, Control: Int): Unit = {
    print("StridePrefetcher:Mode ",Control)
    Array_PC = Produce_Array(Control)
    Array_Adddress = Produce_Array(Control)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
  }

  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  val Record_File = new File("Record_StridePrefetcher.txt")
  val Writer = new FileWriter(Record_File,false)
  DoingTest(2, 0)
  DoingTest(2, 1)
  DoingTest(2, 2)
  DoingTest(3, 3)
  Writer.write(PrefetcherDetecter.Table)
}
class T_MarkovPrefetcherTester(dut:MarkovPrefetcher)extends PeekPokeTester(dut){
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
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
    }
  }

  def DoingTest(Times: Int, Control: Int): Unit = {
    print("MarkovPrefetcher:Mode :", Control)
    Array_PC = Produce_Array(Control)
    Array_Adddress = Produce_Array(Control)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
  }


  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  val Record_File = new File("Record_MarkovPrefetcher.txt")
  val Writer = new FileWriter(Record_File,false)
  DoingTest(2, 0)
  DoingTest(2, 1)
  DoingTest(2, 2)
  DoingTest(3, 3)
  Writer.write(PrefetcherDetecter.Table)
}
class T_RememberStridePrefetcherTester(dut:RememberStridePrefetcher)extends PeekPokeTester(dut){
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
  private def Produce_Array(Control: Int): Array[Int] = {
    Control match {
      case 0 => Sequential_pattern(64)
      case 1 => Strided_pattern(64, 192, 2)
      case 2 => Interleaved_Pattern(Strided_pattern(0, 128, 2), Strided_pattern(52, 500, 7))
      case 3 => RandomArray(64, 0, 128)
    }
  }

  def DoingTest(Times: Int, Control: Int): Unit = {
    print("RememberPrefetcher:Mode :",Control)
    Array_PC = Produce_Array(Control)
    Array_Adddress = Produce_Array(Control)
    PrefetcherDetecter.ChangeArray(Array_PC, Array_Adddress)
    PrefetcherDetecter.Tester(Times)
    //PrefetcherDetecter.Output_Table()
    PrefetcherDetecter.print_Result()
    PrefetcherDetecter.init()
    PrefetcherDetecter.incount()
  }


  private var Array_PC = Produce_Array(0)
  private var Array_Adddress = Produce_Array(0)
  private val PrefetcherDetecter: PrefetcherDetecter = new PrefetcherDetecter(Array_PC, Array_Adddress)
  val Record_File = new File("Record_RememberStridePrefetcher.txt")
  val Writer = new FileWriter(Record_File,false)
  DoingTest(2, 0)
  DoingTest(2, 1)
  DoingTest(2, 2)
  DoingTest(3, 3)
  Writer.write(PrefetcherDetecter.Table)
}
object PrefetcherTester extends App {
  chisel3.iotesters.Driver.execute(args, () => new StridePrefetcher(32, 32)) {
    c => new T_StridePrefetcherTester(c)
  }
  chisel3.iotesters.Driver.execute(args, () => new MarkovPrefetcher(32, 32)) {
    c => new T_MarkovPrefetcherTester(c)
  }
  chisel3.iotesters.Driver.execute(args, () => new RememberStridePrefetcher(32, 32)) {
    c => new T_RememberStridePrefetcherTester(c)
  }
}