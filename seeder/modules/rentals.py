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

    Updates:
      - state["rental_ids"]
      - state["returned_rental_ids"]
      - state["rented_copy_ids"]
    """
    copy_ids: List[int] = list(state.get("book_copy_ids") or [])
    reader_ids: List[int] = list(state.get("reader_ids") or [])

    if not copy_ids or not reader_ids:
        raise RuntimeError("rentals.seed requires book_copy_ids and reader_ids")

    n = int(getattr(cfg, "seed_rentals", 20) or 20)
    returned_pct = float(getattr(cfg, "seed_rentals_returned_pct", 25) or 25)

    created_ids: List[int] = []
    # track copies that are already rented (persisted in state)
    rented_copy_ids: List[int] = list(state.get("rented_copy_ids") or [])
    # working pool of available copies for this run
    available_copy_ids: List[int] = [cid for cid in copy_ids if cid not in rented_copy_ids]

    # map rental_id -> copy_id so we can update rented_copy_ids when returned
    rental_to_copy: Dict[int, int] = {}

    for _ in range(n):
        if not available_copy_ids:
            # no more available copies to rent
            break

        copy_id = random.choice(available_copy_ids)
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
        rental_to_copy[rental_id] = copy_id

        # mark copy as rented for this run and remove from available pool
        if copy_id not in rented_copy_ids:
            rented_copy_ids.append(copy_id)
        if copy_id in available_copy_ids:
            available_copy_ids.remove(copy_id)

    # Return a percentage of the created rentals
    returned_ids: List[int] = []
    for rental_id in created_ids:
        if (random.random() * 100.0) < returned_pct:
            # call the return endpoint for this rental
            client.post(f"/api/v1/rentals/{rental_id}/return")
            returned_ids.append(rental_id)
            # update rented_copy_ids to reflect the returned copy
            copy_id = rental_to_copy.get(rental_id)
            if copy_id is not None and copy_id in rented_copy_ids:
                rented_copy_ids.remove(copy_id)

    state["rental_ids"] = created_ids
    state["returned_rental_ids"] = returned_ids
    state["rented_copy_ids"] = rented_copy_ids
    return state
