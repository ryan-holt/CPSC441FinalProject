package util;

public class ListHistoricalCalculationsResponse extends Message {
    private String listOfHistoricalCalculations;

    public ListHistoricalCalculationsResponse(String listOfHistoricalCalculations) {
        super("sendHistoricalCalculationResponse");
        this.listOfHistoricalCalculations = listOfHistoricalCalculations;
    }

    public String getListOfHistoricalCalculations() {
        return listOfHistoricalCalculations;
    }
}
