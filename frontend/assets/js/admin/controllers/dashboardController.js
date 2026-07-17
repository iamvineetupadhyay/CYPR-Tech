import { state } from '../state/store.js';
import { toast, openConf, closeConf } from '../utils/ui.js';
import { apiClient } from '../utils/api.js';
// addLog is attached to window to avoid circular dependencies

export async function syncStats() {
  try {
    // Show skeleton loader for total users while fetching
    const userCountEl = document.getElementById('sTotalU');
    if (userCountEl) userCountEl.innerHTML = '<div class="skeleton-text" style="width:80px;height:24px;"></div>';
    const notifEl = document.getElementById('sNotif');
    if (notifEl) notifEl.innerHTML = '<div class="skeleton-text" style="width:80px;height:24px;"></div>';
    const emailEl = document.getElementById('sEmail');
    if (emailEl) emailEl.innerHTML = '<div class="skeleton-text" style="width:80px;height:24px;"></div>';
    
    const data = await apiClient.get('/api/v1/dashboard');
    const metrics = data.metrics || data.overview || data;

    // Update dashboard stat cards
    if (userCountEl) userCountEl.textContent = metrics.totalUsers || data.totalUsers || '—';

    const uBadgeEl = document.getElementById('uBadge');
    if (uBadgeEl) uBadgeEl.textContent = metrics.totalUsers || data.totalUsers || '—';

    if (notifEl) notifEl.textContent = metrics.notificationsSent || data.notificationsSent || 0;
    if (emailEl) emailEl.textContent = metrics.emailsSent || data.emailsSent || 0;
  } catch (err) {
    console.error("Failed to fetch dashboard stats", err);
    if (document.getElementById('sTotalU')) document.getElementById('sTotalU').textContent = 'Error';
  }

  const b = JSON.parse(localStorage.getItem('cypr_admin_announcement') || 'null');
  if (b?.active) { 
    document.getElementById('sBnr').textContent = 'ON'; 
    document.getElementById('sBnr').style.color = 'var(--lime)'; 
    document.getElementById('sBnrLbl').textContent = 'Active Announcement'; 
    document.getElementById('sBnrLbl').className = 'sc-chg up'; 
  } else { 
    // Homepage Banner Stats
    const fb = JSON.parse(localStorage.getItem('cypr_admin_featured_banner') || 'null');
    if (fb?.active) {
      document.getElementById('sBnr').textContent = 'ON'; 
      document.getElementById('sBnr').style.color = 'var(--accent)'; 
      document.getElementById('sBnrLbl').textContent = 'Active Featured Banner'; 
      document.getElementById('sBnrLbl').className = 'sc-chg up'; 
    } else {
      document.getElementById('sBnr').textContent = 'OFF'; 
      document.getElementById('sBnr').style.color = ''; 
      document.getElementById('sBnrLbl').textContent = 'No active notice/banner'; 
      document.getElementById('sBnrLbl').className = 'sc-chg'; 
    }
  }
}

export function pubBnr() {
  const fullImg = document.getElementById('bnrFullImg').checked;
  const imgUrl = document.getElementById('bnrImgUrl').value.trim();
  const msg = document.getElementById('bnrMsg').value.trim(); 
  
  if (!fullImg && !msg) { toast('Enter an announcement message.','err'); return; }
  if (fullImg && !imgUrl) { toast('Please provide a Photo/Image URL to show as a full graphic announcement.','err'); return; }
  
  const type=document.getElementById('bnrType').value, exp=document.getElementById('bnrExp').value, link=document.getElementById('bnrLink').value.trim(), lt=document.getElementById('bnrLinkTxt').value.trim();
  const ems = {never:null,'1h':3600000,'6h':21600000,'24h':86400000,'7d':604800000};
  const b = {active:true,type,message:msg || 'Announcement',imageUrl:imgUrl||null,fullImage:fullImg,link:link||null,linkText:lt||null,expiry:exp==='never'?null:Date.now()+ems[exp],publishedAt:Date.now()};
  
  localStorage.setItem('cypr_admin_announcement',JSON.stringify(b)); syncStats(); showABnr(b);
  if (window.addLog) window.addLog('success', `Site announcement "${(msg || imgUrl).slice(0,30)}" published`); toast('Announcement published!','ok');
}

export function rmBnr() { 
  openConf('danger','Remove Announcement?','This will remove the active announcement from all pages.',()=>{ 
    closeConf(); 
    localStorage.removeItem('cypr_admin_announcement'); 
    syncStats(); 
    document.getElementById('aBnrInfo').innerHTML='<div class="empty"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="7" width="20" height="10" rx="2"/></svg><p>No active announcement set</p></div>'; 
    if(window.addLog) window.addLog('warning','Active announcement removed'); 
    toast('Announcement removed.','inf'); 
  }); 
}

export function restBnr() { 
  const b=JSON.parse(localStorage.getItem('cypr_admin_announcement')||'null'); 
  if(!b)return; 
  if(b.expiry&&Date.now()>b.expiry){localStorage.removeItem('cypr_admin_announcement');return;} 
  document.getElementById('bnrMsg').value=b.message||''; 
  document.getElementById('bnrType').value=b.type||'info'; 
  document.getElementById('bnrLink').value=b.link||''; 
  document.getElementById('bnrLinkTxt').value=b.linkText||''; 
  document.getElementById('bnrImgUrl').value=b.imageUrl||''; 
  document.getElementById('bnrFullImg').checked=!!b.fullImage; 
  if (window.updBnr) window.updBnr(); 
  showABnr(b); 
  syncStats(); 
}

export function updBnr() {
  const fullImg = document.getElementById('bnrFullImg').checked;
  const imgUrl = document.getElementById('bnrImgUrl').value.trim();
  const msg = document.getElementById('bnrMsg').value.trim();
  const clrs={info:'var(--blue)',success:'var(--lime)',warning:'var(--yellow)',critical:'var(--red)'};
  const c = clrs[document.getElementById('bnrType').value] || clrs.info;
  
  document.getElementById('bnrImgSec').style.display = fullImg ? 'block' : 'none';
  document.getElementById('bnrMsgSec').style.display = fullImg ? 'none' : 'block';
  
  const prev = document.getElementById('bnrPrev');
  if (prev) {
    if (fullImg) {
      prev.style.background = imgUrl ? `url(${imgUrl}) center/cover no-repeat` : 'var(--bg3)';
      prev.innerHTML = '';
      prev.style.padding = '0';
      prev.style.height = '60px';
    } else {
      prev.style.background = `${c}15`;
      prev.style.padding = '14px 20px';
      prev.style.height = 'auto';
      prev.innerHTML = `<div style="display:flex;align-items:center;gap:12px;color:${c};font-weight:700;font-size:0.9rem"><div style="width:10px;height:10px;border-radius:50%;background:${c};box-shadow:0 0 8px ${c}"></div>${msg || 'Announcement Preview'}</div>`;
    }
  }
}

export function showABnr(b) { 
  const clrs={info:'var(--blue)',success:'var(--lime)',warning:'var(--yellow)',critical:'var(--red)'}; 
  document.getElementById('aBnrInfo').innerHTML=`<div style="display:flex;flex-direction:column;gap:8px"><div style="display:flex;align-items:center;gap:8px"><div style="width:8px;height:8px;border-radius:50%;background:${clrs[b.type]};box-shadow:0 0 6px ${clrs[b.type]}"></div><span style="font-size:.85rem;font-weight:700">${b.fullImage ? 'Full Image Graphic' : b.message}</span></div><div style="font-size:.75rem;color:var(--text3)">Published: ${new Date(b.publishedAt).toLocaleString()} · Expires: ${b.expiry?new Date(b.expiry).toLocaleString():'Never'}</div></div>`; 
}

export function updFeatBnr() {
  const tag = document.getElementById('fbTag').value.trim() || 'NEW FEATURE';
  const title = document.getElementById('fbTitle').value.trim() || 'Banner Title here...';
  const desc = document.getElementById('fbDesc').value.trim() || 'Banner description here...';
  const imgUrl = document.getElementById('fbImgUrl').value.trim();

  document.getElementById('fbPrevTag').textContent = tag;
  document.getElementById('fbPrevTitle').textContent = title;
  document.getElementById('fbPrevDesc').textContent = desc;

  const prev = document.getElementById('fbPrev');
  if (prev) {
    if (imgUrl) {
      prev.style.backgroundImage = `linear-gradient(rgba(0,0,0,0.65), rgba(0,0,0,0.65)), url(${imgUrl})`;
      prev.style.backgroundSize = 'cover';
      prev.style.backgroundPosition = 'center';
      prev.style.backgroundRepeat = 'no-repeat';
    } else {
      prev.style.backgroundImage = 'linear-gradient(135deg, #120e0c 0%, #2b201a 100%)';
    }
  }
}

export function pubFeatBnr() {
  const tag = document.getElementById('fbTag').value.trim() || 'ANNOUNCEMENT';
  const title = document.getElementById('fbTitle').value.trim();
  const desc = document.getElementById('fbDesc').value.trim();
  const imgUrl = document.getElementById('fbImgUrl').value.trim();
  const link = document.getElementById('fbLink').value.trim();

  if (!title || !desc) { toast('Please fill in title and description.', 'err'); return; }

  const fb = {
    active: true,
    tag,
    title,
    desc,
    imageUrl: imgUrl || null,
    link: link || null,
    publishedAt: Date.now()
  };

  localStorage.setItem('cypr_admin_featured_banner', JSON.stringify(fb));
  showAFeatBnr(fb);
  syncStats();
  if (window.addLog) window.addLog('success', `Homepage Featured Banner "<strong>${title.slice(0,30)}</strong>" published`);
  toast('Homepage Banner published!', 'ok');
}

export function rmFeatBnr() {
  openConf('danger', 'Remove Homepage Banner?', 'This will remove the custom featured banner from the homepage.', () => {
    closeConf();
    localStorage.removeItem('cypr_admin_featured_banner');
    document.getElementById('aFeatBnrInfo').innerHTML = '<div class="empty"><p>No active homepage banner set</p></div>';
    syncStats();
    if (window.addLog) window.addLog('warning', 'Homepage featured banner removed');
    toast('Homepage Banner removed.', 'inf');
  });
}

export function restFeatBnr() {
  const fb = JSON.parse(localStorage.getItem('cypr_admin_featured_banner') || 'null');
  if (!fb) return;
  document.getElementById('fbTag').value = fb.tag || '';
  document.getElementById('fbTitle').value = fb.title || '';
  document.getElementById('fbDesc').value = fb.desc || '';
  document.getElementById('fbImgUrl').value = fb.imageUrl || '';
  document.getElementById('fbLink').value = fb.link || '';
  updFeatBnr();
  showAFeatBnr(fb);
  syncStats();
}

export function showAFeatBnr(fb) {
  document.getElementById('aFeatBnrInfo').innerHTML = `
    <div style="display:flex;flex-direction:column;gap:8px">
      <div style="display:flex;align-items:center;gap:8px">
        <div style="width:8px;height:8px;border-radius:50%;background:var(--accent);box-shadow:0 0 6px var(--accent)"></div>
        <span style="font-size:.85rem;font-weight:700">[${fb.tag || 'NEW'}] ${fb.title}</span>
      </div>
      <div style="font-size:.75rem;color:var(--text3)">Published: ${new Date(fb.publishedAt).toLocaleString()}</div>
    </div>
  `;
}
