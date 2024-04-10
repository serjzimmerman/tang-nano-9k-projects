package util

import chisel3._
import chisel3.util.Counter

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
