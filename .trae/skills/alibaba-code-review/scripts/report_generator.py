#!/usr/bin/env python3
"""
报告生成脚本，根据代码检查结果生成HTML格式的代码审查报告
"""

import json
import argparse
import os

class ReportGenerator:
    def __init__(self):
        self.template = """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>代码审查报告</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        h2 {
            color: #555;
            margin-top: 30px;
        }
        h3 {
            color: #666;
            margin-top: 20px;
        }
        .summary {
            background-color: #f9f9f9;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .summary-item {
            display: inline-block;
            margin-right: 20px;
            padding: 5px 15px;
            background-color: #e3f2fd;
            border-radius: 20px;
        }
        .file-section {
            margin-bottom: 30px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .file-path {
            font-weight: bold;
            color: #4CAF50;
            margin-bottom: 10px;
        }
        .issue {
            margin-bottom: 10px;
            padding: 10px;
            background-color: #f8f8f8;
            border-left: 4px solid #ff9800;
            border-radius: 4px;
        }
        .issue-type {
            font-weight: bold;
            color: #f57c00;
        }
        .issue-line {
            color: #666;
            font-size: 0.9em;
        }
        .issue-message {
            margin-top: 5px;
        }
        .severity {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.8em;
            margin-left: 10px;
        }
        .severity-high {
            background-color: #ffcdd2;
            color: #c62828;
        }
        .severity-medium {
            background-color: #fff3e0;
            color: #ef6c00;
        }
        .severity-low {
            background-color: #e8f5e8;
            color: #2e7d32;
        }
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
            text-align: center;
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>代码审查报告</h1>
        <div class="summary">
            <h2>审查摘要</h2>
            <div class="summary-item">总文件数: {{total_files}}</div>
            <div class="summary-item">问题文件数: {{issue_files}}</div>
            <div class="summary-item">总问题数: {{total_issues}}</div>
            <div class="summary-item">严重问题: {{high_issues}}</div>
            <div class="summary-item">中等问题: {{medium_issues}}</div>
            <div class="summary-item">轻微问题: {{low_issues}}</div>
        </div>
        
        <h2>详细问题</h2>
        {{file_sections}}
        
        <div class="footer">
            <p>报告生成时间: {{generate_time}}</p>
            <p>基于阿里巴巴Java编码规范</p>
        </div>
    </div>
</body>
</html>
        """
    
    def get_severity(self, issue_type):
        """根据问题类型确定严重程度"""
        severity_map = {
            '安全问题': 'high',
            '异常处理': 'medium',
            '命名规范': 'low',
            '代码风格': 'low',
            '注释规范': 'low',
            '文件读取错误': 'high'
        }
        return severity_map.get(issue_type, 'medium')
    
    def generate_report(self, input_file, output_file):
        """生成报告"""
        try:
            with open(input_file, 'r', encoding='utf-8') as f:
                results = json.load(f)
        except Exception as e:
            print(f'无法读取输入文件: {str(e)}')
            return
        
        # 统计信息
        total_files = len(results)
        issue_files = sum(1 for issues in results.values() if issues)
        total_issues = sum(len(issues) for issues in results.values())
        
        # 按严重程度统计
        severity_count = {'high': 0, 'medium': 0, 'low': 0}
        for issues in results.values():
            for issue in issues:
                severity = self.get_severity(issue['type'])
                severity_count[severity] += 1
        
        # 生成文件部分
        file_sections = []
        for file_path, issues in results.items():
            if not issues:
                continue
            
            section = f'<div class="file-section">\n                <div class="file-path">{file_path}</div>'
            
            for issue in issues:
                severity = self.get_severity(issue['type'])
                severity_class = f'severity severity-{severity}'
                severity_text = {'high': '严重', 'medium': '中等', 'low': '轻微'}[severity]
                
                section += f'''
                <div class="issue">
                    <span class="issue-type">{issue['type']}</span>
                    <span class="{severity_class}">{severity_text}</span>
                    <span class="issue-line">第 {issue['line']} 行</span>
                    <div class="issue-message">{issue['message']}</div>
                </div>'''
            
            section += '</div>'
            file_sections.append(section)
        
        file_sections_html = '\n'.join(file_sections)
        
        # 填充模板
        import datetime
        generate_time = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        
        report = self.template
        report = report.replace('{{total_files}}', str(total_files))
        report = report.replace('{{issue_files}}', str(issue_files))
        report = report.replace('{{total_issues}}', str(total_issues))
        report = report.replace('{{high_issues}}', str(severity_count['high']))
        report = report.replace('{{medium_issues}}', str(severity_count['medium']))
        report = report.replace('{{low_issues}}', str(severity_count['low']))
        report = report.replace('{{file_sections}}', file_sections_html)
        report = report.replace('{{generate_time}}', generate_time)
        
        # 写入输出文件
        try:
            with open(output_file, 'w', encoding='utf-8') as f:
                f.write(report)
            print(f'报告已生成: {output_file}')
        except Exception as e:
            print(f'无法写入输出文件: {str(e)}')

def main():
    parser = argparse.ArgumentParser(description='生成代码审查报告')
    parser.add_argument('--input', required=True, help='输入文件路径（results.json）')
    parser.add_argument('--output', default='report.html', help='输出文件路径，默认为report.html')
    
    args = parser.parse_args()
    
    generator = ReportGenerator()
    generator.generate_report(args.input, args.output)

if __name__ == '__main__':
    main()
