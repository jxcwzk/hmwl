---
name: "project-manager"
description: "Project management skill for scanning files, structuring requirements into PRD, planning tasks, and coordinating team. Invoke when user says 'pm干活' or asks for project management, requirements analysis, or task planning."
---

# Project Manager

This skill provides comprehensive project management capabilities, focusing on requirements analysis and task planning without writing code.

## Directory Structure

```
01-inbox/    - 存放用户提交的需求文档（Markdown格式）
02-active/   - 存放已激活的执行计划文档（PRD）
03_output/   - 存放 PRD 执行结果（由 prd2code 技能使用）
```

## Core Features

### 1. Scan Inbox (01-inbox)
- Scan `01-inbox/` directory for new requirements
- Read and analyze requirement documents
- Identify scope and objectives

### 2. Requirements Analysis & Clarification
- Analyze each requirement
- **必须与用户确认**：向用户提问以澄清需求细节
- 确保理解用户的真实意图

### 3. Create PRD (02-active)
- Transform vague requirements into structured PRD
- Write to `02-active/` directory
- Define user stories, acceptance criteria
- Set priorities and timeline

### 4. User Confirmation (CRITICAL)
- **必须展示PRD给用户确认**
- 使用 AskUserQuestion 询问用户PRD是否有问题
- 只有用户确认后才能结束PM工作
- 如果用户有异议，需要修改PRD后再次确认

## Usage

### Trigger Command
```
pm干活
```

### Workflow (必须遵循)

**Phase 1: Scan Inbox**
1. Scan `01-inbox/` directory
2. Read all requirement documents
3. Identify new/pending requirements

**Phase 2: Requirements Analysis (必须与用户确认)**
4. 分析每个需求
5. **向用户提问**：澄清需求细节、不清楚的地方
6. 确认理解正确

**Phase 3: Create PRD**
7. Write structured PRD to `02-active/{需求名称}.md`
8. Include user stories
9. Define acceptance criteria
10. Set priorities

**Phase 4: User Confirmation (CRITICAL - 必须执行)**
11. **展示PRD内容给用户**
12. 使用 AskUserQuestion 询问用户：
    - "PRD已经编写完成，请您确认以下内容：
      - 需求理解是否正确
      - 功能列表是否完整
      - 验收标准是否合理
      - 优先级和排期是否认可
    - 如果有任何问题，请告诉我具体需要修改的地方"
13. **只有用户明确确认"没问题"或"可以开始"后才能结束**
14. 如果用户有修改意见，重复 Phase 3-4

## PRD Template

```markdown
# {需求名称}

## Status
- [ ] 待用户确认
- [ ] 已确认，待开发
- [ ] 开发中
- [ ] 已完成

## 1. Overview
- 需求描述
- 目标用户
- 解决的问题

## 2. User Stories
- Story 1: 作为...我希望...以便...

## 3. Functional Requirements
- Requirement 1
- Requirement 2

## 4. Acceptance Criteria
- Criterion 1
- Criterion 2

## 5. Edge Cases
- Case 1

## 6. Priority
- High / Medium / Low

## 7. Timeline
- 预估时间

## 8. Tasks
- [ ] Task 1
- [ ] Task 2
```

## Key Principles

1. **No Code Writing**: Focus on planning, not implementation
2. **Must Confirm with User**: Never complete without user approval
3. **Implementation Agnostic**: Don't care about how it's built, only what needs to be built
4. **Executable Requirements**: Make vague ideas concrete and actionable
5. **Clean Boundaries**: Define what's in scope and out of scope

## Best Practices

1. Always scan 01-inbox first
2. Ask clarifying questions to understand requirements fully
3. Write clear acceptance criteria
4. **Always get user confirmation before completing**
5. Keep original requirements in 01-inbox
6. Move processed items to 02-active as PRD
