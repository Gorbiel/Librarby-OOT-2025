# seeder/modules/rentals.py
from __future__ import annotations

from typing import Any, Dict, List, Optional
from datetime import date, timedelta
import random

from seeder.http_client import HttpClient, ApiError


def _safe_users_list(resp: Any) -> List[dict]:
    # Expect MultipleUsersResponse: { "users": [ ... ] }
    if isinstance(resp, dict) and isinstance(resp.get("users"), list):
        return resp["users"]
    return []


def _resolve_reader_ids(client: HttpClient, state: Dict[str, Any]) -> List[int]:
    """
    Prefer state["reader_ids"]. If missing/empty, fall back to /api/v1/users and filter role==READER.
    Optionally skip usernames promoted to librarians.
    """
    reader_ids: List[int] = list(state.get("reader_ids") or [])
    if reader_ids:
        return reader_ids

    users_resp = client.get("/api/v1/users")
    users = _safe_users_list(users_resp)

    promoted = set(state.get("librarian_usernames") or [])

    ids: List[int] = []
    for u in users:
        if not isinstance(u, dict):
            continue
        if u.get("role") != "READER":
            continue
        username = u.get("username")
        if isinstance(username, str) and username in promoted:
            # If your model “moves” users between role tables, promoted users might not be in readers anymore.
            continue
        uid = u.get("id")
        if uid is None:
            continue
        try:
            ids.append(int(uid))
        except Exception:
            continue

    return ids


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed rentals using POST /api/v1/rentals

    Requires:
      - state["book_copy_ids"] (from book_copies.seed)
      - readers (prefer state["reader_ids"], otherwise infer from /api/v1/users role==READER)

    Optional config (if you add later):
      - cfg.SEED_RENTALS (default: 0 -> skip)
      - cfg.SEED_RENTAL_DAYS (default: 30)
    """
    n = int(getattr(cfg, "seed_rentals", 0) or 0)
    if n <= 0:
        state.setdefault("rental_ids", [])
        return state

    copy_ids: List[int] = list(state.get("book_copy_ids") or [])
    if not copy_ids:
        raise RuntimeError("rentals.seed requires state['book_copy_ids'] (seed book copies first).")

    reader_ids = _resolve_reader_ids(client, state)
    if not reader_ids:
        raise RuntimeError(
            "No reader IDs found. Either users were not created as READERs, "
            "or /api/v1/users does not include them, or promotion removed them from readers."
        )

    rng = random.Random(getattr(cfg, "seed_random_seed", None) or None)

    rental_days = int(getattr(cfg, "seed_rental_days", 30) or 30)
    if rental_days <= 0:
        rental_days = 30

    created_rental_ids: List[int] = []

    # Try not to reuse the same copy too much
    copy_pool = copy_ids[:]
    rng.shuffle(copy_pool)

    for i in range(n):
        reader_id = rng.choice(reader_ids)

        # choose a copy; if we run out, just keep sampling from all copies
        copy_id = copy_pool[i] if i < len(copy_pool) else rng.choice(copy_ids)

        due = (date.today() + timedelta(days=rng.randint(7, rental_days))).isoformat()

        payload = {
            "readerId": reader_id,
            "copyId": copy_id,
            "dueDate": due,
        }

        try:
            rental = client.post("/api/v1/rentals", json=payload)
        except ApiError as e:
            # Useful debug: print which id failed
            if e.status_code == 404:
                raise RuntimeError(
                    f"Rental create got 404. Payload was readerId={reader_id}, copyId={copy_id}. "
                    f"This strongly suggests readerId is not a Reader PK in DB (or user promotion/role tables). "
                    f"Server body: {e.body}"
                ) from e
            raise

        rid = rental.get("id") if isinstance(rental, dict) else None
        if rid is None:
            raise RuntimeError(f"Rental created but id missing in response: {rental}")

        created_rental_ids.append(int(rid))

    state["rental_ids"] = created_rental_ids
    return state
