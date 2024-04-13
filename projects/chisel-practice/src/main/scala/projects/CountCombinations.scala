package projects

import blinky.SevenSegHexDisplay
import util.ClockDivider
import chisel3.util.log2Up
import chisel3.util.Counter
import chisel3.util.PopCount
import chisel3._
import circt.stage.ChiselStage

class CountCombinations(numBits: Int) extends Module {
  val io = IO(new Bundle {
    val value = Input(UInt(numBits.W))
    val count = Output(UInt(log2Up(numBits).W))
  })

  private val three = "b11".U
  io.count :=
    PopCount(
      VecInit
        .tabulate(numBits - 1) { i => (io.value(i + 1, i) === three) }
        .asUInt
    )
}

class CountCombinationsTopIO(numDigits: Int, numLeds: Int) extends Bundle {
  val digit = Output(UInt(numDigits.W))
  val led = Output(UInt(numLeds.W))
  val segments = Output(UInt(8.W))
}

class CountCombinationsTop(
    numBits: Int,
    numDigits: Int,
    numLeds: Int,
    digitDivideBy: Int,
    countFreq: Int,
    countMax: Int
) extends Module {
  val io = IO(new CountCombinationsTopIO(numDigits, numLeds))
  val invertedReset = ~reset.asBool
  val dividedClock = withReset(reset = false.B) {
    ClockDivider(clock, countFreq)
  }
  val (value, _) =
    withClockAndReset(clock = dividedClock, reset = invertedReset) {
      Counter(true.B, countMax)
    }
  val top = withReset(reset = invertedReset) {
    Module(new CountCombinations(numBits))
  }
  val sevenseg = withReset(reset = invertedReset) {
    Module(new SevenSegHexDisplay(numDigits, digitDivideBy))
  }
  io.digit <> sevenseg.io.digitSelect
  io.segments <> sevenseg.io.segments
  sevenseg.io.value := top.io.count
  top.io.value := value
  io.led := value
}

object CountCombinationsVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new CountCombinationsTop(8 /* numBits */, 4 /* numDigits */,
      8 /* numLeds */, 135_000, 5_000_000, 256),
    args = Array("--target-dir", "generated/projects"),
    firtoolOpts = Array(
      "--disable-all-randomization",
      "--strip-debug-info",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays"
    )
  )
}
