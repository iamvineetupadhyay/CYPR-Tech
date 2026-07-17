import { state } from './state/store.js';
import * as ui from './utils/ui.js';
import * as helpers from './utils/helpers.js';
import * as dashboard from './controllers/dashboardController.js';
import * as navigation from './controllers/navigationController.js';
import * as users from './controllers/usersController.js';
import * as communication from './controllers/communicationController.js';
import * as security from './controllers/securityController.js';
import * as developer from './controllers/developerController.js';
import * as settings from './controllers/settingsController.js';
import * as commandPalette from './controllers/commandPaletteController.js';

// Expose state globally (mostly for debugging)
window.state = state;

// Bind all to window
Object.assign(window, ui);
Object.assign(window, helpers);
Object.assign(window, dashboard);
Object.assign(window, navigation);
Object.assign(window, users);
Object.assign(window, communication);
Object.assign(window, security);
Object.assign(window, developer);
Object.assign(window, settings);
Object.assign(window, commandPalette);

// Bnr functions from original admin.html
window.updBnr = function() {
  const msg = document.getElementById('bnrMsg').value||'Your announcement message...', type = document.getElementById('bnrType').value;
  const imgUrl = document.getElementById('bnrImgUrl').value.trim();
  const fullImg = document.getElementById('bnrFullImg').checked;
  const prev = document.getElementById('bnrPrev'); 
  
  if (prev) {
    prev.className = `bnr-prev bnr-${type}`;
    const cc = document.getElementById('bnrCC');
    if (cc) cc.textContent = document.getElementById('bnrMsg').value.length;

    if (fullImg && imgUrl) {
      prev.innerHTML = `<img src="${imgUrl}" style="width:100%; max-height:80px; object-fit:cover; border-radius:6px; display:block;" alt="Banner Preview">`;
    } else {
      let imgHtml = '';
      if (imgUrl) {
        imgHtml = `<img src="${imgUrl}" style="width:24px; height:24px; border-radius:4px; object-fit:cover; margin-right:8px;" alt="Thum">`;
      }
      const icos = { info:'<circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="8" stroke-linecap="round" stroke-width="3"/>', success:'<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>', warning:'<path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17" stroke-linecap="round" stroke-width="3"/>', critical:'<circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>' };
      
      prev.innerHTML = `
        <span id="bnrIco" style="display:flex;align-items:center;">
          ${imgHtml || icos[type]}
        </span>
        <div class="bnr-txt" id="bnrPrevTxt">${msg}</div>
      `;
    }
  }
};
window.pubBnr = function() {
  const fullImg = document.getElementById('bnrFullImg').checked;
  const imgUrl = document.getElementById('bnrImgUrl').value.trim();
  ui.toast(`Notice bar deployed to Live Network successfully. ${fullImg && imgUrl ? '(With image)' : ''}`, 'ok');
  security.addLog('success', 'Global notice bar published to edge edge network', 'Comms');
};
window.draftBnr = function() { ui.toast('Notice saved as draft.', 'ok'); };
window.clsBnr = function() { ui.toast('Notice unpublished from Live Network.', 'warn'); };

window.dismissUpdateBnr = function(btn) {
  const b = btn.closest('.sys-update-bnr');
  if(b) b.style.display = 'none';
};

// Also we need to close dropdowns when clicking outside
window.addEventListener('click', () => {
  document.querySelectorAll('.dropdown-menu').forEach(m => m.classList.remove('show'));
  const list = document.getElementById('demoDropdownList');
  if (list) list.style.display = 'none';
  const customMenu = document.getElementById('customContextMenu');
  if (customMenu) customMenu.style.display = 'none';
});

// App Initialization
document.addEventListener('DOMContentLoaded', () => {
  dashboard.initCharts();
  
  window.addEventListener('resize', navigation.closeMobileNavOnResize);
  
  // Intersection Observer for animations
  const obs = new IntersectionObserver((es) => {
    es.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); });
  }, { threshold: 0.05, rootMargin: "0px 0px -50px 0px" });
  document.querySelectorAll('.panel, .so-panel, .sys-update-bnr').forEach(el => obs.observe(el));
  
  // Set initial route based on hash or default to overview
  const h = location.hash.substring(1);
  navigation.go(h || 'overview');
  
  // Wait a frame for initialization
  setTimeout(() => {
    // Initial fetch
    users.loadUsers();
    communication.initCommCenter();
    security.initSecurityCenter();
    developer.initDeveloper();
    settings.initColorPicker();
    commandPalette.initCommandPalette();
    
    // Set predefined settings color
    const defaultColor = '#10b981'; // Green from original
    const picker = document.getElementById('brandColorPicker');
    if (picker) {
      document.documentElement.style.setProperty('--accent', defaultColor);
    }
  }, 100);
});
