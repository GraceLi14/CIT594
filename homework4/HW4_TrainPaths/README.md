Name: Grace Li
Penn ID: 78851131

Part 4 Analysis
1. What is the purpose of RELAX-A-STAR described in the pseudo code for A* Search?
The purpose of RELAX-A-STAR is to check whether going from the start node to a neighboring node through the current node provides a shorter path than the one currently known. 
If so, it updates the neighbor’s distance, recomputes its total estimated cost (distance plus heuristic to the target), and records the current node as its predecessor. 
This process ensures that the algorithm continuously improves paths and moves toward the most efficient route to the target.

2. Why does A* require a priority queue (or min-heap)? What would happen if it used a regular queue instead?
A* requires a priority queue so that it always processes the node with the smallest total estimated cost (distance + heuristic) next. 
This ensures the algorithm explores the most promising paths first and efficiently finds the shortest path. 
If a regular queue (FIFO) were used instead, nodes would be processed in the order they are discovered rather than by cost, causing the algorithm to potentially return inefficient or incorrect paths in a weighted graph.

3. When translating your solution from Java to Python, how did your chosen data structures change? Give a specific example of a data structure you used in Java and its equivalent in Python.
When translating my solution from Java to Python, the overall logic stayed the same, but the data structures became more built-in. 
For example, I used a HashMap<String, Double> in Java to store distances and used a Python dictionary for the same purpose, 
and I used a HashSet<String> in Java which became a Python set. 
I also used Java’s PriorityQueue<String> for the min-heap, while in Python I used heapq with tuples to maintain ordering by estimated cost and then alphabetically by station ID.

Additional Questions
1. How long did it take you to complete this assignment (in hours)? 
It took me approximately 25 hours to complete.

2. What parts of this assignment did you find most difficult? 
I found translating the initial design of the program difficult to conceptualize initially: thinking through the different methods, static classes, fields etc. 
I also found the pseudocode translation into real code difficult especially as I had to integrate A* into the existing program and think through more edge cases than what the pseudocode provided.

3. Did you use any outside resources to complete this assignment (worked with a friend, stack overflow, ChatGPT, etc.)? If so, what resources and in what way did you use them?
I used Stack Overflow to understand the Haversine distance formula. 
I used ChatGPT to better understand how Java’s priority queue behaves, specifically that it does not automatically reorder when values in the estimate map are updated, requiring nodes to be reinserted into the heap. 
I also used W3Schools as a reference for Python syntax, including file reading, class definitions, and general language structure.

4. Please write one or two sentences about something that you learned while completing this assignment.
I learned not to take pseudocode at face value and to carefully consider edge cases and how it translates into actual code, 
as well as how different components fit together: for example, some A* parameters were already encapsulated as class fields and didn’t need to be passed explicitly. 
I also learned that while Java requires explicitly defining heap ordering, Python’s heapq handles ordering naturally through tuple structure.