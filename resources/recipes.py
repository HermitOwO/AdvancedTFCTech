from mcresources import ResourceManager, RecipeContext, utils
from mcresources.type_definitions import Json, ResourceIdentifier

from constants import *



def generate(rm:ResourceManager):

    rm.crafting_shaped('crafting/pirn', ['X', 'Y', 'X'], {'X': '#forge:treated_wood', 'Y': '#forge:rods/treated_wood'}, 'advancedtfctech:pirn')

    for grain in TFC_GRAINS:
        thresher_recipe(rm, '%s' % grain,
            result = item_stack_provider('3 tfc:food/%s_grain' % grain, copy_food=True),
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
            result = item_stack_provider('3 tfc:food/%s_flour' % grain, copy_food=True),
            input = not_rotten('tfc:food/%s_grain' % grain),
            time = 80,
            energy = 6400)

    for cloth, weave in LOOM.items():
        power_loom_recipe(rm, cloth,
            result =
                {
                    'count': weave.output_amount,
                    'base_ingredient':
                    {'item': 'tfc:' + cloth}
                },
            inputs =
                [
                    {
                        'count': weave.input_amount,
                        'base_ingredient':
                        {'item': weave.ingredient}
                    },
                    {'item': 'advancedtfctech:%s' % weave.pirn}
                ],
            time = weave.time,
            energy = weave.energy)

        rm.crafting_shaped('crafting/%s' % weave.pirn, ['XXX', 'XYX', 'XXX'], {'X': weave.ingredient, 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:%s' % weave.pirn)

def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, copy_oldest_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False, copy_forging: bool = False, other_modifier: str = None, other_other_modifier: str = None) -> Json:
    if isinstance(data_in, dict):
        return data_in
    stack = utils.item_stack(data_in) if data_in is not None else None
    modifiers = [k for k, v in (
        ('tfc:copy_input', copy_input),
        ('tfc:copy_heat', copy_heat),
        ('tfc:copy_food', copy_food),
        ('tfc:reset_food', reset_food),
        ('tfc:empty_bowl', empty_bowl),
        ('tfc:copy_forging_bonus', copy_forging),
        ('tfc:copy_oldest_food', copy_oldest_food),
        (other_modifier, other_modifier is not None),
        (other_other_modifier, other_other_modifier is not None),
        ({'type': 'tfc:add_heat', 'temperature': add_heat}, add_heat is not None),
        ({'type': 'tfc:add_trait', 'trait': add_trait}, add_trait is not None),
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None)
    ) if v]
    if modifiers:
        return {
            'stack': stack,
            'modifiers': modifiers
        }
    return stack

def thresher_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, secondaries: list, input: Json, time: int, energy: int):
    rm.recipe(('thresher', name_parts), 'advancedtfctech:thresher', {
        'result': item_stack_provider(result),
        'secondaries': secondaries,
        'input': input,
        'time': time,
        'energy': energy
    })

def grist_mill_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, input: Json, time: int, energy: int):
    rm.recipe(('grist_mill', name_parts), 'advancedtfctech:grist_mill', {
        'result': item_stack_provider(result),
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