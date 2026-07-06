# 🛡️ CYPR Tech — Enterprise Cybersecurity Platform

<div align="center">

### **Invisible Defense. Absolute Control.**

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Next.js 14](https://img.shields.io/badge/Next.js-14-black.svg?style=for-the-badge&logo=next.js&logoColor=white)](https://nextjs.org/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue.svg?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS Deployed](https://img.shields.io/badge/AWS-EC2%20%2B%20RDS-FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![License MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Production-grade India-first cybersecurity SaaS protecting everyday users from digital threats**

[🚀 **Get Started**](#-quick-start) • [📖 **Documentation**](#-platform-architecture) • [🔧 **Architecture**](#-platform-architecture) • [📱 **Mobile**](#-android-native-mobile-app-specification) • [🤝 **Contribute**](#contributing)

</div>

---

## ✨ What is CYPR Tech?

**CYPR Tech** (meaning *"Cyber Friend"* in Hindi) is a **zero-external-API threat detection system** that keeps you safe without compromising your privacy. Built with enterprise-grade infrastructure, it's designed for the Indian digital market.

### 🎯 Why Choose CYPR Tech?

| Feature | Impact |
|---------|--------|
| 🧠 **16-Signal AI Detection** | Catches phishing without cloud dependencies |
| 🔒 **Zero-Knowledge Privacy** | Your data never leaves your device |
| ⚡ **Real-time Scanning** | Sub-100ms threat analysis |
| 🌐 **143K+ Threat Database** | Updated malicious domains in-memory |
| 🛡️ **Multi-AV Verification** | VirusTotal 70+ engine validation |
| 🇮🇳 **India-Optimized** | Zepto, PhonePe, Blinkit threat signatures |

---

## 🚀 Quick Start

### Prerequisites
```bash
✓ Java 17 (JDK) or higher
✓ PostgreSQL 13+ (local or AWS RDS)
✓ Node.js 18+ (for frontend)
✓ Docker & Docker Compose
```

### 1️⃣ Clone & Navigate
```bash
git clone https://github.com/iamvineetupadhyay/CYPR-TECH.git
cd CYPR-TECH
```

### 2️⃣ Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
**Backend runs on:** `http://localhost:8080`

### 3️⃣ Frontend Setup
```bash
cd ../frontend
npm install
npm run dev
```
**Frontend runs on:** `http://localhost:3000`

### 4️⃣ Test the Engine
```bash
curl -X POST http://localhost:8080/api/phish-check \
  -H "Content-Type: application/json" \
  -d '{"url": "https://paypa1-secure.xyz/login"}'
```

---

## 🏗️ Platform Architecture

<div align="center">

```
┌─────────────────────────────────────────────────────────────┐
│                    USER LAYER (Web & Mobile)                │
│  Next.js 14 Dashboard │ React Components │ Android Native   │
└──────────────┬────────────────────────────────────┬──────────┘
               │                                    │
               │         HTTPS REST API             │
               ▼                                    ▼
┌─────────────────────────────────────────────────────────────┐
│              API GATEWAY & SECURITY LAYER                   │
│        Cloudflare Turnstile │ JWT Auth │ Rate Limiting      │
└──────────────┬────────────────────────────────────┬──────────┘
               │                                    │
               ▼                                    ▼
┌──────────────────────┐              ┌──────────────────────┐
│   VAJRA Engine v5    │              │ VirusTotal 70+ AVs   │
│  • Shannon Entropy   │              │ • Multi-Engine Scan  │
│  • N-Gram Models     │◄────────────►│ • Malware Detection  │
│  • Homoglyph Check   │              │ • Reputation Score   │
│  • Typosquat Logic   │              └──────────────────────┘
└──────────────┬───────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│              PERSISTENCE LAYER                              │
│  AWS RDS PostgreSQL │ Hibernate ORM │ Encrypted Storage     │
└─────────────────────────────────────────────────────────────┘
```

</div>

---

## 🪓 The VAJRA Detection Engine v5

The **V.A.J.R.A** (Various Attack Junction & Reconnaissance Algorithm) is CYPR Tech's proprietary threat detection system.

### 16 Independent Signals
```
┌──────────────────────────────────────────────────────────────┐
│ SIGNAL #  │ DETECTION TYPE          │ MAX SCORE │ PRIORITY  │
├──────────────────────────────────────────────────────────────┤
│ 01        │ Shannon Entropy          │    30     │ ⭐⭐⭐   │
│ 02        │ N-Gram Language Model    │    28     │ ⭐⭐⭐   │
│ 03        │ Consonant-Vowel Ratio    │    22     │ ⭐⭐    │
│ 04        │ Levenshtein Typosquat    │    40     │ ⭐⭐⭐   │
│ 05        │ Combo-Squatting          │    38     │ ⭐⭐⭐   │
│ 06        │ Unicode Homoglyph        │    45     │ ⭐⭐⭐⭐ │
│ 07        │ Subdomain Brand Abuse    │    45     │ ⭐⭐⭐⭐ │
│ 08        │ URL Structural Signals   │    45     │ ⭐⭐⭐⭐ │
│ 09        │ Malicious Path Patterns  │    35     │ ⭐⭐⭐   │
│ 10        │ High-Risk TLD Scoring    │    35     │ ⭐⭐⭐   │
│ 11        │ IP-as-Host Detection     │    38     │ ⭐⭐⭐   │
│ 12        │ Scheme Abuse             │    50     │ ⭐⭐⭐⭐ │
│ 13        │ Hex/Percent Obfuscation  │    40     │ ⭐⭐⭐   │
│ 14        │ URL Shortener Masking    │    20     │ ⭐⭐    │
│ 15        │ Reversed Brand Names     │    45     │ ⭐⭐⭐⭐ │
│ 16        │ Port Anomaly Detection   │    25     │ ⭐⭐    │
│                                      ├──────────┤
│                              MAX TOTAL │   581     │
└──────────────────────────────────────────────────────────────┘
```

### ⚡ Key Features

#### 🧵 Thread-Safe Brand Registry
```java
// Dynamic runtime brand updates without restart
POST /api/admin/brands
{
  "brand": "Zepto",
  "domains": ["zepto.in", "www.zepto.in"],
  "action": "ADD"
}
```

#### 🔗 URL Shortener Resolution
Automatically expands shortened URLs (bit.ly, tinyurl) following redirect chains up to 5 hops with loop protection.

#### ✅ Subdomain Whitelisting
Avoids false positives for corporate infrastructure while catching brand abuse attacks.

---

## 🎨 Frontend Design System

### Color Palette
```
█████ Deep Dark     #0a0a0a  (Background)
█████ Card Dark     #111111  (Component Base)
█████ Lime Accent   #c8ff00  (Primary CTA)
█████ Mint Safe     #00ff88  (Success State)
█████ Amber Warning #ffaa00  (High Risk)
█████ Red Danger    #ff3333  (Critical)
```

### Typography
- **Headings:** Barlow Condensed (Bold, Uppercase)
- **Monospace:** JetBrains Mono (Logs, Data, Scores)
- **Body:** Inter (Clean, Readable)

### Component Library
- ✅ Custom Score Ring (Animated SVG)
- ✅ Risk Gauge Component
- ✅ Activity Heatmap (70-day History)
- ✅ Toast Notifications (Global)
- ✅ Loading Skeleton States
- ✅ Responsive Drawer Navigation

---

## 📱 Android Native Mobile App

<div align="center">

| Screen | Features |
|--------|----------|
| 🏠 **Home** | Quick scan, recent results, risk summary |
| 🔍 **Scanner** | Real-time URL/Link analysis |
| 📊 **Dashboard** | Activity history, threat timeline |
| 👤 **Profile** | Settings, scan history, preferences |

</div>

### Tech Stack
- **Architecture:** Single Activity + Fragment Navigation
- **Data Storage:** Room DB + SharedPreferences
- **Networking:** Retrofit2 + OkHttp3
- **UI Framework:** Android Material Design 3

### Safe Score Algorithm
```java
public int calculateSafeScore(List<ScanResult> history) {
    if (history.isEmpty()) return 100;
    double avgRisk = history.stream()
        .limit(20)                  // Recent 20 scans
        .mapToInt(ScanResult::getScore)
        .average()
        .orElse(0);
    return Math.min(100, Math.max(0, (int)(100 - avgRisk)));
}
```

---

## 🌐 AWS Infrastructure

### Deployment Architecture
```
┌─────────────────────────────────────────┐
│      AWS EC2 (Ubuntu 26.04 LTS)         │
│  • 2GB RAM Instance                     │
│  • 2GB Virtual Swap Memory              │
│  • JVM: -Xmx400m -Xms200m              │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│   AWS RDS PostgreSQL (Managed)          │
│  • Auto Backups                         │
│  • Multi-AZ Failover                    │
│  • Encryption at Rest                   │
└─────────────────────────────────────────┘
```

### Docker Image
```dockerfile
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/cypr-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "app.jar"]
```

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://[RDS-ENDPOINT]:5432/cypr
SPRING_DATASOURCE_USERNAME=cypr_user
SPRING_DATASOURCE_PASSWORD=***SECURE***
VIRUSTOTAL_API_KEY=***SECURE***
CYPR_JWT_SECRET=***SECURE***
BREVO_API_KEY=***SECURE***
```

---

## 🔌 API Reference

### 🔍 POST `/api/phish-check`
Analyze a URL using VAJRA + VirusTotal.

**Request:**
```json
{
  "url": "https://paypal-security-login.xyz/signin"
}
```

**Response:**
```json
{
  "status": "PHISHING",
  "url": "https://paypal-security-login.xyz/signin",
  "score": 87,
  "tier": "CRITICAL",
  "reasons": [
    {
      "title": "Combo-Squatting Detected",
      "description": "Brand 'paypal' + threat keyword '-security'",
      "severity": "HIGH"
    },
    {
      "title": "Homoglyph Attack",
      "description": "Unicode confusable characters detected",
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

### 👤 POST `/api/auth/register`
Create a new user account.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe"
}
```

### 📧 POST `/api/alerts/subscribe`
Subscribe to security alerts.

**Request:**
```json
{
  "email": "user@example.com",
  "threatLevel": ["HIGH", "CRITICAL"],
  "frequency": "INSTANT"
}
```

---

## 📊 Development Roadmap

```
Phase 1 (✅ ACTIVE)
├─ Unified Security SaaS Web Platform
├─ VAJRA Detection Engine v5
└─ Admin Dashboard & Analytics

Phase 2 (📋 PLANNED - Q3 2026)
├─ Chrome/Firefox/Brave Extension
├─ Real-time Page Scanning
└─ Browser History Analysis

Phase 3 (📋 PLANNED - Q4 2026)
├─ Android Mobile App
├─ Device Safety Module
└─ Cross-Platform Sync

Phase 4 (🔮 FUTURE)
├─ Privacy-First Chromium Browser
├─ VPN Integration
└─ Advanced Threat Intelligence
```

---

## 🐛 Known Issues & Fixes

### ✅ Issue #1: Javac 24 Lombok Incompatibility
- **Fixed:** Upgraded to Lombok 1.18.34
- **Details:** Aligned JDK 17 target with compatible Lombok AST handling

### ✅ Issue #2: PostgreSQL Port Mismatch
- **Fixed:** Configuration fallback to port 5433
- **Details:** Dynamic datasource routing for local/production environments

### ✅ Issue #3: Emoji & Hinglish Cleanup
- **Fixed:** Enterprise-grade codebase formatting
- **Details:** All 🚀⏳⚠✔📧 removed, comments translated to English

---

## 📁 Project Structure

```
cypr/
├── 📦 backend/                          # Spring Boot API Server
│   ├── src/main/java/com/cypr/
│   │   ├── 🔐 config/                  # Security, Database, Web configs
│   │   ├── 🌐 controller/              # REST endpoints
│   │   ├── 🛡️ engine/                  # VAJRA detection logic
│   │   ├── 💾 entity/                  # JPA database models
│   │   ├── 📮 model/                   # DTOs & request/response
│   │   ├── 🗄️ repository/              # Database access layer
│   │   └── ⚙️ service/                 # Business logic
│   ├── Dockerfile                      # Container config
│   ├── pom.xml                         # Maven dependencies
│   └── .gitignore                      # Git rules
│
└── 🎨 frontend/                        # Next.js 14 Dashboard
    ├── app/                            # Pages & routes
    ├── components/                     # React components
    ├── lib/                            # Utilities & validators
    ├── public/                         # Assets & logos
    └── tailwind.config.js              # Styling config
```

---

## 🚀 Building for Production

### Backend
```bash
cd backend
mvn clean package -DskipTests

# Run Docker image
docker build -t cypr-backend:latest .
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://[RDS]:5432/cypr \
  -e SPRING_DATASOURCE_USERNAME=cypr_user \
  -e SPRING_DATASOURCE_PASSWORD=***SECRET*** \
  -e VIRUSTOTAL_API_KEY=***SECRET*** \
  -p 8080:8080 \
  cypr-backend:latest
```

### Frontend
```bash
cd frontend
npm run build
npm start

# Or deploy to Vercel
vercel deploy --prod
```

---

## 📈 Performance Metrics

| Metric | Value |
|--------|-------|
| **URL Analysis Speed** | < 100ms (p95) |
| **Threat Database Size** | 143,150+ malicious domains |
| **Detection Signals** | 16 independent algorithms |
| **External API Calls** | Only VirusTotal (optional) |
| **Memory Footprint** | ~200-400MB (JVM) |
| **Max Concurrent Users** | 1000+ |

---

## 🔐 Security Practices

✅ **End-to-End Encryption** — TLS 1.3 for all data in transit  
✅ **JWT Authentication** — Secure token-based auth  
✅ **Rate Limiting** — 100 requests/minute per IP  
✅ **CORS Protection** — Strict origin validation  
✅ **SQL Injection Prevention** — Parameterized queries via Hibernate  
✅ **XSS Protection** — CSP headers + input sanitization  
✅ **Captcha Integration** — Cloudflare Turnstile bot detection  

---

## 🤝 Contributing

We welcome contributions! Here's how to get started:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow Java code style (Google Style Guide)
- Write unit tests for new features
- Update documentation as needed
- Keep commit messages clear and descriptive

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👤 Developer Profile

<div align="center">

| Attribute | Details |
|-----------|---------|
| **Name** | Vineet Kumar |
| **Role** | Full-Stack Software Engineer & Backend Specialist |
| **Education** | B.Tech CSE, IEC College of Engineering & Technology |
| **Location** | Dadri, Uttar Pradesh, India 🇮🇳 |
| **Core Stack** | Java, Spring Boot, PostgreSQL, Docker, AWS, Next.js, Android |
| **LeetCode** | 300+ Problems Solved |
| **Certifications** | Postman API Student Expert |
| **Awards** | ABU Robocon India National Finalist (2025) |

**Long-term vision:** Scale CYPR Tech into India's leading B2C & B2B cybersecurity platform.

</div>

---

## 💬 Support & Contact

- 📧 **Email:** [support@cypr.tech](mailto:support@cypr.tech)
- 🐙 **GitHub:** [@iamvineetupadhyay](https://github.com/iamvineetupadhyay)
- 🔗 **LinkedIn:** [linkedin.com/in/iamvineetupadhyay](https://linkedin.com)
- 💼 **Portfolio:** [iamvineetupadhyay.com](https://iamvineetupadhyay.com)

---

<div align="center">

### **"Invisible Defense. Absolute Control."**

*Built with ❤️ and 🔒 for India's digital security.*

**[⬆ Back to Top](#-cypr-tech--enterprise-cybersecurity-platform)**

</div>
