---
name: "prd2code"
description: "Execute PRD strictly: read from 02-active, output to 03_output/{PRD}-result.md. No self-created requirements. Invoke when user says 'cc干活' or asks to implement PRD."
---

# PRD2Code

This skill strictly follows PRD to implement features. It reads requirements from 02-active and executes tasks without adding extra features.

## Directory Structure

```
01-inbox/    - 用户提交的需求文档（由项目经理处理）
02-active/   - 已激活的 PRD 文档（本技能读取源）
03_output/   - PRD 执行结果（写入位置）
```

## Core Rules

### 1. Input Source
- **Only read from**: `02-active/` directory
- Look for active PRD documents that are confirmed by user
- Parse requirements from PRD only

### 2. Execution Principles
- **Strictly follow PRD**: Execute exactly what is written
- **No self-created requirements**: Don't add anything not in PRD
- **No proactive features**: Don't implement beyond scope
- **Ask for clarification**: If PRD is unclear, ask before proceeding

### 3. Output Location
- **Write to**: `03_output/{PRD名称}-result.md`
- Document execution process
- Record implementation results
- Keep PRD clean (no logs in PRD)

### 4. Update Status (CRITICAL)
- 完成后必须更新 01-inbox 需求文档状态为"已完成"
- 完成后必须更新 02-active PRD文档状态为"已完成"
- 记录完成时间和结果

### 5. User Confirmation (CRITICAL)
- **实现完成后必须找用户确认**
- 使用 AskUserQuestion 询问用户：
  - 功能是否满足需求
  - 是否有bug需要修复
  - 是否可以验收
- 只有用户确认后才能结束CC工作

## Usage

### Trigger Command
```
cc干活
```

### Workflow (必须遵循)

**Step 1: Read PRD**
1. Scan `02-active/` directory
2. Read confirmed PRD documents (only those with "已确认" status)
3. Parse requirements and acceptance criteria

**Step 2: Execute**
1. Implement features per PRD
2. Follow acceptance criteria exactly
3. Document progress in result file

**Step 3: Update Status**
1. Update 01-inbox/需求文档.md - mark as "已完成"
2. Update 02-active/PRD名称PRD.md - mark as "已完成"
3. Record completion time

**Step 4: User Confirmation (CRITICAL - 必须执行)**
1. **展示实现结果给用户**
2. 使用 AskUserQuestion 询问用户：
   - "功能已经实现完成，请您确认：
     - 功能是否符合预期
     - 是否有bug需要修复
     - 是否可以验收通过
   - 如果有任何问题，请告诉我具体需要修改的地方"
3. **只有用户明确确认"没问题"或"可以了"后才能结束**
4. 如果用户有修改意见，继续修复直到用户确认

## Result File Format

```markdown
# {PRD名称} Implementation Result

## Execution Info
- Start Time: YYYY-MM-DD HH:MM
- End Time: YYYY-MM-DD HH:MM
- Status: In Progress / Completed / Blocked

## Implemented Items
- [ ] Item 1
- [ ] Item 2

## Acceptance Criteria Check
- [ ] Criterion 1
- [ ] Criterion 2

## Issues / Blockers
- None / Issue description

## Notes
- Execution notes here
```

## Strict Boundaries

| Allowed | Not Allowed |
|---------|--------------|
| Read from 02-active/ | Read from other directories |
| Follow PRD exactly | Add new features |
| Ask clarifying questions | Assume requirements |
| Write to 03_output/ | Modify PRD files |
| Update status in 01-inbox and 02-active | Leave status as pending |
| Get user confirmation before finishing | Finish without confirmation |

## Key Principles

1. **PRD Is Law**: Everything must be in the PRD
2. **Clean Execution**: Output goes to result files
3. **No Scope Creep**: Don't add features not in PRD
4. **Traceable**: All work documented in result files
5. **Must Confirm**: Always get user approval before completing
6. **Update Status**: Mark tasks as completed in both 01-inbox and 02-active
