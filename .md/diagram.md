```mermaid
flowchart LR
  subgraph Internet
    UserClients
  end

  subgraph Network
    Router
    WAF
    LoadBalancer
  end

  subgraph Edge
    Nginx
    CDN
    CertManager
  end

  subgraph CICD
    GitHubActions
    DockerRegistry
    DeployScript
  end

  subgraph Frontend
    NextJS
    MobileApp
  end

  subgraph Backend
    APIGateway
    AuthService
    SpringBootAPI
    WebSocketService
    NotificationService
    Scheduler
    WebhookHandler
  end

  subgraph Data
    PostgreSQL
    ReplicaDB
    Redis
    MinIO
    Elasticsearch
  end

  subgraph External
    RiotAPI
    EmailSMS
    PushService
    Analytics
  end

  subgraph Monitoring
    Prometheus
    Grafana
    Sentry
    ELK
    HealthCheck
  end

  %% Connections
  UserClients -->|HTTPS| Router --> WAF --> LoadBalancer --> Nginx --> CertManager
  Nginx --> CDN
  CDN --> UserClients
  Nginx --> APIGateway

  APIGateway --> AuthService
  APIGateway --> SpringBootAPI
  APIGateway --> WebSocketService

  NextJS -->|API| APIGateway
  MobileApp -->|API| APIGateway

  AuthService --> Redis
  AuthService --> PostgreSQL

  SpringBootAPI --> PostgreSQL
  SpringBootAPI --> ReplicaDB
  SpringBootAPI --> Redis
  SpringBootAPI --> MinIO
  SpringBootAPI --> Elasticsearch
  SpringBootAPI --> RiotAPI
  SpringBootAPI --> EmailSMS
  SpringBootAPI --> NotificationService
  SpringBootAPI --> Scheduler
  SpringBootAPI --> WebhookHandler

  WebSocketService --> Redis

  NotificationService --> PushService
  NotificationService --> EmailSMS

  GitHubActions --> DockerRegistry --> DeployScript
  DeployScript --> Nginx
  DeployScript --> AuthService
  DeployScript --> SpringBootAPI
  DeployScript --> WebSocketService
  DeployScript --> NotificationService

  SpringBootAPI -->|metrics| Prometheus --> Grafana
  SpringBootAPI -->|errors| Sentry
  SpringBootAPI --> ELK
  AuthService --> Prometheus
  AuthService --> Sentry
  AuthService --> ELK
  WebSocketService --> Prometheus
  WebSocketService --> Sentry
  WebSocketService --> ELK
  Nginx --> ELK

  UserClients -->|GET /health| HealthCheck
  HealthCheck --> SpringBootAPI
  HealthCheck --> AuthService
  HealthCheck --> Redis
  ```