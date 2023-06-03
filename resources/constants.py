from typing import Dict, Tuple, NamedTuple

ITEMS = ("pirn", "fiber_winded_pirn", "silk_winded_pirn", "wool_winded_pirn")
WINDED_PIRNS = ("fiber_winded_pirn", "silk_winded_pirn", "wool_winded_pirn")
TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')

class Weave(NamedTuple):
    ingredient: str
    pirn: str
    input_amount: int
    output_amount: int

LOOM: Dict[str, Weave] = {
    'burlap_cloth': Weave('tfc:jute_fiber', 'fiber_winded_pirn', 48, 8),
    'silk_cloth': Weave('minecraft:string', 'silk_winded_pirn', 32, 4),
    'wool_cloth': Weave('tfc:wool_yarn', 'wool_winded_pirn', 32, 4)
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