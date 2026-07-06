# The VAJRA Threat Detection Engine (v5) — Deep Audit

The **VAJRA Engine (Various Attack Junction & Reconnaissance Algorithm)** is CYPR Tech's core heuristic analysis system. It performs real-time threat classification in under **100ms** locally, protecting user privacy by avoiding external cloud dependencies except for optional multi-AV checks.

---

## Technical Audit of the 16 Signals

### Signal 01: Shannon Entropy
- **Purpose**: Detects Random/Algorithmic Domain Generation (DGA) used by malware command-and-control (C2) servers.
- **Logic**: Calculates the mathematical entropy (bits per character) of the domain registrable label and subdomains:
  $$H(X) = -\sum_{i=1}^{n} P(x_i) \log_2 P(x_i)$$
- **Weight**: 30 (Critical threshold > 4.2 bits; High threshold > 3.8; Elevated > 3.4).
- **Advantages**: Extremely effective at flagging machine-generated random sequences (e.g. `cx89wsd12q.cc`).
- **Limitations**: High false-positives on short, random-looking valid domains or abbreviation strings.

### Signal 02: N-Gram Language Model
- **Purpose**: Evaluates English lexical structure vs gibberish.
- **Logic**: Compares bigram log-probabilities of the registrable label. Unseen bigrams are penalized with a weight of `-4.5` (raised from `-6.0` to minimize false-positives).
- **Weight**: 28 (Gibberish threshold < -5.5).
- **Advantages**: Filters out phonetic nonsense that matches common language structures.
- **Limitations**: Struggles with acronyms or foreign brand words that don't match standard English frequencies.

### Signal 03: Consonant-Vowel Ratio
- **Purpose**: Identifies machine-generated, unpronounceable domains.
- **Logic**: Checks the percentage of consonants in the domain label. Flags labels with > 72% consonants.
- **Weight**: 22 (Critical > 88% consonants).
- **Advantages**: Detects typical character-stuffed evasion domains (e.g. `brtghw-sec.in`).
- **Limitations**: Flags legitimate consonant-heavy abbreviations (e.g. `bndtc.com`).

### Signal 04: Levenshtein Typosquatting
- **Purpose**: Catches brand impersonation clones.
- **Logic**: Standardizes digits (`0 -> o`, `1 -> l`, etc.), collapses consecutive double characters, and calculates Levenshtein edit distance against registered brands (e.g., `paypal`, `google`, `zepto`). Flags edit distances $\le 2$.
- **Weight**: 40 (Edit-distance = 1); 28 (Edit-distance = 2).
- **Advantages**: Discovers direct target spoofing (e.g., `paypa1.com` or `g00gle.com`).
- **Limitations**: Concurrently registered similar names that are legitimate can trigger alerts.

### Signal 05: Combo-Squatting
- **Purpose**: Flags brand name combinations with high-risk words.
- **Logic**: Detects if a registered brand name is embedded within the label alongside other characters.
- **Weight**: 38 (if delimited by dash/underscore, e.g., `paypal-login.com`); 30 (if simply embedded).
- **Advantages**: Catches standard credential-harvesting setups.
- **Limitations**: Valid partner integrations (e.g., `brand-partner.com`) can be flagged.

### Signal 06: Unicode Homoglyphs
- **Purpose**: Identifies confusable lookalike characters.
- **Logic**: Normalizes Punycode strings (`xn--`) and maps cyrillic/greek lookalikes to Latin equivalents (e.g. cyrillic `а` to Latin `a`). Then runs Levenshtein against registered brands.
- **Weight**: 45 (Exact mapping to brand); 38 (Within 1 edit).
- **Advantages**: Neutralizes advanced internationalized domain name (IDN) spoofing.
- **Limitations**: Legitimate foreign language sites using native scripts might be flagged if they align with western brands.

### Signal 07: Subdomain Brand Abuse
- **Purpose**: Flags brand names injected as subdomains of untrusted parent hosts.
- **Logic**: Checks if a brand name is used as a prefix subdomain (e.g. `paypal.com.evil.xyz` or `accounts.paypal.evil.xyz`).
- **Weight**: 42 (Brand + com prefix); 38 (Exact brand label); 32 (Embedded brand in subdomain).
- **Advantages**: Catches phishing sites relying on the visual length of subdomains to hide the true host.
- **Limitations**: Legitimate multi-tenant SaaS providers hosting subdomains for brands can trigger alerts (resolved via whitelisting).

### Signal 08: URL Structural Signals
- **Purpose**: Evaluates suspicious URL anatomy.
- **Logic**: Checks URL length (> 100, 150, 200), presence of the `@` literal (used to bypass authentication domains), path depth (> 4 or 6 segments), parameter counts, double slashes (`//`) in path, and plaintext HTTP usage.
- **Weight**: Max 45 (Accumulated score).
- **Advantages**: General indicator of complex redirect or obfuscation payloads.
- **Limitations**: Long query strings on legitimate pages (e.g. UTM tracking) can accumulate score.

### Signal 09: Malicious Path Patterns
- **Purpose**: Scans path configurations for known exploit/phishing templates.
- **Logic**: Matches full path against 17 regex patterns (e.g., `/wp-login.php`, `/wp-admin`, `/phpmyadmin`, `/billing/update`, `/confirm-identity`).
- **Weight**: 35.
- **Advantages**: Blocks active scanning of standard administrative paths.
- **Limitations**: Administrative logins of legitimate sites will trigger alerts.

### Signal 10: High-Risk TLD
- **Purpose**: Scores domains based on historical abuse rates.
- **Logic**: Matches the TLD against a weighted list of high-abuse extensions (e.g., `.tk`, `.ml`, `.ga` at 35; `.xyz`, `.top`, `.click` at 30).
- **Weight**: 15 to 35.
- **Advantages**: Deters interaction with historically malicious registrars.
- **Limitations**: Popular low-cost TLDs like `.xyz` are frequently used for legitimate testing.

### Signal 11: IP-as-Host Detection
- **Purpose**: Identifies direct IP access that bypasses DNS filtering.
- **Logic**: Matches the host against IPv4 regex patterns.
- **Weight**: 38 (Public IP); 28 (Private IP range).
- **Advantages**: Flags malware command nodes that avoid domain registrations.
- **Limitations**: Accessing internal networks or router gateways triggers low/medium warnings.

### Signal 12: Scheme Abuse
- **Purpose**: Catches dangerous URI protocol schemes.
- **Logic**: Detects schemes like `javascript:`, `data:`, or `vbscript:` in target URLs.
- **Weight**: 50 (`javascript:` / `vbscript:`); 48 (`data:`).
- **Advantages**: Prevents cross-site scripting (XSS) or browser exploit payloads from firing on click.
- **Limitations**: Plain text display of scripts in technical articles can trigger alerts if formatted as links.

### Signal 13: Hex/Percent Encoding Obfuscation
- **Purpose**: Decodes double-encoded percent sequences.
- **Logic**: Checks for encoded dots (`%2e`), sashes (`%2f`), `@` symbol (`%40`), and double percent-encoding (`%25`), as well as total count of percent tokens.
- **Weight**: Max 40 (Accumulated score).
- **Advantages**: Flags attempts to bypass basic string-matching filters.
- **Limitations**: Heavily encoded valid search queries might trigger warnings.

### Signal 14: URL Shortener Masking
- **Purpose**: Discovers masked redirects when network is offline/unresolved.
- **Logic**: Triggers when a known shortener domain (e.g. `bit.ly`, `t.co`) is accessed but cannot be dynamically expanded by the HTTP HEAD resolver.
- **Weight**: 20.
- **Advantages**: Warns user that a redirection mask is in place.
- **Limitations**: Cannot determine the actual risk of the final link until network connection is resolved.

### Signal 15: Reversed Brand Names / RTL Override
- **Purpose**: Flags reversed brand registrations and Right-to-Left (RTL) character tricks.
- **Logic**: Evaluates if the registrable label is a brand spelled backwards (e.g., `otpez.in` for `zepto`). Also checks for the Unicode RTL override character `U+202E` (`\u202E`).
- **Weight**: 45 (RTL Override); 35 (Reversed brand).
- **Advantages**: Neutralizes visual text direction spoofing.
- **Limitations**: Reverse spelling might occasionally match standard vocabulary.

### Signal 16: Port Anomaly Detection
- **Purpose**: Flags active C2 listening ports and non-standard endpoints.
- **Logic**: Evaluates the destination port. Flags ports other than standard HTTP (80) and HTTPS (443).
- **Weight**: 25 (Known malicious/testing ports like 8080, 8443, 4444, 1337); 15 (Other non-standard ports).
- **Advantages**: Stops connections to active rogue servers or test endpoints.
- **Limitations**: Development web environments (like `localhost:3000`) naturally trigger port alerts.

---

## Architectural Core Features

### 1. Dynamic Thread-Safe Brand Registry
A singleton registry backed by a **`ConcurrentHashMap` set** holds active brands (such as Zepto, Blinkit, and local digital banks) to support sub-millisecond Levenshtein and combosquat matches. Administrators can reload or append brands at runtime:
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
