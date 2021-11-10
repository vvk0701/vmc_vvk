def Kubectl_Password
def VC_IP
def no_of_ns
def no_of_tkg_clusters

pipeline {

    agent {
        label 'built-in'
    }


    environment {
        //defined credentials on jenkins master
        git_uuid = "vvk0701"
        branch = "main"
    }


    parameters {
        booleanParam(name: 'dryrun', defaultValue: true, description: 'Boolean flag for populating params')
	string(name: 'VC_IP', defaultValue: '', description: 'Enter VCenter IP, Also add jenkins IP to Firewall on vCenter before Invoking')
        string(name: 'Kubectl_Password', defaultValue: '', description: 'Enter Password from Cloudadmin user')
	string(name: 'no_of_ns', defaultValue: '10', description: 'Enter number of WCP Namespaces to be deployed, Max allowed is 10')
	string(name: 'no_of_tkg_clusters', defaultValue: '50', description: 'Enter number of tkg clusters to be created, Max allowed is 50')
        booleanParam(name: 'SKIP_NAMESPACE_CREATION', defaultValue: true, description: 'Boolean flag to skip NS Creation')
        booleanParam(name: 'SKIP_TKG_CREATION', defaultValue: false, description: 'Boolean flag skipping TKG Creation')
 	booleanParam(name: 'SKIP_POD_Scale', defaultValue: false, description: 'Boolean flag skipping Pod Scale')
        booleanParam(name: 'SKIP_PVC_Creation', defaultValue: false, description: 'Boolean flag for skipping PVC')
	booleanParam(name: 'SKIP_Scale_Report', defaultValue: false, description: 'Boolean flag for skipping Report')
	    
	
    }
    
    
    stages {
    
        stage("Parameterizing Pipeline") {
            steps {
                echo "Dry  Run - ${params.dryrun}"
                script {
                    if ("${params.dryrun}" == "true") {
                        currentBuild.result = 'ABORTED'
                        error('DRY RUN COMPLETED. JOB PARAMETERIZED.')
                    }
                }
            }
        }
	    

	    
	stage('WCP NS Creation'){
            steps{
                createNs(params.SKIP_NAMESPACE_CREATION, params.VC_IP, params.Kubectl_Password, params.no_of_ns)
            }
        }   
	
    
        stage('TKG Cluster Creation'){
            steps{
                tkgCreation(params.SKIP_TKG_CREATION, params.Kubectl_Password)
            }
        }
    
        stage('POD Scale') {
        	steps{
                parallel podScale: {
                    podScale(params.SKIP_POD_Scale, params.Kubectl_Password)
                },
                pvcScale : {
                    pvcScale(params.SKIP_POD_Scale, params.Kubectl_Password)
                }
        }
        }
	   
	stage('Generate TKG Scale Report') {
		steps{
			scaleReport(params.SKIP_Scale_Report, params.Kubectl_Password)
	}
	}
    }
}




def createNs(skip, VC_IP, Kubectl_Password, no_of_ns){
	if(!skip){
		try{
		build job: 'Create_WCP_NS', parameters: [string(name: 'server', value: VC_IP), string(name: 'no_of_ns', value: no_of_ns), string(name: 'Kubectl_Password', value: Kubectl_Password)]
		sleep (200)
		}
		catch(error){
			echo "Failed to Create NS" + error
			throw new Exception("WCP NameSpace Creation failed")
		}
	}
}

def tkgCreation(skip, Kubectl_Password){
    if(!skip){
        try{
        build job: 'Create_TKG_Cluster', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password)]
        sleep(600)
        }
        catch(error){
            echo "Failed to Create TKG Clusters" + error
	    throw new Exception("TKG Cluster creation failed, aborting here")
        }
    }
    }

def podScale(skip, Kubectl_Password){
    if(!skip){
        try{
        build job: 'Create_PodScale', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name:'tkgcluster', value: "tkg-cluster50"), string (name:'wcpns',value:"wcpns6")]
        sleep(1000)
        }
        catch(error){
            echo "Failed to Scale PODS on large TKG Cluster" + error
        }
    }
    }

def pvcScale(skip, Kubectl_Password){
    if(!skip){
        try{
        build job: 'Create_PVC', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name: 'tkg_cluster',value:"tkg-cluster2"), string(name: 'wcpns', value: "wcpns2")]
        sleep(1000)
        }
        catch(error){
            echo "Failed to Scale PVC" + error
        }
        
    }
    }
	
def scaleReport(skip, Kubectl_Password){
    if(!skip){
        try{
        build job: 'GetTKGScaleReport', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password)]
        }
        catch(error){
            echo "Failed to Generate Report for scale" + error
        }
        
    }
    }
