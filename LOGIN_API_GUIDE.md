# Login API Integration Guide

## Overview
The authentication system now supports role-based access control with three roles: **ADMIN**, **OPERATOR**, and **GUEST**.

## Login Endpoint

**POST** `/api/v1/auth/login`

### Request Body
```json
{
  "username": "john.doe",
  "password": "password123",
  "role": "ADMIN"
}
```

### Request Fields
- `username` (required): User's username
- `password` (required): User's password  
- `role` (optional): One of `ADMIN`, `OPERATOR`, or `GUEST`
  - Case-insensitive (will be normalized to uppercase)
  - Defaults to `GUEST` if not provided

### Response (Success - 200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john.doe",
  "role": "ROLE_ADMIN"
}
```

### Response (Error - 400 Bad Request)
```json
{
  "error": "Invalid role. Must be ADMIN, OPERATOR, or GUEST"
}
```

## Role Hierarchy

```
ROLE_ADMIN > ROLE_OPERATOR > ROLE_GUEST
```

This means:
- **ADMIN** has all permissions that OPERATOR and GUEST have, plus admin-only actions
- **OPERATOR** has all permissions that GUEST has, plus operator actions
- **GUEST** has read-only access

## Role Permissions

### GUEST (Read-Only)
- ✅ GET `/api/v1/sites` - View all sites
- ✅ GET `/api/v1/sites/{id}` - View site details
- ✅ GET `/api/v1/inventory` - View inventory
- ✅ GET `/api/v1/inventory/{id}` - View inventory item
- ✅ GET `/api/v1/status` - View status updates
- ❌ Cannot create, update, or delete anything

### OPERATOR (Can Modify Data)
- ✅ All GUEST permissions
- ✅ POST `/api/v1/sites` - Create new sites
- ✅ PUT `/api/v1/sites/{id}` - Update sites
- ✅ POST `/api/v1/inventory` - Add inventory items
- ✅ PUT `/api/v1/inventory/{id}` - Update inventory
- ✅ POST `/api/v1/status` - Create status updates
- ❌ Cannot delete sites or access admin functions

### ADMIN (Full Access)
- ✅ All OPERATOR permissions
- ✅ DELETE `/api/v1/sites/{id}` - Delete sites
- ✅ DELETE `/api/v1/inventory/{id}` - Delete inventory
- ✅ GET `/api/v1/admin/**` - Access admin endpoints
- ✅ All other administrative functions

## Using the Token

After successful login, include the token in all subsequent requests:

### Authorization Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Example API Call (JavaScript)
```javascript
// Login
const loginResponse = await fetch('http://localhost:8080/api/v1/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123',
    role: 'ADMIN'
  })
});

const { token, username, role } = await loginResponse.json();

// Use token for authenticated requests
const sitesResponse = await fetch('http://localhost:8080/api/v1/sites', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const sites = await sitesResponse.json();
```

## Token Refresh

**POST** `/api/v1/auth/refresh`

### Request Header
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response (Success - 200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john.doe",
  "role": "ROLE_ADMIN",
  "message": "Token refreshed successfully"
}
```

**Note:** The refreshed token will have the same role as the original token.

## Token Lifetime
- JWT tokens expire after **60 minutes**
- Use the `/auth/refresh` endpoint to get a new token before expiration
- Store tokens securely (use `sessionStorage` or `localStorage` with XSS protection)

## UI Gating Based on Role

The `role` field in the login response should be used for UI visibility control:

```javascript
// Example: Hide "Delete" button for non-admins
if (role !== 'ROLE_ADMIN') {
  deleteButton.style.display = 'none';
}

// Example: Disable "Create Site" for guests
if (role === 'ROLE_GUEST') {
  createSiteButton.disabled = true;
}
```

**Important:** UI gating is for user experience only. Server-side permissions are ALWAYS enforced via the JWT token, regardless of what the UI shows.

## Testing

Run the PowerShell test script:
```powershell
.\test-login-with-roles.ps1
```

This will test:
1. Login with all three roles
2. ADMIN creating sites (should succeed)
3. OPERATOR creating sites (should succeed)
4. GUEST creating sites (should fail with 403)
5. GUEST viewing sites (should succeed)

## Error Handling

### 400 Bad Request
- Missing username
- Missing password
- Invalid role value

### 401 Unauthorized
- Invalid or expired token
- Missing Authorization header

### 403 Forbidden
- User authenticated but lacks required role/permission
- Example: GUEST trying to create a site

## Security Notes

1. **HTTPS Required in Production**: Always use HTTPS in production to protect tokens
2. **No Password Validation**: Currently accepts any password (dev mode only)
3. **Role Trust**: The server trusts the role from login - no user database validation yet
4. **Token Storage**: Never store tokens in cookies without HttpOnly flag
5. **XSS Protection**: Sanitize all user inputs to prevent token theft

## Next Steps for Production

- [ ] Add user database with hashed passwords
- [ ] Implement password validation
- [ ] Add email verification
- [ ] Implement password reset flow
- [ ] Add rate limiting on login endpoint
- [ ] Add account lockout after failed attempts
- [ ] Implement refresh token rotation
- [ ] Add audit logging for authentication events
