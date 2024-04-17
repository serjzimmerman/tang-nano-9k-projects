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

  val areOnes =
    VecInit
      .tabulate(numBits - 1) { i =>
        val currentBits = io.value(i + 1, i)
        currentBits === three
      };

  val utilVec = VecInit(0.U(numBits.W).asBools)
  for (i <- 0 until numBits - 1) {
    utilVec(i) := (if (i == 0) { areOnes(i) }
                   else {
                     !utilVec(i - 1) && areOnes(i)
                   })
  }

  io.count :=
    PopCount(utilVec.asUInt)
}

class CountCombinationsTopIO(numDigits: Int, numLeds: Int) extends Bundle {
  val digit = Output(UInt(numDigits.W))
  val led = Output(UInt(numLeds.W))
  val segments = Output(UInt(8.W))
  val switchNumber = Input(UInt(numLeds.W))
  val switch = Input(Bool())
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
  val (valueTmp, _) =
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
  io.led := Mux(io.switch, ~io.switchNumber, valueTmp)
  top.io.value := Mux(io.switch, ~io.switchNumber, valueTmp)
}

object CountCombinationsVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new CountCombinationsTop(8 /* numBits */, 4 /* numDigits */,
      8 /* numLeds */, 135_000, 50_000_000, 256),
    args = Array("--target-dir", "generated/projects"),
    firtoolOpts = Array(
      "--disable-all-randomization",
      "--strip-debug-info",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays"
    )
  )
}
