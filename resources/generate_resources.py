import argparse
import sys
import traceback
from typing import Sequence, Dict

from mcresources import ResourceManager, utils
from mcresources.type_definitions import Json, ResourceIdentifier

import assets
import advancements


class ModificationLoggingResourceManager(ResourceManager):

    def write(self, path_parts: Sequence[str], data_in: Json):
        m = self.modified_files
        super(ModificationLoggingResourceManager, self).write(path_parts, data_in)
        if m != self.modified_files:
            print('Modified: ' + utils.resource_location(self.domain, path_parts).join(), file=sys.stderr)
            traceback.print_stack()
            print('', file=sys.stderr)


class TempResourceManager(ResourceManager):

    def __init__(self, domain: str, resource_dir):
        super().__init__(domain, resource_dir)

    def advancement(self, name_parts: ResourceIdentifier, display: Json = None, parent: str = None, criteria: Dict[str, Dict[str, Json]] = None, requirements: Sequence[Sequence[str]] = None, rewards: Dict[str, Json] = None):
        res = utils.resource_location(self.domain, name_parts)
        if requirements is None or requirements == 'or':
            requirements = [[k for k in criteria.keys()]]
        elif requirements == 'and':
            requirements = [[k] for k in criteria.keys()]
        self.write(('data', res.domain, 'advancement', res.path), {
            'parent': parent,
            'criteria': criteria,
            'display': display,
            'requirements': requirements,
            'rewards': rewards
        })


def main():
    parser = argparse.ArgumentParser(description='Generate resources for Advanced TFC Tech')
    rm = TempResourceManager('advancedtfctech', resource_dir='../src/main/resources')
    parser.add_argument('--clean', action='store_true', dest='clean', help='Clean all auto generated resources')
    args = parser.parse_args()

    if args.clean:
        # Stupid windows file locking errors.
        for tries in range(1, 1 + 3):
            try:
                utils.clean_generated_resources('/'.join(rm.resource_dir))
                print('Clean Success')
                return
            except:
                print('Failed, retrying (%d / 3)' % tries)
        print('Clean Aborted')
        return

    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


def generate_all(rm: ResourceManager):
    assets.generate(rm)
    advancements.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()
