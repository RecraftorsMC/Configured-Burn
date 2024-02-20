# Configured Burn Time
a.k.a. Configured Burn

Allows to make item burn time data-driven.
Change your items' fueling time with a single datapack reload!

Implements a new recipe type: `configured_burn:burning_time`

Structure:

```json5
{
  "type": "configured_burn:burning_time",
  "item": "<item id>",
  "tag": "<tag id>", // Does not take a #
  // Either the item or tag is required, but one can be missing.
  "priority": 0,
  // must be positive or null. Indicates priority over the specified item/tag. Optional, 0 by default.
  "time": 200, // the new fuel time, in server ticks.
  "compat": { // optional element, implements some other mods compat
    "createOverheatTime": 100
    // indicates how long the item would make a Create blaze burner overheat. By default, 0. 
  },
  "conditions": [
    // optional, a collection of stack predicates to be validated
    // in order to apply the specified fuel change at a specific occasion.
    // Warning: due to implementation difficulties, it most often comes irrelevant.
  ]
}
```

## Reference

Here is a small reference sheet of some vanilla item fuel times,
so that you may have an idea of what a value evaluates for:

| item                         | value (ticks) | time (seconds) | number of items |
|------------------------------|---------------|----------------|-----------------|
| `#minecraft:boats`           | 1200          | 60             | 6               |
| `#minecraft:burnable_planks` | 300           | 15             | 1.5             |
| `minecraft:coal_block`       | 16000         | 800            | 80              |
| `minecraft:coal`             | 1600          | 80             | 8               |
| `minecraft:lava_bucket`      | 20000         | 1000           | 100             |
| `#minecraft:burnable_logs`   | 300           | 15             | 1.5             |

## Implemented mod compatibilities

### Create

The recipe supports a compatibility field which allows to define how long should an item make a Create blaze burner overheat.