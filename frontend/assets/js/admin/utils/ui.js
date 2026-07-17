import { state } from '../state/store.js';

export async function animProg(barId, pctId, txtId, steps) {
  return new Promise(r => {
    let si = 0;
    const nx = () => {
      if (si >= steps.length) { r(); return; }
      const s = steps[si++];
      document.getElementById(barId).style.width = s.at + '%';
      document.getElementById(pctId).textContent = s.at + '%';
      document.getElementById(txtId).textContent = s.lbl;
      setTimeout(nx, s.at === 100 ? 400 : 500);
    };
    nx();
  });
}

export function toast(msg, type = 'inf') {
  const icos = {
    ok: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
    err: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
    inf: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="8" stroke-linecap="round" stroke-width="3"/></svg>'
  };
  const t = document.createElement('div');
  t.className = `toast ${type}`;
  t.innerHTML = icos[type] + `<span>${msg}</span>`;
  document.getElementById('toasts').prepend(t);
  setTimeout(() => t.remove(), 4000);
}

export function openConf(ico, ttl, desc, cb) {
  state.confCB = cb;
  document.getElementById('confIco').className = `conf-ico ${ico}`;
  document.getElementById('confTtl').textContent = ttl;
  document.getElementById('confDesc').innerHTML = desc;
  document.getElementById('confOv').classList.add('show');
}

export function closeConf() {
  document.getElementById('confOv').classList.remove('show');
  state.confCB = null;
}

export function doConf() {
  if (state.confCB) state.confCB();
}

export function simulateSkeletonLoad(event) {
  const skState  = document.getElementById('skState');
  const skLoaded = document.getElementById('skLoaded');
  const btn = event.currentTarget;
  if (!skState || !skLoaded) return;

  skState.style.display  = 'block';
  skLoaded.style.display = 'none';

  const orig = btn.innerHTML;
  btn.disabled = true;
  btn.innerHTML = '<div class="loader-spinner" style="width:14px;height:14px;border:2px solid rgba(255,255,255,.15);border-top:2px solid var(--accent);border-radius:50%;margin-right:6px;display:inline-block;"></div> Loading...';

  setTimeout(() => {
    skState.style.display  = 'none';
    skLoaded.style.display = 'block';
    skLoaded.style.animation = 'none';
    void skLoaded.offsetWidth;
    skLoaded.style.animation = 'fadeIn 0.35s ease';

    btn.disabled  = false;
    btn.innerHTML = orig;
    toast('Data loaded successfully!', 'ok');
  }, 1500);
}

// ── IntersectionObserver Lazy Loading ──
export function initLazyLoader() {
  const io = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('loaded');
        io.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1 });

  function observeAll() {
    document.querySelectorAll('.panel-lazy:not(.loaded)').forEach(el => io.observe(el));
  }
  window._lazyObserve = observeAll;
  observeAll();
}

// Ensure the overlay click closes the confirmation box
export function attachGlobalUIListeners() {
  const confOv = document.getElementById('confOv');
  if (confOv) {
    confOv.addEventListener('click', e => {
      if (e.target === e.currentTarget) closeConf();
    });
  }
}
