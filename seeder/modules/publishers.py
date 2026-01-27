from __future__ import annotations

from typing import Any, Dict, List
import random

from seeder.http_client import HttpClient

PUBLISHER_NAMES = [
    "Penguin Random House",
    "HarperCollins",
    "Simon & Schuster",
    "Hachette Livre",
    "Macmillan Publishers",
    "Oxford University Press",
    "Cambridge University Press",
    "Tor Books",
    "Gollancz",
    "Albatros",
    "Prószyński i S-ka",
    "Znak",
    "Czarne",
    "Wydawnictwo Literackie",
]


def _extract_items(resp: Any) -> List[Dict[str, Any]]:
    """
    Accepts either:
      - {"items": [...]} / {"publishers": [...]} / {"data": [...]}
      - [...] (raw list)
    Returns a list of dicts.
    """
    if isinstance(resp, list):
        return [x for x in resp if isinstance(x, dict)]
    if isinstance(resp, dict):
        for key in ("items", "publishers", "data", "results"):
            v = resp.get(key)
            if isinstance(v, list):
                return [x for x in v if isinstance(x, dict)]
    return []


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed publishers using POST /api/v1/publishers, but avoid 409 conflicts by
    reusing existing publishers (idempotent-ish).
    """
    n = int(getattr(cfg, "seed_publishers", None) or getattr(cfg, "SEED_PUBLISHERS", None) or 0)

    if n <= 0:
        n = min(10, len(PUBLISHER_NAMES))

    # Fetch existing once
    existing_resp = client.get("/api/v1/publishers")
    existing_items = _extract_items(existing_resp)

    name_to_id: Dict[str, int] = {}
    for it in existing_items:
        name = it.get("name")
        pid = it.get("id")
        if isinstance(name, str) and isinstance(pid, int):
            name_to_id[name] = pid

    created_or_reused_ids: List[int] = []

    names_pool = list(PUBLISHER_NAMES)
    random.shuffle(names_pool)

    for i in range(n):
        if i < len(names_pool):
            name = names_pool[i]
        else:
            base = random.choice(PUBLISHER_NAMES)
            # Better than f"{base} {i}" because it collides across runs:
            # include a random number
            name = f"{base} {random.randint(1000, 9999)}"

        # Reuse if already exists
        if name in name_to_id:
            created_or_reused_ids.append(name_to_id[name])
            continue

        payload = {"name": name}
        publisher = client.post("/api/v1/publishers", json=payload)

        pub_id = publisher.get("id") if isinstance(publisher, dict) else None
        if pub_id is None:
            raise RuntimeError(f"Publisher created but id missing in response: {publisher}")

        name_to_id[name] = pub_id
        created_or_reused_ids.append(pub_id)

    state["publisher_ids"] = created_or_reused_ids
    return state
