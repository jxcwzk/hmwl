#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
微信小程序UI检查工具
用于检查微信小程序界面是否符合设计规范
"""

import os
import re
import json
import argparse

# 设计规范常量
DESIGN_GUIDELINES = {
    "colors": {
        "primary": "#07C160",
        "success": "#1AAD19",
        "info": "#2782E3",
        "warning": "#F7BA2A",
        "error": "#F76260",
        "text": {
            "dark": "#333333",
            "medium": "#666666",
            "light": "#999999"
        },
        "background": {
            "white": "#FFFFFF",
            "light": "#F2F2F2",
            "border": "#E5E5E5"
        }
    },
    "fonts": {
        "sizes": [11, 12, 13, 14, 16, 18, 20],
        "families": ["PingFang SC", "Helvetica Neue", "Arial"]
    },
    "spacing": [8, 12, 16, 20, 24],
    "border_radius": [4, 8, 12, 16, 24],
    "button_heights": [32, 36, 40, 44],
    "list_heights": [48, 52, 60],
    "navigation_heights": {
        "top": 44,
        "bottom": 50,
        "tab": 40
    }
}

def check_file(file_path):
    """检查单个文件"""
    results = []
    file_ext = os.path.splitext(file_path)[1].lower()
    
    if file_ext == '.wxss':
        results.extend(check_wxss(file_path))
    elif file_ext == '.wxml':
        results.extend(check_wxml(file_path))
    elif file_ext == '.json':
        results.extend(check_json(file_path))
    
    return results

def check_wxss(file_path):
    """检查WXSS文件"""
    results = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        results.append({
            "file": file_path,
            "type": "error",
            "message": f"无法读取文件: {e}"
        })
        return results
    
    # 检查颜色使用
    color_pattern = r'#[0-9A-Fa-f]{6}'
    colors = re.findall(color_pattern, content)
    for color in colors:
        color = color.upper()
        valid_colors = list(DESIGN_GUIDELINES['colors']['text'].values()) + \
                      list(DESIGN_GUIDELINES['colors']['background'].values()) + \
                      [DESIGN_GUIDELINES['colors']['primary'],
                       DESIGN_GUIDELINES['colors']['success'],
                       DESIGN_GUIDELINES['colors']['info'],
                       DESIGN_GUIDELINES['colors']['warning'],
                       DESIGN_GUIDELINES['colors']['error']]
        if color not in valid_colors:
            results.append({
                "file": file_path,
                "type": "warning",
                "message": f"使用了非规范颜色: {color}"
            })
    
    # 检查字体大小
    font_size_pattern = r'font-size:\s*(\d+)rpx'
    font_sizes = re.findall(font_size_pattern, content)
    for size in font_sizes:
        size = int(size)
        if size not in DESIGN_GUIDELINES['fonts']['sizes']:
            results.append({
                "file": file_path,
                "type": "warning",
                "message": f"使用了非规范字体大小: {size}rpx"
            })
    
    # 检查间距
    margin_pattern = r'margin[-_]?[\w]*:\s*(\d+)rpx'
    padding_pattern = r'padding[-_]?[\w]*:\s*(\d+)rpx'
    margins = re.findall(margin_pattern, content)
    paddings = re.findall(padding_pattern, content)
    
    for spacing in margins + paddings:
        spacing = int(spacing)
        if spacing not in DESIGN_GUIDELINES['spacing']:
            results.append({
                "file": file_path,
                "type": "warning",
                "message": f"使用了非规范间距: {spacing}rpx"
            })
    
    # 检查圆角
    border_radius_pattern = r'border-radius:\s*(\d+)rpx'
    border_radii = re.findall(border_radius_pattern, content)
    for radius in border_radii:
        radius = int(radius)
        if radius not in DESIGN_GUIDELINES['border_radius']:
            results.append({
                "file": file_path,
                "type": "warning",
                "message": f"使用了非规范圆角: {radius}rpx"
            })
    
    return results

def check_wxml(file_path):
    """检查WXML文件"""
    results = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        results.append({
            "file": file_path,
            "type": "error",
            "message": f"无法读取文件: {e}"
        })
        return results
    
    # 检查wx:for是否使用wx:key
    if 'wx:for' in content and 'wx:key' not in content:
        results.append({
            "file": file_path,
            "type": "warning",
            "message": "使用wx:for时应添加wx:key以提高性能"
        })
    
    # 检查是否有过多的节点层级
    lines = content.split('\n')
    max_depth = 0
    current_depth = 0
    
    for line in lines:
        line = line.strip()
        if line.startswith('<') and not line.startswith('</'):
            current_depth += 1
            max_depth = max(max_depth, current_depth)
        elif line.startswith('</'):
            current_depth -= 1
    
    if max_depth > 10:
        results.append({
            "file": file_path,
            "type": "warning",
            "message": f"页面节点层级过深: {max_depth}层（建议不超过10层）"
        })
    
    return results

def check_json(file_path):
    """检查JSON文件"""
    results = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = json.load(f)
    except Exception as e:
        results.append({
            "file": file_path,
            "type": "error",
            "message": f"无法读取文件: {e}"
        })
        return results
    
    # 检查页面配置
    if 'navigationBarTitleText' in content:
        # 导航栏标题检查
        pass
    
    if 'navigationBarBackgroundColor' in content:
        color = content['navigationBarBackgroundColor'].upper()
        if color != DESIGN_GUIDELINES['colors']['primary']:
            results.append({
                "file": file_path,
                "type": "warning",
                "message": f"导航栏背景色应使用主色调: {DESIGN_GUIDELINES['colors']['primary']}"
            })
    
    return results

def check_directory(directory):
    """检查目录"""
    results = []
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(('.wxss', '.wxml', '.json')):
                file_path = os.path.join(root, file)
                results.extend(check_file(file_path))
    
    return results

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description='微信小程序UI检查工具')
    parser.add_argument('path', help='要检查的文件或目录路径')
    parser.add_argument('--output', '-o', help='输出结果文件')
    
    args = parser.parse_args()
    
    if os.path.isfile(args.path):
        results = check_file(args.path)
    elif os.path.isdir(args.path):
        results = check_directory(args.path)
    else:
        print(f"错误: {args.path} 不是有效的文件或目录")
        return
    
    # 输出结果
    if args.output:
        with open(args.output, 'w', encoding='utf-8') as f:
            json.dump(results, f, ensure_ascii=False, indent=2)
        print(f"检查结果已保存到: {args.output}")
    else:
        print("检查结果:")
        for result in results:
            print(f"[{result['type'].upper()}] {result['file']}: {result['message']}")
    
    # 统计结果
    error_count = sum(1 for r in results if r['type'] == 'error')
    warning_count = sum(1 for r in results if r['type'] == 'warning')
    
    print(f"\n统计: {error_count} 个错误, {warning_count} 个警告")

if __name__ == '__main__':
    main()