# extrack-microservice

A Java Spring Boot microservices application for expense and fund tracking. This repository contains both the application source and production-grade Kubernetes manifests implementing zero-trust security, automated certificate management, and a CNCF-native service mesh.

---

## Table of Contents

- [Architecture](#architecture)
- [Services](#services)
- [Local Development (Docker Compose)](#local-development-docker-compose)
- [Kubernetes Deployment](#kubernetes-deployment)
  - [Prerequisites](#prerequisites)
  - [Secrets](#secrets)
  - [Deploy Order](#deploy-order)
- [Service Mesh](#service-mesh)
  - [Zero-Trust mTLS (Linkerd)](#zero-trust-mtls-linkerd)
  - [Resiliency and Observability (ServiceProfiles)](#resiliency-and-observability-serviceprofiles)
- [Gateway (Traefik)](#gateway-traefik)
  - [TLS and Certificate Management](#tls-and-certificate-management)
- [Zero-Trust Network Policies](#zero-trust-network-policies)
- [k8s Directory Reference](#k8s-directory-reference)

---

## Architecture

```
                          HTTPS :443
  Client ──────────────► Traefik (CNCF Incubating)
                              │  TLS terminated here
                              │  cert-manager provisions & rotates certs (ACME)
            ┌─────────────────┼──────────────────┐
            ▼                 ▼                  ▼
       auth-service      fund-service      expense-service
            │                 │                  │
        auth-db           fund-db           expense-db
            └─────────────────┼──────────────────┘
                              │
                   ┌──────────┴──────────┐
                   ▼                     ▼
             config-server         eureka-service
                (8888)               (8761)

  ════════════════ Linkerd mTLS sidecar on every pod ════════════════
```

All service-to-service traffic inside the cluster is encrypted with **mutual TLS** via Linkerd, implementing a zero-trust architecture where every workload must authenticate before any connection is accepted.

---

## Services

| Service | Port | Role |
|---|---|---|
| `config-server` | 8888 | Spring Cloud Config Server — centralises application configuration |
| `eureka-service` | 8761 | Spring Eureka Server — service registry and discovery |
| `gateway-service` | — | Replaced by **Traefik** (see [Gateway](#gateway-traefik)) |
| `auth-service` | 8080 | Authentication and user management (JWT issuance) |
| `fund-service` | 8080 | Fund management |
| `expense-service` | 8080 | Expense tracking |
| `auth-db` | 5432 | PostgreSQL database for auth-service |
| `fund-db` | 5432 | PostgreSQL database for fund-service |
| `expense-db` | 5432 | PostgreSQL database for expense-service |

### Startup Order

Services have init-container health gates that enforce this startup order:

```
config-server → eureka-service → gateway (Traefik)
                              → auth-service (+ auth-db ready)
                              → fund-service (+ fund-db ready)
                              → expense-service (+ expense-db ready)
```

---

## Local Development (Docker Compose)

Create a `.env` file at the repo root (never commit this):

```env
AUTH_POSTGRES_USER=auth_user
AUTH_POSTGRES_PASSWORD=secret
AUTH_POSTGRES_DB=auth_db
AUTH_POSTGRES_PORT=5433

FUND_POSTGRES_USER=fund_user
FUND_POSTGRES_PASSWORD=secret
FUND_POSTGRES_DB=fund_db
FUND_POSTGRES_PORT=5434

EXPENSE_POSTGRES_USER=expense_user
EXPENSE_POSTGRES_PASSWORD=secret
EXPENSE_POSTGRES_DB=expense_db
EXPENSE_POSTGRES_PORT=5435

JWT_SECRET=your-256-bit-secret-at-least-32-chars
```

```bash
docker compose up --build
```

| Service | Local URL |
|---|---|
| Gateway (Spring) | http://localhost:8080 |
| Eureka dashboard | http://localhost:8761 |
| Config server | http://localhost:8888 |

---

## Kubernetes Deployment

### Prerequisites

Install the following cluster-level components once before applying any application manifests.

**1. Linkerd** (CNCF Graduated — zero-trust mTLS)
```bash
curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/install | sh
linkerd install --crds | kubectl apply -f -
linkerd install | kubectl apply -f -
linkerd check
```

**2. cert-manager** (CNCF Incubating — automatic certificate management)
```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/latest/download/cert-manager.yaml
kubectl wait --namespace cert-manager \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/instance=cert-manager \
  --timeout=120s
```

**3. A CNI that supports NetworkPolicies** (e.g. Calico, Cilium, or any cloud-managed CNI)
> Standard cloud-managed clusters (GKE, EKS, AKS) support NetworkPolicies out of the box.

---

### Secrets

Before applying secrets, update the placeholder values in `k8s/secrets/` with real credentials. These files are intentionally **not** using base64 encoding — Kubernetes encodes `stringData` automatically.

```bash
# Edit each file and replace placeholder values, then apply:
kubectl apply -f k8s/secrets/
```

> In production, consider replacing these plain-text secret files with an external secrets manager (e.g. External Secrets Operator + Vault or AWS Secrets Manager).

For cert-manager's ACME issuer, also update the email address in `k8s/cert-manager/cluster-issuer.yaml` before applying.

---

### Deploy Order

```bash
# 1. Namespace (Linkerd injection enabled for all pods)
kubectl apply -f k8s/namespace.yaml

# 2. Certificate infrastructure
kubectl apply -f k8s/cert-manager/

# 3. Zero-trust network isolation
kubectl apply -f k8s/network-policies/

# 4. Secrets
kubectl apply -f k8s/secrets/

# 5. Databases (PVC + Deployment + Service)
kubectl apply -f k8s/auth-db/
kubectl apply -f k8s/fund-db/
kubectl apply -f k8s/expense-db/

# 6. Core infrastructure
kubectl apply -f k8s/config-server/
kubectl apply -f k8s/eureka-service/

# 7. Edge gateway
kubectl apply -f k8s/traefik/

# 8. Domain services
kubectl apply -f k8s/auth-service/
kubectl apply -f k8s/fund-service/
kubectl apply -f k8s/expense-service/

# 9. Ingress routing
kubectl apply -f k8s/gateway-service/ingress.yaml

# 10. Linkerd ServiceProfiles (resiliency + per-route observability)
kubectl apply -f k8s/linkerd/serviceprofiles/
```

---

## Service Mesh

The service mesh design is inspired by [ING's service mesh architecture](https://medium.com/ing-blog/evolving-ings-service-mesh-d00babf6b4c0), which advocates for treating security, resiliency, and observability as infrastructure concerns — invisible to application developers.

### Zero-Trust mTLS (Linkerd)

`k8s/namespace.yaml` carries the annotation:

```yaml
annotations:
  linkerd.io/inject: enabled
```

This causes Linkerd to **automatically inject a proxy sidecar** into every pod in the `extrack` namespace. The sidecar:

- Assigns each workload a **cryptographic identity** (SPIFFE/X.509)
- **Encrypts all service-to-service traffic** with mutual TLS — no code changes required
- Provides **client-side load balancing** using the EWMA (Exponentially Weighted Moving Average) algorithm, removing the need for a dedicated load balancer between services
- Exports golden metrics (request rate, success rate, latency percentiles) to Prometheus automatically

### Resiliency and Observability (ServiceProfiles)

Each domain service has a Linkerd `ServiceProfile` in `k8s/linkerd/serviceprofiles/`. They encode the retry and timeout policy at the route level:

| Route type | Retryable | Timeout | Rationale |
|---|---|---|---|
| `GET` (reads) | Yes | 5s | Idempotent — safe to retry on transient failure |
| `POST` / `PUT` / `DELETE` (writes) | No | 10s | Non-idempotent — retrying could create duplicate records or transactions |

A **retry budget** of 20% applies cluster-wide per service: at most 1 in 5 requests may be a retry, with a minimum floor of 10 retries/second to avoid starving low-traffic routes.

Linkerd uses these profiles to export **per-route** golden metrics, enabling fine-grained dashboards and alerts in Prometheus/Grafana without any instrumentation in application code.

---

## Gateway (Traefik)

The original Spring Cloud Gateway Java service has been replaced with [**Traefik**](https://traefik.io/) (CNCF Incubating). Traefik runs as a Kubernetes Ingress controller and handles all external routing.

| Path prefix | Backend service | Port |
|---|---|---|
| `/auth/**` | `auth-service` | 8080 |
| `/users/**` | `auth-service` | 8080 |
| `/fund/**` | `fund-service` | 8080 |
| `/expense/**` | `expense-service` | 8080 |

Traefik manifests live in `k8s/traefik/`:

| File | Purpose |
|---|---|
| `rbac.yaml` | `ServiceAccount` + `ClusterRole/Binding` — Traefik watches Ingress/Service/Endpoints |
| `deployment.yaml` | Traefik v3.3, HTTP→HTTPS redirect on `:80`, TLS on `:443`, dashboard on `:9000` |
| `service.yaml` | `LoadBalancer` exposing ports 80, 443, and 9000 |

The Ingress resource (`k8s/gateway-service/ingress.yaml`) routes directly to backend services — there is no intermediate Java proxy. Traefik communicates with backends over Linkerd mTLS.

### TLS and Certificate Management

TLS is terminated at Traefik. Certificates are provisioned and rotated automatically by **cert-manager** with no manual intervention — mirroring ING's use of the ACME protocol for automatic certificate lifecycle management.

#### Certificate issuers (`k8s/cert-manager/cluster-issuer.yaml`)

| Issuer | Name | Use |
|---|---|---|
| Self-signed root | `extrack-selfsigned-root` | Bootstraps the internal CA |
| Internal CA | `extrack-internal-ca` | Signs edge certs in dev/staging |
| Let's Encrypt (ACME) | `extrack-letsencrypt` | Signs edge certs in production |

#### Switching to production ACME

1. Update `ops@example.com` in `cluster-issuer.yaml` to a real email address.
2. In `ingress-certificate.yaml`, switch `issuerRef.name` from `extrack-internal-ca` to `extrack-letsencrypt` and update `dnsNames` to your public hostname.
3. Ensure the cluster's LoadBalancer IP is publicly reachable on port 80 for the HTTP-01 ACME challenge.

---

## Zero-Trust Network Policies

`k8s/network-policies/` enforces the principle that no pod may receive a connection it has not explicitly been granted. Policies are additive — the default deny is the baseline, and each additional policy opens exactly one communication path.

```
default-deny-ingress          → blocks all ingress to every pod
allow-traefik-ingress         → Traefik → auth-service, fund-service, expense-service
allow-core-services           → all pods → config-server (8888) and eureka (8761)
                              → fund-service → expense-service (inter-service call)
allow-databases               → auth-service → auth-db
                              → fund-service → fund-db
                              → expense-service → expense-db
```

Linkerd's mTLS provides the identity layer on top of these network-level rules. Together they implement defence-in-depth: a compromised pod cannot reach any service it was not already permitted to call, and even permitted connections require a valid mTLS certificate.

---

## k8s Directory Reference

```
k8s/
├── namespace.yaml                          # extrack namespace (Linkerd injection enabled)
│
├── secrets/
│   ├── auth-secret.yaml                    # AUTH_POSTGRES_*, JWT_SECRET
│   ├── fund-secret.yaml                    # FUND_POSTGRES_*, JWT_SECRET
│   └── expense-secret.yaml                 # EXPENSE_POSTGRES_*, JWT_SECRET
│
├── cert-manager/
│   ├── cluster-issuer.yaml                 # Self-signed root + internal CA + ACME issuer
│   └── ingress-certificate.yaml            # 90-day TLS cert for Traefik edge
│
├── linkerd/
│   └── serviceprofiles/
│       ├── auth-service-profile.yaml       # Per-route retries, timeouts, observability
│       ├── fund-service-profile.yaml
│       └── expense-service-profile.yaml
│
├── network-policies/
│   ├── default-deny-ingress.yaml           # Zero-trust baseline
│   ├── allow-traefik-ingress.yaml          # Traefik → domain services
│   ├── allow-core-services.yaml            # All pods → config-server, eureka
│   └── allow-databases.yaml               # Each service → its own DB only
│
├── traefik/
│   ├── rbac.yaml                           # ServiceAccount + ClusterRole/Binding
│   ├── deployment.yaml                     # Traefik v3.3 (HTTP→HTTPS redirect, :443)
│   └── service.yaml                        # LoadBalancer: 80, 443, 9000
│
├── gateway-service/
│   └── ingress.yaml                        # Route /auth /users /fund /expense → backends
│
├── config-server/
│   ├── deployment.yaml
│   └── service.yaml                        # ClusterIP :8888
│
├── eureka-service/
│   ├── deployment.yaml
│   └── service.yaml                        # ClusterIP :8761
│
├── auth-service/
│   ├── deployment.yaml
│   └── service.yaml                        # ClusterIP :8080
│
├── fund-service/
│   ├── deployment.yaml
│   └── service.yaml
│
├── expense-service/
│   ├── deployment.yaml
│   └── service.yaml
│
├── auth-db/
│   ├── pvc.yaml                            # ReadWriteOnce, 1Gi
│   ├── deployment.yaml                     # postgres:16-alpine, Recreate strategy
│   └── service.yaml                        # ClusterIP :5432
│
├── fund-db/
│   ├── pvc.yaml
│   ├── deployment.yaml
│   └── service.yaml
│
└── expense-db/
    ├── pvc.yaml
    ├── deployment.yaml
    └── service.yaml
```
