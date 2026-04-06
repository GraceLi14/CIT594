Name: Grace Li
Penn ID: 78851131

Part 4 Analysis
1. What is the purpose of RELAX-A-STAR described in the pseudo code for A* Search? 
The purpose is to always find the most efficient path to the target.
2. Why does A* require a priority queue (or min-heap)? What would happen if it used a regular queue instead? 
If we used a regular queue, wouldn't use the minimum estimate or total cost
3. When translating your solution from Java to Python, how did your chosen data structures change? Give a specific example of a data structure you used in Java and its equivalent in Python.

Additional Questions
1. How long did it take you to complete this assignment (in hours)? 

2. What parts of this assignment did you find most difficult? 

3. Did you use any outside resources to complete this assignment (worked with a friend, stack overflow, ChatGPT, etc.)? If so, what resources and in what way did you use them? 
Stack Overflow for Haversine formula.
GPT to help me understand how Java min-heap updates i.e. doesn't resort heap when I update a node's estimate map so I need to reinsert node into heap.
w3schools remind me of how to read files in Python

4. Please write one or two sentences about something that you learned while completing this assignment.
Not take pseudocode for face-value. Thinking about the edge cases and also how to incorporate existing programming structure to fit the pseudocode
Figuring out how the different components fit into each other. For example, the pseudocode for A* algorithm has more parameters than what I needed to implement because I already had a lot of the parameters hidden as fields in my class.