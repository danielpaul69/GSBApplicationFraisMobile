# GSBApplicationFraisMobile

Projet android permettant la consultation des fiches de frais (application frais) entrant dans le cadre du BTS SIO.

Technologies utilisées :
- Base de données : MySQL
- Récupération des données  : PHP / Symfony
- Transmission des données : JSON
- Application mobile : Java / Android

## Captures
![connexion](https://cloud.githubusercontent.com/assets/9747815/25101905/65955d08-23b6-11e7-9198-536eb98609b3.PNG)
![fichefrais](https://cloud.githubusercontent.com/assets/9747815/25101907/659e55fc-23b6-11e7-8321-bb12e6175f20.PNG)
![detail](https://cloud.githubusercontent.com/assets/9747815/25101906/6597f2f2-23b6-11e7-9d11-68352de3ccb4.PNG)

## Configuration(routing en yml)
```yml
application_frais_api_connexion:
    path:     /api/connexion
    defaults: { _controller: ApplicationFraisBundle:Default:connexion }

application_frais_api_fichefrais:
    path:     /api/fichefrais/{idUtilisateur}
    defaults: { _controller: ApplicationFraisBundle:Default:ficheFrais }

application_frais_api_detail:
    path:     /api/detail/{idFicheFrais}
    defaults: { _controller: ApplicationFraisBundle:Default:detail }
```

## Exemples
### Connexion
#### Code PHP
```php
public function connexionAction(){
	if (isset($_GET['login']) && isset($_GET['mdp'])) {
		$em = $this->getDoctrine()->getManager();
		$visiteur = $em->getRepository('ApplicationFraisBundle:Visiteur')->findOneBy(array('login' => $_GET['login'], 'mdp' => $_GET['mdp']));

		if ($visiteur != null) {
			$response["success"] = 1;
			$response["message"] = "Connexion Ok";
			$response["utilisateur"] = array(
				'id' => $visiteur->getId(), 
				'login' => $visiteur->getLogin(),
				'nom' => $visiteur->getNom(),
				'prenom'  => $visiteur->getPrenom()
			);
		}else{
			$response["success"] = 0;
			$response["message"] = "Ces informations ne correspondent a aucun utilisateur";
		}
	}else{
		$response["success"] = 0;
		$response["message"] = "Vous devez saisir le nom d'utilisateur et le mot de passe";
	}

	return new JsonResponse($response);
}
```
#### Url
```rest 
http://localhost/gsbapplicationfrais/web/app_dev.php/api/connexion?login=test&mdp=test
```
#### Résultat
```json 
{"success":1,"message":"Connexion Ok","utilisateur":{"id":1,"login":"test","nom":"test","prenom":"test"}}
```


### Fiche frais
#### Code PHP
```php
public function ficheFraisAction($idUtilisateur){
	$em = $this->getDoctrine()->getManager();
	$ficheFrais = $em->getRepository('ApplicationFraisBundle:FicheFrais')->findByVisiteur($idUtilisateur);

	$response = array();
	foreach ($ficheFrais as $fiche) {
		$response[] = array(
			'id' => $fiche->getId(),
			'mois' => $fiche->getMois(),
			'annee' => $fiche->getAnnee()
		);
	}

	if (count($response) == 0) {
		$retour["success"] = 0;
		$retour["message"] = "Aucun resultat";
	}else{
		$retour["success"] = 1;
		$retour["message"] = count($response)." resultat(s)";
		$retour["fiches_frais"] = $response;
	}

	return new JsonResponse($retour);
}
```
#### Url
```rest 
http://localhost/gsbapplicationfrais/web/app_dev.php/api/fichefrais/1
```
#### Résultat
```json 
{"success":1,"message":"3 resultat(s)","fiches_frais":[{"id":1,"mois":5,"annee":2017},{"id":2,"mois":2,"annee":2017},{"id":3,"mois":4,"annee":2017}]}
```


### Detail
#### Code PHP
```php
public function detailAction($idFicheFrais){
	$em = $this->getDoctrine()->getManager();
	$lignesF = $em->getRepository('ApplicationFraisBundle:LigneFraisForfait')->findBy(array('fichefrais' => $idFicheFrais));
	$lignesHF = $em->getRepository('ApplicationFraisBundle:LigneFraisHorsForfait')->findBy(array('fichefrais' => $idFicheFrais));

	$ligneFraisHF = array();
	foreach ($lignesHF as $ligne) {
		$ligneFraisHF[] = array(
			'id' => $ligne->getId(),
			'libelle' => $ligne->getLibelle(),
			'montant' => $ligne->getMontant()
		);
	}

	$ligneFraisF = array();
	foreach ($lignesF as $ligne) {
		$ligneFraisF[] = array(
			'id' => $ligne->getId(),
			'fraisForfait' => $ligne->getFraisForfait()->getLibelle(),
			'montant' => $ligne->getQuantite() * $ligne->getFraisForfait()->getMontant()
		);
	}

	if (count($ligneFraisHF) == 0) {
		$retour["success"] = 0;
		$retour["message"] = "Aucun resultat";
	}else{
		$retour["success"] = 1;
		$retour["message"] = (count($ligneFraisHF) + count($ligneFraisF))." resultat(s)";
		$retour["ligneFraisForfait"] = $ligneFraisF;
		$retour["ligneFraisHorsForfait"] = $ligneFraisHF;
	}

	return new JsonResponse($retour);
}
```
#### Url
```rest 
http://localhost/gsbapplicationfrais/web/app_dev.php/api/detail/1
```
#### Résultat
```json 
{"success":1,"message":"8 resultat(s)","ligneFraisForfait":[{"id":1,"fraisForfait":"test","montant":180},{"id":2,"fraisForfait":"test2","montant":120}],"ligneFraisHorsForfait":[{"id":1,"libelle":"test","montant":50},{"id":2,"libelle":"test2","montant":45},{"id":3,"libelle":"detail","montant":39},{"id":4,"libelle":"ligne","montant":324},{"id":5,"libelle":"ligne2","montant":50},{"id":6,"libelle":"detail2","montant":54}]}
```
