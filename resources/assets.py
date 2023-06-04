from mcresources import ResourceManager, ItemContext, BlockContext, block_states
from mcresources import utils, loot_tables

from constants import *



def generate(rm: ResourceManager):
    for item in ITEMS:
        rm.item_model(item)

    for block in MULTIBLOCKS:
        rm.block('%s' % block).with_lang(lang('%s', block)).with_tag('minecraft:mineable/pickaxe').with_block_loot({"type": "immersiveengineering:drop_inv"}, {'type': 'immersiveengineering:multiblock_original_block'})

    for key, value in DEFAULT_LANG.items():
        rm.lang(key, value)