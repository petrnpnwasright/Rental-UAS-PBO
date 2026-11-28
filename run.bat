@echo off
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\rental\exception\*.java src\rental\payment\*.java src\rental\model\*.java src\rental\service\*.java src\rental\ui\*.java src\rental\Main.java
if %errorlevel% neq 0 (
    echo Terjadi kesalahan kompilasi!
    pause
    exit /b %errorlevel%
)
java -cp bin rental.Main
pause
