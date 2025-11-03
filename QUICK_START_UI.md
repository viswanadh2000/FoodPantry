# 🚀 Quick Start - Connect UI to PantryPulse Backend

## 1️⃣ Start the Backend

```bash
# In the FoodPantry directory
.\gradlew bootRun
```

Server runs at: **`http://localhost:8080`**

---

## 2️⃣ Update Your Frontend Code

### Change 1: Update Base URL
```javascript
// In your React app
const API_BASE_URL = 'http://localhost:8080/api/v1';  // Note: /api/v1 not /api
```

### Change 2: Add Authentication
```javascript
// After login, save token
localStorage.setItem('jwt_token', response.data.token);

// Add to all POST/PUT/PATCH/DELETE requests
headers: {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
}
```

---

## 3️⃣ Test the Connection

### Test 1: Health Check
```bash
curl http://localhost:8080/actuator/health
```
Expected: `{"status":"UP"}`

### Test 2: Get Sites (No auth needed)
```bash
curl http://localhost:8080/api/v1/sites
```

### Test 3: Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

---

## 📚 Full Documentation

See **`UI_INTEGRATION_GUIDE.md`** for:
- Complete endpoint list
- React code examples
- Real-time events (SSE)
- Error handling
- Role-based access control
- Complete login component

---

## 🎯 What Changed in Backend

1. ✅ **All endpoints now use `/api/v1/*`** (not `/api/*`)
2. ✅ **JWT authentication required** for create/update/delete
3. ✅ **Role-based access control:**
   - ADMIN: Full access (including delete)
   - OPERATOR: Create/update only
   - VIEWER: Read-only
4. ✅ **CORS fully configured** - works with React dev server
5. ✅ **Real-time events via SSE** at `/api/v1/events/stream`

---

## ⚡ Quick Integration (React)

```javascript
// Setup axios
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1'
});

// Add token interceptor
api.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Use it
const sites = await api.get('/sites');
const newSite = await api.post('/sites', siteData);
```

---

## 🚨 Common Issues

**❌ 401 Error:** Token missing or expired
- **Fix:** Include `Authorization: Bearer {token}` header

**❌ 403 Error:** Permission denied
- **Fix:** User role doesn't have access (e.g., only ADMIN can delete)

**❌ CORS Error:** 
- **Fix:** Backend already configured! Check you're using `http://localhost:8080/api/v1/*`

---

## ✅ You're Ready!

The backend is configured and ready. Just:
1. Change URLs to `/api/v1/*`
2. Add JWT token to headers
3. Start building! 🎉
