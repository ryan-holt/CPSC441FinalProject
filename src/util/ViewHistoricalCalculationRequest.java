package util;

public class ViewHistoricalCalculationRequest extends Message {
    private String calculationFilename;
    public ViewHistoricalCalculationRequest(String calculationFilename) {
        super("viewHistoricalCalculation");
        this.calculationFilename = calculationFilename;
    }

    public String getCalculationFilename() {
        return calculationFilename;
    }
}
