#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
start=1
end=20
for (( j=$start; j<=$end; j++ ))
do
tkg_name="classy-cluster${j}"
if [ $j -le 5 ]
then
    tkg_ns="wcpns$j"
    template=`cat "test.yaml" | sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='420'
elif [ $j -gt 5 ] && [ $j -lt 10 ]
then
    tkg_ns="wcpns6"
    template=`cat "gcm2_alpha2.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='240'
elif [ $j -gt 9 ] && [ $j -lt 19 ]
then
    tkg_ns="wcpns4"
    template=`cat "gcm2_alpha2_g.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
    sleep_var='240'
else [ $j -ge 19 ]
    tkg_ns="wcpns3"
    template=`cat "gcm150.yaml" | sed "s/{{MY_NAME}}/$tkg_name/g" | sed "s/{{MY_NS}}/$tkg_ns/g"`
    sleep_var='900'
fi
echo "$template" | kubectl apply -f -
output=`kubectl get tkc`
date_time=`date`
sleep $sleep_var
done
