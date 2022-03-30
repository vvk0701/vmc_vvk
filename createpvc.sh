#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
ns=$1
for (( j=1; j<=225; j++ ))
do
    pvc_name="pvc${j}"
    template=`cat "pvc.yaml" |sed "s/{{pvc_name}}/$pvc_name/g" | sed "s/{{ns}}/$ns/g"`
    echo "$template" | kubectl apply -f -
    output=`kubectl get pvc`
    date_time=`date`
    sleep "30s"
done
