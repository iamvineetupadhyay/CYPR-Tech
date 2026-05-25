// Centralized API configuration for CYPR frontend.
// Automatically switches between Localhost and Production AWS Backend!

(function() {
    const isLocalDev = ['localhost', '127.0.0.1', ''].includes(window.location.hostname);
    
    // REPLACE THIS URL WITH YOUR AWS EC2 BACKEND PUBLIC IP / DOMAIN NAME (e.g. 'http://13.234.56.78:8080')
    const AWS_PRODUCTION_API = 'https://cypr-api.duckdns.org';
    
    window.CYBERMITRA_API_BASE = isLocalDev ? 'http://localhost:8080' : AWS_PRODUCTION_API;

    // GLOBAL FETCH INTERCEPTOR FOR SECURITY HARDENING
    const originalFetch = window.fetch;
    window.fetch = async function(resource, config = {}) {
        const resourceUrl = typeof resource === 'string' ? resource : (resource instanceof URL ? resource.toString() : '');
        
        // If request goes to our Spring Boot backend
        if (resourceUrl.startsWith(window.CYBERMITRA_API_BASE) || resourceUrl.startsWith('/api/')) {
            const token = localStorage.getItem('cm_session_token');
            if (token) {
                if (!config.headers) {
                    config.headers = {};
                }
                
                // Attach Bearer token safely regardless of headers format
                if (config.headers instanceof Headers) {
                    config.headers.set('Authorization', 'Bearer ' + token);
                } else if (Array.isArray(config.headers)) {
                    config.headers.push(['Authorization', 'Bearer ' + token]);
                } else {
                    config.headers['Authorization'] = 'Bearer ' + token;
                }
            }
        }
        return originalFetch(resource, config);
    };
})();