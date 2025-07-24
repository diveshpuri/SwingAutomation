#!/bin/bash


echo "Building Java Swing Automation Framework..."

mkdir -p images
mkdir -p lib
mkdir -p build

echo "Created directories: images/, lib/, build/"

echo "Compiling with Maven..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✓ Project compiled successfully with Maven"
else
    echo "✗ Failed to compile with Maven"
    exit 1
fi

echo ""
echo "Note: Sikuli dependencies are now managed by Maven automatically"
echo "All dependencies will be downloaded from Maven repositories"

echo ""
echo "Build completed! Next steps:"
echo "1. Add screenshot images to the src/main/resources/images/ directory"
echo "2. Modify the automation scripts for your specific application"
echo "3. Run: mvn exec:java -Dexec.mainClass=SwingImageAutomation"
echo "   Or: java -cp target/classes SwingImageAutomation"

echo ""
echo "Framework is ready for use with Maven!"
