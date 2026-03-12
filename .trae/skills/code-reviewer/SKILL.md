---
name: "code-reviewer"
description: "Reviews code for best practices, bugs, and security issues. Invoke automatically before code commits or when user asks for code review."
---

# Code Reviewer

This skill performs comprehensive code review for both frontend (Vue) and backend (Java) code in the project.

## When to Use

**This skill should be invoked:**
- Automatically before any code commit (via git hooks)
- When user asks for code review
- When user asks to review changes before merging

## Review Scope

### Frontend (Vue/JavaScript)
- Vue component best practices
- Vue 3 Composition API usage
- Element Plus component usage
- State management patterns
- Performance considerations
- Security (no secrets exposed)
- Code style consistency

### Backend (Java)
- Spring Boot best practices
- Service layer patterns
- Controller conventions
- Database query optimization
- Security (SQL injection, XSS)
- Error handling
- Code style consistency

## Review Rules

### Critical Issues (Must Fix)
1. **Security**: Hardcoded secrets, API keys, passwords in code
2. **Bugs**: Null pointer exceptions, unhandled errors
3. **Performance**: N+1 queries, memory leaks
4. **SQL Injection**: Unparameterized queries

### Warnings (Should Fix)
1. **Code Style**: Inconsistent naming, formatting
2. **Best Practices**: Missing validation, improper error handling
3. **Performance**: Unnecessary re-renders, large bundle size
4. **Documentation**: Missing comments for complex logic

### Suggestions (Nice to Have)
1. Code readability improvements
2. DRY (Don't Repeat Yourself) refactoring
3. TypeScript usage where applicable

## Workflow

### Step 1: Gather Changes
```bash
# Get changed files
git diff --name-only
git diff HEAD~1 --name-only
```

### Step 2: Analyze Each File
- Parse the code
- Apply review rules
- Identify issues by severity

### Step 3: Generate Report
Output format:
```
## Code Review Report

### Critical Issues
- [File:Line] Issue description

### Warnings
- [File:Line] Issue description

### Suggestions
- [File:Line] Issue description
```

### Step 4: Provide Fixes
For each issue, provide:
- Explanation of the problem
- Suggested fix with code example

## Output

The review report should be clear and actionable:
1. Summary of changes reviewed
2. Issues grouped by severity
3. Specific file:line references
4. Code snippets showing the problem
5. Recommended fixes

## Example Output

```markdown
# Code Review Report

## Summary
- Files reviewed: 3
- Critical issues: 1
- Warnings: 2
- Suggestions: 3

## Critical Issues

### Order.vue:145
**Issue**: Hardcoded API key detected
```javascript
const API_KEY = 'AKIDxxx' // BAD
```
**Fix**: Use environment variables
```javascript
const API_KEY = process.env.VUE_APP_API_KEY
```

## Warnings

### GoodsImageServiceImpl.java:58
**Issue**: Potential N+1 query
```java
for (Image img : images) {
    String url = generatePresignedUrl(img.getImageUrl());
}
```
**Fix**: Batch generate URLs outside loop
```

## Key Principles

1. **Be Thorough**: Review all changes, no matter how small
2. **Be Specific**: Always reference file:line
3. **Be Actionable**: Provide concrete fixes
4. **Be Educational**: Explain why something is wrong
5. **Prioritize**: Focus on critical issues first
