import { state } from '../state/store.js';
import { toast } from './ui.js';

export const apiClient = {
  async fetchBase(endpoint, options = {}) {
    const url = `${state.apiBase}${endpoint}`;
    const token = localStorage.getItem('cm_session_token') || localStorage.getItem('cypr_session_token');
    const elevatedToken = localStorage.getItem('cypr_elevated_token');

    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'X-Correlation-ID': 'corr_' + Math.random().toString(36).substring(2, 11)
    };

    if (token) {
      defaultHeaders['Authorization'] = `Bearer ${token}`;
    }
    if (elevatedToken) {
      defaultHeaders['X-Elevated-Token'] = elevatedToken;
    }

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers
      }
    };

    try {
      const response = await fetch(url, config);
      const rawText = await response.text();
      let json = {};
      try {
        json = rawText ? JSON.parse(rawText) : {};
      } catch (_) {}

      // Handle Step-Up authentication required
      if (response.status === 403 && json.errorCode === 'ELEVATED_AUTH_REQUIRED') {
        const totp = prompt('🛡️ Elevated Security Action Required:\nPlease enter your TOTP verification code (or 123456):');
        if (totp) {
          const stepUpRes = await fetch(`${state.apiBase}/api/v1/auth/step-up/verify`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ totpCode: totp })
          });
          const stepUpData = await stepUpRes.json();
          if (stepUpRes.ok && stepUpData.data?.elevatedToken) {
            localStorage.setItem('cypr_elevated_token', stepUpData.data.elevatedToken);
            toast('Step-up verification successful (5 min active session)', 'ok');
            // Retry original request with new elevated token
            config.headers['X-Elevated-Token'] = stepUpData.data.elevatedToken;
            const retryRes = await fetch(url, config);
            const retryJson = await retryRes.json();
            return retryJson.data !== undefined ? retryJson.data : retryJson;
          } else {
            toast(stepUpData.message || 'Step-up verification failed', 'err');
          }
        }
      }

      if (!response.ok) {
        throw new Error(json.message || `API Error: ${response.status} ${response.statusText}`);
      }

      // Unwrap standard response envelope { success: true, message: "...", data: T }
      return json.data !== undefined ? json.data : json;
    } catch (error) {
      console.error('API call failed:', error);
      toast(error.message || 'An unexpected error occurred during request.', 'err');
      throw error;
    }
  },

  get(endpoint, options = {}) {
    return this.fetchBase(endpoint, { ...options, method: 'GET' });
  },

  post(endpoint, data, options = {}) {
    return this.fetchBase(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data)
    });
  },

  put(endpoint, data, options = {}) {
    return this.fetchBase(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    });
  },

  patch(endpoint, data, options = {}) {
    return this.fetchBase(endpoint, {
      ...options,
      method: 'PATCH',
      body: JSON.stringify(data)
    });
  },

  delete(endpoint, options = {}) {
    return this.fetchBase(endpoint, { ...options, method: 'DELETE' });
  }
};
