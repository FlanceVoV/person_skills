import { NextResponse } from "next/server";
import fs from "node:fs/promises";
import path from "node:path";

type ExampleListItem = {
  id: string;
  filename: string;
  display_name?: string;
  skill_id?: string;
};

function fixturesDir(): string {
  // web/ is the Next.js project root at runtime (process.cwd()).
  // fixtures/ is at repo root.
  return path.resolve(process.cwd(), "..", "fixtures");
}

export async function GET() {
  try {
    const dir = fixturesDir();
    const entries = await fs.readdir(dir, { withFileTypes: true });
    const jsonFiles = entries
      .filter((e) => e.isFile() && e.name.toLowerCase().endsWith(".json"))
      .map((e) => e.name)
      .sort((a, b) => a.localeCompare(b));

    const items: ExampleListItem[] = [];
    for (const filename of jsonFiles) {
      const id = filename.replace(/\.json$/i, "");
      try {
        const raw = await fs.readFile(path.join(dir, filename), "utf-8");
        const parsed = JSON.parse(raw) as Record<string, unknown>;
        items.push({
          id,
          filename,
          display_name: typeof parsed.display_name === "string" ? parsed.display_name : undefined,
          skill_id: typeof parsed.skill_id === "string" ? parsed.skill_id : undefined,
        });
      } catch {
        // If any single fixture is invalid JSON, still list it (without metadata).
        items.push({ id, filename });
      }
    }

    return NextResponse.json({ items });
  } catch (err) {
    const msg = err instanceof Error ? err.message : "Failed to list fixtures";
    return new NextResponse(msg, { status: 500 });
  }
}

