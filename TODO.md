
## Task list

Ordered roughly in priority:

- Add more real world payloads
- Symbol pool for class names
- General cleanup of weird/disgusting code areas
- Address all FIXME/TODOs
- Write Contributing guide

- Make website tool prettier
- Throttle website tool/prepare for HN
- Docs + writeup

- Unit tests for individual methods (once codebase a bit less volatile)

## Rough architecture


1. Enqueue each element in a Queue using BFS, recording the level of each element.
Each parent node should have direct access to its children, to allow it to retrieve their calculated type.

2. level order traversal from the bottom of the tree, by polling elements in the Queue.
3. For each level:
    (A) Define the type for each node
    (B) Detect any duplicate type definitions for this level
    (C) Determine the nullability for each value at this level
    (D) Push any new type definitions onto a Stack, and a name pool
    (E) Add any new type definitions into a Collection for quick lookup
4. Pop the stack until it is empty and write each type to the source file