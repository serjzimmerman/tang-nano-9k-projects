package blinky

import util.ClockDivider
import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class SevenSegHexDisplay(numDigits: Int, digitDivideBy: Int) extends Module {
  val displayableBitsNum: Int = numDigits * 4
  val numSegments: Int = 8

  val io = IO(new Bundle {
    val value = Input(UInt(displayableBitsNum.W))
    val segments = Output(UInt(numSegments.W))
    val digitSelect = Output(UInt(numDigits.W))
  })

  val currentDigit = RegInit(1.U(numDigits.W))

  val lookupTable =
    VecInit(
      0x3f.U,
      0x06.U,
      0x5b.U,
      0x4f.U,
      0x66.U,
      0x6d.U,
      0x7d.U,
      0x07.U,
      0x7f.U,
      0x67.U,
      0x77.U,
      0x7c.U,
      0x39.U,
      0x5e.U,
      0x79.U,
      0x71.U
    )

  val maxCounter: Int = digitDivideBy;
  val (_, counterWrap) = Counter(true.B, digitDivideBy / 2)

  when(counterWrap) {
    currentDigit := (currentDigit + 1.U) % numDigits.asUInt
  }

  val currentBitInValue = 4.U * currentDigit

  io.digitSelect := 1.U << currentDigit
  io.segments := lookupTable(
    ((io.value >> currentBitInValue))(3, 0)
  )
}

class SevenSegTopIO(numDigits: Int, numLeds: Int, digitDivideBy: Int)
    extends Bundle {
  val digit = Output(UInt(numDigits.W))
  val segments = Output(UInt((numDigits * 4).W))
}

// https://stackoverflow.com/questions/76521229/how-to-deal-with-activelow-reset-change-implicit-clock-frequency
class SevenSegTop(
    numDigits: Int,
    numLeds: Int,
    digitDivideBy: Int,
    countFreq: Int,
    countMax: Int
) extends Module {
  val io = IO(new SevenSegTopIO(numDigits, numLeds, digitDivideBy))
  val invertedReset = ~reset.asBool
  val dividedClock = withReset(reset = false.B) {
    ClockDivider(clock, countFreq)
  }
  val (value, _) =
    withClockAndReset(clock = dividedClock, reset = invertedReset) {
      Counter(true.B, countMax)
    }
  val top = withReset(reset = invertedReset) {
    Module(new SevenSegHexDisplay(numDigits, digitDivideBy))
  }
  io.digit <> top.io.digitSelect
  io.segments <> top.io.segments
  top.io.value := value;
}

object SevenSegVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new SevenSegTop(4, 6, 270_000, 2_700_000, 0xffff),
    args = Array("--target-dir", "generated/blinky"),
    firtoolOpts = Array(
      "--disable-all-randomization",
      "--strip-debug-info",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays"
    )
  )
}
