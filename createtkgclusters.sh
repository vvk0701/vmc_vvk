#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
for (( j=5; j<=9; j++ ))
do
tkg_name="tkg-cluster${j}"
if [ $j -le 5 ]
then
    tkg_ns="wcpns$j"
    template=`cat "gcm46.yaml" | sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='420'
elif [ $j -gt 5 ] && [ $j -lt 25 ]
then
    template=`cat "gcm2_alpha2.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='320'
elif [ $j -gt 24 ] && [ $j -lt 47 ]
then
    template=`cat "gcm2_alpha2_g.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='320'
else [ $j -ge 47 ]
    a=`expr 56 - $j`
    tkg_ns="wcpns$a"
    template=`cat "gcm150.yaml" | sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='900'
fi
echo "$template" | kubectl apply -f -
output=`kubectl get tkc`
date_time=`date`
sleep $sleep_var
done
