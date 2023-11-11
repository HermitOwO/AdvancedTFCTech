from typing import Dict, NamedTuple

SMALL_ITEMS = ('pirn', 'fiber_winded_pirn', 'silk_winded_pirn', 'wool_winded_pirn', 'pineapple_winded_pirn')
WINDED_PIRNS = ('fiber_winded_pirn', 'silk_winded_pirn', 'wool_winded_pirn')
MULTIBLOCKS = ('thresher', 'grist_mill', 'power_loom', 'beamhouse')
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
    'burlap_cloth': Weave('tfc:jute_fiber', 'fiber_winded_pirn', 48, 8, 'advancedtfctech:block/multiblock/power_loom/burlap', 500, 40000),
    'silk_cloth': Weave('minecraft:string', 'silk_winded_pirn', 32, 4, 'advancedtfctech:block/multiblock/power_loom/wool', 250, 20000),
    'wool_cloth': Weave('tfc:wool_yarn', 'wool_winded_pirn', 32, 4, 'advancedtfctech:block/multiblock/power_loom/wool', 250, 20000)
}

DEFAULT_LANG = {
    'item.advancedtfctech.pirn': 'Pirn',
    'item.advancedtfctech.fiber_winded_pirn': 'Fiber-Winded Pirn',
    'item.advancedtfctech.silk_winded_pirn': 'Silk-Winded Pirn',
    'item.advancedtfctech.wool_winded_pirn': 'Wool-Winded Pirn',
    'item.advancedtfctech.pineapple_winded_pirn': 'Pineapple-Winded Pirn',

    'advancedtfctech.creative_tab.main': 'Advanced TFC Tech',

    'tfc.jei.thresher': 'Thresher',
    'tfc.jei.grist_mill': 'Grist Mill',
    'tfc.jei.power_loom': 'Power Loom',
    'tfc.jei.fleshing_machine': 'Fleshing Machine',
    'tfc.jei.beamhouse': 'Beamhouse',

    'advancedtfctech.jei.not_consumed': 'Not Consumed',
    'advancedtfctech.jei.double_if_has_tag': 'Output will be doubled if input is Machine-Made',

    'advancedtfctech.tooltip.firmalife_not_loaded': 'Dummy item because Firmalife is not loaded',
    'advancedtfctech.tooltip.machine_made': 'Machine-Made',

    'advancedtfctech.gui.distribute': 'Distribute Inputs',

    'manual.advancedtfctech.advancedtfctech': 'TerraFirmaCraft Tech',

    'subtitle.advancedtfctech.thresher': 'Thresher whirs',
    'subtitle.advancedtfctech.grist_mill': 'Grist Mill grinds',
    'subtitle.advancedtfctech.beamhouse': 'Beamhouse barrel spins',
    'subtitle.advancedtfctech.fleshing_machine': 'Fleshing Machine operates',

    'desc.advancedtfctech.bladeIntegrity': 'Integrity %1$s %%'
}


def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
