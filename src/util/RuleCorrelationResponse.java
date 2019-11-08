package util;

import java.util.List;

public class RuleCorrelationResponse extends HostAddressMessage {
	private List<RulesCorrelation> correlations;

	public RuleCorrelationResponse(List<RulesCorrelation> correlations) {
		super("ruleCorrelationResponse");
		this.correlations = correlations;
	}

	public List<RulesCorrelation> getCorrelations() {
		return correlations;
	}
}
