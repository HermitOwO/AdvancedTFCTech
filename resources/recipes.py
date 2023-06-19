from mcresources import ResourceManager, RecipeContext, utils
from mcresources.type_definitions import Json, JsonObject, ResourceIdentifier
from typing import List, Tuple, Dict, Sequence, Optional, Callable, Any, Literal, Union

from constants import *



def generate(rm:ResourceManager):

    rm.crafting_shaped('crafting/pirn', ['X', 'Y', 'X'], {'X': '#forge:treated_wood', 'Y': '#forge:rods/treated_wood'}, 'advancedtfctech:pirn')

    for grain in TFC_GRAINS:
        thresher_recipe(rm, '%s' % grain,
            result = item_stack_provider('3 tfc:food/%s_grain' % grain, copy_food=True),
            secondaries = [{'output': ingredient_with_size('4 tfc:straw')}],
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
            result = utils.item_stack(str(weave.output_amount) + ' tfc:' + cloth),
            secondaries = [{'output': ingredient_with_size('advancedtfctech:pirn')}],
            inputs = ingredient_with_size_list((str(weave.input_amount) + ' ' + weave.ingredient, 'advancedtfctech:%s' % weave.pirn)),
            secondary_input = ingredient_with_size('16 ' + weave.ingredient),
            in_progress_texture = weave.in_progress_texture,
            time = weave.time,
            energy = weave.energy)

        rm.crafting_shaped('crafting/%s' % weave.pirn, ['XXX', 'XYX', 'XXX'], {'X': weave.ingredient, 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:%s' % weave.pirn)

    # Firmalife compat

    power_loom_recipe(rm, 'pineapple_leather',
        result = utils.item_stack('4 firmalife:pineapple_leather'),
        secondaries = [{'output': ingredient_with_size('advancedtfctech:pirn')}],
        inputs = ingredient_with_size_list(('32 firmalife:pineapple_yarn', 'advancedtfctech:pineapple_winded_pirn')),
        secondary_input = ingredient_with_size('16 firmalife:pineapple_yarn'),
        in_progress_texture = 'advancedtfctech:multiblock/power_loom/pineapple',
        time = 250,
        energy = 20000,
        conditional_modid = 'firmalife')

    rm.crafting_shaped('crafting/pineapple_winded_pirn', ['XXX', 'XYX', 'XXX'], {'X': 'firmalife:pineapple_yarn', 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:pineapple_winded_pirn', conditions = {'type': 'forge:mod_loaded', 'modid': 'firmalife'})

def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

def ingredient_with_size(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    item, tag, amount, _ = utils.parse_item_stack(data_in, False)
    if amount is None:
        amount = 1
    if amount > 1:
        return {'base_ingredient': {'tag' if tag else 'item': item}, 'count': amount}
    else:
        return {'tag' if tag else 'item': item}

def ingredient_with_size_list(data_in: Json) -> List[JsonObject]:
    if isinstance(data_in, str) or isinstance(data_in, Dict):
        return [ingredient_with_size(data_in)]
    elif utils.is_sequence(data_in):
        return [*utils.flatten_list([ingredient_with_size(s) for s in data_in])]
    else:
        raise ValueError('Unknown object %s at ingredient_with_size_list' % str(data_in))

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

def power_loom_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, secondaries: list, inputs: Json, secondary_input: Json, in_progress_texture: str, time: int, energy: int, conditional_modid: str = None):
    rm.recipe(('power_loom', name_parts), 'advancedtfctech:power_loom', {
        'result': utils.item_stack(result),
        'secondaries': secondaries,
        'inputs': ingredient_with_size_list(inputs),
        'secondary_input': secondary_input,
        'in_progress_texture': in_progress_texture,
        'time': time,
        'energy': energy
    }, conditions = {'type': 'forge:mod_loaded', 'modid': conditional_modid} if conditional_modid is not None else None)