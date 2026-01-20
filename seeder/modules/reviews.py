# seeder/modules/reviews.py
from __future__ import annotations

from typing import Any, Dict, List, Optional, Tuple
import random

from seeder.http_client import HttpClient, ApiError


REVIEW_SNIPPETS = [
    "Really enjoyed it.",
    "Solid read — would recommend.",
    "Not my favorite, but well written.",
    "Amazing pacing and atmosphere.",
    "The ending was a bit disappointing.",
    "Great characters and worldbuilding.",
    "Would definitely reread.",
    "Interesting ideas, mixed execution.",
    "A classic for a reason.",
    "Kept me hooked throughout.",
]


def _get_reader_ids_from_state_or_api(client: HttpClient, state: Dict[str, Any]) -> List[int]:
    # Prefer state from your users seeding
    for key in ("reader_ids", "reader_user_ids", "users_reader_ids"):
        ids = state.get(key)
        if isinstance(ids, list) and ids:
            return [int(x) for x in ids]

    # Fallback to API: GET /api/v1/users and filter role == READER
    resp = client.get("/api/v1/users")
    users = (resp or {}).get("users") if isinstance(resp, dict) else None
    if not isinstance(users, list):
        return []

    out: List[int] = []
    for u in users:
        if isinstance(u, dict) and u.get("role") == "READER" and u.get("id") is not None:
            out.append(int(u["id"]))
    return out


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed reviews using:
      - POST /api/v1/books/{bookId}/reviews

    Requires:
      - state["book_ids"] (from books.seed)
      - readers available either in state or via /api/v1/users fallback
        (admin token can usually create on behalf of reader if API allows readerId in body)

    Optional config:
      - cfg.seed_reviews (default: 60)
      - cfg.review_text_ratio (default: 0.6) => 60% include text

    Writes:
      state["review_ids"] = [ ... ] (if response includes id; otherwise stores created count)
      state["reviews_created"] = int
    """
    book_ids: List[int] = list(state.get("book_ids") or [])
    if not book_ids:
        raise RuntimeError("reviews.seed requires state['book_ids'] (seed books first).")

    reader_ids = _get_reader_ids_from_state_or_api(client, state)
    if not reader_ids:
        raise RuntimeError("No readers available to create reviews.")

    n = int(getattr(cfg, "seed_reviews", 60) or 60)
    text_ratio = float(getattr(cfg, "review_text_ratio", 0.6) or 0.6)

    created_review_ids: List[int] = []
    created_count = 0

    # avoid obvious duplicates in one run (many systems enforce unique (readerId, bookId))
    used_pairs: set[Tuple[int, int]] = set()

    for _ in range(n):
        reader_id = random.choice(reader_ids)
        book_id = random.choice(book_ids)

        if (reader_id, book_id) in used_pairs:
            continue
        used_pairs.add((reader_id, book_id))

        payload: Dict[str, Any] = {
            "readerId": reader_id,
            "rating": random.randint(1, 5),
        }

        if random.random() < text_ratio:
            payload["text"] = random.choice(REVIEW_SNIPPETS)

        try:
            resp = client.post(f"/api/v1/books/{book_id}/reviews", json=payload)
            created_count += 1

            # If your ReviewResponse contains id, store it
            if isinstance(resp, dict) and resp.get("id") is not None:
                created_review_ids.append(int(resp["id"]))

        except ApiError as e:
            # Common cases:
            # - 409 conflict: duplicate review
            # - 400 bad request: validation
            # We just skip and continue.
            continue

    state["review_ids"] = created_review_ids
    state["reviews_created"] = created_count
    return state
