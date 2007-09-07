#!/bin/sh

export LD_LIBRARY_PATH=/usr/lib:./lib/
cp=$(echo "$1" | sed 's@ @:@g')

failures=""
passed=""
tests=""

function runTest() {
    echo "Running test $i"
    testClass=$(echo "$1" | sed 's@/@.@g' | sed 's@.java$@@g')
    out=$(java -cp "$cp":dist/jOMiSCID.jar "${testClass}" 2>&1)
    echo "$out" | grep 'Test Failed' > /dev/null
    if test $? -eq 0
        then
        failures=$(echo "${failures}" ; echo "$1")
    fi
    echo "$out" | grep 'Test Passed' > /dev/null
    if test $? -eq 0
        then
        passed=$(echo "${passed}" ; echo "$1")
    fi
    tests=$(echo "${tests}" ; echo "$1")
}

for i in $(find test -name \*.java | sed 's@^test/@@g' | sort)
  do
  (grep '/\*- IGNORE -\*/' "test/$i" >/dev/null && echo "Skipping $i") || runTest "$i"
done

failuresCount=$(printf "%s"  "$failures" | wc -l)
passedCount=$(printf "%s"  "$passed" | wc -l)
testsCount=$(printf "%s" "$tests" | wc -l)

echo "Passed:   ${passedCount}/${testsCount}"
echo "Failures: ${failuresCount}/${testsCount}"
printf "%s\\n" "$failures" | egrep -v -e '^$' | sed 's@^@   @g'
