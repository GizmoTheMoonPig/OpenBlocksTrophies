# OpenBlocks Trophies

Brings back trophies from the mod OpenBlocks. They have a .1% chance of dropping from a mob, and can be placed in world.

---
## Adding your own trophies:

Every trophy is registered through a json. A trophy json looks something like this:

```
{
  "behavior": {
    "type": "obtrophies:explosion",
    "destructive": false,
    "power": 2.0
  },
  "drop_chance": 0.001,
  "entity": "minecraft:creeper",
  "offset": 0.0,
  "scale": 1.0,
  "variants": [
    {
      "powered": "false"
    },
    {
      "powered": "true"
    }
  ]
}
```

Each field should be self-explanatory, but if not here's an explanation for each:
- `entity (string)`: the entity you're making a trophy for.
- `drop_chance (double)`: the chance the entity defined will drop its trophy. This number should be anywhere from 0.0 to 1.0. Works as a percent. (example: 0.001, the default, is a 0.1% chance. 0.4 would be a 40% chance.)
- `scale (double)`: the scale of the entity on the trophy. I recommended keeping it at 2.0 or below depending on the mob's size if you want it to fit, but you can do what you want.
- `offset (double)`: the size of the vertical offset the trophy has from the base.As expected, a negative offset will make the entity move down, and a positive offset will make it go up.
- `behavior`: defines what the entity does when right-clicked. There are 12 pre-made behaviors, and each comes with its own parameters you need to define. More details below.
- `variants`: a list of different "variants" a mob has. There are 2 ways to define variants. More information on that below.
---
Jsons should be put in `data/modid/trophies`. All trophies will automatically be registered, added to the creative tab, and drop from their respective mobs.

---

## Behaviors
Datapacks can define what happens when a trophy is right-clicked. 10 behaviors are built into the mod itself, but any other mods can easily add more if they want to. The behaviors are as follows:
- `obtrophies:item`: gives the player an item if the cooldown is at 0. There are 3 parameters to fill out here, although 2 are optional.
  - `item (string)`: the namespace of an item that the trophy should give.
  - `cooldown (int)`: (optional) the time, in ticks, it takes until the trophy can give you another item.
  - `sound (string)`: (optional) the namespace of a sound that should play when an item is given to the player.
- `obtrophies:mob_effect`: gives the player a mob effect. There are 3 parameters to fill out here.
  - `effect (string)`: the effect that should be given to the player.
  - `time (int)`: the time, in ticks, that the effect should last.
  - `amplifier (int)`: the strength of the effect. Works just like how vanilla calculates its amplifiers.
- `obtrophies:place_block`: places a block either on top or around the trophy. There are 2 parameters to fill out here.
  - `block (string)`: the block to place.
  - `place_around_trophy (boolean)`: defines whether the blocks should place around the trophy or on top of it.
- `obtrophies:explosion`: creates an explosion where the trophy is. There are 2 parameters to fill out here.
  - `power (int)`: how strong the explosion should be. Weaker explosions will be visual, but stonger ones will make you take damage.
  - `destructive (boolean)` defines whether the explosion should break blocks or not.
- `obtrophies:arrow`: shoots a set amount of arrows upwards. There are 2 parameters to fill out here, although one is optional.
  - `amount (int)`: how many arrows the trophy should shoot.
  - `effect (string)`: (optional) the effect that the arrow should be tipped with.
- `obtrophies:guardian_curse`: creates the ghost guardian that floats across your screen when an elder guardian curses you with mining fatigue. This doesnt actually give mining fatigue though.
- `obtrophies:ender_pearl`: shoots an ender pearl up into the air. This ender pearl will teleport you.
- `obtrophies:llama_spit`: shoots some llama spit up into the air.
- `obtrophies:totem_of_undying`: plays the totem animation on your screen.
- `obtrophies:loot_table (2.0+ only!)`: pulls a list of items from a loot table a trophy should give the player when clicked. There are 3 parameters to fill out here, although 1 is optional.
	- `loot_table (string)`: the loot table ID the trophy should reference
	- `cooldown (int)`: (optional) the time, in ticks, it takes until the trophy can give you another item.
	- `rolls (int)`: the amount of times the loot table should be referenced. If youre using a loot table like, say, the cat morning gift table, this will be the amount of items you can get. It may be the same item, it may be different ones per roll, its completely random.
- `obtrophies:right_click_item (2.0+ only!)`: when the trophy is right clicked with a certain item, it will execute another right click behavior. There are 5 pararmeters to fill out here, although 2 are optional.
	- `item_to_use (string)`: the namespace of an item the trophy should be right clicked with.
	- `shrink_item_stack (boolean)`: defines whether or not the item used should be consumed. Do note this only affects survival players.
	- `execute_behavior (Behavior)`: a behavior that fires if the item criteria is met. Allows you to further perform actions based on what the player is holding. For example, if the trophy looks for a `minecraft:glass_bottle` and consumes the stack on use you can use this to give a player a filled bottle, simulating a bottle being filled.
	- `cooldown (int)`: (optional) the time, in ticks, it takes until this behavior can be used again.
	- `sound (string)`: (optional) the namespace of a sound that should play when this behavior fires.
---

## Variants
Starting in version 2.0, you can now define mob variants in the trophy json. There are 2 ways to do this.

### variants[]
Using the variants array allows you to define variants based on entity data. Most variants of mobs are dependent on some sort of tag being a certain value to change their appearance. You can read all about the various entity data for vanilla mobs here: https://antifandom.com/minecraft/wiki/Entity_format. I will be using the Axolotl as an example here.
Axolotls change their texture based on what their `Variant` tag is set to. This tag is a number somewhere between 0 and 4, with 0 being the pink (lucy) version, 1 being the brown (wild) version, and so on. Using this knowledge, we can create a list of variants like so:
```
"variants": [
    {
      "Variant": "0"
    },
    {
      "Variant": "1"
    },
    {
      "Variant": "2"
    },
    {
      "Variant": "3"
    },
    {
      "Variant": "4"
    }
  ]
```
This will add a trophy variant for every Axolotl color. Im sure this looks kind of messy, with the curly brackets and everything, but theres very good reason for doing this: it allows you to define multiple data tags per variant! You normally dont need to do this, but theres one mob in particular I use this for: the panda. https://github.com/GizmoTheMoonPig/OpenBlocksTrophies/blob/1.19/src/generated/resources/data/obtrophies/trophies/panda.json.

### variant_registry
Some mobs are interesting in that their variants are actually their own registry, such as cats or frogs. Unlike using the variant array above, this method allows you to dynamically add more variant trophies if other mods add variants for the entity in question! Setting this up is simple:
```
"variant_registry": {
    "key": "variant",
    "registry": "minecraft:cat_variant"
  }
```
Variants are normally stored in entity data just like the variants above. `key` is the tag the registry name is saved to. `registry` is the name of the registry in question. Currently vanilla has 2 registries: `minecraft:cat_variant` and `minecraft:frog_variant`.

You may notice villagers use this system for their profession registry. Unfortunately this one is special cased as the way villager data is saved is a bit wonky. However, if you have an entity that implements `VillagerDataHolder` (this is a vanilla class, dont worry!) you can add the profession registry block to your mob trophy and it will automatically add all profession variants to your mob!
```
"variant_registry": {
    "key": "profession",
    "registry": "minecraft:villager_profession"
  }
```
---
If you're a mod maker, and you want to add your own behaviors, simply follow the instructions in `CustomBehaviorRegistry`.
