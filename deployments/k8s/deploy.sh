#!/usr/bin/env bash
# Deploy all RevCart manifests to Kubernetes in dependency order.
#
# Usage:
#   ./deploy.sh            — apply everything
#   ./deploy.sh --delete   — tear down the entire revcart namespace

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ "${1:-}" == "--delete" ]]; then
  echo ">> Tearing down RevCart namespace (this deletes all data volumes too)..."
  kubectl delete namespace revcart --ignore-not-found
  exit 0
fi

echo ">> [1/6] Applying namespace..."
kubectl apply -f "$SCRIPT_DIR/namespace.yaml"

echo ">> [2/6] Applying secrets and configmap..."
kubectl apply -f "$SCRIPT_DIR/secrets.yaml"
kubectl apply -f "$SCRIPT_DIR/configmap.yaml"

echo ">> [3/6] Applying infrastructure (postgres, zookeeper, kafka, redis)..."
kubectl apply -f "$SCRIPT_DIR/infra/"

echo ">> Waiting for Zookeeper..."
kubectl rollout status statefulset/zookeeper -n revcart --timeout=120s 2>/dev/null || \
  kubectl rollout status deployment/zookeeper -n revcart --timeout=120s

echo ">> Waiting for Kafka brokers..."
kubectl rollout status statefulset/kafka -n revcart --timeout=180s

echo ">> Waiting for Postgres instances..."
for db in postgres-auth postgres-product postgres-order postgres-inventory postgres-payment postgres-cart; do
  echo "   waiting for $db..."
  kubectl rollout status statefulset/$db -n revcart --timeout=120s
done

echo ">> [4/6] Applying application services..."
kubectl apply -f "$SCRIPT_DIR/services/"

echo ">> Waiting for application deployments..."
for svc in auth-service product-service order-service inventory-service payment-service cart-service gateway revcart-ui; do
  echo "   waiting for $svc..."
  kubectl rollout status deployment/$svc -n revcart --timeout=120s
done

echo ">> [5/6] Applying HPAs..."
kubectl apply -f "$SCRIPT_DIR/hpa.yaml"

echo ">> [6/6] Applying Ingress..."
kubectl apply -f "$SCRIPT_DIR/ingress.yaml"

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║           RevCart is up and running!                 ║"
echo "╠══════════════════════════════════════════════════════╣"
echo "║  Angular UI : http://revcart.com                     ║"
echo "║  API Gateway: http://revcart.com/api/<service-path>  ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
echo ">> To get the Ingress external IP:"
echo "   kubectl get ingress revcart-ingress -n revcart"
echo ""
echo ">> For local testing, add to /etc/hosts:"
echo "   <ingress-external-ip>  revcart.com"
echo ""
echo ">> To check pod status:"
echo "   kubectl get pods -n revcart"
