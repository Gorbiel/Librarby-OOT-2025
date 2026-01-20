from __future__ import annotations

from seeder.config import load_config
from seeder.http_client import HttpClient
from seeder.auth import login_admin

# as you implement more, import them here
from seeder.modules import authors, publishers, books, book_editions, book_copies, users, librarians, rentals, reviews


def run():
    cfg = load_config()

    token = login_admin(
        base_url=cfg.base_url,
        username=cfg.admin.username,
        password=cfg.admin.password,
    )

    client = HttpClient(base_url=cfg.base_url, token=token)

    state: dict = {}

    # Seed order matters
    state = authors.seed(client, cfg, state)
    state = publishers.seed(client, cfg, state)
    state = books.seed(client, cfg, state)
    state = book_editions.seed(client, cfg, state)
    state = book_copies.seed(client, cfg, state)
    state = users.seed(client, cfg, state)
    # state = librarians.seed(client, cfg, state)
    state = rentals.seed(client, cfg, state)
    # state = reviews.seed(client, cfg, state)

    print("Seeding finished.")
    print("State summary:")
    for k, v in state.items():
        if isinstance(v, list):
            print(f"  {k}: {len(v)}")
        else:
            print(f"  {k}: {v}")


if __name__ == "__main__":
    run()
