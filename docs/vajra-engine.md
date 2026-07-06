# The VAJRA Threat Detection Engine (v5) — CYPR Tech

The **VAJRA Engine (Various Attack Junction & Reconnaissance Algorithm)** is CYPR Tech's core heuristic analysis system. It is designed to perform threat classification in under **100ms** locally, protecting user privacy by reducing dependency on external cloud lookups.

## Algorithmic Detection Signals (16 Vectors)

| Signal | Vector | Analysis Target | Max Score |
|---|---|---|---|
| **01** | Shannon Entropy | Detects Random/Algorithmic Domain Generation (DGA) | 30 |
| **02** | N-Gram Model | Evaluates English lexical structure vs gibberish | 28 |
| **03** | Consonant-Vowel Ratio | Identifies machine-generated, unpronounceable domains | 22 |
| **04** | Levenshtein Distance | Finds typosquatting clones (e.g. `paypa1`, `g00gle`) | 40 |
| **05** | Combo-Squatting | Flags brand name combinations with high-risk words | 38 |
| **06** | Unicode Homoglyphs | Identifies confusable lookalike characters | 45 |
| **07** | Subdomain Abuse | Flags fake brand redirects (e.g. `zepto.com.evil.in`) | 45 |
| **08** | URL Structure | Evaluates parameter strings, `@` bypass patterns, etc. | 45 |
| **09** | Malicious Paths | Scans path configurations for keywords (e.g., `wp-login`) | 35 |
| **10** | High-Risk TLD | Scores domains based on historical abuse rates | 35 |
| **11** | IP-as-Host | Identifies direct IP access that bypasses DNS filtering | 38 |
| **12** | Scheme Abuse | Catches JavaScript injection and data scheme payloads | 50 |
| **13** | Hex Obfuscation | Decodes double-encoded percent sequences | 40 |
| **14** | URL Shortener | Discovers masked redirects (e.g. `bit.ly`, `t.co`) | 20 |
| **15** | Reversed Brand | Flags reversed brand registrations (e.g. `otpez.in`) | 45 |
| **16** | Port Anomaly | Flags active C2 listening ports (e.g. `8080`, `4444`) | 25 |

---

## Architectural Core Features

### 1. Dynamic Thread-Safe Brand Registry
A singleton registry backed by a **`ConcurrentHashMap` set** holds active brands (such as Zepto, Blinkit, and local digital banks) to support sub-millisecond Levenshtein and typosquat matches. Administrators can reload or append brands at runtime:
```bash
POST /api/admin/brands
{
  "brand": "Blinkit",
  "domains": ["blinkit.com", "www.blinkit.com"],
  "action": "ADD"
}
```

### 2. Redirection Resolve Chain (UrlExpander)
Hiding payloads behind redirects is resolved by sending a non-blocking **HTTP HEAD request**. The engine manual-resolves status codes (301, 302, 307) recursively up to **5 hops** with loop protection, ensuring the final target URL is scanned.

### 3. Smart Subdomain Whitelisting
To prevent false alarms in complex domains (e.g. `internal-dev.support.microsoft.com`), the engine resolves the registrable domain. If the registrable domain matches an officially registered brand domain, it immediately bypasses subdomain branding checks.
