#!/usr/bin/env bash
# Compile semua file .java di src/ ke out/, lalu jalankan Main.
# JavaFX (jar + .so) diambil langsung dari lib/, jar lain di lib/ masuk classpath.
set -e
cd "$(dirname "$0")"

JFX=lib
FX_OPTS=()
if [ -f "$JFX/javafx.controls.jar" ]; then
    FX_OPTS=(--module-path "$JFX" --add-modules javafx.controls)
fi

rm -rf out
javac "${FX_OPTS[@]}" -cp "lib/*" -d out $(find src -name "*.java")
java "${FX_OPTS[@]}" -cp "out:lib/*" Main
