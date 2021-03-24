# cognigolems
Craftable and programmable golem companions. Programming should be simple and powerful, using a **logic programming** paradigm.
They can be used to automate repeatable tasks around your base or follow and help out while you explore the world.

## concept
Golems are shaped similar to existing creatures found around Minecraft, but contain a reprogrammable core.
The golem is programmed by describing the tasks the golem can complete.

The most common and easily crafted cognigolem is a miniature version of the iron golem, which has the unique instinct among cognigolems to be able to use tools.
Other forms are possible, such as ornithopters or shulkers, which each come with unique properties and instincts to utilize.

## examples
Below are examples of what cognigolems can do, and how they are programmed.
It becomes quickly apparent how different **logic programming** is from **procedural programming**.
With **logic programming** it is often enough to describe the interactions you desire, and the computer will look for opportunities to make it work. 

### storage system and sorting
In this example you want to program a sorting golem that takes items from a dump chest and sorts it into your storage room.

You would first describe the items you want the golem to take: items in a chest with a sign labeled "dump".
Then you would describe the chest you want the golem to put the items into: a chest with a sign labeled "storage", that already contains the same type of item taken from the dump chest.

### mining buddy
In this example you want a golem that follows you around and mines only iron and gold ores for you.

The golem would first find the coordinates of nearby blocks that are of the type iron ore or gold ore.
Then it would use the mini-golem's "use tool" action with a carried pickaxe at any coordinates it finds.

Separately, it would find nearby players, specifically with your username, and move closer to them.

