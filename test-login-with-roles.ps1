# Test login with different roles

Write-Host "`n=== Testing Login with ADMIN role ===" -ForegroundColor Cyan
$adminLogin = @{
    username = "admin"
    password = "admin123"
    role = "ADMIN"
} | ConvertTo-Json

try {
    $adminResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $adminLogin -ContentType "application/json"
    Write-Host "✓ Admin login successful" -ForegroundColor Green
    Write-Host "Token: $($adminResponse.token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host "Role: $($adminResponse.role)" -ForegroundColor Yellow
    $adminToken = $adminResponse.token
} catch {
    Write-Host "✗ Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Testing Login with OPERATOR role ===" -ForegroundColor Cyan
$operatorLogin = @{
    username = "operator"
    password = "oper123"
    role = "OPERATOR"
} | ConvertTo-Json

try {
    $operatorResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $operatorLogin -ContentType "application/json"
    Write-Host "✓ Operator login successful" -ForegroundColor Green
    Write-Host "Token: $($operatorResponse.token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host "Role: $($operatorResponse.role)" -ForegroundColor Yellow
    $operatorToken = $operatorResponse.token
} catch {
    Write-Host "✗ Operator login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Testing Login with GUEST role ===" -ForegroundColor Cyan
$guestLogin = @{
    username = "guest"
    password = "guest123"
    role = "GUEST"
} | ConvertTo-Json

try {
    $guestResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method POST -Body $guestLogin -ContentType "application/json"
    Write-Host "✓ Guest login successful" -ForegroundColor Green
    Write-Host "Token: $($guestResponse.token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host "Role: $($guestResponse.role)" -ForegroundColor Yellow
    $guestToken = $guestResponse.token
} catch {
    Write-Host "✗ Guest login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test creating a site with ADMIN token
Write-Host "`n=== Testing Site Creation with ADMIN token ===" -ForegroundColor Cyan
$siteData = @{
    name = "Admin Test Site"
    address = "123 Admin St"
    city = "Springfield"
    state = "IL"
    zip = "62701"
} | ConvertTo-Json

try {
    $headers = @{
        "Authorization" = "Bearer $adminToken"
        "Content-Type" = "application/json"
    }
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/sites" -Method POST -Body $siteData -Headers $headers
    Write-Host "✓ Admin can create site" -ForegroundColor Green
    Write-Host "Created site: $($createResponse.data.name)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Admin site creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test creating a site with OPERATOR token
Write-Host "`n=== Testing Site Creation with OPERATOR token ===" -ForegroundColor Cyan
$siteData2 = @{
    name = "Operator Test Site"
    address = "456 Operator Ave"
    city = "Chicago"
    state = "IL"
    zip = "60601"
} | ConvertTo-Json

try {
    $headers = @{
        "Authorization" = "Bearer $operatorToken"
        "Content-Type" = "application/json"
    }
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/sites" -Method POST -Body $siteData2 -Headers $headers
    Write-Host "✓ Operator can create site" -ForegroundColor Green
    Write-Host "Created site: $($createResponse.data.name)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Operator site creation failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test creating a site with GUEST token (should fail)
Write-Host "`n=== Testing Site Creation with GUEST token ===" -ForegroundColor Cyan
$siteData3 = @{
    name = "Guest Test Site"
    address = "789 Guest Blvd"
    city = "Peoria"
    state = "IL"
    zip = "61601"
} | ConvertTo-Json

try {
    $headers = @{
        "Authorization" = "Bearer $guestToken"
        "Content-Type" = "application/json"
    }
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/sites" -Method POST -Body $siteData3 -Headers $headers
    Write-Host "✗ Guest should NOT be able to create site!" -ForegroundColor Red
} catch {
    Write-Host "✓ Guest correctly denied (403 Forbidden)" -ForegroundColor Green
}

# Test GET sites with GUEST token (should succeed)
Write-Host "`n=== Testing Site Retrieval with GUEST token ===" -ForegroundColor Cyan
try {
    $headers = @{
        "Authorization" = "Bearer $guestToken"
    }
    $sitesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/sites" -Method GET -Headers $headers
    Write-Host "✓ Guest can view sites" -ForegroundColor Green
    Write-Host "Found $($sitesResponse.data.Count) sites" -ForegroundColor Gray
} catch {
    Write-Host "✗ Guest site retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== All Tests Complete ===" -ForegroundColor Cyan

