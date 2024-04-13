package projects.tests

import projects.CountCombinations
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CountCombinationsTester extends AnyFlatSpec with ChiselScalatestTester {
  "CountCombinations of 0b11 in 0b11111111" should "be equal to 7" in {
    test(new CountCombinations(8)) { dut =>
      dut.io.value.poke("b11111111".U)
      dut.io.count.expect(7.U)
    }
  }

  "CountCombinations of 0b11 in 0b00000000" should "be equal to 0" in {
    test(new CountCombinations(8)) { dut =>
      dut.io.value.poke("b00000000".U)
      dut.io.count.expect(0.U)
    }
  }
}

class CountCombinationsTestbench(
    numBits: Int,
    countMax: Int
) extends Module {
  val io = IO(new Bundle {
    val value = Output(UInt(util.log2Up(countMax).W))
    val count = Output(UInt(util.log2Up(numBits).W))
  })

  val dut = Module(new CountCombinations(numBits))
  val (value, _) = util.Counter(true.B, countMax)

  dut.io.value := value
  io.value <> value
  io.count := dut.io.count
}

class CountCombinationsTopTester
    extends AnyFlatSpec
    with ChiselScalatestTester {
  "CountCombinations" should "output 0, 0, 0, 1" in {
    test(new CountCombinationsTestbench(8, 4))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.io.value.expect(0.U)
        dut.io.count.expect(0.U)
        dut.clock.step(1)
        dut.io.value.expect(1.U)
        dut.io.count.expect(0.U)
        dut.clock.step(1)
        dut.io.value.expect(2.U)
        dut.io.count.expect(0.U)
        dut.clock.step(1)
        dut.io.value.expect(3.U)
        dut.io.count.expect(1.U)
        dut.clock.step(1)
      }
  }

  "CountCombinations" should "work for all 8-bit numbers" in {
    test(new CountCombinationsTestbench(8, 256))
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.clock.step(256)
      }
  }
}
