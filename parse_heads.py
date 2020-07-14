#!/usr/bin/env python

import re
import sys
from uuid import UUID


def array_to_uuid(arr):
    arr = [i.to_bytes(4, byteorder='big', signed=True) for i in arr]
    raw_uuid = b''.join(arr)
    return UUID(bytes=raw_uuid)


TAG_PATTERN = re.compile(r'^\s*"tag":\s?"')
UUID_PATTERN = re.compile(r'Id:\[I;((?:-?\d{1,10},){3}-?\d{1,10})\]')
NAME_PATTERN = re.compile(r'Name:\\"([^\\]+)\\"')
TEXTURE_PATTERN = re.compile(r'Value:\\"([^\\]+)\\"')


if len(sys.argv) == 2:
    entity = sys.argv[1].split('.')[0]
    with open(sys.argv[1]) as f:
        for line in f:
            if TAG_PATTERN.match(line):
                uuid_match = UUID_PATTERN.search(line)
                uuid_arr = [int(i) for i in uuid_match.group(1).split(',')]
                uuid = array_to_uuid(uuid_arr)
                name = NAME_PATTERN.search(line).group(1)
                texture = TEXTURE_PATTERN.search(line).group(1)
                predicate = ''
                java_name = entity[0] + entity.title().replace('_', '')[1:]
                if entity == 'villager':
                    predicate = f", {java_name} -> {java_name}.getProfession() == Villager.Profession.{name.split()[0].upper()} "
                elif entity == 'zombie_villager':
                    predicate = f", {java_name} -> {java_name}.getVillagerProfession() == Villager.Profession.{name.split()[1].upper()}"

                print(f'public static Head<{entity.title().replace("_","")}> {name.upper().replace(" ","_")} = new Head<>(EntityType.{entity.upper()}, "{name}", "{str(uuid)}", "{texture}"{predicate});')
