alias f := format
alias l := lint
alias r := run-workflows

run-workflows:
    @act -P ubuntu-22.04=ghcr.io/catthehacker/ubuntu:runner-22.04

commitlint:
    @nix run nixpkgs#commitlint -- --from HEAD~ --to HEAD --verbose

check: commitlint
    @nix flake check

check-format:
    @nix flake check

format:
    @nix fmt

lint: check format

update-inputs:
    #!/usr/bin/env bash
    for i in $(fd --full-path flake.nix); do
      echo $(cd $(dirname $i) && nix flake update)
    done

build-all:
    nix build .#blinky -L --show-trace
    nix build .#chisel-blinky -L --show-trace
    nix build .#chisel-practice -L --show-trace

addlicense:
    @find . -type f \( -name "*.cpp" -o -name "*.hpp" -o -name "*.cc" -o -name "*.h" \) -exec addlicense -f LICENSE -l mit {} \;
