from __future__ import annotations

from typing import Optional
from seeder.http_client import HttpClient, ApiError


def login_admin(base_url: str, username: str, password: str) -> str:
    """
    Logs in using /api/v1/auth/login and returns a JWT token string.
    Adjust parsing here if your auth response DTO changes.
    """
    client = HttpClient(base_url=base_url, token=None)

    # Most common shapes:
    # 1) {"token":"..."} or {"accessToken":"..."}
    # 2) {"data":{"token":"..."}}
    payload = {"username": username, "password": password}
    resp = client.post("/api/v1/auth/login", json=payload)

    token = _extract_token(resp)
    if not token:
        raise ApiError(f"Login succeeded but token not found in response: {resp}")

    return token


def _extract_token(resp) -> Optional[str]:
    if resp is None:
        return None
    if isinstance(resp, dict):
        for key in ("jwtToken", "token", "accessToken", "jwt", "bearerToken"):
            if key in resp and isinstance(resp[key], str):
                return resp[key]

        if "data" in resp and isinstance(resp["data"], dict):
            for key in ("jwtToken", "token", "accessToken", "jwt", "bearerToken"):
                if key in resp["data"] and isinstance(resp["data"][key], str):
                    return resp["data"][key]

    return None

