from __future__ import annotations
from typing import Dict, Any
from seeder.http_client import HttpClient

def seed(client: HttpClient, cfg, state: Dict[str, Any]) -> Dict[str, Any]:
    # TODO: implement
    state.setdefault("reservation_ids", [])
    return state
