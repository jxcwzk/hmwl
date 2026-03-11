#!/usr/bin/env python3
"""
代码检查脚本，根据阿里巴巴编码规范检查Java代码
"""

import os
import re
import argparse
import json

class CodeChecker:
    def __init__(self):
        # 命名规范正则
        self.class_pattern = re.compile(r'class\s+([A-Z][a-zA-Z0-9]*)')
        self.method_pattern = re.compile(r'\b(public|private|protected)\s+\w+\s+([a-z][a-zA-Z0-9]*)\s*\(')
        self.variable_pattern = re.compile(r'\b(\w+)\s*=')
        self.constant_pattern = re.compile(r'public\s+static\s+final\s+\w+\s+([A-Z_][A-Z0-9_]*)\s*=')
        
        # 代码风格正则
        self.indent_pattern = re.compile(r'^\s*')
        self.line_length_pattern = re.compile(r'.{121,}')
        self.space_pattern = re.compile(r'\b(\w+)\s*([+\-*/=])\s*(\w+)')
        self.brace_pattern = re.compile(r'\{\s*$')
        
        # 异常处理正则
        self.generic_exception_pattern = re.compile(r'catch\s*\(\s*Exception\s+')
        self.empty_catch_pattern = re.compile(r'catch\s*\([^)]+\)\s*\{\s*\}')
        
        # 注释规范正则
        self.class_comment_pattern = re.compile(r'/\*\*[\s\S]*?@author[\s\S]*?@date[\s\S]*?\*/')
        self.method_comment_pattern = re.compile(r'/\*\*[\s\S]*?@param[\s\S]*?@return[\s\S]*?\*/')
        
        # 安全问题正则
        self.sql_injection_pattern = re.compile(r'String\s+\w+\s*=\s*"[^"]*\+\s*\w+\s*\+')
        
    def check_file(self, file_path):
        """检查单个文件"""
        issues = []
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
        except Exception as e:
            issues.append({
                'type': '文件读取错误',
                'line': 0,
                'message': f'无法读取文件: {str(e)}'
            })
            return issues
        
        # 检查每行代码
        for i, line in enumerate(lines, 1):
            # 检查行长度
            if self.line_length_pattern.search(line):
                issues.append({
                    'type': '代码风格',
                    'line': i,
                    'message': '行长度超过120个字符'
                })
            
            # 检查空格使用
            if self.space_pattern.search(line):
                if not re.search(r'\w+\s+[+\-*/=]\s+\w+', line):
                    issues.append({
                        'type': '代码风格',
                        'line': i,
                        'message': '操作符前后应添加空格'
                    })
            
            # 检查大括号位置
            if '{' in line and not self.brace_pattern.search(line):
                issues.append({
                    'type': '代码风格',
                    'line': i,
                    'message': '左大括号应与前面的代码在同一行'
                })
            
            # 检查异常处理
            if self.generic_exception_pattern.search(line):
                issues.append({
                    'type': '异常处理',
                    'line': i,
                    'message': '不要捕获通用的Exception，应捕获具体的异常类型'
                })
            
            if self.empty_catch_pattern.search(line):
                issues.append({
                    'type': '异常处理',
                    'line': i,
                    'message': '捕获异常后不应直接忽略，应进行适当处理'
                })
            
            # 检查安全问题
            if self.sql_injection_pattern.search(line):
                issues.append({
                    'type': '安全问题',
                    'line': i,
                    'message': '可能存在SQL注入风险，应使用参数化查询'
                })
        
        # 检查文件整体
        content = ''.join(lines)
        
        # 检查类注释
        if 'class ' in content and not self.class_comment_pattern.search(content):
            issues.append({
                'type': '注释规范',
                'line': 1,
                'message': '类缺少Javadoc注释'
            })
        
        # 检查方法注释
        method_matches = self.method_pattern.findall(content)
        for match in method_matches:
            method_name = match[1]
            if method_name != 'main' and not re.search(r'/\*\*[\s\S]*?' + method_name + r'\s*\(', content):
                issues.append({
                    'type': '注释规范',
                    'line': 1,
                    'message': f'方法 {method_name} 缺少Javadoc注释'
                })
        
        # 检查命名规范
        class_matches = self.class_pattern.findall(content)
        for class_name in class_matches:
            if not re.match(r'^[A-Z][a-zA-Z0-9]*$', class_name):
                issues.append({
                    'type': '命名规范',
                    'line': 1,
                    'message': f'类名 {class_name} 不符合大驼峰命名规范'
                })
        
        method_matches = self.method_pattern.findall(content)
        for match in method_matches:
            method_name = match[1]
            if not re.match(r'^[a-z][a-zA-Z0-9]*$', method_name):
                issues.append({
                    'type': '命名规范',
                    'line': 1,
                    'message': f'方法名 {method_name} 不符合小驼峰命名规范'
                })
        
        constant_matches = self.constant_pattern.findall(content)
        for constant_name in constant_matches:
            if not re.match(r'^[A-Z_][A-Z0-9_]*$', constant_name):
                issues.append({
                    'type': '命名规范',
                    'line': 1,
                    'message': f'常量名 {constant_name} 不符合全大写命名规范'
                })
        
        return issues
    
    def check_directory(self, directory):
        """检查目录下的所有Java文件"""
        all_issues = {}
        
        for root, dirs, files in os.walk(directory):
            for file in files:
                if file.endswith('.java'):
                    file_path = os.path.join(root, file)
                    issues = self.check_file(file_path)
                    if issues:
                        all_issues[file_path] = issues
        
        return all_issues

def main():
    parser = argparse.ArgumentParser(description='根据阿里巴巴编码规范检查Java代码')
    parser.add_argument('--file', help='单个Java文件路径')
    parser.add_argument('--directory', help='Java文件目录路径')
    parser.add_argument('--output', help='输出文件路径，默认为results.json')
    
    args = parser.parse_args()
    
    checker = CodeChecker()
    
    if args.file:
        issues = checker.check_file(args.file)
        result = {args.file: issues}
    elif args.directory:
        result = checker.check_directory(args.directory)
    else:
        print('请指定文件或目录')
        return
    
    output_file = args.output or 'results.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    print(f'检查完成，结果已保存到 {output_file}')

if __name__ == '__main__':
    main()