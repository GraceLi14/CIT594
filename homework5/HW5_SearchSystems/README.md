Name: Grace Li
Penn ID: 78851131

1. How long did it take you to complete this assignment (in hours)? 
This assignment took me ~45 hours.

2. What parts of this assignment did you find most difficult?
The most difficult part was designing and implementing the compressed trie. 
It was challenging to determine the appropriate structure, such as using a character as the key for efficient child access. 
Another difficulty was deciding how to handle overlapping words: 
whether to split nodes dynamically when overlaps occur or to pre-process shared prefixes. 
I ultimately found that handling splits during insertion was more effective.

3. What parts of this assignment did you find most frustrating?
Building the trie was the most frustrating part due to the number of edge cases involved, 
especially during node splitting when handling partial overlaps between words. 
Another challenge was implementing the autocomplete logic.
Specifically, correctly identifying whether a prefix exists in the trie and then performing a DFS to construct valid completions. 
Separating these responsibilities into traversal and DFS logic required careful thought.

4. Did you use any outside resources to complete this assignment (worked with a friend, stack overflow, ChatGPT, etc.)? If so, what resources and in what way did you use them? 
I asked ChatGPT to help me understand the difference in implementation between a trie and compressed trie.

5. Please write one or two sentences about something that you learned while completing this assignment.
I learned the importance of understanding the overall flow of a codebase and how time and space complexity directly impact performance in real-world systems. 
I noticed parallels to BFS-style thinking which ensures that child nodes are correctly constructed and assigned before reconnecting them to parent nodes which helped me reason through node splitting and reconstruction in the compressed trie.