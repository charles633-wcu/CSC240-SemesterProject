Write-Host "🚀 Starting all CSC240 project servers..." -ForegroundColor Cyan

# Start DataAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd DataAPI; mvn exec:java -Dexec.mainClass=dataapi.Main"

Start-Sleep -Seconds 3
# Start ClassAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd ClassAPI; mvn exec:java -Dexec.mainClass=classapi.Main"

Start-Sleep -Seconds 3
# Start UIAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd UIAPI; mvn exec:java -Dexec.mainClass=uiapi.UIAPIServer"

Write-Host ""
Write-Host "✅ All servers launched!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Endpoints:" -ForegroundColor Yellow
Write-Host "   DataAPI:    http://localhost:8081/data/incidents" -ForegroundColor White
Write-Host "               http://localhost:8081/data/victims" -ForegroundColor White
Write-Host "               http://localhost:8081/data/perpetrators" -ForegroundColor White
Write-Host "               http://localhost:8081/data/temperature" -ForegroundColor White
Write-Host ""
Write-Host "   ClassAPI:   http://localhost:8082/summary/{date}" -ForegroundColor White
Write-Host "               Example: http://localhost:8082/summary/2024-10-21" -ForegroundColor White
Write-Host ""
Write-Host "   UIAPI:      http://localhost:8083/ui/view?date=2024-10-21" -ForegroundColor White
Write-Host ""
Write-Host "📊 Tip: Replace the date above with any date available in your database." -ForegroundColor Cyan
