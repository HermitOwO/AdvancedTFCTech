ITEMS = ("pirn", "fiber_winded_pirn", "silk_winded_pirn", "wool_winded_pirn")
TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')

DEFAULT_LANG = {
    'item.advancedtfctech.pirn': 'Pirn',
    'item.advancedtfctech.fiber_winded_pirn': 'Fiber-Winded Pirn',
    'item.advancedtfctech.silk_winded_pirn': 'Silk-Winded Pirn',
    'item.advancedtfctech.wool_winded_pirn': 'Wool-Winded Pirn',

    'block.advancedtfctech.thresher': 'Thresher',
    'block.advancedtfctech.grist_mill': 'Grist Mill',

    'itemGroup.advancedtfctech': 'Advanced TFC Tech',

    'tfc.jei.thresher': "Thresher",
    'tfc.jei.grist_mill': "Grist Mill",

    'manual.advancedtfctech.advancedtfctech': 'TerraFirmaCraft Tech',
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()