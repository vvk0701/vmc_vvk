#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
for (( j=2; j<=50; j++ ))
do
tkg_name="tkg-cluster${j}"
if [ $j -lt 3 ]
then
    template=`cat "gcm150.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='300'
elif [ $j -ge 4 ] && [ $j -lt 45 ]
then
    template=`cat "gcm2_alpha2.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='60'
else [ $j -gt 45 ]
    a=`expr 51 - $j`
    tkg_ns="wcpns$a"
    template=`cat "gcm4.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='120'
fi
echo "$template" | kubectl apply -f -
output=`kubectl get tkc`
date_time=`date`
sleep $sleep_var
done
