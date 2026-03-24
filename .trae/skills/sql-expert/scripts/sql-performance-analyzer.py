#!/usr/bin/env python3
# SQL性能分析脚本

import re
import sys
import time

class SQLPerformanceAnalyzer:
    def __init__(self):
        # 常见的性能问题模式
        self.performance_patterns = {
            'select_star': r'\bSELECT\s+\*\b',
            'no_where': r'\bSELECT\b.*?\bFROM\b.*?(?!\bWHERE\b)',
            'like_start_with_wildcard': r'\bLIKE\s+\'%.*?\'',
            'function_on_index': r'\b\w+\s*\([^\)]*\b\w+\b[^\)]*\)',
            'order_by_no_index': r'\bORDER\s+BY\b',
            'group_by_no_index': r'\bGROUP\s+BY\b',
            'subquery': r'\bSELECT\b.*?\bFROM\b.*?\((SELECT.*?)\)',
            'multiple_joins': r'\bJOIN\b.*?\bJOIN\b',
            'large_limit': r'\bLIMIT\s+\d{5,}\b',
            'in_with_large_set': r'\bIN\s*\([^\)]{50,}\)'
        }
        
        # 性能问题描述
        self.pattern_descriptions = {
            'select_star': '使用了SELECT *，会查询所有列，增加网络传输和内存消耗',
            'no_where': '缺少WHERE子句，会导致全表扫描',
            'like_start_with_wildcard': 'LIKE查询以%开头，会导致索引失效',
            'function_on_index': '在索引列上使用函数，会导致索引失效',
            'order_by_no_index': 'ORDER BY操作可能没有使用索引',
            'group_by_no_index': 'GROUP BY操作可能没有使用索引',
            'subquery': '使用了子查询，可能影响性能',
            'multiple_joins': '使用了多个JOIN，可能影响性能',
            'large_limit': 'LIMIT值过大，可能影响性能',
            'in_with_large_set': 'IN子句包含大量值，可能影响性能'
        }
    
    def analyze_performance(self, sql):
        """分析SQL性能问题"""
        issues = []
        
        # 去除注释
        sql = re.sub(r'--.*$', '', sql, flags=re.MULTILINE)
        sql = re.sub(r'/\*.*?\*/', '', sql, flags=re.DOTALL)
        
        # 检查性能问题模式
        for pattern_name, pattern in self.performance_patterns.items():
            if re.search(pattern, sql, re.IGNORECASE | re.DOTALL):
                issues.append({
                    'type': pattern_name,
                    'description': self.pattern_descriptions[pattern_name]
                })
        
        # 检查其他性能问题
        # 检查OR条件
        if re.search(r'\bOR\b', sql, re.IGNORECASE):
            issues.append({
                'type': 'or_condition',
                'description': '使用了OR条件，可能导致索引失效'
            })
        
        # 检查!=或<>操作符
        if re.search(r'!=|<>', sql):
            issues.append({
                'type': 'inequality_operator',
                'description': '使用了!=或<>操作符，可能导致索引失效'
            })
        
        # 检查IS NULL或IS NOT NULL
        if re.search(r'\bIS\s+NULL\b|\bIS\s+NOT\s+NULL\b', sql, re.IGNORECASE):
            issues.append({
                'type': 'null_check',
                'description': '使用了IS NULL或IS NOT NULL，可能导致索引失效'
            })
        
        # 检查大量的AND条件
        and_count = len(re.findall(r'\bAND\b', sql, re.IGNORECASE))
        if and_count > 5:
            issues.append({
                'type': 'too_many_and_conditions',
                'description': f'WHERE子句包含{and_count}个AND条件，可能影响性能'
            })
        
        return issues
    
    def suggest_optimizations(self, sql, issues):
        """根据性能问题提供优化建议"""
        suggestions = []
        
        for issue in issues:
            if issue['type'] == 'select_star':
                # 尝试提取表名
                table_match = re.search(r'\bFROM\s+(\w+)', sql, re.IGNORECASE)
                if table_match:
                    table_name = table_match.group(1)
                    suggestions.append(f'避免使用SELECT *，改为明确指定需要的列，例如：SELECT column1, column2 FROM {table_name}')
                else:
                    suggestions.append('避免使用SELECT *，改为明确指定需要的列')
            
            elif issue['type'] == 'no_where':
                suggestions.append('添加WHERE子句以限制查询范围，避免全表扫描')
            
            elif issue['type'] == 'like_start_with_wildcard':
                suggestions.append('避免使用以%开头的LIKE查询，考虑使用全文索引或其他搜索方式')
            
            elif issue['type'] == 'function_on_index':
                suggestions.append('避免在索引列上使用函数，考虑在查询值上使用函数')
            
            elif issue['type'] == 'order_by_no_index':
                suggestions.append('为ORDER BY的列创建索引，提高排序性能')
            
            elif issue['type'] == 'group_by_no_index':
                suggestions.append('为GROUP BY的列创建索引，提高分组性能')
            
            elif issue['type'] == 'subquery':
                suggestions.append('考虑使用JOIN代替子查询，提高性能')
            
            elif issue['type'] == 'multiple_joins':
                suggestions.append('检查JOIN的必要性，避免不必要的JOIN操作')
            
            elif issue['type'] == 'large_limit':
                suggestions.append('减小LIMIT值，避免一次性获取过多数据')
            
            elif issue['type'] == 'in_with_large_set':
                suggestions.append('考虑使用临时表或JOIN代替包含大量值的IN子句')
            
            elif issue['type'] == 'or_condition':
                suggestions.append('考虑使用UNION代替OR条件，提高性能')
            
            elif issue['type'] == 'inequality_operator':
                suggestions.append('避免使用!=或<>操作符，考虑使用其他条件')
            
            elif issue['type'] == 'null_check':
                suggestions.append('考虑使用默认值代替NULL，避免IS NULL检查')
            
            elif issue['type'] == 'too_many_and_conditions':
                suggestions.append('考虑使用索引覆盖或优化WHERE子句的顺序')
        
        return suggestions
    
    def analyze(self, sql):
        """执行完整的性能分析"""
        start_time = time.time()
        issues = self.analyze_performance(sql)
        suggestions = self.suggest_optimizations(sql, issues)
        end_time = time.time()
        
        results = {
            'issues': issues,
            'suggestions': suggestions,
            'analysis_time': end_time - start_time
        }
        
        return results

def main():
    if len(sys.argv) != 2:
        print('Usage: python sql-performance-analyzer.py <sql_file>')
        sys.exit(1)
    
    sql_file = sys.argv[1]
    
    try:
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql = f.read()
    except FileNotFoundError:
        print(f'Error: File {sql_file} not found')
        sys.exit(1)
    
    analyzer = SQLPerformanceAnalyzer()
    results = analyzer.analyze(sql)
    
    print('SQL性能分析结果:')
    print('-' * 50)
    
    if results['issues']:
        print('性能问题:')
        for i, issue in enumerate(results['issues'], 1):
            print(f'  {i}. {issue["description"]}')
    else:
        print('未发现明显的性能问题')
    
    print('-' * 50)
    
    if results['suggestions']:
        print('优化建议:')
        for i, suggestion in enumerate(results['suggestions'], 1):
            print(f'  {i}. {suggestion}')
    else:
        print('无优化建议')
    
    print('-' * 50)
    print(f'分析时间: {results["analysis_time"]:.4f} 秒')
    print('-' * 50)

if __name__ == '__main__':
    main()