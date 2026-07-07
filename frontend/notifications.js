/* ══════════════════════════════════════
   CYPR Tech – Notification System
   Shared across all pages
   ══════════════════════════════════════ */

(function () {
  'use strict';

  /* ── Default fallback notifications (For guests) ── */
  const DEFAULT_NOTIFICATIONS = [
    { id: 'n1', type: 'success', icon: '🛡️', title: 'CYPR Tech Vault Active', body: 'Welcome to CYPR Tech! Complete portal scanning modules are online.', time: 'Just now', read: false }
  ];

  /* ── State ── */
  let notifications = [];
  let panelOpen = false;

  function loadNotifications() {
    const saved = localStorage.getItem('cypr_notifications');
    if (saved) {
      try { notifications = JSON.parse(saved); } catch { notifications = structuredClone(DEFAULT_NOTIFICATIONS); }
    } else {
      notifications = structuredClone(DEFAULT_NOTIFICATIONS);
    }
  }

  async function fetchUserNotifications(userId) {
    try {
      const apiBase = window.CYPR_TECH_API_BASE !== undefined ? window.CYPR_TECH_API_BASE : '';
      const res = await fetch(`${apiBase}/api/user/${userId}/activity`);
      if (res.ok) {
        const activities = await res.json();
        if (activities && activities.length > 0) {
          const fresh = [];

          // First add a welcome notification
          fresh.push({
            id: 'welcome',
            type: 'success',
            icon: '🛡️',
            title: 'Protection Active',
            body: 'CYPR Tech active heuristics, URL phishing detectors, and credit monitoring are fully operational.',
            time: 'Just now',
            read: false
          });

          activities.forEach((act) => {
            let type = 'info';
            let icon = 'ℹ️';
            if (act.result === 'danger' || act.result === 'warning' || act.result === 'Risky') {
              type = 'critical';
              icon = '🚨';
            } else if (act.result === 'safe' || act.result === 'Secure') {
              type = 'success';
              icon = '✅';
            }

            let timeAgo = 'Recently';
            if (act.timestamp) {
              const diff = Date.now() - act.timestamp;
              if (diff < 60000) timeAgo = 'Just now';
              else if (diff < 3600000) timeAgo = `${Math.floor(diff / 60000)}m ago`;
              else if (diff < 86400000) timeAgo = `${Math.floor(diff / 3600000)}h ago`;
              else timeAgo = `${Math.floor(diff / 86400000)}d ago`;
            }

            fresh.push({
              id: 'act_' + act.id,
              type: type,
              icon: icon,
              title: act.title || 'Security Scan Event',
              body: act.description || 'Verified security details of your last request.',
              time: timeAgo,
              read: true
            });
          });

          notifications = fresh;
          refreshPanel();
        }
      }
    } catch (e) {
      console.warn("Failed to fetch dynamic user notifications: ", e);
    }
  }

  function saveNotifications() {
    localStorage.setItem('cypr_notifications', JSON.stringify(notifications));
  }

  function unreadCount() {
    return notifications.filter(n => !n.read).length;
  }

  /* ── Inject CSS ── */
  function injectCSS() {
    if (document.getElementById('notif-css')) return;
    const style = document.createElement('style');
    style.id = 'notif-css';
    style.textContent = `
/* ── Notification Panel ── */
.notif-wrapper{position:relative;}
.notif-count{position:absolute;top:3px;right:3px;min-width:16px;height:16px;background:var(--red,#ff4444);color:#fff;font-family:var(--mono,'JetBrains Mono',monospace);font-size:.55rem;font-weight:700;display:flex;align-items:center;justify-content:center;border-radius:100px;padding:0 4px;border:2px solid var(--bg,#0a0a0a);z-index:2;pointer-events:none;animation:notifPop .3s ease;}
.notif-count.hide{display:none;}
@keyframes notifPop{from{transform:scale(0)}to{transform:scale(1)}}

.notif-panel{position:absolute;top:calc(100% + 10px);right:0;width:380px;max-height:480px;background:var(--bg2,#0f0f0f);border:1px solid var(--border,#1f1f1f);border-radius:14px;box-shadow:0 20px 60px rgba(0,0,0,.6),0 0 0 1px rgba(255,255,255,.03);z-index:1000;display:none;flex-direction:column;overflow:hidden;animation:notifSlide .2s ease;}
.notif-panel.open{display:flex;}
@keyframes notifSlide{from{opacity:0;transform:translateY(-8px)}to{opacity:1;transform:translateY(0)}}

.notif-header{display:flex;align-items:center;justify-content:space-between;padding:16px 18px 12px;border-bottom:1px solid var(--border,#1f1f1f);}
.notif-header h3{font-size:.95rem;font-weight:700;display:flex;align-items:center;gap:8px;}
.notif-header h3 .nh-count{font-family:var(--mono,'JetBrains Mono',monospace);font-size:.6rem;font-weight:700;background:var(--red,#ff4444);color:#fff;padding:2px 8px;border-radius:100px;}
.notif-actions{display:flex;gap:6px;}
.notif-btn{font-family:var(--mono,'JetBrains Mono',monospace);font-size:.6rem;font-weight:600;color:var(--text3,#555);background:var(--bg3,#141414);border:1px solid var(--border,#1f1f1f);padding:4px 10px;border-radius:6px;cursor:pointer;transition:all .18s;white-space:nowrap;}
.notif-btn:hover{color:var(--lime,#c8ff00);border-color:rgba(200,255,0,.2);background:rgba(200,255,0,.05);}
.notif-btn.danger:hover{color:var(--red,#ff4444);border-color:rgba(255,68,68,.2);background:rgba(255,68,68,.05);}

.notif-list{flex:1;overflow-y:auto;padding:6px;}
.notif-list::-webkit-scrollbar{width:3px;}
.notif-list::-webkit-scrollbar-thumb{background:var(--border2,#2a2a2a);border-radius:2px;}

.notif-item{display:flex;gap:12px;padding:12px 14px;border-radius:10px;cursor:pointer;transition:all .18s;position:relative;border:1px solid transparent;margin-bottom:2px;}
.notif-item:hover{background:var(--bg3,#141414);border-color:var(--border,#1f1f1f);}
.notif-item.unread{background:rgba(200,255,0,.02);border-color:rgba(200,255,0,.08);}
.notif-item.unread::before{content:'';position:absolute;left:6px;top:50%;transform:translateY(-50%);width:5px;height:5px;border-radius:50%;background:var(--lime,#c8ff00);}
.notif-icon{width:36px;height:36px;border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0;font-size:1rem;background:var(--bg4,#1a1a1a);border:1px solid var(--border,#1f1f1f);}
.notif-item.critical .notif-icon{background:rgba(255,68,68,.1);border-color:rgba(255,68,68,.2);}
.notif-item.warning .notif-icon{background:rgba(255,170,0,.1);border-color:rgba(255,170,0,.2);}
.notif-item.success .notif-icon{background:rgba(200,255,0,.1);border-color:rgba(200,255,0,.2);}
.notif-body{flex:1;min-width:0;}
.notif-title{font-size:.82rem;font-weight:600;margin-bottom:3px;line-height:1.3;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.notif-text{font-size:.72rem;color:var(--text2,#888);line-height:1.45;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;}
.notif-time{font-family:var(--mono,'JetBrains Mono',monospace);font-size:.55rem;color:var(--text3,#555);margin-top:4px;}
.notif-dismiss{position:absolute;top:8px;right:8px;width:20px;height:20px;display:flex;align-items:center;justify-content:center;border-radius:5px;color:var(--text3,#555);font-size:.7rem;cursor:pointer;opacity:0;transition:all .18s;}
.notif-item:hover .notif-dismiss{opacity:1;}
.notif-dismiss:hover{background:rgba(255,68,68,.1);color:var(--red,#ff4444);}

.notif-empty{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:40px 20px;color:var(--text3,#555);}
.notif-empty-icon{font-size:2rem;margin-bottom:10px;opacity:.5;}
.notif-empty-text{font-size:.82rem;font-weight:500;}
.notif-empty-sub{font-size:.7rem;color:var(--text3,#555);margin-top:4px;}

.notif-footer{padding:10px 14px;border-top:1px solid var(--border,#1f1f1f);text-align:center;}
.notif-footer a{font-family:var(--mono,'JetBrains Mono',monospace);font-size:.65rem;color:var(--lime,#c8ff00);font-weight:600;display:inline-flex;align-items:center;gap:4px;transition:gap .18s;}
.notif-footer a:hover{gap:8px;}
.notif-footer a svg{width:11px;height:11px;}

/* Backdrop for mobile */
.notif-backdrop{display:none;position:fixed;inset:0;z-index:999;background:rgba(0,0,0,.4);}
.notif-backdrop.open{display:block;}

@media(max-width:480px){
  .notif-panel{width:calc(100vw - 20px);right:-60px;max-height:70vh;}
}
    `;
    document.head.appendChild(style);
  }

  /* ── Build Panel HTML ── */
  function buildPanel(bellEl) {
    // Wrap bell in notif-wrapper if not already
    let wrapper = bellEl.closest('.notif-wrapper');
    if (!wrapper) {
      wrapper = document.createElement('div');
      wrapper.className = 'notif-wrapper';
      bellEl.parentNode.insertBefore(wrapper, bellEl);
      wrapper.appendChild(bellEl);
    }

    // Remove old panel/count
    const oldPanel = wrapper.querySelector('.notif-panel');
    if (oldPanel) oldPanel.remove();
    const oldCount = wrapper.querySelector('.notif-count');
    if (oldCount) oldCount.remove();
    const oldBackdrop = document.querySelector('.notif-backdrop');
    if (oldBackdrop) oldBackdrop.remove();

    // Count badge
    const count = unreadCount();
    const badge = document.createElement('div');
    badge.className = 'notif-count' + (count === 0 ? ' hide' : '');
    badge.id = 'notifCount';
    badge.textContent = count;
    wrapper.appendChild(badge);

    // Backdrop
    const backdrop = document.createElement('div');
    backdrop.className = 'notif-backdrop';
    backdrop.id = 'notifBackdrop';
    backdrop.onclick = () => closePanel();
    wrapper.appendChild(backdrop);

    // Panel
    const panel = document.createElement('div');
    panel.className = 'notif-panel';
    panel.id = 'notifPanel';
    panel.innerHTML = renderPanel();
    wrapper.appendChild(panel);

    // Update bell click
    bellEl.removeAttribute('onclick');
    bellEl.onclick = (e) => {
      e.stopPropagation();
      togglePanel();
    };

    // Close on outside click
    document.addEventListener('click', (e) => {
      if (panelOpen && !wrapper.contains(e.target)) {
        closePanel();
      }
    });

    // Close on Escape
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && panelOpen) closePanel();
    });
  }

  function renderPanel() {
    const count = unreadCount();
    let itemsHTML = '';

    if (notifications.length === 0) {
      itemsHTML = `
        <div class="notif-empty">
          <div class="notif-empty-icon">🔔</div>
          <div class="notif-empty-text">All caught up!</div>
          <div class="notif-empty-sub">No notifications right now</div>
        </div>`;
    } else {
      itemsHTML = notifications.map(n => `
        <div class="notif-item ${n.type} ${n.read ? '' : 'unread'}" data-id="${n.id}" onclick="window.__notif_read('${n.id}')">
          <div class="notif-icon">${n.icon}</div>
          <div class="notif-body">
            <div class="notif-title">${n.title}</div>
            <div class="notif-text">${n.body}</div>
            <div class="notif-time">${n.time}</div>
          </div>
          <div class="notif-dismiss" onclick="event.stopPropagation();window.__notif_dismiss('${n.id}')" title="Dismiss">✕</div>
        </div>`).join('');
    }

    return `
      <div class="notif-header">
        <h3>Notifications ${count > 0 ? `<span class="nh-count">${count} new</span>` : ''}</h3>
        <div class="notif-actions">
          ${count > 0 ? '<button class="notif-btn" onclick="window.__notif_readAll()">Mark all read</button>' : ''}
          ${notifications.length > 0 ? '<button class="notif-btn danger" onclick="window.__notif_clearAll()">Clear all</button>' : ''}
        </div>
      </div>
      <div class="notif-list">${itemsHTML}</div>
      <div class="notif-footer">
        <a href="activity-logs.html">View Activity Logs <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg></a>
      </div>`;
  }

  function refreshPanel() {
    const panel = document.getElementById('notifPanel');
    if (panel) panel.innerHTML = renderPanel();
    updateBadge();
    saveNotifications();
  }

  function updateBadge() {
    const badge = document.getElementById('notifCount');
    const count = unreadCount();
    if (badge) {
      badge.textContent = count;
      badge.classList.toggle('hide', count === 0);
    }
    // Also update bell dot if exists
    const dot = document.querySelector('.tb-bell-dot, .tb-bell-badge');
    if (dot) dot.style.display = count > 0 ? '' : 'none';
  }

  function togglePanel() {
    panelOpen = !panelOpen;
    const panel = document.getElementById('notifPanel');
    const backdrop = document.getElementById('notifBackdrop');
    if (panel) panel.classList.toggle('open', panelOpen);
    if (backdrop) backdrop.classList.toggle('open', panelOpen);
  }

  function closePanel() {
    panelOpen = false;
    const panel = document.getElementById('notifPanel');
    const backdrop = document.getElementById('notifBackdrop');
    if (panel) panel.classList.remove('open');
    if (backdrop) backdrop.classList.remove('open');
  }

  /* ── Public API ── */
  window.__notif_read = function (id) {
    const n = notifications.find(x => x.id === id);
    if (n) { n.read = true; refreshPanel(); }
  };

  window.__notif_dismiss = function (id) {
    notifications = notifications.filter(x => x.id !== id);
    refreshPanel();
  };

  window.__notif_readAll = function () {
    notifications.forEach(n => n.read = true);
    refreshPanel();
  };

  window.__notif_clearAll = function () {
    notifications = [];
    refreshPanel();
  };

  // Push a new notification programmatically
  window.pushNotification = function (opts) {
    const n = {
      id: 'n_' + Date.now(),
      type: opts.type || 'info',
      icon: opts.icon || 'ℹ️',
      title: opts.title || 'Notification',
      body: opts.body || '',
      time: 'Just now',
      read: false
    };
    notifications.unshift(n);
    refreshPanel();
  };

  window.initializeNotifications = init;

  /* ── Init ── */
  function init() {
    injectCSS();
    loadNotifications();

    const userId = localStorage.getItem('userId') || localStorage.getItem('cypr_user_id');
    if (userId) {
      fetchUserNotifications(userId);
    }

    // Find the bell element
    const bell = document.querySelector('#alertsBellTrigger, .tb-bell');
    if (bell) {
      buildPanel(bell);
    }
  }

  // Auto-init on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
