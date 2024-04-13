package projects

import chisel3.util.log2Up
import chisel3.util.Counter
import chisel3.util.PopCount
import chisel3._
import circt.stage.ChiselStage

class ParallelCarryCounter(maxValue: Int) extends Module {
  val bitWidth: Int = log2Up(maxValue)

  val io = IO(new Bundle {
    val count = Output(UInt(bitWidth.W))
    val overflow = Output(Bool())
  })

  val value = RegInit(VecInit(Seq.fill(bitWidth)(false.B)))

  value(0) := value(0) ^ 1.U(1.W)
  for (i <- 1 until bitWidth) {
    value(i) := value(i) ^ value
      .slice(0, i)
      .map(_.asBool)
      .reduce(_ & _)
  }

  io.count := value.asUInt
  io.overflow := (value.asUInt === (maxValue - 1).U(bitWidth.W))
  when(io.overflow) {
    for (i <- 0 until bitWidth) { value(i) := false.B }
  }
}
