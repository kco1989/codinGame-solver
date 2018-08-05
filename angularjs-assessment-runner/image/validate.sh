#!/bin/bash
cd /project/target
node lib/select-unit-test.js "$1"
npm --silent test
node lib/validate-serializer.js "$1"
