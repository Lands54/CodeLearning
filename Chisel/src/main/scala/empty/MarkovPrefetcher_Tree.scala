/*
package madd

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}
class MarkovPrefetcher(val addressWidth: Int,val pcWidth: Int) extends Module{
    val io=IO(new MarkovPrefetcherIO(val addressWidth: Int,val pcWidth: Int));
    def swap 
    class S_Unit extends Bundle{
        var origin = UInt(addressWidth.W)
        var purpose = UInt(addressWidth.W)
        var amount = UInt(64.W)
        var left = new S_Unit
        var right = new S_Unit 
        def S_Unit() = {
            origin := 0.U
            purpose := 0.U
            amount := 0.U
        }
        def getleft() = {
            return this.left
        }
        def getright() = {
            return this.right
        }
        def setleft(S_Unit left) = {
            this.left=left
        }
        def setright(S_Unit right) = {
            this.right=right
        }
    }
    class S_Tree() extends Bundle{
        var root = new S_Unit 
        def maintain(S_Unit Node) = {
            var currentNode = root
            if(Node.getleft.origin == 0.U&&Node.getright.origin == 0.U)
            return
            maintain(Node.getleft)
            maintain(Node.getright)
            while(1){
                if(Node.getleft.origin>root.origin)swap(Node.getleft,Node)
                if(Node.getright.origin<root.origin)swap(Node.getright,Node)
                if(Node.left.origin<Node.origin&&Node.right.origin>Node.origin)
                return
            }
    }
    File = Reg(Vec(1024,new S_Unit))


}
*/