# Simple login test

Write-Host "Testing ADMIN login..." -ForegroundColor Cyan

$body = @{
    username = "admin"
    password = "admin123"
    role = "ADMIN"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $body -ContentType "application/json"

Write-Host "SUCCESS!" -ForegroundColor Green
Write-Host "Username: $($response.username)"
Write-Host "Role: $($response.role)"
Write-Host "Token: $($response.token.Substring(0,50))..."
