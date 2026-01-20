from __future__ import annotations

from typing import Dict, Any, List, Optional
import random

from seeder.http_client import HttpClient


# --------- Enums (hardcoded; no OpenAPI reading) ---------

GENRES = [
    "FICTION",
    "FANTASY",
    "SCIENCE_FICTION",
    "DYSTOPIAN",
    "ACTION_AND_ADVENTURE",
    "MYSTERY",
    "HORROR",
    "THRILLER",
    "HISTORICAL_FICTION",
    "ROMANCE",
    "CONTEMPORARY_FICTION",
    "LITERARY_FICTION",
    "GRAPHIC_NOVEL",
    "SHORT_STORY",
    "NON_FICTION",
    "MEMOIR",
    "BIOGRAPHY",
    "AUTOBIOGRAPHY",
    "HISTORY",
    "TRAVEL",
    "TRUE_CRIME",
    "HUMOR",
    "ESSAYS",
    "GUIDE_HOW_TO",
    "RELIGION_AND_SPIRITUALITY",
    "HUMANITIES",
    "SCIENCE_AND_TECHNOLOGY",
    "PARENTING",
    "SELF_HELP",
    "COOKBOOK",
    "ART_AND_PHOTOGRAPHY",
    "POETRY",
]

AGE_RATINGS = ["EVERYONE", "TODDLER", "CHILDREN", "TEENAGER", "ADULT"]


# --------- Title generator ---------

ADJECTIVES = [
    "Silent", "Lost", "Hidden", "Burning", "Broken", "Ancient", "Neon", "Golden",
    "Midnight", "Wandering", "Forgotten", "Electric", "Crimson", "Winter", "Glass"
]

NOUNS = [
    "City", "Empire", "Forest", "Ocean", "Machine", "Library", "Signal", "Garden",
    "Voyage", "Chronicle", "Dune", "Shadow", "Tower", "Protocol", "Cathedral"
]

CONNECTORS = ["of the", "and the", "under the", "beyond the", "within the", "from the"]


def _random_title() -> str:
    pattern = random.choice([1, 2, 3])
    if pattern == 1:
        return f"{random.choice(ADJECTIVES)} {random.choice(NOUNS)}"
    if pattern == 2:
        return f"The {random.choice(ADJECTIVES)} {random.choice(NOUNS)}"
    return f"{random.choice(ADJECTIVES)} {random.choice(NOUNS)} {random.choice(CONNECTORS)} {random.choice(NOUNS)}"


# --------- Module entrypoint ---------

def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    """
    Seed books using POST /api/v1/books.

    Requires:
      - state["author_ids"] (optional) produced by authors.seed()

    Uses config:
      - cfg.seed_books (from .env SEED_BOOKS)

    Writes:
      - state["book_ids"] = [ ... ]
    """
    # ✅ FIX: read dataclass-style config first
    n = int(
        getattr(cfg, "seed_books", None)
        or getattr(cfg, "SEED_BOOKS", None)  # fallback if someone kept old naming
        or 0
    )

    author_ids: List[int] = list(state.get("author_ids") or [])
    created_book_ids: List[int] = []

    for _ in range(n):
        title = _random_title()

        genre_count = random.choice([1, 1, 2, 2, 3])  # weighted
        genres = random.sample(GENRES, k=min(genre_count, len(GENRES)))

        age_rating: Optional[str] = random.choice(AGE_RATINGS) if random.random() < 0.7 else None

        picked_author_ids: List[int] = []
        if author_ids:
            picked_author_ids = random.sample(author_ids, k=min(random.choice([1, 2, 3]), len(author_ids)))

        payload: Dict[str, Any] = {
            "title": title,
            "genres": genres,
            "ageRating": age_rating,
            "authorIds": picked_author_ids,
        }

        book = client.post("/api/v1/books", json=payload)

        book_id = book.get("id") if isinstance(book, dict) else None
        if book_id is None:
            raise RuntimeError(f"Book created but id missing in response: {book}")

        created_book_ids.append(book_id)

    state["book_ids"] = created_book_ids
    return state
