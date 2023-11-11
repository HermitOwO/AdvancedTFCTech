from mcresources import ResourceManager, atlases

from constants import *


def generate(rm: ResourceManager):
    for item in SMALL_ITEMS:
        rm.item_model(item)

    for block in MULTIBLOCKS:
        rm.block('%s' % block).with_lang(lang('%s', block)).with_tag('minecraft:mineable/pickaxe').with_block_loot({"type": "immersiveengineering:drop_inv"}, {'type': 'immersiveengineering:multiblock'})

    rm.item_model('fleshing_blades').with_lang(lang('fleshing_blades'))

    rm.block('fleshing_machine').with_lang(lang('fleshing_machine')).with_tag('minecraft:mineable/pickaxe').with_block_loot({"type": "immersiveengineering:drop_inv"}, 'advancedtfctech:fleshing_machine')

    rm.atlas('minecraft:blocks',
             atlases.directory('block/multiblock/power_loom'),
             atlases.directory('block/metal_device/fleshing_machine')
             )

    for key, value in DEFAULT_LANG.items():
        rm.lang(key, value)
