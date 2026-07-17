export const state = {
  // Config
  apiBase: window.CYPR_TECH_API_BASE || '',

  // Dashboard Stats
  nSent: 0,
  eSent: 0,

  // User Directory State
  pg: 1,
  PS: 10,
  users: [],
  fu: [], // filtered users
  sel: new Set(),
  currentSortField: 'name',
  currentSortDir: 'asc',

  // Communication Center State
  compType: 'email',
  activeTmplKey: 'welcome',
  commHistory: [],
  commQueue: [],
  commTemplates: {},

  // Security Center State
  rawActivityLogs: [],
  aLog: [],
  securityLogs: [],
  threatLogs: [],
  fraudAlerts: [],
  blacklistRules: [],

  // Developer State
  apiKeys: [],
  webhooks: [],
  webhookDeliveries: [],
  apiLogs: [],
  inspectorRequests: [],

  // UI State
  confCB: null
};

// Initializer for dynamic mock state (now empty, handled by controllers fetching from API)
export function initStore() {
  state.users = [];
  state.fu = [];
}
