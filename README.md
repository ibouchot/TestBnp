## Installation et Déploiement

### Sans Docker :

```Installer Posgres et créer une base de donnée```
Changer les information de la database dans le properties

```Lancer l'application avec :```
java -jar bnp-api-test-0.0.1-SNAPSHOT.jar --spring.config.location=./application.yaml











### Avec Docker NON TESTE :
# Pour le déploiement, sur votre poste:
installer docker et wsl
# Installer les commandes make et zip
sudo apt update
sudo apt install make
sudo apt install zip -y

### 1. Cloner le Projet
### 2. Construire l'Image Docker
```Lancer à la racine du projet la commande suivante :```

```bash
docker-compose build
```

### 3. Démarrer l'Application en standalone
```Une fois l'image construite, démarrez l'application avec Docker Compose :```
```bash
docker-compose up -d
```
Cela démarrera l'application sur `http://127.0.0.1:8080`.