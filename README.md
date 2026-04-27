# 人员蒸馏（Person → Skill）工具：汇总方案（README）

本仓库目标：用**人员蒸馏工具**把“一个具体的人”系统化蒸馏为**可复用、可评估、可部署**的 `skill` 技能包，供 **AI 模型安装并加载**后，在任务中**按该人员的行为模式、沟通风格与决策习惯**进行自主或半自主工作（仍需遵守本仓库中的边界、合规与风险策略）。

---

## 人员蒸馏工具：输入、输出、目的

| 项目 | 说明 |
| --- | --- |
| **输入** | **人的基本信息**、**性格与行为模式描述**、**聊天记录**、**核心工作成果**、**工作流程**及其他可选证据（如会议纪要、评审记录、知识库等，详见下文「输入清单」） |
| **输出** | 该人员的一份 **skill 技能包**（单文件发布形态示例：`xxxxx.zip`），内含可安装的元数据、规范与资产（如 `spec`、契约、样例、评测等，与仓库内建议目录一致） |
| **目的** | 使 **AI 模型** 能通过安装/加载该 skill，在授权范围内**模仿该人员的工作方式**（不是复制真人身份，而是复现其**可迁移的能力与行为规则**） |

> **说明**：`xxxxx.zip` 为发布物命名约定，实际可加上版本与人物标识，例如 `person_<id>_skill_v1.0.0.zip`。

---

## 1. 你要蒸馏的到底是什么

这里的 `skill` 指一个**可执行能力单元**，通常包含：

- **性格与行为模式**（`persona`）：大五或业务化描述（如内外向、风险取向、合作风格）、在压力/冲突下的习惯反应、**宜模仿的**沟通节奏与措辞倾向（与真人区分：不得冒充身份，仅作风格与规则约束）
- **适用范围**：能解决哪些问题、不解决哪些问题
- **输入/输出契约**：输入数据结构、输出结构、错误与边界条件
- **决策与步骤**：关键判断点、步骤编排、工具调用策略
- **质量标准**：成功/失败判定、可接受的误差、风格与合规约束
- **可验证**：有可重复的测试用例与评分规则

> 一个人可以被蒸馏为多个 skills：按岗位职责、任务类型或高频场景拆分更可控。

---

## 2. 需要哪些数据、参数、资料（输入清单）

蒸馏工具侧建议至少覆盖：**基本信息 + 性格描述 + 聊天记录 + 核心工作成果 + 工作流程**；下表为与工具输入一一对应的最低集合，细节可再扩展。

| 蒸馏工具输入 | 在本文中的位置 |
| --- | --- |
| 人的基本信息 | §2.1 |
| 性格与行为模式描述 | §2.1（性格专项） |
| 聊天记录 | §2.2（对话类证据） |
| 核心工作成果 | §2.2（可复用产物） |
| 工作流程等 | §2.2 + §2.4 |

### 2.1 人员基础信息（用于边界与风格）

- **角色画像**：岗位/职级/领域、上下游协作对象、常用术语
- **权限与责任边界**：能做/不能做、可承诺/不可承诺
- **性格与行为模式**（**蒸馏重点之一**）：建议单独成段或成表，可包含——社交倾向、风险偏好、对冲突/模糊的态度、做决策偏直觉还是数据、是否倾向先同步再执行、对细节与进度的要求等；**最好有事例**（在何种情境下曾如何反应），便于从“标签”变成可执行的规则
- **偏好与风格**：写作口吻、严谨程度、是否喜欢列表/表格/代码、沟通策略
- **时间与资源约束**：常见 deadline、预算/人力/系统权限限制

### 2.2 任务与流程证据（核心）

把“会做”变成“能复现”，优先采集真实过程数据：

- **聊天记录**：与协作相关的 IM/群聊/私聊、邮件 thread（**脱敏后**）；能反映该人的**语气、惯用语、推进方式**与在分歧下的表达习惯
- **任务日志**：工单、邮件、IM、会议纪要、评审意见、issue/PR 讨论
- **可复用产物**（**核心工作成果**）：方案文档、PRD、技术设计、复盘、周报、对外说明、模板
- **操作轨迹**：你希望工具“怎么做”的步骤证据（截图/录屏/命令/操作手册）
- **决策样本**：面对冲突/不确定时如何取舍（why & trade-off）
- **失败样本**：错误案例、踩坑记录、如何纠偏与兜底

建议为每条证据附带元数据：

- **场景标签**：任务类型/难度/紧急程度/利益相关方
- **输入条件**：上下文、资源、约束
- **结果**：最终输出、验收标准、回滚与后续动作
- **质量标注**：优秀/可用/不可用，原因

### 2.3 领域知识与外部依据（减少胡编）

- **知识库**：Wiki/Notion/Confluence、规范与流程制度、FAQ
- **产品与业务**：目标、指标定义、关键路径、数据口径
- **技术与系统**：架构文档、API 文档、权限说明、运行手册
- **合规与安全**：隐私、保密、审计、输出限制（必须明确）

### 2.4 工具与环境（可执行性）

- **可用工具列表**：CLI/IDE/数据库/BI/工单系统/代码仓库/部署平台
- **访问方式**：账号权限、token 获取方式（不要写入仓库）、网络限制
- **调用限制**：频率、成本、审批、生产/测试隔离规则

---

## 3. 关键参数怎么设计（蒸馏配置）

为了让蒸馏可重复，需要把关键变量参数化。下面是一套推荐的参数表（可直接作为配置文件字段）。

### 3.1 Skill 定义参数

- **skill_id**：全局唯一、稳定不变（如 `pm.requirements_triage.v1`）
- **skill_name**：人类可读名称
- **skill_goal**：一句话目的（解决什么、为谁、在什么约束下）
- **in_scope / out_of_scope**：必须写，避免“万能助手”
- **persona_profile**（或等价字段）：性格与可迁移行为规则（可引用证据 ID），并声明**不冒充真人**、不绑定敏感个人标识
- **io_schema**：输入/输出 JSON Schema 或等价描述
- **style_guide**：输出风格（语气、结构、语言、长度）
- **risk_policy**：高风险场景的拒答/升级/确认策略

### 3.2 证据与学习参数

- **evidence_sources**：数据来源清单及优先级
- **labeling_policy**：标注规则（好/坏样本、原因维度）
- **golden_examples**：金标准示例集合（强烈建议）
- **anti_examples**：反例集合（常见误区）

### 3.3 评估参数（验收）

- **success_criteria**：成功判定条件（硬指标 + 软指标）
- **metrics**：准确率/一致性/覆盖率/时延/成本等
- **eval_set**：固定评测集（避免“越改越飘”）
- **scoring_rubric**：评分细则（0-5 或 0-100）

---

## 4. 端到端流程（从人到 skill）

下面流程强调“可复现 + 可验收”。你可以按阶段逐步落地。

### 4.1 选题与切分（把人拆成可做的小块）

输出：

- `Skill Map`：该人的能力地图（按任务类型/频率/价值划分）
- `Candidate Skills`：候选 skill 列表（每个都能独立验收）

切分建议：

- 先从**高频 + 高价值 + 低风险**的任务开始
- 每个 skill 尽量只覆盖**一个清晰任务闭环**

### 4.2 证据收集与清洗（数据准备）

动作：

- 汇总原始材料（文档、对话、工单、代码、会议纪要）
- 去重、脱敏、分段（保留上下文）
- 为每条证据打标签与元数据

输出：

- `evidence/`：按来源与场景组织的证据集
- `evidence_index`：可检索索引（包含标签、时间、质量标注）

### 4.3 抽取与结构化（从材料中提炼“可执行骨架”）

要抽取的结构：

- **意图识别**：什么时候触发这个 skill
- **关键变量**：输入里哪些字段决定走不同路径
- **决策点**：判断规则、优先级、冲突处理
- **步骤编排**：步骤、顺序、循环/分支/终止条件
- **产物模板**：输出结构与常用措辞/模板
- **兜底策略**：缺信息怎么办、风险怎么升级、什么时候停止

输出：

- `skill_spec`（草案）：目标、范围、I/O、步骤、异常、模板

### 4.4 形成 Skill 合同（Contract）

这一步把“会做”变成“别人能用/机器能跑”：

- 明确输入字段与必填项
- 明确输出字段与验收规则
- 明确风险策略（拒答/提醒/升级）
- 明确依赖工具与权限

输出：

- `contract`：可版本化、可回归测试的契约定义

### 4.5 评估集与回归（可验证）

构建评估集：

- **覆盖**：常见场景 + 边界场景 + 失败场景
- **稳定**：固定样本，支持回归
- **可评分**：每个样本都有评分依据

输出：

- `eval_set`：输入 + 期望输出/要点 + 评分 rubric

### 4.6 迭代与上线（持续改进）

循环：

- 运行 → 记录失败 → 归因（缺数据/契约不清/决策错误/工具限制）→ 补证据/补规则 → 回归评测

输出：

- `changelog`：版本变更记录（为何变、影响面、回归结果）

---

## 5. 建议的仓库结构（可直接照此建立）

> 本仓库可逐步在磁盘上按 `skills/<skill_id>/` 落盘；**对外交付物 `{slug}-skill.zip` 为扁平结构**（见 §10.4），根目录为 `manifest.json`、`contract.json`、`evidence/**` 与 `SKILL.md`），供目标环境安装，不必在 zip 里再套一层 `skills/<id>/`。

- `README.md`：总览（本文件）
- `skills/`
  - `<skill_id>/`
    - `SKILL.md`：人类可读主入口（目标/范围/性格/流程/范围；与 zip 根目录 `SKILL.md` 同构）
    - `contract.json`：I/O 契约（Schema）
    - `prompts/`：提示词或策略片段（如需）
    - `examples/`
      - `golden/`：金标准样例
      - `anti/`：反例样例
    - `eval/`
      - `cases.jsonl`：评测集
      - `rubric.md`：评分细则
    - `changelog.md`：版本记录
- `evidence/`
  - `sources.md`：数据来源清单、脱敏说明、更新节奏
  - `index.jsonl`：证据索引（带标签与元数据）
- `templates/`
  - `skill_spec.template.md`
  - `eval_case.template.json`
- `docs/`
  - `taxonomy.md`：能力/任务分类法
  - `privacy.md`：隐私与合规策略

---

## 6. 最小可行方案（MVP）怎么做

如果你只做第一个可落地的 skill，建议用如下最小输入：

- 10-30 条**高质量真实样本**（含 3-5 条失败样本）
- 1 个清晰的输出模板（例如“需求澄清纪要/排期评估/代码评审意见”之一）
- 1 份评估集（至少 20 个 case）+ 评分规则

完成标准：

- 在固定评估集上输出结构稳定、关键点覆盖率达标
- 遇到缺信息/高风险时能按策略提示或升级，而不是编造

---

## 7. 数据安全与合规（必须先定规则）

- **脱敏**：姓名、手机号、邮箱、客户信息、合同金额、token、内部链接等
- **最小化**：只保留完成 skill 所需的信息
- **可审计**：保留证据来源与变更记录
- **禁止入库**：任何密钥/私钥/生产凭证不得进入仓库

---

## 8. 下一步建议（你给我一个人，我怎么开始）

你只需要提供以下最小信息，我就能把第一个 `skill_spec` 写出来并给出评估集草案：

- 该人的**岗位/领域**与一个你想先蒸馏的**高频任务**
- 5-10 份相关材料（文档/聊天记录/工单/邮件/PR 讨论均可，脱敏后）
- 该任务的**“做得好”的标准**（你希望输出长什么样）

---

## 9. 术语对齐（简版）

- **人员蒸馏工具**：以规定输入生成 **skill 技能包**（如 `xxxxx.zip`）的流程与实现
- **技能包（`.zip`）**：可安装的一组文件，使 AI 能加载该人员的 distilled skill 并按行为模式工作
- **证据（evidence）**：真实材料片段 + 元数据 + 质量标注
- **金标准（golden）**：你愿意拿去直接复用的输出样例
- **契约（contract）**：输入/输出结构与验收规则
- **评估集（eval_set）**：固定测试用例集合，用于回归

---

## 10. Web 服务（Next.js + Python）

本仓库包含一个最小联调实现：**前台用 Next.js 填表与上传附件，后台用 Python [FastAPI](https://fastapi.tiangolo.com/) 打包为 `{slug}-skill.zip` 并触发浏览器下载**。（若你习惯说 “fastjson”，在 Python 侧一般对应 **FastAPI**，不是 Java 的 fastjson。）

### 10.1 目录

- `web/`：Next.js 14（App Router）前端
- `backend/`：FastAPI 服务

### 10.2 启动后端

```bash
cd backend
python -m venv .venv
# Windows: .venv\Scripts\activate
pip install -r requirements.txt
python -m uvicorn app.main:app --reload --host 127.0.0.1 --port 8000
```

健康检查：<http://127.0.0.1:8000/api/health>  
生成接口：`POST /api/build-skill-zip`（`multipart/form-data`），返回 `application/zip`。

### 10.3 启动前端

```bash
cd web
npm install
# 若 Windows 下因依赖的 postinstall 需要 bash 而失败，可改用：
# npm install --ignore-scripts
npm run dev
```

浏览器打开 <http://127.0.0.1:3000>。默认请求 `http://127.0.0.1:8000`；若后端地址不同，可在 `web` 目录下创建 `.env.local`：

```bash
NEXT_PUBLIC_API_BASE=http://你的主机:端口
```

**表单字段**（与 `SKILL.md` 章节对齐，均为 `multipart` 文本字段，除 `files`）：  
`display_name`、**`explicit_identity`（必填，身份明确／唯一执行主体锚定）**、`skill_id`、`version`、`purpose_description`、`activation_hints`、`in_scope`、`out_of_scope`、`basic_info`、`latest_updates`、`role_play_rules`、`persona`、`chat_records`、`work_achievements`、`analyst_rules`、`workflow`、`decision_heuristics`、`timeline`、`values_antipatterns`、`intellectual_lineage`、`honesty_supplement`；附件字段名 **`files`**（可多选）。

**Windows 排错**：若 `npm run build` / `npm run dev` 报 WSL/bash 相关错误，可用 Node 直调（已在 `package.json` 中显式用 `node .../next`；若 `npm` 仍劫持 shell，可检查 `npm config get script-shell`），或在项目 `web` 下执行：

```bat
node node_modules\next\dist\bin\next build
node node_modules\next\dist\bin\next dev -p 3000
```

### 10.4 技能包内容（当前实现）

`{slug}-skill.zip` **根目录**（无 `skills/…` 嵌套）：`manifest.json`（含 `name` 与 `entry: SKILL.md`）、`contract.json`、`evidence/*`、`references/research/README.txt`（与参考案例的调研目录同构）及主文件 **`SKILL.md`**。`SKILL.md` 结构对齐参考：YAML 的 `name` / `description` / `explicit_identity` 头、 **身份锚定（唯一执行主体）**、**激活确认**、路径 A/B、**Agentic 工作流**、身份卡、心智模型、启发式、表达 DNA、时间线、价值观、智识谱系、诚实边界、附录等；正文由表单与占位组成，可手改后固定。

### 10.5 Windows 脚本（`scripts/`）

| 脚本 | 作用 |
|------|------|
| `dev-setup.bat` | 后台：`backend\.venv` + `pip install -r requirements.txt`；前台：`web` 下 `npm install` |
| `dev-start.bat` | 新开两个窗口分别启动 FastAPI（`:8000`）与 Next 开发服（`:3000`），并打开浏览器访问前台 |
| `dev-all.bat` | 依次执行 `dev-setup.bat` + `dev-start.bat` |
| `dev-stop.bat` | 尝试结束占用 **8000 / 3000** 端口的进程（开发机清理用） |
| `dev-restart.bat` | `dev-stop.bat` → 等待 2 秒 → `dev-start.bat` |
| `pack-build.bat` | 在仓库根目录生成 **`build/`**：前台为 Next **静态导出**（`web/out` → `build/dist`，**不含** `node_modules`），`fixtures` 拷至 `build/fixtures`，后台为 `build/backend` + `requirements.txt`；**并内嵌** Windows 官方 [embeddable](https://docs.python.org/3/using/windows.html#the-embeddable-package) Python 3.12 x64 到 `build/python-embed`，用 pip 将依赖装入该解释器，并复制**相对路径**启动脚本：`start.bat` / `stop.bat` / `restart.bat` / `_run-backend.cmd`（**仅** FastAPI；**不**起独立 Node/Next 服务） |

打包目录 **`build/`** 用法：将整个 `build` 目录拷贝到目标机后，**`start.bat`** 启动、**`stop.bat`** 结束 8000 上监听进程、**`restart.bat`** 先停再起；各脚本以**自身所在目录**为包根，请保持 `dist` / `backend` / `python-embed` 等子目录与脚本同级。详情见同目录下 **`README-Pack.txt`**。  
- **目标机不需要安装 Node**；`build/dist` 为纯静态资源，由 FastAPI 在 **:8000** 挂载，与 `/api` 同端口。  
- **目标机可不再安装 Python**：`pack-build` 成功时 `build\python-embed\` 自带解释器与依赖，`_run-backend.cmd` 会优先使用。  
- 若打包时 `set PACK_SKIP_PYTHON_EMBED=1` 并重新 `pack-build.bat`，则不内嵌解释器，需目标机自装 Python 以走 venv 逻辑。  
- 内嵌为 **x64/amd64** 解释器，与 32 位/ARM 不兼容。  
- 联网打包时，embed 与 get-pip 会缓存到 **`scripts\cache\embed\`**，便于复用/离线。  

运行态：`start.bat` 只启后端，浏览器打开 **`http://127.0.0.1:8000`**（与本地开发「前后端分口」是两套方式；开发仍见 `dev-start.bat`）。打包构建前可将 `NEXT_PUBLIC_API_BASE` 设为空，使前端在 8000 上走同源 API。

说明：`pack-build.bat` 内使用 `node ...\next build` 调用构建，避免个别环境下 `npm run build` 被错误指向 WSL/bash 导致失败。

