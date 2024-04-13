package projects.tests

import projects.ParallelCarryCounter
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class ParallelCarryCounterTester
    extends AnyFlatSpec
    with ChiselScalatestTester {
  "ParallelCarryCounter" should "count up to 2" in {
    test(new ParallelCarryCounter(2)) { dut =>
      dut.io.count.expect(0.U)
      dut.io.overflow.expect(false.B)
      dut.clock.step(1)
      dut.io.count.expect(1.U)
      dut.io.overflow.expect(true.B)
      dut.clock.step(1)
      dut.io.count.expect(0.U)
      dut.io.overflow.expect(false.B)
    }
  }

  "ParallelCarryCounter" should "count up to 5" in {
    test(new ParallelCarryCounter(5)).withAnnotations(Seq(WriteVcdAnnotation)) {
      dut =>
        dut.io.count.expect(0.U)
        dut.io.overflow.expect(false.B)
        dut.clock.step(1)
        dut.io.count.expect(1.U)
        dut.io.overflow.expect(false.B)
        dut.clock.step(1)
        dut.io.count.expect(2.U)
        dut.io.overflow.expect(false.B)
        dut.clock.step(1)
        dut.io.count.expect(3.U)
        dut.io.overflow.expect(false.B)
        dut.clock.step(1)
        dut.io.count.expect(4.U)
        dut.io.overflow.expect(true.B)
        dut.clock.step(1)
        dut.io.count.expect(0.U)
        dut.io.overflow.expect(false.B)
    }
  }
}
