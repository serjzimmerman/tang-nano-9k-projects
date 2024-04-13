package projects

import util.ClockDivider
import blinky.SevenSegTop
import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage


object CountCombinationsVerilog extends App {
  ChiselStage.emitSystemVerilogFile(
    new SevenSegTop(4, 6, 270_000, 2_700_000, 0xffff),
    args = Array("--target-dir", "generated/projects"),
    firtoolOpts = Array(
      "--disable-all-randomization",
      "--strip-debug-info",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays"
    )
  )
}
