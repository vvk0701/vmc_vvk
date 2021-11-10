export KUBECTL_VSPHERE_PASSWORD=$1;
LARGETKG="tkg-cluster50";
LARGETKGNS="wcpns6";
kubectl vsphere login --server=192.168.123.2 -u cloudadmin@vmc.local --insecure-skip-tls-verify > /dev/null;
kubectl config use-context "192.168.123.2";

echo "Number of NameSpaces: "
kubectl get ns | grep wcpns | wc -l;

echo "Number of TKC Clusters: "
kubectl get tkc -A | grep tkg | wc -l;

echo "Login to Large TKC Cluster"

kubectl vsphere login --server=192.168.123.2 -u cloudadmin@vmc.local --insecure-skip-tls-verify --tanzu-kubernetes-cluster-name $LARGETKG --tanzu-kubernetes-cluster-namespace $LARGETKGNS > /dev/null;

echo "Number of Running Nginx Pods: "
kubectl get pods -A | grep "nginx" | grep "Running" | wc -l;

echo "Number of Nginx pods in Pending state: "
kubectl get pods -A | grep "nginx" | grep "Pending" | wc -l;

kubectl vsphere login --server=192.168.123.2 -u cloudadmin@vmc.local --insecure-skip-tls-verify --tanzu-kubernetes-cluster-name tkg-cluster2 --tanzu-kubernetes-cluster-namespace wcpns2 > /dev/null 

echo "Number of PVC's in tkg-cluster6 of NS wcpns4"
kubectl get pvc -A | wc -l;
