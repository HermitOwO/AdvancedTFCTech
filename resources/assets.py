from mcresources import ResourceManager, ItemContext, BlockContext, block_states
from mcresources import utils, loot_tables

from constants import *



def generate(rm: ResourceManager):
    for item in ITEMS:
        rm.item_model(item)

    for key, value in DEFAULT_LANG.items():
        rm.lang(key, value)