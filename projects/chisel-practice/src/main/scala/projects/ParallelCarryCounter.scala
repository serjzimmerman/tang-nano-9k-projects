package projects

import util.ClockDivider
import blinky.SevenSegHexDisplay
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

class ParallelCarryCounterTopIO(numDigits: Int, numLeds: Int, numBits: Int)
    extends Bundle {
  val digit = Output(UInt(numDigits.W))
  val led = Output(UInt(numLeds.W))
  val dac = Output(UInt(numBits.W))
  val segments = Output(UInt(8.W))
  val switch = Input(Bool())
}

class ParallelCarryCounterTop(
    numDigits: Int,
    numLeds: Int,
    digitDivideBy: Int,
    countMax: Int,
    countFreq1: Int,
    countFreq2: Int
) extends Module {
  val numBits = log2Up(countMax)
  val io = IO(new ParallelCarryCounterTopIO(numDigits, numLeds, numBits))
  val invertedReset = ~reset.asBool
  val dividedClock1 = withReset(reset = false.B) {
    ClockDivider(clock, countFreq1)
  }
  val dividedClock2 = withReset(reset = false.B) {
    ClockDivider(clock, countFreq2)
  }
  val selectedClock = Mux(io.switch, dividedClock1, dividedClock2)
  val top = withClockAndReset(clock = selectedClock, reset = invertedReset) {
    Module(new ParallelCarryCounter(countMax))
  }
  val sevenseg = withReset(reset = invertedReset) {
    Module(new SevenSegHexDisplay(numDigits, digitDivideBy))
  }
  io.digit <> sevenseg.io.digitSelect
  io.segments <> sevenseg.io.segments
  sevenseg.io.value := Mux(io.switch, top.io.count, 0.U)
  io.led := Mux(io.switch, top.io.count, 0.U)
  io.dac := top.io.count
}

object ParallelCarryCounterVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new ParallelCarryCounterTop(/* numDigits */ 4, /* numLeds*/ 8,
      /* digitDivideBy */ 135_000,
      /* countMax */ 13, /* countFreq1 */ 27_000_000, /* countFreq2 */ 5400),
    args = Array("--target-dir", "generated/projects"),
    firtoolOpts = Array(
      "--disable-all-randomization",
      "--strip-debug-info",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays"
    )
  )
}
