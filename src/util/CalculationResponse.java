package util;

import java.util.ArrayList;
import java.util.List;

public class CalculationResponse extends Message {
    private List<RulesCorrelation> correlations;

    private long masterPart1Time;
    private long masterPart2Time;
    private long slavePart1Time;
    private long slavePart2Time;

    public CalculationResponse(List<RulesCorrelation> correlations, long masterPart1Time, long masterPart2Time, long slavePart1Time, long slavePart2Time) {
        super("sendCalculationResponse");
        this.correlations = correlations;

        this.masterPart1Time = masterPart1Time;
        this.masterPart2Time = masterPart2Time;
        this.slavePart1Time = slavePart1Time;
        this.slavePart2Time = slavePart2Time;
    }

    public List<RulesCorrelation> getCorrelations() {
        return correlations;
    }

    public long getMasterPart1Time() {
        return masterPart1Time;
    }

    public long getMasterPart2Time() {
        return masterPart2Time;
    }

    public long getSlavePart1Time() {
        return slavePart1Time;
    }

    public long getSlavePart2Time() {
        return slavePart2Time;
    }
}
