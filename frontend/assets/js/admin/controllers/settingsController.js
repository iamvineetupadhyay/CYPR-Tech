import { toast } from '../utils/ui.js';

// ── Backup Manager ──
export function triggerManualBackup() {
  toast('Manual backup initiated. Snapshot job queued...', 'inf');
  setTimeout(() => toast('Backup snapshot completed: snapshot_indcie_prod_manual_' + Date.now() + '.sql.gz (124.8 MB)', 'ok'), 3500);
}

export function filterBackupHistory(type) {
  const rows = document.querySelectorAll('#bkHistoryTbody tr');
  rows.forEach(r => {
    r.style.display = (type === 'all' || r.dataset.type === type) ? '' : 'none';
  });
}

export function selectSnap(el, name, date, size, status) {
  document.querySelectorAll('.bk-snap-card').forEach(c => {
    c.style.border = '1px solid var(--bdr)';
    c.style.background = '';
    const badge = c.querySelector('.ubadge');
    if (badge && badge.textContent === '✓ Selected') {
      const t = c.dataset.type || '';
      badge.className = 'ubadge';
      badge.style = 'background:rgba(99,102,241,.1);color:#818cf8;border:1px solid rgba(99,102,241,.2);font-size:0.68rem';
      badge.textContent = 'Auto';
    }
  });
  el.style.border = '1.5px solid var(--accent)';
  el.style.background = 'rgba(99,102,241,.08)';
  const badge = el.querySelector('.ubadge');
  if (badge) { badge.className = 'ubadge pro'; badge.textContent = '✓ Selected'; badge.style = ''; }
  const snapNameEl = document.getElementById('restoreSnapName');
  const dateEl = document.getElementById('restoreDate');
  const sizeEl = document.getElementById('restoreSize');
  if (snapNameEl) snapNameEl.textContent = name;
  if (dateEl) dateEl.textContent = date;
  if (sizeEl) sizeEl.textContent = size;
}

export function initiateRestore() {
  const chk = document.getElementById('restoreConfirmChk');
  if (!chk || !chk.checked) {
    toast('Please confirm you understand this is a destructive operation.', 'err');
    return;
  }
  const modeEl = document.getElementById('restoreMode');
  const mode = modeEl ? modeEl.value : 'Full Database Restore';
  const wrap = document.getElementById('restoreProgressWrap');
  const bar = document.getElementById('restoreProgressBar');
  const lbl = document.getElementById('restoreProgressLbl');
  const pct = document.getElementById('restoreProgressPct');
  if (!wrap || !bar || !lbl || !pct) return;
  wrap.style.display = 'block';
  const steps = [
    { p: 10, l: 'Verifying snapshot integrity...' },
    { p: 25, l: 'Stopping write operations...' },
    { p: 45, l: 'Restoring schema...' },
    { p: 65, l: 'Restoring data (tables)...' },
    { p: 80, l: 'Rebuilding indexes...' },
    { p: 95, l: 'Running integrity checks...' },
    { p: 100, l: 'Restore complete!' }
  ];
  let i = 0;
  const run = () => {
    if (i >= steps.length) return;
    const s = steps[i++];
    bar.style.width = s.p + '%';
    lbl.textContent = s.l;
    pct.textContent = s.p + '%';
    if (s.p === 100) { toast('Database restored successfully!', 'ok'); chk.checked = false; }
    else setTimeout(run, 700);
  };
  toast('Restore initiated. This may take a few minutes.', 'warn');
  run();
}

export function generateDownloadLink() {
  const expiryEl = document.getElementById('dlLinkExpiry');
  const expiry = expiryEl ? expiryEl.value : '24 hours';
  const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
  let token = 'dl_tk_';
  for (let i = 0; i < 24; i++) token += chars[Math.floor(Math.random() * chars.length)];
  const url = `https://cdn.indcie.io/backups/secure/${token}`;
  const out = document.getElementById('dlLinkOutput');
  const val = document.getElementById('dlLinkValue');
  if (out && val) {
    val.textContent = url;
    out.style.display = 'block';
  }
  toast(`Secure link generated. Expires in ${expiry}.`, 'ok');
}

export function copyDlLink() {
  const valEl = document.getElementById('dlLinkValue');
  const v = valEl ? valEl.textContent : '';
  if (v && navigator.clipboard) navigator.clipboard.writeText(v);
  toast('Download link copied to clipboard.', 'ok');
}

// ── Settings Manager ──
export function switchSettingsTab(tab, btn) {
  document.querySelectorAll('.settings-subpanel').forEach(p => p.style.display = 'none');
  
  const activeSubpanel = document.getElementById('sett-' + tab);
  if (activeSubpanel) {
    activeSubpanel.style.display = activeSubpanel.classList.contains('card') ? 'block' : 'grid';
  }
  
  document.querySelectorAll('.tabs-nav .tab-btn').forEach(b => {
    b.classList.remove('active');
    b.style.background = 'transparent';
    b.style.color = 'var(--text2)';
  });
  
  if (btn) {
    btn.classList.add('active');
    btn.style.background = 'var(--accent)';
    btn.style.color = '#fff';
  }
}

export function setPresetColor(color, el) {
  const picker = document.getElementById('brandColorPicker');
  if (picker) picker.value = color;
  
  document.querySelectorAll('#colorPresets span').forEach(s => s.style.borderColor = 'transparent');
  if (el) el.style.borderColor = '#fff';
  
  document.documentElement.style.setProperty('--accent', color);
  toast(`Accent color changed to ${color}`, 'ok');
}

export function togglePasswordVisibility(inputId) {
  const input = document.getElementById(inputId);
  if (!input) return;
  input.type = input.type === 'password' ? 'text' : 'password';
  toast(`${input.type === 'text' ? 'Visible' : 'Hidden'} secret value.`, 'info');
}

export function testSmtpConnection() {
  const recipientEl = document.getElementById('smtpTestRecipient');
  const recipient = recipientEl ? recipientEl.value.trim() : '';
  if (!recipient) {
    toast('Please specify a recipient email address.', 'err');
    return;
  }
  const hostEl = document.getElementById('smtpHost');
  const host = hostEl ? hostEl.value : 'localhost';
  toast(`Connecting to ${host}...`, 'inf');
  setTimeout(() => {
    toast(`SMTP Handshake Successful. Outbound test mail dispatched to ${recipient}`, 'ok');
  }, 1500);
}

export function rotateJwtSecret() {
  const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#';
  let secret = 'sec_key_';
  for (let i = 0; i < 24; i++) secret += chars[Math.floor(Math.random() * chars.length)];
  const input = document.getElementById('secJwtSecret');
  if (input) input.value = secret;
  toast('Global JWT signature secret rotated.', 'ok');
}

export function selectStorageDriver(driver) {
  document.querySelectorAll('.st-driver-card').forEach(c => {
    c.classList.remove('selected-drv');
    c.style.border = '1px solid var(--bdr)';
    c.style.background = '';
  });
  
  const selected = document.getElementById('drv-' + driver);
  if (selected) {
    selected.classList.add('selected-drv');
    selected.style.border = '1.5px solid var(--accent)';
    selected.style.background = 'rgba(99,102,241,.08)';
  }
  
  const s3Block = document.getElementById('awsS3ConfigBlock');
  if (s3Block) {
    s3Block.style.display = (driver === 's3' || driver === 'r2') ? 'block' : 'none';
  }
  
  toast(`Storage driver switched to ${driver.toUpperCase()}`, 'info');
}

export function saveSettings(name) {
  toast(`${name} saved successfully!`, 'ok');
}

// ── UI KIT & REUSABLE COMPONENTS ──
export function openDemoModal() {
  const m = document.getElementById('demoModal');
  if (m) m.style.display = 'flex';
}
export function closeDemoModal() {
  const m = document.getElementById('demoModal');
  if (m) m.style.display = 'none';
}

export function openDemoConfirmDialog() {
  const d = document.getElementById('demoConfirmDialog');
  if (d) d.style.display = 'flex';
}
export function closeDemoConfirmDialog() {
  const d = document.getElementById('demoConfirmDialog');
  if (d) d.style.display = 'none';
}

export function toggleDemoDropdown(event) {
  event.stopPropagation();
  const list = document.getElementById('demoDropdownList');
  if (!list) return;
  list.style.display = list.style.display === 'none' ? 'block' : 'none';
}

export function selectDemoDropdownValue(val) {
  const display = document.getElementById('demoDropdownSelectedValue');
  if (display) display.textContent = val;
  const list = document.getElementById('demoDropdownList');
  if (list) list.style.display = 'none';
  toast(`Selected Filter: ${val}`, 'info');
}

export function switchDemoTab(tab, btn) {
  document.querySelectorAll('.demotab-content').forEach(c => c.style.display = 'none');
  const activeContent = document.getElementById('demotab-' + tab);
  if (activeContent) activeContent.style.display = 'block';

  document.querySelectorAll('#panel-dev-uikit .tab-btn').forEach(b => {
    b.classList.remove('active');
    b.style.background = 'transparent';
    b.style.color = 'var(--text2)';
  });
  if (btn) {
    btn.classList.add('active');
    btn.style.background = 'var(--accent)';
    btn.style.color = '#fff';
  }
}

export function executeCtxAction(action) {
  if (action === 'copy') {
    navigator.clipboard.writeText('node_token_7182fb10eac88a');
    toast('Copied token hash node_token_7182fb10eac88a to clipboard.', 'ok');
  } else if (action === 'reload') {
    toast('Refreshing local telemetry node state...', 'info');
  } else if (action === 'delete') {
    toast('Initiated safe deletion of nodes. Node marked.', 'warn');
  }
}

export function initColorPicker() {
  const picker = document.getElementById('brandColorPicker');
  if (picker) {
    picker.addEventListener('input', (e) => {
      document.documentElement.style.setProperty('--accent', e.target.value);
      document.querySelectorAll('#colorPresets span').forEach(s => s.style.borderColor = 'transparent');
    });
  }
}
