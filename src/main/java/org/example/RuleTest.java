package org.example;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class RuleTest {

    public static void main(String[] args) {
        MeasurementUnit measurementUnit = new MeasurementUnit();
        RuleUnitInstance<MeasurementUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(measurementUnit);

        try {
            // 添加测试数据
            measurementUnit.getMeasurements().add(new Measurement("color", "red"));
            measurementUnit.getMeasurements().add(new Measurement("color", "green"));
            measurementUnit.getMeasurements().add(new Measurement("color", "blue"));
            measurementUnit.getMeasurements().add(new Measurement("color", "yellow"));
            measurementUnit.getMeasurements().add(new Measurement("color", "cyan"));

            // 执行查询
            System.out.println("=== 开始执行查询 ===");
            QueryResults results = instance.executeQuery("FindColor");

            // 遍历结果
            System.out.println("匹配到的颜色列表：");
            int count = 0;
            for (QueryResultsRow row : results) {
                Measurement m = (Measurement) row.get("$m");
                System.out.println("  " + m.getVal());
                count++;
            }
            System.out.println("总计匹配到 " + count + " 条记录");

        } finally {
            instance.close();
        }
    }
}