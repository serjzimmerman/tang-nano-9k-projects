package blinky

import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

object ClockDivider {
  def apply(
      clockIn: Clock,
      divideBy: Int
  ): Clock = {
    require(divideBy % 2 == 0, "Must divide by an even factor")

    val outputClock = Wire(Clock())
    withClock(clock = clockIn) {
      val dividedClock = RegInit(false.B)
      val max: Int = divideBy / 2
      val (_, counterWrap) = Counter(true.B, max)
      when(counterWrap) {
        dividedClock := ~dividedClock
      }
      outputClock := dividedClock.asClock
    }

    outputClock
  }
}

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
class TopWrapper(freq: Int, bits: Int) extends Module {
  val io = IO(new BlinkyIO(bits))
  val invertedReset = ~reset.asBool
  val dividedClock = withReset(reset = invertedReset) {
    ClockDivider(clock, freq)
  }
  val top = withClockAndReset(clock = dividedClock, reset = invertedReset) {
    Module(new Blinky(bits))
  }
  io <> top.io
}

object Main extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new TopWrapper(13_500_000, 6),
      firtoolOpts = Array(
        "-disable-all-randomization",
        "-strip-debug-info",
        "--lowering-options=disallowPackedArrays",
        "--lowering-options=disallowLocalVariables"
      )
    )
  )
}
