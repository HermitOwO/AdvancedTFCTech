from typing import Dict, Tuple, NamedTuple

ITEMS = ('pirn', 'fiber_winded_pirn', 'silk_winded_pirn', 'wool_winded_pirn', 'pineapple_winded_pirn')
WINDED_PIRNS = ('fiber_winded_pirn', 'silk_winded_pirn', 'wool_winded_pirn')
MULTIBLOCKS = ('thresher', 'grist_mill', 'power_loom')
TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')

class Weave(NamedTuple):
    ingredient: str
    pirn: str
    input_amount: int
    output_amount: int
    in_progress_texture: str
    time: int
    energy: int

LOOM: Dict[str, Weave] = {
    'burlap_cloth': Weave('tfc:jute_fiber', 'fiber_winded_pirn', 48, 8, 'advancedtfctech:multiblock/power_loom/burlap', 500, 40000),
    'silk_cloth': Weave('minecraft:string', 'silk_winded_pirn', 32, 4, 'advancedtfctech:multiblock/power_loom/wool', 250, 20000),
    'wool_cloth': Weave('tfc:wool_yarn', 'wool_winded_pirn', 32, 4, 'advancedtfctech:multiblock/power_loom/wool', 250, 20000)
}

DEFAULT_LANG = {
    'item.advancedtfctech.pirn': 'Pirn',
    'item.advancedtfctech.fiber_winded_pirn': 'Fiber-Winded Pirn',
    'item.advancedtfctech.silk_winded_pirn': 'Silk-Winded Pirn',
    'item.advancedtfctech.wool_winded_pirn': 'Wool-Winded Pirn',
    'item.advancedtfctech.pineapple_winded_pirn': 'Pineapple-Winded Pirn',

    'itemGroup.advancedtfctech': 'Advanced TFC Tech',

    'tfc.jei.thresher': 'Thresher',
    'tfc.jei.grist_mill': 'Grist Mill',
    'tfc.jei.power_loom': 'Power Loom',

    'manual.advancedtfctech.advancedtfctech': 'TerraFirmaCraft Tech',

    'advancedtfctech.jei.not_consumed': 'Not Consumed',

    'advancedtfctech.tooltip.firmalife_not_loaded': 'Dummy item because Firmalife is not loaded'
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()