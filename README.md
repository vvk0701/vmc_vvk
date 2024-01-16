# vmc_vvk


For Running iperf in TKG Node:
Steps to be followed:

1)Run PSP.yaml to set necessary Role Permissions
2)Create a NS, for ex: k create ns networktest
3)Copy iperftest.yaml to kubectl client, login to tkg for ex: kubectl vsphere login --server=k8s.Cluster-1.vcenter.sddc-35-166-245-14.vmwarevmc.com -u cloudadmin@vmc.local --tanzu-kubernetes-cluster-name tkg-cluster1 --tanzu-kubernetes-cluster-namespace wcpns1
4)k apply -f iperftest.yaml -n networktest
5)Loginto any of the iperf pod client, for ex: kubectl exec -n networkcheck iperf3-clients-74zqh -it /bin/bash
6)Run Command: iperf3 -c iperf3-server
