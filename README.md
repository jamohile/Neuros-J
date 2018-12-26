# Neuros-J
Neuros-J is a Java implemented core library for Neuros. Neuros, still in its very early stages, is a program with the goal of solving computations by means of simulating "neurons" akin to a brain.


## Notable Systems / Concepts

### Probabilistic Determination

Neural network are deterministic. Neuros is built on the core assumption that accurately simulating a dynamic "brain" relies on both
probability and determination. It seeks to develop a network of neurons, whose growth is influenced by deterministic properties, but mediated through randomness.


### Charge Traversal
Charges are used to moderate growth and development of a Neuros network. Charges traverse a network, and can exhibit complex behaviour. This can be simplified into three key properties:

#### Payload

A charge's history, logging previous locations and the path taken. Used to expand the network. Used in close conjunction with lifespan, which moderates how a charge weakens and dies.

#### Excitability

How likely a charge is to split. Charges, once excited, can simultaneously traverse alongside other charges. The system takes advantage of multithreading, and protects against race conditions.

#### Stability

If low, a charge is likely to decay prematurely.

### State Logging

Neuros used proprietary NMS (Neuros Machine State) and NHRS (Neuros Human Readable State) to store and resume network state.
These formats were developed to allow saving, analysis, and (with the condensed NMS file) importing into sister programs.

