@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ===================================================
echo  ⚡ FastJava Native Code-Signing Tool
echo  Sign native DLLs for Windows SmartScreen ^& AppLocker
echo ===================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$cert = Get-ChildItem Cert:\CurrentUser\My -CodeSigningCert | Where-Object { $_.Subject -match 'FastJava' } | Select-Object -First 1;" ^
    "if (-not $cert) {" ^
    "    Write-Host 'Creating FastJava Code Signing Certificate in CurrentUser\My...' -ForegroundColor Yellow;" ^
    "    $cert = New-SelfSignedCertificate -Type CodeSigningCert -Subject 'CN=FastJava OpenSource, O=FastJava Community' -CertStoreLocation Cert:\CurrentUser\My -NotAfter (Get-Date).AddYears(5);" ^
    "}" ^
    "$dlls = Get-ChildItem -Path . -Recurse -Filter *.dll | Where-Object { $_.FullName -notmatch '\\(target|\.git)\\' };" ^
    "if ($dlls.Count -eq 0) { Write-Host 'No native DLLs found to sign.' -ForegroundColor Gray; exit 0; }" ^
    "foreach ($dll in $dlls) {" ^
    "    Write-Host ('Signing ' + $dll.Name + ' ... ') -NoNewline;" ^
    "    $res = Set-AuthenticodeSignature -FilePath $dll.FullName -Certificate $cert -HashAlgorithm SHA256;" ^
    "    Write-Host ('[' + $res.Status + ']') -ForegroundColor Green;" ^
    "}"

echo.
echo ✅ Native DLL signing finished.
pause