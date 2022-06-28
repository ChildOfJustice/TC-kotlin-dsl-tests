import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.exposeAwsCredentialsToEnvVars
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.s3Storage

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    buildType(RunAwsCliCommand)

    features {
        s3Storage {
            id = "PROJECT_EXT_2"
            bucketName = "teamcity-artifact-publishing-tests-artifact-storage-bucket"
            enablePresignedURLUpload = false
            forceVirtualHostAddressing = true
            multipartThreshold = "6MB"
            awsEnvironment = default {
                awsRegionName = "eu-central-1"
            }
            useDefaultCredentialProviderChain = true
        }
        awsConnection {
            id = "PROJECT_EXT_3"
            name = "Amazon Web Services"
            credentialsType = static {
                accessKeyId = "test"
                secretAccessKey = "credentialsJSON:c0e43a4c-97f1-4a51-86e6-9b35f51b02e1"
                regionName = "us-east-1"
                useSessionCredentials = true
                stsEndpoint = "https://sts.amazonaws.com"
            }
            param("aws.session.credentials_checkbox", "true")
        }
    }
}

object RunAwsCliCommand : BuildType({
    name = "RunAwsCliCommand"

    steps {
        script {
            name = "Run AWS CLI command"
            scriptContent = """
                echo "Trying to list S3 buckets..."
                aws s3 ls
                
                echo "Trying to list EC2 instances..."
                aws ec2 describe-instances
                
                echo "The session token is:"
                echo ${'$'}AWS_SESSION_TOKEN
                
                echo "The secret key is:"
                echo ${'$'}AWS_SECRET_ACCESS_KEY
            """.trimIndent()
        }
    }

    features {
        exposeAwsCredentialsToEnvVars {
            awsConnectionId = "PROJECT_EXT_3"
            sessionDuration = "60"
        }
    }
})
