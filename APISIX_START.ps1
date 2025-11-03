# CSC240 Semester Project - APISIX Gateway Launcher

Write-Host "Checking Docker status..." -ForegroundColor Yellow
$dockerRunning = $true
try {
    docker info | Out-Null
    Write-Host "Docker is running. Starting APISIX and etcd containers..." -ForegroundColor Green
    docker compose up -d
    Start-Sleep -Seconds 5
}
catch {
    Write-Host "Docker is not running or not installed. Skipping APISIX setup and continuing with Java APIs." -ForegroundColor Red
    $dockerRunning = $false
}

# Environment variables for APIs
$env:CLASS_API_URL = "http://localhost:9080"
$env:DATA_API_URL = "http://localhost:9080/data"
$env:PORT = "9080"

Write-Host "Starting CSC240 Project with Apache APISIX Gateway..." -ForegroundColor Cyan

# Configure APISIX routes (only if Docker/APISIX is running)
if ($dockerRunning) {
    Write-Host "Configuring APISIX routes..." -ForegroundColor Yellow
    $adminKey = "edd1c9f034335f136f87ad84b625c8f1"

    $routes = @(
        @{ id = 1; uri = "/data/*"; port = 8081 },
        @{ id = 2; uri = "/summary/*"; port = 8082 },
        @{ id = 3; uri = "/ui/*"; port = 8083 }
    )

    foreach ($r in $routes) {
        $body = @{
            uri = $r.uri
            upstream = @{
                type = "roundrobin"
                nodes = @{ "host.docker.internal:$($r.port)" = 1 }
            }
        } | ConvertTo-Json -Depth 5

        try {
            Invoke-RestMethod -Uri "http://localhost:9180/apisix/admin/routes/$($r.id)" `
                -Method PUT `
                -Headers @{ "X-API-KEY" = $adminKey } `
                -Body $body `
                -ContentType "application/json"
            Write-Host "Route registered: $($r.uri) → port $($r.port)" -ForegroundColor Green
        }
        catch {
            Write-Host "Failed to register route $($r.uri): $_" -ForegroundColor Red
        }
    }

    Write-Host ""
    Write-Host "    APISIX Gateway Active at: http://localhost:9080" -ForegroundColor Green
    Write-Host "   /data/*      from DataAPI (port 8081)"
    Write-Host "   /summary/*   from ClassAPI (port 8082)"
    Write-Host "   /ui/*        from UIAPI (port 8083)"
    Write-Host ""
}

Write-Host "Starting Java API servers..." -ForegroundColor Yellow
$projectRoot = $PSScriptRoot

# DataAPI
Write-Host "Starting DataAPI from: $projectRoot\DataAPI" -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$projectRoot\DataAPI'; mvn clean compile exec:java '-Dexec.mainClass=dataapi.Main'"
Start-Sleep -Seconds 3

# ClassAPI
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$projectRoot\ClassAPI'; mvn clean compile exec:java '-Dexec.mainClass=classapi.Main'"
Start-Sleep -Seconds 3

# UIApi
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$projectRoot\UIApi'; mvn clean compile exec:java '-Dexec.mainClass=uiapi.UIAPIServer'"

Write-Host ""
Write-Host " Launched Project Servers" -ForegroundColor Green
Write-Host ""
Write-Host "Endpoints Listed:" -ForegroundColor Yellow
Write-Host ""
Write-Host '     DataAPI (via APISIX):'
Write-Host '     http://localhost:9080/data/incidents'
Write-Host '     http://localhost:9080/data/perpetrators'
Write-Host '     http://localhost:9080/data/victims'
Write-Host '     http://localhost:9080/data/temperatures'
Write-Host ""
Write-Host '     ClassAPI (via APISIX):'
Write-Host '     http://localhost:9080/summary/{date}'
Write-Host ""
Write-Host '     UIAPI (via APISIX):'
Write-Host '     http://localhost:9080/ui/view?date={date}'
Write-Host ""
