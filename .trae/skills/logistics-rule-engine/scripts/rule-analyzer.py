#!/usr/bin/env python3
# 规则分析脚本

import sys
import json
import networkx as nx
import matplotlib.pyplot as plt

class RuleAnalyzer:
    def __init__(self):
        self.rules = {}
        self.dependencies = {}
        self.conflicts = {}
        self.performance_issues = {}
    
    def load_rules(self, rules_file):
        """加载规则文件"""
        try:
            with open(rules_file, 'r', encoding='utf-8') as f:
                self.rules = json.load(f)
            print(f"成功加载 {len(self.rules)} 条规则")
        except Exception as e:
            print(f"加载规则文件失败: {e}")
            sys.exit(1)
    
    def analyze_dependencies(self):
        """分析规则依赖关系"""
        print("\n分析规则依赖关系...")
        
        for rule_name, rule in self.rules.items():
            dependencies = []
            
            # 分析条件中的依赖
            conditions = rule.get("conditions", [])
            for condition in conditions:
                field = condition.get("field")
                if field:
                    dependencies.append(field)
            
            # 分析动作中的依赖
            actions = rule.get("actions", [])
            for action in actions:
                field = action.get("field")
                if field:
                    dependencies.append(field)
            
            self.dependencies[rule_name] = list(set(dependencies))
        
        print("依赖关系分析完成")
    
    def analyze_conflicts(self):
        """分析规则冲突"""
        print("\n分析规则冲突...")
        
        for rule1_name, rule1 in self.rules.items():
            conflicts = []
            
            for rule2_name, rule2 in self.rules.items():
                if rule1_name == rule2_name:
                    continue
                
                # 检查条件冲突
                if self.check_condition_conflict(rule1, rule2):
                    conflicts.append(rule2_name)
            
            if conflicts:
                self.conflicts[rule1_name] = conflicts
        
        print("规则冲突分析完成")
    
    def check_condition_conflict(self, rule1, rule2):
        """检查两个规则的条件是否冲突"""
        conditions1 = rule1.get("conditions", [])
        conditions2 = rule2.get("conditions", [])
        
        # 检查是否有相同字段的相反条件
        field_conditions1 = {}
        for condition in conditions1:
            field = condition.get("field")
            operator = condition.get("operator")
            value = condition.get("value")
            if field:
                field_conditions1[field] = (operator, value)
        
        for condition in conditions2:
            field = condition.get("field")
            operator = condition.get("operator")
            value = condition.get("value")
            
            if field in field_conditions1:
                op1, val1 = field_conditions1[field]
                if self.check_operators_conflict(op1, val1, operator, value):
                    return True
        
        return False
    
    def check_operators_conflict(self, op1, val1, op2, val2):
        """检查两个操作符是否冲突"""
        # 简单的冲突检查
        if op1 == "==" and op2 == "==" and val1 != val2:
            return True
        elif op1 == ">" and op2 == "<" and val1 <= val2:
            return True
        elif op1 == "<" and op2 == ">" and val1 >= val2:
            return True
        elif op1 == ">=" and op2 == "<" and val1 >= val2:
            return True
        elif op1 == "<=" and op2 == ">" and val1 <= val2:
            return True
        return False
    
    def analyze_performance(self):
        """分析规则性能问题"""
        print("\n分析规则性能问题...")
        
        for rule_name, rule in self.rules.items():
            issues = []
            
            # 检查条件复杂度
            conditions = rule.get("conditions", [])
            if len(conditions) > 5:
                issues.append("条件数量过多")
            
            # 检查动作复杂度
            actions = rule.get("actions", [])
            if len(actions) > 3:
                issues.append("动作数量过多")
            
            # 检查字段访问深度
            for condition in conditions:
                field = condition.get("field")
                if field and "." in field and field.count(".") > 2:
                    issues.append("字段访问深度过大")
                    break
            
            # 检查计算复杂度
            for action in actions:
                if action.get("type") == "calculate":
                    expression = action.get("value", "")
                    if len(expression) > 50:
                        issues.append("计算表达式过于复杂")
                        break
            
            if issues:
                self.performance_issues[rule_name] = issues
        
        print("性能问题分析完成")
    
    def generate_dependency_graph(self):
        """生成依赖关系图"""
        print("\n生成依赖关系图...")
        
        G = nx.DiGraph()
        
        # 添加规则节点
        for rule_name in self.rules.keys():
            G.add_node(rule_name)
        
        # 添加依赖边
        for rule_name, deps in self.dependencies.items():
            for dep in deps:
                G.add_edge(rule_name, dep)
        
        # 绘制图形
        plt.figure(figsize=(12, 8))
        pos = nx.spring_layout(G, k=0.3)
        nx.draw(G, pos, with_labels=True, node_size=1000, node_color='lightblue', font_size=8, font_weight='bold')
        plt.title('规则依赖关系图')
        plt.savefig('rule_dependencies.png')
        print("依赖关系图已保存到 rule_dependencies.png")
    
    def generate_conflict_graph(self):
        """生成冲突关系图"""
        print("\n生成冲突关系图...")
        
        G = nx.Graph()
        
        # 添加规则节点
        for rule_name in self.rules.keys():
            G.add_node(rule_name)
        
        # 添加冲突边
        for rule1, conflicts in self.conflicts.items():
            for rule2 in conflicts:
                G.add_edge(rule1, rule2)
        
        # 绘制图形
        plt.figure(figsize=(12, 8))
        pos = nx.spring_layout(G, k=0.3)
        nx.draw(G, pos, with_labels=True, node_size=1000, node_color='lightcoral', font_size=8, font_weight='bold')
        plt.title('规则冲突关系图')
        plt.savefig('rule_conflicts.png')
        print("冲突关系图已保存到 rule_conflicts.png")
    
    def generate_report(self):
        """生成分析报告"""
        print("\n" + "=" * 80)
        print("规则分析报告")
        print("=" * 80)
        
        # 依赖关系分析
        print("\n1. 依赖关系分析")
        print("-" * 40)
        for rule_name, deps in self.dependencies.items():
            print(f"{rule_name}: {', '.join(deps) if deps else '无依赖'}")
        
        # 冲突分析
        print("\n2. 冲突分析")
        print("-" * 40)
        if self.conflicts:
            for rule_name, conflicts in self.conflicts.items():
                print(f"{rule_name}: 与 {', '.join(conflicts)} 冲突")
        else:
            print("未发现规则冲突")
        
        # 性能分析
        print("\n3. 性能分析")
        print("-" * 40)
        if self.performance_issues:
            for rule_name, issues in self.performance_issues.items():
                print(f"{rule_name}: {', '.join(issues)}")
        else:
            print("未发现性能问题")
        
        # 规则统计
        print("\n4. 规则统计")
        print("-" * 40)
        print(f"总规则数: {len(self.rules)}")
        print(f"有依赖的规则数: {sum(1 for deps in self.dependencies.values() if deps)}")
        print(f"有冲突的规则数: {len(self.conflicts)}")
        print(f"有性能问题的规则数: {len(self.performance_issues)}")
        
        # 保存分析结果
        analysis_result = {
            "dependencies": self.dependencies,
            "conflicts": self.conflicts,
            "performance_issues": self.performance_issues
        }
        
        with open("rule_analysis.json", "w", encoding="utf-8") as f:
            json.dump(analysis_result, f, indent=2, ensure_ascii=False)
        print("\n分析结果已保存到 rule_analysis.json")
        print("=" * 80)
    
    def run_analysis(self):
        """运行完整分析"""
        self.analyze_dependencies()
        self.analyze_conflicts()
        self.analyze_performance()
        self.generate_dependency_graph()
        self.generate_conflict_graph()
        self.generate_report()

def main():
    if len(sys.argv) != 2:
        print("Usage: python rule-analyzer.py <rules_file>")
        sys.exit(1)
    
    rules_file = sys.argv[1]
    
    analyzer = RuleAnalyzer()
    analyzer.load_rules(rules_file)
    analyzer.run_analysis()

if __name__ == "__main__":
    main()