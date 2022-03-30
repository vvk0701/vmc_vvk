def Kubectl_Password
def wcpns
def SV_Hostname

pipeline {

    agent {
        label 'built-in'
    }


    parameters {
        booleanParam(name: 'dryrun', defaultValue: true, description: 'Boolean flag for populating params')
	      string(name: 'SV_Hostname', defaultValue: '', description: 'Enter Supervisor Cluster Hostname')
        string(name: 'Kubectl_Password', defaultValue: '', description: 'Enter Password from Cloudadmin user')
	      string(name: 'wcpns', defaultValue: '10', description: 'Enter  WCP Namespace Where PVCs have to created'
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
                createPvc(params.Kubectl_Password, params.wcpns, params.SV_Hostname)
            }
        }  
      
    }
               
  
  def createPvc(Kubectl_Password, wcpns, SV_Hostname){
		try{
		build job: 'Create_PVC', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name: 'wcpns', value: wcpns), string(name: 'SV_Hostname', value: SV_Hostname)]
		sleep (60)
		}
		catch(error){
			echo "Failed to PVCS" + error
			throw new Exception("PVC Scale Across Namespaces Failed")
		}
	}
}
	
