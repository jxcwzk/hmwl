#!/bin/bash

echo "开始四角色物流流程测试..."

PHASE=$1

if [ "$PHASE" == "1" ]; then
  pytest tests/phase1_normal_flow --html=reports/phase1-report.html
elif [ "$PHASE" == "2" ]; then
  pytest tests/phase2_exception --html=reports/phase2-report.html
elif [ "$PHASE" == "3" ]; then
  pytest tests/phase3_parallel --html=reports/phase3-report.html
elif [ "$PHASE" == "4" ]; then
  cd tests/phase4_ui && npm install && node puppeteer-runner.js
elif [ "$PHASE" == "5" ]; then
  pytest tests/phase5_isolation --html=reports/phase5-report.html
else
  pytest tests/ --html=reports/test-report.html
fi

echo "测试完成！"
