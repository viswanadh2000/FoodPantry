# 🔌 PantryPulse UI Integration Guide

## 🚀 Quick Start - Connect Your React/Frontend to Backend

### Step 1: Start the Backend Server

```bash
# In the FoodPantry directory
.\gradlew bootRun
```

The server will start on: **`http://localhost:8080`**

---

## 📝 Critical Changes for Your UI

### 1️⃣ Update Base URL

**All your API endpoints have changed from `/api/*` to `/api/v1/*`**

```javascript
// ❌ OLD (will NOT work)
const API_BASE_URL = 'http://localhost:8080/api';

// ✅ NEW (correct)
const API_BASE_URL = 'http://localhost:8080/api/v1';
```

### 2️⃣ Add JWT Authentication Headers

For **ALL** write operations (POST, PUT, PATCH, DELETE), you MUST include the JWT token:

```javascript
// After login, store the token
localStorage.setItem('jwt_token', response.data.token);

// Include in all authenticated requests
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
};
```

### 3️⃣ No CORS Changes Needed! ✅

The backend is already configured to accept requests from your frontend. CORS is **fully configured** to allow:
- All origins (dev mode)
- All methods (GET, POST, PUT, PATCH, DELETE)
- All headers (Authorization, Content-Type, etc.)

---

## 🔐 Authentication Flow

### Step 1: Login
```javascript
// POST /api/v1/auth/login
const login = async (username, password) => {
  const response = await fetch('http://localhost:8080/api/v1/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  // data = { token: "eyJhbGc...", username: "admin" }
  
  localStorage.setItem('jwt_token', data.token);
  localStorage.setItem('username', data.username);
  
  return data;
};
```

### Step 2: Use Token for Protected Endpoints
```javascript
// Example: Create a new site
const createSite = async (siteData) => {
  const response = await fetch('http://localhost:8080/api/v1/sites', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
    },
    body: JSON.stringify(siteData)
  });
  
  return await response.json();
};
```

### Step 3: Token Refresh (Optional)
```javascript
// POST /api/v1/auth/refresh
const refreshToken = async () => {
  const response = await fetch('http://localhost:8080/api/v1/auth/refresh', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
    }
  });
  
  const data = await response.json();
  localStorage.setItem('jwt_token', data.token);
  return data.token;
};
```

---

## 📡 Available Endpoints

### 🔓 Public Endpoints (No Auth Required)

```javascript
// Authentication
POST   /api/v1/auth/login              // Login
POST   /api/v1/auth/refresh            // Refresh token

// Sites (Read Only)
GET    /api/v1/sites                   // Get all sites
GET    /api/v1/sites/search?city=Boston&state=MA&page=0&size=20
GET    /api/v1/sites/{id}              // Get single site

// Inventory (Read Only)
GET    /api/v1/inventory               // Get all inventory
GET    /api/v1/inventory/low-stock?threshold=10

// Queue (Read Only + Create Token)
POST   /api/v1/queue/tokens            // Create queue token (for public kiosk)
GET    /api/v1/queue/tokens/{tokenNumber}
GET    /api/v1/queue/sites/{siteId}/waiting
GET    /api/v1/queue/sites/{siteId}/tokens

// Metrics
GET    /api/v1/metrics                 // Global metrics
GET    /api/v1/metrics/site/{siteId}   // Per-site metrics
GET    /api/v1/metrics/by-city         // By-city metrics

// Real-time Events
GET    /api/v1/events/stream           // SSE event stream

// Health Check
GET    /actuator/health
```

### 🔒 Protected Endpoints (Require JWT Token)

**OPERATOR or ADMIN Role Required:**
```javascript
// Sites
POST   /api/v1/sites                   // Create site
PUT    /api/v1/sites/{id}              // Update site

// Inventory
POST   /api/v1/inventory               // Create inventory
PATCH  /api/v1/inventory/{id}/adjust?quantity=5

// Queue Management
PATCH  /api/v1/queue/tokens/{tokenNumber}/status?status=CALLED

// Webhooks
POST   /api/v1/webhooks                // Register webhook
GET    /api/v1/webhooks                // List webhooks
PATCH  /api/v1/webhooks/{id}?active=true
DELETE /api/v1/webhooks/{id}
```

**ADMIN Role Only:**
```javascript
// Sites
DELETE /api/v1/sites/{id}              // Delete site

// Audit Logs
GET    /api/v1/admin/audit             // All audit logs
GET    /api/v1/admin/audit/user/{username}
GET    /api/v1/admin/audit/entity/{entity}
```

---

## 🎨 React Integration Examples

### Setup Axios with Interceptors

```javascript
// src/api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle 401 errors
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired - redirect to login
      localStorage.removeItem('jwt_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Service Layer Examples

```javascript
// src/services/siteService.js
import api from '../api/axios';

export const siteService = {
  // Get all sites
  getAll: () => api.get('/sites'),
  
  // Search sites
  search: (params) => api.get('/sites/search', { params }),
  
  // Get single site
  getById: (id) => api.get(`/sites/${id}`),
  
  // Create site (requires OPERATOR/ADMIN)
  create: (siteData) => api.post('/sites', siteData),
  
  // Update site (requires OPERATOR/ADMIN)
  update: (id, siteData) => api.put(`/sites/${id}`, siteData),
  
  // Delete site (requires ADMIN)
  delete: (id) => api.delete(`/sites/${id}`),
};

// Usage in component:
import { siteService } from '../services/siteService';

const Sites = () => {
  const [sites, setSites] = useState([]);
  
  useEffect(() => {
    siteService.getAll()
      .then(response => setSites(response.data.data))
      .catch(error => console.error(error));
  }, []);
  
  return (
    <div>
      {sites.map(site => (
        <div key={site.id}>{site.name}</div>
      ))}
    </div>
  );
};
```

### Real-Time Events (SSE)

```javascript
// src/hooks/useEvents.js
import { useEffect, useState } from 'react';

export const useEvents = () => {
  const [events, setEvents] = useState([]);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8080/api/v1/events/stream');

    eventSource.onopen = () => {
      console.log('✅ Connected to event stream');
      setConnected(true);
    };

    // Listen to specific events
    eventSource.addEventListener('inventory.low', (e) => {
      const event = JSON.parse(e.data);
      console.log('📦 Low stock alert:', event);
      setEvents(prev => [...prev, event]);
    });

    eventSource.addEventListener('queue.token.created', (e) => {
      const event = JSON.parse(e.data);
      console.log('🎫 New queue token:', event);
      setEvents(prev => [...prev, event]);
    });

    eventSource.addEventListener('queue.token.called', (e) => {
      const event = JSON.parse(e.data);
      console.log('📢 Token called:', event);
      setEvents(prev => [...prev, event]);
    });

    eventSource.onerror = (error) => {
      console.error('❌ SSE error:', error);
      setConnected(false);
      eventSource.close();
    };

    return () => {
      eventSource.close();
      setConnected(false);
    };
  }, []);

  return { events, connected };
};

// Usage in component:
const Dashboard = () => {
  const { events, connected } = useEvents();
  
  return (
    <div>
      <div className={connected ? 'online' : 'offline'}>
        {connected ? '🟢 Live' : '🔴 Disconnected'}
      </div>
      
      <div>
        {events.slice(-10).map((event, i) => (
          <div key={i}>{event.type}: {JSON.stringify(event.data)}</div>
        ))}
      </div>
    </div>
  );
};
```

---

## 📋 Request/Response Examples

### Create Queue Token
```javascript
// POST /api/v1/queue/tokens
const response = await fetch('http://localhost:8080/api/v1/queue/tokens', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    siteId: 1,
    contactName: "John Doe",
    contactPhone: "555-1234"
  })
});

// Response:
{
  "success": true,
  "data": {
    "id": 1,
    "tokenNumber": "BOS-20251102-0001",
    "status": "WAITING",
    "contactName": "John Doe",
    "estimatedWaitMinutes": 30,
    "createdAt": "2025-11-02T10:30:00"
  },
  "message": "Queue token created successfully"
}
```

### Search Sites
```javascript
// GET /api/v1/sites/search?city=Boston&page=0&size=20
const response = await fetch('http://localhost:8080/api/v1/sites/search?city=Boston&page=0&size=20');

// Response:
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Downtown Pantry",
        "address": "123 Main St",
        "city": "Boston",
        "state": "MA",
        "zip": "02101"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "number": 0,
    "size": 20
  },
  "message": "Sites fetched successfully"
}
```

### Get Metrics
```javascript
// GET /api/v1/metrics
const response = await fetch('http://localhost:8080/api/v1/metrics');

// Response:
{
  "totalSites": 12,
  "totalInventoryItems": 450,
  "lowStockItemCount": 23,
  "totalQueueTokens": 1543,
  "waitingQueueTokens": 15
}
```

---

## 🎯 Environment Variables

### For Development
Create a `.env` file in your React app:

```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1
# or for Create React App:
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
```

Then use it:
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL; // Vite
// or
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL; // CRA
```

### For Production
Update to your production backend URL:
```bash
VITE_API_BASE_URL=https://api.pantrypulse.io/api/v1
```

---

## 🧪 Testing the Connection

### 1. Test Health Endpoint
```bash
curl http://localhost:8080/actuator/health
```

### 2. Test Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

### 3. Test Get Sites
```bash
curl http://localhost:8080/api/v1/sites
```

### 4. Test SSE Stream
```bash
curl -N http://localhost:8080/api/v1/events/stream
```

---

## 🚨 Common Issues & Solutions

### Issue 1: CORS Errors
**Solution:** Backend CORS is already configured! If you still see errors:
- Check you're using the correct URL: `http://localhost:8080/api/v1/*`
- Ensure you're not blocking cookies if using credentials

### Issue 2: 401 Unauthorized
**Solution:** 
- Check if JWT token is being sent: `Authorization: Bearer {token}`
- Verify token hasn't expired (tokens expire in 60 minutes)
- Try refreshing the token using `/api/v1/auth/refresh`

### Issue 3: 403 Forbidden
**Solution:**
- User doesn't have required role for that endpoint
- Check role requirements in this guide
- ADMIN can do everything, OPERATOR can create/edit, VIEWER is read-only

### Issue 4: SSE Connection Drops
**Solution:**
- SSE connections can timeout after 30-60 seconds of no activity
- Implement auto-reconnect logic
- Backend sends heartbeat events to keep connection alive

---

## 📱 Complete Login Component Example

```javascript
// src/pages/Login.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/login', {
        username,
        password
      });

      // Store token
      localStorage.setItem('jwt_token', response.data.token);
      localStorage.setItem('username', response.data.username);

      // Redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h2>PantryPulse Login</h2>
        
        {error && <div className="error-message">{error}</div>}
        
        <div className="form-group">
          <label>Username</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            disabled={loading}
          />
        </div>
        
        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={loading}
          />
        </div>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default Login;
```

---

## ✅ Integration Checklist

Before deploying your UI:

- [ ] Update all API URLs from `/api/*` to `/api/v1/*`
- [ ] Implement JWT token storage and management
- [ ] Add `Authorization: Bearer {token}` header to authenticated requests
- [ ] Create login page/component
- [ ] Implement token refresh mechanism
- [ ] Add error handling for 401 (redirect to login)
- [ ] Add error handling for 403 (show permission error)
- [ ] Connect to SSE endpoint for real-time updates
- [ ] Test all CRUD operations
- [ ] Verify role-based UI elements (hide/show based on permissions)

---

## 🎉 You're Ready!

Your backend is **fully configured** and ready to accept connections from your UI. The main changes you need:

1. ✅ Change URLs to `/api/v1/*`
2. ✅ Add JWT token to headers
3. ✅ Handle authentication flow

**No CORS changes needed!** Everything else is handled by the backend.

Happy coding! 🚀
