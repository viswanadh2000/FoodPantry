# Test adding a site to PantryPulse API

$uri = "http://localhost:8080/api/v1/sites"
$headers = @{
    "Content-Type" = "application/json"
}
$body = @{
    name = "Test Site"
    address = "123 Main St"
    city = "Boston"
    state = "MA"
    zip = "02101"
    phone = "555-1234"
    email = "test@example.com"
    capacity = 100
} | ConvertTo-Json

Write-Host "Testing POST $uri" -ForegroundColor Cyan
Write-Host "Body: $body" -ForegroundColor Gray

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Headers $headers -Body $body
    Write-Host "`nSuccess!" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "`nError!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Message: $($_.Exception.Message)"
    
    # Try to read error response body
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $responseBody = $reader.ReadToEnd()
    Write-Host "Response: $responseBody"
}
