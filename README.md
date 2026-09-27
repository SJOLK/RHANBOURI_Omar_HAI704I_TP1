# Cabinet vétérinaire distribué avec Java RMI

**HAI704I — Architectures logicielles distribuées — TP1 — 2026–2027**

## Présentation

Cette application permet à plusieurs vétérinaires d’utiliser à distance un même cabinet vétérinaire.

Fonctionnalités :

- afficher et rechercher les patients ;
- enregistrer un nouveau patient ;
- consulter et modifier son dossier de suivi ;
- s’abonner et se désabonner des alertes ;
- notifier tous les clients abonnés lorsque le nombre de patients franchit les seuils 100, 500 ou 1000.

Les données sont conservées en mémoire par le serveur. Elles sont réinitialisées lorsque le serveur redémarre.

## Architecture

Le projet contient trois modules :

```text
common/
    Contrats distants et objets transmis par valeur

server/
    Implémentations distantes et lancement du serveur

client/
    Point d’entrée, logique cliente et interface console
```

Le module `client` dépend uniquement de `common`. Il ne connaît aucune classe d’implémentation du serveur.

### Organisation du client

```text
Client.java
    Connexion au registre RMI et lancement de l’application

LogiqueClient.java
    Appels aux interfaces distantes et gestion des erreurs

InterfaceConsole.java
    Menu, saisies et affichage

ObservateurCabinetImpl.java
    Réception des alertes envoyées par le serveur
```

## Prérequis

- JDK 17 minimum ;
- aucun `module-info.java` ;
- aucune dépendance externe ;
- port RMI utilisé par défaut : `1099`.

## Compilation en ligne de commande

Depuis la racine du projet.

### Windows PowerShell

```powershell
javac -d common\out (Get-ChildItem -Recurse -Filter *.java common\src).FullName

javac -cp common\out -d server\out (Get-ChildItem -Recurse -Filter *.java server\src).FullName

javac -cp common\out -d client\out (Get-ChildItem -Recurse -Filter *.java client\src).FullName
```

### Linux ou macOS

```bash
javac -d common/out $(find common/src -name "*.java")

javac -cp common/out -d server/out $(find server/src -name "*.java")

javac -cp common/out -d client/out $(find client/src -name "*.java")
```

## Exécution

Le serveur crée lui-même le registre RMI. Il n’est donc pas nécessaire de lancer `rmiregistry` séparément.

### Windows PowerShell

Lancer d’abord le serveur :

```powershell
java -cp "common\out;server\out" animal.server.Server
```

Dans un autre terminal, lancer un client :

```powershell
java -cp "common\out;client\out" animal.client.Client
```

Il est également possible de préciser l’hôte et le port :

```powershell
java -cp "common\out;client\out" animal.client.Client localhost 1099
```

Pour utiliser plusieurs clients, lancer la commande cliente dans plusieurs terminaux.

### Linux ou macOS

```bash
java -cp "common/out:server/out" animal.server.Server
```

Puis, dans un autre terminal :

```bash
java -cp "common/out:client/out" animal.client.Client localhost 1099
```

## Exécution dans IntelliJ IDEA

1. Vérifier que les modules `server` et `client` dépendent du module `common`.
2. Lancer `animal.server.Server`.
3. Lancer `animal.client.Client`.
4. Pour lancer plusieurs clients, activer **Allow multiple instances** dans la configuration du client.
5. Les arguments du client sont facultatifs. Leur format est :

```text
hôte port
```

Exemple :

```text
localhost 1099
```

## Menu du client

```text
1. Afficher les patients
2. Rechercher un patient
3. Enregistrer un patient
4. Consulter un dossier de suivi
5. Modifier un dossier de suivi
6. S'abonner aux alertes
7. Se désabonner des alertes
0. Quitter
```

Les saisies incorrectes et les erreurs de communication sont affichées avec un message compréhensible sans interrompre brutalement le client.

## Objets transmis

`Espece` est sérialisable et transmise par valeur. Le client reçoit donc une copie.

`IAnimal`, `IDossierSuivi`, `ICabinetVeterinaire` et `IObservateurCabinet` sont des interfaces distantes. Les processus communiquent avec les objets réels au moyen de stubs RMI.

Le cabinet est le seul objet directement publié dans le registre :

```text
Registry
    └── Cabinet
          └── Patients
                └── Dossiers de suivi
```

## Alertes

Chaque client peut exporter un observateur et transmettre son stub au serveur.

```text
Client ── opération distante ──> Serveur
Client <────── callback ──────── Serveur
```

Lorsqu’un seuil est franchi, le serveur appelle les observateurs abonnés. Un client devenu injoignable est retiré de la liste des abonnés sans arrêter le serveur.

Lorsqu’un client quitte, il se désabonne et désenregistre localement son observateur afin que la JVM puisse se terminer proprement.

## Expérience de sérialisation A7

Une sous-classe `Chien` placée uniquement dans le module client a d’abord provoqué :

```text
ServerException
└── UnmarshalException
    └── ClassNotFoundException: animal.client.Chien
```

Le flux sérialisé contient le nom de la classe et l’état de l’objet, mais pas le bytecode de la classe.

La correction consiste à placer `Chien` dans `common`, afin que sa définition soit disponible dans le classpath du client et du serveur.