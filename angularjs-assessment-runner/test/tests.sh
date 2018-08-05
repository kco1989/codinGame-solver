#!/bin/bash

echo "Starting IDE tests with a working answer..."
docker run -v "`pwd`/valid-answer:/project/work" codingame/angularjs-assessment-runner /bin/bash -c "cd /project/target && cp /project/work/* . && ./run.sh"

echo "Starting IDE tests with a non working answer..."
docker run -v "`pwd`/invalid-answer:/project/work" codingame/angularjs-assessment-runner /bin/bash -c "cd /project/target && cp /project/work/* . && ./run.sh"

echo "Running validator on 1 working test"
docker run -v "`pwd`/valid-answer:/project/work" codingame/angularjs-assessment-runner /bin/bash -c "cd /project/target && cp /project/work/* . && ./validate.sh 'should increase the remaining task count'; cat result.json"

echo "Running validator on 1 faling test"
docker run -v "`pwd`/invalid-answer:/project/work" codingame/angularjs-assessment-runner /bin/bash -c "cd /project/target && cp /project/work/* . && ./validate.sh 'should increase the remaining task count'; cat result.json"
