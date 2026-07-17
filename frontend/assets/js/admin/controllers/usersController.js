import { state } from '../state/store.js';
import { toast, openConf, closeConf } from '../utils/ui.js';
import { tAgo } from '../utils/helpers.js';
import { syncStats } from './dashboardController.js';
import { apiClient } from '../utils/api.js';

export async function loadUsers() {
  document.getElementById('uTbody').innerHTML='<tr><td colspan="7"><div class="skeleton skeleton-block skeleton-w-full skeleton-h-12" style="margin-bottom:8px;"></div><div class="skeleton skeleton-block skeleton-w-full skeleton-h-12"></div></td></tr>';
  try { 
    const q=document.getElementById('uSrch')?.value.toLowerCase() || '';
    const r=document.getElementById('uRole')?.value || '';
    const s=document.getElementById('uStatus')?.value || '';
    const query = new URLSearchParams({ search: q, role: r, status: s, sort: state.currentSortField, dir: state.currentSortDir, page: state.pg, limit: state.PS }).toString();
    
    const data = await apiClient.get(`/api/admin/users?${query}`); 
    state.users = data.items || [];
    state.totalUsers = data.total || state.users.length;
  }
  catch (err) { 
    state.users = []; 
    state.totalUsers = 0;
  }
  state.fu = [...state.users]; // using this to render current page
  renderU();
}

export function filterU() { 
  state.pg = 1;
  loadUsers(); 
}

export function sortU(field, force = false) {
  if (!force) {
    if (state.currentSortField === field) {
      state.currentSortDir = state.currentSortDir === 'asc' ? 'desc' : 'asc';
    } else {
      state.currentSortField = field;
      state.currentSortDir = 'asc';
    }
  }

  document.querySelectorAll('th.sortable').forEach(th => {
    th.classList.remove('active');
    const svg = th.querySelector('svg');
    if (svg) svg.style.transform = '';
  });

  const activeTh = document.getElementById('th-' + field);
  if (activeTh) {
    activeTh.classList.add('active');
    const svg = activeTh.querySelector('svg');
    if (svg && state.currentSortDir === 'desc') {
      svg.style.transform = 'rotate(180deg)';
    }
  }

  loadUsers();
}

export function renderU() {
  const s=(state.pg-1)*state.PS, page=state.fu; // The backend already sliced it
  document.getElementById('uInfo').textContent=`Showing ${page.length ? s+1 : 0}–${Math.min(s+state.PS, state.totalUsers || 0)} of ${state.totalUsers || 0} users`;
  if(!page.length){document.getElementById('uTbody').innerHTML='<tr><td colspan="7"><div class="empty"><p>No users found</p></div></td></tr>';rPgn();return;}
  
  document.getElementById('uTbody').innerHTML=page.map(u=>`
    <tr onclick="viewUserSO('${u.id}')" style="cursor:pointer;">
      <td onclick="event.stopPropagation()"><input type="checkbox" class="rck" ${state.sel.has(u.id)?'checked':''} onchange="togSel('${u.id}')"></td>
      <td>
        <div class="uname">
          <div class="uav">${u.name.split(' ').map(n=>n[0]).join('').slice(0,2).toUpperCase()}</div>
          <div>
            <div style="font-weight:600; color:var(--text);">${u.name}</div>
            <div style="font-size:.72rem;color:var(--text3)">${u.email}</div>
          </div>
        </div>
      </td>
      <td><span class="ubadge ${u.role}">${u.role.toUpperCase()}</span></td>
      <td><div class="ustatus"><div class="udot ${u.status}"></div>${u.status}</div></td>
      <td style="font-size:.78rem">${u.joinedAt?new Date(u.joinedAt).toLocaleDateString('en-IN'):'—'}</td>
      <td style="font-size:.78rem">${tAgo(u.lastSeen)}</td>
      <td onclick="event.stopPropagation()">
        <div class="act-dropdown">
          <button class="btn btn-s btn-sm" onclick="toggleRowDropdown(event, '${u.id}')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="width: 12px; height: 12px;"><circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/><circle cx="5" cy="12" r="1"/></svg>
          </button>
          <div class="dropdown-menu" id="dropdown-${u.id}">
            <button class="dropdown-item" onclick="viewUserSO('${u.id}')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 12px; height: 12px;"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
              View details
            </button>
            <button class="dropdown-item" onclick="qNotif('${u.id}','${u.name}')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 12px; height: 12px;"><path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0"/></svg>
              Dispatch alert
            </button>
            <button class="dropdown-item" onclick="adjustUserCredits('${u.id}')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 12px; height: 12px;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
              Edit credits
            </button>
            <button class="dropdown-item text-danger" onclick="suspUser('${u.id}','${u.name}')">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 12px; height: 12px;"><circle cx="12" cy="12" r="10"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/></svg>
              Suspend
            </button>
          </div>
        </div>
      </td>
    </tr>
  `).join('');
  rPgn();
  syncStats();
}

export function toggleRowDropdown(event, userId) {
  event.stopPropagation();
  document.querySelectorAll('.dropdown-menu').forEach(m => {
    if (m.id !== 'dropdown-' + userId) m.classList.remove('show');
  });
  const menu = document.getElementById('dropdown-' + userId);
  if (menu) menu.classList.toggle('show');
}

export function exportCSV() {
  let csv = 'Name,Email,Role,Status,Joined\n';
  state.fu.forEach(u => {
    csv += `"${u.name}","${u.email}","${u.role}","${u.status}","${new Date(u.joinedAt).toLocaleDateString()}"\n`;
  });
  const blob = new Blob([csv], { type: 'text/csv' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'indcie_users_export.csv';
  link.click();
  toast('CSV Export completed!', 'ok');
}

export function exportJSON() {
  const blob = new Blob([JSON.stringify(state.fu, null, 2)], { type: 'application/json' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'indcie_users_export.json';
  link.click();
  toast('JSON Export completed!', 'ok');
}

export function viewUserSO(userId) {
  const u = state.users.find(x => x.id === userId);
  if (!u) return;
  state.currentSOUserId = userId;
  
  document.getElementById('soUserTitle').textContent = `User Profile — ${u.name}`;
  document.getElementById('soUser').classList.add('open');
  document.getElementById('soOverlay').classList.add('show');
  
  switchSOTab('overview');
}

export function switchSOTab(tabId) {
  const u = state.users.find(x => x.id === state.currentSOUserId);
  if (!u) return;
  
  document.querySelectorAll('.so-tab-btn').forEach(btn => btn.classList.remove('on'));
  const activeBtn = document.getElementById('sot-' + tabId);
  if (activeBtn) activeBtn.classList.add('on');
  
  const body = document.getElementById('soBody');
  let html = '';
  
  switch(tabId) {
    case 'overview':
      html = `
        <div style="display:flex; flex-direction:column; align-items:center; gap:12px; text-align:center; margin-bottom:24px; padding-bottom:20px; border-bottom:1px solid var(--border)">
          <div class="adm-av" style="width:72px; height:72px; font-size:1.6rem; border-radius:50%; background:linear-gradient(135deg, var(--accent), var(--accent-dim)); color:#fff; display:grid; place-items:center; font-weight:800; box-shadow:0 0 15px rgba(249,87,34,0.25)">
            ${u.name.split(' ').map(n=>n[0]).join('').slice(0,2).toUpperCase()}
          </div>
          <div>
            <h3 style="font-size:1.15rem; font-weight:800; color:var(--text);">${u.name}</h3>
            <span style="font-size:0.8rem; color:var(--text3);">${u.email}</span>
          </div>
          <div style="display:flex; gap:8px;">
            <span class="ubadge ${u.role}">${u.role.toUpperCase()}</span>
            <span class="ubadge" style="background:${u.status==='active'?'var(--lime-dim)':'var(--red-dim)'}; color:${u.status==='active'?'var(--lime)':'var(--red)'};">${u.status.toUpperCase()}</span>
          </div>
        </div>

        <div style="display:grid; grid-template-columns: 1fr 1fr; gap:16px; margin-bottom:20px;">
          <div class="card" style="padding:14px; margin:0">
            <span style="font-size:0.68rem; color:var(--text3); display:block; margin-bottom:4px">SECURITY SCORE</span>
            <span style="font-size:1.3rem; font-weight:800; color:var(--lime); font-family:var(--mono);">98%</span>
          </div>
          <div class="card" style="padding:14px; margin:0">
            <span style="font-size:0.68rem; color:var(--text3); display:block; margin-bottom:4px">THREAT RISK INDEX</span>
            <span style="font-size:1.3rem; font-weight:800; color:var(--yellow); font-family:var(--mono);">Minimal</span>
          </div>
        </div>

        <div class="card" style="padding:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Account Details</h4>
          <div style="display:flex; flex-direction:column; gap:10px; font-size:0.75rem; color:var(--text2);">
            <div style="display:flex; justify-content:space-between;"><span>User ID Key:</span><strong style="color:var(--text); font-family:var(--mono);">${u.id}</strong></div>
            <div style="display:flex; justify-content:space-between;"><span>Account Created:</span><strong style="color:var(--text)">${new Date(u.joinedAt).toLocaleString('en-IN')}</strong></div>
            <div style="display:flex; justify-content:space-between;"><span>Last Session Activity:</span><strong style="color:var(--text)">${tAgo(u.lastSeen)}</strong></div>
            <div style="display:flex; justify-content:space-between;"><span>Authorized IP Bound:</span><strong style="color:var(--text); font-family:var(--mono);">157.34.204.112</strong></div>
          </div>
        </div>
      `;
      break;
      
    case 'credits':
      html = `
        <div class="card" style="padding:20px; text-align:center; background:linear-gradient(180deg, var(--bg3), var(--bg2)); margin-bottom:20px;">
          <span style="font-size:0.75rem; color:var(--text2); display:block; margin-bottom:6px">AVAILABLE PROMOTIONAL BALANCE</span>
          <span style="font-size:2rem; font-weight:900; color:var(--text); font-family:var(--mono);">$284.50</span>
          <span style="font-size:0.68rem; color:var(--text3); display:block; margin-top:4px">Non-expiring sandbox scan credits</span>
        </div>

        <div class="card" style="padding:16px; margin-bottom:20px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Credit Adjustments (Admin Only)</h4>
          <div class="sfrow" style="gap:8px;">
            <input type="number" id="soCreditAmt" class="fi" value="10" style="width:80px;" min="1">
            <button class="btn btn-p btn-sm" onclick="adjustSOBalance('add')">Add Credits</button>
            <button class="btn btn-d btn-sm" onclick="adjustSOBalance('sub')">Deduct</button>
          </div>
        </div>

        <div class="card" style="padding:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Usage & Credit Log</h4>
          <div style="display:flex; flex-direction:column; gap:10px; font-size:0.74rem;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div><span style="font-weight:600; color:var(--text);">Manual Promo Allocation</span><span style="display:block; font-size:0.65rem; color:var(--text3)">Jul 10, 2026</span></div>
              <strong style="color:var(--lime)">+$100.00</strong>
            </div>
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div><span style="font-weight:600; color:var(--text);">Malware Sandbox Scan (-2 quota)</span><span style="display:block; font-size:0.65rem; color:var(--text3)">Jul 09, 2026</span></div>
              <strong style="color:var(--red)">-$2.00</strong>
            </div>
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div><span style="font-weight:600; color:var(--text);">Monthly Account Credit</span><span style="display:block; font-size:0.65rem; color:var(--text3)">Jul 01, 2026</span></div>
              <strong style="color:var(--lime)">+$50.00</strong>
            </div>
          </div>
        </div>
      `;
      break;
      
    case 'devices':
      html = `
        <div style="margin-bottom:16px; font-size:0.75rem; color:var(--text2);">Configure authorized workstation locks for endpoints.</div>
        <div style="display:flex; flex-direction:column; gap:12px;">
          <div class="card" style="padding:14px; display:flex; justify-content:space-between; align-items:center; margin:0">
            <div>
              <div style="font-weight:700; color:var(--text); font-size:0.78rem;">Apple MacBook Pro (Sonoma)</div>
              <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Safari · OS v14.4 · IP: 198.51.100.42</div>
            </div>
            <button class="btn btn-d btn-sm" onclick="toast('Workstation blocked.','warn')">Block</button>
          </div>
          <div class="card" style="padding:14px; display:flex; justify-content:space-between; align-items:center; margin:0">
            <div>
              <div style="font-weight:700; color:var(--text); font-size:0.78rem;">Lenovo ThinkPad X1 Carbon</div>
              <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Chrome · Windows 11 · IP: 103.88.22.109</div>
            </div>
            <button class="btn btn-d btn-sm" onclick="toast('Workstation blocked.','warn')">Block</button>
          </div>
          <div class="card" style="padding:14px; display:flex; justify-content:space-between; align-items:center; margin:0">
            <div>
              <div style="font-weight:700; color:var(--text); font-size:0.78rem;">iPhone 15 Pro Max</div>
              <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">CYPR Agent App v2.4 · IP: 49.36.2.14</div>
            </div>
            <button class="btn btn-d btn-sm" onclick="toast('Workstation blocked.','warn')">Block</button>
          </div>
        </div>
      `;
      break;
      
    case 'sessions':
      html = `
        <div style="margin-bottom:16px; font-size:0.75rem; color:var(--text2);">Terminate active daemon sessions directly from the administrator console.</div>
        <div style="display:flex; flex-direction:column; gap:12px;">
          <div class="card" style="padding:14px; border-color:var(--lime-dim); margin:0;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start;">
              <div>
                <span class="ubadge pro" style="font-size:0.6rem; padding:2px 6px; margin-bottom:4px; display:inline-block;">CURRENT SESSION</span>
                <div style="font-weight:700; color:var(--text); font-size:0.78rem;">New Delhi, India</div>
                <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Chrome 126 · IP: 103.24.2.11 · Last active: Just now</div>
              </div>
            </div>
          </div>
          <div class="card" style="padding:14px; margin:0; display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-weight:700; color:var(--text); font-size:0.78rem;">San Jose, United States</div>
              <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Firefox 125 · IP: 192.0.2.82 · Last active: 2h ago</div>
            </div>
            <button class="btn btn-s btn-sm" onclick="toast('Session terminated successfully.','ok')">Revoke</button>
          </div>
        </div>
      `;
      break;
      
    case 'payments':
      html = `
        <div class="card" style="padding:16px; margin-bottom:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Subscription Status</h4>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <strong style="color:var(--text); font-size:0.85rem;">INDCIE SaaS Pro</strong>
              <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Renews automatically on August 01, 2026</div>
            </div>
            <div style="font-size:1.15rem; font-weight:900; color:var(--text); font-family:var(--mono);">$49.00<span style="font-size:0.7rem; color:var(--text3)">/mo</span></div>
          </div>
        </div>

        <div class="card" style="padding:16px; margin-bottom:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Payment Method</h4>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <div style="display:flex; align-items:center; gap:8px;">
              <span style="font-size:1.1rem;">💳</span>
              <div>
                <strong style="color:var(--text); font-size:0.76rem;">Visa ending in 4242</strong>
                <div style="font-size:0.68rem; color:var(--text3); margin-top:2px;">Expires: 08/2029</div>
              </div>
            </div>
            <button class="btn btn-s btn-sm" onclick="toast('Payment update link sent.','inf')">Change</button>
          </div>
        </div>

        <div class="card" style="padding:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Invoice History</h4>
          <div style="display:flex; flex-direction:column; gap:10px; font-size:0.74rem;">
            <div style="display:flex; justify-content:space-between;">
              <span>Invoice #INV-2026-004 (Jul 10, 2026)</span>
              <strong style="color:var(--lime)">Paid ($49.00)</strong>
            </div>
            <div style="display:flex; justify-content:space-between;">
              <span>Invoice #INV-2026-003 (Jun 10, 2026)</span>
              <strong style="color:var(--lime)">Paid ($49.00)</strong>
            </div>
          </div>
        </div>
      `;
      break;
      
    case 'loginHistory':
      html = `
        <div style="margin-bottom:16px; font-size:0.75rem; color:var(--text2);">Audit trail of login sequences.</div>
        <div style="overflow-x:auto">
          <table class="utbl" style="font-size:0.74rem">
            <thead>
              <tr>
                <th>Log Time</th>
                <th>Location / IP</th>
                <th>Browser</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Jul 10, 18:42</td>
                <td><strong>Delhi, IN</strong><div style="font-size:0.65rem; color:var(--text3)">103.24.2.11</div></td>
                <td>Chrome</td>
                <td><span class="ubadge pro" style="padding:1px 5px; font-size:0.6rem;">Success</span></td>
              </tr>
              <tr>
                <td>Jul 10, 17:15</td>
                <td><strong>San Jose, US</strong><div style="font-size:0.65rem; color:var(--text3)">192.0.2.82</div></td>
                <td>Firefox</td>
                <td><span class="ubadge pro" style="padding:1px 5px; font-size:0.6rem;">Success</span></td>
              </tr>
              <tr>
                <td>Jul 08, 04:12</td>
                <td><strong>Beijing, CN</strong><div style="font-size:0.65rem; color:var(--text3)">45.89.2.190</div></td>
                <td>Opera</td>
                <td><span class="ubadge admin" style="padding:1px 5px; font-size:0.6rem;">Failed</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      `;
      break;
      
    case 'activity':
      html = `
        <div style="margin-bottom:18px; font-size:0.75rem; color:var(--text2);">Historical timeline of client operations.</div>
        <div class="so-timeline">
          <div class="so-timeline-item">
            <div class="so-timeline-dot success"></div>
            <div class="so-timeline-time">Jul 10, 2026 · 18:43</div>
            <div class="so-timeline-title">MFA Authentication Setup</div>
            <div class="so-timeline-desc">Secured user account with mobile TOTP registration keys.</div>
          </div>
          <div class="so-timeline-item">
            <div class="so-timeline-dot info"></div>
            <div class="so-timeline-time">Jul 09, 2026 · 14:20</div>
            <div class="so-timeline-title">Malware Scan Dispatch</div>
            <div class="so-timeline-desc">Analyzed threat vectors on file uploads: <code>report_payload.exe</code>. State: clean.</div>
          </div>
          <div class="so-timeline-item">
            <div class="so-timeline-dot critical"></div>
            <div class="so-timeline-time">Jul 08, 2026 · 04:12</div>
            <div class="so-timeline-title">Suspicious Login Blocked</div>
            <div class="so-timeline-desc">Prevented unauthorized session binding from suspicious geolocation (Beijing, CN).</div>
          </div>
          <div class="so-timeline-item">
            <div class="so-timeline-dot info"></div>
            <div class="so-timeline-time">Jul 01, 2026 · 09:00</div>
            <div class="so-timeline-title">Tier Subscription Upgrade</div>
            <div class="so-timeline-desc">Subscribed to Pro Tier plan ($49/month).</div>
          </div>
        </div>
      `;
      break;
      
    case 'apiUsage':
      html = `
        <div class="card" style="padding:16px; margin-bottom:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">API Key Quota Bounds</h4>
          
          <div class="srv-item" style="margin-bottom:12px;">
            <div class="srv-hdr">
              <span>Daily Request Volume</span>
              <span>2,450 / 5,000 requests</span>
            </div>
            <div class="srv-bar-wrap">
              <div class="srv-bar-fill blue" style="width: 49%;"></div>
            </div>
          </div>

          <div class="srv-item">
            <div class="srv-hdr">
              <span>Concurrent Sandbox Threads</span>
              <span>3 / 10 analyses</span>
            </div>
            <div class="srv-bar-wrap">
              <div class="srv-bar-fill lime" style="width: 30%;"></div>
            </div>
          </div>
        </div>

        <div class="card" style="padding:16px;">
          <h4 style="font-size:0.8rem; font-weight:700; margin-bottom:12px; border-bottom:1px solid var(--border); padding-bottom:6px;">Active API Tokens</h4>
          <div style="display:flex; flex-direction:column; gap:10px; font-size:0.74rem;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div>
                <strong style="color:var(--text); font-family:var(--mono);">ind_dev_••••••••••••x91c</strong>
                <span style="display:block; font-size:0.65rem; color:var(--text3)">Created: 3 days ago · scope: read:threats</span>
              </div>
              <button class="btn btn-d btn-sm" style="font-size:0.6rem; padding:2px 6px;" onclick="toast('API Token revoked.','warn')">Revoke</button>
            </div>
          </div>
        </div>
      `;
      break;
  }
  
  body.innerHTML = html;
}

export function closeUserSO() {
  document.getElementById('soUser').classList.remove('open');
  document.getElementById('soOverlay').classList.remove('show');
  state.currentSOUserId = null;
}

export async function adjustSOBalance(type) {
  const amtInput = document.getElementById('soCreditAmt');
  if (!amtInput) return;
  const amt = parseFloat(amtInput.value) || 0;
  if (!state.currentSOUserId) return;
  
  try {
    await apiClient.post(`/api/admin/users/${state.currentSOUserId}/credits`, { type, amount: amt });
    toast(`${type === 'add' ? 'Added' : 'Deducted'} $${amt.toFixed(2)} to/from user balance.`, 'ok');
    loadUsers(); // Refresh list to get updated data
  } catch (err) {
    // API error caught by apiClient, already toasted
  }
}

export async function adjustUserCredits(userId) {
  const u = state.users.find(x => x.id === userId);
  const name = u ? u.name : `User #${userId}`;
  
  const amountStr = prompt(`💳 Adjust Credit Balance for ${name}:\nEnter credit amount (e.g. 100 to add, -50 to deduct):`);
  if (!amountStr) return;
  const amount = parseInt(amountStr, 10);
  if (isNaN(amount) || amount === 0) {
    toast('Invalid credit amount entered', 'err');
    return;
  }

  const reason = prompt(`Reason for credit adjustment of ${amount} pts for ${name}:`, 'Admin manual adjustment') || 'Admin adjustment';

  // Requirement 13: Mandatory Idempotency-Key header
  const idempotencyKey = 'idemp_' + Math.random().toString(36).substring(2) + Date.now().toString(36);

  try {
    await apiClient.post('/api/v1/developer/credits/adjust', {
      targetUserId: userId,
      amountDelta: amount,
      reason: reason
    }, {
      headers: { 'Idempotency-Key': idempotencyKey }
    });

    toast(`Successfully adjusted ${amount} credits for ${name}!`, 'ok');
    loadUsers();
  } catch (err) {
    // Error toasted by apiClient
  }
}

export async function resetUserCreds(userId) {
  try {
    await apiClient.post(`/api/v1/users/${userId}/reset-mfa`);
    toast(`MFA reset link dispatched.`, 'ok');
  } catch (err) {}
}

export async function toggleUserSuspension(userId) {
  const u = state.users.find(x => x.id === userId);
  const name = u ? u.name : `User #${userId}`;
  const currentStatus = u ? u.status : 'ACTIVE';
  const newStatus = currentStatus.toUpperCase() === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';

  const reason = prompt(`⚠️ Update Account Status for ${name} to ${newStatus}:\nPlease enter mandatory reason for audit log:`);
  if (!reason) {
    toast('Action cancelled — status change requires an audit reason.', 'inf');
    return;
  }

  try {
    await apiClient.put(`/api/v1/users/${userId}/status`, {
      status: newStatus,
      reason: reason
    });
    toast(`User ${name} status updated to ${newStatus}.`, 'ok');
    loadUsers();
  } catch (err) {}
}

export function bulkAddCredits() {
  const c = state.sel.size;
  if (c === 0) return;
  openConf('warn', 'Bulk Add Credits?', `Add 100 promotional credits to <strong>${c} selected users</strong>?`, () => {
    closeConf();
    if (window.addLog) window.addLog('success', `Added 100 credits in bulk to <strong>${c}</strong> users`);
    toast(`Allocated credits to ${c} users!`, 'ok');
    clearSel();
  });
}

export function rPgn() { 
  const tot=Math.ceil((state.totalUsers||0)/state.PS), pEl=document.getElementById('uPgn'); 
  if(tot<=1){pEl.innerHTML='';return;} 
  let h=`<button class="pgb" onclick="cPg(${state.pg-1})" ${state.pg===1?'disabled':''}><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg></button>`; 
  for(let i=1;i<=Math.min(tot,5);i++) h+=`<button class="pgb ${i===state.pg?'on':''}" onclick="cPg(${i})">${i}</button>`; 
  if(tot>5)h+=`<span style="color:var(--text3);font-size:.8rem">…${tot}</span>`; 
  h+=`<button class="pgb" onclick="cPg(${state.pg+1})" ${state.pg===tot?'disabled':''}><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg></button>`; 
  pEl.innerHTML=h; 
}

export function cPg(p) { 
  const tot=Math.ceil((state.totalUsers||0)/state.PS); 
  if(p<1||p>tot)return; 
  state.pg=p; 
  loadUsers(); 
}

export function togSelAll() { 
  const c=document.getElementById('selAll').checked; 
  state.fu.forEach(u=>c?state.sel.add(u.id):state.sel.delete(u.id)); 
  renderU(); 
  updBulk(); 
}

export function togSel(id) { 
  state.sel.has(id)?state.sel.delete(id):state.sel.add(id); 
  updBulk(); 
}

export function updBulk() { 
  const n=state.sel.size; 
  const b=document.getElementById('bulkBar'); 
  n>0?(b.classList.add('show'),document.getElementById('bulkLbl').textContent=n+' selected'):b.classList.remove('show'); 
}

export function clearSel() { 
  state.sel.clear(); 
  renderU(); 
  updBulk(); 
}

export function qNotif(id,name) { 
  const n={id:'q_'+Date.now(),type:'info',icon:'ℹ️',title:'Admin Notification',body:'You have a new message from CYPR Tech Admin.',time:'Just now',read:false}; 
  const un=JSON.parse(localStorage.getItem('cypr_notifications')||'[]'); 
  un.unshift(n); 
  localStorage.setItem('cypr_notifications',JSON.stringify(un.slice(0,50))); 
  if (window.addLog) window.addLog('info',`Notification sent to <strong>${name}</strong>`); 
  toast(`Sent to ${name}!`,`ok`); 
}

export function suspUser(id,name) { 
  openConf('danger','Suspend User?',`Suspend <strong>${name}</strong>? They will lose access to CYPR Tech.`, async ()=>{ 
    closeConf(); 
    try {
      await apiClient.post(`/api/admin/users/${id}/suspend`);
      toast(`${name} suspended.`,`inf`);
      loadUsers();
    } catch (err) {}
  }); 
}

export function bulkNotif() { 
  const c=state.sel.size; 
  openConf('warn','Notify Selected?',`Send a notification to <strong>${c} users</strong>?`, async ()=>{ 
    closeConf(); 
    try {
      const ids = Array.from(state.sel);
      await apiClient.post(`/api/admin/communications/dispatch-bulk`, { userIds: ids, message: 'New update from CYPR Tech.' });
      toast(`Sent to ${c} users!`,`ok`); 
      clearSel();
    } catch(err) {}
  }); 
}

export function bulkSusp() { 
  const c=state.sel.size; 
  openConf('danger','Bulk Suspend?',`Suspend <strong>${c} selected users</strong>? This is critical.`, async ()=>{ 
    closeConf(); 
    try {
      const ids = Array.from(state.sel);
      await apiClient.post(`/api/admin/users/bulk-suspend`, { userIds: ids });
      toast(`${c} users suspended.`,`inf`); 
      clearSel();
      loadUsers();
    } catch(err) {}
  }); 
}
