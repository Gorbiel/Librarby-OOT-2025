from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Dict, Optional
import requests


class ApiError(RuntimeError):
    def __init__(
            self,
            *,
            status_code: int,
            method: str,
            url: str,
            body: Any,
    ):
        self.status_code = status_code
        self.method = method
        self.url = url
        self.body = body

        super().__init__(
            f"{method} {url} -> {status_code}\n"
            f"Response body: {body}"
        )


@dataclass
class HttpClient:
    base_url: str
    token: Optional[str] = None
    timeout_s: int = 20

    def _headers(self) -> Dict[str, str]:
        h = {"Accept": "application/json"}
        if self.token:
            h["Authorization"] = f"Bearer {self.token}"
        return h

    def get(self, path: str, params: Optional[Dict[str, Any]] = None) -> Any:
        url = self.base_url.rstrip("/") + path
        r = requests.get(url, params=params, headers=self._headers(), timeout=self.timeout_s)
        return self._handle(r, "GET", url)

    def post(self, path: str, json: Optional[Dict[str, Any]] = None) -> Any:
        url = self.base_url.rstrip("/") + path
        r = requests.post(url, json=json, headers=self._headers(), timeout=self.timeout_s)
        return self._handle(r, "POST", url)

    def put(self, path: str, json: Optional[Dict[str, Any]] = None) -> Any:
        url = self.base_url.rstrip("/") + path
        r = requests.put(url, json=json, headers=self._headers(), timeout=self.timeout_s)
        return self._handle(r, "PUT", url)

    def patch(self, path: str, json: Optional[Dict[str, Any]] = None) -> Any:
        url = self.base_url.rstrip("/") + path
        r = requests.patch(url, json=json, headers=self._headers(), timeout=self.timeout_s)
        return self._handle(r, "PATCH", url)

    def delete(self, path: str) -> Any:
        url = self.base_url.rstrip("/") + path
        r = requests.delete(url, headers=self._headers(), timeout=self.timeout_s)
        return self._handle(r, "DELETE", url)

    def _handle(self, r: requests.Response, method: str, url: str) -> Any:
        # 204 has no body
        if r.status_code == 204:
            return None

        content_type = (r.headers.get("Content-Type") or "").lower()
        is_json = "application/json" in content_type

        if 200 <= r.status_code < 300:
            return r.json() if is_json and r.text else (r.text if r.text else None)

        # ---- Error handling ----
        if is_json:
            try:
                body = r.json()
            except Exception:
                body = r.text
        else:
            body = r.text

        raise ApiError(
            status_code=r.status_code,
            method=method,
            url=url,
            body=body,
        )
