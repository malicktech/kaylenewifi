- Set up project folder
- Boostrap project
- Modelize & Generate Domain model
- Implement Audit event
- Implement sms business logic
- phone number validation
- unique event_code validation
- set up docker 
- set ci/cd in jenkins
- create database
- Deploy in rancher envs test & dév
- Set up design and access policy 
- Set up jhipster registry
- Set up jhipster console
- change admin password

- ajout info supllméntaire

- improve sms auth
 	- set up single ton restemplate
 	- 
https://www.baeldung.com/how-to-use-resttemplate-with-basic-authentication-in-spring

- audit
https://www.baeldung.com/database-auditing-jpa
simplify, just event
manual in record
remove history 



# Import JDL

	jhipster import-jdl domain-jdl.jh --force

# To generate the missing Docker image(s), please run:

in C:\DevLabs\KayleneLabs\kaylenewifi

	./mvnw package -Pprod jib:dockerBuild in C:\DevLabs\KayleneLabs\kaylenewifi
  
# You will need to push your image to a registry. to tag and push the images:

	docker tag kaylenewifi kaylene/kaylenewifi
	docker push kaylene/kaylenewifi