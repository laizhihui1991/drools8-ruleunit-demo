package org.example;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

import java.util.HashSet;
import java.util.Set;

public class MeasurementUnit implements RuleUnitData {
    private final DataStore<Measurement> measurements;
    private final Set<String> controlSet = new HashSet<>();

    public MeasurementUnit() {
        this.measurements = DataSource.createStore();
    }

    public DataStore<Measurement> getMeasurements() {
        return measurements;
    }

    public Set<String> getControlSet() {
        return controlSet;
    }
}