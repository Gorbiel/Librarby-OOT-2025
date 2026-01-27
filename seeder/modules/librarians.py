# seeder/modules/librarians.py
from __future__ import annotations

from typing import Any, Dict, List
import random

from seeder.http_client import HttpClient, ApiError


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed librarians by promoting existing users.

    Uses:
      - POST /api/v1/librarians with { "username": "..." } (ADMIN-only) :contentReference[oaicite:6]{index=6}

    Requires:
      - state["reader_usernames"] from users.seed()

    Optional config:
      - cfg.SEED_LIBRARIANS (defaults to ~20% of readers, min 1, max 10)

    Writes:
      state["librarian_usernames"] = [...]
    """
    reader_usernames: List[str] = list(state.get("reader_usernames") or [])
    if not reader_usernames:
        state["librarian_usernames"] = []
        return state

    n_cfg = int(getattr(cfg, "seed_librarians", 0) or 0)
    if n_cfg <= 0:
        n_cfg = max(1, min(10, len(reader_usernames) // 5))  # ~20%

    n = min(n_cfg, len(reader_usernames))

    rng = random.Random(getattr(cfg, "seed_random_seed", None) or None)
    picks = rng.sample(reader_usernames, k=n)

    promoted: List[str] = []

    for username in picks:
        payload = {"username": username}
        try:
            client.post("/api/v1/librarians", json=payload)
            promoted.append(username)
        except ApiError as e:
            # If your backend returns 409/400 for "already librarian" etc. — skip.
            if e.status_code in (400, 409):
                promoted.append(username)
                continue
            raise

    state["librarian_usernames"] = promoted

    # Optional: update credentials list with role="LIBRARIAN" for promoted users
    creds = state.get("user_credentials")
    if isinstance(creds, list):
        for item in creds:
            if isinstance(item, dict) and item.get("username") in set(promoted):
                item["role"] = "LIBRARIAN"

    return state
