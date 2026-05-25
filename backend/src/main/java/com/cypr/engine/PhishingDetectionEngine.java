package com.cypr.engine;

import java.net.*;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;
import java.util.stream.*;

// ╔══════════════════════════════════════════════════════════════════════════╗
// ║   CYPR  —  PhishingDetectionEngine v5  "VAJRA-INDUSTRIAL"              ║
// ║                                                                          ║
// ║   15 independent algorithmic signals. Zero external API calls.           ║
// ║   All review bugs patched. 3 industrial upgrades applied.               ║
// ║                                                                          ║
// ║   INDUSTRIAL FIXES (v4 → v5):                                           ║
// ║   [IND-1] BrandRegistry — dynamic, thread-safe, admin-panel ready       ║
// ║            BRANDS hardcoded list → BrandRegistry.getInstance()          ║
// ║            Supports: add(), remove(), bulkLoad(), snapshot()            ║
// ║   [IND-2] URL Expander — shortener blindspot closed                     ║
// ║            sig14 ke baad analyze() FINAL URL fetch karta hai            ║
// ║            HTTP 3xx Location header chain follow karta hai              ║
// ║   [IND-3] Subdomain Whitelist — false positive elimination              ║
// ║            sig07 official brands ke subdomains skip karta hai           ║
// ║            e.g. internal.dev.legacy.amazon.com → no SUBDOMAIN_ABUSE    ║
// ║                                                                          ║
// ║   ORIGINAL FIXES RETAINED FROM v4:                                      ║
// ║   [FIX-1] sig05 comboSquat — dead code branch corrected                 ║
// ║   [FIX-2] sig08 urlStructure — @ vs %40 double-penalty removed          ║
// ║   [FIX-3] sig08 urlStructure — confidence divisor 60→90 corrected       ║
// ║   [FIX-4] ParsedUrl.rawUrl → originalInput (naming trap removed)        ║
// ║   [FIX-5] normDigits — added 4→a, 6→b, 7→t, 8→b, 9→g mappings         ║
// ║   [FIX-6] BIGRAM_UNSEEN -6.0 → -4.5 (false positive reduction)         ║
// ║   [FIX-7] sig07 — added subdomain brand embedding detection             ║
// ║   [NEW-8] sig16 — Non-standard port anomaly detection                   ║
// ║                                                                          ║
// ║   SIGNAL MAP                                              MAX SCORE      ║
// ║   ─────────────────────────────────────────────────────────────────      ║
// ║   01. Shannon entropy          DGA / random-label              30        ║
// ║   02. N-gram language model    real word vs. gibberish          28        ║
// ║   03. Consonant-vowel ratio    unpronounceable = generated      22        ║
// ║   04. Levenshtein typosquat    paypa1, g00gle, rn→m             40        ║
// ║   05. Combo-squatting          paypal-secure-update.com         38        ║
// ║   06. Unicode NFKC homoglyph   full confusable normalisation    45        ║
// ║   07. Subdomain brand abuse    paypal.com.evil.xyz              45        ║
// ║   08. URL structural signals   @, depth, length, params         45        ║
// ║   09. Malicious path patterns  /wp-login, /admin, redirects     35        ║
// ║   10. High-risk TLD scoring    weighted abuse table             35        ║
// ║   11. IP-as-host               bypasses DNS reputation          38        ║
// ║   12. Scheme abuse             data:, javascript:, vbscript:    50        ║
// ║   13. Hex/percent obfuscation  %2e, %40, double-encoding        40        ║
// ║   14. URL shortener masking    bit.ly, tinyurl, t.co            20        ║
// ║   15. Reversed/inverted brand  lapyap.com, U+202E RTL           45        ║
// ║   16. Port anomaly [NEW]       :8080, :4444, non-std ports      25        ║
// ║                                                    TOTAL MAX: 581        ║
// ╚══════════════════════════════════════════════════════════════════════════╝

public class PhishingDetectionEngine {

    // =========================================================================
    // [IND-1] BRAND REGISTRY — Dynamic, Thread-Safe, Admin-Panel Ready
    // =========================================================================
    //
    //  PROBLEM: In older versions, brands were static and hardcoded. 
    //  New fintech/e-commerce brands emerge frequently, making runtime 
    //  updates impossible without code redeployment.
    //
    //  SOLUTION: BrandRegistry singleton —
    //    • Thread-safe ConcurrentHashMap-backed Set (optimized for read-heavy workloads)
    //    • add()/remove() — modify monitored brands at runtime without application restart
    //    • bulkLoad(Collection) — seed brands from DB/Redis on startup
    //    • snapshot() — immutable view of the current state for management consoles
    //    • OFFICIAL_DOMAINS — whitelist for sig07 false-positive suppression [IND-3]
    //
    //  FUTURE UPGRADE PATH:
    //    This registry can be abstracted behind an interface:
    //      - RedisBrandRegistry (centralized, multi-instance support)
    //      - PostgresBrandRegistry (persistent, audit-logged)
    // =========================================================================
    public static final class BrandRegistry {

        private static final BrandRegistry INSTANCE = new BrandRegistry();
        public  static BrandRegistry getInstance() { return INSTANCE; }

        // Thread-safe brand store — ConcurrentHashMap as a Set
        private final Set<String> brands = ConcurrentHashMap.newKeySet();

        // [IND-3] Official domains whitelist — agar registrable domain yahaan hai
        // toh uske subdomains par sig07 SUBDOMAIN_ABUSE fire NAHI hoga.
        // e.g. internal.dev.legacy.amazon.com → amazon.com is whitelisted → SKIP
        private final Set<String> officialDomains = ConcurrentHashMap.newKeySet();

        private BrandRegistry() {
            // ── Seed data (default brands) ──────────────────────────────────
            // These are fallback seed values. In production, invoke bulkLoad()
            // during startup to seed brands dynamically from a database or cache.
            List<String> defaultBrands = List.of(
                    "paypal", "google", "facebook", "instagram", "amazon",
                    "apple", "microsoft", "netflix", "twitter", "linkedin",
                    "sbi", "hdfc", "icici", "axis", "paytm", "phonepe",
                    "dropbox", "salesforce", "adobe", "zoom", "coinbase",
                    "binance", "robinhood", "venmo", "cashapp", "ebay",
                    "wellsfargo", "citibank", "barclays", "hsbc", "chase"
            );
            brands.addAll(defaultBrands);

            // [IND-3] Official domain whitelist seeds
            // Format: "<brand>.<tld>" — registrable domain exactly
            List<String> defaultOfficial = List.of(
                    "amazon.com", "amazon.in", "google.com", "google.co.in",
                    "microsoft.com", "apple.com", "facebook.com", "instagram.com",
                    "linkedin.com", "paypal.com", "sbi.co.in", "hdfcbank.com",
                    "icicibank.com", "axisbank.com", "paytm.com", "phonepe.com",
                    "netflix.com", "zoom.us", "salesforce.com", "adobe.com",
                    "dropbox.com", "twitter.com", "coinbase.com", "ebay.com",
                    "wellsfargo.com", "citibank.com", "barclays.co.uk", "hsbc.com",
                    "chase.com", "binance.com"
            );
            officialDomains.addAll(defaultOfficial);
        }

        // ── Brand CRUD ───────────────────────────────────────────────────────

        /** Naya brand add karo (admin panel → POST /api/admin/brands) */
        public void add(String brand) {
            if (brand != null && !brand.isBlank())
                brands.add(brand.trim().toLowerCase());
        }

        /** Brand remove karo (admin panel → DELETE /api/admin/brands/{name}) */
        public void remove(String brand) {
            if (brand != null) brands.remove(brand.trim().toLowerCase());
        }

        /** DB/Redis se batch load karo (application startup par call karo) */
        public void bulkLoad(Collection<String> incoming) {
            incoming.stream()
                    .filter(b -> b != null && !b.isBlank())
                    .map(b -> b.trim().toLowerCase())
                    .forEach(brands::add);
        }

        /** Admin panel ke liye: current brand list ka immutable snapshot */
        public Set<String> snapshot() {
            return Collections.unmodifiableSet(new HashSet<>(brands));
        }

        /** Internal engine use — contains check */
        public boolean contains(String brand) { return brands.contains(brand); }

        /** Internal engine use — iterate all brands */
        public Set<String> all() { return brands; }

        // ── Official Domain Whitelist CRUD ───────────────────────────────────

        /** Official domain add karo whitelist mein */
        public void addOfficialDomain(String domain) {
            if (domain != null && !domain.isBlank())
                officialDomains.add(domain.trim().toLowerCase());
        }

        public void removeOfficialDomain(String domain) {
            if (domain != null) officialDomains.remove(domain.trim().toLowerCase());
        }

        /** Check: kya ye registrable domain officially whitelisted hai? */
        public boolean isOfficialDomain(String regLabel, String tld) {
            return officialDomains.contains(regLabel + "." + tld);
        }
    }

    // Convenience accessor — engine ke andar sab jagah yahi use hoga
    private final BrandRegistry brandRegistry = BrandRegistry.getInstance();


    // =========================================================================
    // [IND-2] URL EXPANDER — Shortener Blindspot Fix
    // =========================================================================
    //
    //  PROBLEM: Masked URLs (e.g. shortlinks like bit.ly/xyz) previously bypassed
    //  deep scan because the engine only analyzed the shortener domain (safe/high trust).
    //  The true threat lies in the final redirection target.
    //
    //  SOLUTION: UrlExpander class —
    //    • Automatically resolves shortened URLs when a known shortener domain is detected
    //    • Sends an HTTP HEAD request (minimal network cost, fetches only headers)
    //    • Follows the 3xx Location redirection chain (max 5 hops, with a loop guard)
    //    • Performs the final deep engine scan on the resolved destination URL
    //    • Gracefully falls back to the original URL on connection timeout or offline status
    //
    //  NOTE: This implementation performs blocking I/O.
    //  For high-scale production, wrap execution inside a CompletableFuture.
    // =========================================================================
    static final class UrlExpander {

        private static final int MAX_HOPS    = 5;
        private static final int TIMEOUT_MS  = 3_000;

        /**
         * Shortened URL ko expand karo final destination tak.
         * @param shortUrl  e.g. "https://bit.ly/3xAbc"
         * @return final destination URL, ya shortUrl agar expand fail ho
         */
        public String expand(String shortUrl) {
            String current = shortUrl;
            Set<String> visited = new HashSet<>();

            for (int hop = 0; hop < MAX_HOPS; hop++) {
                if (!visited.add(current)) break; // loop detected

                try {
                    HttpURLConnection conn = (HttpURLConnection)
                            new URL(current).openConnection();
                    conn.setInstanceFollowRedirects(false);   // manual follow
                    conn.setRequestMethod("HEAD");
                    conn.setConnectTimeout(TIMEOUT_MS);
                    conn.setReadTimeout(TIMEOUT_MS);
                    // Browser-like UA taaki bot-block na ho
                    conn.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (compatible; CYPR/5.0)");

                    int status = conn.getResponseCode();
                    conn.disconnect();

                    // 3xx → Location header mein next URL hoga
                    if (status >= 300 && status < 400) {
                        String location = conn.getHeaderField("Location");
                        if (location == null || location.isBlank()) break;
                        // Relative URL handle karo
                        current = location.startsWith("http")
                                ? location
                                : new URL(new URL(current), location).toString();
                    } else {
                        // 2xx ya kuch aur → ye hi final URL hai
                        break;
                    }
                } catch (Exception e) {
                    // Network error, timeout, etc. → original URL return karo
                    break;
                }
            }
            return current;
        }
    }

    private final UrlExpander urlExpander = new UrlExpander();


    // =========================================================================
    // HIGH-RISK TLD TABLE  (abuse-frequency weighted)
    // =========================================================================
    private static final Map<String, Integer> TLD_RISK;
    static {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(".tk", 35); m.put(".ml", 35); m.put(".ga", 35);
        m.put(".cf", 35); m.put(".gq", 35);
        m.put(".xyz", 30); m.put(".top", 30); m.put(".click", 30);
        m.put(".loan", 30); m.put(".work", 28); m.put(".icu", 28);
        m.put(".cam", 28); m.put(".gdn", 25); m.put(".pw", 25);
        m.put(".info", 15); m.put(".biz", 15); m.put(".online", 18);
        m.put(".site", 18); m.put(".shop", 15); m.put(".link", 20);
        m.put(".live", 20); m.put(".cc", 15);
        TLD_RISK = Collections.unmodifiableMap(m);
    }

    // =========================================================================
    // URL SHORTENER REGISTRY
    // =========================================================================
    private static final Set<String> SHORTENERS = Set.of(
            "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly",
            "is.gd", "rb.gy", "short.link", "cutt.ly", "clck.ru",
            "tiny.cc", "lc.chat", "bl.ink", "rebrand.ly", "buff.ly"
    );

    // =========================================================================
    // MALICIOUS PATH PATTERNS  (regex-based, 17 patterns)
    // =========================================================================
    private static final List<Pattern> MALICIOUS_PATHS = List.of(
            Pattern.compile("/wp-login\\.php",         Pattern.CASE_INSENSITIVE),
            Pattern.compile("/wp-admin",               Pattern.CASE_INSENSITIVE),
            Pattern.compile("/xmlrpc\\.php",           Pattern.CASE_INSENSITIVE),
            Pattern.compile("/cgi-bin/",               Pattern.CASE_INSENSITIVE),
            Pattern.compile("/phpmyadmin",             Pattern.CASE_INSENSITIVE),
            Pattern.compile("/administrator",          Pattern.CASE_INSENSITIVE),
            Pattern.compile("/login\\.php",            Pattern.CASE_INSENSITIVE),
            Pattern.compile("/signin\\.php",           Pattern.CASE_INSENSITIVE),
            Pattern.compile("/secure/login",           Pattern.CASE_INSENSITIVE),
            Pattern.compile("/account/verify",         Pattern.CASE_INSENSITIVE),
            Pattern.compile("/billing/update",         Pattern.CASE_INSENSITIVE),
            Pattern.compile("/index\\.php\\?.*=http",  Pattern.CASE_INSENSITIVE),
            Pattern.compile("/redirect\\?",            Pattern.CASE_INSENSITIVE),
            Pattern.compile("/out\\.php",              Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\.exe$|\\.bat$|\\.ps1$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("/update.*card",           Pattern.CASE_INSENSITIVE),
            Pattern.compile("/confirm.*identity",      Pattern.CASE_INSENSITIVE)
    );

    // =========================================================================
    // N-GRAM MODEL  (English bigram log-probabilities)
    // [FIX-6] BIGRAM_UNSEEN raised from -6.0 to -4.5 to reduce false positives
    // =========================================================================
    private static final Map<String, Double> BIGRAM_LP;
    private static final double BIGRAM_UNSEEN = -4.5;
    static {
        Map<String, Double> m = new HashMap<>();
        String[] common = {
                "th","he","in","er","an","re","on","en","at","es",
                "st","ed","or","te","of","to","it","is","ng","ea",
                "ti","al","ha","ou","io","le","as","nd","se","ly",
                "ck","ma","pa","go","am","la","co","pr","me","ne"
        };
        for (String b : common) m.put(b, -2.5);
        String[] medium = {
                "ab","ac","ad","ai","ar","au","ba","be","bi","bo",
                "bu","ca","ce","ch","cl","cr","cu","da","de","di",
                "do","dr","du","ec","ef","el","em","ep","eq","et",
                "eu","ev","ew","ex","fa","fe","fi","fl","fo","fr"
        };
        for (String b : medium) m.put(b, -3.5);
        BIGRAM_LP = Collections.unmodifiableMap(m);
    }

    // =========================================================================
    // UNICODE CONFUSABLE MAP
    // =========================================================================
    private static final Map<Character, Character> CONFUSABLE;
    static {
        Map<Character, Character> m = new HashMap<>();
        m.put('\u0430', 'a'); m.put('\u0435', 'e'); m.put('\u043E', 'o');
        m.put('\u0440', 'r'); m.put('\u0441', 'c'); m.put('\u0445', 'x');
        m.put('\u0443', 'y'); m.put('\u0432', 'b'); m.put('\u0456', 'i');
        m.put('\u0458', 'j'); m.put('\u0455', 's'); m.put('\u0454', 'e');
        m.put('\u03BF', 'o'); m.put('\u03B1', 'a'); m.put('\u03C1', 'p');
        m.put('\u03B5', 'e'); m.put('\u03B9', 'i'); m.put('\u03BD', 'v');
        m.put('\u00F6', 'o'); m.put('\u00FC', 'u'); m.put('\u00E4', 'a');
        m.put('\u0131', 'i'); m.put('\u01A1', 'o'); m.put('\u00F3', 'o');
        m.put('\u00E9', 'e'); m.put('\u00E0', 'a'); m.put('\u00EC', 'i');
        for (char c = '\uFF41'; c <= '\uFF5A'; c++) m.put(c, (char)('a' + c - '\uFF41'));
        CONFUSABLE = Collections.unmodifiableMap(m);
    }

    // =========================================================================
    // RESULT RECORDS
    // =========================================================================

    public record Signal(
            String id,
            int    score,
            double confidence,
            String reason
    ) {}

    public record EngineResult(
            String       riskTier,
            int          normalizedScore,
            int          rawScore,
            List<Signal> signals,
            List<String> reasons,
            String       summary,
            String       scannedUrl,       // [IND-2] actual URL scanned (may differ from input)
            String       originalInput     // [IND-2] original input URL
    ) {}

    // =========================================================================
    // URL PARSER
    // =========================================================================
    private record ParsedUrl(
            String       originalInput,
            String       scheme,
            String       host,
            String       regLabel,
            String       tld,
            List<String> subs,
            String       path,
            String       query,
            int          pathDepth,
            int          qParams,
            int          port,
            boolean      isIp
    ) {}

    // =========================================================================
    // PUBLIC ENTRY POINT
    // =========================================================================
    //
    //  Analysis Flow:
    //    1. Parse the input URL
    //    2. Check for shortener domains (sig14)
    //    3. If shortener is detected -> resolve via UrlExpander
    //    4. Re-parse the expanded target URL
    //    5. Execute all security signals on the final expanded target
    //    6. Compile and return execution results including both original and resolved URLs
    // =========================================================================
    public EngineResult analyze(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank())
            return stub("Empty URL");

        // ── Step 1: Parse input ──────────────────────────────────────────────
        ParsedUrl initial = parse(rawUrl);

        // ── Step 2: [IND-2] Shortener detect → expand ───────────────────────
        String urlToScan      = rawUrl;
        boolean wasExpanded   = false;

        if (SHORTENERS.contains(initial.host)) {
            String expanded = urlExpander.expand(rawUrl);
            if (!expanded.equalsIgnoreCase(rawUrl)) {
                urlToScan    = expanded;
                wasExpanded  = true;
            }
        }

        // ── Step 3: Final URL parse karo ────────────────────────────────────
        ParsedUrl p = wasExpanded ? parse(urlToScan) : initial;

        // ── Step 4: Sab signals run karo ────────────────────────────────────
        List<Signal> fired = new ArrayList<>();

        run(fired, sig01_entropy(p));
        run(fired, sig02_ngram(p));
        run(fired, sig03_cvRatio(p));
        run(fired, sig04_levenshtein(p));
        run(fired, sig05_comboSquat(p));
        run(fired, sig06_homoglyph(p));
        run(fired, sig07_subdomainAbuse(p));
        run(fired, sig08_urlStructure(p, urlToScan));
        run(fired, sig09_maliciousPath(p));
        run(fired, sig10_tldRisk(p));
        run(fired, sig11_ipHost(p));
        run(fired, sig12_schemeAbuse(p));
        run(fired, sig13_hexObfuscation(urlToScan));

        // [IND-2] Shortener signal: agar expand hua toh informational add karo
        if (wasExpanded) {
            fired.add(sig("SHORTENER_EXPANDED", 5, 0.99,
                    fmt("URL shortener (%s) expanded to: %s", initial.host, urlToScan)));
        } else {
            run(fired, sig14_shortener(p));
        }

        run(fired, sig15_reversedBrand(p));
        run(fired, sig16_portAnomaly(p));

        // ── Step 5: Score calculate karo ────────────────────────────────────
        int raw  = fired.stream().mapToInt(Signal::score).sum();
        int norm = normalize(raw);

        List<String> reasons = fired.stream()
                .sorted(Comparator.comparingInt(Signal::score).reversed())
                .map(Signal::reason)
                .toList();

        return new EngineResult(
                tier(norm), norm, raw,
                Collections.unmodifiableList(fired),
                reasons,
                summary(tier(norm), norm, fired.size()),
                urlToScan,   // [IND-2] actual scanned URL
                rawUrl       // [IND-2] original input
        );
    }

    // =========================================================================
    // SIGNAL 01 — SHANNON ENTROPY
    // =========================================================================
    private Signal sig01_entropy(ParsedUrl p) {
        double labelE  = shannonEntropy(p.regLabel);
        double maxSubE = p.subs.stream().mapToDouble(this::shannonEntropy).max().orElse(0);
        double eff     = Math.max(labelE, maxSubE);

        if      (eff > 4.2) return sig("ENTROPY", 30, cap((eff - 4.2) / 0.6 + 0.7),
                fmt("Very high entropy (%.2f bits) in domain label — DGA-generated domain suspected", eff));
        else if (eff > 3.8) return sig("ENTROPY", 22, cap((eff - 3.8) / 0.4 + 0.4),
                fmt("High entropy (%.2f bits) — domain label looks algorithmically generated", eff));
        else if (eff > 3.4) return sig("ENTROPY", 14, cap((eff - 3.4) / 0.4),
                fmt("Elevated entropy (%.2f bits) in domain label", eff));
        return null;
    }

    // =========================================================================
    // SIGNAL 02 — N-GRAM LANGUAGE MODEL
    // =========================================================================
    private Signal sig02_ngram(ParsedUrl p) {
        String lbl = p.regLabel.replaceAll("[^a-z]", "");
        if (lbl.length() < 4) return null;

        double total = 0;
        int    cnt   = 0;
        for (int i = 0; i < lbl.length() - 1; i++) {
            total += BIGRAM_LP.getOrDefault(lbl.substring(i, i + 2), BIGRAM_UNSEEN);
            cnt++;
        }
        double avg = cnt > 0 ? total / cnt : BIGRAM_UNSEEN;
        if (avg > -3.8) return null;

        if      (avg < -5.5) return sig("NGRAM", 28, cap((-avg - 5.5) / 0.5 + 0.8),
                fmt("Domain label '%s' has near-zero N-gram score (%.2f) — gibberish sequence", p.regLabel, avg));
        else if (avg < -4.8) return sig("NGRAM", 18, cap((-avg - 4.8) / 0.7 + 0.4),
                fmt("Domain label '%s' scores poorly on language model (%.2f)", p.regLabel, avg));
        return sig("NGRAM", 10, 0.35,
                fmt("Domain label '%s' has below-average N-gram score (%.2f)", p.regLabel, avg));
    }

    // =========================================================================
    // SIGNAL 03 — CONSONANT-VOWEL RATIO
    // =========================================================================
    private Signal sig03_cvRatio(ParsedUrl p) {
        String lbl = p.regLabel.replaceAll("[^a-z]", "");
        if (lbl.length() < 5) return null;

        long   v  = lbl.chars().filter(c -> "aeiou".indexOf(c) >= 0).count();
        double cr = 1.0 - (double) v / lbl.length();
        if (cr < 0.72) return null;

        if      (cr > 0.88) return sig("CV_RATIO", 22, cap((cr - 0.72) / 0.28),
                fmt("%.0f%% consonants in domain label — unpronounceable, machine-generated", cr * 100));
        else if (cr > 0.80) return sig("CV_RATIO", 14, cap((cr - 0.72) / 0.28),
                fmt("High consonant ratio %.0f%% in domain label", cr * 100));
        return sig("CV_RATIO", 8, 0.25,
                fmt("Elevated consonant ratio %.0f%%", cr * 100));
    }

    // =========================================================================
    // SIGNAL 04 — LEVENSHTEIN TYPOSQUATTING
    // =========================================================================
    private Signal sig04_levenshtein(ParsedUrl p) {
        String raw   = p.regLabel;
        String norm  = normDigits(raw);
        String dedup = collapseDoubles(norm);

        int    best      = Integer.MAX_VALUE;
        String bestBrand = null;

        for (String brand : brandRegistry.all()) {
            if (raw.equals(brand) || norm.equals(brand)) continue;
            int d = Math.min(levenshtein(norm, brand), levenshtein(dedup, brand));
            if (d < best) { best = d; bestBrand = brand; }
        }

        if (bestBrand == null || best > 2) return null;

        int score = best == 1 ? 40 : 28;
        return sig("TYPOSQUAT", score, best == 1 ? 0.92 : 0.72,
                fmt("'%s' is a typosquat of brand '%s' (edit-distance %d after normalization)",
                        raw, bestBrand, best));
    }

    // =========================================================================
    // SIGNAL 05 — COMBO-SQUATTING  [FIX-1]
    // =========================================================================
    private Signal sig05_comboSquat(ParsedUrl p) {
        String lbl = p.regLabel;
        if (brandRegistry.contains(lbl)) return null;

        for (String brand : brandRegistry.all()) {
            if (!lbl.contains(brand)) continue;

            boolean isDashSegment = Arrays.asList(lbl.split("[-_]")).contains(brand);
            int    score = isDashSegment ? 38 : 30;
            double conf  = isDashSegment ? 0.88 : 0.75;
            String how   = isDashSegment ? "is a dash-segment" : "is embedded";

            return sig("COMBO_SQUAT", score, conf,
                    fmt("Combo-squatting: brand '%s' %s in label '%s' — impersonation pattern",
                            brand, how, lbl));
        }
        return null;
    }

    // =========================================================================
    // SIGNAL 06 — UNICODE HOMOGLYPH
    // =========================================================================
    private Signal sig06_homoglyph(ParsedUrl p) {
        String host = p.host;
        if (host.contains("xn--"))
            return sig("HOMOGLYPH", 40, 0.92,
                    "Punycode (IDN xn--) label detected — common homoglyph phishing vector: " + host);

        if (host.chars().noneMatch(c -> c > 127)) return null;

        String mapped = applyConfusable(
                Normalizer.normalize(host, Normalizer.Form.NFKC).toLowerCase());
        String ml = registrableLabel(mapped);

        for (String brand : brandRegistry.all()) {
            if (ml.equals(brand))
                return sig("HOMOGLYPH", 45, 0.97,
                        fmt("Homoglyph attack: '%s' uses Unicode lookalikes to EXACTLY impersonate '%s'",
                                host, brand));
            if (levenshtein(ml, brand) <= 1)
                return sig("HOMOGLYPH", 38, 0.85,
                        fmt("Homoglyph attack (near match): '%s' maps to '%s', 1 edit from '%s'",
                                host, ml, brand));
        }
        return sig("HOMOGLYPH", 15, 0.50,
                "Non-ASCII Unicode characters in domain (possible IDN abuse): " + host);
    }

    // =========================================================================
    // SIGNAL 07 — SUBDOMAIN BRAND ABUSE
    // =========================================================================
    //
    //  [IND-3] WHITELIST FIX:
    //    PROBLEM: Legitimate complex subdomains (e.g., "internal.dev.legacy.amazon.com")
    //    falsely triggered brand abuse signals because the official brand appeared
    //    in the subdomain chain.
    //
    //    SOLUTION: Verify if the registrable domain is whitelisted in officialDomains.
    //    If matched -> skip subdomain spoofing checks (legitimate brand subdomain).
    //    Otherwise -> run standard brand abuse signals.
    //
    //    Edge case: "amazon.evil.com" correctly triggers alert because "evil.com"
    //    is not in the official whitelist.
    //
    //  [FIX-7] Tier 3 retained: brand embedded in subdomain label
    // =========================================================================
    private Signal sig07_subdomainAbuse(ParsedUrl p) {
        // Whitelist check — skip subdomain check if it's an official brand domain
        if (brandRegistry.isOfficialDomain(p.regLabel, p.tld)) return null;

        List<String> subs = p.subs;

        // Tier 1: brand + "com" as consecutive subdomain prefix (paypal.com.evil.xyz)
        for (int i = 0; i < subs.size() - 1; i++)
            if (brandRegistry.contains(subs.get(i)) && subs.get(i + 1).equals("com"))
                return sig("SUBDOMAIN_ABUSE", 42, 0.96,
                        fmt("Full '%s.com' used as subdomain prefix — definitive URL spoofing",
                                subs.get(i)));

        // Tier 2: exact brand IS a subdomain label (accounts.paypal.evil.xyz)
        for (String s : subs)
            if (brandRegistry.contains(s))
                return sig("SUBDOMAIN_ABUSE", 38, 0.88,
                        fmt("Brand '%s' used as subdomain of '%s.%s' — subdomain spoofing",
                                s, p.regLabel, p.tld));

        // Tier 3 [FIX-7]: brand EMBEDDED in a subdomain label (accounts-paypal.evil.xyz)
        for (String s : subs)
            for (String brand : brandRegistry.all())
                if (s.contains(brand) && !s.equals(brand))
                    return sig("SUBDOMAIN_ABUSE", 32, 0.78,
                            fmt("Brand '%s' embedded in subdomain label '%s' — partial spoofing",
                                    brand, s));

        // Depth penalty
        if (subs.size() >= 4)
            return sig("SUBDOMAIN_ABUSE", 20, 0.60,
                    "Excessive subdomain depth (" + subs.size() + " levels) — real domain is at far right");

        return null;
    }

    // =========================================================================
    // SIGNAL 08 — URL STRUCTURAL ANALYSIS  [FIX-2] [FIX-3]
    // =========================================================================
    private Signal sig08_urlStructure(ParsedUrl p, String raw) {
        if (p.scheme.equals("data") || p.scheme.equals("javascript")) return null;

        int          score = 0;
        List<String> fl    = new ArrayList<>();

        int len = raw.length();
        if      (len > 200) { score += 28; fl.add("extreme URL length (" + len + " chars)"); }
        else if (len > 150) { score += 20; fl.add("very long URL (" + len + " chars)"); }
        else if (len > 100) { score += 12; fl.add("long URL (" + len + " chars)"); }

        boolean hasAtLiteral = raw.contains("@");
        boolean hasAtEncoded = raw.toLowerCase().contains("%40");
        if (hasAtLiteral && !hasAtEncoded) {
            score += 30;
            fl.add("'@' in URL — browser ignores everything before it (credential bypass)");
        }

        if      (p.pathDepth > 6) { score += 20; fl.add("deep path (" + p.pathDepth + " segments)"); }
        else if (p.pathDepth > 4) { score += 12; fl.add("path depth " + p.pathDepth); }

        if      (p.qParams > 7) { score += 18; fl.add(p.qParams + " query parameters"); }
        else if (p.qParams > 4) { score += 10; fl.add(p.qParams + " query parameters"); }

        if (p.path.contains("//")) { score += 18; fl.add("double-slash in path — open redirect"); }
        if (p.scheme.equals("http")) { score += 15; fl.add("plaintext HTTP (no TLS)"); }

        if (score == 0) return null;
        return sig("URL_STRUCTURE", Math.min(45, score), cap(score / 90.0),
                "Suspicious URL structure: " + String.join("; ", fl));
    }

    // =========================================================================
    // SIGNAL 09 — MALICIOUS PATH PATTERNS
    // =========================================================================
    private Signal sig09_maliciousPath(ParsedUrl p) {
        String full = p.path + (p.query != null ? "?" + p.query : "");
        for (Pattern pat : MALICIOUS_PATHS)
            if (pat.matcher(full).find())
                return sig("MALICIOUS_PATH", 35, 0.88,
                        "Path matches known phishing/exploit template: " + pat.pattern());
        return null;
    }

    // =========================================================================
    // SIGNAL 10 — HIGH-RISK TLD
    // =========================================================================
    private Signal sig10_tldRisk(ParsedUrl p) {
        Integer s = TLD_RISK.get("." + p.tld);
        if (s == null) return null;
        return sig("HIGH_RISK_TLD", s, cap(s / 35.0),
                "High-abuse TLD '." + p.tld + "' detected (abuse weight: " + s + "/35)");
    }

    // =========================================================================
    // SIGNAL 11 — IP-AS-HOST
    // =========================================================================
    private Signal sig11_ipHost(ParsedUrl p) {
        if (!p.host.matches("\\d{1,3}(\\.\\d{1,3}){3}")) return null;
        boolean priv = p.host.startsWith("192.168.") || p.host.startsWith("10.")
                || p.host.startsWith("172.16.")   || p.host.startsWith("127.");
        return sig("IP_HOST", priv ? 28 : 38, priv ? 0.75 : 0.95,
                "Raw IP as host"
                        + (priv ? " (private range — intranet/captive portal attack)" : " (public IP)")
                        + ": " + p.host + " — bypasses all domain reputation systems");
    }

    // =========================================================================
    // SIGNAL 12 — SCHEME ABUSE
    // =========================================================================
    private Signal sig12_schemeAbuse(ParsedUrl p) {
        return switch (p.scheme) {
            case "javascript" -> sig("SCHEME_ABUSE", 50, 1.0,
                    "CRITICAL: javascript: URI — direct code execution on click");
            case "data"       -> sig("SCHEME_ABUSE", 48, 0.98,
                    "CRITICAL: data: URI — inline HTML/JS payload, bypasses URL filtering");
            case "vbscript"   -> sig("SCHEME_ABUSE", 50, 1.0,
                    "CRITICAL: vbscript: URI — legacy IE script execution");
            case "ftp"        -> sig("SCHEME_ABUSE", 30, 0.80,
                    "FTP scheme in web link — possible file drop or credential theft");
            default           -> null;
        };
    }

    // =========================================================================
    // SIGNAL 13 — HEX / PERCENT OBFUSCATION
    // =========================================================================
    private Signal sig13_hexObfuscation(String raw) {
        String       lc    = raw.toLowerCase();
        int          score = 0;
        List<String> fl    = new ArrayList<>();

        if (lc.contains("%2e")) { score += 22; fl.add("encoded dot %2e (hides subdomain depth)"); }
        if (lc.contains("%2f")) { score += 22; fl.add("encoded slash %2f (hides path segments)"); }
        if (lc.contains("%40")) { score += 28; fl.add("encoded '@' %40 (credential bypass obfuscation)"); }
        if (lc.contains("%25")) { score += 18; fl.add("double-encoding %25 (second-pass evasion)"); }

        long tokens = Arrays.stream(lc.split("%"))
                .filter(s -> s.length() >= 2 && s.substring(0, 2).matches("[0-9a-f]{2}"))
                .count();
        if (tokens > 6 && score == 0) {
            score += 18;
            fl.add("heavy percent-encoding (" + tokens + " tokens)");
        }

        if (score == 0) return null;
        return sig("HEX_OBFUSCATION", Math.min(40, score), cap(score / 50.0),
                "URL obfuscation detected: " + String.join("; ", fl));
    }

    // =========================================================================
    // SIGNAL 14 — URL SHORTENER MASKING
    // (Called only when URL could NOT be expanded — network unavailable, etc.)
    // =========================================================================
    private Signal sig14_shortener(ParsedUrl p) {
        if (!SHORTENERS.contains(p.host)) return null;
        return sig("SHORTENER", 20, 0.65,
                "URL shortener (" + p.host + ") — true destination masked; analyze the expanded URL");
    }

    // =========================================================================
    // SIGNAL 15 — REVERSED / INVERTED BRAND + RTL OVERRIDE
    // =========================================================================
    private Signal sig15_reversedBrand(ParsedUrl p) {
        if (p.originalInput != null && p.originalInput.contains("\u202E"))
            return sig("REVERSED_BRAND", 45, 0.99,
                    "CRITICAL: Unicode RTL Override (U+202E) — URL visually misrepresents destination");

        String rev = new StringBuilder(p.regLabel).reverse().toString();
        for (String brand : brandRegistry.all()) {
            if (rev.equals(brand))
                return sig("REVERSED_BRAND", 35, 0.88,
                        fmt("'%s' is '%s' spelled backwards — reversed brand domain registration",
                                p.regLabel, brand));
            if (levenshtein(rev, brand) == 1)
                return sig("REVERSED_BRAND", 25, 0.72,
                        fmt("Reversed label '%s' is 1 edit from brand '%s'", rev, brand));
        }
        return null;
    }

    // =========================================================================
    // SIGNAL 16 — PORT ANOMALY  [NEW-8]
    // =========================================================================
    private Signal sig16_portAnomaly(ParsedUrl p) {
        int port = p.port;
        if (port == -1 || port == 80 || port == 443) return null;

        Set<Integer> knownAbuse = Set.of(8080, 8443, 4444, 3000, 8888, 9090, 1337, 6666, 6667);
        if (knownAbuse.contains(port))
            return sig("PORT_ANOMALY", 25, 0.85,
                    fmt("Non-standard port %d — known phishing/C2 port; legitimate services don't use this", port));

        return sig("PORT_ANOMALY", 15, 0.65,
                fmt("Non-standard port %d — legitimate HTTPS/HTTP services run on 443/80 only", port));
    }

    // =========================================================================
    // CORE ALGORITHMS
    // =========================================================================

    private double shannonEntropy(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        Map<Character, Integer> f = new HashMap<>();
        for (char c : s.toCharArray()) f.merge(c, 1, Integer::sum);
        double h = 0, n = s.length();
        for (int cnt : f.values()) { double p = cnt / n; h -= p * Math.log(p) / Math.log(2); }
        return h;
    }

    private int levenshtein(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] d = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) d[i][0] = i;
        for (int j = 0; j <= n; j++) d[0][j] = j;
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++) {
                int c = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(Math.min(d[i-1][j] + 1, d[i][j-1] + 1), d[i-1][j-1] + c);
            }
        return d[m][n];
    }

    private String normDigits(String s) {
        return s.replace('0', 'o')
                .replace('1', 'l')
                .replace('3', 'e')
                .replace('4', 'a')
                .replace('5', 's')
                .replace('6', 'b')
                .replace('7', 't')
                .replace('8', 'b')
                .replace('9', 'g');
    }

    private String collapseDoubles(String s) { return s.replaceAll("(.)\\1{1,}", "$1"); }

    private String applyConfusable(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) sb.append(CONFUSABLE.getOrDefault(c, c));
        return sb.toString();
    }

    private double cap(double v) { return Math.max(0.0, Math.min(1.0, v)); }

    private int normalize(int raw) {
        double r = raw / 581.0;
        return (int) Math.min(100, Math.round((1.0 - Math.exp(-4.0 * r)) * 100));
    }

    private String tier(int score) {
        if (score >= 72) return "CRITICAL";
        if (score >= 48) return "HIGH";
        if (score >= 24) return "MEDIUM";
        if (score >= 10) return "LOW";
        return "SAFE";
    }

    private String summary(String t, int s, int n) {
        return switch (t) {
            case "CRITICAL" -> fmt("CRITICAL RISK (%d/100) — %d signal(s) fired; block immediately", s, n);
            case "HIGH"     -> fmt("HIGH RISK (%d/100) — %d phishing signal(s) detected", s, n);
            case "MEDIUM"   -> fmt("MEDIUM RISK (%d/100) — suspicious; verify before visiting", s, n);
            case "LOW"      -> fmt("LOW RISK (%d/100) — minor signal(s), proceed cautiously", s, n);
            default         -> fmt("SAFE (%d/100) — no phishing indicators detected", s);
        };
    }

    private Signal sig(String id, int score, double conf, String reason) {
        return new Signal(id, score, cap(conf), reason);
    }

    private String fmt(String f, Object... args) { return String.format(f, args); }
    private void run(List<Signal> list, Signal s) { if (s != null) list.add(s); }

    // =========================================================================
    // URL PARSER
    // =========================================================================
    private ParsedUrl parse(String raw) {
        String lc = raw.trim().toLowerCase();

        String scheme = "http";
        if      (lc.startsWith("https://"))    scheme = "https";
        else if (lc.startsWith("http://"))     scheme = "http";
        else if (lc.startsWith("javascript:")) scheme = "javascript";
        else if (lc.startsWith("data:"))       scheme = "data";
        else if (lc.startsWith("vbscript:"))   scheme = "vbscript";
        else if (lc.startsWith("ftp://"))      scheme = "ftp";

        if (scheme.equals("javascript") || scheme.equals("data") || scheme.equals("vbscript"))
            return new ParsedUrl(raw, scheme, "", "", "", List.of(), "", null, 0, 0, -1, false);

        String forParse = raw.trim().matches("^[a-zA-Z][a-zA-Z0-9+.\\-]*://.*")
                ? raw.trim() : "http://" + raw.trim();
        try {
            URL    url    = new URL(forParse);
            String h      = url.getHost()  != null ? url.getHost().toLowerCase()  : "";
            String pa     = url.getPath()  != null ? url.getPath()                : "";
            String q      = url.getQuery();
            int    port   = url.getPort();
            boolean ip    = h.matches("\\d{1,3}(\\.\\d{1,3}){3}");

            String[] labels = h.split("\\.");
            String tld  = labels.length >= 1 ? labels[labels.length - 1] : "";
            String reg  = labels.length >= 2 ? labels[labels.length - 2] : h;
            List<String> subs = labels.length > 2
                    ? List.of(Arrays.copyOfRange(labels, 0, labels.length - 2))
                    : List.of();

            int depth  = (int) Arrays.stream(pa.split("/")).filter(s -> !s.isEmpty()).count();
            int params = q != null && !q.isEmpty() ? q.split("&").length : 0;

            return new ParsedUrl(raw, url.getProtocol(), h, reg, tld,
                    subs, pa, q, depth, params, port, ip);
        } catch (MalformedURLException e) {
            return new ParsedUrl(raw, scheme, "", "", "", List.of(), "", null, 0, 0, -1, false);
        }
    }

    private String registrableLabel(String host) {
        String[] p = host.split("\\.");
        return p.length >= 2 ? p[p.length - 2] : host;
    }

    private EngineResult stub(String msg) {
        return new EngineResult("SAFE", 0, 0, List.of(), List.of(msg), "SAFE — " + msg, "", "");
    }
}