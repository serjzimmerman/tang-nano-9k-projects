package blinky

import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class Blinky(freq: Int, bits: Int) extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(bits.W))
  })
  val led = RegInit(1.U(bits.W))
  val (_, counterWrap) = Counter(true.B, freq / 2)
  when(counterWrap) {
    led := (led << 1.U) | led(bits - 1)
  }
  io.led := ~led
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Blinky(10000000, 6),
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--lowering-options=disallowPackedArrays",
        "--lowering-options=disallowLocalVariables"
      )
    )
  )
}
