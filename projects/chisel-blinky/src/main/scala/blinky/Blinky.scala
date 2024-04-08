package blinky

import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class Blinky(freq: Int, startOn: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
  })
  val led = RegInit(startOn.B)
  val (_, counterWrap) = Counter(true.B, freq / 2)
  when(counterWrap) {
    led := ~led
  }
  io.led0 := led
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Blinky(10000000),
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--lowering-options=disallowPackedArrays",
        "--lowering-options=disallowLocalVariables"
      )
    )
  )
}
