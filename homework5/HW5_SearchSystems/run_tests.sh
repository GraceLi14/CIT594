#!/bin/bash
set -e 

echo "Cleaning and compiling..."
rm -rf out
mkdir out

# Compile all source and test files
echo "Compiling source and test files..."
javac -cp "lib/*" src/*.java test/*.java -d out

# If no argument is provided, run ALL tests dynamically
if [ -z "$1" ]; then
    echo "Running all baseline tests..."

    # Grab all filenames in test/, strip the "test/" prefix, and strip ".java"
    # This turns "test/SearchServiceTest.java" into "SearchServiceTest"
    ALL_TESTS=$(ls test/*.java | sed 's|test/||' | sed 's|\.java||')

    java -Xms4g -Xmx4g -XX:+UseG1GC -XX:ActiveProcessorCount=4 -cp "out:lib/*" org.junit.runner.JUnitCore $ALL_TESTS

# If you pass an argument (e.g., ./run_tests.sh SpellCheckerTest), run just that one
else
    echo "🎯 Running specific test: $1..."
    java -Xms4g -Xmx4g -XX:+UseG1GC -XX:ActiveProcessorCount=4 -cp "out:lib/*" org.junit.runner.JUnitCore "$1"
fi