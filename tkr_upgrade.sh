#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset
set -x
tkg_version="v1.21.6---vmware.1-tkg.1.7fb9067"
template=`cat "tkg_upgrade.yaml" |sed "s/{{TKG_VERSION}}/$tkg_version/g"`
sleep_var='60'
echo "Upgrading TKG"
echo "$template" | kubectl apply -f -
sleep $sleep_var
done
