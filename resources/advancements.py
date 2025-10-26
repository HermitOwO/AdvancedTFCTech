from mcresources import ResourceManager, utils, advancements
from mcresources.advancements import AdvancementCategory
from mcresources.type_definitions import Json
from mcresources.utils import is_sequence, resource_location, parse_item_stack

from constants import *


def generate(rm: ResourceManager):
    story = AdvancementCategory(rm, 'story', 'immersiveengineering:textures/block/wooden_decoration/treated_wood.png')

    story.advancement('root', icon('advancedtfctech:pirn'), 'Advanced TFC Tech', 'Machines for large-scale production', None, root_trigger(), chat=False)
    story.advancement('mb_grist_mill', icon('advancedtfctech:grist_mill'), 'On My Grind', 'Form the Grist Mill Multiblock', 'root', multiblock_formed('grist_mill'))
    story.advancement('mb_thresher', icon('advancedtfctech:thresher'), 'Undressing', 'Form the Thresher Multiblock', 'root', multiblock_formed('thresher'))
    story.advancement('mb_power_loom', icon('advancedtfctech:power_loom'), 'Unemployment', 'Form the Power Loom Multiblock', 'root', multiblock_formed('power_loom'))
    story.advancement('grind_all_grain', icon('tfc:food/wheat_flour'), 'Another One Bites The Dust', 'Obtain a Full Stack of Every Flour', 'mb_grist_mill', multiple(*[inventory_changed('32 tfc:food/%s_flour' % grain, name = grain) for grain in TFC_GRAINS]), requirements = [[grain] for grain in TFC_GRAINS], frame='goal')
    story.advancement('thresh_all_grain', icon('tfc:food/wheat_grain'), 'Farmer\'s Delight', 'Obtain a Full Stack of Every Grain', 'mb_thresher', multiple(*[inventory_changed('32 tfc:food/%s_grain' % grain, name = grain) for grain in TFC_GRAINS]), requirements = [[grain] for grain in TFC_GRAINS], frame='goal')
    story.advancement('craft_winded_pirn', icon('advancedtfctech:silk_winded_pirn'), 'Stop Winding', 'Craft all Winded Pirns', 'mb_power_loom', multiple(*[inventory_changed('advancedtfctech:%s' % pirn, name = pirn) for pirn in WINDED_PIRNS]), requirements = [[pirn] for pirn in WINDED_PIRNS], frame='goal')


def icon(name: str) -> Json:
    return {'id': name}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}

def inventory_changed(item: str | Json, name: str = 'item_obtained') -> Json:
    if isinstance(item, str) and name == 'item_obtained':
        name = item.split(':')[1]
    return {name: inventory_changed2(item)}

def inventory_changed2(*item_predicates: Json) -> Json:
    return {
        'trigger': 'minecraft:inventory_changed',
        'conditions': {
            'items': [item_predicate(ip) for ip in item_predicates]
        }
    }

def multiblock_formed(multiblock: str | Json, name: str = 'form_multiblock') -> Json:
    return {
        name: {
            'trigger': 'immersiveengineering:multiblock_formed',
            'conditions': {
                'multiblock': 'advancedtfctech:multiblocks/' + multiblock,
                'hammer': item_predicate('immersiveengineering:hammer')
            }
        }
    }

def item_predicate(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    elif is_sequence(data_in):  # List of item IDs
        return {'items': [resource_location(e).join() for e in data_in]}
    elif isinstance(data_in, str):  # Single item or tag
        item, tag, count, _ = parse_item_stack(data_in, False)
        d: Json = {'items': '#' + item} if tag else {'items': item}
        if count:
            d['count'] = count
        return d
    else:
        raise ValueError('Unknown object %s at item_predicate' % str(data_in))

def multiple(*conditions: Json) -> Json:
    merged = {}
    for c in conditions:
        merged.update(c)
    return merged