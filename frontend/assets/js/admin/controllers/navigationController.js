import { toast } from '../utils/ui.js';
// To avoid circular dependencies, we'll dispatch an event or rely on global window.loadUsers
// Since `admin.html` uses global handlers, we'll attach `loadUsers` to window in main.js.

export function go(id) {
  document.querySelectorAll('.panel').forEach(p => p.classList.remove('on'));
  document.querySelectorAll('.sb-item').forEach(s => s.classList.remove('on'));
  
  const targetPanel = document.getElementById('panel-' + id);
  const targetItem = document.getElementById('si-' + id);
  
  if (targetPanel) targetPanel.classList.add('on');
  if (targetItem) targetItem.classList.add('on');
  
  expandGroupOf(id);

  const map = {
    overview: 'Dashboard',
    users: 'User Directory',
    'users-rbac': 'Roles & RBAC',
    'comm-campaigns': 'Campaign Composer',
    'comm-templates': 'Template Manager',
    'comm-announcements': 'Announcement Center',
    'comm-history': 'Dispatch Ledger',
    'sec-activity-logs': 'Activity Logs',
    'sec-security-logs': 'Security Logs',
    'sec-threats': 'Threat Detection',
    'sec-fraud': 'Fraud Detection',
    'sec-blacklist': 'Blacklist',
    'analytics-business': 'Business Metrics',
    'analytics-users': 'User Intelligence',
    'analytics-ops': 'Operational Metrics',
    'sys-infra': 'Infrastructure Health',
    'sys-api': 'API & Queue Telemetry',
    'sys-controls': 'Platform Controls',
    'billing-overview': 'Billing & Revenue Overview',
    'billing-ledger': 'Transactions & Invoices Ledger',
    'billing-coupons': 'Discount Coupons Manager',
    'support-tickets': 'Helpdesk Support Tickets',
    'dev-keys': 'API Secret Keys',
    'dev-webhooks': 'Webhooks Configuration',
    'dev-integrations': 'Third-Party Integrations',
    'dev-api-logs': 'API Usage Logs',
    'dev-request-logs': 'Request Inspector',
    'dev-uikit': 'UI Components Library',
    'sys-404': 'Page Not Found (404)',
    'sys-denied': 'Access Restricted (403)',
    'backups-history': 'Backup History',
    'backups-restore': 'Restore Center',
    'backups-schedules': 'Backup Schedules',
    'backups-storage': 'Storage Usage',
    'backups-downloads': 'Backup Downloads',
    'settings-config': 'Platform Configurations'
  };

  const titleEl = document.getElementById('breadcrumb-title');
  if (titleEl && map[id]) {
    titleEl.textContent = map[id];
  }

  if (window.innerWidth <= 768) {
    const sb = document.querySelector('.adm-sidebar');
    const ov = document.getElementById('sidebarOverlay');
    if (sb) sb.classList.remove('open');
    if (ov) ov.classList.remove('show');
  }

  if (id === 'users' && window.loadUsers) {
    window.loadUsers();
  }
}

export function toggleSidebarDrawer() {
  const sb = document.querySelector('.adm-sidebar');
  const ov = document.getElementById('sidebarOverlay');
  if (sb && ov) {
    const isOpen = sb.classList.toggle('open');
    if (isOpen) ov.classList.add('show');
    else ov.classList.remove('show');
  }
}

export function toggleGroup(groupName) {
  const group = document.getElementById('sbg-' + groupName);
  if (group) group.classList.toggle('expanded');
}

export function expandGroupOf(id) {
  const item = document.getElementById('si-' + id);
  if (item) {
    const group = item.closest('.sb-group');
    if (group) group.classList.add('expanded');
  }
}

export function filterSidebarMenus(query) {
  const q = query.toLowerCase().trim();
  const groups = document.querySelectorAll('.sb-group');
  groups.forEach(group => {
    let groupMatches = false;
    const items = group.querySelectorAll('.sb-item');
    items.forEach(item => {
      const txt = item.textContent.toLowerCase();
      if (txt.includes(q)) {
        item.style.display = 'flex';
        groupMatches = true;
      } else {
        item.style.display = 'none';
      }
    });

    const headerText = group.querySelector('.sb-group-hdr span').textContent.toLowerCase();
    if (headerText.includes(q)) {
      groupMatches = true;
      items.forEach(item => item.style.display = 'flex');
    }

    if (groupMatches) {
      group.style.display = 'flex';
      if (q.length > 0) {
        group.classList.add('expanded');
      }
    } else {
      group.style.display = 'none';
    }
  });
}

export function toggleThemeLocal() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('cypr_theme', next);
  toast(`Theme toggled to ${next} mode.`, 'inf');
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
