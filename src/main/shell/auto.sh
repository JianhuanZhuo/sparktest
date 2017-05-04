#!/bin/bash

echo good!

scp /home/tom/idea/sparktest/target/spark-test-1.0-SNAPSHOT.jar tom@k0:~
cp /home/tom/idea/sparktest/target/spark-test-1.0-SNAPSHOT.jar ~/
ssh tom@k0 sscp spark-test-1.0-SNAPSHOT.jar

echo everything looks good!