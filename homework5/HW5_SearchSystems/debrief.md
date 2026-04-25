1. What was causing the issue? What part of the code was causing the problem? If relevant, include a method signature and the name of the file in which the problematic code was contained.
The issue was in AutocompleteService, specifically in the buildPrefixIndex method. 
This method constructs an prefix index by generating all possible prefixes for every word and mapping them to matching words. 
Additionally, the collectMatchingWords method repeatedly scans the entire dataset to recompute matches for each prefix, leading to significant inefficiency.

2. Why was this part of the code causing this issue? Include a plain english explanation of what was happening.
The problem arises because, for large datasets like amazon_data.txt, the number of prefixes grows rapidly with both the number of words and their lengths. 
This leads to a very large hash map and high memory usage. 
Furthermore, collectMatchingWords repeatedly scans the full dataset for each prefix, resulting in redundant computation and poor performance.

3. Why does this problem only appear for some datasets?
This issue becomes noticeable with large datasets that contain many entries and long strings. 
In smaller datasets, the number of prefixes and repeated scans is manageable. 
In larger datasets, both time and space complexity grow significantly, causing performance and memory issues.

4. How did you solve the issue?
I replaced the prefix index approach with a compressed trie. 
Instead of scanning the entire dataset for each query, the trie allows autocomplete to follow only the relevant paths that match the prefix. 
This significantly reduces both time and space usage.

5. What other solutions did you use to try to solve the underlying issue? Why were they not effective?
I initially tried optimizing the existing prefix index by adding words to each prefix incrementally during buildPrefixIndex.
This eliminated the need for collectMatchingWords. However, this still required storing a large number of prefixes and associated word lists, which led to excessive memory usage. 
Due to these limitations, I ultimately switched to a trie-based solution, which is more efficient for prefix-based queries.

6. What was the time complexity of the original code that was causing the issue? Respond in terms of n (number of items) and m (average item length in characters).
The original code has a time complexity of approximately O(n²m²), where n is the number of items and m is the average length of each item. 
This is because for every word (n), the code generates all possible prefixes (m), and for each prefix, it calls collectMatchingWords.
The collectMatchingWords method scans the entire dataset again (n). Within that scan, each startsWith comparison can take up to O(m) time. 
As a result, the total work becomes n × m × n × m, leading to O(n²m²) time complexity.

7. What is the time complexity of your fix? Respond in terms of n (number of words) and m (average item length in characters).
The time complexity of building the compressed trie is approximately O(nm), where n is the number of words and m is the average word length. 
Each word is inserted by comparing characters along trie paths, and in the worst case, the insertion work is proportional to the length of the word. 
Autocomplete lookup is O(m + k), where m is the prefix length and k is the number of characters/words visited during DFS to collect matching completions.
Overall, nm dominates so the overall time complexity is O(nm). This is much better than the original implementation because it avoids repeatedly scanning the entire dataset for every prefix.