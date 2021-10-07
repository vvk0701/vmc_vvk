#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
for (( j=1; j<=50; j++ ))
do
tkg_name="tkg-cluster${j}"
if [ $j -le 3 ]
then
    tkg_ns="wcpns$j"
    template=`cat "gcm150.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='900'
elif [ $j -gt 3 ] && [ $j -lt 45 ]
then
    template=`cat "gcm2_alpha2.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='320'
else [ $j -ge 45 ]
    a=3
    a=$a+`expr 51 - $j`
    tkg_ns="wcpns$a"
    template=`cat "gcm4.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='420'
fi
echo "$template" | kubectl apply -f -
output=`kubectl get tkc`
date_time=`date`
sleep $sleep_var
done
