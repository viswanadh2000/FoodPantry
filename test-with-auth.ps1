# Test PantryPulse API with Authentication

$baseUri = "http://localhost:8080/api/v1"

Write-Host "Step 1: Login to get JWT token" -ForegroundColor Cyan

# Login first
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUri/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "✓ Login successful!" -ForegroundColor Green
    $token = $loginResponse.token
    Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ Login failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host "`nStep 2: Add a site with JWT token" -ForegroundColor Cyan

$headers = @{
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token"
}

$siteBody = @{
    name = "Downtown Pantry"
    address = "123 Main St"
    city = "Boston"
    state = "MA"
    zip = "02101"
    phone = "617-555-0100"
    email = "downtown@pantrypulse.org"
    capacity = 200
} | ConvertTo-Json

try {
    $siteResponse = Invoke-RestMethod -Uri "$baseUri/sites" -Method POST -Headers $headers -Body $siteBody
    Write-Host "✓ Site created successfully!" -ForegroundColor Green
    $siteResponse | ConvertTo-Json -Depth 5
} catch {
    Write-Host "✗ Failed to create site!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    Write-Host "Response: $errorBody" -ForegroundColor Yellow
}
