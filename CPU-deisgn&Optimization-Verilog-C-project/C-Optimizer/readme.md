Understanding makeFile in c++. 

The Makefile works based on the concept of timestamping. It compares the timestamps of the target and its dependencies to determine if a target needs to be rebuilt. If the target does not exist or its dependencies have been modified since the last build, the associated commands are executed to rebuild the target.

- Rules: structure
    - target: dependencies
    - command
- 
