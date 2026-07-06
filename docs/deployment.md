# Production Deployment Guide — CYPR Tech

This document details the configuration and deployment procedures for running CYPR Tech in a production-grade AWS ecosystem.

## AWS EC2 Virtual Machine Host

### 1. Swap Memory Configuration
Constrained compute instances (e.g. t3.micro with 1GB or 2GB RAM) can suffer JVM crashes due to Memory Starvation. Enforce a **2 GB Swap Memory space**:
```bash
# Allocate swap space
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make permanent in fstab
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 2. JVM Runtime Configurations
To avoid JVM out-of-memory errors on limited containers, constrain Heap allocation using standard parameters:
- `-Xmx400m`: Maximum Heap Memory allocation (400MB)
- `-Xms200m`: Initial Startup Heap Memory allocation (200MB)

---

## Production Container Configuration (Docker)

### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/cypr-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "app.jar"]
```

### Running the Container
```bash
docker build -t cypr-backend:latest .
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://[RDS-ENDPOINT]:5432/cypr \
  -e SPRING_DATASOURCE_USERNAME=cypr_user \
  -e SPRING_DATASOURCE_PASSWORD=***SECURE*** \
  -e VIRUSTOTAL_API_KEY=***SECURE*** \
  -e CYPR_JWT_SECRET=***SECURE*** \
  -e BREVO_API_KEY=***SECURE*** \
  -p 8080:8080 \
  cypr-backend:latest
```

---

## AWS RDS PostgreSQL managed database cluster
1. Spin up an AWS RDS PostgreSQL instance (v13+).
2. Configure Security Groups to allow inbound TCP traffic on port **5432** only from the EC2 instance's elastic IP.
3. Database backups are handled automatically by AWS RDS daily with multi-AZ replication.
