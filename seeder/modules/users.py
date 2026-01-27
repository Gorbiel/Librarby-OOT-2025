# seeder/modules/users.py
from __future__ import annotations

from typing import Any, Dict, List, Optional
from dataclasses import asdict
import json
import random
from datetime import date, timedelta
from pathlib import Path

from seeder.http_client import HttpClient, ApiError


FIRST_NAMES = [
    "Jan", "Anna", "Piotr", "Kasia", "Ola", "Marek", "Ewa", "Tomek", "Zosia", "Bartek",
    "John", "Jane", "Alice", "Bob", "Charlie", "Daisy",
]
LAST_NAMES = [
    "Kowalski", "Nowak", "Wiśniewski", "Wójcik", "Kamiński", "Lewandowski",
    "Smith", "Johnson", "Brown", "Taylor", "Anderson",
]


def _random_dob(rng: random.Random) -> str:
    """
    Generates dateOfBirth as YYYY-MM-DD.
    Keep them adult-ish to avoid any age/policy constraints later.
    """
    # 18..65 years old
    today = date.today()
    years = rng.randint(18, 65)
    extra_days = rng.randint(0, 365)
    dob = today - timedelta(days=years * 365 + extra_days)
    return dob.isoformat()


def _safe_users_list(resp: Any) -> List[dict]:
    """
    MultipleUsersResponse: { "users": [ ...UserResponse... ] }
    """
    if isinstance(resp, dict) and isinstance(resp.get("users"), list):
        return resp["users"]
    return []


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed reader users.

    Uses:
      - POST /api/v1/auth/register (public; returns 201 without body) :contentReference[oaicite:3]{index=3}
      - GET  /api/v1/users (secured; returns MultipleUsersResponse {users:[...]}) :contentReference[oaicite:4]{index=4}

    Writes:
      state["reader_usernames"] = [...]
      state["reader_ids"] = [...]
      state["user_credentials_path"] = "..."
      state["user_credentials"] = [ {username,password,role,email,firstName,lastName}, ... ]
    """
    n = int(getattr(cfg, "seed_readers", 0) or 0)
    if n <= 0:
        state["reader_usernames"] = []
        state["reader_ids"] = []
        state["user_credentials"] = []
        return state

    rng = random.Random(getattr(cfg, "seed_random_seed", None) or None)

    # Where to write creds JSON (optional env/config)
    out_path = getattr(cfg, "seed_users_creds_path", None) or "seeded_users.json"
    out_path = str(out_path)

    created_usernames: List[str] = []
    credentials: List[dict] = []

    # Create readers via /auth/register (no body returned)
    for i in range(n):
        first = rng.choice(FIRST_NAMES)
        last = rng.choice(LAST_NAMES)

        username = f"reader_{i:03d}"
        password = f"Reader{i:03d}!Pass"  # meets minLength=8 :contentReference[oaicite:5]{index=5}
        email = f"{username}@example.com"

        payload = {
            "username": username,
            "password": password,
            "email": email,
            "firstName": first,
            "lastName": last,
            "dateOfBirth": _random_dob(rng),
        }

        try:
            client.post("/api/v1/auth/register", json=payload)
            created_usernames.append(username)
            credentials.append(
                {
                    "username": username,
                    "password": password,
                    "role": "READER",
                    "email": email,
                    "firstName": first,
                    "lastName": last,
                }
            )
        except ApiError as e:
            # 409 => user exists; keep going
            if e.status_code == 400:
                # still record creds if you want deterministic logins for reruns
                created_usernames.append(username)
                credentials.append(
                    {
                        "username": username,
                        "password": password,
                        "role": "READER",
                        "email": email,
                        "firstName": first,
                        "lastName": last,
                        "note": "already existed (409)",
                    }
                )
                continue
            raise

    # Resolve ids by fetching /users (admin token required; your HttpClient has it)
    users_resp = client.get("/api/v1/users")
    users_list = _safe_users_list(users_resp)

    username_to_id: Dict[str, int] = {}
    for u in users_list:
        if isinstance(u, dict) and isinstance(u.get("username"), str) and u.get("id") is not None:
            try:
                username_to_id[u["username"]] = int(u["id"])
            except Exception:
                pass

    reader_ids: List[int] = [username_to_id[u] for u in created_usernames if u in username_to_id]

    # Write creds JSON
    Path(out_path).write_text(json.dumps(credentials, indent=2, ensure_ascii=False), encoding="utf-8")

    state["reader_usernames"] = created_usernames
    state["reader_ids"] = reader_ids
    state["user_credentials_path"] = out_path
    state["user_credentials"] = credentials
    return state
