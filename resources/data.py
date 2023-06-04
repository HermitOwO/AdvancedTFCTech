from enum import Enum, auto

from mcresources import ResourceManager, utils, loot_tables

from constants import *

class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()

class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()



def generate(rm: ResourceManager):
    for item in ITEMS:
        item_size(rm, '%s' % item, 'advancedtfctech:%s' % item, Size.small, Weight.light)



def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })