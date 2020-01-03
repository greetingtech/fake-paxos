# TryPaxos

run example

Proposer id:0 info ->  sent prepare  

Proposer id:1 info ->  sent prepare  

Proposer id:2 info ->  sent prepare  

Proposer id:0 info ->  prepared success  

Proposer id:0 info ->  sent propose  

Proposer id:1 info ->  prepared success  

Proposer id:1 info ->  sent propose  

Proposer id:2 info ->  prepared success  

Proposer id:2 info ->  sent propose  

Acceptor id:3 info ->  accept the value:115  

Acceptor id:4 info ->  accept the value:115  

Proposer id:1 info ->  accepted failed  

Proposer id:1 info ->  sent prepare  

Proposer id:0 info ->  accepted failed  

Proposer id:0 info ->  sent prepare  

Acceptor id:5 info ->  accept the value:115  

Proposer id:2 info ->  has been accepted finalValue is 115  

Proposer id:1 info ->  prepared success  

Proposer id:1 info ->  sent propose  

Proposer id:0 info ->  prepared failed  

Proposer id:0 info ->  sent prepare  

Proposer id:0 info ->  prepared success  

Proposer id:0 info ->  sent propose  

Proposer id:1 info ->  has been accepted finalValue is 115  

Proposer id:0 info ->  has been accepted finalValue is 115
