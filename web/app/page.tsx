"use client";

import { useCallback, useEffect, useMemo, useState } from "react";

const defaultApi = "http://127.0.0.1:8000";
const API_BASE =
  (typeof process !== "undefined" && process.env.NEXT_PUBLIC_API_BASE) || defaultApi;

export default function Home() {
  const [displayName, setDisplayName] = useState("");
  const [skillId, setSkillId] = useState("");
  const [version, setVersion] = useState("1.0.0");
  const [purposeDescription, setPurposeDescription] = useState("");
  const [activationHints, setActivationHints] = useState("");
  const [inScope, setInScope] = useState("");
  const [outOfScope, setOutOfScope] = useState("");
  const [basicInfo, setBasicInfo] = useState("");
  const [latestUpdates, setLatestUpdates] = useState("");
  const [rolePlayRules, setRolePlayRules] = useState("");
  const [persona, setPersona] = useState("");
  const [chatRecords, setChatRecords] = useState("");
  const [workAchievements, setWorkAchievements] = useState("");
  const [analystRules, setAnalystRules] = useState("");
  const [workflow, setWorkflow] = useState("");
  const [decisionHeuristics, setDecisionHeuristics] = useState("");
  const [timeline, setTimeline] = useState("");
  const [valuesAntipatterns, setValuesAntipatterns] = useState("");
  const [intellectualLineage, setIntellectualLineage] = useState("");
  const [honestySupplement, setHonestySupplement] = useState("");
  const [files, setFiles] = useState<FileList | null>(null);

  const [examples, setExamples] = useState<
    { id: string; filename: string; display_name?: string; skill_id?: string }[]
  >([]);
  const [exampleId, setExampleId] = useState<string>("");

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: "ok" | "err"; text: string } | null>(null);
  const [lastZipName, setLastZipName] = useState<string | null>(null);

  const selectedExample = useMemo(
    () => examples.find((e) => e.id === exampleId) || null,
    [examples, exampleId]
  );

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const res = await fetch("/api/examples", { cache: "no-store" });
        if (!res.ok) return;
        const data = (await res.json()) as { items?: unknown };
        const items = Array.isArray((data as any).items) ? ((data as any).items as any[]) : [];
        const normalized = items
          .map((x) => ({
            id: typeof x?.id === "string" ? x.id : "",
            filename: typeof x?.filename === "string" ? x.filename : "",
            display_name: typeof x?.display_name === "string" ? x.display_name : undefined,
            skill_id: typeof x?.skill_id === "string" ? x.skill_id : undefined,
          }))
          .filter((x) => x.id && x.filename);
        if (!cancelled) {
          setExamples(normalized);
          if (!exampleId && normalized.length) setExampleId(normalized[0]!.id);
        }
      } catch {
        // ignore
      }
    })();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadExample = useCallback(async () => {
    if (!exampleId) return;
    setMessage(null);
    try {
      const res = await fetch(`/api/examples/${encodeURIComponent(exampleId)}`, { cache: "no-store" });
      if (!res.ok) {
        const t = await res.text();
        setMessage({ type: "err", text: t || `加载示例失败 ${res.status}` });
        return;
      }
      const d = (await res.json()) as Record<string, unknown>;

      const s = (k: string) => (typeof d[k] === "string" ? (d[k] as string) : "");

      // 对齐表单字段（忽略 _comment / expected_zip_basename）
      setDisplayName(s("display_name"));
      setSkillId(s("skill_id"));
      setVersion(s("version") || "1.0.0");
      setPurposeDescription(s("purpose_description"));
      setActivationHints(s("activation_hints"));
      setInScope(s("in_scope"));
      setOutOfScope(s("out_of_scope"));
      setBasicInfo(s("basic_info"));
      setLatestUpdates(s("latest_updates"));
      setRolePlayRules(s("role_play_rules"));
      setPersona(s("persona"));
      setChatRecords(s("chat_records"));
      setWorkAchievements(s("work_achievements"));
      setAnalystRules(s("analyst_rules"));
      setWorkflow(s("workflow"));
      setDecisionHeuristics(s("decision_heuristics"));
      setTimeline(s("timeline"));
      setValuesAntipatterns(s("values_antipatterns"));
      setIntellectualLineage(s("intellectual_lineage"));
      setHonestySupplement(s("honesty_supplement"));

      // files 不可从 JSON 回填
      setFiles(null);
      setMessage({
        type: "ok",
        text: `已加载示例：${selectedExample?.display_name || selectedExample?.filename || exampleId}（附件需手动选择）`,
      });
    } catch (err) {
      setMessage({
        type: "err",
        text: err instanceof Error ? err.message : "加载示例失败",
      });
    }
  }, [exampleId, selectedExample]);

  const onSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      setMessage(null);
      setLastZipName(null);
      if (!displayName.trim()) {
        setMessage({ type: "err", text: "请填写展示名称（用于生成 {slug}-skill.zip）" });
        return;
      }
      setLoading(true);
      try {
        const fd = new FormData();
        fd.set("display_name", displayName.trim());
        if (skillId.trim()) fd.set("skill_id", skillId.trim());
        fd.set("version", version.trim() || "1.0.0");
        fd.set("purpose_description", purposeDescription);
        fd.set("activation_hints", activationHints);
        fd.set("in_scope", inScope);
        fd.set("out_of_scope", outOfScope);
        fd.set("basic_info", basicInfo);
        fd.set("latest_updates", latestUpdates);
        fd.set("role_play_rules", rolePlayRules);
        fd.set("persona", persona);
        fd.set("chat_records", chatRecords);
        fd.set("work_achievements", workAchievements);
        fd.set("analyst_rules", analystRules);
        fd.set("workflow", workflow);
        fd.set("decision_heuristics", decisionHeuristics);
        fd.set("timeline", timeline);
        fd.set("values_antipatterns", valuesAntipatterns);
        fd.set("intellectual_lineage", intellectualLineage);
        fd.set("honesty_supplement", honestySupplement);
        if (files) {
          for (let i = 0; i < files.length; i++) {
            const f = files.item(i);
            if (f) fd.append("files", f, f.name);
          }
        }
        const res = await fetch(`${API_BASE}/api/build-skill-zip`, {
          method: "POST",
          body: fd,
        });
        if (!res.ok) {
          const t = await res.text();
          setMessage({ type: "err", text: t || `请求失败 ${res.status}` });
          return;
        }
        const cd = res.headers.get("Content-Disposition");
        let name = "skill.zip";
        const m = cd && /filename="([^"]+)"/.exec(cd);
        if (m) name = m[1] ?? name;
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = name;
        a.click();
        URL.revokeObjectURL(url);
        setLastZipName(name);
        setMessage({ type: "ok", text: `已下载：${name}` });
      } catch (err) {
        setMessage({
          type: "err",
          text: err instanceof Error ? err.message : "网络或跨域错误；请确认后端已启动且地址正确。",
        });
      } finally {
        setLoading(false);
      }
    },
    [
      displayName,
      skillId,
      version,
      purposeDescription,
      activationHints,
      inScope,
      outOfScope,
      basicInfo,
      latestUpdates,
      rolePlayRules,
      persona,
      chatRecords,
      workAchievements,
      analystRules,
      workflow,
      decisionHeuristics,
      timeline,
      valuesAntipatterns,
      intellectualLineage,
      honestySupplement,
      files,
    ]
  );

  return (
    <main className="wrap">
      <h1>人员蒸馏 · 技能包生成</h1>
      <p className="lead">
        参数分区与生成包内 <code>SKILL.md</code> 章节对齐（YAML <code>name</code> / <code>description</code>、激活、路径
        A/B、Agentic、身份卡、心智模型、启发式、表达 DNA、时间线等）。默认 API：<code>{API_BASE}</code>
      </p>

      <form className="card" onSubmit={onSubmit}>
        <div className="form-section">
          <h2>加载示例数据</h2>
          <p className="section-desc">从仓库根目录 <code>fixtures/*.json</code> 读取并回填表单（不包含附件 files）。</p>
          <div className="row">
            <label className="field" style={{ flex: 1 }}>
              <span>选择示例</span>
              <select value={exampleId} onChange={(e) => setExampleId(e.target.value)}>
                {examples.map((ex) => (
                  <option key={ex.id} value={ex.id}>
                    {(ex.display_name || ex.filename) + (ex.skill_id ? ` · ${ex.skill_id}` : "")}
                  </option>
                ))}
                {!examples.length && <option value="">（未找到 fixtures 示例）</option>}
              </select>
            </label>
            <button type="button" className="secondary" onClick={loadExample} disabled={!exampleId}>
              加载示例
            </button>
          </div>
        </div>

        <div className="form-section">
          <h2>包标识与 YAML 头</h2>
          <p className="section-desc">对应 <code>SKILL.md</code> 顶部 <code>name</code>、<code>description</code> 与版本信息。</p>
          <label className="field">
            <span>
              展示名称 <span className="hint">（必填，zip 文件名 slug）</span>
            </span>
            <input
              type="text"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              placeholder="例：Trump-military-Demo"
              required
              autoComplete="off"
            />
          </label>
          <label className="field">
            <span>
              Skill ID <span className="hint">（可选；对应包内 skill_id）</span>
            </span>
            <input
              type="text"
              value={skillId}
              onChange={(e) => setSkillId(e.target.value)}
              placeholder="例：demo.military_narrative.v1"
              autoComplete="off"
            />
          </label>
          <label className="field">
            <span>版本</span>
            <input type="text" value={version} onChange={(e) => setVersion(e.target.value)} placeholder="1.0.0" />
          </label>
          <label className="field">
            <span>
              用途与描述 <span className="hint">（写入 YAML description 主段落：思维框架、资料维度、用途、触发词等）</span>
            </span>
            <textarea
              value={purposeDescription}
              onChange={(e) => setPurposeDescription(e.target.value)}
              rows={6}
              placeholder="多段说明：蒸馏对象、调研维度、三种用途、触发用语…"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>激活确认与范围</h2>
          <p className="section-desc">对应「激活确认」与 In / Out of scope；可扩展自定义触发表。</p>
          <label className="field">
            <span>
              自定义触发与模式 <span className="hint">（可选，Markdown；追加在默认表格后）</span>
            </span>
            <textarea
              value={activationHints}
              onChange={(e) => setActivationHints(e.target.value)}
              rows={4}
              placeholder="例：| 触发信号 | 模式 | 路径 | …"
            />
          </label>
          <label className="field">
            <span>In scope</span>
            <textarea value={inScope} onChange={(e) => setInScope(e.target.value)} rows={3} placeholder="本 skill 覆盖的任务与语境" />
          </label>
          <label className="field">
            <span>Out of scope</span>
            <textarea value={outOfScope} onChange={(e) => setOutOfScope(e.target.value)} rows={3} placeholder="必须拒绝或不做的事" />
          </label>
        </div>

        <div className="form-section">
          <h2>身份卡与最新动态</h2>
          <p className="section-desc">对应「身份卡」与「最新动态」小节。</p>
          <label className="field">
            <span>身份卡（我是谁、起点、当前重心）</span>
            <textarea
              value={basicInfo}
              onChange={(e) => setBasicInfo(e.target.value)}
              rows={5}
              placeholder="第一行可作文首引用；其余为身份叙事"
            />
          </label>
          <label className="field">
            <span>最新动态（时期敏感上下文）</span>
            <textarea
              value={latestUpdates}
              onChange={(e) => setLatestUpdates(e.target.value)}
              rows={5}
              placeholder="政策/市场/关系等可公开摘要，供预判任务使用"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>路径 A · 角色扮演</h2>
          <p className="section-desc">规则、Persona 支撑、聊天/口吻样例。</p>
          <label className="field">
            <span>角色扮演规则（步骤、退出指令、禁忌）</span>
            <textarea
              value={rolePlayRules}
              onChange={(e) => setRolePlayRules(e.target.value)}
              rows={5}
              placeholder="留空时可回退到「工作流」与 Persona"
            />
          </label>
          <label className="field">
            <span>性格与表达（Persona）</span>
            <textarea
              value={persona}
              onChange={(e) => setPersona(e.target.value)}
              rows={5}
              placeholder="语气、决策习惯、风险取向等"
            />
          </label>
          <label className="field">
            <span>聊天记录 / 沟通摘录（The Weave 等）</span>
            <textarea
              value={chatRecords}
              onChange={(e) => setChatRecords(e.target.value)}
              rows={4}
              placeholder="脱敏后的对话或摘录"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>路径 B · 分析师与心智模型</h2>
          <p className="section-desc">第三人称分析规则 + 核心工作成果/模型素材。</p>
          <label className="field">
            <span>分析师补充规则（步骤、置信度、预判格式）</span>
            <textarea
              value={analystRules}
              onChange={(e) => setAnalystRules(e.target.value)}
              rows={4}
              placeholder="可选：路径 B 专用说明"
            />
          </label>
          <label className="field">
            <span>核心工作成果与心智模型素材</span>
            <textarea
              value={workAchievements}
              onChange={(e) => setWorkAchievements(e.target.value)}
              rows={6}
              placeholder="可整理为多条「模型：一句话 + 证据 + 应用 + 局限」"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>回答工作流（Agentic）</h2>
          <p className="section-desc">对应「Agentic Protocol」：分类、是否检索事实、输出结构。</p>
          <label className="field">
            <span>工作流</span>
            <textarea
              value={workflow}
              onChange={(e) => setWorkflow(e.target.value)}
              rows={6}
              placeholder="Step 1 分类 → Step 2 研究 → Step 3 回答 …"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>决策启发式</h2>
          <p className="section-desc">编号列表优先；留空则从 Persona + 工作流自动生成占位结构。</p>
          <label className="field">
            <span>决策启发式（独立填写）</span>
            <textarea
              value={decisionHeuristics}
              onChange={(e) => setDecisionHeuristics(e.target.value)}
              rows={6}
              placeholder="场景 → 逻辑 → 案例 → 局限"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>人物时间线</h2>
          <label className="field">
            <span>时间线（Markdown 表格或列表）</span>
            <textarea
              value={timeline}
              onChange={(e) => setTimeline(e.target.value)}
              rows={5}
              placeholder="| 时间 | 事件 | 影响 | …"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>价值观与智识谱系</h2>
          <label className="field">
            <span>价值观与反模式</span>
            <textarea
              value={valuesAntipatterns}
              onChange={(e) => setValuesAntipatterns(e.target.value)}
              rows={5}
              placeholder="留空则用 In/Out scope 生成简要列表"
            />
          </label>
          <label className="field">
            <span>智识谱系（影响来源与影响链）</span>
            <textarea
              value={intellectualLineage}
              onChange={(e) => setIntellectualLineage(e.target.value)}
              rows={4}
              placeholder="著作、师承、批评传统等"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>诚实边界补充</h2>
          <p className="section-desc">追加在固定诚实边界条款之后。</p>
          <label className="field">
            <span>补充说明</span>
            <textarea
              value={honestySupplement}
              onChange={(e) => setHonestySupplement(e.target.value)}
              rows={3}
              placeholder="局限、调研截止期、不可预测性等"
            />
          </label>
        </div>

        <div className="form-section">
          <h2>附件</h2>
          <label className="field">
            <span>上传资料 <span className="hint">（多选；单文件 ≤ 15MB → evidence/uploads）</span></span>
            <input type="file" multiple onChange={(e) => setFiles(e.target.files)} />
          </label>
        </div>

        {message && <div className={`msg ${message.type === "ok" ? "ok" : "err"}`}>{message.text}</div>}

        <button type="submit" className="primary" disabled={loading}>
          {loading ? "生成中…" : "生成并下载 xxxx-skill.zip"}
        </button>
        {lastZipName && (
          <p className="lead" style={{ margin: 0 }}>
            最近下载：{lastZipName}
          </p>
        )}
      </form>

      <footer className="note" />
    </main>
  );
}
