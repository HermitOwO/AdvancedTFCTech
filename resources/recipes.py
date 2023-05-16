from mcresources import ResourceManager, RecipeContext, utils
from mcresources.type_definitions import Json, ResourceIdentifier

from constants import *



def generate(rm:ResourceManager):

    rm.crafting_shaped('crafting/pirn', ['X', 'Y', 'X'], {'X': '#forge:treated_wood', 'Y': '#forge:rods/treated_wood'}, 'advancedtfctech:pirn')

    for grain in TFC_GRAINS:
        thresher_recipe(rm, '%s' % grain,
            result =
                {
                    'count': 3,
                    'base_ingredient':
                    {'item': 'tfc:food/%s_grain' % grain}
                },
            secondaries =
                [{
                    'output':
                    {
                        'count': 4,
                        'base_ingredient': {'item': 'tfc:straw'}
                    }
                }],
            input = not_rotten('tfc:food/%s' % grain),
            time = 80,
            energy = 6400)

        grist_mill_recipe(rm, '%s' % grain,
            result =
                {
                    'count': 3,
                    'base_ingredient':
                    {'item': 'tfc:food/%s_flour' % grain}
                },
            input = not_rotten('tfc:food/%s_grain' % grain),
            time = 80,
            energy = 6400)

    for pirn, weave in LOOM.items():
        power_loom_recipe(rm, weave.cloth,
            result =
                {
                    'count': weave.amount,
                    'base_ingredient':
                    {'item': 'tfc:' + weave.cloth}
                },
            inputs =
                [
                    {
                        'count': weave.amount * 6,
                        'base_ingredient':
                        {'item': weave.ingredient}
                    },
                    {'item': 'advancedtfctech:%s' % pirn}
                ],
            time = weave.amount * 62.5,
            energy = weave.amount * 5000)

        rm.crafting_shaped('crafting/%s' % pirn, ['XXX', 'XYX', 'XXX'], {'X': weave.ingredient, 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:%s' % pirn)

def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

def thresher_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, secondaries: list, input: Json, time: int, energy: int):
    rm.recipe(('thresher', name_parts), 'advancedtfctech:thresher', {
        'result': result,
        'secondaries': secondaries,
        'input': input,
        'time': time,
        'energy': energy
    })

def grist_mill_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, input: Json, time: int, energy: int):
    rm.recipe(('grist_mill', name_parts), 'advancedtfctech:grist_mill', {
        'result': result,
        'input': input,
        'time': time,
        'energy': energy
    })

def power_loom_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, inputs: list, time: int, energy: int):
    rm.recipe(('power_loom', name_parts), 'advancedtfctech:power_loom', {
        'result': result,
        'inputs': inputs,
        'time': time,
        'energy': energy
    })