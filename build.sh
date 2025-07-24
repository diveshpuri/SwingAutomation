#!/bin/bash


echo "Building Java Swing Automation Framework..."

mkdir -p images
mkdir -p lib
mkdir -p build

echo "Created directories: images/, lib/, build/"

echo "Compiling SwingImageAutomation.java..."
javac -d build SwingImageAutomation.java

if [ $? -eq 0 ]; then
    echo "✓ SwingImageAutomation compiled successfully"
else
    echo "✗ Failed to compile SwingImageAutomation"
    exit 1
fi

echo ""
echo "Note: To compile SikuliExample.java, you need to:"
echo "1. Download Sikuli JAR from https://raiman.github.io/SikuliX1/"
echo "2. Place it in the lib/ directory"
echo "3. Run: javac -cp lib/sikulixapi-2.0.5.jar -d build SikuliExample.java"

echo ""
echo "Build completed! Next steps:"
echo "1. Add screenshot images to the images/ directory"
echo "2. Modify the automation scripts for your specific application"
echo "3. Run: java -cp build SwingImageAutomation"

echo ""
echo "Framework is ready for use!"
