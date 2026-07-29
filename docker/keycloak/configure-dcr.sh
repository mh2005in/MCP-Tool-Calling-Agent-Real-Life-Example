#!/bin/sh
# ============================================================
# One-shot Keycloak bootstrap for MCP Dynamic Client Registration (RFC 7591).
#
# The base realm is imported from realm-immiauto.json(.template). We deliberately
# do NOT add the mcp.* scopes there: supplying a `clientScopes` array in a realm
# import makes Keycloak skip creating its built-in scopes (roles, profile, ...),
# which would drop the realm_access.roles claim the MCP role gate relies on.
#
# So instead this job runs AFTER Keycloak is up and, idempotently:
#   1. Creates the mcp.* client scopes and marks them realm OPTIONAL defaults, so
#      dynamically-registered clients can request them (the MCP server enforces
#      an mcp.* scope per tool).
#   2. Removes the anonymous "Trusted Hosts" client-registration policy, which
#      Keycloak seeds to block anonymous DCR by default -> opens anonymous DCR.
#
# Idempotent: safe to re-run on every `docker compose up`.
# NOTE: no `set -e` on purpose — kcadm lookups routinely exit non-zero and we
# handle every failure explicitly, so `set -e` would abort the job spuriously.
# ============================================================
set -u

KCADM=/opt/keycloak/bin/kcadm.sh
KC_URL="${KEYCLOAK_INTERNAL_URL:-http://keycloak:8080}"
REALM="${KC_REALM:-immiauto}"
ADMIN="${KEYCLOAK_ADMIN:-admin}"
ADMIN_PW="${KEYCLOAK_ADMIN_PASSWORD:-admin}"
POLICY_TYPE=org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy

fail() { echo "[dcr-config] ERROR: $1" >&2; exit 1; }

echo "[dcr-config] waiting for Keycloak at $KC_URL ..."
i=0
until "$KCADM" config credentials --server "$KC_URL" --realm master --user "$ADMIN" --password "$ADMIN_PW" >/dev/null 2>&1; do
  i=$((i + 1))
  [ "$i" -ge 60 ] && fail "Keycloak not reachable after 60 attempts"
  sleep 3
done
echo "[dcr-config] authenticated to Keycloak realm '$REALM'"

# Prints the id of the row whose name column ($2) equals $1 in kcadm id,name CSV ($3).
id_for() {
  printf '%s\n' "$3" | while IFS=, read -r id name; do
    if [ "$name" = "$1" ]; then
      printf '%s' "$id"
      return 0
    fi
  done
}

scopes_csv="$("$KCADM" get client-scopes -r "$REALM" --fields id,name --format csv --noquotes 2>/dev/null)"
# Names already registered as realm optional defaults (the PUT below is NOT idempotent:
# re-assigning an already-registered scope errors, so we assign only when missing).
optional_csv="$("$KCADM" get default-optional-client-scopes -r "$REALM" --fields name --format csv --noquotes 2>/dev/null)"

# True if $1 appears as a whole line in the newline-separated list $2.
in_list() {
  printf '%s\n' "$2" | while IFS= read -r line; do
    [ "$line" = "$1" ] && return 0
  done
}

# Creates the client scope $1 (consent text $2) if absent, then registers it as a
# realm optional default so new clients (incl. DCR ones) may request it.
ensure_scope() {
  name="$1"
  text="$2"
  id="$(id_for "$name" name "$scopes_csv")"
  if [ -n "$id" ]; then
    echo "[dcr-config] scope '$name' already exists (id=$id)"
  else
    "$KCADM" create client-scopes -r "$REALM" \
      -s "name=$name" \
      -s protocol=openid-connect \
      -s 'attributes."include.in.token.scope"=true' \
      -s 'attributes."display.on.consent.screen"=true' \
      -s "attributes.\"consent.screen.text\"=$text" \
      || fail "could not create scope '$name'"
    id="$("$KCADM" get client-scopes -r "$REALM" --fields id,name --format csv --noquotes 2>/dev/null | while IFS=, read -r cid cname; do [ "$cname" = "$name" ] && printf '%s' "$cid"; done)"
    echo "[dcr-config] created scope '$name' (id=$id)"
  fi
  [ -n "$id" ] || fail "no id resolved for scope '$name'"
  if in_list "$name" "$optional_csv"; then
    echo "[dcr-config]   -> '$name' already a realm optional default"
    return 0
  fi
  # PUT membership; -n skips the pre-fetch (this endpoint is PUT-only), body ignored.
  if printf '{}' | "$KCADM" update "default-optional-client-scopes/$id" -r "$REALM" -f - -n >/dev/null 2>&1; then
    echo "[dcr-config]   -> '$name' set as realm optional default"
  else
    fail "could not mark '$name' as a realm optional default"
  fi
}

ensure_scope mcp.tools.read          "List available tools"
ensure_scope mcp.cases.read          "Read case summaries"
ensure_scope mcp.documents.read      "Read documents"
ensure_scope mcp.checklists.generate "Generate checklists"
ensure_scope mcp.messages.draft      "Draft client messages"

# --- Open anonymous Dynamic Client Registration ---
# Keycloak seeds anonymous client-registration policies that block/limit DCR. For a dev-open
# posture we remove three:
#   * "Trusted Hosts"      — otherwise anonymous registration is rejected by host validation.
#   * "Consent Required"   — otherwise every registered client is forced consentRequired=true,
#                            which also blocks non-interactive grants.
#   * "Full Scope Disabled"— otherwise registered clients get fullScopeAllowed=false, which
#                            STRIPS the user's realm roles from the token (realm_access.roles
#                            comes back empty) and the MCP role gate would 403 every real user.
# Re-enable these in production and instead provision trusted clients / explicit role scope
# mappings if you want a tighter posture.
policies_csv="$("$KCADM" get components -r "$REALM" -q "type=$POLICY_TYPE" --fields id,name --format csv --noquotes 2>/dev/null)"
drop_policy() {
  pid="$(id_for "$1" name "$policies_csv")"
  if [ -n "$pid" ]; then
    "$KCADM" delete "components/$pid" -r "$REALM" \
      && echo "[dcr-config] removed anonymous '$1' policy" \
      || fail "could not remove '$1' policy"
  else
    echo "[dcr-config] no anonymous '$1' policy present (already removed)"
  fi
}
drop_policy "Trusted Hosts"
drop_policy "Consent Required"
drop_policy "Full Scope Disabled"
echo "[dcr-config] anonymous Dynamic Client Registration is open"

echo "[dcr-config] done"
