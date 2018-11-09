#!/usr/bin/env groovy

node {

    stage('checkout') {
        checkout scm
    }

    docker.image('jhipster/jhipster:v5.6.1').inside('-u root -e MAVEN_OPTS="-Duser.home=./"') {
        stage('check java') {
            sh "java -version"
        }

        stage('clean') {
            sh "chmod +x mvnw"
            sh "./mvnw clean"
        }

        stage('install tools') {
            sh "./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-npm -DnodeVersion=v10.13.0 -DnpmVersion=6.4.1"
        }

        stage('npm install') {
            sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm"
        }

        // stage('backend tests') {
        //     try {
        //         sh "./mvnw test"
        //     } catch(err) {
        //         throw err
        //     } finally {
        //         junit '**/target/surefire-reports/TEST-*.xml'
        //     }
        // }

        // stage('frontend tests') {
        //     try {
        //         sh "./mvnw com.github.eirslett:frontend-maven-plugin:npm -Dfrontend.npm.arguments='test -- -u'"
        //     } catch(err) {
        //         throw err
        //     } finally {
        //         junit '**/target/test-results/jest/TESTS-*.xml'
        //     }
        // }

        stage('packaging') {
            sh "./mvnw verify -Pprod -DskipTests"
            archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
        }
        
        stage('Directory Permission') {
		        sh "chmod 777 -R target"
        }
    }

        maintainer_name = "kaylene"
        container_name = "kaylenewifi"
        registry_url = "https://index.docker.io/v1/"
        docker_creds_id = "docker-login"

        def dockerImage
        stage('build docker') {
            sh "cp -R src/main/docker target/"
            sh "cp target/*.war target/docker/"
            dockerImage = docker.build("${maintainer_name}/${container_name}", 'target/docker')
        }

        stage('publish docker') {
            docker.withRegistry("${registry_url}", "${docker_creds_id}") {
                dockerImage.push 'test'
            }
        }
}
