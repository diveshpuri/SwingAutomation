#!/bin/bash


echo "Testing Screenshot Capture Tools..."
echo "=================================="

echo "1. Testing immediate screenshot capture..."
java -cp build GlobalScreenshotCapture capture

if [ -f captured_images/*.png ]; then
    echo "✓ Screenshot captured successfully"
    ls -la captured_images/
else
    echo "✗ No screenshot found"
fi

echo ""
echo "2. Testing delayed capture (3 seconds)..."
echo "   This will capture a screenshot in 3 seconds..."
java -cp build GlobalScreenshotCapture delayed &
sleep 4

echo ""
echo "Captured images:"
ls -la captured_images/ 2>/dev/null || echo "No images directory found"

echo ""
echo "Screenshot capture test completed!"
echo "Use './capture_script.sh interactive' for manual testing"
