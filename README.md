# OpenBlocks Trophies

Brings back trophies from the mod OpenBlocks. They have a .1% chance of dropping from a mob, and can be placed in world.

---

### Adding your own trophies:

Every trophy is registered through a json. A trophy json looks something like this:

```
{
  "entity": "minecraft:cow",
  "drop_chance": 0.001,
  "offset": 0.0,
  "scale": 1.0,
  "behavior": {
    "type": "obtrophies:item",
    "cooldown": 20000,
    "item": "minecraft:leather"
  }
}
```

Each field should be self-explanatory, but if not here's an explanation for each:

- `entity (string)`: the entity you're making a trophy for.
- `drop_chance (double)`: the chance the entity defined will drop its trophy. This number should be anywhere from 0.0 to
  1.0. Works as a percent. (example: 0.001, the default, is a 0.1% chance. 0.4 would be a 40% chance.)
- `scale (double)`: the scale of the entity on the trophy. I recommended keeping it at 2.0 or below depending on the
  mob's size if you want it to fit, but you can do what you want.
- `offset (double)`: the size of the vertical offset the trophy has from the base.As expected, a negative offset will
  make the entity move down, and a positive offset will make it go up.
- `behavior`: defines what the entity does when right-clicked. There are 10 pre-made behaviors, and each comes with its
  own parameters you need to define. More details below.

---
Jsons should be put in `data/modid/trophies`. All trophies will automatically be registered, added to the creative tab,
and drop from their respective mobs.

---

### Behaviors

Datapacks can define what happens when a trophy is right-clicked. 10 behaviors are built into the mod itself, but any
other mods can easily add more if they want to. The behaviors are as follows:

- `obtrophies:item`: gives the player an item if the cooldown is at 0. There are 3 parameters to fill out here, although
  one is optional.
	- `item (string)`: the item that the trophy should give.
	- `cooldown (int)`: the time, in ticks, it takes until the trophy can give you another item.
	- `sound (string)`: (optional) the sound that should play when an item is given to the player.
- `obtrophies:mob_effect`: gives the player a mob effect. There are 3 parameters to fill out here.
	- `effect (string)`: the effect that should be given to the player.
	- `time (int)`: the time, in ticks, that the effect should last.
	- `amplifier (int)`: the strength of the effect. Works just like how vanilla calculates its amplifiers.
- `obtrophies:place_block`: places a block either on top or around the trophy. There are 2 parameters to fill out here.
	- `block (string)`: the block to place.
	- `place_around_trophy (boolean)`: defines whether the blocks should place around the trophy or on top of it.
- `obtrophies:explosion`: creates an explosion where the trophy is. There are 2 parameters to fill out here.
	- `power (int)`: how strong the explosion should be. Weaker explosions will be visual, but stonger ones will make
	  you take damage.
	- `destructive (boolean)` defines whether the explosion should break blocks or not.
- `obtrophies:arrow`: shoots a set amount of arrows upwards. There are 2 parameters to fill out here, although one is
  optional.
	- `amount (int)`: how many arrows the trophy should shoot.
	- `effect (string)`: (optional) the effect that the arrow should be tipped with.
- `obtrophies:guardian_curse`: creates the ghost guardian that floats across your screen when an elder guardian curses
  you with mining fatigue. This doesnt actually give mining fatigue though.
- `obtrophies:ender_pearl`: shoots an ender pearl up into the air. This ender pearl will teleport you.
- `obtrophies:llama_spit`: shoots some llama spit up into the air.
- `obtrophies:totem_of_undying`: plays the totem animation on your screen.

---
If you're a mod maker, and you want to add your own behaviors, simply follow the instructions
in `CustomBehaviorRegistry`.

## Future TODO list:

- add support for Entity NBT, allowing for mob variants (brown mooshrooms, axolotl types, cat types, etc)
- add a builin datapack that creates trophies for a bunch of mod entities
