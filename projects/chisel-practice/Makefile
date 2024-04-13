YOSYS = yosys
NEXTPNR = nextpnr-gowin
APICULA = gowin_pack
SBT = sbt
VERIBLE_FMT = verible-verilog-format

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

pnrblinky.json: blinky.json
	$(NEXTPNR) --freq $(NEXTPNR_FREQ) --json blinky.json --write pnrblinky.json --device $(NEXTPNR_DEVICE) \
	--family $(NEXTPNR_FAMILY) --cst boards/tangnano9k.cst

blinky.fs: pnrblinky.json
	$(APICULA) -d $(NEXTPNR_FAMILY) -o blinky.fs pnrblinky.json

generated/blinky/SevenSegTop.sv: src/main/scala/blinky/SevenSeg.scala
	$(SBT) "runMain blinky.SevenSegVerilog"

sevenseg.json: generated/blinky/SevenSegTop.sv
	$(YOSYS) -p "read_verilog -sv generated/blinky/SevenSegTop.sv; synth_gowin -json sevenseg.json"

pnrsevenseg.json: sevenseg.json
	$(NEXTPNR) --freq $(NEXTPNR_FREQ) --json sevenseg.json --write pnrsevenseg.json --device $(NEXTPNR_DEVICE) \
	--family $(NEXTPNR_FAMILY) --cst boards/tangnano9k.cst

sevenseg.fs: pnrsevenseg.json
	$(APICULA) -d $(NEXTPNR_FAMILY) -o sevenseg.fs pnrsevenseg.json

generated/projects/SevenSegTop.sv: ./src/main/scala/projects/CountCombinations.scala
	$(SBT) "runMain projects.CountCombinationsVerilog"

countcomb.json: generated/projects/SevenSegTop.sv
	$(YOSYS) -p "read_verilog -sv generated/projects/SevenSegTop.sv; synth_gowin -json countcomb.json"

pnrcountcomb.json: countcomb.json
	$(NEXTPNR) --freq $(NEXTPNR_FREQ) --json countcomb.json --write pnrcountcomb.json --device $(NEXTPNR_DEVICE) \
	--family $(NEXTPNR_FAMILY) --cst boards/tangnano9k.cst

countcomb.fs: pnrcountcomb.json
	$(APICULA) -d $(NEXTPNR_FAMILY) -o countcomb.fs pnrcountcomb.json

clean:
	rm -rf target/ *.fs *.json generated/
