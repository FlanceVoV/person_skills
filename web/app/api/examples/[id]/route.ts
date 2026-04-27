import { NextResponse } from "next/server";
import fs from "node:fs/promises";
import path from "node:path";

function fixturesDir(): string {
  return path.resolve(process.cwd(), "..", "fixtures");
}

function safeId(id: string): string | null {
  // allow: a-zA-Z0-9._- only
  const cleaned = (id || "").trim();
  if (!cleaned) return null;
  if (!/^[A-Za-z0-9._-]+$/.test(cleaned)) return null;
  return cleaned;
}

export async function GET(
  _req: Request,
  ctx: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await ctx.params;
    const ok = safeId(id);
    if (!ok) return new NextResponse("Bad example id", { status: 400 });

    const file = path.join(fixturesDir(), `${ok}.json`);
    const raw = await fs.readFile(file, "utf-8");
    const parsed = JSON.parse(raw) as Record<string, unknown>;
    return NextResponse.json(parsed);
  } catch (err) {
    const msg = err instanceof Error ? err.message : "Failed to read fixture";
    // 404 if file missing; otherwise 500
    if (typeof msg === "string" && (msg.includes("ENOENT") || msg.includes("not found"))) {
      return new NextResponse("Not found", { status: 404 });
    }
    return new NextResponse(msg, { status: 500 });
  }
}

