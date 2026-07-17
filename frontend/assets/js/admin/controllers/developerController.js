import { state } from '../state/store.js';
import { toast, openConf, closeConf } from '../utils/ui.js';

export function initDeveloper() {
  renderApiKeys();
  renderWebhooks();
  renderWebhookDeliveries();
  renderApiLogs();
  renderRequestList();
  
  // Auto-select the first request on load
  const firstRow = document.querySelector('#reqListTbody tr');
  if (firstRow) {
    firstRow.click();
  }

  // Set up dynamic connection badges for Integrations
  document.querySelectorAll('.integration-card').forEach(card => {
    const checkbox = card.querySelector('.tog-inp');
    const name = card.querySelector('strong').textContent;
    if (checkbox) {
      checkbox.addEventListener('change', () => {
        const badge = card.querySelector('.ubadge');
        if (checkbox.checked) {
          badge.className = 'ubadge pro';
          badge.textContent = 'Connected';
          badge.style = '';
          toast(`${name} connected successfully.`, 'ok');
        } else {
          badge.className = 'ubadge';
          badge.textContent = 'Disconnected';
          badge.style = 'background:rgba(107,114,128,.1);color:#9ca3af;border:1px solid rgba(107,114,128,.2)';
          toast(`${name} disconnected.`, 'warn');
        }
      });
    }
  });
}

export function renderApiKeys() {
  const tbody = document.getElementById('apiKeysTbody');
  if (!tbody) return;
  tbody.innerHTML = state.apiKeys.map((k, idx) => {
    const maskedToken = k.token.length > 16 
      ? `${k.token.slice(0, 8)}_••••••••••••${k.token.slice(-4)}`
      : k.token;
    const envClass = k.env === 'Live' ? 'ubadge pro' : (k.env === 'Sandbox' ? 'ubadge info' : 'ubadge warning');
    const badgeStyle = k.env === 'Sandbox' 
      ? 'background:rgba(99,102,241,.1);color:#818cf8;border:1px solid rgba(99,102,241,.2)'
      : (k.env === 'Test' ? 'background:rgba(245,158,11,.1);color:#f59e0b;border:1px solid rgba(245,158,11,.2)' : '');
    
    return `
      <tr>
        <td><strong style="color:var(--text)">${k.alias}</strong></td>
        <td><code style="font-family:var(--mono);color:var(--text);font-size:0.72rem;">${maskedToken}</code></td>
        <td><span class="${envClass}" style="${badgeStyle}">${k.env}</span></td>
        <td><span style="font-family:var(--mono);font-size:0.65rem;color:var(--text2)">${k.scopes}</span></td>
        <td>${k.lastUsed}</td>
        <td>${k.expires}</td>
        <td><div class="ustatus"><div class="udot ${k.status === 'Active' ? 'active' : ''}"></div>${k.status}</div></td>
        <td style="display:flex;gap:6px;">
          <button class="btn btn-s btn-sm" onclick="rotateKey(${idx})">Rotate</button>
          <button class="btn btn-d btn-sm" onclick="revokeKey(${idx})">${k.status === 'Active' ? 'Revoke' : 'Delete'}</button>
        </td>
      </tr>
    `;
  }).join('');
}

export async function generateApiKey() {
  const aliasEl = document.getElementById('newKeyAlias');
  const alias = aliasEl ? aliasEl.value.trim() : '';
  if (!alias) { toast('Please enter a key alias.', 'err'); return; }
  
  const envSelect = document.getElementById('newKeyEnv');
  const env = envSelect ? envSelect.value : 'Live';
  const scopesEl = document.getElementById('newKeyScopes');
  const scopes = scopesEl ? scopesEl.value.trim() : 'read:threats';
  const expiryEl = document.getElementById('newKeyExpiry');
  const expiry = expiryEl ? expiryEl.value : 'Never';
  
  try {
    const resp = await apiClient.post('/api/admin/developer/keys', { alias, env, scopes, expiry });
    toast('API key generated! Save this token now — it will not be shown again.', 'ok');
    
    // For now we simulate reloading the list if the backend is not returning the full list yet
    renderApiKeys();
    
    const nkval = document.getElementById('newKeyValue');
    if (nkval) nkval.textContent = resp.token; // Must return actual token just once
    const nkout = document.getElementById('newKeyOutput');
    if (nkout) nkout.style.display = 'block';
  } catch(err) {}
}

export function copyNewKey() {
  const v = document.getElementById('newKeyValue').textContent;
  if (navigator.clipboard) navigator.clipboard.writeText(v);
  toast('API key copied to clipboard.', 'ok');
}

export function filterApiKeys(q) {
  const rows = document.querySelectorAll('#apiKeysTbody tr');
  rows.forEach(r => {
    r.style.display = r.textContent.toLowerCase().includes(q.toLowerCase()) ? '' : 'none';
  });
}

export async function rotateKey(idx) {
  const k = state.apiKeys[idx];
  if (!k || !k.id) return;
  try {
    const res = await apiClient.post(`/api/admin/developer/keys/${k.id}/rotate`);
    k.token = res.token;
    k.lastUsed = 'Just now';
    renderApiKeys();
    toast(`API Key "${k.alias}" rotated. New token is: ${k.token.slice(0, 12)}... (Copied to clipboard)`, 'ok');
    if (navigator.clipboard) navigator.clipboard.writeText(k.token);
  } catch (err) {}
}

export async function revokeKey(idx) {
  const k = state.apiKeys[idx];
  if (!k || !k.id) return;
  try {
    if (k.status === 'Active') {
      await apiClient.post(`/api/admin/developer/keys/${k.id}/revoke`);
      k.status = 'Revoked';
      toast(`API Key "${k.alias}" revoked.`, 'warn');
    } else {
      await apiClient.delete(`/api/admin/developer/keys/${k.id}`);
      state.apiKeys.splice(idx, 1);
      toast(`API Key "${k.alias}" permanently deleted.`, 'warn');
    }
    renderApiKeys();
  } catch(err) {}
}

export function renderWebhooks() {
  const tbody = document.getElementById('webhooksTbody');
  if (!tbody) return;
  tbody.innerHTML = state.webhooks.map((w, idx) => {
    const isFailing = w.status === 'Failing';
    const isAct = w.status === 'Active';
    return `
      <tr>
        <td><code style="font-family:var(--mono);font-size:0.72rem;color:var(--text)">${w.url}</code></td>
        <td><span style="font-size:0.68rem;color:var(--text2)">${w.events}</span></td>
        <td><span style="color:${isFailing ? '#ef4444' : '#10b981'};font-weight:600">${w.rate}</span></td>
        <td>${w.lastDelivery}</td>
        <td><div class="ustatus"><div class="udot" style="${isFailing ? 'background:#ef4444;box-shadow:0 0 6px rgba(239,68,68,.5)' : (isAct ? 'background:#10b981;box-shadow:0 0 6px rgba(16,185,129,.5)' : 'background:#6b7280')}"></div>${w.status}</div></td>
        <td style="display:flex;gap:6px;">
          <button class="btn btn-s btn-sm" onclick="pingWebhook(${idx})">Ping</button>
          <button class="btn btn-d btn-sm" onclick="toggleWebhook(${idx})">${w.status === 'Active' ? 'Disable' : 'Enable'}</button>
          <button class="btn btn-d btn-sm" onclick="deleteWebhook(${idx})" style="background:rgba(239,68,68,.12);color:#ef4444;border-color:transparent">Remove</button>
        </td>
      </tr>
    `;
  }).join('');
}

export async function registerWebhook() {
  const urlEl = document.getElementById('whUrl');
  const url = urlEl ? urlEl.value.trim() : '';
  if (!url) { toast('Please specify a destination URL.', 'err'); return; }
  
  const selectedEvents = [];
  const checkboxes = document.querySelectorAll('#whEventBoxes input[type="checkbox"]');
  checkboxes.forEach(cb => {
    if (cb.checked) {
      const labelText = cb.parentElement.textContent.trim();
      selectedEvents.push(labelText);
    }
  });
  
  if (selectedEvents.length === 0) {
    toast('Please subscribe to at least one event type.', 'err');
    return;
  }
  
  try {
    await apiClient.post('/api/admin/developer/webhooks', { url, events: selectedEvents.join(', ') });
    toast('Webhook registered successfully! Sending test ping...', 'ok');
    renderWebhooks();
    renderWebhookDeliveries();
  } catch(err) {}
}

export async function pingWebhook(idx) {
  const w = state.webhooks[idx];
  if (!w || !w.id) return;
  toast(`Sending test ping to ${w.url}...`, 'inf');
  try {
    await apiClient.post(`/api/admin/developer/webhooks/${w.id}/ping`);
    toast(`Ping successful! Response: 200 OK`, 'ok');
    renderWebhookDeliveries();
  } catch(err) {
    toast(`Ping failed: server returned error`, 'err');
    renderWebhookDeliveries();
  }
}

export async function toggleWebhook(idx) {
  const w = state.webhooks[idx];
  if (!w || !w.id) return;
  try {
    await apiClient.post(`/api/admin/developer/webhooks/${w.id}/toggle`);
    w.status = w.status === 'Active' ? 'Disabled' : 'Active';
    toast(`Webhook endpoint ${w.status.toLowerCase()}.`, w.status === 'Active' ? 'ok' : 'warn');
    renderWebhooks();
  } catch(err) {}
}

export async function deleteWebhook(idx) {
  const w = state.webhooks[idx];
  if (!w || !w.id) return;
  try {
    await apiClient.delete(`/api/admin/developer/webhooks/${w.id}`);
    state.webhooks.splice(idx, 1);
    toast(`Webhook ${w.url} removed.`, 'warn');
    renderWebhooks();
  } catch(err) {}
}

export function renderWebhookDeliveries() {
  const tbody = document.getElementById('whDeliveryTbody');
  if (!tbody) return;
  tbody.innerHTML = state.webhookDeliveries.map((d, idx) => {
    const isError = d.status !== '200 OK';
    return `
      <tr>
        <td><span style="font-family:var(--mono);font-size:0.72rem">${d.event}</span></td>
        <td style="font-size:0.72rem;color:var(--text2)">${d.endpoint}</td>
        <td><span class="ubadge ${isError ? 'warning' : 'pro'}" style="${isError ? 'background:rgba(239,68,68,.1);color:#ef4444;border:1px solid rgba(239,68,68,.2)' : ''}">${d.status}</span></td>
        <td>${d.latency}</td>
        <td>${d.time}</td>
        <td>
          ${d.retry ? `<button class="btn btn-s btn-sm" onclick="retryWebhookDelivery(${idx})">Retry</button>` : '—'}
        </td>
      </tr>
    `;
  }).join('');
}

export async function retryWebhookDelivery(idx) {
  const d = state.webhookDeliveries[idx];
  if (!d || !d.id) return;
  toast(`Retrying delivery of event "${d.event}"...`, 'inf');
  try {
    await apiClient.post(`/api/admin/developer/webhooks/deliveries/${d.id}/retry`);
    toast('Retry succeeded', 'ok');
    renderWebhookDeliveries();
  } catch(err) {}
}

export function renderApiLogs() {
  const tbody = document.getElementById('apiLogTbody');
  if (!tbody) return;
  
  const methEl = document.getElementById('apiLogMethod');
  const statEl = document.getElementById('apiLogStatus');
  const pathEl = document.getElementById('apiLogPath');
  const methodFilter = methEl ? methEl.value : 'All Methods';
  const statusFilter = statEl ? statEl.value : 'All Status';
  const pathFilter = pathEl ? pathEl.value.toLowerCase().trim() : '';
  
  const filtered = state.apiLogs.filter(l => {
    if (methodFilter !== 'All Methods' && l.method !== methodFilter) return false;
    if (statusFilter !== 'All Status') {
      if (statusFilter === '2xx' && !l.status.startsWith('2')) return false;
      if (statusFilter === '4xx' && !l.status.startsWith('4')) return false;
      if (statusFilter === '5xx' && !l.status.startsWith('5')) return false;
    }
    if (pathFilter && !l.path.toLowerCase().includes(pathFilter)) return false;
    return true;
  });
  
  tbody.innerHTML = filtered.map(l => {
    const isError = !l.status.startsWith('2');
    const badgeClass = l.method === 'GET' ? 'pro' : (l.method === 'POST' ? 'active' : 'warning');
    const badgeStyle = l.method === 'POST' ? 'background:rgba(16,185,129,.1);color:#10b981;border:1px solid rgba(16,185,129,.2)' : '';
    
    return `
      <tr>
        <td style="font-family:var(--mono);font-size:0.7rem">${l.time}</td>
        <td><span class="ubadge ${badgeClass}" style="${badgeStyle};font-family:var(--mono);font-size:0.7rem">${l.method}</span></td>
        <td style="font-family:var(--mono);font-size:0.72rem;color:var(--text)">${l.path}</td>
        <td style="font-size:0.7rem;color:var(--text2)">${l.key}</td>
        <td><span class="ubadge ${isError ? 'warning' : 'pro'}" style="${isError ? `background:rgba(239,68,68,.1);color:#ef4444;border:1px solid rgba(239,68,68,.2)` : ''}">${l.status}</span></td>
        <td>${l.latency}</td>
        <td style="font-family:var(--mono);font-size:0.7rem">${l.ip}</td>
      </tr>
    `;
  }).join('');
}

export function filterApiLogs() {
  renderApiLogs();
}

export function renderRequestList() {
  const tbody = document.getElementById('reqListTbody');
  if (!tbody) return;
  
  tbody.innerHTML = state.inspectorRequests.map((r, idx) => {
    const badgeClass = r.method === 'GET' ? 'pro' : (r.method === 'POST' ? 'active' : 'warning');
    const badgeStyle = r.method === 'POST' ? 'background:rgba(16,185,129,.1);color:#10b981;border:1px solid rgba(16,185,129,.2)' : '';
    const statusColorClass = r.status.startsWith('2') ? 'pro' : 'warning';
    const statusStyle = !r.status.startsWith('2') ? 'background:rgba(239,68,68,.1);color:#ef4444;border:1px solid rgba(239,68,68,.2)' : '';
    
    return `
      <tr class="req-row" onclick="showReqDetailByIndex(this, ${idx})" style="cursor:pointer">
        <td style="font-family:var(--mono);font-size:0.68rem">${r.time}</td>
        <td><span class="ubadge ${badgeClass}" style="${badgeStyle};font-family:var(--mono);font-size:0.68rem">${r.method}</span></td>
        <td style="font-family:var(--mono);font-size:0.7rem;color:var(--text)">${r.path}</td>
        <td><span class="ubadge ${statusColorClass}" style="${statusStyle}">${r.status}</span></td>
        <td>${r.latency}</td>
      </tr>
    `;
  }).join('');
}

export function showReqDetailByIndex(rowElement, idx) {
  const r = state.inspectorRequests[idx];
  showReqDetail(rowElement, r.method, r.path, r.status, r.latency, r.fullTime, r.ip, r.key, r.reqBody, r.resBody);
}

export function filterRequests() {
  const methodEl = document.getElementById('reqMethod');
  const statusEl = document.getElementById('reqStatus');
  const keyEl = document.getElementById('reqApiKey');
  const latEl = document.getElementById('reqMinLatency');
  
  const methodFilter = methodEl ? methodEl.value : 'All';
  const statusFilter = statusEl ? statusEl.value : 'All';
  const keyFilter = keyEl ? keyEl.value : 'all';
  const minLatency = latEl ? parseFloat(latEl.value) || 0 : 0;
  
  const tbody = document.getElementById('reqListTbody');
  if (!tbody) return;
  
  const filtered = state.inspectorRequests.filter(r => {
    if (methodFilter !== 'All' && r.method !== methodFilter) return false;
    if (statusFilter !== 'All' && r.status !== statusFilter) return false;
    if (keyFilter !== 'all' && !r.key.includes(keyFilter) && !(keyFilter === '—' && r.key === '—')) return false;
    if (minLatency) {
      const latVal = parseFloat(r.latency.replace('ms', '')) || 0;
      if (latVal < minLatency) return false;
    }
    return true;
  });
  
  tbody.innerHTML = filtered.map(r => {
    const idx = state.inspectorRequests.indexOf(r);
    const badgeClass = r.method === 'GET' ? 'pro' : (r.method === 'POST' ? 'active' : 'warning');
    const badgeStyle = r.method === 'POST' ? 'background:rgba(16,185,129,.1);color:#10b981;border:1px solid rgba(16,185,129,.2)' : '';
    const statusColorClass = r.status.startsWith('2') ? 'pro' : 'warning';
    const statusStyle = !r.status.startsWith('2') ? 'background:rgba(239,68,68,.1);color:#ef4444;border:1px solid rgba(239,68,68,.2)' : '';
    
    return `
      <tr class="req-row" onclick="showReqDetailByIndex(this, ${idx})" style="cursor:pointer">
        <td style="font-family:var(--mono);font-size:0.68rem">${r.time}</td>
        <td><span class="ubadge ${badgeClass}" style="${badgeStyle};font-family:var(--mono);font-size:0.68rem">${r.method}</span></td>
        <td style="font-family:var(--mono);font-size:0.7rem;color:var(--text)">${r.path}</td>
        <td><span class="ubadge ${statusColorClass}" style="${statusStyle}">${r.status}</span></td>
        <td>${r.latency}</td>
      </tr>
    `;
  }).join('');
  
  const firstRow = document.querySelector('#reqListTbody tr');
  if (firstRow) {
    firstRow.click();
  } else {
    document.getElementById('reqDetailContent').innerHTML = `
      <div style="text-align:center;padding:40px 0;color:var(--text2);font-size:0.82rem">
        No matching requests found
      </div>
    `;
  }
}

export function filterIntegrations(cat, btn) {
  document.querySelectorAll('.integration-card').forEach(c => {
    c.style.display = (cat === 'all' || c.dataset.cat === cat) ? '' : 'none';
  });
  document.querySelectorAll('#panel-dev-integrations button[onclick^="filterIntegrations"]').forEach(b => {
    b.classList.remove('btn-p');
    b.classList.add('btn-s');
  });
  if (btn) { btn.classList.remove('btn-s'); btn.classList.add('btn-p'); }
}

export function showReqDetail(row, method, path, status, latency, time, ip, keyAlias, reqBody, resBody) {
  document.querySelectorAll('.req-row').forEach(r => r.classList.remove('on-req-row'));
  row.classList.add('on-req-row');
  const statusColor = status.startsWith('2') ? '#10b981' : '#ef4444';
  const methodColors = { GET:'#6366f1',POST:'#10b981',PUT:'#f59e0b',PATCH:'#f59e0b',DELETE:'#ef4444' };
  const mc = methodColors[method] || '#9ca3af';
  const rdc = document.getElementById('reqDetailContent');
  if (!rdc) return;
  rdc.innerHTML = `
    <div style="display:flex;align-items:center;gap:10px;margin-bottom:14px;flex-wrap:wrap;">
      <span style="background:rgba(${method==='GET'?'99,102,241':method==='POST'?'16,185,129':method==='DELETE'?'239,68,68':'245,158,11'},.12);color:${mc};border:1px solid ${mc}30;padding:3px 10px;border-radius:6px;font-family:var(--mono);font-size:0.8rem;font-weight:700">${method}</span>
      <code style="font-family:var(--mono);font-size:0.78rem;color:var(--text);word-break:break-all">${path}</code>
    </div>
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:14px;">
      <div style="background:var(--bg2);border-radius:8px;padding:10px 12px;">
        <div style="font-size:0.65rem;color:var(--text2);margin-bottom:4px;text-transform:uppercase;letter-spacing:.06em">Status</div>
        <div style="font-weight:700;color:${statusColor};font-size:1rem">${status}</div>
      </div>
      <div style="background:var(--bg2);border-radius:8px;padding:10px 12px;">
        <div style="font-size:0.65rem;color:var(--text2);margin-bottom:4px;text-transform:uppercase;letter-spacing:.06em">Latency</div>
        <div style="font-weight:700;color:var(--text);font-size:1rem">${latency}</div>
      </div>
      <div style="background:var(--bg2);border-radius:8px;padding:10px 12px;">
        <div style="font-size:0.65rem;color:var(--text2);margin-bottom:4px;text-transform:uppercase;letter-spacing:.06em">Timestamp</div>
        <div style="font-size:0.8rem;color:var(--text);font-family:var(--mono)">${time}</div>
      </div>
      <div style="background:var(--bg2);border-radius:8px;padding:10px 12px;">
        <div style="font-size:0.65rem;color:var(--text2);margin-bottom:4px;text-transform:uppercase;letter-spacing:.06em">API Key</div>
        <div style="font-size:0.8rem;color:var(--text);font-family:var(--mono)">${keyAlias}</div>
      </div>
    </div>
    <div style="margin-bottom:4px;font-size:0.7rem;color:var(--text2);text-transform:uppercase;letter-spacing:.06em;font-weight:600">Request Headers</div>
    <pre style="background:var(--bg2);border-radius:8px;padding:10px 12px;font-size:0.72rem;font-family:var(--mono);color:var(--text2);overflow-x:auto;margin:0 0 12px;white-space:pre-wrap">Authorization: Bearer ${keyAlias}
Content-Type: application/json
X-Request-ID: req_${Math.random().toString(36).substr(2,8)}
X-Client-IP: ${ip}
Accept: application/json</pre>
    <div style="margin-bottom:4px;font-size:0.7rem;color:var(--text2);text-transform:uppercase;letter-spacing:.06em;font-weight:600">Request Body</div>
    <pre style="background:var(--bg2);border-radius:8px;padding:10px 12px;font-size:0.72rem;font-family:var(--mono);color:#a5b4fc;overflow-x:auto;margin:0 0 12px;white-space:pre-wrap">${JSON.stringify(JSON.parse(reqBody||'{}'),null,2)}</pre>
    <div style="margin-bottom:4px;font-size:0.7rem;color:var(--text2);text-transform:uppercase;letter-spacing:.06em;font-weight:600">Response Body</div>
    <pre style="background:var(--bg2);border-radius:8px;padding:10px 12px;font-size:0.72rem;font-family:var(--mono);color:${statusColor};overflow-x:auto;margin:0;white-space:pre-wrap">${JSON.stringify(JSON.parse(resBody||'{}'),null,2)}</pre>
  `;
}
