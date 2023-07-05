package Prefetcher

import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class RememberStridePrefetcherTester(dut:RememberStridePrefetcher)extends PeekPokeTester(dut) {
  var acc = BigInt(0)
  var ful = BigInt(0)
  var count = BigInt(0)
  val num_gap_1 = 4.U
  val num_gap_2 = 8.U
  poke(dut.io.pc,0)
  poke(dut.io.address,0)
  for(i <-num_gap_1 to 40959.U by num_gap_1)
  {
    if(peek(dut.io.prefetch_valid)==1){
      ful = ful + 1
      if(peek(dut.io.prefetch_address)==i){
        acc = acc + 1
      }}
    step(1)
    poke(dut.io.pc,i)
    poke(dut.io.address,i)
    count = count+1
  }
  println("0,4,8..ACCURATE is %f%%(num:%d)".format((acc.toDouble/ful.toDouble)*100,count))
  println("0,4,8..FULLRATE is %f%%(num:%d)".format((ful.toDouble/count.toDouble)*100,count))
  var acct = BigInt(0)
  var fult = BigInt(0)
  var countt = BigInt(0)
  poke(dut.io.pc,0)
  poke(dut.io.address,0)
  for(i <-num_gap_2 to 40959.U by num_gap_2)
  {
    if(peek(dut.io.prefetch_valid)==1){
      fult = fult + 1
      if(peek(dut.io.prefetch_address)==i){
        acct = acct + 1
      }}
    step(1)
    poke(dut.io.pc,0)
    poke(dut.io.address,i)
    countt = countt+1
  }
  //print(peek(dut.io.test))
  println("0,8,16..ACCURATE is %f%%(num:%d)".format((acct.toDouble/fult.toDouble)*100,countt))
  println("0,8,16..FULLRATE is %f%%(num:%d)".format((fult.toDouble/countt.toDouble)*100,countt))
  println("_________________________________")
  var total_count = count + countt
  var total_acc = acc + acct
  var total_ful = ful + fult
  println("\nTotal Test Number:%d\nTotal ACCURATE:%f%%\nTotal FULLRATE:%f%%".format(total_count,100*total_acc.toDouble/total_ful.toDouble,100*total_ful.toDouble/total_count.toDouble))
}

object RememberStridePrefetcherTester extends App {
  chisel3.iotesters.Driver.execute(args, () => new RememberStridePrefetcher(32, 32)) {
    c => new RememberStridePrefetcherTester(c)
  }
}
