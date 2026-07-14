#!/bin/bash
# Import transactions from mock-transactions.csv

BASE_URL="http://localhost:8080"
CSV_FILE="$(dirname "$0")/docs/mock-transactions.csv"

# Login
echo "=== Login ==="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "Login failed"
  echo "$LOGIN_RESPONSE"
  exit 1
fi

echo "Token obtained"

# Import transactions
echo ""
echo "=== Importing transactions ==="

tail -n +2 "$CSV_FILE" | while IFS=',' read -r assetCode assetName type quantity price amount tradeDate; do
  JSON="{\"assetCode\":\"$assetCode\",\"assetName\":\"$assetName\",\"type\":$type,\"quantity\":$quantity,\"price\":$price,\"amount\":$amount,\"tradeDate\":\"$tradeDate\"}"

  echo -n "$tradeDate $assetName type=$type "
  RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/transactions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "$JSON")
  HTTP_CODE=$(echo "$RESP" | tail -1)
  echo "-> HTTP $HTTP_CODE"
done

echo ""
echo "=== Done ==="
