# SPRING-WEB-API_AUTH
La première et la plus importante : une API pour gérer l’authentification. Elle prendra en :
- entrée :
identifiant
password
- sortie en cas de 200 :
token
L’objectif de cette API sera de générer un token d’authentification (fait maison).
Dans une base seront stockés les identifiants et mots de passe (pas nécessaire de s’embêter
avec du cryptage, on veut faire simple).
Lors d’un appel à l’API avec ces informations, si le mot de passe correspond à l’identifiant, on
retourne un token auto-généré qu’on va stocker en base en se basant sur les informations
d’authentification.
L’objectif est de générer un token encrypté constitué de : username-date(YYYY/MM/DD)-heure
(HH:mm:ss). Par exemple :
Ce token sera valable une heure, et sa date d’expiration devra donc être enregistrée dans la base.
A chaque appel d’une autre API du système, ce token devra être passé à et vérifié par l’API
d’authentification. Si le token est toujours valide, l’API d’authentification valide l’appel et retourne
le username lié au token, puis met à jour la date d’expiration à [maintenant + une heure].
Si le token est expiré, l’API renvoie une erreur 401, et demande donc une nouvelle authentification
à l’utilisateur.
