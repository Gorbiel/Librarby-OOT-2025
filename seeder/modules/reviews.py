# seeder/modules/reviews.py
from __future__ import annotations

from typing import Any, Dict, List, Optional, Set, Tuple
import random

from seeder.http_client import HttpClient, ApiError

RATING_TEXTS: Dict[int, str] = {
    1: "Terrible, would not recommend.",
    2: "Very poor — many issues.",
    3: "Below average, not great.",
    4: "Disappointing overall.",
    5: "Fair — some good and some bad.",
    6: "Okay, had enjoyable parts.",
    7: "Good — I liked it.",
    8: "Very good, strong recommendation.",
    9: "Excellent — highly recommended.",
    10: "Perfect — an all time favorite.",
}


def _first_int_from(obj: Dict[str, Any], keys: Tuple[str, ...]) -> Optional[int]:
    for k in keys:
        if k in obj and obj[k] is not None:
            try:
                return int(obj[k])
            except (ValueError, TypeError):
                continue
    return None


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed reviews for books that were actually rented and returned.

    Prefers:
      - state["returned_rental_ids"]

    Fallback:
      - GET /api/v1/rentals?active=false

    Writes:
      - state["review_ids"]
      - state["reviews_created"]
    """
    returned_ids: List[int] = list(state.get("returned_rental_ids") or [])

    rentals_list: List[Dict[str, Any]] = []

    # If explicit returned rental ids are provided, fetch those rentals
    if returned_ids:
        for rid in returned_ids:
            try:
                r = client.get(f"/api/v1/rentals/{rid}")
            except ApiError:
                continue
            if isinstance(r, dict):
                rentals_list.append(r)
    else:
        # Prefer the endpoint that lists only returned rentals
        try:
            resp = client.get("/api/v1/rentals", params={"active": "false"})
        except ApiError:
            resp = None

        # Accept either {"rentals": [...]} or a raw list
        if isinstance(resp, dict) and isinstance(resp.get("rentals"), list):
            rentals_list = [r for r in resp["rentals"] if isinstance(r, dict)]
        elif isinstance(resp, list):
            rentals_list = [r for r in resp if isinstance(r, dict)]

    if not rentals_list:
        raise RuntimeError("reviews.seed requires returned rentals (state['returned_rental_ids'] or GET /api/v1/rentals?active=false)")

    # limit how many reviews to create (default: all returned rentals)
    max_reviews = int(getattr(cfg, "seed_reviews", len(rentals_list)) or len(rentals_list))

    created_review_ids: List[int] = []
    created_count = 0
    used_pairs: Set[Tuple[int, int]] = set()  # (readerId, editionId_or_bookId)

    # shuffle and take up to max_reviews
    random.shuffle(rentals_list)
    selected = rentals_list[:max_reviews]

    for rental in selected:
        reader_id = _first_int_from(rental, ("readerId", "reader_id", "readerId"))
        edition_id = _first_int_from(rental, ("editionId", "bookEditionId", "edition_id", "book_edition_id"))
        book_id = _first_int_from(rental, ("bookId", "book_id"))

        if reader_id is None:
            continue

        # need at least a book id to POST to the per-book endpoint
        if book_id is None and edition_id is None:
            continue

        # use edition if available for duplicate detection, otherwise fall back to book id
        dup_key_val = edition_id if edition_id is not None else book_id
        if dup_key_val is None:
            continue

        pair = (reader_id, dup_key_val)
        if pair in used_pairs:
            continue
        used_pairs.add(pair)

        rating = random.randint(1, 10)
        text = RATING_TEXTS.get(rating, "")

        payload: Dict[str, Any] = {
            "readerId": reader_id,
            "rating": rating,
            "text": text,
        }
        if edition_id is not None:
            payload["bookEditionId"] = edition_id

        # If book_id is missing but edition_id exists, try to post using edition as book path parameter.
        post_book_id = book_id if book_id is not None else edition_id

        try:
            resp = client.post(f"/api/v1/books/{post_book_id}/reviews", json=payload)
        except ApiError:
            # skip failures (duplicate, validation, etc.)
            continue

        created_count += 1
        if isinstance(resp, dict) and resp.get("id") is not None:
            try:
                created_review_ids.append(int(resp["id"]))
            except Exception:
                pass

    state["review_ids"] = created_review_ids
    state["reviews_created"] = created_count
    return state
