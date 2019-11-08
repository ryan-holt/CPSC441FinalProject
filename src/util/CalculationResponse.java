package util;

import java.util.ArrayList;

public class CalculationResponse extends Message {
    private ArrayList<RulesCorrelation> correlations;

    public CalculationResponse(ArrayList<RulesCorrelation> correlations) {
        super("sendCalculationResponse");
        this.correlations = correlations;
    }

    public ArrayList<RulesCorrelation> getCorrelations() {
        return correlations;
    }
}
