#!/usr/bin/env python

# Usage: ./parse_heads.py data/minecraft/loot_tables/entities

import re
import sys
from uuid import UUID
import pathlib
from typing import Union
from collections import namedtuple

Chances = namedtuple('Chances', ['chance', 'looting_multiplier'])

TAG_PATTERN = re.compile(r'^\s*"tag":\s?"')
UUID_PATTERN = re.compile(r'Id:\[I;((?:-?\d{1,10},){3}-?\d{1,10})\]')
NAME_PATTERN = re.compile(r'Name:\\"([^\\]+)\\"')
TEXTURE_PATTERN = re.compile(r'Value:\\"([^\\]+)\\"')


def array_to_uuid(arr):
    arr = [i.to_bytes(4, byteorder='big', signed=True) for i in arr]
    raw_uuid = b''.join(arr)
    return UUID(bytes=raw_uuid)

def to_java_var_name(entity_name: str) -> str:
    return entity_name[0] + entity_name.title().replace('_', '')[1:]

def rchop(s, suffix):
    if suffix and s.endswith(suffix):
        return s[:-len(suffix)]
    return s


def parse_file(path: Union[str, pathlib.Path]):
    file_path = pathlib.Path(path)
    entity = file_path.stem
    with file_path.open() as f:
        for line in f:
            if TAG_PATTERN.match(line):
                uuid_match = UUID_PATTERN.search(line)
                uuid_arr = [int(i) for i in uuid_match.group(1).split(',')]
                uuid = array_to_uuid(uuid_arr)
                name = NAME_PATTERN.search(line).group(1)
                texture = TEXTURE_PATTERN.search(line).group(1)
                predicate = ''
                java_name = to_java_var_name(entity)
                chances = None
                if entity == 'villager':
                    predicate = f", {java_name} -> {java_name}.getProfession() == Villager.Profession.{name.split()[0].upper()} "
                elif entity == 'zombie_villager':
                    predicate = f", {java_name} -> {java_name}.getVillagerProfession() == Villager.Profession.{name.split()[1].upper()}"
                elif file_path.parent.stem == 'sheep':
                    sheep_type = entity
                    java_name = entity = 'sheep'
                    if sheep_type == 'jeb_sheep':
                        predicate = f', {java_name} -> "jeb_".equals({java_name}.getCustomName())'
                        chances = Chances(0.10, 0.05)
                    else:
                        predicate = f', {java_name} -> !"jeb_".equals({java_name}.getCustomName()) && {java_name}.getColor() == DyeColor.{rchop(name, " Sheep").upper().replace(" ", "_")}'
                        chances = Chances(0.0175, 0.0025)

                print(f'    public static Head<{entity.title().replace("_","")}> {name.upper().replace(" ","_")} = new Head<>(EntityType.{entity.upper()}, "{name}", "{str(uuid)}", "{texture}"{predicate}{( ", " + ", ".join(str(c) for c in chances)) if chances else ""});')

if len(sys.argv) >= 2:
    for path in sys.argv[1:]:
        path = pathlib.Path(path)
        if path.is_file():
            parse_file(path)
        elif path.is_dir():
            for path in path.iterdir():
                if path.is_file():
                    parse_file(path)
                else:
                    print(f'Not a file: {path}')
                    sys.exit(1)

        else:
            print(f'Unknown type for path: {path}')
            sys.exit(1)

