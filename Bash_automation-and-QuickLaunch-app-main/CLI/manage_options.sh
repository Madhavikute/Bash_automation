#!/bin/bash

# Function to open URLs in new tabs within the same Chrome window
open_in_new_tabs() {
    urls=("$@")
    if command -v google-chrome > /dev/null; then
        google-chrome "${urls[@]}" &  # Google Chrome on Linux
    elif command -v open > /dev/null; then
        open -na "Google Chrome" --args "${urls[@]}" &  # Google Chrome on macOS
    elif command -v xdg-open > /dev/null; then
        xdg-open "${urls[@]}" &  # Linux default browser
    else
        echo "Please open the URLs manually: ${urls[@]}"
    fi
}

# Function to open VS Code
open_vscode() {
    if command -v code > /dev/null; then
        code &  # Standard way to open VS Code
    else
        echo "VS Code command not found. Please check your installation."
    fi
}

# Function for DSA submenu
dsa_menu() {
    echo "Select an option:"
    echo "1. Leetcode Daily"
    echo "2. Striver SDE Sheet"
    echo "3. Back to main menu"
    read -p "Enter your choice: " dsa_choice

    case $dsa_choice in
        1)
            open_in_new_tabs "https://leetcode.com/problemset/" "https://www.youtube.com/channel/UCsBqgSlYEOxzF6SKtiot9NA"
            ;;
        2)
            open_in_new_tabs "https://takeuforward.org/interviews/strivers-sde-sheet-top-coding-interview-problems" "https://www.youtube.com/channel/UCsBqgSlYEOxzF6SKtiot9NA"
            ;;
        3)
            main_menu
            ;;
        *)
            echo "Invalid option. Please try again."
            dsa_menu
            ;;
    esac
}

# Main menu function
main_menu() {
    echo "Choose your activity:"
    echo "1. DSA"
    echo "2. Development"
    echo "3. Placement"
    echo "4. AI/ML"
    echo "5. Linux Customization"
    echo "6. Exit"
    read -p "Enter your choice: " choice

    case $choice in
        1)
            dsa_menu
            ;;
        2)
            open_vscode  # Open VS Code
            open_in_new_tabs "https://www.youtube.com" "https://github.com/ShreyashCC"
            ;;
        3)
            open_in_new_tabs "https://www.linkedin.com/feed/" "https://app.pod.ai/"
            ;;
        4)
            open_in_new_tabs "https://drive.google.com/drive/folders/1f7zLp-VkUbswDvY9FkemMLMJzloFDxJY" "https://colab.research.google.com/?utm_source=scs-index"
            ;;
        5)
            open_in_new_tabs "https://www.reddit.com/r/unixporn/" "https://www.gnome-look.org/browse/"
            ;;
        6)
            echo "Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid option. Please try again."
            main_menu
            ;;
    esac
}

# Start the script with the main menu
main_menu
