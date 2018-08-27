def call(Map pipelineParams) {
    Map defaultValues  = readYaml text: libraryResource('default_pipeline_environment.yml')
                 
     pipelineParams.each { entry ->
                      defaultValues.put(entry.key,entry.value);
                  } 
                 
    pipeline {
        agent none
        
        options {
            authorizationMatrix(['com.cloudbees.jenkins.plugins.git.vmerge.CommitSpoolRepository.Pull:'+defaultValues.MatrixUser, 'com.cloudbees.jenkins.plugins.git.vmerge.CommitSpoolRepository.Pull:'+defaultValues.MatrixUser1, 'com.cloudbees.jenkins.plugins.git.vmerge.CommitSpoolRepository.Push:'+defaultValues.MatrixUser, 'com.cloudbees.jenkins.plugins.git.vmerge.CommitSpoolRepository.Push:'+defaultValues.MatrixUser1, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.Create:'+defaultValues.MatrixUser, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.Create:'+defaultValues.MatrixUser1, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.Delete:'+defaultValues.MatrixUser, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.Delete:'+defaultValues.MatrixUser1, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.View:'+defaultValues.MatrixUser, 'com.cloudbees.jenkins.plugins.securecopy.ExportsRootAction.View:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.credentials.CredentialsProvider.Create:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.credentials.CredentialsProvider.Create:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.credentials.CredentialsProvider.Delete:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.credentials.CredentialsProvider.Delete:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.credentials.CredentialsProvider.Update:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.credentials.CredentialsProvider.Update:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.credentials.CredentialsProvider.View:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.credentials.CredentialsProvider.View:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.deployer.DeployNowRunAction.Deploy:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.deployer.DeployNowRunAction.Deploy:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.deployer.DeployNowRunAction.JobCredentials:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.deployer.DeployNowRunAction.JobCredentials:'+defaultValues.MatrixUser1, 'com.cloudbees.plugins.deployer.DeployNowRunAction.UserCredentials:'+defaultValues.MatrixUser, 'com.cloudbees.plugins.deployer.DeployNowRunAction.UserCredentials:'+defaultValues.MatrixUser1, 'hudson.model.Item.Build:'+defaultValues.MatrixUser, 'hudson.model.Item.Build:'+defaultValues.MatrixUser1, 'hudson.model.Item.Cancel:'+defaultValues.MatrixUser, 'hudson.model.Item.Cancel:'+defaultValues.MatrixUser1, 'hudson.model.Item.Configure:'+defaultValues.MatrixUser, 'hudson.model.Item.Configure:'+defaultValues.MatrixUser1, 'hudson.model.Item.Delete:'+defaultValues.MatrixUser, 'hudson.model.Item.Delete:'+defaultValues.MatrixUser1, 'hudson.model.Item.Discover:'+defaultValues.MatrixUser, 'hudson.model.Item.Discover:'+defaultValues.MatrixUser1, 'hudson.model.Item.Move:'+defaultValues.MatrixUser, 'hudson.model.Item.Move:'+defaultValues.MatrixUser1, 'hudson.model.Item.Promote:'+defaultValues.MatrixUser, 'hudson.model.Item.Promote:'+defaultValues.MatrixUser1, 'hudson.model.Item.Read:'+defaultValues.MatrixUser, 'hudson.model.Item.Read:'+defaultValues.MatrixUser1, 'hudson.model.Item.Request:'+defaultValues.MatrixUser, 'hudson.model.Item.Request:'+defaultValues.MatrixUser1, 'hudson.model.Item.Workspace:'+defaultValues.MatrixUser, 'hudson.model.Item.Workspace:'+defaultValues.MatrixUser1, 'hudson.model.Run.Delete:'+defaultValues.MatrixUser, 'hudson.model.Run.Delete:'+defaultValues.MatrixUser1, 'hudson.model.Run.Replay:'+defaultValues.MatrixUser, 'hudson.model.Run.Replay:'+defaultValues.MatrixUser1, 'hudson.model.Run.Update:'+defaultValues.MatrixUser, 'hudson.model.Run.Update:'+defaultValues.MatrixUser1, 'hudson.plugins.promoted_builds.Promotion.Promote:'+defaultValues.MatrixUser, 'hudson.plugins.promoted_builds.Promotion.Promote:'+defaultValues.MatrixUser1, 'hudson.scm.SCM.Tag:'+defaultValues.MatrixUser, 'hudson.scm.SCM.Tag:'+defaultValues.MatrixUser1])
            buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: defaultValues.days_to_keep, numToKeepStr: defaultValues.builds_to_keep)
        }
        
        environment {
            BITBUCKET_COMMON_CREDS = credentials('2978ef9b-1138-47d4-9d84-2c040f863717')
            BUILD_NUM = "${env.BUILD_NUMBER}"
            
        }

        stages {
            
            stage('checkout git') {
                
                agent {
                label 'master'
            }
                tools {
                   jdk defaultValues.java_version
                   maven defaultValues.maven_version
                }
                steps {
                    timestamps {
                    sh 'java -version'
                    sh 'mvn -version'
                    git poll: true, branch: defaultValues.branch, credentialsId: '2978ef9b-1138-47d4-9d84-2c040f863717', url: defaultValues.scmUrl
                    
                }
            }
         }
            stage('build') {
                agent {
                label 'master'
            }
                   tools {
                   jdk defaultValues.java_version
            }
                steps {
                    timestamps {
                    sh 'java -version'
                    sh 'mvn -gs /abe/apache/maven/3.0.3/conf/settings_dev_blw_pipeline.xml clean install deploy'
                    
                }
            }
            }

            stage('Creating Owasp Report') {
                agent {
                label 'master'
            }
            steps {
                timestamps { 
                script{
                if(defaultValues.skip_owasp=='Y')
                  {
                     echo "SKIP OWASP STEP"
                  }
                  else {
                    try{
                        sh "${env.JENKINS_SCRIPTS}/owasp.sh 'OWASP Report' ${env.WORKSPACE}"
                        }catch(Exception e) {
                            echo "(Job ${env.JOB_NAME} has been failed, Please go to ${env.BUILD_URL}.), while project scan from OWASP."
                            sh 'exit 1'
                            }
                         }
                }
                    
                }
            }
        }
            stage('SonarQube analysis') {
                agent {
                    label 'master'
                }
            steps {
                timestamps {
                script {
                  if(defaultValues.skip_sonar=='Y')
                  {
                     echo "SKIP SONAR STEP"
                  }
                  else {
                  // requires SonarQube Scanner 2.8+
                  scannerHome = tool 'sonar-scanner-3.1'
                  withSonarQubeEnv('Sonar-DEV-5.4') {
                  sh "${scannerHome}/bin/sonar-scanner -e -Dsonar.projectKey=web -Dsonar.projectName=web -Dsonar.projectVersion=1.0 -Dsonar.sources=$WORKSPACE/src -Dsonar.projectBaseDir=$WORKSPACE"
                }
                }
            }
            }
            }
            }
            stage('Artifact Archive') {
                agent {
                    label 'master'
                }
                steps {
                    timestamps {
                     archiveArtifacts artifacts: 'target/*.war'
                }   
            }
            } 

            stage('Create Yaml') {
           steps {
            timestamps {
           node('master') {
               
             script{
                    try{
                        sh '$JENKINS_SCRIPTS/queuedeploy_bbk_blw.sh create pom.xml war $JOB_NAME $BUILD_NUMBER/jboss BLW'
                        variables = [ job: currentBuild.rawBuild.getFullDisplayName(),
                                      job_url: "${env.BUILD_URL}" ]
                        template = libraryResource('template.html')
                        report = helper.renderTemplate(template, variables)
                                    mail (mimeType: 'text/html',to: 'STIP-ABE-ADMIN@unionbank.com',
                                   subject: "Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) executed sucessfully",
                                   body: report)

                        }catch(Exception e) {
                            echo "(Job ${env.JOB_NAME} has been failed, Please go to ${env.BUILD_URL}.), while execute project from maven."
                            sh 'exit 1'
                            }
                         }
            }
            }
        }
        }

        
// TST

 stage('Deploy') {
           steps {
            timestamps {
            checkpoint 'Deploy into TST'
            node('master') {
          
             script{
                    try{
                      env.RELEASE_SCOPE = input message: 'User input required', ok: 'Deploy!',
                            parameters: [choice(name: 'Environment', choices: 'dev\ntst\npte', description: 'What is the release scope?')]
          
                        echo "${env.RELEASE_SCOPE}"   
                        sh "$JENKINS_SCRIPTS/queuedeploy_bbk_blw.sh queue $JOB_NAME ${BUILD_NUM}/jboss BLW ${env.RELEASE_SCOPE}/jboss"
                        variables = [ job: currentBuild.rawBuild.getFullDisplayName(),
                                      job_url: "${env.BUILD_URL}" ]
                        template = libraryResource('template.html')
                        report = helper.renderTemplate(template, variables)
                                    mail (mimeType: 'text/html',to: 'STIP-ABE-ADMIN@unionbank.com',
                                   subject: "Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) deployed sucessfully",
                                   body: report)
//add 
                        item = Jenkins.instance.getItemByFullName("$JOB_NAME")
                         
                        item.builds.each() { build ->
                          
                          if (build.getNumber() == Integer.valueOf(BUILD_NUM))
                          {
                            //build.setDisplayName("Development build by Pankaj#${build.getNumber()}")
                            //build.description = build.description+" <a href='${env.BUILD_URL}'><img title='Promote to TST' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-orange.png'></a>"
                          if("${env.RELEASE_SCOPE}"=="tst")
                          {
                          if (build.description?.trim()) {
                                build.description = build.description+" <a href='${env.BUILD_URL}'><img title='Promote to TST' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-orange.png'></a>"
                              }
                              else
                              {
                                build.description = "<a href='${env.BUILD_URL}'><img title='Promote to TST' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-orange.png'></a>"
                              }
                            }

                            if("${env.RELEASE_SCOPE}"=="dev")
                          {
                          if (build.description?.trim()) {
                                build.description = build.description+" <a href='${env.BUILD_URL}'><img title='Promote to DEV' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-purple.png'></a>"
                              }
                              else
                              {
                                build.description = "<a href='${env.BUILD_URL}'><img title='Promote to DEV' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-purple.png'></a>"
                              }
                            }

                            if("${env.RELEASE_SCOPE}"=="pte")
                          {
                          if (build.description?.trim()) {
                                build.description = build.description+" <a href='${env.BUILD_URL}'><img title='Promote to PTE' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-red.png'></a>"
                              }
                              else
                              {
                                build.description = "<a href='${env.BUILD_URL}'><img title='Promote to PTE' height='16' width='16' src='/jenkins/static/13bdf8d5/plugin/promoted-builds/icons/16x16/star-red.png'></a>"
                              }
                            }
                            

                          }

                        }

                        

//com
                        }catch(Exception e) {
                            echo "(Job ${env.JOB_NAME} has been failed, Please go to ${env.BUILD_URL}.), while deploy project from maven."
                            sh 'exit 1'
                            }
                         }
            }
            }
        }
        }

// TST end            
        }
        
    }
}
