kubectl vsphere login --vsphere-username cloudadmin@vmc.local --server=k8s.Cluster-1.vcenter.sddc-44-226-126-127.vmwarevmc.com --tanzu-kubernetes-cluster-name $2  --tanzu-kubernetes-cluster-namespace $1; 

kubectl apply -f psp.yaml; kubectl create ns tkc1; sleep 5; kubectl apply -f deployment.yaml; sleep 5 ; kubectl apply -f service.yml ; sleep 8; kubectl get svc -n tkc1
