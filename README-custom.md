# Story

Durant les events où KAYLENE propose sa solution KayleneWifi, un portail actif de connection permet au public présent lors de l’évènement d’accèder à internet via le wifi « KAYLENE-Wifi ». Ce portail s’affiche dès que l’utilisateur choisit de joindre le wifi « KAYLENE-Wifi ». Une utilisation gratuite pendant x minutes ou un achat de pass sont proposés sur le portail captif, et permettent de bénéficier de l’internet lors de l’event.

Un charriot wifi, brandé aux couleurs de KAYLENE, est installé sur le site de l’event et une ressource de KAYLENE présente durant l’event est chargée de vendre les pass aux personnes désirant bénéficier de l’internet sur site.
L’internet est fourni soit par le partenaire opérateur de KAYLENE, soit par une connection 4G (modem) achetée par KAYLENE.

La page web kaylenewifi.supervision247.com est la solution technique permettant :
- d’envoyer le code d’accés au client une fois qu’il a acheté un pass
- de stocker le numéro de téléphone du client afin de constituer une BDD que KAYLENE pourra monétiser auprès des organisateurs d’event

La page web (voir storyboard) permet de saisir les informations suivantes:
- le nom de l’event: champ texte
- le numéro de téléphone du client qui achète le pass: numéro mobile SN
- le code pass: champ texte

Une fois ces numéros saisis, la ressource KAYLENE click sur le bouton « Envoyer code » et les vérifications suivantes sont effectuées:
- les 3 champs à remplir sont saisis et sont non vides
- le champ tél correspond au format d’un numéro de tel SN
- le code saisi n’a pas encore été utilisé

Une fois ces vérifications effectuées avec succès:
- un SMS est envoyé au client avec le contenu suivant « Bonjour, votre code d’accès au WIFI-KAYLENE est le : <code_pass> »
- le triplet Event_Name/Phone_ID/Pass_ID est horodaté et enregistré dans la BDD
- les champs "Numéro de téléphone" et "Code de connexion" sont RAZ