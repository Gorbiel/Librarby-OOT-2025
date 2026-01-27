from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Any, List
import random

from seeder.http_client import HttpClient


FIRST_NAMES = [
    "George", "Frank", "Ursula", "Philip", "Isaac", "Mary", "Agatha", "Stephen",
    "Andrzej", "Olga", "Stanisław", "J.R.R.", "F.", "Haruki", "Terry"
]

MIDDLE_NAMES = [
    None, None, None,  # skew towards null
    "Raymond", "Scott", "Roger", "Howard", "Anne", "Marie"
]

LAST_NAMES = [
    "Martin", "Fitzgerald", "Le Guin", "Dick", "Asimov", "Shelley", "Christie",
    "King", "Sapkowski", "Tokarczuk", "Lem", "Tolkien", "Murakami", "Pratchett"
]


def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed authors using POST /api/v1/authors.

    Writes:
      state["author_ids"] = [ ... ]
    """
    n = int(getattr(cfg, "seed_authors", 0) or 0)
    created_ids: List[int] = []

    for _ in range(n):
        first = random.choice(FIRST_NAMES)
        middle = random.choice(MIDDLE_NAMES)
        last = random.choice(LAST_NAMES)

        payload = {
            "firstName": first,          # required
            "middleName": middle,        # optional, can be null
            "lastName": last             # optional, can be null
        }

        author = client.post("/api/v1/authors", json=payload)

        # Expect AuthorResponse: {id, firstName, middleName, lastName}
        author_id = author.get("id") if isinstance(author, dict) else None
        if author_id is None:
            raise RuntimeError(f"Author created but id missing in response: {author}")

        created_ids.append(author_id)

    state["author_ids"] = created_ids
    return state
