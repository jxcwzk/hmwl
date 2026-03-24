#!/usr/bin/env python3
# 规则测试脚本

import sys
import time
import json

class RuleTester:
    def __init__(self):
        self.rules = {}
        self.test_cases = {}
    
    def load_rules(self, rules_file):
        """加载规则文件"""
        try:
            with open(rules_file, 'r', encoding='utf-8') as f:
                self.rules = json.load(f)
            print(f"成功加载 {len(self.rules)} 条规则")
        except Exception as e:
            print(f"加载规则文件失败: {e}")
            sys.exit(1)
    
    def load_test_cases(self, test_cases_file):
        """加载测试用例文件"""
        try:
            with open(test_cases_file, 'r', encoding='utf-8') as f:
                self.test_cases = json.load(f)
            print(f"成功加载 {len(self.test_cases)} 个测试用例")
        except Exception as e:
            print(f"加载测试用例文件失败: {e}")
            sys.exit(1)
    
    def test_rule(self, rule_name, test_data):
        """测试单个规则"""
        if rule_name not in self.rules:
            return {"status": "error", "message": f"规则 {rule_name} 不存在"}
        
        rule = self.rules[rule_name]
        start_time = time.time()
        
        try:
            # 模拟规则执行
            # 这里只是一个示例，实际应该根据规则引擎的API执行规则
            # 例如使用Drools、Easy Rules等规则引擎
            
            # 检查条件
            conditions = rule.get("conditions", [])
            condition_result = self.evaluate_conditions(conditions, test_data)
            
            if not condition_result:
                return {
                    "status": "pass",
                    "message": "规则条件不满足",
                    "executed": False,
                    "execution_time": time.time() - start_time
                }
            
            # 执行动作
            actions = rule.get("actions", [])
            result_data = self.execute_actions(actions, test_data.copy())
            
            return {
                "status": "pass",
                "message": "规则执行成功",
                "executed": True,
                "execution_time": time.time() - start_time,
                "result": result_data
            }
        except Exception as e:
            return {
                "status": "fail",
                "message": f"规则执行失败: {str(e)}",
                "execution_time": time.time() - start_time
            }
    
    def evaluate_conditions(self, conditions, data):
        """评估规则条件"""
        for condition in conditions:
            field = condition.get("field")
            operator = condition.get("operator")
            value = condition.get("value")
            
            # 获取字段值
            field_value = self.get_field_value(data, field)
            
            # 评估条件
            if not self.evaluate_condition(field_value, operator, value):
                return False
        
        return True
    
    def get_field_value(self, data, field):
        """获取字段值"""
        if "." in field:
            parts = field.split(".")
            value = data
            for part in parts:
                if isinstance(value, dict) and part in value:
                    value = value[part]
                else:
                    return None
            return value
        else:
            return data.get(field)
    
    def evaluate_condition(self, field_value, operator, value):
        """评估单个条件"""
        if operator == "==":
            return field_value == value
        elif operator == "!=":
            return field_value != value
        elif operator == ">":
            return field_value > value
        elif operator == ">=":
            return field_value >= value
        elif operator == "<":
            return field_value < value
        elif operator == "<=":
            return field_value <= value
        elif operator == "contains":
            return value in field_value
        elif operator == "not contains":
            return value not in field_value
        else:
            return False
    
    def execute_actions(self, actions, data):
        """执行规则动作"""
        for action in actions:
            action_type = action.get("type")
            field = action.get("field")
            value = action.get("value")
            
            if action_type == "set":
                self.set_field_value(data, field, value)
            elif action_type == "calculate":
                self.calculate_field_value(data, field, value)
        
        return data
    
    def set_field_value(self, data, field, value):
        """设置字段值"""
        if "." in field:
            parts = field.split(".")
            current = data
            for part in parts[:-1]:
                if part not in current:
                    current[part] = {}
                current = current[part]
            current[parts[-1]] = value
        else:
            data[field] = value
    
    def calculate_field_value(self, data, field, expression):
        """计算字段值"""
        # 简单的表达式计算
        # 实际应用中可能需要使用更复杂的表达式引擎
        try:
            # 替换表达式中的变量
            for key, val in data.items():
                expression = expression.replace(f"${key}", str(val))
            
            # 计算表达式
            result = eval(expression)
            self.set_field_value(data, field, result)
        except Exception as e:
            print(f"计算表达式失败: {e}")
    
    def run_tests(self):
        """运行所有测试用例"""
        results = {}
        total_tests = 0
        passed_tests = 0
        total_execution_time = 0
        
        for test_name, test_case in self.test_cases.items():
            rule_name = test_case.get("rule")
            test_data = test_case.get("data")
            expected = test_case.get("expected")
            
            print(f"\n测试用例: {test_name}")
            print(f"测试规则: {rule_name}")
            
            result = self.test_rule(rule_name, test_data)
            results[test_name] = result
            total_tests += 1
            total_execution_time += result.get("execution_time", 0)
            
            if result["status"] == "pass":
                # 检查结果是否符合预期
                if expected:
                    actual_result = result.get("result", {})
                    if self.compare_results(actual_result, expected):
                        print("✅ 测试通过")
                        passed_tests += 1
                    else:
                        print("❌ 测试失败: 结果与预期不符")
                        print(f"预期: {expected}")
                        print(f"实际: {actual_result}")
                else:
                    print("✅ 测试通过")
                    passed_tests += 1
            else:
                print(f"❌ 测试失败: {result['message']}")
        
        # 生成测试报告
        self.generate_report(results, total_tests, passed_tests, total_execution_time)
    
    def compare_results(self, actual, expected):
        """比较实际结果与预期结果"""
        for key, expected_value in expected.items():
            if key not in actual:
                return False
            actual_value = actual[key]
            if isinstance(expected_value, dict):
                if not isinstance(actual_value, dict) or not self.compare_results(actual_value, expected_value):
                    return False
            elif actual_value != expected_value:
                return False
        return True
    
    def generate_report(self, results, total_tests, passed_tests, total_execution_time):
        """生成测试报告"""
        print("\n" + "=" * 80)
        print("规则测试报告")
        print("=" * 80)
        print(f"总测试用例: {total_tests}")
        print(f"通过测试: {passed_tests}")
        print(f"失败测试: {total_tests - passed_tests}")
        print(f"测试通过率: {passed_tests / total_tests * 100:.2f}%")
        print(f"总执行时间: {total_execution_time:.4f} 秒")
        print(f"平均执行时间: {total_execution_time / total_tests:.4f} 秒")
        print("=" * 80)
        
        # 保存测试结果
        with open("test_results.json", "w", encoding="utf-8") as f:
            json.dump(results, f, indent=2, ensure_ascii=False)
        print("测试结果已保存到 test_results.json")

def main():
    if len(sys.argv) != 3:
        print("Usage: python rule-tester.py <rules_file> <test_cases_file>")
        sys.exit(1)
    
    rules_file = sys.argv[1]
    test_cases_file = sys.argv[2]
    
    tester = RuleTester()
    tester.load_rules(rules_file)
    tester.load_test_cases(test_cases_file)
    tester.run_tests()

if __name__ == "__main__":
    main()