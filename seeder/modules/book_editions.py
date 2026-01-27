# seeder/modules/book_editions.py
from __future__ import annotations

from typing import Any, Dict, List
import random

from seeder.http_client import HttpClient

LANGS = ["en", "pl", "de", "fr", "es"]


def _isbn13_check_digit(d12: str) -> str:
    """
    Compute ISBN-13 check digit for the first 12 digits.
    """
    if len(d12) != 12 or not d12.isdigit():
        raise ValueError("d12 must be 12 digits")

    total = 0
    for i, ch in enumerate(d12):
        n = int(ch)
        total += n if (i % 2 == 0) else 3 * n

    check = (10 - (total % 10)) % 10
    return str(check)


def _make_isbn13(seed: int) -> str:
    """
    Generate a *valid* ISBN-13 string with hyphens.
    Not tied to real registrant ranges, but check digit is correct.
    Format example: 978-1-23456-7890-7
    """
    rnd = random.Random(seed)
    d12 = "978" + "".join(str(rnd.randint(0, 9)) for _ in range(9))  # 3 + 9 = 12 digits
    check = _isbn13_check_digit(d12)
    return f"{d12[0:3]}-{d12[3]}-{d12[4:9]}-{d12[9:12]}-{check}"


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed book editions using POST /api/v1/book-editions.

    Requires:
      - state["book_ids"] (from books.seed)
      - state["publisher_ids"] (from publishers.seed)

    Writes:
      state["book_edition_ids"] = [ ... ]
      state["book_id_to_edition_ids"] = { bookId: [editionId, ...], ... }
      state["book_edition_ids_by_book"] = same mapping (alias for consistency)
    """
    book_ids: List[int] = list(state.get("book_ids") or [])
    publisher_ids: List[int] = list(state.get("publisher_ids") or [])

    if not book_ids:
        raise RuntimeError("book_editions.seed requires state['book_ids'] (seed books first).")
    if not publisher_ids:
        raise RuntimeError("book_editions.seed requires state['publisher_ids'] (seed publishers first).")

    # Prefer dataclass-style config field. Fall back to env-style name if you kept it.
    per_book = int(
        getattr(cfg, "seed_editions_per_book", None)
        or getattr(cfg, "SEED_EDITIONS_PER_BOOK", None)
        or 1
    )
    if per_book <= 0:
        per_book = 1

    created_ids: List[int] = []
    book_to_editions: Dict[int, List[int]] = {}

    used_isbns = set()

    for book_id in book_ids:
        book_to_editions[book_id] = []

        for j in range(per_book):
            for attempt in range(10):
                isbn = _make_isbn13(seed=(book_id * 1000 + j * 10 + attempt))
                if isbn not in used_isbns:
                    used_isbns.add(isbn)
                    break
            else:
                raise RuntimeError("Failed to generate a unique ISBN after 10 attempts")

            payload = {
                "bookId": book_id,
                "isbn": isbn,
                "pageCount": random.randint(80, 900),
                "publicationYear": random.randint(1950, 2026),
                "publisherId": random.choice(publisher_ids),
                "language": random.choice(LANGS),
            }

            edition = client.post("/api/v1/book-editions", json=payload)

            edition_id = edition.get("id") if isinstance(edition, dict) else None
            if edition_id is None:
                raise RuntimeError(f"Book edition created but id missing in response: {edition}")

            created_ids.append(edition_id)
            book_to_editions[book_id].append(edition_id)

    state["book_edition_ids"] = created_ids
    state["book_id_to_edition_ids"] = book_to_editions
    state["book_edition_ids_by_book"] = book_to_editions  # alias key (handy for other modules)
    return state
