import { state } from '../state/store.js';
import { toast, openConf, closeConf } from '../utils/ui.js';

export function initSecurityCenter() {
  renderActivityLogs();
  renderSecurityLogs();
  renderThreatLogs();
  renderFraudAlerts();
  renderBlacklist();
}

export async function renderActivityLogs() {
  const tbody = document.getElementById('actLogTbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="5"><div class="skeleton skeleton-block skeleton-w-full skeleton-h-12" style="margin-bottom:8px;"></div><div class="skeleton skeleton-block skeleton-w-full skeleton-h-12"></div></td></tr>';
  
  const qEl = document.getElementById('actLogSrch');
  const modEl = document.getElementById('actLogMod');
  const sevEl = document.getElementById('actLogSev');
  
  const q = new URLSearchParams({
    search: qEl ? qEl.value.toLowerCase() : '',
    module: modEl ? modEl.value : '',
    severity: sevEl ? sevEl.value : ''
  }).toString();
  
  try {
    const data = await apiClient.get(`/api/admin/security/activity-logs?${q}`);
    if (!data.items || !data.items.length) {
      tbody.innerHTML = '<tr><td colspan="5"><div class="empty"><p>No activity logs found</p></div></td></tr>';
      return;
    }
    tbody.innerHTML = data.items.map(x => {
      const sevClass = x.severity === 'success' ? 'pro' : (x.severity === 'warning' ? 'free' : 'admin');
      return `
        <tr>
          <td style="font-size:0.75rem; color:var(--text3); font-family:var(--mono);">${x.time}</td>
          <td><strong>${x.admin}</strong></td>
          <td><span class="ubadge" style="background:var(--bg3); color:var(--text2);">${x.module}</span></td>
          <td>${x.desc}</td>
          <td><span class="ubadge ${sevClass}">${x.severity.toUpperCase()}</span></td>
        </tr>
      `;
    }).join('');
  } catch (err) {
    tbody.innerHTML = '<tr><td colspan="5"><div class="empty"><p>Failed to load activity logs</p></div></td></tr>';
  }
}

export function filterActivityLogs() {
  renderActivityLogs();
}

export function clearActivityLogs() {
  openConf('danger', 'Clear Activity Logs?', 'This will wipe the temporary activity logs index.', async () => {
    closeConf();
    try {
      await apiClient.delete('/api/admin/security/activity-logs');
      renderActivityLogs();
      toast('Activity logs cleared.', 'inf');
    } catch (err) {}
  });
}

export function renderSecurityLogs() {
  const tbody = document.getElementById('secLogTbody');
  const timeline = document.getElementById('secTimeline');
  if (!tbody) return;
  
  const qEl = document.getElementById('secLogSrch');
  const evtEl = document.getElementById('secLogEvent');
  const q = qEl ? qEl.value.toLowerCase() : '';
  const evt = evtEl ? evtEl.value : '';
  
  const filtered = state.securityLogs.filter(x => {
    const matchesQ = !q || x.user.toLowerCase().includes(q) || x.ip.includes(q);
    const matchesEvt = !evt || x.event === evt;
    return matchesQ && matchesEvt;
  });
  
  if (!filtered.length) {
    tbody.innerHTML = '<tr><td colspan="5"><div class="empty"><p>No security events found</p></div></td></tr>';
  } else {
    tbody.innerHTML = filtered.map(x => {
      const sevClass = x.severity === 'critical' ? 'admin' : (x.severity === 'warning' ? 'free' : 'pro');
      return `
        <tr>
          <td style="font-size:0.75rem; color:var(--text3); font-family:var(--mono);">${x.time}</td>
          <td><strong>${x.user}</strong></td>
          <td style="font-family:var(--mono);">${x.ip}</td>
          <td>${x.event}</td>
          <td><span class="ubadge ${sevClass}">${x.severity.toUpperCase()}</span></td>
        </tr>
      `;
    }).join('');
  }
  
  if (timeline) {
    timeline.innerHTML = state.securityLogs.slice(0, 3).map(x => {
      const dotType = x.severity === 'critical' ? 'critical' : (x.severity === 'warning' ? 'warning' : 'info');
      return `
        <div class="so-timeline-item">
          <div class="so-timeline-dot ${dotType}"></div>
          <div class="so-timeline-time">${x.time}</div>
          <div class="so-timeline-title">${x.event}</div>
          <div class="so-timeline-desc">${x.user} at IP ${x.ip}</div>
        </div>
      `;
    }).join('');
  }
}

export function filterSecurityLogs() {
  renderSecurityLogs();
}

export function renderThreatLogs() {
  const tbody = document.getElementById('threatTbody');
  if (!tbody) return;
  
  const qEl = document.getElementById('threatSrch');
  const vecEl = document.getElementById('threatVector');
  const q = qEl ? qEl.value.toLowerCase() : '';
  const vec = vecEl ? vecEl.value : '';
  
  const filtered = state.threatLogs.filter(x => {
    const matchesQ = !q || x.ip.includes(q);
    const matchesVec = !vec || x.vector === vec;
    return matchesQ && matchesVec;
  });
  
  if (!filtered.length) {
    tbody.innerHTML = '<tr><td colspan="6"><div class="empty"><p>No threat entries found</p></div></td></tr>';
    return;
  }
  
  tbody.innerHTML = filtered.map(x => {
    const sevClass = x.severity === 'critical' ? 'admin' : (x.severity === 'high' ? 'free' : 'pro');
    return `
      <tr>
        <td><strong style="color:var(--text); font-family:var(--mono);">${x.ip}</strong></td>
        <td>${x.vector}</td>
        <td><code style="font-family:var(--mono); font-size:0.7rem; color:var(--text2);">${x.route}</code></td>
        <td><span class="ubadge ${sevClass}">${x.severity.toUpperCase()}</span></td>
        <td style="font-size:0.75rem; color:var(--text3);">${x.time}</td>
        <td><button class="btn btn-s btn-sm" onclick="ignoreThreat('${x.id}', '${x.ip}')">Allow IP</button></td>
      </tr>
    `;
  }).join('');
}

export function filterThreatLogs() {
  renderThreatLogs();
}

export function ignoreThreat(id, ip) {
  openConf('warn', 'Allow Blocked IP?', `Authorize traffic and unblock host <strong>${ip}</strong>?`, async () => {
    closeConf();
    try {
      await apiClient.post(`/api/admin/security/threats/${id}/allow`);
      renderThreatLogs();
      toast(`IP ${ip} allowed.`, 'ok');
    } catch (err) {}
  });
}

export function renderFraudAlerts() {
  const tbody = document.getElementById('fraudTbody');
  if (!tbody) return;
  
  const qEl = document.getElementById('fraudSrch');
  const typeEl = document.getElementById('fraudType');
  const q = qEl ? qEl.value.toLowerCase() : '';
  const type = typeEl ? typeEl.value : '';
  
  const filtered = state.fraudAlerts.filter(x => {
    const matchesQ = !q || x.user.toLowerCase().includes(q);
    const matchesType = !type || x.type === type;
    return matchesQ && matchesType;
  });
  
  if (!filtered.length) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty"><p>No risk alerts found</p></div></td></tr>';
    return;
  }
  
  tbody.innerHTML = filtered.map(x => {
    const sevClass = x.severity === 'critical' ? 'admin' : (x.severity === 'high' ? 'free' : 'pro');
    return `
      <tr>
        <td style="font-size:0.75rem; color:var(--text3);">${x.time}</td>
        <td><strong>${x.user}</strong></td>
        <td><strong style="color:var(--red); font-family:var(--mono);">${x.score}</strong></td>
        <td>${x.type}</td>
        <td>${x.geo}</td>
        <td><span class="ubadge ${sevClass}">${x.severity.toUpperCase()}</span></td>
        <td>
          <div style="display:flex; gap:6px;">
            <button class="btn btn-d btn-sm" onclick="suspendFraudUser('${x.user}')">Lock Account</button>
            <button class="btn btn-s btn-sm" onclick="dismissFraud('${x.id}', '${x.user}')">Dismiss</button>
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

export function filterFraudAlerts() {
  renderFraudAlerts();
}

export function suspendFraudUser(name) {
  openConf('danger', 'Lock Fraudulent User?', `Suspend <strong>${name}</strong> immediately under risk rules?`, async () => {
    closeConf();
    try {
      await apiClient.post(`/api/admin/users/fraud-suspend`, { username: name });
      toast(`${name} locked.`, 'inf');
    } catch (err) {}
  });
}

export async function dismissFraud(id, user) {
  try {
    await apiClient.delete(`/api/admin/security/fraud-alerts/${id}`);
    renderFraudAlerts();
    toast('Risk alert dismissed.', 'ok');
  } catch(err) {}
}

export function renderBlacklist() {
  const tbody = document.getElementById('blTbody');
  if (!tbody) return;
  
  const qEl = document.getElementById('blSrch');
  const q = qEl ? qEl.value.toLowerCase() : '';
  const filtered = state.blacklistRules.filter(x => !q || x.pattern.toLowerCase().includes(q) || x.reason.toLowerCase().includes(q));
  
  if (!filtered.length) {
    tbody.innerHTML = '<tr><td colspan="6"><div class="empty"><p>No blacklist entries found</p></div></td></tr>';
    return;
  }
  
  tbody.innerHTML = filtered.map(x => {
    const sevClass = x.severity === 'critical' ? 'admin' : (x.severity === 'high' ? 'free' : 'pro');
    return `
      <tr>
        <td><strong style="color:var(--text); font-family:var(--mono);">${x.pattern}</strong></td>
        <td><span class="ubadge" style="background:var(--bg3); color:var(--text2);">${x.scope}</span></td>
        <td>${x.reason}</td>
        <td><span class="ubadge ${sevClass}">${x.severity.toUpperCase()}</span></td>
        <td style="font-size:0.75rem; color:var(--text3);">${x.date}</td>
        <td><button class="btn btn-d btn-sm" onclick="removeBlacklist('${x.id}', '${x.pattern}')">Unblock</button></td>
      </tr>
    `;
  }).join('');
}

export function filterBlacklist() {
  renderBlacklist();
}

export async function addBlacklistRule() {
  const patEl = document.getElementById('blPattern');
  const scopeEl = document.getElementById('blScope');
  const reasonEl = document.getElementById('blReason');
  if (!patEl || !scopeEl) return;

  const pat = patEl.value.trim();
  const scope = scopeEl.value;
  const reason = reasonEl && reasonEl.value.trim() ? reasonEl.value.trim() : 'Manual administrator block';
  
  if (!pat) {
    toast('Please specify subnet range or email domain.', 'err');
    return;
  }
  
  try {
    await apiClient.post('/api/admin/security/blacklist', { pattern: pat, scope, reason });
    renderBlacklist();
    patEl.value = '';
    if (reasonEl) reasonEl.value = '';
    toast(`Firewall filter applied for ${pat}!`, 'ok');
  } catch (err) {}
}

export function removeBlacklist(id, pat) {
  openConf('warn', 'Remove Restriction Rule?', `Unblock routing for <strong>${pat}</strong>?`, async () => {
    closeConf();
    try {
      await apiClient.delete(`/api/admin/security/blacklist/${id}`);
      renderBlacklist();
      toast(`Unblocked ${pat}.`, 'ok');
    } catch (err) {}
  });
}

export async function addLog(type, msg, module = 'General') {
  try {
    await apiClient.post('/api/admin/security/activity-logs', { type, msg, module });
    renderActivityLogs();
  } catch (err) {}
}
