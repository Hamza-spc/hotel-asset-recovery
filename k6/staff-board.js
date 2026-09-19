import http from 'k6/http';
import { check, sleep } from 'k6';

const API = __ENV.API_URL || 'http://localhost:8080';
const KEYCLOAK = __ENV.KEYCLOAK_URL || 'http://localhost:8081';

export const options = {
  scenarios: {
    board: {
      executor: 'constant-vus',
      vus: Number(__ENV.VUS || 10),
      duration: __ENV.DURATION || '30s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
    checks: ['rate>0.99'],
  },
};

export function setup() {
  const health = http.get(`${API}/actuator/health`);
  if (health.status !== 200) {
    throw new Error(`API is not up at ${API}: ${health.status}`);
  }
  return { token: tokenFor('manager', 'manager') };
}

export default function (data) {
  const headers = { Authorization: `Bearer ${data.token}` };

  const calls = [
    ['health', http.get(`${API}/actuator/health`)],
    ['me', http.get(`${API}/api/me`, { headers })],
    ['items', http.get(`${API}/api/items`, { headers })],
    ['reports', http.get(`${API}/api/loss-reports`, { headers })],
    ['zones', http.get(`${API}/api/map/zones`, { headers })],
    ['audit', http.get(`${API}/api/audit`, { headers })],
    ['matches', http.get(`${API}/api/matches`, { headers })],
  ];

  for (const [name, response] of calls) {
    check(response, { [`${name} 200`]: (r) => r.status === 200 });
  }

  sleep(0.25);
}

function tokenFor(username, password) {
  const response = http.post(
    `${KEYCLOAK}/realms/hotel/protocol/openid-connect/token`,
    {
      grant_type: 'password',
      client_id: 'staff-web',
      username,
      password,
    },
    { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } },
  );
  check(response, { 'keycloak token': (r) => r.status === 200 });
  const body = response.json();
  if (!body || !body.access_token) {
    throw new Error(`Keycloak token failed: ${response.status} ${response.body}`);
  }
  return body.access_token;
}
