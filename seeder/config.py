import os
from dataclasses import dataclass
from dotenv import load_dotenv

load_dotenv("../.env")

def _require(name: str) -> str:
    value = os.getenv(name)
    if not value:
        raise RuntimeError(f"Missing required env var: {name}")
    return value


@dataclass(frozen=True)
class AdminConfig:
    username: str
    password: str
    email: str
    first_name: str
    last_name: str


@dataclass(frozen=True)
class SeederConfig:
    base_url: str
    seed_authors: int
    seed_books: int
    seed_readers: int
    seed_copies_per_edition: int
    admin: AdminConfig


def load_config() -> SeederConfig:
    return SeederConfig(
        base_url=_require("LIBRARBY_BASE_URL"),
        seed_authors=int(os.getenv("SEED_AUTHORS", 10)),
        seed_books=int(os.getenv("SEED_BOOKS", 20)),
        seed_readers=int(os.getenv("SEED_READERS", 10)),
        seed_copies_per_edition=int(os.getenv("SEED_COPIES_PER_EDITION", 2)),
        admin=AdminConfig(
            username=_require("APP_DEV_ADMIN_USERNAME"),
            password=_require("APP_DEV_ADMIN_PASS"),
            email=_require("APP_DEV_ADMIN_EMAIL"),
            first_name=_require("APP_DEV_ADMIN_FIRSTNAME"),
            last_name=_require("APP_DEV_ADMIN_LASTNAME"),
        )
    )
