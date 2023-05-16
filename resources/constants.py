from typing import Dict, Tuple, NamedTuple

ITEMS = ("pirn", "fiber_winded_pirn", "silk_winded_pirn", "wool_winded_pirn")
WINDED_PIRNS = ("fiber_winded_pirn", "silk_winded_pirn", "wool_winded_pirn")
TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')

class Weave(NamedTuple):
    ingredient: str
    cloth: str
    amount: int

LOOM: Dict[str, Weave] = {
    'fiber_winded_pirn': Weave('tfc:jute_fiber', 'burlap_cloth', 8),
    'silk_winded_pirn': Weave('minecraft:string', 'silk_cloth', 4),
    'wool_winded_pirn': Weave('tfc:wool_yarn', 'wool_cloth', 6)
}

DEFAULT_LANG = {
    'item.advancedtfctech.pirn': 'Pirn',
    'item.advancedtfctech.fiber_winded_pirn': 'Fiber-Winded Pirn',
    'item.advancedtfctech.silk_winded_pirn': 'Silk-Winded Pirn',
    'item.advancedtfctech.wool_winded_pirn': 'Wool-Winded Pirn',

    'block.advancedtfctech.thresher': 'Thresher',
    'block.advancedtfctech.grist_mill': 'Grist Mill',
    'block.advancedtfctech.power_loom': 'Power Loom',

    'itemGroup.advancedtfctech': 'Advanced TFC Tech',

    'tfc.jei.thresher': "Thresher",
    'tfc.jei.grist_mill': "Grist Mill",
    'tfc.jei.power_loom': "Power Loom",

    'manual.advancedtfctech.advancedtfctech': 'TerraFirmaCraft Tech',
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()