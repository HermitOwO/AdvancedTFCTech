import os
from argparse import ArgumentParser

from mcresources.type_definitions import ResourceIdentifier

from patchouli import *
from i18n import I18n


class LocalInstance:
    INSTANCE_DIR = os.getenv('LOCAL_MINECRAFT_INSTANCE')  # The location of a local .minecraft directory, for testing in external minecraft instance (as hot reloading works much better)

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None


def main():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us')

    args = parser.parse_args()

    rm = ResourceManager('tfc', '../src/main/resources')
    i18n = I18n.create(args.translate)

    print('Writing book')
    make_book(rm, i18n)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
        make_book(rm, I18n.create('en_us'), local_instance=True)

    print('Done')


def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False):
    rm.domain = 'advancedtfctech'
    book = Book(rm, 'field_guide', {}, i18n, local_instance)

    book.category('main', 'Advanced TFC Tech', 'Machines for large-scale production.', 'advancedtfctech:pirn', is_sorted=True, entries=(
        entry('power_loom', 'Power Loom', 'advancedtfctech:power_loom', pages=(
            non_text_first_page(),
            multiblock('Power Loom', 'To form the structure, $(item)right click$() the $(thing)Light Engineering Block$() above the Block of Steel with an Engineer\'s Hammer.', True, pattern=(
                ('   ', 'SM ', 'SM ', 'SM ', '   '),
                (' L ', 'SMS', ' F ', 'SMS', ' L '),
                (' L ', 'SHS', 'S0S', 'SHS', 'RT ')), mapping={
                'S': 'immersiveengineering:steel_scaffolding_standard',
                'M': 'immersiveengineering:sheetmetal_steel',
                'T': 'immersiveengineering:storage_steel',
                'F': 'immersiveengineering:steel_fence',
                'L': 'immersiveengineering:light_engineering',
                'H': 'immersiveengineering:heavy_engineering',
                'R': 'immersiveengineering:rs_engineering',
                '0': 'immersiveengineering:heavy_engineering'
            }).link('advancedtfctech:power_loom'),
            text('The $(thing)Power Loom$() is an innovative solution to a growing demand for textiles.$(br2)To automate the weaving process, a shuttle containing a pirn spooled with yarn is rapidly sent back and forth between two perpendicular sets of strung yarn alternating between high and low positions.$(br2)In order to operate the Power Loom, $(thing)pirns spooled with a selected yarn/fiber$() must be put on the holder located at the $(thing)side$() of the machine.'),
            text('Before the loom can operate, it must be primed with $(thing)16 of the selected yarn/fiber$() by $(item)rightclicking$() with the yarn/fiber on the \'grill\' in the $(thing)center$() of the machine.$(br2)Then, $(thing)input yarn/fiber$() can be put with the same method on the $(thing)lower beam on the machine\'s shorter side$().$(br2)Output may be retrieved by a storage device placed in the $(thing)center of the output side$(), or by $(item)shift-rightclicking$() on the back.'),
            text('By default, it consumes 80IF/t.')
        )),
        entry('thresher', 'Thresher', 'advancedtfctech:thresher', pages=(
            non_text_first_page(),
            multiblock('Thresher', '$(item)Right click$() the $(thing)Light Engineering Block$() in the middle layer opposite to Steel Sheetmetal with an Engineer\'s Hammer.', True, pattern=(
                ('   ', 'MHM', '   '),
                ('MMM', 'LLR', 'MLM'),
                ('MLM', 'M0M', 'MMM')), mapping={
                'M': 'immersiveengineering:sheetmetal_steel',
                'L': 'immersiveengineering:light_engineering',
                'R': 'immersiveengineering:rs_engineering',
                'H': 'minecraft:hopper',
                '0': 'immersiveengineering:light_engineering'
            }).link('advancedtfctech:thresher'),
            text('The $(thing)Thresher$() separates chaff from grain using rotating cylinders and mechanical power.$(br2)Items can be inputted from the top by a Hopper, a Chute, or a Dropping Conveyor Belt.$(br2)By default, it consumes 80IF/t.')
        )),
        entry('grist_mill', 'Grist Mill', 'advancedtfctech:grist_mill', pages=(
            non_text_first_page(),
            multiblock('Grist Mill', '$(item)Right click$() the $(thing)Block of Steel$() from the side opposite of the Redstone Engineering Block with an Engineer\'s Hammer.', True, pattern=(
                ('   ', ' H ', '   ', '   '),
                (' L ', 'RLM', 'mTm', 'mLm'),
                ('SMS', 'S0L', 'SSS', 'SMS')), mapping={
                'S': 'immersiveengineering:steel_scaffolding_standard',
                'M': 'immersiveengineering:sheetmetal_steel',
                'm': 'immersiveengineering:slab_sheetmetal_steel',
                'T': 'immersiveengineering:storage_steel',
                'L': 'immersiveengineering:light_engineering',
                'R': 'immersiveengineering:rs_engineering',
                'H': 'minecraft:hopper',
                '0': 'immersiveengineering:steel_scaffolding_standard'
            }).link('advancedtfctech:grist_mill'),
            text('The $(thing)Grist Mill$() grains raw grain into more edible flour.$(br2)Items can be inputted from the top by a Hopper, a Chute, or a Dropping Conveyor Belt.$(br2)By default, it consumes 80IF/t.')
        )),
        entry('beamhouse', 'Beamhouse', 'advancedtfctech:beamhouse', pages=(
            non_text_first_page(),
            multiblock('Beamhouse', '$(item)Right click$() the $(thing)Heavy Engineering Block$() with the Hammer. $(#007500)Orange wool$() should be $(#007500)fluid pipes$() but they crash if rendered.', True, pattern=(
                ('    ', ' mmm', ' MMM', ' mmm'),
                (' L L', 'RMHM', 'LM M', 'LMMM'),
                ('SPSL', 'SP0S', 'LPSS', 'LSSS')), mapping={
                'S': 'immersiveengineering:steel_scaffolding_standard',
                'M': 'immersiveengineering:sheetmetal_iron',
                'm': 'immersiveengineering:slab_sheetmetal_iron',
                'P': 'minecraft:orange_wool',
                'H': 'immersiveengineering:heavy_engineering',
                'L': 'immersiveengineering:light_engineering',
                'R': 'immersiveengineering:rs_engineering',
                '0': 'immersiveengineering:steel_scaffolding_standard'
            }).link('advancedtfctech:beamhouse'),
            text('The $(thing)Beamhouse$() handles liming, washing and tanning of hides in the leather production process.$(br2)For fluid input, either put a filled bucket inside the GUI or $(item)rightclick$() the fluid input port with the filled bucket. Unwanted fluid can be removed by $(item)rightclick$() the same port with an empty bucket.$(br2)By default, it consumes 20IF/t.'),
            item_spotlight('tfc:large_prepared_hide{machine_made:1b}', title='Machine-Made Hides', text_contents='Raw hides will obtain the $(thing)"Machine-Made"$() tag after getting processed by the $(thing)Beamhouse$(). The tag will be inherited if the subsequent washing, fleshing (scraping) and tanning process are handled by the $(thing)Beamhouse$() and the $(l:advancedtfctech:main/fleshing_machine)Fleshing Machine$().').anchor('machine_made'),
            text('Going through all these processes, the amount of leather obtained will be $(thing)double$() the normal amount.$(br2)Note that if any step of the process is not handled by machines, the $(thing)"Machine-Made"$() tag will be lost and the bonus of double output cannot be retained.')
        )),
        entry('fleshing_machine', 'Fleshing Machine', 'advancedtfctech:fleshing_machine', pages=(
            text('The $(thing)Fleshing Machine$() removes excess fat and flesh from hides using a rotating cylinder with sharp blades that scrap the hide as it passes through the machine.').link('advancedtfctech:fleshing_machine', 'advancedtfctech:fleshing_blades'),
            crafting('advancedtfctech:crafting/fleshing_machine'),
            text('In order to operate the $(thing)Fleshing Machine$(), $(thing)Fleshing Blades$() need to be put on the cylinder inside the machine by $(item)rightclicking$() the machine with $(thing)Fleshing Blades$().$(br2)Output will be tagged with $(l:advancedtfctech:main/beamhouse#machine_made)"Machine-Made"$() if the input has the same tag.$(br2)By default, it consumes 20IF/t.'),
            anvil_recipe('advancedtfctech:anvil/fleshing_blades', text_content='')
        ))
    ))


if __name__ == '__main__':
    main()
