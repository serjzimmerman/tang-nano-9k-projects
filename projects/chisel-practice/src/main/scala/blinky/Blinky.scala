package blinky

import util.ClockDivider
import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class BlinkyIO(bits: Int) extends Bundle {
  val led = Output(UInt(bits.W))
}

class Blinky(bits: Int) extends Module {
  val io = IO(new BlinkyIO(bits))
  val led = RegInit(1.U(bits.W))

  led := (led << 1.U) | led(bits - 1)
  io.led := ~led
}

// https://stackoverflow.com/questions/76521229/how-to-deal-with-activelow-reset-change-implicit-clock-frequency
class Top(freq: Int, bits: Int) extends Module {
  val io = IO(new BlinkyIO(bits))
  val invertedReset = ~reset.asBool
  val dividedClock = withReset(reset = false.B) {
    ClockDivider(clock, freq)
  }
  val top = withClockAndReset(clock = dividedClock, reset = invertedReset) {
    Module(new Blinky(bits))
  }
  io <> top.io
}

object BlinkyVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new Top(27_000_000, 6),
    args = Array("--target-dir", "generated/blinky"),
    firtoolOpts = Array(
      "-disable-all-randomization",
      "-strip-debug-info",
      "--lowering-options=disallowPackedArrays",
      "--lowering-options=disallowLocalVariables"
    )
  )
}
