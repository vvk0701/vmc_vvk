def Kubectl_Password
def ns_count
def SV_Hostname

pipeline {

    agent {
        label 'built-in'
    }


    parameters {
        booleanParam(name: 'dryrun', defaultValue: true, description: 'Boolean flag for populating params')
	      string(name: 'SV_Hostname', defaultValue: '', description: 'Enter Supervisor Cluster Hostname')
        string(name: 'Kubectl_Password', defaultValue: '', description: 'Enter Password from Cloudadmin user')
	      string(name: 'ns_count', defaultValue: '10', description: 'Enter  WCP Namespace Where PVCs have to created')
		    	     
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
	    

	    
	stage('PVC Creation:'){
			    steps{
				createPvc(params.Kubectl_Password, params.ns_count, params.SV_Hostname)
			    }
		    }
		    } 
      
    }
               
  
  def createPvc(Kubectl_Password, ns_count, SV_Hostname){
	  	int count = ns_count.toInteger()
		try{
		for (int i=1; i<count; i++)
			{
			def wcpns='wcpns'+i
			build job: 'Create_PVC', parameters: [string(name: 'Kubectl_Password', value: Kubectl_Password), string(name: 'wcpns', value: wcpns), string(name: 'SV_Hostname', value: SV_Hostname)]
			sleep (60)
			}
		}
		catch(error){
			echo "Failed to PVCS" + error
			throw new Exception("PVC Scale Across Namespaces Failed")
		}
	}
	
