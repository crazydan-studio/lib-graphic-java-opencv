#!/bin/bash

DIR="$1"

if [ ! -d "${DIR}" ]; then
    echo "Usage: $0 /path/to/static/library/dir"
    exit 1
fi

pushd "${DIR}" >/dev/null
    for lib in `ls -1 *.a`; do
        name="$(echo ${lib} | sed 's/^lib//g; s/\.a$//g')"
        soname="lib${name}.so"

        echo "Convert ${lib} to ${soname} ..."
        mkdir "${name}"
        pushd "${name}" >/dev/null
            ar x "../$lib"
            gcc -shared -fPIC -Wl,--export-dynamic -o "../${soname}" *.o
        popd >/dev/null

        rm -rf "${name}"
    done
popd >/dev/null

echo "Done"
