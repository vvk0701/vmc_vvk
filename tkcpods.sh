#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
for (( i=1; i<=250; i++ ))
do
echo "creating tkc namespace "
kubectl create ns tkc-$i
echo "create tkc pods"
kubectl apply -f replicaset.yaml -n tkc-$i
sleep 5
done
