#!/usr/bin/env bash
# Compile semua file .java di src/ ke out/, lalu jalankan Main (Linux/macOS).
# JavaFX SDK sesuai OS diekstrak ke javafx/ (tidak masuk git, lihat README).
# Jar lain (driver database, dll.) ada di lib/ dan masuk classpath.
set -e
cd "$(dirname "$0")"

JFX=javafx/lib
if [ ! -f "$JFX/javafx.controls.jar" ]; then
    echo "JavaFX SDK belum ada di $JFX. Lihat bagian Setup di README.md." >&2
    exit 1
fi
FX_OPTS=(--module-path "$JFX" --add-modules javafx.controls --enable-native-access=javafx.graphics)

rm -rf out
javac --module-path "$JFX" --add-modules javafx.controls -cp "lib/*" -d out $(find src -name "*.java")
mkdir -p out/view && cp src/view/*.css out/view/
java "${FX_OPTS[@]}" -cp "out:lib/*" Main
