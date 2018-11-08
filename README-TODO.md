- Set up project folder
- Boostrap project
- Modelize & Generate Domain model
- Implement Audit event
- set up docker 
- set ci/cd in jenkins
- create database
- Deploy in rancher envs test & dév
- Set up design and access policy 
- Set up jhipster registry
- Set up jhipster console
- change admin password

# Import JDL

	jhipster import-jdl domain-jdl.jh --force

# To generate the missing Docker image(s), please run:

in C:\DevLabs\KayleneLabs\kaylenewifi

	./mvnw package -Pprod jib:dockerBuild in C:\DevLabs\KayleneLabs\kaylenewifi
  
# You will need to push your image to a registry. to tag and push the images:

	docker tag kaylenewifi kaylene/kaylenewifi
	docker push kaylene/kaylenewifi