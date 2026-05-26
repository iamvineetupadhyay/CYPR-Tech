<div align="center">

<img src="https://img.shields.io/badge/CYPR-CyberMitra-c8ff00?style=for-the-badge&labelColor=0a0a0a&color=c8ff00" alt="Cypr" height="40"/>

# 🛡️ CYPR — CyberMitra Security Platform

### *"Invisible Defense. Absolute Control."*

**A production-grade, India-focused cybersecurity SaaS platform protecting B2C users and B2B SMEs from evolving digital threats.**  
Built on a Next.js 14 frontend, a high-performance Java Spring Boot backend, and the proprietary **VAJRA Detection Engine v5** — a 16-signal heuristic threat analysis system that operates with zero external API dependency.

---

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14-000000?style=for-the-badge&logo=nextdotjs&logoColor=white)](https://nextjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-RDS-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-EC2_+_RDS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/)
[![License](https://img.shields.io/badge/License-MIT-c8ff00?style=for-the-badge)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Production_Verified-00ff88?style=for-the-badge)]()

</div>

---

## 📋 Table of Contents

- [Project Vision](#-project-vision)
- [Platform Architecture](#-platform-architecture)
- [Product Roadmap](#-product-roadmap)
- [Tech Stack](#-tech-stack)
- [The VAJRA Detection Engine v5](#-the-vajra-detection-engine-v5)
  - [16-Signal Map](#16-signal-detection-map)
  - [BrandRegistry — Thread-Safe Runtime](#1-dynamic-brandregistry-thread-safe-singleton)
  - [UrlExpander — Shortener Resolution](#2-url-expander-shortener-blindspot-resolution)
  - [Subdomain Whitelisting](#3-subdomain-whitelisting-ind-3)
  - [Sigmoid Scoring Model](#sigmoid-normalized-risk-scoring)
- [Core Features](#-core-features)
- [API Reference](#-api-reference)
- [Frontend Design System](#-frontend-design-system)
- [AWS Infrastructure & Deployment](#-aws-infrastructure--deployment)
- [Database Schema & ORM](#-database-schema--orm)
- [Android Mobile App Specification](#-android-mobile-app-specification)
- [Complete File Structure](#-complete-file-structure)
- [Environment Configuration](#-environment-configuration)
- [Debugging Chronicles](#-debugging-chronicles)
- [Developer Profile](#-developer-profile)

---

## 🎯 Project Vision

**Cypr** (derived from *"Cyber Mitra"* — meaning **Cyber Friend** in Hindi/Sanskrit) is engineered as a complete, privacy-first cybersecurity ecosystem purpose-built for the Indian digital market.

The platform addresses two critical gaps in the current Indian security landscape:

| Gap | Cypr's Solution |
|-----|----------------|
| Consumer-grade tools lack enterprise-level heuristics | VAJRA Engine: 16-signal threat analysis, locally executed |
| Foreign platforms don't parse India-specific threat vectors | India-focused brand registry (Zepto, Blinkit, UPI apps) |
| Privacy-invasive telemetry in existing tools | Zero-knowledge, client-side only telemetry architecture |
| No unified SaaS for SME threat management | Full dashboard: URL scanning, password audit, email alerts |

> Cypr is not just a tool — it is a full security operations platform designed to scale from individual users to mid-sized enterprise deployments.

---

## 🏗️ Platform Architecture

The platform decouples compute from persistent storage using a scalable, highly available AWS-hosted architecture.

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                │
│   Next.js 14 Web App  ──────────────────────  Android Native App   │
└───────────────────────────────┬─────────────────────────────────────┘
                                │  HTTPS / REST API
┌───────────────────────────────▼─────────────────────────────────────┐
│                    SECURITY GATEWAY LAYER                           │
│         Cloudflare Turnstile Captcha Verification                   │
│              (Botnet & Credential Stuffing Mitigation)              │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────┐
│              SPRING BOOT APPLICATION SERVER (EC2)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │  Controllers │  │   Services   │  │  JWT Auth + Security     │  │
│  │  (REST API)  │  │ (Biz Logic)  │  │  Config (Stateless)      │  │
│  └──────┬───────┘  └──────┬───────┘  └──────────────────────────┘  │
│         │                 │                                          │
│  ┌──────▼─────────────────▼────────────────────────────────────┐   │
│  │             VAJRA DETECTION ENGINE v5                        │   │
│  │   16 Parallel Heuristic Signals  │  Sigmoid Score (0–100)   │   │
│  │   BrandRegistry Singleton        │  UrlExpander (HEAD Chain) │   │
│  └──────┬─────────────────────────────────────────────────────┘   │
│         │                                                           │
│  ┌──────▼──────────────────────────────────────────────────────┐   │
│  │            EXTERNAL API ORCHESTRATION                        │   │
│  │  VirusTotal (70+ AV Engines)  │  Brevo (Email Alerts)       │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────────┬─────────────────────────────────────┘
                                │  Hibernate ORM
┌───────────────────────────────▼─────────────────────────────────────┐
│                    PERSISTENCE LAYER (AWS RDS)                      │
│         PostgreSQL  ──  Users / SecurityAlerts / EmailLogs          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🗺️ Product Roadmap

```
Phase 1  ✅ ACTIVE   ──►  Unified Security SaaS Web Platform
                          (URL Scanner, Password Auditor, Dashboard, Email Alerts)

Phase 2  🔄 PLANNED  ──►  Cross-Browser Security Extension
                          (Chrome, Firefox, Brave, Edge — real-time tab protection)

Phase 3  🔄 PLANNED  ──►  Cypr Mobile Authenticator & Device Safety App
                          (Android Native — Retrofit2, Room DB, RadialGaugeView)

Phase 4  🔭 FUTURE   ──►  Privacy-First Chromium-Based Secure Browser
                          (Cypr Browser — built-in VAJRA engine, zero third-party telemetry)
```

---

## 🛠️ Tech Stack

### Backend
| Layer | Technology | Purpose |
|-------|-----------|---------|
| Runtime | Java 17 (Eclipse Temurin) | LTS, stable, enterprise-grade |
| Framework | Spring Boot 3.3.0 | REST API, Dependency Injection, Security |
| ORM | Hibernate (JPA) | PostgreSQL schema mapping, TEXT column migration |
| Auth | Spring Security + JWT | Stateless session management |
| Build | Maven + Lombok 1.18.34 | Dependency management, boilerplate reduction |
| Container | Docker (eclipse-temurin:17-jre-jammy) | Reproducible production deployments |

### Frontend
| Layer | Technology | Purpose |
|-------|-----------|---------|
| Framework | Next.js 14 (App Router) | SSR, routing, component architecture |
| Styling | Tailwind CSS | Utility-first responsive design |
| Animation | Framer Motion | Fluid, premium UI transitions |
| Fonts | Barlow Condensed + JetBrains Mono | Visual brand identity |

### Infrastructure
| Service | Provider | Role |
|---------|---------|------|
| Compute | AWS EC2 (Ubuntu 24.04 LTS) | Backend application hosting |
| Database | AWS RDS PostgreSQL | Managed relational persistence |
| Bot Protection | Cloudflare Turnstile | Captcha at API gateway layer |
| Email | Brevo Transactional API | Decoupled security alert delivery |
| Threat Intel | VirusTotal API (70+ AV engines) | Multi-engine URL verdict validation |
| Blocklists | Phishing.army + OpenPhish | 143,150+ live malicious domains (in-memory) |

---

## 🪓 The VAJRA Detection Engine v5

> **VAJRA** — *Various Attack Junction & Reconnaissance Algorithm*

VAJRA is Cypr's core, proprietary, **zero-external-API** threat intelligence engine. All 16 signals execute **locally and in-memory**, ensuring that no user browsing data is ever transmitted to third parties during heuristic analysis.

### Architecture Philosophy

```
INPUT URL
    │
    ▼
┌─────────────────────────────────────────────────────┐
│  PRE-PROCESSING                                     │
│  • URL normalization (NFKC Unicode)                │
│  • Scheme extraction & validation                  │
│  • Shortener detection → UrlExpander chain         │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  16 PARALLEL SIGNAL ANALYSIS                        │
│  Each signal returns a weighted sub-score           │
│  Signals run independently — no cascade dependency  │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  SCORE AGGREGATION                                  │
│  Raw score → Sigmoid normalization → 0–100 scale    │
│  Mapped to: SAFE / LOW / MEDIUM / HIGH / CRITICAL   │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  OPTIONAL: VirusTotal Cross-Validation              │
│  Triggered for ambiguous MEDIUM-tier scores         │
│  Returns: harmless / suspicious / malicious counts  │
└─────────────────────────────────────────────────────┘
```

---

### 16-Signal Detection Map

```
 #   SIGNAL NAME                  TECHNIQUE                              MAX SCORE
─────────────────────────────────────────────────────────────────────────────────
 01  Shannon Entropy              DGA / random-label detection                  30
 02  N-Gram Language Model        Real word vs. gibberish classification         28
 03  Consonant–Vowel Ratio        Unpronounceable = algorithmically generated    22
 04  Levenshtein Typosquatting    paypa1, g00gle, rn→m substitutions             40
 05  Combo-Squatting              paypal-secure-update.com pattern detection      38
 06  Unicode NFKC Homoglyph      Full confusable normalisation (IDN attacks)     45
 07  Subdomain Brand Abuse        paypal.com.evil.xyz structure parsing           45
 08  URL Structural Signals       @-symbol, depth, length, parameter abuse        45
 09  Malicious Path Patterns      /wp-login, /admin, open redirect detection      35
 10  High-Risk TLD Scoring        Weighted abuse frequency table (.xyz, .tk)      35
 11  IP-as-Host Detection         Bypasses DNS reputation scoring systems         38
 12  Scheme Abuse                 data:, javascript:, vbscript: payloads          50
 13  Hex / Percent Obfuscation    %2e, %40, double-encoding evasion               40
 14  URL Shortener Masking        bit.ly, tinyurl, t.co → UrlExpander chain      20
 15  Reversed / Inverted Brand    lapyap.com, U+202E RTL override detection       45
 16  Port Anomaly Detection       :8080, :4444, non-standard port scoring         25
─────────────────────────────────────────────────────────────────────────────────
                                                              TOTAL MAX:         581
```

> **Scoring Note:** The raw maximum of 581 is intentionally over-capped. The sigmoid normalization function compresses this into a clean 0–100 scale, preventing any single signal from dominating the final verdict.

---

### 1. Dynamic `BrandRegistry` (Thread-Safe Singleton)

In legacy VAJRA versions, monitored brands were static and required application restarts to update. VAJRA v5 eliminates this limitation entirely.

**Implementation:**
```java
// Thread-safe runtime brand registry
private static final ConcurrentHashMap<String, BrandEntry> REGISTRY = new ConcurrentHashMap<>();

// Admin endpoint — no restart required
// POST /api/admin/brands
public void registerBrand(String brandName, String registrableDomain, List<String> aliases) {
    REGISTRY.put(brandName.toLowerCase(), new BrandEntry(registrableDomain, aliases));
    log.info("BrandRegistry updated: {} -> {} (aliases: {})", brandName, registrableDomain, aliases);
}
```

**Why it matters:**
- New Indian fintech apps (Zepto, Blinkit, ONDC-linked platforms) get added at runtime
- Zero application downtime during brand list updates
- Thread-safe reads under high concurrent scan load

---

### 2. URL Expander — Shortener Blindspot Resolution

Attackers routinely hide malware targets behind trusted shortener services (`bit.ly`, `t.co`, `tinyurl.com`). Naive scanners evaluate the shortener URL, not the destination — returning a false SAFE verdict.

**VAJRA's Resolution Chain:**
```
Input: bit.ly/3xK9mZ
    │
    ▼
[Shortener Detected] ── known-shortener domain list
    │
    ▼
HTTP HEAD Request (non-blocking, no content downloaded)
    │
    ▼
Follow 3xx Redirect Chain (manual status resolution, max 5 hops)
    │
    ├── Loop protection: tracks visited URLs, breaks on cycle
    │
    ▼
Final destination URL extracted
    │
    ▼
Full 16-signal VAJRA analysis on REAL target URL
```

**Safety constraints:**
- Maximum 5 redirect hops (prevents infinite loop attacks)
- HEAD-only requests (zero content downloaded, zero SSRF risk)
- Timeout-bound connections (prevents hanging on slow decoys)

---

### 3. Subdomain Whitelisting `[IND-3]`

A naive implementation would flag `internal.dev.legacy.amazon.com` as suspicious (excessive subdomain depth). VAJRA v5 includes corporate subdomain whitelisting rules.

**Logic:**
```
URL: internal.dev.legacy.amazon.com
    │
    ▼
Extract registrable domain → amazon.com
    │
    ▼
Check against whitelist map:
  amazon.com → VERIFIED (brand: Amazon)
    │
    ▼
Signal 07 (Subdomain Brand Abuse) → BYPASSED
    │
    ▼
No false positive generated ✓
```

---

### Sigmoid-Normalized Risk Scoring

Raw heuristic scores are compressed into a human-readable 0–100 scale using sigmoid normalization, then mapped to five clean threat tiers:

```
Raw Score ──► σ(x) normalization ──► 0–100 scale
                                          │
                    ┌─────────────────────┼──────────────────────┐
                    │                     │                       │
                 0–19                  20–49                  50–69
                 SAFE                   LOW                  MEDIUM
                  🟢                    🟡                    🟠
                                          │
                    ┌─────────────────────┘
                    │
                 70–89                 90–100
                  HIGH                CRITICAL
                  🔴                    ⛔
```

---

## ✨ Core Features

### 🔍 URL & Phishing Scanner
- Full VAJRA 16-signal heuristic analysis (local, zero-API)
- Optional VirusTotal cross-validation (70+ AV engine consensus)
- Real-time threat categorization with per-signal reasoning
- Shortener resolution through full redirect chain traversal

### 🔑 Password Security Auditor
- Shannon entropy calculation for password strength scoring
- Common password pattern detection
- Breach correlation signals
- Visual strength meter output (strength bar UI component)

### 📊 Security Dashboard (NexusSec)
- Personalized logged-in index with live threat overview
- 70-day activity heatmap grid (scan history visualization)
- Rotating SVG score ring (animated composite safe-score)
- Quick stats: scans run, threats detected, safe score trend

### 📧 Transactional Security Alerts
- Brevo-powered email delivery (decoupled from application logic)
- Automated alerts on CRITICAL threat detection
- Configurable alert thresholds per user account

### 🏢 Admin Control Panel
- Runtime brand registry management (`POST /api/admin/brands`)
- Live blocklist sync status (143,150+ domain index)
- User management and scan telemetry review

### 🔐 Authentication & Access Control
- Stateless JWT session management
- Cloudflare Turnstile at API gateway (botnet mitigation)
- Secure password hashing (BCrypt)

---

## 🔌 API Reference

All endpoints are prefixed with `/api`. Authentication via `Authorization: Bearer <jwt_token>` header (except `/auth/*`).

---

### `POST /api/phish-check`

Analyze a URL using the VAJRA heuristic engine with optional VirusTotal cross-validation.

**Request:**
```http
POST /api/phish-check
Content-Type: application/json

{
  "url": "https://paypal-security-login.xyz/signin"
}
```

**Response:**
```json
{
  "status": "Phishing",
  "url": "https://paypal-security-login.xyz/signin",
  "score": 87,
  "tier": "CRITICAL",
  "reasons": [
    {
      "signal": "COMBO_SQUATTING",
      "title": "Combo-Squatting Detected",
      "description": "Brand 'paypal' combined with high-threat keyword '-security'",
      "severity": "HIGH",
      "weight": 38
    },
    {
      "signal": "HIGH_RISK_TLD",
      "title": "High-Risk TLD",
      "description": "Domain uses .xyz — high abuse frequency TLD",
      "severity": "MEDIUM",
      "weight": 28
    }
  ],
  "virusTotal": {
    "harmless": 45,
    "suspicious": 8,
    "malicious": 21,
    "undetected": 0,
    "verdict": "MALICIOUS"
  },
  "scanDurationMs": 312
}
```

---

### `POST /api/auth/register`

Register a new user account.

**Request:**
```json
{
  "name": "Vineet Kumar",
  "email": "vineet@example.com",
  "password": "SecureP@ssw0rd!"
}
```

**Response:**
```json
{
  "token": "<jwt>",
  "user": { "id": 1, "name": "Vineet Kumar", "email": "vineet@example.com" }
}
```

---

### `POST /api/auth/login`

Authenticate and retrieve a JWT session token.

**Request:**
```json
{
  "email": "vineet@example.com",
  "password": "SecureP@ssw0rd!"
}
```

---

### `GET /api/user/dashboard`

Retrieve the authenticated user's dashboard summary.

**Response:**
```json
{
  "safeScore": 84,
  "totalScans": 143,
  "threatsDetected": 17,
  "lastScanAt": "2026-05-25T18:32:00Z",
  "recentActivity": [ ... ],
  "heatmapData": { "2026-05-25": 3, "2026-05-24": 7 }
}
```

---

### `POST /api/admin/brands`

Register a new brand in the live `BrandRegistry` (admin-only, no restart required).

**Request:**
```json
{
  "brandName": "zepto",
  "registrableDomain": "zeptonow.com",
  "aliases": ["zepto-app", "zeptodelivery"]
}
```

---

### Telemetry Payload Schema

Scan records stored in PostgreSQL include the following telemetry schema:

```json
{
  "scanId": "uuid",
  "userId": 42,
  "url": "https://...",
  "score": 87,
  "tier": "CRITICAL",
  "signals": { ... },
  "deviceInfo": {
    "userAgent": "Mozilla/5.0 ...",
    "platform": "web"
  },
  "detectedThreats": [ ... ],
  "virusTotalResult": { ... },
  "createdAt": "2026-05-26T10:00:00Z"
}
```

> **Note:** `device_info` and `detected_threats` are stored as PostgreSQL `TEXT` columns containing deep-nested JSON, supporting schema-free evolution without migrations.

---

## 🎨 Frontend Design System

The visual identity of Cypr is built around a **Security Operations Center (SOC)** aesthetic — dark, precise, and command-grade.

### Color Tokens

| Token | Hex | Usage |
|-------|-----|-------|
| `--bg-primary` | `#0a0a0a` | Base dark background |
| `--bg-card` | `#111111` | Card and panel surfaces |
| `--accent-lime` | `#c8ff00` | Primary brand accent, CTAs |
| `--accent-safe` | `#00ff88` | Safe status indicators |
| `--accent-warning` | `#ffaa00` | High-risk warnings |
| `--accent-critical` | `#ff3333` | Critical threat alerts |
| `--text-primary` | `#ffffff` | Primary content |
| `--text-muted` | `#888888` | Secondary labels |

### Typography

| Role | Font | Style |
|------|------|-------|
| Display / Headings | Barlow Condensed | Bold, Uppercase |
| Body text | Inter | Regular, 400–600 |
| Monospace / Raw data | JetBrains Mono | Code, logs, entropy scores |

### Component Library

| Component | Description |
|-----------|-------------|
| `ScoreRing` | Rotating SVG ring displaying composite safe-score (0–100) |
| `ThreatCard` | Signal-level breakdown with severity badge and weight |
| `ActivityHeatmap` | 70-day scan history grid (GitHub-style) |
| `StrengthBar` | Password entropy visual meter |
| `ToastNotification` | Global non-blocking alert system |
| `MobileDrawer` | Collapsible nav with status dot indicator |

### Frontend Evolution Sessions

| Session | Milestone |
|---------|-----------|
| Session 1 | Particle canvas, glassmorphism cards, local password/URL tools |
| Session 2 | 4-breakpoint responsive overhaul, collapsible nav |
| Session 3 | Support dropdowns, auto character counters, accordion FAQ |
| Session 4 | Design pivot: Cyan → Lime Green `#c8ff00`, Barlow Condensed, scanlines |
| Session 5 | NexusSec Dashboard, rotating SVG score ring, avatar uploads |
| Session 6 | Personalized logged-in index, 70-day activity heatmap |
| Session 7 | Next.js 14 migration, Tailwind CSS, Framer Motion animations |
| Session 8 | Code audit: fixed localhost URLs, mapped active state notifications |

---

## 🌐 AWS Infrastructure & Deployment

### Infrastructure Overview

| Resource | Service | Configuration |
|---------|---------|--------------|
| Compute | AWS EC2 | Ubuntu 24.04 LTS |
| Database | AWS RDS | PostgreSQL (managed, multi-AZ recommended) |
| Memory | Virtual Swap | 2 GB swap configured on EC2 host |
| Container | Docker | eclipse-temurin:17-jre-jammy base |
| DNS / Bot Protection | Cloudflare | Turnstile Captcha at API gateway |

> **Why 2 GB Virtual Swap?** The VAJRA engine loads 143,150+ malicious domains into a `ConcurrentHashMap` at startup. On constrained EC2 instances, swap prevents OOM kills during high-concurrency scan bursts.

---

### Production Dockerfile

```dockerfile
# eclipse-temurin:17-jre-jammy — minimal JRE, no JDK in production
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy compiled artifact only — no source code in image
COPY target/cypr-backend-1.0.0.jar app.jar

EXPOSE 8080

# Memory-bounded JVM: 400 MB max heap on constrained instances
# -Xmx400m prevents OOM on EC2 t2/t3.micro
# -Xms200m pre-allocates to avoid GC pressure at startup
ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "app.jar"]
```

### Build & Deploy

```bash
# 1. Build the JAR
mvn clean package -DskipTests

# 2. Build Docker image
docker build -t cypr-backend:1.0.0 .

# 3. Run with environment injection (never hardcode secrets)
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<rds-endpoint>:5432/cypr" \
  -e SPRING_DATASOURCE_USERNAME="<username>" \
  -e SPRING_DATASOURCE_PASSWORD="<password>" \
  -e VIRUSTOTAL_API_KEY="<key>" \
  -e CYPR_JWT_SECRET="<secret>" \
  -e BREVO_API_KEY="<key>" \
  --name cypr-backend \
  cypr-backend:1.0.0
```

---

## 🗄️ Database Schema & ORM

### Core Tables

```sql
-- Users
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,      -- BCrypt hashed
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- Scan / Security Alerts
CREATE TABLE security_alerts (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT REFERENCES users(id),
    url              TEXT NOT NULL,
    score            INTEGER NOT NULL,
    tier             VARCHAR(20) NOT NULL,
    reasons          TEXT,                  -- JSON array (deep-nested)
    device_info      TEXT,                  -- JSON object
    detected_threats TEXT,                  -- JSON array
    virus_total      TEXT,                  -- JSON object
    created_at       TIMESTAMP DEFAULT NOW()
);

-- Email Logs
CREATE TABLE email_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id),
    recipient   VARCHAR(255) NOT NULL,
    subject     VARCHAR(255) NOT NULL,
    status      VARCHAR(50) NOT NULL,
    sent_at     TIMESTAMP DEFAULT NOW()
);
```

> **Schema Evolution Note:** Legacy `VARCHAR` columns were automatically migrated to `TEXT` via Hibernate DDL auto-update to support deep-nested JSON payloads without data truncation.

---

## 📱 Android Mobile App Specification

### Architecture

| Component | Technology |
|-----------|-----------|
| Navigation | Single Activity + Fragment Navigation Architecture |
| URL Risk Display | Custom `RadialGaugeView` (animated SVG-style gauge) |
| Password Strength | Custom `StrengthBar` view component |
| Local Storage | Room DB (scan history cache) + SharedPreferences (user config) |
| Networking | Retrofit2 + OkHttp3 (retry interceptors, exponential backoff) |

### Safe Score Calculation

```java
/**
 * Calculates the user's composite safe score from their recent scan history.
 * Analyzes the last 20 scans to produce a rolling security posture score.
 *
 * @param history List of ScanResult objects from Room DB
 * @return Safe score between 0 (high risk) and 100 (clean perimeter)
 */
public int calculateSafeScore(List<ScanResult> history) {
    if (history.isEmpty()) return 100; // No scans = clean perimeter assumed

    double avgRisk = history.stream()
        .limit(20)                          // Rolling window: last 20 scans
        .mapToInt(ScanResult::getScore)
        .average()
        .orElse(0);

    return Math.min(100, Math.max(0, (int)(100 - avgRisk)));
}
```

### Networking Layer

```java
// Retrofit client with exponential backoff for EC2 cold-start tolerance
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new RetryInterceptor(maxRetries = 3, backoffMs = 1000))
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(BuildConfig.CYPR_API_BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

---

## 📁 Complete File Structure

```
CYPR/
├── backend/                                 # Enterprise Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       ├── java/com/cypr/
│   │       │   ├── config/
│   │       │   │   ├── AsyncConfig.java      # Async thread pool config
│   │       │   │   ├── AuthConfig.java       # Spring Security filter chain
│   │       │   │   ├── DatabaseInitConfig.java # Startup blocklist loader
│   │       │   │   ├── JwtConfig.java        # JWT signing & validation
│   │       │   │   └── WebConfig.java        # CORS, MVC settings
│   │       │   ├── controller/
│   │       │   │   ├── AuthController.java   # /api/auth/** endpoints
│   │       │   │   ├── ScannerController.java # /api/phish-check
│   │       │   │   ├── UserController.java   # /api/user/** endpoints
│   │       │   │   ├── EmailController.java  # /api/alerts/**
│   │       │   │   └── AdminController.java  # /api/admin/** (BrandRegistry)
│   │       │   ├── engine/
│   │       │   │   ├── VajraEngine.java      # Orchestrator: 16 signals
│   │       │   │   ├── BrandRegistry.java    # Thread-safe ConcurrentHashMap
│   │       │   │   ├── UrlExpander.java      # HEAD-chain shortener resolver
│   │       │   │   ├── EntropyAnalyzer.java  # Shannon entropy (Signal 01)
│   │       │   │   ├── NGramModel.java       # Language model (Signal 02)
│   │       │   │   ├── HomoglyphNormalizer.java # NFKC Unicode (Signal 06)
│   │       │   │   ├── TyposquatDetector.java # Levenshtein (Signal 04)
│   │       │   │   └── SigmoidScorer.java    # Score normalization
│   │       │   ├── entity/
│   │       │   │   ├── User.java
│   │       │   │   ├── SecurityAlert.java
│   │       │   │   └── EmailLog.java
│   │       │   ├── model/
│   │       │   │   ├── ScanRequest.java      # DTO: incoming URL
│   │       │   │   ├── ScanResponse.java     # DTO: full threat analysis
│   │       │   │   ├── ThreatReason.java     # DTO: per-signal result
│   │       │   │   └── VirusTotalResult.java # DTO: VT verdict
│   │       │   ├── repository/
│   │       │   │   ├── UserRepository.java
│   │       │   │   ├── SecurityAlertRepository.java
│   │       │   │   └── EmailLogRepository.java
│   │       │   └── service/
│   │       │       ├── ScanService.java      # VAJRA orchestration
│   │       │       ├── VirusTotalService.java # VT API client
│   │       │       ├── BrevoEmailService.java # Brevo transactional email
│   │       │       ├── CaptchaService.java   # Turnstile verification
│   │       │       ├── BlocklistService.java # Phishing.army + OpenPhish sync
│   │       │       └── UserService.java      # Account management
│   │       └── resources/
│   │           └── application.properties   # Environment routing & fallbacks
│   ├── Dockerfile                           # Production container config
│   ├── pom.xml                              # Lombok 1.18.34, Java 17 target
│   └── .gitignore                           # Excludes /target, .idea/, *.class
│
└── frontend/                                # Next.js 14 Web Application
    ├── app/
    │   ├── page.tsx                         # Landing page
    │   ├── login/page.tsx
    │   ├── register/page.tsx
    │   ├── dashboard/page.tsx               # NexusSec dashboard
    │   ├── scanner/page.tsx                 # URL scanner tool
    │   ├── password/page.tsx                # Password auditor
    │   └── contact/page.tsx                 # Support & FAQ
    ├── components/
    │   ├── ScoreRing.tsx                    # Animated SVG score ring
    │   ├── ThreatCard.tsx                   # Signal breakdown card
    │   ├── ActivityHeatmap.tsx              # 70-day scan heatmap
    │   ├── StrengthBar.tsx                  # Password strength meter
    │   ├── MobileDrawer.tsx                 # Collapsible nav
    │   └── ToastNotification.tsx            # Global alert system
    ├── lib/
    │   ├── api.ts                           # Fetch wrapper + JWT handling
    │   ├── validators.ts                    # Client-side URL/password checks
    │   └── utils.ts                         # Shared utilities
    ├── public/
    │   ├── logo.svg
    │   └── favicon.ico
    └── tailwind.config.js                   # Theme: lime accent, dark bg
```

---

## 🔐 Environment Configuration

### Local Development (`application.properties` fallbacks)

```properties
# Database — override port for local PostgreSQL 18 install
spring.datasource.url=jdbc:postgresql://localhost:5433/cypr
spring.datasource.username=postgres
spring.datasource.password=local_dev_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
cypr.jwt.secret=${CYPR_JWT_SECRET:local_dev_secret_change_in_prod}
cypr.jwt.expiration=86400000

# VirusTotal
virustotal.api.key=${VIRUSTOTAL_API_KEY:}

# Brevo
brevo.api.key=${BREVO_API_KEY:}
```

### Production Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `SPRING_DATASOURCE_URL` | AWS RDS PostgreSQL connection URL | ✅ |
| `SPRING_DATASOURCE_USERNAME` | RDS master username | ✅ |
| `SPRING_DATASOURCE_PASSWORD` | RDS master password | ✅ |
| `CYPR_JWT_SECRET` | Cryptographic key for JWT signing (min 256-bit) | ✅ |
| `VIRUSTOTAL_API_KEY` | VirusTotal v3 API key | ✅ |
| `BREVO_API_KEY` | Brevo transactional email key | ✅ |

> **Security Rule:** Never commit secrets to version control. All sensitive values must be injected via environment variables or a secrets manager (AWS Secrets Manager recommended for production).

---

## 🔬 Debugging Chronicles

A candid account of the architectural bugs resolved during Cypr's development — each one a genuine engineering lesson.

---

### Bug 1 — Javac Version Incompatibility & Lombok AST Crash

**Symptom:**
```
java.lang.ExceptionInInitializerError
    at com.sun.tools.javac.code.TypeTag.<clinit>(TypeTag.java)
```

**Root Cause:** IntelliJ resolved to `javac 24.0.2` for compilation. Lombok `1.18.32` (pulled transitively by Spring Boot's parent POM) modifies Java ASTs using internal compiler APIs — and lacked support for Java 24 internals.

**Fix:**
```xml
<!-- pom.xml: explicit Lombok version override -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.34</version>   <!-- Compatible with Java 22, 23, 24 -->
    <scope>provided</scope>
</dependency>
```
Also realigned IntelliJ's Project SDK → Modules → Compiler settings back to JDK 17.

---

### Bug 2 — Database Connection Metadata Starvation (Port Mismatch)

**Symptom:**
```
Unable to create requested service [JdbcEnvironment]
due to: Unable to determine Dialect without JDBC metadata
```

**Root Cause:** PostgreSQL 18 was installed locally and bound to port `5433` (not the default `5432`). The application attempted `5432` → connection refused → Hibernate could not query DB metadata to determine SQL dialect.

**Fix:**
```properties
# application.properties — corrected local port
spring.datasource.url=jdbc:postgresql://localhost:5433/cypr
```
Added dynamic override so production RDS (port `5432`) takes precedence via environment variable injection.

---

### Bug 3 — Informal Code Artifacts in Production Codebase

**Symptom:** Logger output contained emoji characters (`🚀`, `⏳`, `⚠`, `✔`) and developer comments were written in Hinglish (e.g., *"sig14 ke baad analyze() FINAL URL fetch karta hai"*).

**Fix:** Full codebase sweep — replaced all emoji-decorated logs with standard SLF4J format strings. Translated all informal developer comments to professional English engineering documentation. Final output: enterprise-grade, public-ready code.

---

### Bug 4 — Build Pollution via Missing `.gitignore`

**Symptom:** Risk of pushing `/target/` (compiled binaries, 50+ MB) and `.idea/` (editor configs containing local paths) to the public repository.

**Fix:**
```gitignore
# Maven build output
target/
*.class
*.jar
*.war

# IntelliJ IDEA
.idea/
*.iml
*.iws

# Environment secrets (never commit)
.env
application-local.properties
```

---

## 👤 Developer Profile

```
┌─────────────────────────────────────────────────────┐
│  Vineet Kumar                                       │
│  Full-Stack Software Engineer & Backend Specialist  │
├─────────────────────────────────────────────────────┤
│  Education   B.Tech, Computer Science & Engineering │
│  Location    Dadri, Uttar Pradesh, India            │
│  Core Stack  Java · Spring Boot · PostgreSQL        │
│              Docker · AWS · Next.js · Android       │
├─────────────────────────────────────────────────────┤
│  Career      Primary:   Campus Placement            │
│  Goals       (HCL Tech / Tier 1 Corporates)        │
│              Long-Term: Scale Cypr into a leading   │
│              Indian B2C & B2B Cybersecurity Platform│
└─────────────────────────────────────────────────────┘
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Engineered by Vineet Kumar | Cypr Core Platform v2.5**  
*Last Updated: May 2026 · Total Engineering Modules: 9+ · Production Verified*

```
"Invisible Defense. Absolute Control."
```

[![GitHub](https://img.shields.io/badge/Built_with-☕_Java_&_⚡_Next.js-c8ff00?style=for-the-badge&labelColor=0a0a0a)](https://github.com)

</div>
