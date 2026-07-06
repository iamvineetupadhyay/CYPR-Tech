# API Reference — CYPR Tech

Complete endpoint documentation for the CYPR Tech Spring Boot REST API. All endpoints are prefixed with the base API URL (e.g. `http://localhost:8080/api`).

## Authentication APIs

### 1. Register Account
- **Endpoint**: `POST /auth/register`
- **Access**: Public
- **Request Payload**:
  ```json
  {
    "email": "user@example.com",
    "password": "SecurePass123!",
    "fullName": "Vineet Kumar"
  }
  ```
- **Response Payload (Success)**:
  ```json
  {
    "message": "Account created successfully! Please verify your email.",
    "status": "SUCCESS"
  }
  ```

### 2. Login
- **Endpoint**: `POST /auth/login`
- **Access**: Public
- **Request Payload**:
  ```json
  {
    "identifier": "user@example.com",
    "password": "SecurePass123!"
  }
  ```
- **Response Payload (Success)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsIn...",
    "userId": 123,
    "email": "user@example.com",
    "name": "Vineet Kumar"
  }
  ```

---

## Security & Scan APIs

### 1. URL Phishing Scan
- **Endpoint**: `POST /phish-check`
- **Access**: Authenticated / Public (Rate-limited)
- **Request Payload**:
  ```json
  {
    "url": "https://paypal-security-login.xyz/signin"
  }
  ```
- **Response Payload**:
  ```json
  {
    "status": "PHISHING",
    "url": "https://paypal-security-login.xyz/signin",
    "score": 87,
    "tier": "CRITICAL",
    "reasons": [
      {
        "title": "Combo-Squatting Detected",
        "description": "Brand 'paypal' matches with high-threat keyword '-security'",
        "severity": "HIGH"
      }
    ],
    "virusTotal": {
      "harmless": 45,
      "suspicious": 8,
      "malicious": 21,
      "verdict": "MALICIOUS"
    },
    "processingTime": "87ms"
  }
  ```

### 2. Scan History
- **Endpoint**: `GET /malware/history/{userId}`
- **Access**: Authenticated
- **Response Payload**:
  ```json
  [
    {
      "id": 45,
      "url": "https://secure-pay.in",
      "score": 12,
      "tier": "LOW",
      "timestamp": 1783317605000
    }
  ]
  ```

---

## Admin & Config APIs

### 1. Configure Brand Registry
- **Endpoint**: `POST /admin/brands`
- **Access**: Admin (JWT protected)
- **Request Payload**:
  ```json
  {
    "brand": "Zepto",
    "domains": ["zepto.in", "www.zepto.in"],
    "action": "ADD"
  }
  ```
- **Response Payload**:
  ```json
  {
    "message": "Brand Zepto successfully registered.",
    "status": "SUCCESS"
  }
  ```
