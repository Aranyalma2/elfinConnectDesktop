#!/bin/bash

destinationZip="../releases/latest.zip"
work_dir="Elfin Connect Desktop"
java_folder="$work_dir/java"
download_link="https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jre_x64_windows_hotspot_21.0.2_13.zip"

# Check if the work directory exists
if [ ! -d "$work_dir" ]; then
    mkdir -p "$work_dir"
    echo "Created components folder."
fi

# Check if the Java folder exists
if [ ! -d "$java_folder" ]; then
    echo "Java folder not found. Downloading Java..."

    # Create the desktop folder if it doesn't exist
    mkdir -p "$work_dir"

    # Download Java zip file
    wget -O "$work_dir/java.zip" "$download_link"

    # Unzip the Java file
    unzip -q "$work_dir/java.zip" -d "$work_dir"

    # Rename the extracted folder to "java"
    mv "$work_dir/jdk-21.0.2+13-jre" "$java_folder"

    # Clean up - remove the downloaded zip file
    rm "$work_dir/java.zip"

    echo "Java hooked successfully."
else
    echo "Java folder already exists. No action needed."
fi

# Check if files were copied
artifactsFiles=(../elfinConnectDesktop/*)
cp -r ../elfinConnectDesktop/* "$work_dir"

if [ ${#artifactsFiles[@]} -gt 0 ]; then
    echo "Artifacts files copied."
else
    echo "Artifacts files are missing."
fi

# Create gfx folder if it doesn't exist
gfxPath="$work_dir/gfx"
if [ ! -d "$gfxPath" ]; then
    mkdir -p "$gfxPath"
    echo "Created gfx folder."
fi
cp -r ../../../gfx/* "$gfxPath"
echo "GFX files copied."

cp "Elfin Connect Desktop.exe" "$work_dir"
echo "Launcher file copied."

# Zip the folder
zip -r "$destinationZip" "$work_dir"

# Check if the archive was created successfully
if [ -f "$destinationZip" ]; then
    echo "Folder has been zipped successfully."
else
    echo "Failed to zip the folder."
fi
