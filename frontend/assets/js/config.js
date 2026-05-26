// Centralized API configuration for CYPR frontend.
// Automatically switches between Localhost and Production AWS Backend!

(function() {
    const isLocalDev = ['localhost', '127.0.0.1', ''].includes(window.location.hostname);
    
    // 🔥 Tumhara valid AWS Production API URL
    const AWS_PRODUCTION_API = 'https://cypr-api.duckdns.org';
    const LOCAL_API = 'http://localhost:8080';
    
    // 🔗 Dynamically set base URL based on environment so requests go to the right server!
    window.CYBERMITRA_API_BASE = isLocalDev ? LOCAL_API : AWS_PRODUCTION_API;

    // GLOBAL FETCH INTERCEPTOR FOR SECURITY HARDENING
    const originalFetch = window.fetch;
    window.fetch = async function(resource, config = {}) {
        let resourceUrl = typeof resource === 'string' ? resource : (resource instanceof URL ? resource.toString() : '');
        
        // 🛠️ FIX: Agar request short path '/api/' se shuru ho rahi hai, toh uske aage hamara correct Base URL jodd do!
        if (resourceUrl.startsWith('/api/')) {
            resource = window.CYBERMITRA_API_BASE + resourceUrl;
            resourceUrl = resource;
        }
        
        // If request goes to our Spring Boot backend
        if (resourceUrl.startsWith(window.CYBERMITRA_API_BASE)) {
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
