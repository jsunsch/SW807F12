#!/bin/sh

for i in {1..100}
do
   curl http://62.243.46.141:12345/CreatePOI/name$i/$i/$i/description$i
done

