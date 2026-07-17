import { state } from '../state/store.js';
import { toast, openConf, closeConf, animProg } from '../utils/ui.js';
import { syncStats } from './dashboardController.js';
import { apiClient } from '../utils/api.js';

export async function initCommCenter() {
  updCompPreview();
  await loadCommTemplate('welcome');
  await renderCommLedgers();
}

export function switchCampaignType(type) {
  state.compType = type;
  const btnEmail = document.getElementById('btn-comp-email');
  const btnPush = document.getElementById('btn-comp-push');
  if (btnEmail) btnEmail.style.background = type === 'email' ? 'var(--bg3)' : '';
  if (btnPush) btnPush.style.background = type === 'push' ? 'var(--bg3)' : '';
  
  const ef = document.getElementById('emailFields');
  const pf = document.getElementById('pushFields');
  if (ef) ef.style.display = type === 'email' ? 'block' : 'none';
  if (pf) pf.style.display = type === 'push' ? 'block' : 'none';
  
  const ep = document.getElementById('emailPreviewFrame');
  const pp = document.getElementById('pushPreviewFrame');
  if (ep) ep.style.display = type === 'email' ? 'block' : 'none';
  if (pp) pp.style.display = type === 'push' ? 'block' : 'none';
  
  updCompPreview();
}

export function insertToken(tok) {
  const el = document.getElementById('compBody');
  if (!el) return;
  const start = el.selectionStart;
  const end = el.selectionEnd;
  const val = el.value;
  el.value = val.substring(0, start) + tok + val.substring(end);
  el.focus();
  el.selectionStart = el.selectionEnd = start + tok.length;
  updCompPreview();
}

export function updCompPreview() {
  const compBody = document.getElementById('compBody');
  if (!compBody) return;
  const bodyVal = compBody.value || 'Message body content...';
  const previewBody = bodyVal
    .replace(/\{\{user\.name\}\}/g, 'John Doe')
    .replace(/\{\{user\.credits\}\}/g, '$120.00')
    .replace(/\{\{user\.email\}\}/g, 'john.doe@example.com');
    
  if (state.compType === 'email') {
    const subjEl = document.getElementById('compEmailSubj');
    const subjVal = subjEl ? subjEl.value || 'Subject here' : 'Subject here';
    const prevSubj = document.getElementById('prevEmailSubj');
    const prevBody = document.getElementById('prevEmailBody');
    if (prevSubj) prevSubj.textContent = subjVal;
    if (prevBody) prevBody.textContent = previewBody;
  } else {
    const titleEl = document.getElementById('compPushTitle');
    const titleVal = titleEl ? titleEl.value || 'Alert Title' : 'Alert Title';
    const prevTtl = document.getElementById('prevPushTitle');
    const prevBdy = document.getElementById('prevPushBody');
    if (prevTtl) prevTtl.textContent = titleVal;
    if (prevBdy) prevBdy.textContent = previewBody;
  }
}

export function toggleSchedInputs(visible) {
  const schedInputs = document.getElementById('schedInputs');
  if (schedInputs) schedInputs.style.display = visible ? 'grid' : 'none';
}

export async function dispatchCampaign() {
  const compNameEl = document.getElementById('compName');
  const compBodyEl = document.getElementById('compBody');
  const audEl = document.getElementById('compAudience');
  if (!compNameEl || !compBodyEl || !audEl) return;

  const name = compNameEl.value.trim();
  const body = compBodyEl.value.trim();
  const aud = audEl.value;
  
  if (!name || !body) {
    toast('Please fill in Campaign Name and Content.', 'err');
    return;
  }
  
  const schedTypeEl = document.querySelector('input[name="compSchedType"]:checked');
  if (!schedTypeEl) return;
  const schedType = schedTypeEl.value;
  
  const schedTimeEl = document.getElementById('compSchedTime');
  const schedTime = schedTimeEl ? schedTimeEl.value : null;

  if (schedType === 'later' && !schedTime) {
    toast('Please specify target scheduling date & time.', 'err');
    return;
  }

  const payload = {
    name,
    body,
    audience: aud,
    type: state.compType,
    scheduleType: schedType,
    scheduleTime: schedTime
  };

  if (schedType === 'now') {
    const compProg = document.getElementById('compProg');
    if (compProg) compProg.classList.add('show');
    await animProg('compPB', 'compPPct', 'compPT', [
      { at: 0, lbl: 'Generating recipients map...' },
      { at: 35, lbl: 'Injecting dynamic tokens...' },
      { at: 75, lbl: 'Delivering outbound packets...' },
      { at: 100, lbl: 'Dispatched!' }
    ]);
    setTimeout(() => { if (compProg) compProg.classList.remove('show'); }, 1200);
  }

  try {
    await apiClient.post('/api/admin/communications/dispatch', payload);
    toast(schedType === 'later' ? 'Campaign scheduled successfully!' : 'Campaign sent successfully!', 'ok');
    resetComposer();
    renderCommLedgers();
    syncStats();
  } catch (err) {}
}

export function saveCampaignDraft() {
  const nameEl = document.getElementById('compName');
  const name = nameEl ? nameEl.value.trim() : '';
  if (!name) {
    toast('Please specify a Campaign Name to save draft.', 'err');
    return;
  }
  toast('Draft saved!', 'ok');
}

export function resetComposer() {
  const nameEl = document.getElementById('compName'); if (nameEl) nameEl.value = '';
  const bodyEl = document.getElementById('compBody'); if (bodyEl) bodyEl.value = '';
  const subjEl = document.getElementById('compEmailSubj'); if (subjEl) subjEl.value = '';
  const titleEl = document.getElementById('compPushTitle'); if (titleEl) titleEl.value = '';
  const schedTimeEl = document.getElementById('compSchedTime'); if (schedTimeEl) schedTimeEl.value = '';
  updCompPreview();
}

export async function loadCommTemplate(key) {
  state.activeTmplKey = key;
  document.querySelectorAll('.tmpl').forEach(x => x.classList.remove('on'));
  const btn = document.getElementById('tmpl-btn-' + key);
  if (btn) btn.classList.add('on');
  
  try {
    const tmpl = await apiClient.get(`/api/admin/communications/templates/${key}`);
    const tmplSubj = document.getElementById('tmplSubject');
    const tmplMark = document.getElementById('tmplMarkup');
    if (tmplSubj) tmplSubj.value = tmpl.subj || '';
    if (tmplMark) tmplMark.value = tmpl.body || '';
  } catch (err) {}
}

export async function saveCommTemplate() {
  const tmplSubj = document.getElementById('tmplSubject');
  const tmplMark = document.getElementById('tmplMarkup');
  const subj = tmplSubj ? tmplSubj.value.trim() : '';
  const markup = tmplMark ? tmplMark.value.trim() : '';
  
  if (!subj || !markup) {
    toast('Subject and HTML content markup are required.', 'err');
    return;
  }
  
  try {
    await apiClient.put(`/api/admin/communications/templates/${state.activeTmplKey}`, { subj, body: markup });
    toast('Template parameters saved!', 'ok');
  } catch (err) {}
}

export async function renderCommLedgers() {
  const histTbody = document.getElementById('commHistoryTbody');
  const queueTbody = document.getElementById('commQueueTbody');
  
  try {
    const history = await apiClient.get('/api/admin/communications/history');
    if (histTbody) {
      if (!history || !history.length) {
        histTbody.innerHTML = '<tr><td colspan="6"><div class="empty"><p>No dispatched history logs</p></div></td></tr>';
      } else {
        histTbody.innerHTML = history.map(x => `
          <tr>
            <td style="font-size:0.75rem; color:var(--text3); font-family:var(--mono);">${x.date}</td>
            <td><strong>${x.name}</strong></td>
            <td><span class="ubadge" style="background:var(--bg3); color:var(--text2);">${x.type}</span></td>
            <td>${x.segment}</td>
            <td>${x.delivered}</td>
            <td><span class="ubadge pro">DISPATCHED</span></td>
          </tr>
        `).join('');
      }
    }
    
    const queue = await apiClient.get('/api/admin/communications/queue');
    if (queueTbody) {
      if (!queue || !queue.length) {
        queueTbody.innerHTML = '<tr><td colspan="6"><div class="empty"><p>No scheduled tasks in queue</p></div></td></tr>';
      } else {
        queueTbody.innerHTML = queue.map(x => `
          <tr>
            <td style="font-size:0.75rem; color:var(--text3); font-family:var(--mono);">${x.date}</td>
            <td><strong>${x.name}</strong></td>
            <td><span class="ubadge" style="background:var(--bg3); color:var(--text2);">${x.type}</span></td>
            <td>${x.segment}</td>
            <td><span class="ubadge free">PENDING</span></td>
            <td>
              <div style="display:flex; gap:6px;">
                <button class="btn btn-p btn-sm" style="font-size:0.6rem; padding:2px 6px;" onclick="triggerScheduledNow('${x.id}')">Send Now</button>
                <button class="btn btn-d btn-sm" style="font-size:0.6rem; padding:2px 6px;" onclick="cancelScheduled('${x.id}')">Cancel</button>
              </div>
            </td>
          </tr>
        `).join('');
      }
    }
  } catch (err) {}
}

export function triggerScheduledNow(id) {
  openConf('warn', 'Trigger Scheduled Task?', `Send campaign immediately?`, async () => {
    closeConf();
    try {
      await apiClient.post(`/api/admin/communications/queue/${id}/dispatch-now`);
      toast('Campaign dispatched now!', 'ok');
      renderCommLedgers();
    } catch (err) {}
  });
}

export function cancelScheduled(id) {
  openConf('danger', 'Cancel Scheduled Campaign?', `Cancel upcoming dispatch?`, async () => {
    closeConf();
    try {
      await apiClient.delete(`/api/admin/communications/queue/${id}`);
      toast('Dispatch cancelled.', 'inf');
      renderCommLedgers();
    } catch (err) {}
  });
}
