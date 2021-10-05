def Kubectl_Password

pipeline {

    agent {
        label 'master'
    }

    environment {
        branch = "master"
    }

    parameters {
        booleanParam(name: 'dryrun', defaultValue: true, description: 'Boolean flag for populating params')
        string(name: 'Kubectl_Password', defaultValue: '', description: 'Enter Password from Cloudadmin user')
        booleanParam(name: 'SKIP_NAMESPACE_CREATION', defaultValue: true, description: 'Boolean flag to skip NS Creation')
        booleanParam(name: 'SKIP_TKG_CREATION', defaultValue: false, description: 'Boolean flag skipping TKG Creation')
 	booleanParam(name: 'SKIP_POD_Scale', defaultValue: false, description: 'Boolean flag skipping Pod Scale')
        booleanParam(name: 'SKIP_PVC_Creation', defaultValue: false, description: 'Boolean flag for skipping PVC')
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
        build job: 'Create_PodScale', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name:'tkgcluster', value: tkg-cluster1), string (name:'wcpns',value:wcpns1)]
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
        build job: 'Create_PVC', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name: 'tkg_cluster',value:pvc), string(name: 'wcpns', value: wcpns4)]
        sleep(1000)
        }
        catch(error){
            echo "Failed to Scale PVC" + error
        }
        
    }
    }
