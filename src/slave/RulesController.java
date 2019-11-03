package slave;

import util.AssociationRuleRequest;
import util.KeywordGroup;
import util.SurveyEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RulesController {
	/**
	 * Calculate the support and confidence of each keyword group for each survey entry
	 * @param request
	 */
	public Map<KeywordGroup, Double> calculateAssociationRules(AssociationRuleRequest request) {
		// Sort the groups by keyword
		List<KeywordGroup> groups = request.getKeywordGroups()
				.stream().map(group -> { group.sort(); return group; })
				.collect(Collectors.toList());
		List<SurveyEntry> entries = request.getSurveyEntries();

		Map<String, Integer> keywordCounts = new HashMap<>();
		Map<KeywordGroup, Integer> groupCounts = new HashMap<>();

		// Add the individual keywordCounts and the groups for minor improved performance
		for (KeywordGroup group : groups) {
			groupCounts.put(group, 0);

			for (String keyword : group.getKeywords()) {
				keywordCounts.put(keyword, 0);
			}
		}

		ArrayList<String> selections;
		for (SurveyEntry entry : entries) {
			selections = entry.getSelections();

			for (KeywordGroup group : groups) {
				ArrayList<String> keywords = group.getKeywords();
				if (selections.containsAll(keywords)) {
					incrementMapCount(groupCounts, group);
				}
			}

			for (Map.Entry<String, Integer> keywordEntry : keywordCounts.entrySet()) {
				if (selections.contains(keywordEntry.getKey())) {
					keywordEntry.setValue(keywordEntry.getValue() + 1); // RIP incrementMapCount, screw reusability
				}
			}
		}

		double entriesCount = (double) entries.size();
		Map<KeywordGroup, Double> scores = new HashMap<>();

		for (KeywordGroup group : groups) {
			ArrayList<String> keywords = group.getKeywords();
			double groupCount = (double) groupCounts.get(group);

			double supportScore = groupCount / entriesCount;

			for (String keyword : keywords) {
				double keywordCount = (double) keywordCounts.get(keyword);

				// Get the confidence score
				double confidenceScore = groupCount / keywordCount;
				double score = calculateAssociationScore(supportScore, confidenceScore);
				scores.put(getOrderedKeywordGroup(group, keyword), score);
			}
		}

		return scores;
	}

	private <K> void incrementMapCount(Map<K, Integer> map, K key) {
		map.put(key, map.get(key) + 1);
	}

	/**
	 * Get a copy of the keyword group where the first entry of the keywordGroup.keywords is the firstKeyword
	 * @param group
	 * @param firstKeyword
	 * @return
	 */
	private KeywordGroup getOrderedKeywordGroup(KeywordGroup group, String firstKeyword) {
		ArrayList<String> orderedKeywords = (ArrayList<String>) group.getKeywords().clone();
		orderedKeywords.remove(firstKeyword);
		orderedKeywords.add(0, firstKeyword);
		return new KeywordGroup(orderedKeywords);
	}

	private double calculateAssociationScore(double supportScore, double confidenceScore) {
		double supportWeight = 0.3, confidenceWeight = 0.7;
		return supportWeight * supportScore + confidenceWeight * confidenceScore;
	}
}
