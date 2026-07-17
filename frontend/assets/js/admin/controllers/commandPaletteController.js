import { toast } from '../utils/ui.js';
import { go } from './navigationController.js';
import { closeDemoModal, closeDemoConfirmDialog } from './settingsController.js';

const cmdPaletteList = [
  { name: '🌐 Go to Main Overview Dashboard', action: () => go('overview') },
  { name: '👥 Go to User Directory Manager', action: () => go('users') },
  { name: '⚙️ Go to Platforms Settings & SMTP', action: () => go('settings-config') },
  { name: '🛡️ Go to Threat Detection Dashboard', action: () => go('sec-threats') },
  { name: '💳 Go to Billing Ledger & Invoices', action: () => go('billing-ledger') },
  { name: '💾 Go to Backup Snapshots History', action: () => go('backups-history') },
  { name: '🔑 Generate sandbox/live API Secret Key', action: () => { go('dev-keys'); toast('Redirected. Click Generate to complete.', 'info'); } },
  { name: '🔔 Trigger Reusable Alert Toast (Success)', action: () => toast('Success! Component library operational.', 'ok') },
  { name: '🚨 Trigger Telemetry Error Status Toast', action: () => toast('Critical Error: Failed response codes.', 'err') }
];

export function toggleCommandPalette(visible) {
  const pal = document.getElementById('cmdPalette');
  if (!pal) return;
  pal.style.display = visible ? 'flex' : 'none';
  if (visible) {
    const input = document.getElementById('cmdInput');
    if (input) {
      input.value = '';
      input.focus();
    }
    renderCommandPaletteResults('');
  }
}

export function filterCommandPalette() {
  const query = document.getElementById('cmdInput')?.value || '';
  renderCommandPaletteResults(query);
}

export function renderCommandPaletteResults(query) {
  const container = document.getElementById('cmdResults');
  if (!container) return;
  
  const cleanQuery = query.toLowerCase().trim();
  const filtered = cmdPaletteList.filter(c => c.name.toLowerCase().includes(cleanQuery));
  
  if (filtered.length === 0) {
    container.innerHTML = `<div style="padding:16px;text-align:center;color:var(--text2);font-size:0.75rem;">No matching commands found</div>`;
    return;
  }
  
  container.innerHTML = filtered.map((c, idx) => {
    return `
      <div class="cmd-item" onclick="executeCmdItemByIndex(${cmdPaletteList.indexOf(c)})" style="padding:10px 16px;font-size:0.8rem;color:var(--text);cursor:pointer;display:flex;align-items:center;transition:.15s;font-weight:500;">
        ${c.name}
      </div>
    `;
  }).join('');
}

export function executeCmdItemByIndex(idx) {
  const item = cmdPaletteList[idx];
  if (item) {
    item.action();
    toggleCommandPalette(false);
  }
}

export function initCommandPalette() {
  // Add shortcut listener for Ctrl+K
  document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
      e.preventDefault();
      toggleCommandPalette(true);
    }
    if (e.key === 'Escape') {
      toggleCommandPalette(false);
      closeDemoModal();
      closeDemoConfirmDialog();
    }
  });

  // Reusable A11y keyboard Tab-outline tracker
  window.addEventListener('keydown', (e) => {
    if (e.key === 'Tab') {
      document.body.classList.add('user-is-tabbing');
    }
  });
  window.addEventListener('mousedown', () => {
    document.body.classList.remove('user-is-tabbing');
  });
}
