(function () {
  // Immediate theme initialization to prevent content flash
  const savedTheme = localStorage.getItem('cm_theme') || 'light';
  document.documentElement.setAttribute('data-theme', savedTheme);

  const API_BASE = window.CYBERMITRA_API_BASE || 'http://localhost:8080';

  // Inject Refined Premium Styles
  const style = document.createElement('style');
  style.textContent = `
    @import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@900&display=swap');

    /* ── GLOBAL THEME VARIABLE OVERRIDES ── */
    html[data-theme="light"] {
      --logo-fill: #0B192C; /* Deep Royal Blue / Dark Indigo */
      --logo-glow-color: rgba(11, 25, 44, 0.15);
      --bg: #ffffff;
      --bg2: #f8fafc;
      --bg3: #f1f5f9;
      --bg4: #e2e8f0;
      --surface: #ffffff;
      --surface2: #f8fafc;
      --surface3: #f1f5f9;
      --card: #ffffff;
      --card2: #f8fafc;
      --border: #e2e8f0;
      --border2: #cbd5e1;
      --border-bright: #cbd5e1;
      --accent: #1e40af; /* Sleek corporate blue */
      --accent2: #3b82f6;
      --accent3: #ef4444;
      --accent-dim: rgba(30, 64, 175, 0.08);
      --lime: #1e40af;
      --lime2: #3b82f6;
      --lime-dim: rgba(30, 64, 175, 0.08);
      --lime-glow: rgba(30, 64, 175, 0.15);
      --green: #10b981;
      --green-dim: rgba(16, 185, 129, 0.12);
      --red: #ef4444;
      --red-dim: rgba(239, 68, 68, 0.10);
      --amber: #f59e0b;
      --amber-dim: rgba(245, 158, 11, 0.08);
      --warn: #f59e0b;
      --warn-dim: rgba(245, 158, 11, 0.08);
      --blue: #3b82f6;
      --text: #0f172a; /* Slate 900 */
      --text2: #475569; /* Slate 600 */
      --text3: #94a3b8; /* Slate 400 */
      --text-dim: #4b5563;
      --text-muted: #6b7280;
      --shadow: rgba(0, 0, 0, 0.05);
      --shadow-lg: 0 10px 30px rgba(0, 0, 0, 0.08);
      --nav-bg: rgba(255, 255, 255, 0.88);
    }
    html[data-theme="dark"] {
      --logo-fill: #00FF66; /* Exact bright Neon Green */
      --logo-glow-color: rgba(0, 255, 102, 0.4);
      --bg: #0a0a0a;

      --bg2: #0f0f0f;
      --bg3: #141414;
      --bg4: #1a1a1a;
      --surface: #111111;
      --surface2: #161616;
      --surface3: #1a1a1a;
      --card: #111111;
      --card2: #161616;
      --border: #1f1f1f;
      --border2: #2a2a2a;
      --border-bright: #3a3a3a;
      --accent: #c8ff00; /* Neon cyberpunk lime */
      --accent2: #aaff00;
      --accent3: #ff4444;
      --accent-dim: rgba(200, 255, 0, 0.10);
      --lime: #c8ff00;
      --lime2: #aaff00;
      --lime-dim: rgba(200, 255, 0, 0.10);
      --lime-glow: rgba(200, 255, 0, 0.25);
      --green: #c8ff00;
      --green-dim: rgba(200, 255, 0, 0.10);
      --red: #ff4444;
      --red-dim: rgba(255, 68, 68, 0.12);
      --amber: #ffaa00;
      --amber-dim: rgba(255, 170, 0, 0.12);
      --warn: #ffaa00;
      --warn-dim: rgba(255, 170, 0, 0.12);
      --blue: #64b4ff;
      --text: #f2f2f2;
      --text2: #888888;
      --text3: #555555;
      --text-dim: #888888;
      --text-muted: #555555;
      --shadow: rgba(0, 0, 0, 0.5);
      --shadow-lg: 0 10px 30px rgba(0, 0, 0, 0.5);
      --nav-bg: rgba(10, 10, 10, 0.88);
    }

    body {
      background: var(--bg) !important;
      color: var(--text) !important;
    }

    /* Global smooth theme transition */
    body, .sidebar, .topbar, .card, .glass-card, .btn, .input-field, .st-chart-icon, .t7-item, .stat-tile, .cm-theme-toggle {
      transition: background-color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  border-color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  color 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  box-shadow 0.25s cubic-bezier(0.4, 0, 0.2, 1),
                  transform 0.2s ease !important;
    }

    /* Core Layout Elements Overrides */
    .topbar {
      background: var(--bg) !important;
      border-bottom: 1px solid var(--border) !important;
    }

    .sb-logo-icon svg {
      color: var(--bg) !important;
    }

    .sb-item.active {
      color: var(--lime) !important;
      background: var(--lime-dim) !important;
      border-color: var(--lime-glow) !important;
    }
    .sb-item.active svg, .sb-item.active span {
      color: var(--lime) !important;
    }

    /* Form & inputs premium styling */
    .input-field:focus,
    .fg input:focus,
    .fg textarea:focus,
    .fg select:focus,
    .search-input:focus {
      border-color: var(--lime) !important;
      box-shadow: 0 0 0 3px var(--lime-glow) !important;
      background: var(--bg) !important;
    }

    /* Dynamic line accents top of cards */
    .glass-card::before,
    .card::before,
    .info-panel::before,
    .terminal::before,
    .testi-card::before {
      background: linear-gradient(90deg, transparent, var(--border-bright), transparent) !important;
    }

    .navbar::after {
      background: linear-gradient(90deg, transparent, var(--lime-glow), transparent) !important;
    }

    /* Speedometer & Gauge svg overrides */
    .score-card svg circle:first-of-type {
      stroke: var(--border) !important;
    }
    .score-card svg circle#ringArc {
      transition: stroke 0.8s ease, stroke-dashoffset 1.6s cubic-bezier(.4, 0, .2, 1), filter 0.8s ease !important;
    }

    /* Logs list styling */
    .log-dot.lime {
      background: var(--lime-dim) !important;
      border-color: var(--lime-glow) !important;
    }
    .log-dot.lime svg {
      color: var(--lime) !important;
    }
    .log-dot.red {
      background: var(--red-dim) !important;
      border-color: rgba(239, 68, 68, 0.25) !important;
    }
    .log-dot.red svg {
      color: var(--red) !important;
    }
    .log-dot.amber {
      background: var(--amber-dim) !important;
      border-color: rgba(245, 158, 11, 0.25) !important;
    }
    .log-dot.amber svg {
      color: var(--amber) !important;
    }
    .log-dot.blue {
      background: var(--blue-dim) !important;
      border-color: rgba(59, 130, 246, 0.25) !important;
    }
    .log-dot.blue svg {
      color: var(--blue) !important;
    }
    .log-tag.safe {
      color: var(--bg) !important;
      background: var(--lime) !important;
    }
    .log-tag.danger {
      color: #ffffff !important;
      background: var(--red) !important;
    }
    .log-tag.warning {
      color: #ffffff !important;
      background: var(--amber) !important;
    }
    .log-tag.info {
      color: #ffffff !important;
      background: var(--blue) !important;
    }

    /* ── URL CHECK PAGE SCANNER ALIGNMENTS ── */
    .scan-toggle-wrapper {
      background: var(--bg3) !important;
      border: 1px solid var(--border) !important;
    }
    .scan-toggle-title {
      color: var(--text) !important;
    }
    .scan-toggle-sub {
      color: var(--text3) !important;
    }
    .scan-slider {
      background: var(--bg4) !important;
      border-color: var(--border2) !important;
    }
    .scan-slider::before {
      background: var(--text3) !important;
    }
    .scan-toggle-switch input:checked + .scan-slider {
      background: var(--lime) !important;
      border-color: var(--lime) !important;
      box-shadow: 0 0 10px var(--lime-glow) !important;
    }
    .scan-toggle-switch input:checked + .scan-slider::before {
      background: var(--bg) !important;
    }
    .btn-scan {
      background: var(--lime) !important;
      color: var(--bg) !important;
      box-shadow: 0 4px 12px var(--lime-glow) !important;
    }
    .btn-scan:hover {
      background: var(--lime2) !important;
      color: var(--bg) !important;
      box-shadow: 0 8px 20px var(--lime-glow) !important;
    }
    .badge-safe {
      color: var(--bg) !important;
      background: var(--lime) !important;
    }

    /* Buttons global dynamic support */
    .btn-primary,
    .btn-accent,
    .btn-submit,
    .nav-pill {
      background: var(--lime) !important;
      color: var(--bg) !important;
      box-shadow: 0 4px 12px var(--lime-glow) !important;
    }
    .btn-primary:hover,
    .btn-accent:hover,
    .btn-submit:hover,
    .nav-pill:hover {
      background: var(--lime2) !important;
      box-shadow: 0 8px 20px var(--lime-glow) !important;
      color: var(--bg) !important;
    }
    .btn-ghost {
      color: var(--lime) !important;
      border-color: var(--border2) !important;
      background: transparent !important;
    }
    .btn-ghost:hover {
      border-color: var(--lime) !important;
      background: var(--lime-dim) !important;
      color: var(--lime2) !important;
    }

    /* Header text highlights landing pages */
    .hero h1 span,
    .hero h1 .line3 {
      background: linear-gradient(135deg, var(--lime), var(--lime2)) !important;
      -webkit-background-clip: text !important;
      -webkit-text-fill-color: transparent !important;
      background-clip: text !important;
    }

    /* User Profile avatar global supports */
    .tb-avatar, .id-avatar, .modal-av {
      background: linear-gradient(135deg, var(--lime), var(--lime2)) !important;
      border-color: var(--lime-glow) !important;
      color: var(--bg) !important;
    }

    .cm-logo-slot{width:34px;height:34px;display:inline-grid;place-items:center;flex-shrink:0;border-radius:9px;border:1px solid var(--lime-glow) !important;background:var(--lime-dim) !important;overflow:hidden}
    .cm-logo-slot img{width:100%;height:100%;object-fit:contain;padding:4px}
    .cm-logo-slot span{color:var(--lime) !important;font:800 .68rem 'JetBrains Mono',monospace;letter-spacing:.04em}
    .nav-logo::before, .logo::before, .auth-brand::before { display: none !important; }

    /* ── REFINED GLASS NAVBAR ── */
    .navbar {
      display: flex !important;
      justify-content: space-between !important;
      align-items: center !important;
      padding: 0 40px !important;
      background: var(--bg) !important;
      border-bottom: 1px solid var(--border) !important;
      box-shadow: var(--shadow-lg) !important;
      position: sticky !important;
      top: 0 !important;
      z-index: 999 !important;
      height: 64px !important;
      width: 100% !important;
      box-sizing: border-box !important;
    }
    
    .cm-nav-container {
      display: flex;
      width: 100%;
      align-items: center;
      justify-content: space-between;
      gap: 20px;
    }

    .cm-nav-logo {
      display: flex;
      align-items: center;
      gap: 10px;
      font-weight: 800;
      font-size: 1.15rem;
      color: var(--text);
    }
    .cm-nav-logo span {
      color: var(--lime);
    }
    
    .cm-nav-links {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-left: auto;
    }
    .cm-nav-links a {
      color: var(--text2);
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.7rem;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      padding: 8px 16px;
      border-radius: 999px;
      border: 1px solid transparent;
      transition: all 0.2s ease;
    }
    .cm-nav-links a:hover, .cm-nav-links a.active {
      color: var(--lime) !important;
      border-color: var(--border2) !important;
      background: var(--bg3) !important;
    }
    
    .cm-nav-actions {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-left: 20px;
    }
    
    /* Premium Interactive Elements */
    .cm-promo-pill {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 14px;
      background: var(--lime-dim) !important;
      border: 1px solid var(--lime-glow) !important;
      border-radius: 999px;
      color: var(--lime) !important;
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.65rem;
      font-weight: 700;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      box-shadow: 0 0 15px var(--lime-glow) !important;
      transition: all 0.25s ease;
      cursor: pointer;
      animation: cm-pulse-glow 2.5s infinite;
      text-decoration: none !important;
    }
    .cm-promo-pill:hover {
      transform: translateY(-1px);
      box-shadow: 0 0 25px var(--lime-glow) !important;
      border-color: var(--lime) !important;
    }
    
    .cm-btn-accent {
      background: var(--lime) !important;
      color: var(--bg) !important;
      border-radius: 999px !important;
      font-family: 'JetBrains Mono', monospace !important;
      font-size: 0.68rem !important;
      font-weight: 700 !important;
      letter-spacing: 0.06em !important;
      text-transform: uppercase !important;
      padding: 8px 18px !important;
      transition: all 0.2s ease !important;
      display: inline-flex !important;
      align-items: center !important;
      justify-content: center !important;
      border: none !important;
      cursor: pointer !important;
      text-decoration: none !important;
    }
    .cm-btn-accent:hover {
      background: var(--lime2) !important;
      box-shadow: 0 0 20px var(--lime-glow) !important;
      transform: translateY(-1px) !important;
    }
    
    .cm-btn-ghost {
      color: var(--text2) !important;
      border: 1px solid var(--border2) !important;
      background: var(--bg) !important;
      border-radius: 999px !important;
      font-family: 'JetBrains Mono', monospace !important;
      font-size: 0.68rem !important;
      font-weight: 700 !important;
      letter-spacing: 0.06em !important;
      text-transform: uppercase !important;
      padding: 8px 18px !important;
      transition: all 0.2s ease !important;
      display: inline-flex !important;
      align-items: center !important;
      justify-content: center !important;
      cursor: pointer !important;
      text-decoration: none !important;
    }
    .cm-btn-ghost:hover {
      border-color: var(--lime) !important;
      background: var(--bg3) !important;
      color: var(--lime) !important;
    }
    
    .cm-credit-pill {
      display: inline-flex;
      align-items: center;
      gap: 7px;
      min-height: 34px;
      padding: 7px 14px;
      border: 1px solid var(--lime-glow) !important;
      border-radius: 999px;
      background: var(--lime-dim) !important;
      color: var(--lime) !important;
      font: 700 .68rem 'JetBrains Mono',monospace;
      letter-spacing: .06em;
      text-transform: uppercase;
      white-space: nowrap;
      cursor: pointer;
      box-shadow: 0 0 15px var(--lime-glow) !important;
      transition: all 0.2s ease;
      text-decoration: none !important;
    }
    .cm-credit-pill::before {
      content: '';
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--lime);
      box-shadow: 0 0 10px var(--lime-glow);
    }
    .cm-credit-pill:hover {
      border-color: var(--lime) !important;
      box-shadow: 0 0 20px var(--lime-glow) !important;
      background: var(--lime-dim) !important;
    }
    .cm-credit-pill.low {
      border-color: rgba(255, 68, 68, 0.3) !important;
      color: #ff4444 !important;
      background: rgba(255, 68, 68, 0.08) !important;
    }
    .cm-credit-pill.low::before {
      background: #ff4444 !important;
      box-shadow: 0 0 10px rgba(255, 68, 68, 0.6) !important;
    }

    /* ── USER MENU & DROPDOWN ── */
    .cm-user-menu {
      position: relative;
      display: inline-block;
    }
    .cm-user-trigger {
      cursor: pointer;
      display: flex;
      align-items: center;
    }
    .cm-avatar-small {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      background: linear-gradient(135deg, var(--lime), var(--lime2)) !important;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 0.8rem;
      color: var(--bg) !important;
      border: 1.5px solid var(--lime-glow) !important;
      transition: all 0.2s ease;
    }
    .cm-avatar-small:hover {
      transform: scale(1.05);
      box-shadow: 0 0 12px var(--lime-glow);
    }
    .cm-dropdown-menu {
      position: absolute;
      top: calc(100% + 10px);
      right: 0;
      width: 220px;
      background: var(--bg2) !important;
      border: 1px solid var(--border) !important;
      border-radius: 12px;
      padding: 8px;
      box-shadow: var(--shadow-lg) !important;
      display: none;
      flex-direction: column;
      gap: 4px;
      z-index: 9999;
      transform: translateY(-10px);
      opacity: 0;
      transition: transform 0.2s ease, opacity 0.2s ease;
    }
    .cm-dropdown-menu.open {
      display: flex;
      transform: translateY(0);
      opacity: 1;
    }
    .cm-dropdown-header {
      padding: 10px 12px;
      border-bottom: 1px solid var(--border) !important;
      margin-bottom: 4px;
    }
    .cm-dropdown-header .name {
      font-weight: 700;
      font-size: 0.85rem;
      color: var(--text) !important;
      display: block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .cm-dropdown-header .email {
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.65rem;
      color: var(--text3) !important;
      display: block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-top: 2px;
    }
    .cm-dropdown-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 12px;
      border-radius: 8px;
      font-size: 0.8rem;
      color: var(--text2) !important;
      transition: all 0.15s;
      cursor: pointer;
      text-decoration: none !important;
    }
    .cm-dropdown-item:hover {
      background: var(--bg3) !important;
      color: var(--lime) !important;
    }
    .cm-dropdown-divider {
      height: 1px;
      background: var(--border) !important;
      margin: 4px 0;
    }
    .cm-dropdown-item.logout {
      color: #ff5555 !important;
    }
    .cm-dropdown-item.logout:hover {
      background: rgba(255, 85, 85, 0.08) !important;
      color: #ff5555 !important;
    }

    /* ── HAMBURGER & MOBILE MENU ── */
    .cm-hamburger-btn {
      display: none;
      flex-direction: column;
      gap: 5px;
      width: 36px;
      height: 36px;
      background: var(--bg) !important;
      border: 1px solid var(--border) !important;
      border-radius: 8px;
      padding: 9px;
      cursor: pointer;
      justify-content: center;
    }
    .cm-hamburger-btn span {
      display: block;
      width: 100%;
      height: 2px;
      background: var(--text2) !important;
      border-radius: 2px;
      transition: all 0.25s ease;
    }
    .cm-hamburger-btn.open span:nth-child(1) {
      transform: translateY(7px) rotate(45deg);
    }
    .cm-hamburger-btn.open span:nth-child(2) {
      opacity: 0;
    }
    .cm-hamburger-btn.open span:nth-child(3) {
      transform: translateY(-7px) rotate(-45deg);
    }

    /* Mobile Drawer */
    .cm-mobile-drawer {
      position: fixed;
      top: 64px;
      left: 0;
      right: 0;
      height: calc(100vh - 64px);
      background: var(--bg2) !important;
      backdrop-filter: blur(20px);
      border-top: 1px solid var(--border) !important;
      z-index: 998;
      padding: 24px;
      display: none;
      flex-direction: column;
      gap: 20px;
      opacity: 0;
      transform: translateY(-10px);
      transition: all 0.3s ease;
    }
    .cm-mobile-drawer.open {
      display: flex;
      opacity: 1;
      transform: translateY(0);
    }
    .cm-mobile-links {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .cm-mobile-links a {
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      color: var(--text2) !important;
      padding: 12px 16px;
      border-radius: 8px;
      border: 1px solid transparent;
      transition: all 0.2s;
      text-decoration: none !important;
    }
    .cm-mobile-links a:hover, .cm-mobile-links a.active {
      color: var(--lime) !important;
      background: var(--lime-dim) !important;
      border-color: var(--lime-glow) !important;
    }
    .cm-mobile-actions {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-top: auto;
      padding-bottom: 40px;
      border-top: 1px solid var(--border) !important;
      padding-top: 20px;
    }

    /* ── SIDEBAR PROFILE CARD ── */
    .cm-sidebar-profile {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 14px;
      margin: 14px;
      background: var(--bg) !important;
      border: 1px solid var(--border) !important;
      border-radius: 12px;
      margin-top: auto;
      transition: all 0.2s ease;
    }
    .cm-sidebar-profile:hover {
      border-color: var(--lime-glow) !important;
      background: var(--lime-dim) !important;
    }
    .cm-sidebar-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, var(--lime), var(--lime2)) !important;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 0.85rem;
      color: var(--bg) !important;
      border: 1px solid var(--lime-glow) !important;
      flex-shrink: 0;
      overflow: hidden;
    }
    .cm-sidebar-avatar img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .cm-sidebar-info {
      display: flex;
      flex-direction: column;
      min-width: 0;
    }
    .cm-sidebar-name {
      font-size: 0.8rem;
      font-weight: 700;
      color: var(--text) !important;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .cm-sidebar-score {
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.65rem;
      color: var(--text3) !important;
      margin-top: 2px;
    }
    .cm-sidebar-score span {
      color: var(--green) !important;
      font-weight: 700;
    }

    /* ── AI BUDDY OVERRIDES & LAUNCHER ── */
    .cm-ai-launch{position:fixed;right:22px;bottom:22px;z-index:700;display:inline-flex;align-items:center;gap:9px;min-height:46px;padding:12px 16px;border:1px solid var(--lime-glow) !important;border-radius:999px;background:var(--lime) !important;color:var(--bg) !important;font:800 .78rem 'JetBrains Mono',monospace;letter-spacing:.06em;text-transform:uppercase;box-shadow:0 10px 30px var(--lime-glow);cursor:pointer}
    .cm-ai-launch:hover{background:var(--lime2) !important;box-shadow:0 12px 35px var(--lime-glow)}
    .cm-ai-panel{position:fixed;right:22px;bottom:82px;z-index:701;width:min(420px,calc(100vw - 28px));display:none;overflow:hidden;border:1px solid var(--border) !important;border-radius:16px;background:var(--bg) !important;box-shadow:0 15px 45px rgba(0,0,0,.15)}
    .cm-ai-panel.open{display:block}
    .cm-ai-head{display:flex;align-items:center;justify-content:space-between;gap:12px;padding:15px 16px;border-bottom:1px solid var(--border) !important;background:var(--bg2) !important}
    .cm-ai-head strong{display:block;color:var(--text) !important}
    .cm-ai-head span{display:block;margin-top:2px;color:var(--text3) !important;font:600 .62rem 'JetBrains Mono',monospace;letter-spacing:.08em;text-transform:uppercase}
    .cm-ai-close{width:34px;height:34px;border:1px solid var(--border) !important;border-radius:999px;background:var(--bg) !important;color:var(--text) !important;cursor:pointer}
    .cm-ai-body{display:grid;gap:12px;padding:14px}
    .cm-ai-log{display:grid;gap:10px;max-height:310px;overflow:auto}
    .cm-ai-msg{padding:10px 12px;border:1px solid var(--border) !important;border-radius:12px;background:var(--bg2) !important;color:var(--text2) !important;font:.88rem 'Outfit',sans-serif;line-height:1.55;white-space:pre-wrap}
    .cm-ai-msg.user{justify-self:end;max-width:88%;border-color:var(--lime-glow) !important;background:var(--lime-dim) !important;color:var(--lime) !important}
    .cm-ai-form{display:flex;gap:8px}
    .cm-ai-form input{flex:1;min-width:0;min-height:42px;padding:11px 12px;border:1px solid var(--border) !important;border-radius:12px;background:var(--bg) !important;color:var(--text) !important;font:.88rem 'Outfit',sans-serif;outline:none}
    .cm-ai-form input:focus{border-color:var(--lime) !important;box-shadow:0 0 0 3px var(--lime-glow)}
    .cm-ai-form button{min-height:42px;padding:10px 14px;border:0;border-radius:999px;background:var(--lime) !important;color:var(--bg) !important;font:800 .7rem 'JetBrains Mono',monospace;letter-spacing:.06em;text-transform:uppercase;cursor:pointer}

    /* ── DUAL THEME TOGGLE SYSTEM ── */
    .cm-theme-toggle {
      width: 34px;
      height: 34px;
      border-radius: 50%;
      border: 1px solid var(--border) !important;
      background: var(--bg2) !important;
      color: var(--text2) !important;
      display: inline-grid !important;
      place-items: center;
      cursor: pointer;
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      flex-shrink: 0;
      padding: 0 !important;
      box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
    .cm-theme-toggle:hover {
      color: var(--lime) !important;
      border-color: var(--lime) !important;
      background: var(--lime-dim) !important;
      transform: scale(1.05);
      box-shadow: 0 0 12px var(--lime-glow);
    }
    .cm-theme-toggle svg {
      width: 16px;
      height: 16px;
      transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s;
      position: absolute;
      stroke: currentColor;
    }
    html[data-theme="light"] .cm-theme-toggle .sun-icon {
      opacity: 1;
      transform: rotate(0) scale(1);
    }
    html[data-theme="light"] .cm-theme-toggle .moon-icon {
      opacity: 0;
      transform: rotate(90deg) scale(0);
    }
    html[data-theme="dark"] .cm-theme-toggle .sun-icon {
      opacity: 0;
      transform: rotate(-90deg) scale(0);
    }
    html[data-theme="dark"] .cm-theme-toggle .moon-icon {
      opacity: 1;
      transform: rotate(0) scale(1);
    }

    @keyframes cm-pulse-glow {
      0%, 100% { box-shadow: 0 0 10px var(--lime-glow); }
      50% { box-shadow: 0 0 20px var(--lime-glow); }
    }

    @media(max-width:900px){
      .cm-hamburger-btn{display:flex}
      .cm-nav-links{display:none}
      .cm-nav-actions{display:none}
      .cm-ai-launch{right:14px;bottom:14px}
      .cm-ai-panel{right:14px;bottom:72px}
      .cm-credit-pill{display:none}
    }

    /* ── DASHBOARD SHELL & SIDEBAR FOR WRAPPED PUBLIC PAGES ── */
    .shell {
      display: flex;
      min-height: 100vh;
    }
    .sidebar {
      width: 210px;
      flex-shrink: 0;
      background: var(--bg2);
      border-right: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      position: fixed;
      top: 0;
      left: 0;
      bottom: 0;
      z-index: 200;
      transition: transform .3s ease;
    }
    .sb-logo {
      padding: 20px 20px 18px;
      display: flex;
      align-items: center;
      gap: 9px;
      border-bottom: 1px solid var(--border);
      text-decoration: none;
    }
    .sb-logo-icon {
      width: 30px;
      height: 30px;
      background: var(--lime);
      border-radius: 7px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }
    .sb-logo-text {
      font-size: 1.05rem;
      font-weight: 800;
      letter-spacing: .02em;
      color: var(--text);
    }
    .sb-nav {
      flex: 1;
      padding: 14px 10px;
      display: flex;
      flex-direction: column;
      gap: 2px;
      overflow-y: auto;
    }
    .sb-item {
      display: flex;
      align-items: center;
      gap: 11px;
      padding: 10px 12px;
      border-radius: 8px;
      font-size: .875rem;
      font-weight: 500;
      color: var(--text3);
      transition: all .18s;
      border: 1px solid transparent;
      text-decoration: none;
    }
    .sb-item svg {
      width: 17px;
      height: 17px;
      flex-shrink: 0;
    }
    .sb-item:hover {
      color: var(--text2);
      background: var(--bg3);
    }
    .sb-item.active {
      color: var(--lime) !important;
      background: var(--lime-dim) !important;
      border-color: var(--lime-glow) !important;
    }
    .sb-item.active svg, .sb-item.active span {
      color: var(--lime) !important;
    }
    .sb-bottom {
      padding: 10px;
      border-top: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      gap: 2px;
    }
    .sb-logout {
      display: flex;
      align-items: center;
      gap: 11px;
      padding: 10px 12px;
      border-radius: 8px;
      font-size: .875rem;
      font-weight: 500;
      color: var(--text3);
      transition: all .18s;
      text-decoration: none;
      cursor: pointer;
    }
    .sb-logout:hover {
      color: var(--red);
      background: var(--red-dim);
    }
    .sb-logout svg {
      width: 17px;
      height: 17px;
    }
    .sb-overlay {
      display: none;
      position: fixed;
      inset: 0;
      background: rgba(0,0,0,.7);
      z-index: 199;
      pointer-events: none !important;
    }
    .sb-overlay.open {
      display: block;
      pointer-events: auto !important;
    }
    .main-area {
      flex: 1;
      margin-left: 210px;
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }
    .topbar {
      height: 60px;
      border-bottom: 1px solid var(--border);
      display: flex !important;
      align-items: center !important;
      justify-content: space-between !important;
      padding: 0 28px;
      position: sticky;
      top: 0;
      z-index: 100;
      box-sizing: border-box;
      background: rgba(10, 10, 10, .85);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
    }
    html[data-theme="light"] .topbar {
      background: rgba(255, 255, 255, .85);
    }
    .tb-left {
      display: flex !important;
      align-items: center !important;
      gap: 14px;
    }
    .tb-hamburger {
      display: none;
      flex-direction: column;
      gap: 4px;
      width: 32px;
      height: 32px;
      background: var(--bg3);
      border: 1px solid var(--border);
      border-radius: 7px;
      padding: 8px;
      cursor: pointer;
      box-sizing: border-box;
    }
    .tb-hamburger span {
      display: block;
      width: 100%;
      height: 2px;
      background: var(--text2);
      border-radius: 2px;
    }
    .tb-right {
      display: flex !important;
      align-items: center !important;
      gap: 12px;
    }
    .tb-user {
      display: flex !important;
      align-items: center !important;
      gap: 10px;
      padding: 5px 5px 5px 12px;
      background: var(--bg3);
      border: 1px solid var(--border);
      border-radius: 100px;
      cursor: pointer;
      position: relative;
      transition: border-color .18s;
      box-sizing: border-box;
    }
    .tb-user:hover {
      border-color: var(--border2);
    }
    .tb-user-info {
      text-align: right;
    }
    .tb-user-name {
      font-size: .82rem;
      font-weight: 600;
      line-height: 1.2;
    }
    .tb-user-plan {
      font-family: var(--mono);
      font-size: .6rem;
      color: var(--text3);
    }
    .tb-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: linear-gradient(135deg, var(--lime), #44ff88);
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: .88rem;
      color: #0a0a0a;
      overflow: hidden;
      flex-shrink: 0;
      border: 1.5px solid rgba(200, 255, 0, .3);
    }
    .tb-avatar img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .page {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: calc(100vh - 60px);
    }
    .page-content {
      flex: 1;
      padding: 28px 28px 48px;
      overflow-y: auto;
      box-sizing: border-box;
    }
    
    /* ── MOBILE RESPONSIVENESS OVERRIDES ── */
    @media(max-width:900px) {
      .main-area {
        margin-left: 0 !important;
      }
      .sidebar {
        transform: translateX(-100%);
      }
      .sidebar.open {
        transform: translateX(0);
      }
      .tb-hamburger {
        display: flex !important;
      }
      .tb-search {
        display: none !important;
      }
    }

    /* ── BREADCRUMB & GREET ── */
    .tb-breadcrumb {
      display: flex;
      align-items: center;
      gap: 7px;
      font-size: .82rem;
      color: var(--text3);
      font-family: var(--mono);
    }
    .tb-breadcrumb .sep {
      color: var(--border2);
    }
    .tb-breadcrumb .current {
      color: var(--text2);
    }
    .tb-greet {
      font-size: .95rem;
      font-weight: 700;
      white-space: nowrap;
    }
    .tb-greet span {
      color: var(--lime);
    }
    .tb-date {
      font-family: var(--mono);
      font-size: .6rem;
      color: var(--text3);
      margin-top: 1px;
    }

    /* ── SPOTLIGHT SEARCH RESULTS PANEL ── */
    .tb-search {
      position: relative;
    }
    .tb-search-results {
      position: absolute;
      top: calc(100% + 8px);
      left: 0;
      right: 0;
      background: var(--bg2) !important;
      border: 1px solid var(--border) !important;
      border-radius: 12px;
      box-shadow: var(--shadow-lg) !important;
      max-height: 380px;
      overflow-y: auto;
      z-index: 9999;
      display: none;
      flex-direction: column;
      padding: 8px;
      gap: 4px;
    }
    .tb-search-results.open {
      display: flex;
    }
    .tb-search-group {
      font-family: var(--mono);
      font-size: 0.6rem;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      color: var(--text3);
      padding: 6px 10px 2px;
      text-align: left;
    }
    .tb-search-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 12px;
      border-radius: 8px;
      font-size: 0.8rem;
      color: var(--text2) !important;
      cursor: pointer;
      transition: all 0.15s;
      text-align: left;
      text-decoration: none !important;
    }
    .tb-search-item:hover, .tb-search-item.selected {
      background: var(--bg3) !important;
      color: var(--lime) !important;
    }
    .tb-search-item .title {
      font-weight: 600;
    }
    .tb-search-item .desc {
      font-size: 0.72rem;
      color: var(--text3);
      margin-left: 8px;
    }
    .tb-search-item .badge {
      font-family: var(--mono);
      font-size: 0.58rem;
      padding: 2px 6px;
      border-radius: 4px;
      background: var(--lime-dim);
      color: var(--lime);
      border: 1px solid var(--lime-glow);
    }
    .tb-search-empty {
      padding: 12px;
      text-align: center;
      font-size: 0.8rem;
      color: var(--text3);
    }

    /* ── SETTINGS PAGE TAB SWITCHER ── */
    .cm-settings-tabs {
      display: flex;
      gap: 8px;
      margin-bottom: 24px;
      border-bottom: 1px solid var(--border);
      padding-bottom: 12px;
    }
    .cm-settings-tab-btn {
      padding: 10px 20px;
      border-radius: 8px;
      font-family: var(--mono);
      font-size: 0.78rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--text2);
      border: 1px solid transparent;
      transition: all 0.2s;
    }
    .cm-settings-tab-btn:hover {
      color: var(--text);
      background: var(--bg3);
    }
    .cm-settings-tab-btn.active {
      color: var(--lime) !important;
      background: var(--lime-dim) !important;
      border-color: var(--lime-glow) !important;
    }
    
    /* Avatar selection grid */
    .avatar-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(68px, 1fr));
      gap: 12px;
      margin-top: 10px;
    }
    .avatar-option {
      aspect-ratio: 1;
      border-radius: 12px;
      border: 2px solid var(--border);
      background: var(--bg3);
      cursor: pointer;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s ease;
      padding: 4px;
    }
    .avatar-option:hover {
      transform: scale(1.05);
      border-color: var(--border2);
    }
    .avatar-option.selected {
      border-color: var(--lime) !important;
      background: var(--lime-dim) !important;
      box-shadow: 0 0 12px var(--lime-glow);
    }
    .avatar-option svg, .avatar-option img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }

    /* Pinned tools badges */
    .pin-badge {
      cursor: pointer;
      font-size: 1rem;
      transition: transform 0.15s;
    }
    .pin-badge:hover {
      transform: scale(1.2);
    }
  `;
  document.head.appendChild(style);

  // Toggle Theme Action with global persistent storage support
  function toggleThemeAction() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('cm_theme', newTheme);
    showToast(newTheme === 'dark' ? '🌙 Cyberpunk Dark Mode activated!' : '☀️ Premium Corporate Light Mode activated!');
  }


  // Helper: Read session details
  function readUserId() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id') || localStorage.getItem('userId') || localStorage.getItem('cm_user_id');
  }

  function creditValue(profile) {
    return profile?.credits ?? profile?.dailyCredits ?? profile?.remainingCredits ?? profile?.creditBalance ?? profile?.creditsLeft;
  }

  // Check if link is active
  function isActive(page) {
    return window.location.pathname.endsWith(page) ? 'active' : '';
  }

  // Handle Logout
  function handleSignOut(e) {
    if (e) e.preventDefault();
    // Clear all user session keys to prevent stale data on next login
    ['userId','cm_user_id','userName','userEmail',
     'cm_user_name','cm_user_email','cm_user_initials','cm_user_avatar',
     'cm_user_credits','cm_user_subscription','cm_user_score','cm_notifications'
    ].forEach(k => localStorage.removeItem(k));
    sessionStorage.clear();
    showToast("🔒 Logged out securely. Stay safe!");
    setTimeout(() => {
      window.location.href = 'index.html?logout=true';
    }, 1000);
  }

  // Brand logo helper (preserves authentication page branding layout)
  function enhanceLogo() {
    // 1. Rebrand page titles
    if (document.title && document.title.includes('CyberMitra')) {
      document.title = document.title.replace(/CyberMitra/g, 'CYPR');
    }

    // 2. Rebrand text copy in body
    const replaceText = (el) => {
      if (el.nodeType === Node.TEXT_NODE) {
        if (el.nodeValue.includes('CyberMitra')) {
          el.nodeValue = el.nodeValue.replace(/CyberMitra/g, 'CYPR');
        }
      } else {
        // Skip script and style tags
        if (el.tagName !== 'SCRIPT' && el.tagName !== 'STYLE') {
          el.childNodes.forEach(replaceText);
        }
      }
    };
    
    const mainArea = document.querySelector('.main-area') || document.body;
    if (mainArea) {
      replaceText(mainArea);
    }

    // 3. Define the premium, futuristic reactive SVG logo component for "CYPR"
    const logoSvg = `
      <svg class="cypr-logo" viewBox="0 0 100 32" xmlns="http://www.w3.org/2000/svg" style="height: 28px; width: auto; overflow: visible; display: inline-block; vertical-align: middle;">
        <!-- Futuristic Geometric Icon Prefix -->
        <polygon points="12,4 4,16 12,28 20,16" fill="var(--logo-fill)" style="transition: fill 0.25s ease; filter: drop-shadow(0 0 3px var(--logo-glow-color));" />
        <polygon points="12,9 7,16 12,23 17,16" fill="var(--bg)" style="transition: fill 0.25s ease;" />
        <!-- CYPR Futuristic Text -->
        <text x="28" y="22" font-family="'Orbitron', 'Montserrat', sans-serif" font-weight="900" font-size="16" letter-spacing="1" fill="var(--logo-fill)" style="transition: fill 0.25s ease; filter: drop-shadow(0 0 4px var(--logo-glow-color));">CYPR</text>
      </svg>
    `;

    // 4. Inject logo SVG into all branding classes
    const logoElements = document.querySelectorAll('.nav-logo, .logo, .auth-brand, .sb-logo, .footer-logo, .cm-nav-logo');
    logoElements.forEach((el) => {
      el.innerHTML = logoSvg;
      
      // Clean up classes/structure that are obsolete
      el.className = el.className.replace(/\b(has-logo)\b/g, '');
      if (el.tagName === 'A') {
        el.href = readUserId() ? 'home.html' : 'index.html';
      }
    });
  }

  // Dynamic public header implementation
  async function enhancePublicHeader() {
    const navbar = document.querySelector('.navbar');
    if (!navbar || document.querySelector('.cm-nav-container')) return;

    // Build perfect structural container
    const navContainer = document.createElement('div');
    navContainer.className = 'cm-nav-container';

    // Logo Block
    const logoLink = document.createElement('a');
    logoLink.href = readUserId() ? 'home.html' : 'index.html';
    logoLink.className = 'cm-nav-logo';
    logoLink.innerHTML = `
      <span class="cm-logo-slot">
        <img src="assets/logo.png" alt="CM" onerror="const s=document.createElement('span');s.textContent='CM';this.replaceWith(s);">
      </span>
      Cyber<span>Mitra</span>
    `;
    navContainer.appendChild(logoLink);

    // Links Block
    const linksDiv = document.createElement('div');
    linksDiv.className = 'cm-nav-links';
    linksDiv.innerHTML = `
      <a href="tools.html" class="${isActive('tools.html')}">Tools</a>
      <a href="pricing.html" class="${isActive('pricing.html')}">Pricing</a>
      <a href="cyber-news.html" class="${isActive('cyber-news.html')}">Cyber News</a>
      <a href="aboutus.html" class="${isActive('aboutus.html')}">About Us</a>
      <a href="contactus.html" class="${isActive('contactus.html')}">Contact</a>
    `;
    navContainer.appendChild(linksDiv);

    // Actions Block
    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'cm-nav-actions';
    navContainer.appendChild(actionsDiv);

    // Hamburger Menu Button
    const hamBtn = document.createElement('button');
    hamBtn.className = 'cm-hamburger-btn';
    hamBtn.setAttribute('aria-label', 'Toggle Navigation');
    hamBtn.innerHTML = '<span></span><span></span><span></span>';
    navContainer.appendChild(hamBtn);

    // Clear and swap
    navbar.innerHTML = '';
    navbar.appendChild(navContainer);

    // Build Mobile Drawer
    let drawer = document.querySelector('.cm-mobile-drawer');
    if (!drawer) {
      drawer = document.createElement('div');
      drawer.className = 'cm-mobile-drawer';
      document.body.appendChild(drawer);
    }
    
    drawer.innerHTML = `
      <div class="cm-mobile-links">
        <a href="tools.html" class="${isActive('tools.html')}">Tools</a>
        <a href="pricing.html" class="${isActive('pricing.html')}">Pricing</a>
        <a href="cyber-news.html" class="${isActive('cyber-news.html')}">Cyber News</a>
        <a href="aboutus.html" class="${isActive('aboutus.html')}">About Us</a>
        <a href="contactus.html" class="${isActive('contactus.html')}">Contact</a>
      </div>
      <div class="cm-mobile-actions"></div>
    `;

    const mobActions = drawer.querySelector('.cm-mobile-actions');

    // Toggle event
    hamBtn.addEventListener('click', () => {
      hamBtn.classList.toggle('open');
      drawer.classList.toggle('open');
    });

    // Close menu when clicking outside
    document.addEventListener('click', (e) => {
      if (!navbar.contains(e.target) && !drawer.contains(e.target)) {
        hamBtn.classList.remove('open');
        drawer.classList.remove('open');
      }
    });

    const userId = readUserId();
    if (!userId) {
      // Guest state
      actionsDiv.innerHTML = `
        <a href="signup.html" class="cm-promo-pill">🎁 50 Free Credits</a>
        <a href="login.html" class="cm-btn-ghost">Sign In</a>
        <a href="signup.html" class="cm-btn-accent">Get Started</a>
      `;
      mobActions.innerHTML = `
        <a href="signup.html" class="cm-promo-pill" style="justify-content:center">🎁 Get 50 Free Credits</a>
        <a href="login.html" class="cm-btn-ghost" style="width:100%">Sign In</a>
        <a href="signup.html" class="cm-btn-accent" style="width:100%">Get Started</a>
      `;
    } else {
      // Logged in state
      actionsDiv.innerHTML = `
        <span class="cm-credit-pill">Credits: ...</span>
        <a href="dashboard.html" class="cm-btn-accent">Dashboard</a>
        <div class="cm-user-menu">
          <div class="cm-user-trigger">
            <div class="cm-avatar-small">?</div>
          </div>
          <div class="cm-dropdown-menu">
            <div class="cm-dropdown-header">
              <span class="name">Loading Profile...</span>
              <span class="email">...</span>
            </div>
            <a href="dashboard.html" class="cm-dropdown-item">📊 Dashboard</a>
            <a href="settings.html" class="cm-dropdown-item">⚙️ Settings</a>
            <div class="cm-dropdown-divider"></div>
            <div class="cm-dropdown-item logout">🔒 Sign Out</div>
          </div>
        </div>
      `;

      mobActions.innerHTML = `
        <div style="display:flex;align-items:center;justify-content:space-between;padding:12px;background:rgba(255,255,255,0.02);border-radius:100px;border:1px solid rgba(255,255,255,0.06);margin-bottom:8px;">
          <span class="cm-credit-pill" style="min-height:30px">Credits: ...</span>
          <span style="font-family:'JetBrains Mono',monospace;font-size:0.65rem;color:var(--text-muted);text-transform:uppercase;margin-right:8px;">Logged In</span>
        </div>
        <a href="dashboard.html" class="cm-btn-accent" style="width:100%">Dashboard</a>
        <a href="settings.html" class="cm-btn-ghost" style="width:100%">Settings</a>
        <a href="#" class="cm-btn-ghost logout" style="width:100%;color:#ff5555;border-color:rgba(255,85,85,0.2)">🔒 Sign Out</a>
      `;

      // Set up drop down click trigger
      const trigger = actionsDiv.querySelector('.cm-user-trigger');
      const menu = actionsDiv.querySelector('.cm-dropdown-menu');
      if (trigger && menu) {
        trigger.addEventListener('click', (e) => {
          e.stopPropagation();
          menu.classList.toggle('open');
        });
        document.addEventListener('click', () => menu.classList.remove('open'));
      }

      // Bind dynamic logouts
      [actionsDiv, mobActions].forEach(container => {
        const loBtn = container.querySelector('.logout');
        if (loBtn) loBtn.addEventListener('click', handleSignOut);
      });

      // Load Profile & credits dynamically
      try {
        const response = await fetch(`${API_BASE}/api/user/${encodeURIComponent(userId)}/profile`);
        if (response.ok) {
          const d = await response.json();
          const name = d.name || 'User';
          const email = d.email || 'user@example.com';
          const init = name.split(' ').map(w=>w[0]).join('').toUpperCase().slice(0,2);
          const credits = creditValue(d);

          localStorage.setItem('cm_user_name', name);
          localStorage.setItem('cm_user_email', email);
          localStorage.setItem('cm_user_initials', init);
          localStorage.setItem('cm_user_credits', credits == null ? 'Active' : credits);
          const avatarUrl = d.avatarSrc || d.profilePicUrl || '';
          if (avatarUrl) {
            localStorage.setItem('cm_user_avatar', avatarUrl);
          } else {
            localStorage.removeItem('cm_user_avatar');
          }
          localStorage.setItem('cm_user_subscription', d.subscriptionType || 'FREE');

          // Update header pills
          const pills = document.querySelectorAll('.cm-credit-pill');
          pills.forEach(pill => {
            pill.textContent = credits == null ? 'Credits: Active' : `Credits: ${credits}`;
            pill.href = 'pricing.html';
            if (credits !== null && credits <= 5) {
              pill.classList.add('low');
            }
          });

          // Update profile dropdown elements
          const nameEl = actionsDiv.querySelector('.cm-dropdown-header .name');
          const emailEl = actionsDiv.querySelector('.cm-dropdown-header .email');
          const avatarEl = actionsDiv.querySelector('.cm-avatar-small');

          if (nameEl) nameEl.textContent = name;
          if (emailEl) emailEl.textContent = email;
          const avatarUrlForHeader = d.avatarSrc || d.profilePicUrl || '';
          if (avatarEl) {
            if (avatarUrlForHeader) {
              avatarEl.innerHTML = `<img src="${avatarUrlForHeader}" alt="Avatar" style="width:100%;height:100%;border-radius:50%;object-fit:cover;">`;
            } else {
              avatarEl.textContent = init;
            }
          }
        }
      } catch (err) {
        console.warn("Public Header Profile Load Fail:", err);
        const pills = document.querySelectorAll('.cm-credit-pill');
        pills.forEach(p => p.textContent = 'Credits: --');
      }
    }

    // Inject Theme Toggle into public navbar and drawer
    if (!document.querySelector('.navbar .cm-theme-toggle')) {
      const toggleBtn = document.createElement('button');
      toggleBtn.className = 'cm-theme-toggle';
      toggleBtn.setAttribute('aria-label', 'Toggle Theme');
      toggleBtn.type = 'button';
      toggleBtn.innerHTML = `
        <svg class="sun-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg>
        <svg class="moon-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>
      `;
      toggleBtn.addEventListener('click', toggleThemeAction);
      
      const userTrigger = actionsDiv.querySelector('.cm-user-menu');
      if (userTrigger) actionsDiv.insertBefore(toggleBtn, userTrigger);
      else actionsDiv.appendChild(toggleBtn);

      const mobToggleBtn = document.createElement('button');
      mobToggleBtn.className = 'cm-theme-toggle';
      mobToggleBtn.setAttribute('aria-label', 'Toggle Theme');
      mobToggleBtn.type = 'button';
      mobToggleBtn.innerHTML = toggleBtn.innerHTML;
      mobToggleBtn.addEventListener('click', toggleThemeAction);
      mobActions.appendChild(mobToggleBtn);
    }
  }

  // Helper: Sync VirusTotal scan mode toggle with topbar engine indicator
  function syncScannerPill() {
    const toggle = document.getElementById('scanModeToggle');
    if (!toggle) return;

    const topbar = document.querySelector('.topbar');
    if (!topbar) return;

    let enginePill = topbar.querySelector('#enginePill');
    if (!enginePill) {
      enginePill = document.createElement('div');
      enginePill.id = 'enginePill';
      enginePill.className = 'cm-promo-pill';
      enginePill.style.marginRight = '12px';
      enginePill.style.animation = 'none';
      enginePill.style.cursor = 'default';
      const tbRight = topbar.querySelector('.tb-right');
      if (tbRight) {
        tbRight.insertBefore(enginePill, tbRight.firstChild);
      }
    }

    function updatePill() {
      if (toggle.checked) {
        enginePill.textContent = '🚀 Powered by VirusTotal';
        enginePill.style.color = '#3b82f6';
        enginePill.style.borderColor = 'rgba(59, 130, 246, 0.4)';
        enginePill.style.background = 'rgba(59, 130, 246, 0.08)';
      } else {
        enginePill.textContent = '⚡ Powered by VAJRA';
        enginePill.style.color = 'var(--lime)';
        enginePill.style.borderColor = 'var(--lime-glow)';
        enginePill.style.background = 'var(--lime-dim)';
      }
    }

    toggle.addEventListener('change', updatePill);
    updatePill();
  }

  // Spotlight Search Logic
  let cachedActivities = [];
  async function loadSearchActivities(userId) {
    if (!userId) return;
    try {
      const response = await fetch(`${API_BASE}/api/user/${encodeURIComponent(userId)}/activity`);
      if (response.ok) {
        cachedActivities = await response.json();
      }
    } catch (err) {
      console.warn("Failed to load search activities:", err);
    }
  }

  function setupSpotlightSearch() {
    const searchBox = document.getElementById('searchBox');
    if (!searchBox) return;

    let resultsPanel = searchBox.parentNode.querySelector('.tb-search-results');
    if (!resultsPanel) {
      resultsPanel = document.createElement('div');
      resultsPanel.className = 'tb-search-results';
      searchBox.parentNode.appendChild(resultsPanel);
    }

    const userId = readUserId();
    loadSearchActivities(userId);

    document.addEventListener('keydown', (e) => {
      if ((e.key === '/' && document.activeElement !== searchBox && !['INPUT', 'TEXTAREA'].includes(document.activeElement.tagName)) || 
          ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k')) {
        e.preventDefault();
        searchBox.focus();
        searchBox.select();
      }
    });

    searchBox.addEventListener('focus', () => {
      renderSearchResults(searchBox.value);
    });

    searchBox.addEventListener('input', () => {
      renderSearchResults(searchBox.value);
    });

    document.addEventListener('click', (e) => {
      if (!searchBox.parentNode.contains(e.target)) {
        resultsPanel.classList.remove('open');
      }
    });

    searchBox.addEventListener('keydown', (e) => {
      const items = Array.from(resultsPanel.querySelectorAll('.tb-search-item'));
      if (items.length === 0) return;

      const selectedIndex = items.findIndex(item => item.classList.contains('selected'));

      if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (selectedIndex >= 0) items[selectedIndex].classList.remove('selected');
        const nextIndex = (selectedIndex + 1) % items.length;
        items[nextIndex].classList.add('selected');
        items[nextIndex].scrollIntoView({ block: 'nearest' });
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (selectedIndex >= 0) items[selectedIndex].classList.remove('selected');
        const prevIndex = (selectedIndex - 1 + items.length) % items.length;
        items[prevIndex].classList.add('selected');
        items[prevIndex].scrollIntoView({ block: 'nearest' });
      } else if (e.key === 'Enter') {
        e.preventDefault();
        if (selectedIndex >= 0) {
          items[selectedIndex].click();
        } else {
          items[0].click();
        }
      } else if (e.key === 'Escape') {
        searchBox.blur();
        resultsPanel.classList.remove('open');
      }
    });

    function renderSearchResults(query) {
      const q = query.toLowerCase().trim();
      resultsPanel.innerHTML = '';

      const staticTargets = [
        { title: 'URL Scanner', desc: 'Scan links for phishing/malware', url: 'url-check.html', category: 'Tool' },
        { title: 'Password Check', desc: 'Verify password complexity & strength', url: 'password-check.html', category: 'Tool' },
        { title: 'Malware Scanner', desc: 'Scan files for malware using VAJRA heuristics', url: 'malwareanalysis.html', category: 'Tool' },
        { title: 'Activity Logs', desc: 'View past scans and status history', url: 'activity-logs.html', category: 'Tool' },
        { title: 'Home / Stats', desc: 'Your security dashboard overview', url: 'home.html', category: 'Portal' },
        { title: 'Dashboard Tools', desc: 'Discover and pin widgets', url: 'dashboard.html', category: 'Portal' },
        { title: 'Account Settings', desc: 'Update profile details & avatar', url: 'settings.html', category: 'Settings' },
        { title: 'Support & FAQs', desc: 'Submit help tickets & read FAQs', url: 'settings.html#support', category: 'Settings' },
        { title: 'Cyber News Feed', desc: 'Latest real-time advisory threat news', url: 'cyber-news.html', category: 'Intel' },
        { title: 'Pricing Plans', desc: 'Buy credits and upgrade subscription', url: 'pricing.html', category: 'Billing' },
      ];

      const matches = [];
      staticTargets.forEach(t => {
        if (t.title.toLowerCase().includes(q) || t.desc.toLowerCase().includes(q) || t.category.toLowerCase().includes(q)) {
          matches.push(t);
        }
      });

      cachedActivities.forEach(a => {
        const inputData = a.scannedItem || a.inputData || '';
        const status = a.status || '';
        const type = a.type || 'Scan';
        if (inputData.toLowerCase().includes(q) || status.toLowerCase().includes(q) || type.toLowerCase().includes(q)) {
          matches.push({
            title: inputData.length > 25 ? inputData.slice(0, 25) + '...' : inputData,
            desc: `Status: ${status} (Scanned: ${new Date(a.scannedAt).toLocaleDateString()})`,
            url: type.toLowerCase().includes('pass') ? 'password-check.html' : 'url-check.html',
            category: 'Past Scan'
          });
        }
      });

      if (matches.length === 0) {
        resultsPanel.innerHTML = '<div class="tb-search-empty">No results matching your query.</div>';
      } else {
        const categories = {};
        matches.slice(0, 8).forEach(item => {
          if (!categories[item.category]) categories[item.category] = [];
          categories[item.category].push(item);
        });

        Object.keys(categories).forEach(cat => {
          const groupHeader = document.createElement('div');
          groupHeader.className = 'tb-search-group';
          groupHeader.textContent = cat;
          resultsPanel.appendChild(groupHeader);

          categories[cat].forEach((item, idx) => {
            const row = document.createElement('a');
            row.href = item.url;
            row.className = 'tb-search-item';
            row.innerHTML = `
              <div>
                <span class="title">${item.title}</span>
                <span class="desc">${item.desc}</span>
              </div>
              <span class="badge">${item.category}</span>
            `;
            row.addEventListener('mousedown', (e) => {
              e.preventDefault();
            });
            row.addEventListener('click', () => {
              window.location.href = item.url;
            });
            resultsPanel.appendChild(row);
          });
        });
      }

      resultsPanel.classList.add('open');
    }
  }

  // Dynamic dashboard enhancement (topbar + sidebar)
  async function enhanceDashboardUI() {
    const sidebar = document.querySelector('.sidebar');
    const topbar = document.querySelector('.topbar');
    const userId = readUserId();

    if (!userId) return;

    // 1. Rebuild Sidebar Nav dynamically to include all essential pages and highlight active page
    if (sidebar) {
      const sbNav = sidebar.querySelector('.sb-nav');
      if (sbNav) {
        sbNav.innerHTML = `
          <a class="sb-item ${isActive('home.html')}" href="home.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            <span>Home</span>
          </a>
          <a class="sb-item ${isActive('dashboard.html')}" href="dashboard.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            <span>Dashboard</span>
          </a>
          <a class="sb-item ${isActive('url-check.html')}" href="url-check.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
            <span>URL Scanner</span>
          </a>
          <a class="sb-item ${isActive('password-check.html')}" href="password-check.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
            <span>Password Check</span>
          </a>
          <a class="sb-item ${isActive('malwareanalysis.html')}" href="malwareanalysis.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
            <span>Malware Scanner</span>
          </a>
          <a class="sb-item ${isActive('activity-logs.html')}" href="activity-logs.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
            <span>Activity Logs</span>
          </a>
          <a class="sb-item ${isActive('cyber-news.html')}" href="cyber-news.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><path d="M16 8h2m-2 4h2M6 8h6v8H6z"/></svg>
            <span>News</span>
          </a>
          <a class="sb-item ${isActive('pricing.html')}" href="pricing.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M12 8v8m-4-4h8"/></svg>
            <span>Pricing</span>
          </a>
          <a class="sb-item ${isActive('contactus.html')}" href="contactus.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
            <span>Contact Us</span>
          </a>
          <a class="sb-item" href="#" onclick="showToast('Vault — Coming Soon')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            <span>Family Vault</span>
          </a>
        `;
      }

      // Rebuild Sidebar Profile & Bottom Nav cleanly (No credit or name overlap)
      let sbProfile = sidebar.querySelector('.cm-sidebar-profile');
      if (!sbProfile) {
        sbProfile = document.createElement('div');
        sbProfile.className = 'cm-sidebar-profile';
        sbProfile.innerHTML = `
          <div class="cm-sidebar-avatar" id="sbAvatar">?</div>
          <div class="cm-sidebar-info">
            <span class="cm-sidebar-name" id="sbName">User</span>
            <span class="cm-sidebar-score">Safe Score: <span id="sbScore">...</span></span>
          </div>
        `;
        sidebar.insertBefore(sbProfile, sidebar.querySelector('.sb-bottom'));
      }

      const sbBottom = sidebar.querySelector('.sb-bottom');
      if (sbBottom) {
        sbBottom.innerHTML = `
          <a class="sb-item ${isActive('settings.html')}" href="settings.html">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/></svg>
            <span>Settings</span>
          </a>
          <a class="sb-logout" href="#">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            <span>Secure Log Out</span>
          </a>
        `;
        const logoutBtn = sbBottom.querySelector('.sb-logout');
        if (logoutBtn) {
          logoutBtn.addEventListener('click', handleSignOut);
        }
      }
    }

    // 2. Uniform Topbar Breadcrumbs & Spotlight Search Standardization
    if (topbar) {
      const pageFile = window.location.pathname.split('/').pop() || 'index.html';
      const isMainHomeOrDash = pageFile.includes('home.html') || pageFile.includes('dashboard.html');

      if (!isMainHomeOrDash) {
        const greet = topbar.querySelector('.tb-greet');
        if (greet) greet.style.display = 'none';
        const tbDate = topbar.querySelector('.tb-date');
        if (tbDate) tbDate.style.display = 'none';

        if (!topbar.querySelector('.tb-breadcrumb')) {
          const breadcrumb = document.createElement('div');
          breadcrumb.className = 'tb-breadcrumb';
          
          let displayTitle = 'Overview';
          if (pageFile.includes('url-check.html')) displayTitle = 'URL Scanner';
          else if (pageFile.includes('password-check.html')) displayTitle = 'Password Check';
          else if (pageFile.includes('malwareanalysis.html')) displayTitle = 'Malware Scanner';
          else if (pageFile.includes('activity-logs.html')) displayTitle = 'Activity Logs';
          else if (pageFile.includes('settings.html')) displayTitle = 'Account Settings';
          else if (pageFile.includes('tools.html')) displayTitle = 'Tools';
          else if (pageFile.includes('pricing.html')) displayTitle = 'Pricing';
          else if (pageFile.includes('cyber-news.html')) displayTitle = 'Cyber News';
          else if (pageFile.includes('aboutus.html')) displayTitle = 'About Us';
          else if (pageFile.includes('contactus.html')) displayTitle = 'Contact Us';

          breadcrumb.innerHTML = `
            <span class="sep">Portal</span>
            <span class="sep">›</span>
            <span class="current">${displayTitle}</span>
          `;
          const tbLeft = topbar.querySelector('.tb-left');
          if (tbLeft) {
            tbLeft.appendChild(breadcrumb);
          }
        }
      }

      // Ensure search bar exists on all dashboard topbars
      if (!topbar.querySelector('#searchBox')) {
        let tbCenter = topbar.querySelector('.tb-center');
        if (!tbCenter) {
          tbCenter = document.createElement('div');
          tbCenter.className = 'tb-center';
          const tbRight = topbar.querySelector('.tb-right');
          if (tbRight) {
            topbar.insertBefore(tbCenter, tbRight);
          } else {
            topbar.appendChild(tbCenter);
          }
        }
        tbCenter.innerHTML = `
          <div class="tb-search" style="position:relative;width:100%;max-width:380px;">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="position:absolute;left:12px;top:50%;transform:translateY(-50%);width:13px;height:13px;color:var(--text3);"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
            <input id="searchBox" placeholder="Spotlight search (/ or ⌘K)" style="width:100%;background:var(--bg3);border:1px solid var(--border);border-radius:100px;padding:7px 14px 7px 32px;font-family:var(--mono);font-size:.72rem;color:var(--text);outline:none;transition:border-color .18s;">
            <div class="tb-search-kbd" style="position:absolute;right:10px;top:50%;transform:translateY(-50%);font-family:var(--mono);font-size:.55rem;color:var(--text3);background:var(--bg4);border:1px solid var(--border);padding:2px 6px;border-radius:4px;">/</div>
          </div>
        `;
      }

      setupSpotlightSearch();
      syncScannerPill();

      // Topbar Credits Pill & Premium Profile Dropdown
      const tbRight = topbar.querySelector('.tb-right');
      if (tbRight && !document.querySelector('.tb-right .cm-credit-pill')) {
        const creditPill = document.createElement('a');
        creditPill.href = 'pricing.html';
        creditPill.className = 'cm-credit-pill';
        creditPill.textContent = 'Credits: ...';
        tbRight.insertBefore(creditPill, tbRight.firstChild);
      }

      if (tbRight && !tbRight.querySelector('.cm-theme-toggle')) {
        const tbThemeToggle = document.createElement('button');
        tbThemeToggle.className = 'cm-theme-toggle';
        tbThemeToggle.setAttribute('aria-label', 'Toggle Theme');
        tbThemeToggle.type = 'button';
        tbThemeToggle.innerHTML = `
          <svg class="sun-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M6.34 17.66l-1.41 1.41M19.07 4.93l-1.41 1.41"/></svg>
          <svg class="moon-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/></svg>
        `;
        tbThemeToggle.addEventListener('click', toggleThemeAction);
        
        const tbUser = tbRight.querySelector('.tb-user');
        const tbBell = tbRight.querySelector('.tb-bell');
        if (tbUser) tbRight.insertBefore(tbThemeToggle, tbUser);
        else if (tbBell) tbRight.insertBefore(tbThemeToggle, tbBell);
        else tbRight.appendChild(tbThemeToggle);
      }

      const tbUser = topbar.querySelector('.tb-user');
      if (tbUser && !tbUser.querySelector('.cm-dropdown-menu')) {
        tbUser.style.position = 'relative';
        
        const ddMenu = document.createElement('div');
        ddMenu.className = 'cm-dropdown-menu';
        ddMenu.innerHTML = `
          <div class="cm-dropdown-header">
            <span class="name" id="ddName">Loading profile...</span>
            <span class="email" id="ddEmail">...</span>
          </div>
          <a href="dashboard.html" class="cm-dropdown-item">📊 Dashboard</a>
          <a href="settings.html" class="cm-dropdown-item">⚙️ Settings</a>
          <div class="cm-dropdown-divider"></div>
          <div class="cm-dropdown-item logout">🔒 Sign Out</div>
        `;
        tbUser.appendChild(ddMenu);

        tbUser.addEventListener('click', (e) => {
          if (ddMenu.contains(e.target)) return;
          e.preventDefault();
          e.stopPropagation();
          ddMenu.classList.toggle('open');
        });

        ddMenu.querySelector('.logout').addEventListener('click', handleSignOut);
        document.addEventListener('click', () => ddMenu.classList.remove('open'));
      }
    }

    // 3. Load profile data and populate ALL placeholders (sidebar + topbar + dropdown)
    try {
      const response = await fetch(`${API_BASE}/api/user/${encodeURIComponent(userId)}/profile`);
      if (response.ok) {
        const d = await response.json();
        const name = d.name || 'User';
        const email = d.email || 'user@example.com';
        const init = name.split(' ').map(w=>w[0]).join('').toUpperCase().slice(0,2);
        const credits = creditValue(d);
        const score = d.safeScore != null ? d.safeScore : '—';

        localStorage.setItem('cm_user_name', name);
        localStorage.setItem('cm_user_email', email);
        localStorage.setItem('cm_user_initials', init);
        localStorage.setItem('cm_user_credits', credits == null ? 'Active' : credits);
        localStorage.setItem('cm_user_score', score);
        if (d.avatarSrc) {
          localStorage.setItem('cm_user_avatar', d.avatarSrc);
        } else {
          localStorage.removeItem('cm_user_avatar');
        }

        const tbPill = document.querySelector('.tb-right .cm-credit-pill');
        if (tbPill) {
          tbPill.textContent = credits == null ? 'Credits: Active' : `Credits: ${credits}`;
          if (credits !== null && credits <= 5) tbPill.classList.add('low');
        }

        const sbN = document.getElementById('sbName');
        const sbA = document.getElementById('sbAvatar');
        const sbS = document.getElementById('sbScore');
        if (sbN) sbN.textContent = name;
        if (sbA) {
          if (d.avatarSrc) {
            sbA.innerHTML = `<img src="${d.avatarSrc}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
          } else {
            sbA.textContent = init;
          }
        }
        const tbAvatar = document.getElementById('tbAvatar');
        if (tbAvatar) {
          if (d.avatarSrc) {
            tbAvatar.innerHTML = `<img src="${d.avatarSrc}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
          } else {
            tbAvatar.textContent = init;
          }
        }
        if (sbS) sbS.textContent = score;

        ['sbName2', 'welcomeName'].forEach(id => {
          const el = document.getElementById(id);
          if (el) el.textContent = name;
        });
        const sbAvatar2 = document.getElementById('sbAvatar2');
        if (sbAvatar2) {
          if (d.avatarSrc) {
            sbAvatar2.innerHTML = `<img src="${d.avatarSrc}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
          } else {
            sbAvatar2.textContent = init;
          }
        }
        const ddAvatar = document.querySelector('.tb-user .cm-avatar-small');
        if (ddAvatar && d.avatarSrc) {
          ddAvatar.innerHTML = `<img src="${d.avatarSrc}" alt="Avatar" style="width:100%;height:100%;border-radius:50%;object-fit:cover;">`;
        }
        const sbScore2 = document.getElementById('sbScore2');
        if (sbScore2) sbScore2.textContent = score;

        const ddName = document.getElementById('ddName');
        const ddEmail = document.getElementById('ddEmail');
        if (ddName) ddName.textContent = name;
        if (ddEmail) ddEmail.textContent = email;
      }
    } catch (err) {
      console.warn("Dashboard Header Load Fail:", err);
    }
  }

  // Handle logout check on load
  function checkLogoutAlert() {
    const params = new URLSearchParams(window.location.search);
    if (params.get('logout') === 'true') {
      // Clear URL params without reload
      const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
      window.history.replaceState({ path: cleanUrl }, '', cleanUrl);
      setTimeout(() => {
        showToast("🔒 Logged out securely. Stay safe!");
      }, 500);
    }
  }

  function pageContext() {
    const main = document.querySelector('main') || document.body;
    return {
      title: document.title,
      url: location.href,
      page: location.pathname.split('/').pop() || 'index.html',
      text: (main.innerText || '').replace(/\s+/g, ' ').slice(0, 4200)
    };
  }

  function localExpertAnswer(question) {
    const q = question.toLowerCase();
    if (q.includes('credit')) return 'Credits paid plan me daily reset hote hain. Advanced online link check 1 credit use karta hai; local checks 0 credit use karte hain. Login ke baad header me current credits dikhenge.';
    if (q.includes('phishing') || q.includes('link')) return 'Suspicious link check karne ke liye Tools > Phishing Site Check open karo. Unknown sender, misspelled domain, urgency, shortened URLs, aur login forms ko extra caution se verify karo.';
    if (q.includes('password')) return 'Strong password 14+ characters, unique, random, aur password manager generated hona chahiye. Reused password sabse bada risk hai.';
    if (q.includes('news')) return 'Cyber News page RSS sources se latest stories laata hai. Har story ka CyberMitra AI summary article page par milega, aur original publisher ke liye Visit Original Site button rahega.';
    return 'Main CyberMitra AI Buddy hoon. Is page ke context ke basis par help kar sakta hoon: tools, credits, phishing, password safety, news summaries, aur general cybersecurity guidance.';
  }

  async function askBuddy(question) {
    const payload = {
      question,
      mode: 'site_rag_cybersecurity_expert',
      context: pageContext()
    };
    try {
      const controller = new AbortController();
      const timeout = setTimeout(() => controller.abort(), 2800);
      const response = await fetch(`${API_BASE}/api/ai/buddy-chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        signal: controller.signal
      });
      clearTimeout(timeout);
      if (!response.ok) throw new Error('AI Buddy endpoint unavailable');
      const data = await response.json();
      return data.answer || localExpertAnswer(question);
    } catch (error) {
      return localExpertAnswer(question);
    }
  }

  // Floating speech bubble for AI Buddy
  function showChatbotSpeechBubble(launch) {
    if (localStorage.getItem('cm_buddy_welcome_shown')) return;

    const bubble = document.createElement('div');
    bubble.className = 'cm-ai-bubble';
    bubble.style.cssText = 'position:fixed;right:22px;bottom:80px;z-index:702;background:var(--bg2);border:1px solid var(--lime);border-radius:12px;padding:10px 14px;font-size:0.75rem;color:var(--text);box-shadow:var(--shadow-lg);max-width:200px;font-family:var(--font);animation:fadeUp .4s ease;';
    bubble.innerHTML = `
      <div style="font-weight:700;color:var(--lime);margin-bottom:4px;">🤖 CyberMitra AI</div>
      Need help scanning a link or strengthening your password? Ask me!
      <div class="cm-bubble-close" style="position:absolute;top:4px;right:6px;cursor:pointer;font-weight:800;color:var(--text3);font-size:0.7rem;">×</div>
    `;
    document.body.appendChild(bubble);

    const closeBtn = bubble.querySelector('.cm-bubble-close');
    closeBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      bubble.remove();
      localStorage.setItem('cm_buddy_welcome_shown', 'true');
    });

    launch.addEventListener('click', () => {
      bubble.remove();
      localStorage.setItem('cm_buddy_welcome_shown', 'true');
    });

    setTimeout(() => {
      if (bubble.parentNode) {
        bubble.style.opacity = '0';
        bubble.style.transition = 'opacity .4s';
        setTimeout(() => bubble.remove(), 400);
      }
    }, 8000);
  }

  function installAiBuddy() {
    if (document.getElementById('cmAiBuddy')) return;
    const panel = document.createElement('section');
    panel.className = 'cm-ai-panel';
    panel.id = 'cmAiBuddy';
    panel.innerHTML = `
      <div class="cm-ai-head">
        <div><strong>AI Buddy</strong><span>Cybersecurity Expert · Site RAG</span></div>
        <button class="cm-ai-close" type="button" aria-label="Close AI Buddy">×</button>
      </div>
      <div class="cm-ai-body">
        <div class="cm-ai-log">
          <div class="cm-ai-msg">Namaste. Main CyberMitra AI Buddy hoon. Site ke current page context se help kar sakta hoon.</div>
        </div>
        <form class="cm-ai-form">
          <input autocomplete="off" placeholder="Ask cybersecurity help...">
          <button type="submit">Ask</button>
        </form>
      </div>
    `;
    const launch = document.createElement('button');
    launch.className = 'cm-ai-launch';
    launch.type = 'button';
    launch.textContent = 'AI Buddy';
    document.body.appendChild(panel);
    document.body.appendChild(launch);

    const log = panel.querySelector('.cm-ai-log');
    const form = panel.querySelector('form');
    const input = panel.querySelector('input');
    const close = panel.querySelector('.cm-ai-close');

    function addMessage(text, type) {
      const msg = document.createElement('div');
      msg.className = `cm-ai-msg ${type === 'user' ? 'user' : ''}`;
      msg.textContent = text;
      log.appendChild(msg);
      log.scrollTop = log.scrollHeight;
      return msg;
    }

    launch.addEventListener('click', () => panel.classList.toggle('open'));
    close.addEventListener('click', () => panel.classList.remove('open'));
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const question = input.value.trim();
      if (!question) return;
      addMessage(question, 'user');
      input.value = '';
      const pending = addMessage('Thinking with site context...', 'bot');
      pending.textContent = await askBuddy(question);
    });

    launch.style.boxShadow = '0 0 15px var(--lime-glow)';
    launch.style.transition = 'all 0.25s ease';
    launch.style.animation = 'cm-pulse-glow 2.5s infinite';
    launch.addEventListener('mouseenter', () => {
      launch.style.transform = 'scale(1.05) translateY(-2px)';
      launch.style.boxShadow = '0 0 25px var(--lime-glow)';
    });
    launch.addEventListener('mouseleave', () => {
      launch.style.transform = 'none';
      launch.style.boxShadow = '0 0 15px var(--lime-glow)';
    });

    showChatbotSpeechBubble(launch);
  }

  // Dynamic wrapper for public marketing pages when user is logged in
  function wrapInDashboardShell() {
    const pubNavbar = document.querySelector('.navbar');
    const pubFooter = document.querySelector('.footer') || document.querySelector('footer');
    if (pubNavbar) pubNavbar.style.setProperty('display', 'none', 'important');
    if (pubFooter) pubFooter.style.setProperty('display', 'none', 'important');

    const mainContent = document.querySelector('main') || document.querySelector('.page-wrapper') || document.body.firstElementChild;
    if (!mainContent) return;

    const parent = mainContent.parentNode;
    mainContent.remove();

    const shellDiv = document.createElement('div');
    shellDiv.className = 'shell';

    const overlay = document.createElement('div');
    overlay.className = 'sb-overlay';
    overlay.id = 'sbOverlay';
    shellDiv.appendChild(overlay);

    const sidebar = document.createElement('aside');
    sidebar.className = 'sidebar';
    sidebar.id = 'sidebar';
    sidebar.innerHTML = `
      <a href="home.html" class="sb-logo">
        <div class="sb-logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
        </div>
        <span class="sb-logo-text">CyberMitra</span>
      </a>
      <nav class="sb-nav"></nav>
      <div class="sb-bottom"></div>
    `;
    shellDiv.appendChild(sidebar);

    const mainArea = document.createElement('div');
    mainArea.className = 'main-area';

    const topbar = document.createElement('header');
    topbar.className = 'topbar';
    topbar.innerHTML = `
      <div class="tb-left">
        <button class="tb-hamburger">
          <span></span><span></span><span></span>
        </button>
      </div>
      <div class="tb-center"></div>
      <div class="tb-right">
        <div class="tb-secure-pill" style="display:inline-flex;align-items:center;gap:6px;padding:5px 12px;background:rgba(200,255,0,0.07);border:1px solid rgba(200,255,0,0.18);border-radius:100px;font-family:var(--mono);font-size:0.6rem;font-weight:600;color:var(--lime);letter-spacing:0.04em;text-transform:uppercase;white-space:nowrap;margin-right:12px;">
          <div class="tb-secure-dot" style="width:5px;height:5px;border-radius:50%;background:var(--lime);animation:pulse 2s infinite;"></div>
          <span>System Secure</span>
        </div>
        <div class="tb-bell" onclick="showToast('No new alerts')" style="position:relative;width:32px;height:32px;background:var(--bg3);border:1px solid var(--border);border-radius:8px;display:flex;align-items:center;justify-content:center;cursor:pointer;margin-right:12px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width:14px;height:14px;color:var(--text2);">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          </svg>
          <div class="tb-bell-badge" style="position:absolute;top:5px;right:5px;width:6px;height:6px;border-radius:50%;background:var(--red);border:1.5px solid var(--bg);"></div>
        </div>
        <div class="tb-user">
          <div class="tb-user-info" style="margin-right: 8px;">
            <div class="tb-user-name" id="tbName">User</div>
            <div class="tb-user-plan" style="font-size:0.65rem;color:var(--text3);">Pro Plan</div>
          </div>
          <div class="tb-avatar" id="tbAvatar">U</div>
        </div>
      </div>
    `;
    mainArea.appendChild(topbar);

    const pageDiv = document.createElement('div');
    pageDiv.className = 'page';
    const pageContent = document.createElement('div');
    pageContent.className = 'page-content';
    pageContent.style.padding = '36px';

    pageContent.appendChild(mainContent);
    pageDiv.appendChild(pageContent);
    mainArea.appendChild(pageDiv);
    shellDiv.appendChild(mainArea);

    document.body.appendChild(shellDiv);

    document.body.style.margin = '0';
    document.body.style.padding = '0';
    document.body.style.background = 'var(--bg)';

    mainContent.style.maxWidth = '100%';
    mainContent.style.padding = '0';
    mainContent.style.margin = '0';
    mainContent.classList.remove('page-wrapper', 'marketing-page');

    const hamburger = topbar.querySelector('.tb-hamburger');
    if (hamburger) {
      hamburger.addEventListener('click', () => {
        sidebar.classList.toggle('open');
        overlay.classList.toggle('open');
      });
    }
    overlay.addEventListener('click', () => {
      sidebar.classList.remove('open');
      overlay.classList.remove('open');
    });
  }

  // Toast notifications helper
  function showToast(msg) {
    const e = document.querySelector('.cm-toast');
    if (e) e.remove();
    const t = document.createElement('div');
    t.className = 'cm-toast';
    t.textContent = msg;
    t.style.cssText = 'position:fixed;bottom:30px;left:50%;transform:translateX(-50%);background:var(--bg2);border:1px solid var(--lime);border-radius:100px;padding:10px 22px;font-family:\'JetBrains Mono\',monospace;font-size:.78rem;color:var(--lime);z-index:99999;white-space:nowrap;pointer-events:none;box-shadow:0 0 20px var(--lime-glow);animation:cm-pulse-glow 2.5s infinite;';
    document.body.appendChild(t);
    setTimeout(() => {
      t.style.opacity = '0';
      t.style.transition = 'opacity .3s';
      setTimeout(() => t.remove(), 300);
    }, 2400);
  }

  function applyCachedProfile() {
    const name = localStorage.getItem('cm_user_name') || 'User';
    const email = localStorage.getItem('cm_user_email') || 'user@example.com';
    const avatar = localStorage.getItem('cm_user_avatar');
    const initials = localStorage.getItem('cm_user_initials') || 'U';
    const credits = localStorage.getItem('cm_user_credits');
    const score = localStorage.getItem('cm_user_score') || '—';

    // Update Topbar Plan Dynamic Badge
    const subType = localStorage.getItem('cm_user_subscription') || 'FREE';
    const planText = (subType.toUpperCase() === 'PRO') ? 'Pro Tier' : 'Free Tier';
    document.querySelectorAll('.tb-user-plan').forEach(el => {
      el.textContent = planText;
    });

    // 1. Sidebar Elements
    const sbN = document.getElementById('sbName');
    const sbA = document.getElementById('sbAvatar');
    const sbS = document.getElementById('sbScore');
    if (sbN) sbN.textContent = name;
    if (sbA) {
      if (avatar) {
        sbA.innerHTML = `<img src="${avatar}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
      } else {
        sbA.textContent = initials;
      }
    }
    if (sbS) sbS.textContent = score;

    // 2. Topbar Elements
    const tbN = document.getElementById('tbName');
    const tbAvatar = document.getElementById('tbAvatar');
    if (tbN) tbN.textContent = name;
    if (tbAvatar) {
      if (avatar) {
        tbAvatar.innerHTML = `<img src="${avatar}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
      } else {
        tbAvatar.textContent = initials;
      }
    }

    // Welcome blocks
    ['sbName2', 'welcomeName'].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.textContent = name;
    });
    const sbAvatar2 = document.getElementById('sbAvatar2');
    if (sbAvatar2) {
      if (avatar) {
        sbAvatar2.innerHTML = `<img src="${avatar}" alt="Avatar" style="width:100%;height:100%;object-fit:cover;">`;
      } else {
        sbAvatar2.textContent = initials;
      }
    }
    const sbScore2 = document.getElementById('sbScore2');
    if (sbScore2) sbScore2.textContent = score;

    // Dropdown header & small avatar
    const ddName = document.getElementById('ddName');
    const ddEmail = document.getElementById('ddEmail');
    if (ddName) ddName.textContent = name;
    if (ddEmail) ddEmail.textContent = email;

    const ddAvatar = document.querySelector('.tb-user .cm-avatar-small');
    if (ddAvatar) {
      if (avatar) {
        ddAvatar.innerHTML = `<img src="${avatar}" alt="Avatar" style="width:100%;height:100%;border-radius:50%;object-fit:cover;">`;
      } else {
        ddAvatar.textContent = initials;
      }
    }

    // Credits pill
    const tbPill = document.querySelector('.tb-right .cm-credit-pill');
    if (tbPill && credits !== null) {
      tbPill.textContent = credits === 'Active' ? 'Credits: Active' : `Credits: ${credits}`;
      if (credits !== 'Active' && Number(credits) <= 5) {
        tbPill.classList.add('low');
      } else {
        tbPill.classList.remove('low');
      }
    }

    // Credits pill click binding for easy test refills (Test Mode Bypass)
    document.querySelectorAll('.cm-credit-pill').forEach(pill => {
      if (!pill.dataset.refillBound) {
        pill.dataset.refillBound = 'true';
        pill.addEventListener('click', (e) => {
          e.preventDefault();
          e.stopPropagation();
          localStorage.setItem('cm_user_credits', '50');
          showToast('🎁 Test Mode: Credits refilled to 50!');
          applyCachedProfile();
          if (typeof profile !== 'undefined') {
            profile.credits = 50;
            if (typeof updateProfileUI === 'function') updateProfileUI();
          }
        });
      }
    });

    // Public Header Dropdown Small Avatar & Name
    const pubAvatarEl = document.querySelector('.cm-user-menu .cm-avatar-small');
    const pubNameEl = document.querySelector('.cm-dropdown-header .name');
    const pubEmailEl = document.querySelector('.cm-dropdown-header .email');
    if (pubAvatarEl) {
      if (avatar) {
        pubAvatarEl.innerHTML = `<img src="${avatar}" alt="Avatar" style="width:100%;height:100%;border-radius:50%;object-fit:cover;">`;
      } else {
        pubAvatarEl.textContent = initials;
      }
    }
    if (pubNameEl) pubNameEl.textContent = name;
    if (pubEmailEl) pubEmailEl.textContent = email;

    const pubPills = document.querySelectorAll('.cm-credit-pill');
    pubPills.forEach(pill => {
      if (credits !== null) {
        pill.textContent = credits === 'Active' ? 'Credits: Active' : `Credits: ${credits}`;
        if (credits !== 'Active' && credits <= 5) pill.classList.add('low');
      }
    });
  }

  // Trigger enhancements on load
  function initEnhancements() {
    enhanceLogo();

    const userId = readUserId();
    const publicPages = ['tools.html', 'pricing.html', 'cyber-news.html', 'aboutus.html', 'contactus.html', 'malwareanalysis.html'];
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';

    if (userId && publicPages.some(page => currentPage.includes(page))) {
      wrapInDashboardShell();
    }

    const isDashboard = document.querySelector('.sidebar') || document.querySelector('.topbar') || document.querySelector('.sb-nav');
    
    if (isDashboard) {
      enhanceDashboardUI();
    } else {
      enhancePublicHeader();
    }

    // Instantly paint cached values on initial load to eliminate initials flashing or avatar desync
    if (userId) {
      applyCachedProfile();
    }
    
    installAiBuddy();
    checkLogoutAlert();

    // Hydrate notification bell if dashboard layout is active
    if (isDashboard) {
      if (typeof window.initializeNotifications === 'function') {
        window.initializeNotifications();
      } else {
        const notifScript = document.createElement('script');
        notifScript.src = 'notifications.js';
        notifScript.onload = () => {
          if (typeof window.initializeNotifications === 'function') {
            window.initializeNotifications();
          }
        };
        document.head.appendChild(notifScript);
      }
    }
    // Re-run logo and text rebranding at the end to cover all dynamic slots
    enhanceLogo();
  }

  // Bind init
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initEnhancements);
  } else {
    initEnhancements();
  }


  // ── GUEST RESTRICTION POPUP MODAL ──
  function showGuestRestrictionModal() {
    let modal = document.getElementById('guestRestrictionModal');
    if (modal) {
      modal.style.display = 'flex';
      setTimeout(() => modal.classList.add('open'), 10);
      return;
    }

    const modalStyle = document.createElement('style');
    modalStyle.textContent = `
      #guestRestrictionModal {
        position: fixed; inset: 0;
        background: rgba(15, 23, 42, 0.3);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        display: none; align-items: center; justify-content: center;
        z-index: 99999; opacity: 0;
        transition: opacity 0.25s cubic-bezier(0.16, 1, 0.3, 1);
        padding: 20px;
      }
      #guestRestrictionModal.open {
        display: flex;
        opacity: 1;
      }
      html[data-theme="dark"] #guestRestrictionModal {
        background: rgba(8, 12, 24, 0.75);
      }
      #guestRestrictionModal .modal {
        background: var(--surface, #ffffff);
        border: 1px solid var(--border, #e2e8f0);
        border-radius: 16px; padding: 32px;
        width: 100%; max-width: 440px;
        box-shadow: 0 20px 50px rgba(15, 23, 42, 0.12);
        transform: scale(0.95);
        transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
        text-align: left;
        position: relative;
      }
      #guestRestrictionModal.open .modal {
        transform: scale(1);
      }
      html[data-theme="dark"] #guestRestrictionModal .modal {
        box-shadow: 0 25px 60px rgba(0, 0, 0, 0.65);
      }
      #guestRestrictionModal .modal-close {
        position: absolute; top: 16px; right: 16px;
        font-size: 1rem; color: var(--text3, #94a3b8);
        cursor: pointer; transition: all 0.18s;
        width: 32px; height: 32px;
        display: flex; align-items: center; justify-content: center;
        border-radius: 50%; border: none; background: transparent;
      }
      #guestRestrictionModal .modal-close:hover {
        color: var(--text, #0f172a);
        background: var(--bg3, #f1f5f9);
      }
      html[data-theme="dark"] #guestRestrictionModal .modal-close:hover {
        background: var(--bg3, rgba(255, 255, 255, 0.05));
      }
      #guestRestrictionModal .modal-shield-icon {
        width: 42px; height: 42px;
        background: rgba(30, 64, 175, 0.06);
        border: 1px solid rgba(30, 64, 175, 0.15);
        border-radius: 10px;
        display: flex; align-items: center; justify-content: center;
        color: #1e40af;
        flex-shrink: 0;
      }
      html[data-theme="dark"] #guestRestrictionModal .modal-shield-icon {
        background: rgba(200, 255, 0, 0.06);
        border: 1px solid rgba(200, 255, 0, 0.18);
        color: #c8ff00;
      }
      #guestRestrictionModal .modal-title {
        font-family: var(--font, 'Outfit', sans-serif);
        font-size: 1.35rem; font-weight: 700;
        color: var(--text, #0f172a);
      }
      #guestRestrictionModal .modal-sub {
        font-family: var(--font, 'Outfit', sans-serif);
        font-size: 0.88rem; color: var(--text2, #475569);
        line-height: 1.6; margin-bottom: 26px;
      }
      #guestRestrictionModal .btn {
        display: inline-flex; align-items: center; justify-content: center;
        min-height: 44px; padding: 12px 20px; border-radius: 8px;
        font-family: var(--font, 'Outfit', sans-serif); font-size: 0.85rem; font-weight: 600;
        cursor: pointer; transition: all 0.2s ease; border: 1px solid transparent;
        text-transform: none; text-align: center;
      }
      #guestRestrictionModal .btn-primary {
        background: #1e40af;
        color: #ffffff !important;
        box-shadow: 0 4px 12px rgba(30, 64, 175, 0.2);
        flex: 2;
      }
      #guestRestrictionModal .btn-primary:hover {
        background: #1e3a8a;
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(30, 64, 175, 0.3);
      }
      html[data-theme="dark"] #guestRestrictionModal .btn-primary {
        background: #c8ff00;
        color: #0a0a0a !important;
        box-shadow: 0 4px 12px rgba(200, 255, 0, 0.2);
      }
      html[data-theme="dark"] #guestRestrictionModal .btn-primary:hover {
        background: #c8ff00;
        opacity: 0.9;
        box-shadow: 0 6px 16px rgba(200, 255, 0, 0.35);
      }
      #guestRestrictionModal .btn-ghost {
        color: #1e40af;
        border-color: #cbd5e1;
        background: transparent;
        flex: 1;
      }
      #guestRestrictionModal .btn-ghost:hover {
        border-color: #1e40af;
        background: #f8fafc;
      }
      html[data-theme="dark"] #guestRestrictionModal .btn-ghost {
        color: #f2f2f2;
        border-color: rgba(255, 255, 255, 0.15);
      }
      html[data-theme="dark"] #guestRestrictionModal .btn-ghost:hover {
        border-color: #f2f2f2;
        background: rgba(255, 255, 255, 0.05);
      }
    `;
    document.head.appendChild(modalStyle);

    modal = document.createElement('div');
    modal.id = 'guestRestrictionModal';
    modal.innerHTML = `
      <div class="modal">
        <button class="modal-close" onclick="window.closeGuestModal()">✕</button>
        <div style="display: flex; align-items: center; gap: 14px; margin-bottom: 18px;">
          <div class="modal-shield-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="width: 20px; height: 20px;"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
          </div>
          <div class="modal-title">Sign Up / Login Required</div>
        </div>
        <p class="modal-sub">
          To run threat analysis scans and view detailed security reports, please log in or create a free CYPR account.
        </p>
        <div style="display: flex; gap: 12px;">
          <button class="btn btn-ghost" onclick="window.location.href='login.html'">Login</button>
          <button class="btn btn-primary" onclick="window.location.href='signup.html'">Sign Up</button>
        </div>
      </div>
    `;

    document.body.appendChild(modal);
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('open'), 10);
  }

  window.closeGuestModal = function() {
    const modal = document.getElementById('guestRestrictionModal');
    if (modal) {
      modal.classList.remove('open');
      setTimeout(() => {
        modal.style.display = 'none';
      }, 250);
    }
  };

  window.showGuestRestrictionModal = showGuestRestrictionModal;

  // ── PREMIUM PRICING COMING SOON POPUP MODAL ──
  function showPricingComingSoonModal() {
    let modal = document.getElementById('pricingComingSoonModal');
    if (modal) {
      modal.style.display = 'flex';
      setTimeout(() => modal.classList.add('open'), 10);
      return;
    }

    const modalStyle = document.createElement('style');
    modalStyle.textContent = `
      #pricingComingSoonModal {
        position: fixed; inset: 0;
        background: rgba(15, 23, 42, 0.4);
        backdrop-filter: blur(16px);
        -webkit-backdrop-filter: blur(16px);
        display: none; align-items: center; justify-content: center;
        z-index: 99999; opacity: 0;
        transition: opacity 0.25s cubic-bezier(0.16, 1, 0.3, 1);
        padding: 20px;
      }
      #pricingComingSoonModal.open {
        display: flex;
        opacity: 1;
      }
      html[data-theme="dark"] #pricingComingSoonModal {
        background: rgba(8, 12, 24, 0.8);
      }
      #pricingComingSoonModal .modal {
        background: var(--surface, #ffffff);
        border: 1px solid var(--border, #e2e8f0);
        border-radius: 20px; padding: 36px;
        width: 100%; max-width: 480px;
        box-shadow: 0 20px 50px rgba(15, 23, 42, 0.15);
        transform: scale(0.95);
        transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
        text-align: center;
        position: relative;
        overflow: hidden;
      }
      #pricingComingSoonModal.open .modal {
        transform: scale(1);
      }
      html[data-theme="dark"] #pricingComingSoonModal .modal {
        box-shadow: 0 25px 60px rgba(0, 0, 0, 0.7);
        background: rgba(15, 23, 42, 0.95);
        border-color: rgba(255, 255, 255, 0.08);
      }
      #pricingComingSoonModal .modal-close {
        position: absolute; top: 16px; right: 16px;
        font-size: 1rem; color: var(--text3, #94a3b8);
        cursor: pointer; transition: all 0.18s;
        width: 32px; height: 32px;
        display: flex; align-items: center; justify-content: center;
        border-radius: 50%; border: none; background: transparent;
      }
      #pricingComingSoonModal .modal-close:hover {
        color: var(--text, #0f172a);
        background: var(--bg3, #f1f5f9);
      }
      html[data-theme="dark"] #pricingComingSoonModal .modal-close:hover {
        background: rgba(255, 255, 255, 0.05);
        color: #ffffff;
      }
      #pricingComingSoonModal .modal-icon-wrapper {
        width: 60px; height: 60px;
        background: rgba(59, 130, 246, 0.08);
        border: 1px solid rgba(59, 130, 246, 0.2);
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        margin: 0 auto 20px;
        box-shadow: 0 0 20px rgba(59, 130, 246, 0.1);
      }
      html[data-theme="dark"] #pricingComingSoonModal .modal-icon-wrapper {
        background: rgba(200, 255, 0, 0.06);
        border-color: rgba(200, 255, 0, 0.25);
        box-shadow: 0 0 20px rgba(200, 255, 0, 0.15);
      }
      #pricingComingSoonModal .modal-title {
        font-family: var(--font, 'Outfit', sans-serif);
        font-size: 1.6rem; font-weight: 800;
        color: var(--text, #0f172a);
        margin-bottom: 8px;
        background: linear-gradient(135deg, #3b82f6, #10b981);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      #pricingComingSoonModal .modal-badge {
        display: inline-block;
        font-family: var(--mono, monospace);
        font-size: 0.6rem; font-weight: 700;
        color: #3b82f6;
        background: rgba(59, 130, 246, 0.1);
        border: 1px solid rgba(59, 130, 246, 0.2);
        padding: 3px 12px; border-radius: 100px;
        text-transform: uppercase; letter-spacing: 0.1em;
        margin-bottom: 20px;
      }
      html[data-theme="dark"] #pricingComingSoonModal .modal-badge {
        color: #c8ff00;
        background: rgba(200, 255, 0, 0.08);
        border-color: rgba(200, 255, 0, 0.2);
      }
      #pricingComingSoonModal .modal-sub {
        font-family: var(--font, 'Outfit', sans-serif);
        font-size: 0.95rem; color: var(--text2, #475569);
        line-height: 1.6; margin-bottom: 28px;
      }
      html[data-theme="dark"] #pricingComingSoonModal .modal-sub {
        color: #94a3b8;
      }
      #pricingComingSoonModal .btn-primary {
        background: #1e40af;
        color: #ffffff !important;
        box-shadow: 0 4px 12px rgba(30, 64, 175, 0.2);
        width: 100%; min-height: 46px;
        border-radius: 10px; font-weight: 600;
        font-size: 0.9rem;
        cursor: pointer; transition: all 0.2s ease; border: 1px solid transparent;
      }
      #pricingComingSoonModal .btn-primary:hover {
        background: #1e3a8a;
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(30, 64, 175, 0.3);
      }
      html[data-theme="dark"] #pricingComingSoonModal .btn-primary {
        background: #c8ff00;
        color: #0a0a0a !important;
        box-shadow: 0 4px 12px rgba(200, 255, 0, 0.25);
      }
      html[data-theme="dark"] #pricingComingSoonModal .btn-primary:hover {
        background: #c8ff00;
        opacity: 0.95;
        box-shadow: 0 6px 16px rgba(200, 255, 0, 0.4);
      }
    `;
    document.head.appendChild(modalStyle);

    modal = document.createElement('div');
    modal.id = 'pricingComingSoonModal';
    modal.innerHTML = `
      <div class="modal">
        <button class="modal-close" onclick="window.closePricingModal()">✕</button>
        <div class="modal-icon-wrapper">
          <span style="font-size: 1.8rem; filter: drop-shadow(0 0 5px rgba(59, 130, 246, 0.4));">👑</span>
        </div>
        <div class="modal-title">Pricing Coming Soon</div>
        <div class="modal-badge">Free Beta Testing Phase</div>
        <p class="modal-sub">
          CYPR premium subscription plans are currently under development. To support our starting community, <b>all advanced scanners and security checks are 100% free and unlimited</b> during this beta testing period!
        </p>
        <button class="btn btn-primary" onclick="window.closePricingModal()">Start Scanning — Free</button>
      </div>
    `;

    document.body.appendChild(modal);
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('open'), 10);
  }

  window.closePricingModal = function() {
    const modal = document.getElementById('pricingComingSoonModal');
    if (modal) {
      modal.classList.remove('open');
      setTimeout(() => {
        modal.style.display = 'none';
      }, 250);
    }
  };

  window.showPricingComingSoonModal = showPricingComingSoonModal;

  // Intercept all pricing link clicks globally
  document.addEventListener('click', (e) => {
    const target = e.target.closest('a');
    if (target && target.getAttribute('href') === 'pricing.html') {
      e.preventDefault();
      e.stopPropagation();
      showPricingComingSoonModal();
    }
  });

})();
