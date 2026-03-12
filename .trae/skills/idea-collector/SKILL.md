---
name: "idea-collector"
description: "Collects and stores ideas/requirements to 01-inbox. Invoke when user says '灵感来了' or wants to capture ideas for later processing."
---

# Idea Collector

This skill collects your ideas, thoughts, and requirements and stores them in the 01-inbox directory for later processing by the project manager.

## Purpose

- Capture fleeting ideas before they are forgotten
- Store requirements in a standardized format
- Make ideas ready for project manager to process

## Usage

### Trigger Command
```
灵感来了
```

### Workflow

**Step 1: Listen**
1. Receive the user's idea or thought
2. Ask clarifying questions if needed
3. Gather enough detail to create a useful document

**Step 2: Format**
1. Create a Markdown document
2. Use the template below
3. Include all relevant details

**Step 3: Store**
1. Save to `01-inbox/` directory
2. Use descriptive filename
3. Confirm storage location to user

## Document Template

```markdown
# {想法/需求标题}

## 原始想法
> 用户原始表述

## 详细描述
- 详细说明这个想法
- 背景和动机
- 预期效果

## 相关想法
- 与其他功能的关联
- 可能的影响范围

## 优先级评估
- 高/中/低
- 理由

## 备注
- 其他补充信息
```

## Example

If user says: "我想给系统加一个用户通知功能"

The output in `01-inbox/用户通知功能.md`:

```markdown
# 用户通知功能

## 原始想法
> 我想给系统加一个用户通知功能

## 详细描述
- 用户登录后可以收到系统通知
- 通知包括订单状态更新、物流动态等
- 需要在后台管理通知内容

## 相关想法
- 与订单模块关联
- 可能需要消息队列支持

## 优先级评估
- 中优先级
- 提升用户体验

## 备注
- 初步想法，需要进一步细化
```

## Key Principles

1. **Capture Everything**: No idea is too small
2. **Keep Original**: Preserve user's original words
3. **Add Structure**: Organize into readable format
4. **No Judgment**: Don't evaluate, just capture
5. **Ready for PM**: Format so project manager can process

## Directory

```
01-inbox/  <- Ideas are stored here
02-active/ <- Processed by PM
03_output/ <- Results from prd2code
```
