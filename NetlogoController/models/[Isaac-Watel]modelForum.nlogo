globals
[ 
  list-domain        ;; une liste de domaines pour les entreprises
  number-of-students ;; nombres d'Ã©tudiants
  nego-time-max      ;; Duree max de negociation pour un cv
  nego-time-min      ;; Duree min de negociation pour un cv
  wait-time-max      ;; Temps d'attente max a calibrer.
  wait-time-min      ;; Temps d'attente min a calibrer  
  max-stage-needed   ;; Nombre maximal de stage a pouvoir
  min-interviewers   ;; Nombre minimal d'intervenants
  min-stage-needed
  add-student-time   ;; Temps pour l'ajout d'un etudiant au cours de la simulation.
  time-max           ;; Temps que l'utilisateurs peux depenser dans le forum.
  time-min           ;; Temps minimal possible a passer dans le forum  
  move-noise         ;; Proba de rester bloqué lors d'un déplacement. 
  
                     ;; patch agentsets
  stands         ;; agentset contenant les intervenants des entreprises
  ways           ;; agentset contenant les chemins entre les stands.
  intersections  ;; agentset contenant l'ensemble des intersections des routes. 
]

patches-own      ;; attributs des intervenants des entreprises valables pour les autres 
                 ;; mais non utilises 
[                 
  domain                  ;; domaine de l'entreprise de l'intervenant
  famousness              ;; Degree de reputation de l'entreprise
  look                    ;; Apparence du stand. 
  stage-needed            ;; nombre de stage a pourvoir : permet de changer la proba d'acceptation d'un cv
  nbr-cv-rec              ;; nombre de cv recu
  nbr-interviewers        ;; nombre d'intervenants sur le stand.
  waiting-file            ;; file d'attente
  nbr-students            ;; Nombre d'etudiant venu sur le stand durant le forum.
  nbr-students-left-wait  ;; Nombre d'etudiant partis pour attente trop longue
  nbr-students-left-nego  ;; Nombre d'etudiant partis pour negociation trop longue
  attraction-distance
  size-stand;
  who-list
]

turtles-own
[
  nbr-cv            ;; le nombre de cv qu'il lui reste
  nbr-cv-init       ;; le nombre de cv initial.
  moving            ;; booleen qui est vrai si il bouge, pas dans un stand
  time              ;; le nombre de tour restant avant qu'il parte
  time-init         ;; Nombre donnee de tour initiale. 
  dom-interest      ;; Domaine qui l'interesse : pour le moment 1 seul Keep it Simple Stupid ! :D 
  list-company-size ;; Nombre d'entreprise qu'il est allÃ© voir (mÃªme si il est partie pendant l'attente).
  company           ;; cible d'entreprise sur le long terme represente par un patch company
  target-company    ;; cible d'entreprise sur le court terme represente par un patch company
  visited-companies ;; Entreprises deja visitees
  nego-time         ;; temps de negociation d'un cv.
  nego-time-total   ;; temps de negociation total. 
  nego-time-init    ;; temp de negociation initial d'un cv.
  target            ;; cible actuelle way pas loin du company : facilite le deplacement. 
  target_int        ;; intersection la plus proche de la cible : facilite le deplacement.
  wait-time         ;; temps d'attente en file d'attente.
  wait-time-total   ;; temps d'attente total.
  wait-time-init    ;; temps d'attente initial en file d'attente.
  
]

to setup
  init true;
end
to init [delete-files]
  clear-all      ;; clear the world
  setup-globals delete-files
  setup-patches
  
  set number-of-students floor (count stands * students-companies-ratio)  
  crt number-of-students   ;; on cree le nombre d'eleves determines par le slider
  ask turtles [setup-turtles]
  update-plot
  
end

to setup-globals [delete-files]
  ;;set grid-x-inc world-width / grid-x
  ;;set grid-y-inc world-height / grid-y
  set list-domain ["Informatique" "Energie" "Telecomunication" "Micro & Electronique" "Automatique" "Finance"]
  reset-ticks
  
  
  ;; Parametres a calibrer : 
  
  set time-max 8 * 60
  set time-min 0
  set nego-time-max floor (time-max * 0.125)
  set nego-time-min floor (time-max * 0.01)
  set wait-time-max floor (time-max * 0.095) 
  set wait-time-min 0
  set min-stage-needed 5
  set min-interviewers 2
  set add-student-time 20
  set move-noise 1
  
  ;; Gestion des fichiers : 
  
  ;; Elimination des fichiers de log deja cree 
  if delete-files
  [
    if file-exists? "students-report"
    [file-delete "students-report"]
    if file-exists? "companies-report"
    [file-delete "companies-report"]
  ]
  
  ;; Creation d'un legende :
  file-open "companies-report"
  file-type "# pxcor pycor domain nbr-cv-rec nbr-interviewers stage-needed famousness look nbr-students left-wait left-nego \n"
  file-close
  file-open "students-report"
  
  file-type "# who domain nbr-cv nbr-cv-init time time-init list-company-size nego-time-init nego-time-total wait-time-init wait-time-total\n"
  file-close  
end

;to test-companies-compacting
;  ;; Elimination des fichiers de log deja cree 
;  
;  if file-exists? "test-company-compacting"
;    [
;      file-open "test-company-compacting"
;      file-close
;      file-delete "test-company-compacting"
;    ]
;  set companies-compacting 1
;  let i 0
;  while [companies-compacting <= 50]
;  [ 
;    while [i <= 10]
;    [
;      setup-patches
;      
;      let test-nbr-neighb []
;      ask stands [set test-nbr-neighb fput count neighbors with [member? self stands] test-nbr-neighb]
;      file-open "test-company-compacting"
;      file-type companies-compacting
;      file-type " "
;      file-type mean test-nbr-neighb
;      file-type " "
;      file-type count stands / count patches
;      file-type "\n"
;      file-close
;      set i i + 1
;    ]
;    set i 0
;    set companies-compacting companies-compacting + 1
;    show companies-compacting
;  ]
;end
;
;to test-domain-compacting
;  ;; Elimination des fichiers de log deja cree 
;  
;  if file-exists? "test-domain-compacting"
;    [
;      file-open "test-domain-compacting"
;      file-close
;      file-delete "test-domain-compacting"
;    ]
;  
;  set domain-compacting 1
;  let i 0
;  while [domain-compacting <= 5]
;  [ 
;    while [i <= 10]
;    [
;      setup-patches
;      let test-nbr-neighb []
;      ask stands [set test-nbr-neighb fput ([sum map [neighbor-same-domain domain item 0 ? item 1 ?] [[-1 0] [0 1] [1 0] [0 -1] [-1 1] [1 1] [1 -1] [-1 -1]]] of self ) test-nbr-neighb]
;      file-open "test-domain-compacting"
;      file-type domain-compacting
;      file-type " "
;      file-type mean test-nbr-neighb
;      file-type "\n"
;      file-close
;      set i i + 1
;    ]
;    set i 0
;    set domain-compacting domain-compacting + 1
;    show domain-compacting
;  ]
;end
;
;to test-random
;  let i 0
;  let l1 []
;  let l2 []
;  let l3 []
;    let l4 []
;  while [i < 1000000]
;  [
;    set l1 fput random 100 l1
;    set l2 fput random-float 1 l2
;    set l4 fput random-normal 0 1 l4
;    set i i + 1 
;  ]
;    set-current-plot "Random 100"  
;  histogram l1
;  
;  foreach l2 [set l3 fput (floor 100 * ?) l3]
;    set-current-plot "Randomf 1"  
;  histogram l3
;  
;  set-current-plot "RandomN 0 1"
;  histogram l4
;  
;end
;
;
;to test-validation-interne
;  let stage-cv-ratio []
;  set companies-compacting 0
;  set domain-compacting 1
;  set students-companies-ratio 0.3
;  
;  while [companies-compacting <= 100]
;  [
;    show companies-compacting
;    while [domain-compacting <= 5]
;    [
;      while [students-companies-ratio <= 3]
;      [
;        let i 0
;        while [i < 20]
;        [
;          setup false
;          let test-boolean true
;          while [test-boolean][  
;            set move-noise  floor (4 * count turtles / count ways)
;            
;            if not any? turtles 
;            [
;              while [not empty? list-domain]
;              [ 
;                let d first list-domain
;                let stds stands with [domain = d]
;                ask stds [
;                  
;                ] 
;                ask stds [ print-company]
;                set list-domain remove d list-domain
;              ]
;              set test-boolean false
;            ]
;            
;            if ticks = add-student-time
;            [
;              crt 1
;              reset-ticks
;            ]
;            
;            ask turtles[studentjobs]
;            ask stands[standjobs]
;            update-plot
;          ]
;          set stage-cv-ratio []
;          ask stands[set stage-cv-ratio fput (nbr-cv-rec / stage-needed) stage-cv-ratio]
;          set i i + 1
;          file-open "validation-interne"
;          file-type i file-type " "
;          file-type companies-compacting file-type " "
;          file-type domain-compacting file-type " "
;          file-type students-companies-ratio file-type " "
;          file-type mean stage-cv-ratio file-type "\n"
;          file-close
;        ]
;        set students-companies-ratio students-companies-ratio + 0.3
;      ]
;      set students-companies-ratio 0.3
;      set domain-compacting domain-compacting + 1
;    ]
;    set students-companies-ratio 0.3
;    set domain-compacting 1
;    set companies-compacting companies-compacting + 20
;  ]
;end


to-report neighbor-same-domain [dom dirx diry]
  let pat nobody
  set pat patch-at dirx diry
  
  
  ifelse (pat != nobody)[
    ifelse member? pat stands
      [
        ifelse ([domain] of pat = dom) [report 1][report 0]
      ]
      [
        ifelse (dirx = 0 or diry = 0)
        [
          report [neighbor-same-domain dom dirx diry] of pat
        ]
        [
          let report-1 [neighbor-same-domain dom dirx 0] of pat
          ifelse (report-1 = 0)[report [neighbor-same-domain dom 0 diry] of pat][report report-1]
        ]
        
      ] 
  ]
  [report 0]
end

to setup-patches
  ask patches
  [
    set pcolor red
  ]
  
  let x-list []
  let x-ways 0
  while [x-ways < world-width]
  [
    set x-list lput x-ways x-list
    set x-ways x-ways + random 3 + 1 ;* companies-compacting / 100 + 1 
  ]
  
  let y-list []
  let y-ways 0
  while [y-ways < world-height]
  [
    set y-list lput y-ways y-list
    set y-ways y-ways + random (floor (companies-compacting * world-height / 400) + 2) + 1
  ]
  
  set ways patches with
  [
    member? floor (pxcor + world-width / 2) x-list or member? floor (pycor + world-height / 2) y-list
  ]
  
  set intersections ways with
  [ 
    member? floor (pxcor + world-width / 2) x-list and member? floor (pycor + world-height / 2) y-list
  ]
  
  ;; Creation des stands pour qu'ils soient plus ou moins nombreux en fonction
  ;; de la grille, pas moyen de faire avec une boucle pour reduire le code ...
  ;; mais c'est utilise qu"une fois au debut donc ca va.
  
  set stands patches with                    
  [
    not member? floor (pxcor + world-width / 2) x-list and not member? floor (pycor + world-height / 2) y-list
  ]
  
  ask ways [setup-ways]
  ask intersections [setup-intersections] 
  ask stands [set domain ""]
  ask stands [setup-stands]
  let att-dist-list [] ask stands [set att-dist-list fput attraction-distance att-dist-list]
  let mean-att-dist-list mean att-dist-list
  ask stands [set attraction-distance attraction-distance / mean-att-dist-list]
end

to setup-domain-stands [dom dist]
  if (dom != "" and dist != 0)
  [
    set domain dom
    ask neighbors [setup-domain-stands dom dist - 1]
  ]
end


to setup-stands
  ;    let neighbors-domains []
  ;    ask stands in-radius (floor (sqrt (world-width * world-height) * domain-compacting * 2  / (100 * length list-domain)))  with [not empty? domain] [set neighbors-domains lput domain neighbors-domains]
  ;    let i 0
  ;    ifelse empty? neighbors-domains
  ;    [
  ;      set domain one-of list-domain  
  ;    ]
  ;    [
  ;      set domain one-of neighbors-domains
  ;    ]       
  ;    set pcolor position domain list-domain
  
  ;  if empty? domain
  ;  [
  ;    set domain one-of list-domain  
  ;    let dom domain
  ;    ask stands in-radius (floor (sqrt (world-width * world-height) * domain-compacting  / (100 * length list-domain)))  with [empty? domain] [set domain dom]
  ;  ]
  if empty? domain
  [
    set domain one-of list-domain  
    let dom domain
    let dist (floor (sqrt (world-width * world-height) * ((domain-compacting - 1) * 10 + 15) * 2 / (100 * length list-domain)))
    ;let dist (floor (sqrt (world-width * world-height) * domain-compacting * 2 / (100 * length list-domain)))
    setup-domain-stands dom dist
  ]     
  
  
  
  set pcolor position domain list-domain 
  
  ;; La couleur et le domaine seront correle now. 
  set famousness random 101                              
  set look (1 + famousness / 10 + random-normal 0 1) / 10
  set size-stand (1 + famousness / 33 + random-normal 0 0.25) / 4
  set stage-needed floor (random-normal famousness famousness / 10)
  if (stage-needed < min-stage-needed)[set stage-needed min-stage-needed]
  
  set nbr-cv-rec 0
  set nbr-interviewers floor (random-normal (famousness / 10) (famousness / 20))
  if (nbr-interviewers < min-interviewers)[set nbr-interviewers min-interviewers]
  set waiting-file []
  set nbr-students 0
  set nbr-students-left-wait 0
  set nbr-students-left-nego 0   
  set attraction-distance size-stand * 57 / 100 + look * 43 / 100  
  if attraction-distance < 0 [set attraction-distance 0]                        
  set who-list []
  
  
  
end

to setup-ways
  set pcolor white
end

to setup-intersections
  ;;set pcolor white
end

to setup-turtles
  set nbr-cv-init floor (random-normal 12 2.5)
  if (nbr-cv-init < 0)[set nbr-cv-init 0]
  set nbr-cv nbr-cv-init
  set moving true
  set time-init 2 * floor((random-normal 85 70) / (8 * 60) * time-max)
  if (time-init < time-min) [set time-init time-min]
  if (time-init > time-max) [set time-init time-max]
  set time time-init
  set shape "person"
  set company nobody
  set target-company nobody
  set list-company-size 0
  set visited-companies []
  set nego-time floor ((random-normal 25 8)/(8 * 60) * time-max)
  if (nego-time < nego-time-min) [set nego-time nego-time-min]
  if (nego-time > nego-time-max) [set nego-time nego-time-max]
  set nego-time-init nego-time
  set nego-time-total 0
  set wait-time 2 * nego-time
  if (wait-time < wait-time-min) [set wait-time wait-time-min]
  if (wait-time > wait-time-max) [set wait-time wait-time-max]
  set wait-time-init wait-time
  set wait-time-total 0
  set dom-interest item random length list-domain list-domain
  let wayEmpty one-of ways 
  setxy [pxcor] of wayEmpty [pycor] of wayEmpty
  
  choose-target
end

to update
  go
  
end
to go
  
  set move-noise  floor (4 * count turtles / count ways)
  
  if not any? turtles 
    [
      while [not empty? list-domain]
      [ 
        let d first list-domain
        let stds stands with [domain = d]
        ask stds [
          
        ] 
        ask stds [ print-company]
        set list-domain remove d list-domain
      ]
      stop 
    ]
  
  if ticks = add-student-time
    [
      crt 1
      reset-ticks
    ]
  
  ask turtles[studentjobs]
  ask stands[standjobs]
  
  update-plot
  tick
end

to studentjobs
  
  if time <= 0 or nbr-cv <= 0                          ;; Je n'ai plus de but, je n'ai plus de raison de vivre. C'est radical ...
    [
      print-student
    ] 
  
  ifelse moving
    [ 
      
      move
      if distance target = 0  ;; il faudra gerer dans le else du if moving le contact avec un mec de la boite
        [ set moving false ]
      
      if target_int != nobody and distance target_int = 0
        [set target_int nobody] 
      
    ]
    [
      negociate  
    ]
  set time time - 1           
end

to move
  ifelse target_int = nobody
  [
    let t target
    let way min-one-of ways with [distance myself = 1] [distance t]
    if random move-noise = 0
    [ move-to way] 
  ]
  [  
    let t target_int
    let way min-one-of ways with [distance myself = 1] [distance t]
    if random move-noise = 0
    [ move-to way] 
  ]
end

to choose-target
  ;; Prise en compte ici de la famousness et des entreprises deja visites
  ;; Ensuite si toutes les boites du domaine d'interet on ete visite = random
  let di dom-interest
  let vc visited-companies
  
  let stds stands with [domain = di and not member? self vc]
  ifelse any? stds
    [
      let stand max-one-of stds [famousness]       ;; Choix de l'entreprise a aller voir : en fonction de la reputation (entre 1 et 10).
      set company stand
      update-target-company[company] of self
    ]
    [
      print-student
      die
    ]
end

to update-target-company [cpn]
  set target-company cpn
  let target_ min-one-of ways [distance cpn]                       ;; Trouver le patch way le plus proche permettant d'y acceder
  set target target_                                     
  set target_int min-one-of intersections [distance target_]       ;; Trouver le patch intersection le plus proche permettant d'y acceder
end


to negociate
  
  ;; Fonction de gestion de la negociation avec une entreprise.
  ;; Basiquement l'eleve donne un cv et discute en un temps donne.
  let finish false
  if not member? self [waiting-file] of target-company             ;; Debut de l'attente a un stand
  [
    ask target-company 
    [
      set waiting-file lput myself waiting-file
      set nbr-students nbr-students + 1 
    ]
    set list-company-size list-company-size + 1
    
  ]
  
  ifelse position self [waiting-file] of target-company < [nbr-interviewers] of target-company  ;; Si l'etudiant discute
  [ 
    set nego-time (nego-time - 1)
    if nego-time = 0
      [
        gived-cv
        set nego-time-total (nego-time-total + nego-time-init)
        ask target-company [ set nbr-students-left-nego nbr-students-left-nego + 1 ]
        set finish true     
      ]
  ]
  [
    set wait-time (wait-time - 1)
    
    if wait-time = 0
    [ 
      set finish true
      ask target-company [set nbr-students-left-wait nbr-students-left-wait + 1]
    ]
  ]
  
  if finish
  [
    set wait-time-total wait-time-total + (wait-time-init - wait-time)
    set moving true
    set nego-time floor((random-normal 25 8)/(8 * 60) * time-max)
    set nego-time-init nego-time
    set wait-time 2 * nego-time
    set wait-time-init wait-time
    set visited-companies fput target-company visited-companies
    
    ask target-company [set waiting-file remove myself waiting-file]
    
    ifelse company = target-company
      [choose-target]
      [update-target-company[company] of self]
    
  ]
  
end


to gived-cv
  if (random 100 < 50 + stage-needed / 2)
  [
    ;;show ["Remise d'un cv"]
    set nbr-cv (nbr-cv - 1)
    ask target-company [ 
      set nbr-cv-rec (nbr-cv-rec + 1) 
      set who-list fput [who] of myself who-list
    ] 
  ]
end

to standjobs
  let d domain
  ask turtles in-radius attraction-distance [
    if company = target-company and not (target-company = myself) and dom-interest = d and not member? myself visited-companies
    [
      let c myself
      update-target-company[c] of self
    ]
  ]
  
end


to update-plot
  set-current-plot "Nomber of students"  
  plot count turtles
  set-current-plot "CVs' number"
  let numberCV 0
  ask turtles 
  [
    set numberCV numberCV + nbr-cv
  ]
  plot numberCV
end

to print-student
  file-open "students-report"
  file-type who file-type " "
  file-type dom-interest file-type " "
  file-type nbr-cv file-type " "
  file-type nbr-cv-init file-type " "  
  file-type time file-type " "
  file-type time-init file-type " "
  file-type list-company-size file-type " "
  file-type nego-time-init file-type " "
  file-type nego-time-total file-type " "
  file-type wait-time-init file-type " "
  file-type wait-time-total file-type " "
  file-type "\n"
  file-close
end 

to print-company
  file-open "companies-report"
  file-type pxcor file-type " "
  file-type pycor file-type " "
  file-type domain file-type " "
  file-type nbr-cv-rec file-type " "
  file-type nbr-interviewers file-type " "
  file-type stage-needed file-type " "
  file-type famousness file-type " "
  file-type look file-type " "
  file-type nbr-students file-type " "
  file-type nbr-students-left-wait file-type " "
  file-type nbr-students-left-nego file-type " "
  file-type "\n"
  file-close
end 
@#$#@#$#@
GRAPHICS-WINDOW
310
92
650
453
10
10
15.7143
1
10
1
1
1
0
0
0
1
-10
10
-10
10
0
0
1
ticks

CC-WINDOW
5
543
659
638
Command Center
0

BUTTON
108
118
171
151
Go
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
23
118
95
151
Setup
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SLIDER
206
24
392
57
students-companies-ratio
students-companies-ratio
0.01
5
3
0.01
1
NIL
HORIZONTAL

PLOT
38
409
230
529
Nomber of students
NIL
NIL
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true

PLOT
41
258
244
399
CVs' number
NIL
NIL
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 0 -16777216 true
"turtle" 1.0 0 -11221820 true

SLIDER
22
24
208
57
companies-compacting
companies-compacting
1
100
23
1
1
NIL
HORIZONTAL

BUTTON
182
119
272
152
Go once
go
NIL
1
T
OBSERVER
NIL
G
NIL
NIL

SLIDER
26
66
198
99
domain-compacting
domain-compacting
1
5
1
1
1
NIL
HORIZONTAL

@#$#@#$#@
A QUOI SERT CE MODELE ? 
-----------
Ce modéle simule le fonctionnement d'un salon d'entreprise dans lequel des étudiants tentent de décrocher un stage auprés d'entreprises travaillant dans des domaines divers. Les étudiants tentent de donner un cv aux entreprises qui les interessent le plus rapidement possible afin de visiter le maximum d'entreprises dans un temps définis.  
Ce modèle à pour but de déterminer l'influence de la densité des stands et de la répartition des entreprises/domaines sur le nombre de cv donnés par les étudiants et sur le nombre de cv recus par chaque entreprise en fonction du nombre de stage à pourvoir qu'elle possède.

COMMENT FONCTIONNE T-IL ?
------------
Les étudiants sont représentés par des agents "personnes" de couleurs aléatoires.
Les stands sont représenté par des emplacement dont le couleurs dépend du domaine de l'entreprise. 

	Les agents sont initialisés et placés dans un environnement peuplé par un ensemble de stands dentreprises de domaines divers. Linitialisation des variables des agents est réalisée de façon aléatoire entre les valeurs min et max définis en variables globales (voir le code pour plus d'information)
	Un agent choisit un stand non visité pour laquelle le domaine est identique à son domaine dintérêt. Il le prend comme cible et se dirige vers lui (son déplacement est soumis à une probabilité représentant la densité dagent présent : voir code).
	Si au cours du trajet, il rentre dans la zone dattraction dune autre entreprise dont le domaine est également identique à son domaine dintérêt et dont il na pas déjà visité le stand, il garde en mémoire la première entreprise (comme entreprise à visiter plus tard) et se dirige vers la seconde. Sinon il continue jusquau stand de la première entreprise.
	On considère que létudiant est arrivé au stand sil est à une distance dune case du stand. Arrivé au stand, si un intervenant est disponible pour discuter avec lui, alors il se met à négocier avec lentreprise, sinon il se place dans la file dattente.
 	S'il attend, il le fait jusquà ce que ce soit son tour de discuter avec un intervenant, dans ce cas il négocie, ou jusquà ce que le temps dattente ait dépassé son temps dattente initial en file dattente, dans ce cas il sen va et ne reviendra pas sur le stand. 
	Sil négocie,à chaque tour, on décide par une fonction aléatoire définie dans la calibration si lélève donne son CV ou non à lentreprise. Si oui, il sen va et ne reviendra pas sur le stand, sinon il continue de négocier. Si le temps de négociation dépasse le temps de négociation initial, alors il ne remet pas son CV, il sen va et ne reviendra pas sur le stand. 
	Si cette entreprise était la cible première, on revient à létape 2, sinon on revient à létape 3.
	Les agents quittent le salon lorsque leur temps est écoulé ou lorsquils nont plus dentreprise à aller voir. 

COMMENT UTILISER CE MODELE ?
-------------
Classiquement le modèle présente des boutons SETUP pour initialiser la simulation, GO ONCE pour avancer d'un itération et GO pour lancer la simulation en mode continue.

Les différents sliders permettent de choisir certains paramètres de la simulation.
Les changements ne sont pris en compte qu'au travers du SETUP.

Voila leurs définitions : 

companies-compacting : Permet de gérer la densité de stands de la simulation. 
domain-compacting : Permet de gérer la densité de domaine de la simulation.
students-companies-ratio : Permet de gérer le ratio nombre d"étudiant / nombre de stands
negociation-time-max : Permet de définir le temps maximal de négociation supportable par les differents agents. 
time-wait-max : Permet de définir le temps maximal d'attente supportable par les differents agents.

REMARQUES
----------------
	Etant donnée le nombre de variables important du modèle, l'affichage des résultats et reduit au minimun dans netLogo et est déporté dans deux fichiers de statistiques : student-report et companies-report, utilisables pour afficher les résultats de la simulation grace à un tableur ou une logiciel de création de courbes comme gnuplot.
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
0
Rectangle -7500403 true true 151 225 180 285
Rectangle -7500403 true true 47 225 75 285
Rectangle -7500403 true true 15 75 210 225
Circle -7500403 true true 135 75 150
Circle -16777216 true false 165 76 116

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270

@#$#@#$#@
NetLogo 4.0.3
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 1.0 0.0
0.0 1 1.0 0.0
0.2 0 1.0 0.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$#@#$#@
