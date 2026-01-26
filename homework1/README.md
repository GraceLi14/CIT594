## Please enter your personal info here:
Name: Grace Li

PennKey (e.g., taliem): li63

Statement of work: Stack Overflow, ChatGPT to explain data structure concepts

# Part 1:
## Are Alicia and Lloyd both wrong, or perhaps both right? Is only one of them correct? Why?
It depends because List is an interface and depending on how it is implemented, either Alicia or Lloyd can be correct.

For example, if an ArrayList is implemented at runtime, Snippet A will run in time O(1) because get(i) is constant-time indexing. Snippet B will run in time O(N) because the remove(0) will shift all remaining elements of the array list left.

On the other hand, if a LinkedList is implemented at runtime, then Snippet A will run in time O(N) given the get(middle) function will need to traverse ~N/2 nodes which is O(N). Snippet B will run in time O(1) because removing first and last elements is constant time.

Big O cannot be determined from the interface type alone, we would need to see how it is implemented.

# Part 2:
## What are the Big O and Big Ω times for snippets C and D?
For snippet C, the loop breaks once the target is found. In this case, Big Ω is Ω(1) given in the best case scenario, target is found in the first cell and you break out of both the inner and outer loop. 
Big O is O(nm) given in the worst case scenario, the target is in the last cell and the algorithm will look at each cell in the n*m grid.

For snippet D, the loop will continue until the very end of the grid. In this case, Big Ω is Ω(nm) given even in the best case scenario, even though the target is found in the first cell, the loop doesn't break so the algorithm will need to examine every cell of the n*m grid. 
Big O is also O(nm) given in the worst case scenario, the target is in the last cell and the algorithm will still need to look through all cells of the n*m grid.

## When measuring actual runtime, does one of the snippets run faster than the other? In what situations? Why do you think this is the case?
Snippet C runs faster when the target occurs earlier in the grid because it breaks out of both loops immediately and would examine far fewer than n*m cells.
Snippet D does not get faster when the target occurs earlier because it always scans the entire n*m grid.
When the target is towards the end or not in the grid, both snippets examine roughly the full grid so the runtime becomes similar.
The reason is one of control flow i.e. snippet C allows for an early exit but snippet D still does all iterations.

Out of all the combinations, the grid from getGridOne() and findFirstInstanceOne() i.e. Snippet C definitely runs the fastest. This is because the target is the first cell of the grid from getGridOne() and snippet C's algorithm found the target and was able to break out of all loops immediately. 

getGridOne() + findFirstInstanceTwo() i.e. Snippet D,  getGridTwo() + findFirstInstanceOne() i.e. Snippet C, and getGridTwo() + findFirstInstanceTwo() i.e. Snippet D were all slower because the target is at the end and/or findFirstInstanceTwo() i.e. snippet D was used to find the target in which cases every cell had to be examined.

## What else do you notice about the reported runtime? Is it 100% consistent every time you run it?
The reported runtime is not consistent each time I run it. This is because despite using the same grid and method, the time can vary due to the computer doing other tasks in the background.
It is important to look at the overall pattern in the results given exact runtime is not exactly the same every time.

# Part 3:
## Before you make any changes, explain whether you think a LinkedList or an ArrayList makes more sense in this instance. Which do you think will be faster? Why?
LinkedList makes more sense because Suho will be constantly processing tickets in a FIFO manner by repeatedly removing the next ticket from the front of the queue.
Removing the first element in a LinkedList has time complexity of O(1) whereas in an ArrayList, removing at index 0 will have the time complexity of O(n) because all remaining elements will have to shift over to the left. This difference is especially pronounced when the queue is long.

## When measuring actual runtime, is the LinkedList version Suho wrote, or your ArrayList version faster? Does this change when the list size is small versus when it is very large?
The LinkedList Suho wrote was faster. For small lists, the difference was minor and the runtime looked similar. For larger lists, however, LinkedList is more obviously faster.

## If you ignore queue creation times, does that affect which ticket processor version is faster?
The LinkedList is still faster than the ArrayList. If the queue creation time is ignored, the runtime is shorter for both ticket processor versions but the relative difference between the list types remains.
The main difference in runtime is driven by the processing time, specifically the time it takes to remove an element from the beginning of the list. This is more expensive for an ArrayList vs a LinkedList.

## Write a paragraph or two in the style of a technical report (think about – how would I write this professionally if I needed to explain my findings to my manager?).
Your report should answer the following questions:
* What did you learn from this experience?
* Which implementation do you suggest should be used? Are there certain situations that might call for the other approach?
* How does the theoretical time complexity compare with your findings?

I learned that the choice of data structure has significant impact on performance of a ticket processing solution where tickets are constantly removed from a queue as they are processed. One must consider how this processing is implemented. In particular, processes can have very different costs depending on how the underlying data structure is implemented. For example, where the elements are removed from and how often certain operations are repeated can dominate the runtime far more than one-time setup costs like queue creation.

I recommend a LinkedList when the tickets need to be processed in a FIFO order. In this case, each removal takes O(1) time making it an efficient data structure to use for large ticket volumes. To implement a LIFO ticket processing order, an ArrayList can be equally efficient since each removal will take O(1) time. For smaller queues, the difference between the two structures is negligent but for larger queues, LinkedList is far more efficient than ArrayList to process tickets in a FIFO manner. My findings align with the theoretical time complexity analysis. Removing elements from the beginning of an ArrayList repeatedly causes all elements to shift to the right each time. In a while loop, that costs O(n^2) processing time. Conversely, removing from the front with LinkedLists takes O(n) time to process in a while loop as expected. Additionally, testing the two different snippets showed that non-algorithmic aspects like printing in a loop can overtake data structure runtime performance and must be controlled when benchmarking. 

# Part 4
## What are the Big O and Big Ω times for Javier's algorithm? What are the Big O and Big Ω for space use?

The Big O time is O(nlog(n)) and Big Ω time is also Ω(nlog(n)). This is because no matter what input order, there will be n single-element arrays that are merged at every level. There are O(log(n)) levels of merging and then each element will be processed at every level for the sorting phase O(n) The runtime does not depend on if the input is already sorted or not so the best and worst case scenarios are the same.

The Big O space is O(n) and Big Ω space is also Ω(n). This is because in the best and worst case scenario, a LinkedList of single element arrays will be created whose total number of elements is proportional to n. Additional arrays are created during merging but at any snapshot of time the total amount of extra memory used grows linearly with the size of the input. 

## Write a paragraph or two in the style of a technical report (think about – how would I write this professionally if I needed to explain my findings to my manager?). 
Your report should answer the following questions:
* Which of the two algorithms (yours versus Javier's) is more efficient in time and space (in terms of Big O)
    * What about in actual runtime?
* Which implementation do you suggest should be used? Are there certain situations that might call for the other approach?

