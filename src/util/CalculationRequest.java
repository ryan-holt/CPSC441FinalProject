package util;

public class CalculationRequest extends Message {
	private int keywordGroupSize;

	public CalculationRequest(int keywordGroupSize) {
		super("calculateCorrelation");
		this.keywordGroupSize = keywordGroupSize;
	}

	public int getKeywordGroupSize() {
		return keywordGroupSize;
	}
}
