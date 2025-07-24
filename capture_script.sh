#!/bin/bash


SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVA_CLASS="GlobalScreenshotCapture"
OUTPUT_DIR="captured_images"

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Screenshot Capture Tool for Swing Automation${NC}"
echo "=============================================="

if [ ! -f "$SCRIPT_DIR/build/$JAVA_CLASS.class" ]; then
    echo -e "${YELLOW}Compiling Java classes...${NC}"
    mkdir -p build
    javac -d build "$SCRIPT_DIR/$JAVA_CLASS.java"
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Failed to compile Java class${NC}"
        exit 1
    fi
    echo -e "${GREEN}Compilation successful${NC}"
fi

mkdir -p "$OUTPUT_DIR"

capture_screenshot() {
    local prefix="$1"
    echo -e "${BLUE}Capturing screenshot...${NC}"
    java -cp build "$JAVA_CLASS" capture
    echo -e "${GREEN}Screenshot saved to $OUTPUT_DIR/${NC}"
}

capture_delayed() {
    local delay="$1"
    echo -e "${YELLOW}Screenshot will be captured in $delay seconds...${NC}"
    echo -e "${YELLOW}Position your application window now!${NC}"
    java -cp build "$JAVA_CLASS" delayed
}

start_interactive() {
    echo -e "${GREEN}Starting interactive capture mode...${NC}"
    echo "Available commands:"
    echo "  c  - Capture screenshot immediately"
    echo "  d  - Capture screenshot with 3 second delay"
    echo "  v  - View captured images directory"
    echo "  h  - Show this help"
    echo "  q  - Quit"
    echo ""
    
    while true; do
        echo -n -e "${BLUE}Enter command (c/d/v/h/q): ${NC}"
        read -r command
        
        case "$command" in
            c|C)
                capture_screenshot "interactive"
                ;;
            d|D)
                capture_delayed 3
                ;;
            v|V)
                echo -e "${GREEN}Captured images in $OUTPUT_DIR:${NC}"
                ls -la "$OUTPUT_DIR"/*.png 2>/dev/null || echo "No images captured yet"
                ;;
            h|H)
                echo "Commands:"
                echo "  c - Capture screenshot immediately"
                echo "  d - Capture with 3 second delay"
                echo "  v - View captured images"
                echo "  q - Quit"
                ;;
            q|Q)
                echo -e "${GREEN}Goodbye!${NC}"
                break
                ;;
            *)
                echo -e "${RED}Unknown command: $command${NC}"
                ;;
        esac
        echo ""
    done
}

show_usage() {
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  capture     - Take screenshot immediately"
    echo "  delayed     - Take screenshot after 3 seconds"
    echo "  interactive - Start interactive mode"
    echo "  help        - Show this help"
    echo ""
    echo "Examples:"
    echo "  $0 capture          # Take immediate screenshot"
    echo "  $0 delayed          # Take screenshot after delay"
    echo "  $0 interactive      # Start interactive session"
    echo ""
    echo "Output: Screenshots saved to $OUTPUT_DIR/"
}

case "${1:-interactive}" in
    capture)
        capture_screenshot "manual"
        ;;
    delayed)
        capture_delayed 3
        ;;
    interactive)
        start_interactive
        ;;
    help|--help|-h)
        show_usage
        ;;
    *)
        echo -e "${RED}Unknown command: $1${NC}"
        echo ""
        show_usage
        exit 1
        ;;
esac

echo -e "${GREEN}Screenshot capture completed!${NC}"
echo -e "${BLUE}Images saved to: $(pwd)/$OUTPUT_DIR${NC}"
echo -e "${BLUE}Use these images as templates in your automation scripts${NC}"
