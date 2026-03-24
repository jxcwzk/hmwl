#!/usr/bin/env python3
# SQL错误检测脚本

import re
import sys

class SQLErrorChecker:
    def __init__(self):
        # SQL关键字列表
        self.sql_keywords = {
            'SELECT', 'FROM', 'WHERE', 'JOIN', 'LEFT', 'RIGHT', 'INNER', 'OUTER',
            'GROUP', 'BY', 'ORDER', 'LIMIT', 'OFFSET', 'HAVING', 'AS', 'ON',
            'INSERT', 'UPDATE', 'DELETE', 'CREATE', 'ALTER', 'DROP', 'TRUNCATE',
            'TABLE', 'DATABASE', 'INDEX', 'VIEW', 'PROCEDURE', 'FUNCTION', 'TRIGGER',
            'BEGIN', 'COMMIT', 'ROLLBACK', 'SAVEPOINT', 'SET', 'VALUES', 'IN',
            'LIKE', 'BETWEEN', 'AND', 'OR', 'NOT', 'IS', 'NULL', 'EXISTS', 'UNION',
            'DISTINCT', 'ALL', 'ANY', 'SOME', 'CASE', 'WHEN', 'THEN', 'ELSE', 'END'
        }
        
        # 常见SQL函数列表
        self.sql_functions = {
            'COUNT', 'SUM', 'AVG', 'MAX', 'MIN', 'ABS', 'CEIL', 'FLOOR', 'ROUND',
            'TRUNCATE', 'CONCAT', 'SUBSTRING', 'LENGTH', 'LOWER', 'UPPER', 'TRIM',
            'NOW', 'CURRENT_DATE', 'CURRENT_TIME', 'CURRENT_TIMESTAMP', 'DATE',
            'TIME', 'YEAR', 'MONTH', 'DAY', 'HOUR', 'MINUTE', 'SECOND', 'DATEDIFF',
            'TIMESTAMPDIFF', 'IF', 'IFNULL', 'NULLIF', 'COALESCE', 'CAST', 'CONVERT'
        }
    
    def check_syntax(self, sql):
        """检查SQL语法错误"""
        errors = []
        
        # 去除注释
        sql = re.sub(r'--.*$', '', sql, flags=re.MULTILINE)
        sql = re.sub(r'/\*.*?\*/', '', sql, flags=re.DOTALL)
        
        # 检查分号
        if not sql.strip().endswith(';'):
            errors.append('缺少结束分号')
        
        # 检查关键字拼写
        words = re.findall(r'\b\w+\b', sql.upper())
        for word in words:
            if word in self.sql_keywords or word in self.sql_functions:
                continue
            # 检查是否是可能的关键字拼写错误
            for keyword in self.sql_keywords:
                if self.levenshtein_distance(word, keyword) <= 2:
                    errors.append(f'可能的关键字拼写错误: {word} (可能是 {keyword})')
                    break
        
        # 检查括号匹配
        open_parens = sql.count('(')
        close_parens = sql.count(')')
        if open_parens != close_parens:
            errors.append(f'括号不匹配: 打开 {open_parens} 个，关闭 {close_parens} 个')
        
        # 检查单引号匹配
        single_quotes = sql.count("'")
        if single_quotes % 2 != 0:
            errors.append('单引号不匹配')
        
        # 检查双引号匹配
        double_quotes = sql.count('"')
        if double_quotes % 2 != 0:
            errors.append('双引号不匹配')
        
        # 检查常见语法错误
        # 检查SELECT语句
        if re.search(r'\bSELECT\b', sql, re.IGNORECASE):
            if not re.search(r'\bFROM\b', sql, re.IGNORECASE):
                errors.append('SELECT语句缺少FROM子句')
        
        # 检查INSERT语句
        if re.search(r'\bINSERT\b', sql, re.IGNORECASE):
            if not re.search(r'\bINTO\b', sql, re.IGNORECASE):
                errors.append('INSERT语句缺少INTO关键字')
            if not re.search(r'\bVALUES\b', sql, re.IGNORECASE):
                errors.append('INSERT语句缺少VALUES子句')
        
        # 检查UPDATE语句
        if re.search(r'\bUPDATE\b', sql, re.IGNORECASE):
            if not re.search(r'\bSET\b', sql, re.IGNORECASE):
                errors.append('UPDATE语句缺少SET子句')
        
        # 检查DELETE语句
        if re.search(r'\bDELETE\b', sql, re.IGNORECASE):
            if not re.search(r'\bFROM\b', sql, re.IGNORECASE):
                errors.append('DELETE语句缺少FROM子句')
        
        return errors
    
    def check_logic(self, sql):
        """检查SQL逻辑错误"""
        errors = []
        
        # 检查WHERE子句中的逻辑错误
        if re.search(r'\bWHERE\b', sql, re.IGNORECASE):
            # 检查矛盾条件
            if re.search(r'\bAND\b.*?>.*?<.*?\bAND\b', sql, re.IGNORECASE | re.DOTALL):
                errors.append('可能存在矛盾的WHERE条件')
        
        # 检查JOIN语句
        if re.search(r'\bJOIN\b', sql, re.IGNORECASE):
            if not re.search(r'\bON\b', sql, re.IGNORECASE):
                errors.append('JOIN语句缺少ON子句')
        
        # 检查GROUP BY语句
        if re.search(r'\bGROUP\s+BY\b', sql, re.IGNORECASE):
            if not re.search(r'\bSELECT\b.*?\bFROM\b', sql, re.IGNORECASE | re.DOTALL):
                errors.append('GROUP BY语句应该与SELECT语句一起使用')
        
        # 检查ORDER BY语句
        if re.search(r'\bORDER\s+BY\b', sql, re.IGNORECASE):
            if not re.search(r'\bSELECT\b.*?\bFROM\b', sql, re.IGNORECASE | re.DOTALL):
                errors.append('ORDER BY语句应该与SELECT语句一起使用')
        
        return errors
    
    def check_security(self, sql):
        """检查SQL安全问题"""
        errors = []
        
        # 检查SQL注入风险
        # 检查拼接的字符串
        if re.search(r"'\s*\+\s*", sql):
            errors.append('可能存在SQL注入风险：使用了字符串拼接')
        
        # 检查常见的SQL注入模式
        injection_patterns = [
            r"'\s*OR\s*1\s*=\s*1",
            r"'\s*AND\s*1\s*=\s*1",
            r"'\s*;\s*DROP\s*TABLE",
            r"'\s*;\s*DELETE\s*FROM",
            r"'\s*--",
            r"'\s*#"
        ]
        
        for pattern in injection_patterns:
            if re.search(pattern, sql, re.IGNORECASE):
                errors.append('可能存在SQL注入风险')
                break
        
        return errors
    
    def levenshtein_distance(self, s1, s2):
        """计算两个字符串之间的编辑距离"""
        if len(s1) < len(s2):
            return self.levenshtein_distance(s2, s1)
        
        if len(s2) == 0:
            return len(s1)
        
        previous_row = range(len(s2) + 1)
        for i, c1 in enumerate(s1):
            current_row = [i + 1]
            for j, c2 in enumerate(s2):
                insertions = previous_row[j + 1] + 1
                deletions = current_row[j] + 1
                substitutions = previous_row[j] + (c1 != c2)
                current_row.append(min(insertions, deletions, substitutions))
            previous_row = current_row
        
        return previous_row[-1]
    
    def check(self, sql):
        """执行完整的SQL检查"""
        results = {
            'syntax_errors': self.check_syntax(sql),
            'logic_errors': self.check_logic(sql),
            'security_issues': self.check_security(sql)
        }
        
        return results

def main():
    if len(sys.argv) != 2:
        print('Usage: python sql-checker.py <sql_file>')
        sys.exit(1)
    
    sql_file = sys.argv[1]
    
    try:
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql = f.read()
    except FileNotFoundError:
        print(f'Error: File {sql_file} not found')
        sys.exit(1)
    
    checker = SQLErrorChecker()
    results = checker.check(sql)
    
    print('SQL错误检测结果:')
    print('-' * 50)
    
    if results['syntax_errors']:
        print('语法错误:')
        for error in results['syntax_errors']:
            print(f'  - {error}')
    else:
        print('语法检查通过')
    
    print('-' * 50)
    
    if results['logic_errors']:
        print('逻辑错误:')
        for error in results['logic_errors']:
            print(f'  - {error}')
    else:
        print('逻辑检查通过')
    
    print('-' * 50)
    
    if results['security_issues']:
        print('安全问题:')
        for issue in results['security_issues']:
            print(f'  - {issue}')
    else:
        print('安全检查通过')
    
    print('-' * 50)

if __name__ == '__main__':
    main()