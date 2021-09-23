set -o errexit
set -o pipefail
set -o nounset
set -x
tkg_name="tkg-1"
template=`cat "gcm2_alpha2.yaml" |sed "s/{{MY_NAME}}/$tkg_name/g"`
sleep_var='60'
echo "$template" | kubectl apply -f -
output=`kubectl get tkc`
date_time=`date`
sleep $sleep_var
done
