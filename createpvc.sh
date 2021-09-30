set -o errexit
set -o pipefail
set -o nounset
set -x
for (( j=1; j<=60; j++ ))
do
    pvc_name="pvc${j}"
    template=`cat "pvc.yaml" |sed "s/{{pvc_name}}/$pvc_name/g"`
    echo "$template" | kubectl apply -f -
    output=`kubectl get pvc`
    date_time=`date`
    sleep "1m"
done
