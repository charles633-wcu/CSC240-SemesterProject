#environment variable local
$env:CLASS_API_URL = "http://localhost:9080"

# start-all.ps1
Write-Host "Starting all CSC240 project servers..." -ForegroundColor Cyan

# Start DataAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd DataAPI; mvn exec:java '-Dexec.mainClass=dataapi.DataAPIServer'"

Start-Sleep -Seconds 3  
# Start ClassAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd ClassAPI; mvn exec:java '-Dexec.mainClass=classapi.ClassAPIServer'"

Start-Sleep -Seconds 3

# Start UI API
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd UIApi; mvn exec:java '-Dexec.mainClass=uiapi.UIAPIServer'"

Write-Host "   All servers launched! Open the following:" -ForegroundColor Green
Write-Host "   DataAPI → http://localhost:9080/data/incidents"
Write-Host "   ClassAPI → http://localhost:9080/combined/{date}"
Write-Host "   UI API → http://localhost:9080/ui/summary/{date}"
