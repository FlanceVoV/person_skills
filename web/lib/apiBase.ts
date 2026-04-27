/** 开发阶段默认指向独立后端；生产静态包由同一 FastAPI 提供页面与 API，NEXT_PUBLIC_API_BASE 置空走同源 */

export const defaultApi = "http://127.0.0.1:8000";

export function getApiBase(): string {
  if (typeof process === "undefined" || !process.env) return defaultApi;
  const e = process.env.NEXT_PUBLIC_API_BASE;
  if (e === "") return "";
  if (e != null && e.length > 0) return e;
  if (process.env.NODE_ENV === "production") return "";
  return defaultApi;
}

export function apiUrl(path: string): string {
  const p = path.startsWith("/") ? path : `/${path}`;
  const base = getApiBase();
  if (base === "") return p;
  return `${base.replace(/\/$/, "")}${p}`;
}
