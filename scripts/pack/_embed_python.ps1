# Embeddable Python 3.12 x64 + pip install backend deps into build\python-embed
# Cache: scripts\cache\embed. Offline: place python-3.12.8-embed-amd64.zip and get-pip.py there.
# Set PACK_SKIP_PYTHON_EMBED=1 to skip.
param(
    [Parameter(Mandatory = $true)]
    [string] $BuildRoot
)
$ErrorActionPreference = "Stop"

if ($env:PACK_SKIP_PYTHON_EMBED -eq "1") {
    Write-Host "[embed] PACK_SKIP_PYTHON_EMBED=1 — skip embedded Python."
    exit 0
}

$ver = "3.12.8"
$arch = "amd64"
$zipName = "python-$ver-embed-$arch.zip"
$pyUrl = "https://www.python.org/ftp/python/$ver/$zipName"
$getPipUrl = "https://bootstrap.pypa.io/get-pip.py"

# PSScriptRoot = <repo>\scripts\pack
$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$cacheDir = Join-Path $repoRoot "scripts\cache\embed"
if (-not (Test-Path $cacheDir)) {
    New-Item -ItemType Directory -Path $cacheDir -Force | Out-Null
}

$req = Join-Path $BuildRoot "backend\requirements.txt"
if (-not (Test-Path $req)) {
    throw "Missing requirements: $req"
}

$dest = Join-Path $BuildRoot "python-embed"
if (Test-Path $dest) {
    Remove-Item -Recurse -Force $dest
}
New-Item -ItemType Directory -Path $dest -Force | Out-Null

$zipPath = Join-Path $cacheDir $zipName
if (-not (Test-Path $zipPath)) {
    Write-Host "[embed] Download: $pyUrl"
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $pyUrl -OutFile $zipPath -UseBasicParsing
}

$getPip = Join-Path $cacheDir "get-pip.py"
if (-not (Test-Path $getPip)) {
    Write-Host "[embed] Download: get-pip.py"
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $getPipUrl -OutFile $getPip -UseBasicParsing
}

Write-Host "[embed] Extract -> $dest"
Expand-Archive -Path $zipPath -DestinationPath $dest -Force

$pth = Get-ChildItem -Path $dest -Filter "*._pth" -File | Select-Object -First 1
if ($null -eq $pth) {
    throw "No *._pth in embeddable tree"
}
# Enable site and Lib\site-packages (Python embed docs)
$pthContent = "python312.zip`r`n.`r`nLib\site-packages`r`nimport site`r`n"
[System.IO.File]::WriteAllText($pth.FullName, $pthContent, (New-Object System.Text.UTF8Encoding $false))

$sp = Join-Path $dest "Lib\site-packages"
if (-not (Test-Path $sp)) {
    New-Item -ItemType Directory -Path $sp -Force | Out-Null
}

$py = Join-Path $dest "python.exe"
Write-Host "[embed] get-pip..."
Push-Location $dest
try {
    & $py $getPip @("--no-warn-script-location")
    if ($LASTEXITCODE) {
        if ($LASTEXITCODE -ne 0) { throw "get-pip failed exit=$LASTEXITCODE" }
    }
} finally {
    Pop-Location
}

Write-Host "[embed] pip install (may take a few minutes)..."
& $py -m pip install -r $req --disable-pip-version-check
if ($LASTEXITCODE) {
    if ($LASTEXITCODE -ne 0) { throw "pip install failed exit=$LASTEXITCODE" }
}

Write-Host "[embed] OK: $py"
exit 0
