package util;

import java.util.ArrayList;
import java.util.List;

public class CalculationResponse extends Message {
    private List<RulesCorrelation> correlations;

    public CalculationResponse(List<RulesCorrelation> correlations) {
        super("sendCalculationResponse");
        this.correlations = correlations;
    }

    public List<RulesCorrelation> getCorrelations() {
        return correlations;
    }
}
