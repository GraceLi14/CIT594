Name: Grace Li
Penn ID: 78851131

1. How long did it take you to complete this assignment (in hours)?

    This assignment took me approximately 30 hours to complete. 

2. What parts of this assignment did you find most difficult? 

    The most difficult part is definitely implementing remove() in MyTree. 
    Breaking out the potential cases in which to delete a node and then further breaking that out into various cases (root vs. non-root nodes and, for two children, direct vs. non-direct successor) took a while to think about implementing.
    
    Writing insert() and toString() in MyTree took a while to think about too. 
    Also, thinking about how to implement add() in MyHashTable so that the method differentiates between adding a new node or if there's an existing node (ii.e. adding size to MyTree) forced me to think outside of the box a little since we cannot traverse twice.

3. Did you use any outside resources to complete this assignment (worked with a friend, stack overflow, ChatGPT, etc.)? If so, what resources and in what way did you use them?

    Yes, I watched a lot of YouTube videos like mycodeschool to gain understanding of concepts like BST deletion. I also used ChatGPT to help me debug issues like making sure hashCode() converts all items into positive numbers.

4. Please write one or two sentences about something that you learned while completing this assignment.

   I learned how important pointer rewiring order is when deleting nodes in a BST, especially in the two-children case. 
   I also gained a much deeper understanding of how hash tables use bucket structures and how size tracking must be carefully managed to avoid double-counting or missed updates.
