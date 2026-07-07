// Centralized API configuration for CYPR frontend.
// Automatically switches between Localhost and Production AWS Backend!

(function() {
    const hostname = window.location.hostname;
    
    // Clean and optimized check using regex for local development environments
    const isLocalDev = [
        'localhost', '127.0.0.1', ''
    ].includes(hostname) || 
    /^192\.168\./.test(hostname) || 
    /^10\./.test(hostname) || 
    /^172\.(1[6-9]|2[0-9]|3[0-1])\./.test(hostname) || // Matches 172.16.x.x to 172.31.x.x completely
    hostname.endsWith('.local');
    
    // 🔥 Tumhara valid AWS Production API URL
    const AWS_PRODUCTION_API = 'https://cypr-api.duckdns.org';
    
    // 🛠️ Dynamic local API for mobile/LAN testing!
    // Forces http:// protocol for local development backend to avoid local HTTPS mismatch
    let LOCAL_API = 'http://localhost:8080';
    if (hostname) {
        LOCAL_API = `http://${hostname}:8080`;
    }
    
    // 🔗 Dynamically set base URL based on environment so requests go to the right server!
    window.CYPR_TECH_API_BASE = isLocalDev ? LOCAL_API : AWS_PRODUCTION_API;

    // GLOBAL FETCH INTERCEPTOR FOR SECURITY HARDENING
    const originalFetch = window.fetch;
    window.fetch = async function(resource, config = {}) {
        let resourceUrl = typeof resource === 'string' ? resource : (resource instanceof URL ? resource.toString() : '');
        
        // 🛠️ FIX: Agar request short path '/api/' se shuru ho rahi hai, toh uske aage hamara correct Base URL jodd do!
        if (resourceUrl.startsWith('/api/')) {
            // Check if it already has a trailing slash or avoid double slashes
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
