#!/bin/bash

JAR=`ls | grep -F 'money-keeper' | grep -F '.jar'`

java -jar ${JAR}