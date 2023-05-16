from mcresources import ResourceManager, utils, advancements
from mcresources.advancements import AdvancementCategory
from mcresources.type_definitions import Json

from constants import *



def generate(rm: ResourceManager):
    story = AdvancementCategory(rm, 'story', 'immersiveengineering:textures/block/wooden_decoration/treated_wood.png')

    story.advancement('root', icon('tfc:silk_cloth'), 'Advanced TFC Tech', 'Machines for large-scale production', None, root_trigger(), chat=False)
    story.advancement('craft_pirn', icon('advancedtfctech:pirn'), 'Winder', 'Craft a Pirn', 'root', inventory_changed('advancedtfctech:pirn'))
    story.advancement('craft_weaved_pirn', icon('advancedtfctech:silk_winded_pirn'), 'Stop Winding', 'Craft all Winded Pirns', 'craft_pirn', multiple(*[inventory_changed('advancedtfctech:%s' % pirn, name = pirn) for pirn in WINDED_PIRNS]), requirements = [[pirn] for pirn in WINDED_PIRNS], frame='goal')



def icon(name: str) -> Json:
    return {'item': name}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}

def inventory_changed(item: str | Json, name: str = 'item_obtained') -> Json:
    if isinstance(item, str) and name == 'item_obtained':
        name = item.split(':')[1]
    return {name: advancements.inventory_changed(item)}

def multiple(*conditions: Json) -> Json:
    merged = {}
    for c in conditions:
        merged.update(c)
    return merged