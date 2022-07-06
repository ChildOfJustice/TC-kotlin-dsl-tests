import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.exposeAwsCredentialsToEnvVars
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection

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

    buildType(TestBuildConfigWithExteralConnection)

    features {
        awsConnection {
            id = "PROJECT_EXT_9"
            name = "Amazon Web Services"
            regionName = "us-east-1"
            credentialsType = static {
                accessKeyId = "test"
                secretAccessKey = "credentialsJSON:3c5d8a97-09f1-4d00-8826-ccd8746a057b"
                useSessionCredentials = true
                stsEndpoint = "https://sts.us-east-1.amazonaws.com"
            }
            param("awsStsEndpointIamRole", "https://sts.us-east-1.amazonaws.com")
            param("awsIamRoleProjectId", "TestProject2")
            param("awsSessionDuration", "60")
            param("awsSessionCredentials_checkbox", "true")
            param("awsIamRoleSessionName", "TeamCity-session")
        }
    }
}

object TestBuildConfigWithExteralConnection : BuildType({
    name = "TestBuildConfigWithExteralConnection"

    steps {
        script {
            name = "Test external connection"
            scriptContent = "aws s3 ls"
        }
    }

    features {
        exposeAwsCredentialsToEnvVars {
            awsConnectionId = "PROJECT_EXT_5"
            sessionDuration = "60"
        }
    }
})
