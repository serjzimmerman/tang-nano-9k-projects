# tang-nano-9k-projects

This is a personal playground for learning Verilog and FPGAs. All of the recepies are packages
as a nix flake, which makes it possible to open a dev shell and compile the bitstream with a single command.

## Quick start

Install nix via official installation [guide](https://nixos.org/download/). Skip this
if you already have nix installed one way or another.

Please make sure that you have nix flakes enabled.
Enter the shell:

```bash
nix develop .
```

Build an example project and flash it to the board:

```bash
nix build .\#blinky
sudo openFPGALoader --detect
sudo openFPGALoader result/blinky.fs -b tangnano9k
```

If you do not want to use nix, then here's an approximate list of required tools:

- [apicula](https://github.com/YosysHQ/apicula) - Bitstreams for Gowin FPGAs.
- [yosys](https://github.com/YosysHQ/yosys) - RTL synthesis.
- [nextpnr](https://github.com/YosysHQ/nextpnr) - Place & Route tool with support for Gowin LittleBee chips.
- [openfpgaloader](https://github.com/trabucayre/openFPGALoader) - For flashing bitstreams to the board.

## List of projects

- [blinky](./projects/blinky) - Sample project borrowed from Apicula [examples](https://github.com/YosysHQ/apicula/tree/master/examples).
