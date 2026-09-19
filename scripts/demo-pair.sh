#!/usr/bin/env bash
set -euo pipefail

API="${API_URL:-http://localhost:8080}"
KEYCLOAK="${KEYCLOAK_URL:-http://localhost:8081}"
STAMP="$(date -u +%H%M%S)"
DESCRIPTION="Black leather wallet left on the lobby sofa (${STAMP})"

token() {
  curl -sf -X POST "${KEYCLOAK}/realms/hotel/protocol/openid-connect/token" \
    -d grant_type=password \
    -d client_id=staff-web \
    -d "username=${1}" \
    -d "password=${2}" | python3 -c 'import json,sys; print(json.load(sys.stdin)["access_token"])'
}

HOUSE="$(token housekeeper housekeeper)"
DESK="$(token frontdesk frontdesk)"
MGR="$(token manager manager)"

echo "Logging found item as housekeeping…"
ITEM="$(curl -sf -X POST "${API}/api/items" \
  -H "Authorization: Bearer ${HOUSE}" \
  -F "description=${DESCRIPTION}" \
  -F category=WALLET \
  -F zoneName=Lobby \
  -F mapX=13 \
  -F mapY=60.2)"
echo "${ITEM}" | python3 -m json.tool

echo "Filing a matching loss report as front desk…"
REPORT="$(curl -sf -X POST "${API}/api/loss-reports" \
  -H "Authorization: Bearer ${DESK}" \
  --data-urlencode "guestName=Amelia Chen" \
  --data-urlencode "roomNumber=412" \
  --data-urlencode "contact=amelia@example.com" \
  --data-urlencode "description=${DESCRIPTION}" \
  --data-urlencode "zoneName=Lobby" \
  --data-urlencode "mapX=13" \
  --data-urlencode "mapY=60.2")"
echo "${REPORT}" | python3 -m json.tool

echo "Duty-manager match inbox…"
curl -sf -H "Authorization: Bearer ${MGR}" "${API}/api/matches" | python3 -m json.tool
