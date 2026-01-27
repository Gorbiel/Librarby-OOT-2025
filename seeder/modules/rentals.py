# seeder/modules/rentals.py
from __future__ import annotations

from typing import Any, Dict, List
from datetime import date, timedelta
import random

from seeder.http_client import HttpClient


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed rentals using POST /api/v1/rentals (or similar endpoint).

    Requires:
      - state["book_copy_ids"]
      - state["reader_ids"]
    """
    copy_ids: List[int] = list(state.get("book_copy_ids") or [])
    reader_ids: List[int] = list(state.get("reader_ids") or [])

    if not copy_ids or not reader_ids:
        raise RuntimeError("rentals.seed requires book_copy_ids and reader_ids")

    n = int(getattr(cfg, "seed_rentals", 0) or 0)
    created_ids: List[int] = []

    for i in range(n):
        copy_id = random.choice(copy_ids)
        reader_id = random.choice(reader_ids)

        # Due date = today + 14-30 days
        due_date = (date.today() + timedelta(days=random.randint(14, 30))).isoformat()

        payload = {
            "readerId": reader_id,
            "copyId": copy_id,
            "dueDate": due_date
        }

        rental = client.post("/api/v1/rentals", json=payload)

        rental_id = rental.get("id") if isinstance(rental, dict) else None
        if rental_id is None:
            raise RuntimeError(f"Rental created but id missing: {rental}")

        created_ids.append(rental_id)

    state["rental_ids"] = created_ids
    return state
