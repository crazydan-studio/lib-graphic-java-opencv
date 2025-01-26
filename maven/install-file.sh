#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"

usage() {
    echo "Usage: $0 -g <group-id> -a <artifact-id> -v <version> -f <file>"
    echo "Note: Before applying the local repository,"
    echo "      you should run 'mvn install' first in the project's root directory"
}

while [[ -n $1 ]]; do
  case $1 in
    -g) shift; group_id=$1 ;;
    -a) shift; artifact_id=$1 ;;
    -v) shift; version=$1 ;;
    -f) shift; file="$1" ;;
  esac
  shift
done

if [[ "x${group_id}" = "x" || "x${artifact_id}" = "x" || "x${version}" = "x" || "x${file}" = "x" ]]; then
    usage
    exit 1
fi

# https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html
# http://roufid.com/3-ways-to-add-local-jar-to-maven-project/#3-_creating_a_different_local_maven_repository
mvn deploy:deploy-file \
        -Dfile="${file}" \
        -DgroupId=${group_id} \
        -DartifactId=${artifact_id} \
        -Dversion=${version} \
        -Dpackaging=jar \
        -Durl=file:"${DIR}/repo" \
        -DrepositoryId=local-repo \
        -DupdateReleaseInfo=true
