from enum import Enum
from typing import List

from mcresources import ResourceManager, utils
from mcresources.type_definitions import Json, JsonObject

from constants import *


class Rules(Enum):
    hit_any = 'hit_any'
    hit_not_last = 'hit_not_last'
    hit_last = 'hit_last'
    hit_second_last = 'hit_second_last'
    hit_third_last = 'hit_third_last'
    draw_any = 'draw_any'
    draw_last = 'draw_last'
    draw_not_last = 'draw_not_last'
    draw_second_last = 'draw_second_last'
    draw_third_last = 'draw_third_last'
    punch_any = 'punch_any'
    punch_last = 'punch_last'
    punch_not_last = 'punch_not_last'
    punch_second_last = 'punch_second_last'
    punch_third_last = 'punch_third_last'
    bend_any = 'bend_any'
    bend_last = 'bend_last'
    bend_not_last = 'bend_not_last'
    bend_second_last = 'bend_second_last'
    bend_third_last = 'bend_third_last'
    upset_any = 'upset_any'
    upset_last = 'upset_last'
    upset_not_last = 'upset_not_last'
    upset_second_last = 'upset_second_last'
    upset_third_last = 'upset_third_last'
    shrink_any = 'shrink_any'
    shrink_last = 'shrink_last'
    shrink_not_last = 'shrink_not_last'
    shrink_second_last = 'shrink_second_last'
    shrink_third_last = 'shrink_third_last'


def generate(rm: ResourceManager):
    rm.crafting_shaped('crafting/pirn', ['X', 'Y', 'X'], {'X': '#forge:treated_wood', 'Y': '#forge:rods/treated_wood'}, 'advancedtfctech:pirn')

    rm.crafting_shaped('crafting/fleshing_machine', ['IEI', 'BMB', 'ICI'], {'I': '#forge:sheetmetals/iron', 'E': 'immersiveengineering:component_electronic', 'B': 'tfc:brass_mechanisms', 'M': 'immersiveengineering:component_iron', 'C': 'immersiveengineering:coil_lv'}, 'advancedtfctech:fleshing_machine')

    anvil_recipe(rm, 'fleshing_blades', '#forge:sheets/wrought_iron', 'advancedtfctech:fleshing_blades', 3, Rules.hit_last, Rules.shrink_second_last, Rules.upset_third_last, bonus=True)

    for grain in TFC_GRAINS:
        thresher_recipe(rm, '%s' % grain,
                        result=item_stack_provider('2 tfc:food/%s_grain' % grain, copy_food=True),
                        secondaries=[{'output': ingredient_with_size('4 tfc:straw')}],
                        input=not_rotten('tfc:food/%s' % grain),
                        time=80,
                        energy=6400)

        grist_mill_recipe(rm, '%s' % grain,
                          result=item_stack_provider('2 tfc:food/%s_flour' % grain, copy_food=True),
                          input=not_rotten('tfc:food/%s_grain' % grain),
                          time=80,
                          energy=6400)

    for cloth, weave in LOOM.items():
        power_loom_recipe(rm, cloth,
                          result=utils.item_stack('%s tfc:' % weave.output_amount + cloth),
                          secondaries=[{'output': ingredient_with_size('advancedtfctech:pirn')}],
                          inputs=ingredient_with_size_list(('%s %s' % (weave.input_amount, weave.ingredient), 'advancedtfctech:%s' % weave.pirn)),
                          secondary_input=ingredient_with_size('16 ' + weave.ingredient),
                          in_progress_texture=weave.in_progress_texture,
                          time=weave.time,
                          energy=weave.energy)

        rm.crafting_shaped('crafting/%s' % weave.pirn, ['XXX', 'XYX', 'XXX'], {'X': weave.ingredient, 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:%s' % weave.pirn)

    for size, time in (('small', 100), ('medium', 150), ('large', 200)):
        fleshing_machine_recipe(rm, '%s_scraped_hide' % size,
                                result=item_stack_provider('tfc:%s_scraped_hide' % size, copy_tag='machine_made'),
                                input='tfc:%s_soaked_hide' % size,
                                time=time,
                                energy=20 * time)

    for size, amount, output, time in (('small', 300, 1, 200), ('medium', 400, 2, 300), ('large', 500, 3, 400)):
        beamhouse_recipe(rm, '%s_soaked_hide' % size,
                         result=item_stack_provider('tfc:%s_soaked_hide' % size, add_tag='machine_made'),
                         input='tfc:%s_raw_hide' % size,
                         fluid='%s #tfc:limewater' % amount,
                         time=time,
                         energy=20 * time)

        beamhouse_recipe(rm, '%s_prepared_hide' % size,
                         result=item_stack_provider('tfc:%s_prepared_hide' % size, copy_tag='machine_made'),
                         input='tfc:%s_scraped_hide' % size,
                         fluid='%s #tfc:fresh_water' % amount,
                         time=time,
                         energy=20 * time)

        beamhouse_recipe(rm, '%s_leather' % size,
                         result=item_stack_provider('%s minecraft:leather' % output, double_if_has_tag='machine_made'),
                         input='tfc:%s_prepared_hide' % size,
                         fluid='%s #tfc:tannin' % amount,
                         time=time,
                         energy=20 * time)

    # Firmalife compat

    power_loom_recipe(rm, 'pineapple_leather',
                      result=utils.item_stack('4 firmalife:pineapple_leather'),
                      secondaries=[{'output': ingredient_with_size('advancedtfctech:pirn')}],
                      inputs=ingredient_with_size_list(('32 firmalife:pineapple_yarn', 'advancedtfctech:pineapple_winded_pirn')),
                      secondary_input=ingredient_with_size('16 firmalife:pineapple_yarn'),
                      in_progress_texture='advancedtfctech:block/multiblock/power_loom/pineapple',
                      time=250,
                      energy=20000,
                      conditional_modid='firmalife')

    rm.crafting_shaped('crafting/pineapple_winded_pirn', ['XXX', 'XYX', 'XXX'], {'X': 'firmalife:pineapple_yarn', 'Y': 'advancedtfctech:pirn'}, 'advancedtfctech:pineapple_winded_pirn', conditions={'type': 'forge:mod_loaded', 'modid': 'firmalife'})


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


def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, copy_oldest_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False, copy_forging: bool = False, add_tag: str = None, copy_tag: str = None, double_if_has_tag: str = None, other_modifier: str = None, other_other_modifier: str = None) -> Json:
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
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None),
        ({'type': 'advancedtfctech:add_tag', 'tag': add_tag}, add_tag is not None),
        ({'type': 'advancedtfctech:copy_tag', 'tag': copy_tag}, copy_tag is not None),
        ({'type': 'advancedtfctech:double_if_has_tag', 'tag': double_if_has_tag}, double_if_has_tag is not None)
    ) if v]
    if modifiers:
        return {
            'stack': stack,
            'modifiers': modifiers
        }
    return stack


def fluid_tag_input(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
    assert tag, 'fluid_tag_input() must be a tag'
    return {
        'tag': fluid,
        'amount': amount
    }


def anvil_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: Json, result: Json, tier: int, *rules: Rules, bonus: bool = None):
    rm.recipe(('anvil', name_parts), 'tfc:anvil', {
        'input': utils.ingredient(ingredient),
        'result': item_stack_provider(result),
        'tier': tier,
        'rules': [r.name for r in rules],
        'apply_forging_bonus': bonus
    })


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
    }, conditions={'type': 'forge:mod_loaded', 'modid': conditional_modid} if conditional_modid is not None else None)


def beamhouse_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, input: Json, fluid: Json, time: int, energy: int):
    rm.recipe(('beamhouse', name_parts), 'advancedtfctech:beamhouse', {
        'result': item_stack_provider(result),
        'input': ingredient_with_size(input),
        'fluid': fluid_tag_input(fluid),
        'time': time,
        'energy': energy
    })


def fleshing_machine_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, result: Json, input: Json, time: int, energy: int):
    rm.recipe(('fleshing_machine', name_parts), 'advancedtfctech:fleshing_machine', {
        'result': item_stack_provider(result),
        'input': utils.ingredient(input),
        'time': time,
        'energy': energy
    })
