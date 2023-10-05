#!/usr/bin/env bash

set -o errexit
set -o nounset

echo -n "Username: "
read username

echo -n "Password: "
read -s password

export ORG_GRADLE_PROJECT_ossrhUsername="$username"
export ORG_GRADLE_PROJECT_ossrhPassword="$password"
./gradlew publish "$@"
