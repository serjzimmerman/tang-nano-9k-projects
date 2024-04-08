YOSYS = yosys
NEXTPNR = nextpnr-gowin
APICULA = gowin_pack

NEXTPNR_DEVICE = GW1NR-LV9QN88PC6/I5
NEXTPNR_FAMILY = GW1N-9C

LEDS_NR = 6

.DEFAULT_GOAL := blinky.fs

blinky.json:
	$(YOSYS) -D LEDS_NR=$(LEDS_NR) -p "read_verilog src/blinky.v; synth_gowin -json blinky.json"

pnrblinky.json: blinky.json
	$(NEXTPNR) --json blinky.json --write pnrblinky.json --device $(NEXTPNR_DEVICE) \
	--family $(NEXTPNR_FAMILY) --cst boards/tangnano9k.cst

blinky.fs: pnrblinky.json
	$(APICULA) -d $(NEXTPNR_FAMILY) -o blinky.fs pnrblinky.json
