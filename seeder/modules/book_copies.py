# seeder/modules/book_copies.py
from __future__ import annotations

from typing import Any, Dict, List

from seeder.http_client import HttpClient


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed book copies using POST /api/books/exact-book/create-book.

    Requires:
      - state["book_edition_ids"] (from book_editions.seed)

    Uses:
      - cfg.seed_copies_per_edition (from .env SEED_COPIES_PER_EDITION)

    Writes:
      state["book_copy_ids"] = [ ... ]
      state["book_copy_ids_by_edition"] = { editionId: [copyId, ...], ... }
    """
    edition_ids: List[int] = list(state.get("book_edition_ids") or [])
    if not edition_ids:
        raise RuntimeError("book_copies.seed requires state['book_edition_ids'] (seed book editions first).")

    per_edition = int(getattr(cfg, "seed_copies_per_edition", 0) or 0)
    if per_edition <= 0:
        per_edition = 1

    created_ids: List[int] = []
    by_edition: Dict[int, List[int]] = {}

    for edition_id in edition_ids:
        for _ in range(per_edition):
            payload = {
                "bookEditionId": edition_id,
                "status": "AVAILABLE",
            }

            copy = client.post("/api/books/exact-book/create-book", json=payload)

            copy_id = copy.get("id") if isinstance(copy, dict) else None
            if copy_id is None:
                raise RuntimeError(f"Book copy created but id missing in response: {copy}")

            created_ids.append(copy_id)
            by_edition.setdefault(edition_id, []).append(copy_id)

    state["book_copy_ids"] = created_ids
    state["book_copy_ids_by_edition"] = by_edition
    return state
