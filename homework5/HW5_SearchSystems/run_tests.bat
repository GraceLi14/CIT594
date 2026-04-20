@echo off
setlocal enabledelayedexpansion

echo Cleaning and compiling...
if exist out rmdir /s /q out
mkdir out

:: Compile all source and test files
echo Compiling source and test files...
javac -cp "lib\*" src\*.java test\*.java -d out

:: If no argument is provided, run ALL tests dynamically
if "%~1"=="" (
    echo Running all baseline tests...
    
    :: Dynamically find all .java files in the test directory and extract just the filename
    set "ALL_TESTS="
    for %%f in (test\*.java) do (
        set "ALL_TESTS=!ALL_TESTS! %%~nf"
    )
    
    java -Xms4g -Xmx4g -XX:+UseG1GC -XX:ActiveProcessorCount=4 -cp "out;lib\*" org.junit.runner.JUnitCore !ALL_TESTS!

:: If you pass an argument (e.g., .\run_tests.bat SpellCheckerTest), run just that one
) else (
    echo Running specific test: %1...
    java -Xms4g -Xmx4g -XX:+UseG1GC -XX:ActiveProcessorCount=4 -cp "out;lib\*" org.junit.runner.JUnitCore %1
)