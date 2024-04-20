YOSYS = yosys
NEXTPNR = nextpnr-gowin
APICULA = gowin_pack
SBT = sbt
VERIBLE_FMT = verible-verilog-format
NETLISTSVG = netlistsvg

NEXTPNR_DEVICE = GW1NR-LV9QN88PC6/I5
NEXTPNR_FAMILY = GW1N-9C
NEXTPNR_FREQ = 27.00

.DEFAULT_GOAL := all
.PHONY := clean all

all: blinky.fs sevenseg.fs countcomb.fs

generated/blinky/Top.sv:
	$(SBT) "runMain blinky.BlinkyVerilog"

blinky.json: generated/blinky/Top.sv
	$(YOSYS) -p "read_verilog generated/blinky/Top.sv; synth_gowin -json blinky.json"

generated/blinky/SevenSegTop.sv: src/main/scala/blinky/SevenSeg.scala
	$(SBT) "runMain blinky.SevenSegVerilog"

sevenseg.json: generated/blinky/SevenSegTop.sv
	$(YOSYS) -p "read_verilog -sv generated/blinky/SevenSegTop.sv; synth_gowin -json sevenseg.json"

generated/projects/CountCombinationsTop.sv: ./src/main/scala/projects/CountCombinations.scala
	$(SBT) "runMain projects.CountCombinationsVerilog"

countcomb.json: generated/projects/CountCombinationsTop.sv
	$(YOSYS) -p "read_verilog -sv generated/projects/CountCombinationsTop.sv; synth_gowin -json countcomb.json"

generated/projects/ParallelCarryCounterTop.sv: ./src/main/scala/projects/ParallelCarryCounter.scala
	$(SBT) "runMain projects.ParallelCarryCounterVerilog"

parallelcarrycounter.json: generated/projects/ParallelCarryCounterTop.sv
	$(YOSYS) -p "read_verilog -sv generated/projects/ParallelCarryCounterTop.sv; synth_gowin -json $@" $^

pnr%.json: %.json
	$(NEXTPNR) --freq $(NEXTPNR_FREQ) --json $^ --write $@ --device $(NEXTPNR_DEVICE) \
	--family $(NEXTPNR_FAMILY) --cst boards/tangnano9k.cst

%.fs: pnr%.json
	$(APICULA) -d $(NEXTPNR_FAMILY) -o $@ $^

CountCombinationsTop.json: generated/projects/CountCombinationsTop.sv
	$(YOSYS) -p "prep -top CountCombinationsTop; simplemap CountCombinations; flatten CountCombinations; write_json $@" $^

CountCombinations.json: generated/projects/CountCombinationsTop.sv
	$(YOSYS) -p "prep -top CountCombinations; simplemap; flatten; write_json $@" $^

ParallelCarryCounterTop.json: generated/projects/ParallelCarryCounterTop.sv
	$(YOSYS) -p "prep -top ParallelCarryCounterTop; simplemap ParallelCarryCounter; flatten ParallelCarryCounter; write_json $@" $^

ParallelCarryCounter.json: generated/projects/ParallelCarryCounterTop.sv
	$(YOSYS) -p "prep -top ParallelCarryCounter; simplemap; flatten; write_json $@" $^

%.pdf: %.svg
	cairosvg $^ -o $@

%.svg: %.json
	$(NETLISTSVG) $^ -o $@

clean:
	rm -rf target/ *.fs *.json generated/
