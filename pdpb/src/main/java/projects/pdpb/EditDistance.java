package projects.pdpb;

public class EditDistance {
	
	/**
	 * Returns the edit distance between two strings
	 * @param s substring of problem name
	 * @param t search string
	 * @return integer value representing the edit distance
	 */
	public static int editDistance(String s, String t) {
//		if (Math.abs(s.length() - t.length()) > 1) return 0x3f3f3f3f;
		s = s.toLowerCase();
		t = t.toLowerCase();
		int[][] dp = new int[s.length() + 1][t.length() + 1];
		for (int i = 0; i <= s.length(); i++) {
			for (int j = 0; j <= t.length(); j++) {
				// if one of the current prefixes is empty, then the edit distance would simply be the length of the other prefix,
				// as the only required operation would be deletion.
				// the pair {0, 0} is when both prefixes are empty
				if (i == 0 || j == 0) {
					dp[i][j] = Math.max(i, j);
					continue;
				}
				// otherwise, choose the minimum operations to get current prefix of s by inserting, deleting, or changing (if needed) at 
				// the current position in t.
				dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]) + 1, 
						dp[i - 1][j - 1] + (s.charAt(i - 1) == t.charAt(j - 1) ? 0 : 1));
				// if swapping two adjacent characters is valid, checks if doing so reduces the total number of operations
				if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2) && s.charAt(i - 2) == t.charAt(j - 1)) 
					dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1); 
			}
		}
//		for (int i = 0; i <= s.length(); i++) {
//			for (int j = 0; j <= t.length(); j++) System.out.printf("%d ", dp[i][j]);
//			System.out.println();
//		}
		return dp[s.length()][t.length()];
	}
}
