# IsomEdit

Le projet IsomEdit est un de mes plus récents projets, débuté en 2020 en fin de lycée.

Il avait pour but initial de servir d'éditeur de terrain/carte personnel au vu de la création d'un jeu vidéo en 2-Dimension en vue isométrique.
L'éditeur le plus connu ouvert au public, Tiled, ne convenait pas entièrement à mes besoins j'ai donc décidé de refaire un logiciel à mes besoins spécifiques.

L'éditeur n'est pas fini et la dernière version mise à jour date de fin 2020 après avoir commencé ma MPSI.
Le logiciel est entièrement conçu en Java et reprend les principes de base de l'éditeur Tiled : placement de tiles (= sprite/cube isométrique composant le terrain) sur le terrain,
déplacement sur la carte, prise en charge de tileset (également spritesheet) qui est une image reprenant un ensemble de textures représentant tout l'ensemble des tiles,
gestion de calques sur la carte, gestion de différentes cartes par projet et encore.

Les détails plus spécifiques sont les suivants :
  - découpage intelligent et automatique d'un tileset/spritesheet passé dans le logiciel, le découpage permet de séparer les tiles collés dans le fichier image par défaut pour raison d'efficacité.
  - propriétés spécifiques et personnalisables pouvant être assignées à chaque tile directement sur la carte et modifiables dynamiquement (par exemple la hauteur d'une tile sur la carte).
  - placement/utilisation d'images haute qualité redimensionnées à l'échelle de la carte en même temps que des tiles à dimension très inférieure, cela permet de donner un rendu au joueur de divers objets en haute définition et donc d'améliorer la qualité graphique du jeu. (Note : ce critère fut déterminant dans le choix de conception du logiciel par rapport à l'utlisation de Tiled qui ne permettait pas l'utilisation simultanée des tiles et images de manière aisée)
  - l'exportation de terrains/cartes adaptées selon une librairie Java personnelle utilisée dans la programmation du jeu, cela permet d'avoir plus de maniabilité sur les modifications.
  - Gestion de lumières et éventuellement simulation de collisions. (pris en compte par Tiled)
  - L'optimisation matérielle est à travailler.

Refaire la roue cependant n'est pas facile et ce projet est un de mes plus gros projets, j'y consacre beaucoup de temps à la recherche d'algorithmes permettant une utilisation plus fluide du programme et de détails facilitant l'utilisateur sans que ce dernier ne le remarque (notamment le découpage intelligent).

Le logiciel utilise les bibliothèques javax.swing et java.awt.
J'espère pouvoir finir ce projet d'ici 2023, pour ensuite entamer le projet de librairie graphique Java et de gestion de terrains en accord avec IsomEdit.

Démonstration d'utilisation avec un tileset (fichier image contenant les tiles (blocs)) pris sur internet et un terrain de petites dimensions :

![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_usage.gif)
 
 
Fenêtre par défaut :

![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_frame.PNG)
 

Exemple de terrain :

 ![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_example.PNG)
 
 
Création d'un nouveau terrain :

 ![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_new_map.PNG)
 

Création d'un nouveau tileset :

 ![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_new_tileset.PNG)
 
 
Import de terrain, tileset :

 ![alt text](https://github.com/zentsugo/IsomEdit/blob/main/isomedit_import.PNG)
