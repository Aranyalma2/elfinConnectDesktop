$destinationZip = "..\releases\latest.zip"
$work_dir = "Elfin Connect Desktop"
$java_folder = Join-Path $work_dir "java"
$download_link = "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.2%2B13/OpenJDK21U-jre_x64_windows_hotspot_21.0.2_13.zip"

if (-not (Test-Path -Path $work_dir)) {
    New-Item -ItemType Directory -Path $work_dir | Out-Null
    Write-Host "Created components folder."
}

if (-not (Test-Path -Path "..\releases")) {
    New-Item -ItemType Directory -Path "..\releases" | Out-Null
    Write-Host "Created output folder."
}


# Check if the java folder exists
if (-not (Test-Path $java_folder)) {
    Write-Host "Java folder not found. Downloading Java..."

    # Create the desktop folder if it doesn't exist
    New-Item -ItemType Directory -Path $work_dir -ErrorAction SilentlyContinue

    # Download Java zip file
    Invoke-WebRequest -Uri $download_link -OutFile (Join-Path -Path $work_dir -ChildPath java.zip)

    # Unzip the Java file
    Expand-Archive -Path (Join-Path -Path $work_dir -ChildPath java.zip) -DestinationPath $work_dir

    # Rename the extracted folder to "java"
    Rename-Item -Path (Join-Path $work_dir "jdk-21.0.2+13-jre") -NewName "java"

    # Clean up - remove the downloaded zip file
    Remove-Item -Path (Join-Path -Path $work_dir -ChildPath java.zip)

    Write-Host "Java hooked successfully."
} else {
    Write-Host "Java folder already exists. No action needed."
}

# Check if files were copied
$artifactsFiles = Get-ChildItem -Path ../elfinConnectDesktop
Copy-Item -Path ../elfinConnectDesktop/* -Destination $work_dir

if ($artifactsFiles.Count -gt 0){
	Write-Host "Artifacts files copied."
} else {
    Write-Host "Artifacts files are missing."
}

# Create gfx folder if it doesn't exist
$gfxPath = Join-Path -Path $work_dir -ChildPath gfx
if (-not (Test-Path -Path $gfxPath)) {
    New-Item -ItemType Directory -Path $gfxPath | Out-Null
    Write-Host "Created gfx folder."
}
Copy-Item -Path ../../../gfx/* -Destination $gfxPath -Recurse
Write-Host "GFX files copied."

Copy-Item -Path "Elfin Connect Desktop.exe" -Destination $work_dir
Write-Host "Launcher file copied."

# Zip the folder
Compress-Archive -Path $work_dir -DestinationPath $destinationZip -Force

# Check if the archive was created successfully
if (Test-Path $destinationZip) {
    Write-Host "Folder has been zipped successfully."
} else {
    Write-Host "Failed to zip the folder."
}

Remove-Item -Path $work_dir -Force -Recurse

