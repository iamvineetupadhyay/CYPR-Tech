// Centralized API configuration for CYPR frontend.
// Automatically switches between Localhost and Production AWS Backend!

(function() {
    const hostname = window.location.hostname;
    const isLocalDev = ['localhost', '127.0.0.1', ''].includes(hostname) ||
                       hostname.startsWith('192.168.') ||
                       hostname.startsWith('10.') ||
                       hostname.startsWith('172.16.') ||
                       hostname.startsWith('172.17.') ||
                       hostname.startsWith('172.18.') ||
                       hostname.startsWith('172.19.') ||
                       hostname.startsWith('172.20.') ||
                       hostname.startsWith('172.21.') ||
                       hostname.startsWith('172.22.') ||
                       hostname.startsWith('172.23.') ||
                       hostname.startsWith('172.24.') ||
                       hostname.startsWith('172.25.') ||
                       hostname.startsWith('172.26.') ||
                       hostname.startsWith('172.27.') ||
                       hostname.startsWith('172.28.') ||
                       hostname.startsWith('172.29.') ||
                       hostname.startsWith('172.30.') ||
                       hostname.startsWith('172.31.') ||
                       hostname.endsWith('.local');
    
    // 🔥 Tumhara valid AWS Production API URL
    const AWS_PRODUCTION_API = 'https://cypr-api.duckdns.org';
    
    // 🛠️ Dynamic local API for mobile/LAN testing!
    // Connect to backend at the same host on port 8080.
    let LOCAL_API = 'http://localhost:8080';
    if (hostname) {
        LOCAL_API = window.location.protocol + '//' + hostname + ':8080';
    }
    
    // 🔗 Dynamically set base URL based on environment so requests go to the right server!
    window.CYPR_TECH_API_BASE = isLocalDev ? LOCAL_API : AWS_PRODUCTION_API;

    // GLOBAL FETCH INTERCEPTOR FOR SECURITY HARDENING
    const originalFetch = window.fetch;
    window.fetch = async function(resource, config = {}) {
        let resourceUrl = typeof resource === 'string' ? resource : (resource instanceof URL ? resource.toString() : '');
        
        // 🛠️ FIX: Agar request short path '/api/' se shuru ho rahi hai, toh uske aage hamara correct Base URL jodd do!
        if (resourceUrl.startsWith('/api/')) {
            resource = window.CYPR_TECH_API_BASE + resourceUrl;
            resourceUrl = resource;
        }
        
        // If request goes to our Spring Boot backend
        if (resourceUrl.startsWith(window.CYPR_TECH_API_BASE)) {
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
