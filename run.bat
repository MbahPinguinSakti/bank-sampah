@echo off
rem Compile semua file .java di src\ ke out\, lalu jalankan Main (Windows).
rem JavaFX SDK sesuai OS diekstrak ke javafx\ (tidak masuk git, lihat README).
rem Jar lain (driver database, dll.) ada di lib\ dan masuk classpath.
setlocal
cd /d "%~dp0"

set JFX=javafx\lib
if not exist "%JFX%\javafx.controls.jar" (
    echo JavaFX SDK belum ada di %JFX%. Lihat bagian Setup di README.md.
    exit /b 1
)

if exist out rmdir /s /q out
dir /s /b src\*.java > sources.txt
javac --module-path "%JFX%" --add-modules javafx.controls -cp "lib\*" -d out @sources.txt || (del sources.txt & exit /b 1)
del sources.txt
mkdir out\view
copy /y src\view\*.css out\view\ >nul
java --module-path "%JFX%" --add-modules javafx.controls --enable-native-access=javafx.graphics -cp "out;lib\*" Main
