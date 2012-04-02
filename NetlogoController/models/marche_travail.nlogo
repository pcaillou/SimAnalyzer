breed[travailleurs_etudiants etudiant]
breed[travailleurs_ambitieux ambitieux]
breed[travailleurs_raisonnables raisonnable]

breed[entreprises_pme pme]
breed[entreprises_publiques publique]
breed[entreprises_services service]

breed[emplois emploi]

travailleurs_etudiants-own[entreprise securite salaire hausse_salaire salaire_embauche limite_demission mobilite formation]
travailleurs_ambitieux-own[entreprise securite salaire hausse_salaire salaire_embauche limite_demission mobilite formation]
travailleurs_raisonnables-own[entreprise securite salaire hausse_salaire salaire_embauche limite_demission mobilite formation]

entreprises_pme-own[liste_offres_emplois entreprise_nombre_emplois liste_employes ]
entreprises_publiques-own[liste_offres_emplois entreprise_nombre_emplois liste_employes]
entreprises_services-own[liste_offres_emplois entreprise_nombre_emplois liste_employes]

emplois-own[emp_entreprise securite salaire formation liste_candidats]

globals [
  nombre_emplois
]

;--------------------------------------------------------------------------------------------------------
;---------------------------------------------Reporter---------------------------------------------------
;--------------------------------------------------------------------------------------------------------

to-report min_formation
  if formation_pme < formation_publique and formation_pme < formation_service[
    report formation_pme
  ] 
  if formation_publique < formation_service and formation_publique < formation_pme[
    report formation_publique
  ]
  if formation_service < formation_publique and formation_service < formation_pme[
    report formation_service
  ]
  report formation_pme
end
to-report random_quantite [nombre var]
  report floor (nombre + ((random ( 2 * var)) - var) / 100 * nombre )
end


to-report nombre_travailleurs
  report nombre_etudiants + nombre_ambitieux + nombre_raisonnables
end
to-report pme_offres_emplois
  report sum [length liste_offres_emplois] of entreprises_pme
end
to-report publiques_offres_emplois
  report sum [length liste_offres_emplois] of entreprises_publiques
end
to-report services_offres_emplois
  report sum [length liste_offres_emplois] of entreprises_services
end
to-report chomeurs_etudiants
  report count travailleurs_etudiants with [entreprise = nobody]
end
to-report chomeurs_ambitieux
  report count travailleurs_ambitieux with [entreprise = nobody]
end
to-report chomeurs_raisonnables
  report count travailleurs_raisonnables with [entreprise = nobody]
end
to-report nombre_candidatures_pme
  report sum [length liste_candidats] of emplois with [is-pme? emp_entreprise]
end
to-report nombre_candidatures_service
  report sum [length liste_candidats] of emplois with [is-service? emp_entreprise]
end
to-report nombre_candidatures_publique
  report sum [length liste_candidats] of emplois with [is-publique? emp_entreprise]
end
to-report salaire_moyen_etudiants
  report mean [salaire_embauche] of travailleurs_etudiants
end
to-report salaire_moyen_ambitieux
  report mean [salaire_embauche] of travailleurs_ambitieux
end
to-report salaire_moyen_raisonnables
  report mean [salaire_embauche] of travailleurs_raisonnables
end
to-report salaire_moyen_demande_etudiants
  report mean [salaire] of travailleurs_etudiants
end
to-report salaire_moyen_demande_ambitieux
  report mean [salaire] of travailleurs_ambitieux
end
to-report salaire_moyen_demande_raisonnables
  report mean [salaire] of travailleurs_raisonnables
end
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------


;--------------------------------------------------------------------------------------------------------
;----------------------------------------------- Plotting -----------------------------------------------
;--------------------------------------------------------------------------------------------------------
to update-plot
  set-current-plot "chomage"
  set-current-plot-pen "etudiants"
  plot chomeurs_etudiants
  set-current-plot-pen "ambitieux"
  plot chomeurs_ambitieux
  set-current-plot-pen "raisonnables"
  plot chomeurs_raisonnables
  
  set-current-plot "moyenne_salaires"
  set-current-plot-pen "etudiants"
  plot salaire_moyen_etudiants
  set-current-plot-pen "ambitieux"
  plot salaire_moyen_ambitieux
  set-current-plot-pen "raisonnables"
  plot salaire_moyen_raisonnables
  
  set-current-plot "moyenne_salaires_demande"
  set-current-plot-pen "etudiants"
  plot salaire_moyen_demande_etudiants
  set-current-plot-pen "ambitieux"
  plot salaire_moyen_demande_ambitieux
  set-current-plot-pen "raisonnables"
  plot salaire_moyen_demande_raisonnables
    
  set-current-plot "besoin_main_oeuvre"
  set-current-plot-pen "pme"
  plot pme_offres_emplois
  set-current-plot-pen "services"
  plot services_offres_emplois
  set-current-plot-pen "publiques"
  plot publiques_offres_emplois
end
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------


;--------------------------------------------------------------------------------------------------------
;---------------------------------------Creation des agents----------------------------------------------
;--------------------------------------------------------------------------------------------------------

;--------------------------
;------ Travailleurs ------
;--------------------------
to create_travailleurs
  create-travailleurs_etudiants nombre_etudiants[
    set shape "person"
    set color 15
    setxy random-xcor random-ycor
    set size 1
    ;parametre simulation
    set entreprise nobody
    set securite random_quantite securite_etudiants var_securite_etudiants
    set salaire random_quantite salaire_etudiants var_salaire_etudiants
    set mobilite random_quantite mobilite_etudiants var_mobilite_etudiants
    set formation random_quantite formation_etudiants var_formation_etudiants
    set hausse_salaire hausse_salaire_etudiants
    set limite_demission limite_demission_etudiants
  ]
  create-travailleurs_ambitieux nombre_ambitieux[
    set shape "person"
    set color 125
    setxy random-xcor random-ycor
    set size 1
    ;parametre simulation
    set entreprise nobody
    set securite random_quantite securite_ambitieux var_securite_ambitieux
    set salaire random_quantite salaire_ambitieux var_salaire_ambitieux
    set mobilite random_quantite mobilite_ambitieux var_mobilite_ambitieux
    set formation random_quantite formation_ambitieux var_formation_ambitieux
    set hausse_salaire hausse_salaire_ambitieux
    set limite_demission limite_demission_ambitieux
  ]
  create-travailleurs_raisonnables nombre_raisonnables[
    set shape "person"
    set color 65
    setxy random-xcor random-ycor
    set size 1
    ;parametre simulation
    set entreprise nobody
    set securite random_quantite securite_raisonnables var_securite_raisonnables
    set salaire random_quantite salaire_raisonnables var_salaire_raisonnables
    set mobilite random_quantite mobilite_raisonnables var_mobilite_raisonnables
    set formation random_quantite formation_raisonnables var_formation_raisonnables
    set hausse_salaire hausse_salaire_raisonnables
    set limite_demission limite_demission_raisonnables
  ]
end

;--------------------------
;------ Entreprises -------
;--------------------------
to create_entreprises
    create-entreprises_pme nombre_pme[
    set shape "house"
    set color 25
    setxy random-xcor random-ycor
    set size 1
    set entreprise_nombre_emplois random_quantite nombre_postes_pme var_nombre_postes_pme
    set nombre_emplois nombre_emplois + entreprise_nombre_emplois
    set liste_offres_emplois[]
    set liste_employes []
    
    hatch-emplois entreprise_nombre_emplois[
      set emp_entreprise myself
      set securite random_quantite securite_pme var_securite_pme
      set salaire random_quantite salaire_pme var_salaire_pme
      set liste_candidats []
      set hidden? true
      ask myself[
        set liste_offres_emplois lput myself liste_offres_emplois
      ]
    ]
     
  ]
  create-entreprises_publiques nombre_publique[
    set shape "house"
    set color 45
    setxy random-xcor random-ycor
    set size 1
    set entreprise_nombre_emplois random_quantite nombre_postes_publique var_nombre_postes_publique
    set nombre_emplois nombre_emplois + entreprise_nombre_emplois
    set liste_offres_emplois[]
    set liste_employes []
    
    hatch-emplois entreprise_nombre_emplois[
      set emp_entreprise myself
      set securite random_quantite securite_publique var_securite_publique
      set salaire random_quantite salaire_publique var_salaire_publique
      set liste_candidats []
      set hidden? true
      ask myself[
        set liste_offres_emplois lput myself liste_offres_emplois
      ]
    ]
  ]
  create-entreprises_services nombre_service[
    set shape "house"
    set color 35
    setxy random-xcor random-ycor
    set size 1
    set entreprise_nombre_emplois random_quantite nombre_postes_service var_nombre_postes_service
    set nombre_emplois nombre_emplois + entreprise_nombre_emplois
    set liste_offres_emplois[]
    set liste_employes []
    
    hatch-emplois entreprise_nombre_emplois[
      set emp_entreprise myself
      set securite random_quantite securite_service var_securite_service
      set salaire random_quantite salaire_service var_salaire_service
      set liste_candidats []
      set hidden? true
      ask myself[ ;myself=entreprise
        set liste_offres_emplois lput myself liste_offres_emplois ; myself=emploi
      ]
    ]
  ]
end
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------


;--------------------------------------------------------------------------------------------------------
;------------------------------------- Initialisation et démarrage --------------------------------------
;--------------------------------------------------------------------------------------------------------

to setup
  clear-all
  create_travailleurs
  create_entreprises

  ask patches [set pcolor white]
  
  update-plot
end

to start
  
  run_entreprises entreprises_pme prob_embauche_pme freq_ajustement_pme freq_embauche_pme nombre_postes_pme var_nombre_postes_pme securite_pme var_securite_pme salaire_pme var_salaire_pme formation_pme var_formation_pme pme_prefs
  run_entreprises entreprises_services prob_embauche_service freq_ajustement_service freq_embauche_service nombre_postes_service var_nombre_postes_service securite_service var_securite_service salaire_service var_salaire_service formation_service var_formation_service pme_prefs
  run_entreprises entreprises_publiques prob_embauche_publique freq_ajustement_publique freq_embauche_publique nombre_postes_publique var_nombre_postes_publique securite_publique var_securite_publique salaire_publique var_salaire_publique formation_publique var_formation_publique publique_prefs
  
  run_travailleurs travailleurs_etudiants
  run_travailleurs travailleurs_ambitieux
  run_travailleurs travailleurs_raisonnables
  tick
  
  update-plot
end

to run_entreprises [entreprises l_prob_embauche l_freq_ajustement l_freq_embauche l_nombre_postes l_var_nombre_postes l_securite l_var_securite l_salaire l_var_salaire l_formation l_var_formation l_prefs]
  if (ticks mod l_freq_ajustement) = 0 [
    ask entreprises[
      politique_emplois l_nombre_postes l_var_nombre_postes l_securite l_var_securite l_salaire l_var_salaire l_formation l_var_formation
    ]
  ]
  if (ticks mod l_freq_embauche) = 0 [
    ask entreprises[
      embauche l_prob_embauche l_prefs
    ]
  ]
end
to run_travailleurs [travailleurs]
  ask travailleurs[
    candidature
    move
  ]
end
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------


;--------------------------------------------------------------------------------------------------------
;------------------------------------- procedures des entreprises ----------------------------------------
;--------------------------------------------------------------------------------------------------------

to politique_emplois  [nombre_postes var_nombre_postes l_securite l_var_securite l_salaire l_var_salaire l_formation l_var_formation]
  
  set nombre_emplois nombre_emplois - entreprise_nombre_emplois
  set entreprise_nombre_emplois random_quantite nombre_postes var_nombre_postes
  set nombre_emplois nombre_emplois + entreprise_nombre_emplois
  
  ifelse entreprise_nombre_emplois < length liste_employes[;il faut licencier
    set liste_offres_emplois[]
    let n length liste_employes - entreprise_nombre_emplois
    repeat n [
      let a one-of liste_employes
      ask a[
        set entreprise nobody
      ]
      set liste_employes remove a liste_employes
    ]
  ][;il faut ajuster le nombre d'offre
    let n entreprise_nombre_emplois - length liste_employes - length liste_offres_emplois
    ifelse(n < 0)[
      repeat (- n) [    
        let ind random length liste_offres_emplois
        let emp one-of liste_offres_emplois
        set liste_offres_emplois remove emp liste_offres_emplois
        ask emp[die]
      ]
    ][;il y a moins d'offres d'emploi que d'emplois à satifaire-> creation d'emploi
      hatch-emplois n[
        set emp_entreprise myself
        set securite random_quantite l_securite l_var_securite
        set salaire random_quantite l_salaire l_var_salaire
        set formation random_quantite l_formation l_var_formation
        set liste_candidats []
        set hidden? true
        ask myself[
          set liste_offres_emplois lput myself liste_offres_emplois
        ]
      ]
    ] 
  ]
end

to embauche [prob_embauche l_prefs]
  foreach liste_offres_emplois [
    if (is-emploi? ? and length [liste_candidats] of ? > 0) [
      let l [liste_candidats] of ?
      ask ?[;?=>emploi
        set liste_candidats (filter [([entreprise] of ?) = nobody] liste_candidats)
      ]
      if length [liste_candidats] of ? > 0 [
        let meilleur_candidat nobody
        ifelse (l_prefs = "salaire" )[
          set meilleur_candidat first sort-by [ [salaire] of ?1 < [salaire] of ?2] [liste_candidats] of ?
        ][
        set meilleur_candidat first sort-by [ [formation] of ?1 < [formation] of ?2] [liste_candidats] of ?
        ]
        if((random (100 - prob_embauche)) = 0)[
          ask meilleur_candidat [
            set entreprise myself
            set salaire_embauche [salaire] of ?
            setxy [xcor] of myself [ycor] of myself
            rt random 360
            fd 1
          ]
          set liste_employes lput meilleur_candidat liste_employes
          set liste_offres_emplois remove ? liste_offres_emplois
          ask ?[die]
        ]
      ]
    ]
  ]
end
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------


;--------------------------------------------------------------------------------------------------------
;------------------------------------- procedures des travailleurs --------------------------------------
;--------------------------------------------------------------------------------------------------------
to candidature
  if(entreprise = nobody)[
    foreach [liste_offres_emplois] of entreprises_pme in-radius floor floor ((mobilite / 100)* ( sqrt((max-pxcor - min-pxcor)*(max-pxcor - min-pxcor) + (max-pycor - min-pxcor)*(max-pycor - min-pycor))))[
      ;?=>liste_offres_emplois
      foreach ? [
        ;?=>offre emploi
        if ([salaire] of ? > salaire and [securite] of ? > securite)[
          ask ? [
            set liste_candidats remove myself liste_candidats
            set liste_candidats lput myself liste_candidats
          ]
        ]
      ]
    ] 
    foreach [liste_offres_emplois] of entreprises_publiques in-radius floor floor ((mobilite / 100)* ( sqrt((max-pxcor - min-pxcor)*(max-pxcor - min-pxcor) + (max-pycor - min-pxcor)*(max-pycor - min-pycor))))[
      ;?=>liste_offres_emplois
      foreach ? [
        ;?=>offre emploi
        if ([salaire] of ? > salaire and [securite] of ? > securite)[
          ask ? [
            set liste_candidats remove myself liste_candidats
            set liste_candidats lput myself liste_candidats
          ]
        ]
      ]
    ] 
    foreach [liste_offres_emplois] of entreprises_services in-radius floor floor ((mobilite / 100)* ( sqrt((max-pxcor - min-pxcor)*(max-pxcor - min-pxcor) + (max-pycor - min-pxcor)*(max-pycor - min-pycor))))[
      ;?=>liste_offres_emplois
      foreach ? [
        ;?=>offre emploi
        if ([salaire] of ? > salaire and [securite] of ? > securite)[
          ask ? [
            set liste_candidats remove myself liste_candidats
            set liste_candidats lput myself liste_candidats
          ]
        ]
      ]
    ] 
  ]
end
to move
  rt random 360 - 180
  ifelse(entreprise = nobody)[
    set salaire salaire * ( 1 - hausse_salaire / 100 )
    fd random 2 + floor ((mobilite / 100)* ( sqrt((max-pxcor - min-pxcor)*(max-pxcor - min-pxcor) + (max-pycor - min-pxcor)*(max-pycor - min-pycor))))
  ][
    set salaire salaire * ( 1 + hausse_salaire / 100 )
    if salaire > limite_demission * salaire_embauche [
      ask entreprise[
        set liste_employes remove myself liste_employes
      ]
      set entreprise nobody
    ]
  ]
  
end
@#$#@#$#@
GRAPHICS-WINDOW
1105
245
1673
834
16
16
16.91
1
10
1
1
1
0
1
1
1
-16
16
-16
16
0
0
1
ticks

SLIDER
25
65
197
98
nombre_etudiants
nombre_etudiants
0
100
54
1
1
NIL
HORIZONTAL

SLIDER
25
100
197
133
nombre_ambitieux
nombre_ambitieux
0
100
47
1
1
NIL
HORIZONTAL

SLIDER
25
30
197
63
nombre_raisonnables
nombre_raisonnables
0
100
46
1
1
NIL
HORIZONTAL

SLIDER
205
30
377
63
nombre_pme
nombre_pme
0
100
1
1
1
NIL
HORIZONTAL

SLIDER
205
65
377
98
nombre_publique
nombre_publique
0
100
4
1
1
NIL
HORIZONTAL

SLIDER
205
100
377
133
nombre_service
nombre_service
0
100
11
1
1
NIL
HORIZONTAL

BUTTON
890
245
985
278
initialisation
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
985
245
1077
278
démarrage
start
T
1
T
OBSERVER
NIL
NIL
NIL
NIL

SLIDER
405
30
545
63
securite_etudiants
securite_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
540
30
700
63
var_securite_etudiants
var_securite_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
405
65
545
98
securite_ambitieux
securite_ambitieux
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
540
65
700
98
var_securite_ambitieux
var_securite_ambitieux
0
100
20
1
1
NIL
HORIZONTAL

SLIDER
405
100
545
133
securite_raisonnables
securite_raisonnables
0
100
60
1
1
NIL
HORIZONTAL

SLIDER
540
100
700
133
var_securite_raisonnables
var_securite_raisonnables
0
100
12
1
1
NIL
HORIZONTAL

SLIDER
715
30
845
63
salaire_etudiants
salaire_etudiants
0
100
20
1
1
NIL
HORIZONTAL

SLIDER
845
30
1010
63
var_salaire_etudiants
var_salaire_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
715
65
845
98
salaire_ambitieux
salaire_ambitieux
0
100
40
1
1
NIL
HORIZONTAL

SLIDER
845
65
1010
98
var_salaire_ambitieux
var_salaire_ambitieux
0
100
24
1
1
NIL
HORIZONTAL

SLIDER
715
100
845
133
salaire_raisonnables
salaire_raisonnables
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
845
100
1010
133
var_salaire_raisonnables
var_salaire_raisonnables
0
100
7
1
1
NIL
HORIZONTAL

SLIDER
1025
30
1155
63
mobilite_etudiants
mobilite_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
1155
30
1320
63
var_mobilite_etudiants
var_mobilite_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
1025
65
1155
98
mobilite_ambitieux
mobilite_ambitieux
0
100
5
1
1
NIL
HORIZONTAL

SLIDER
1155
65
1320
98
var_mobilite_ambitieux
var_mobilite_ambitieux
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
1025
100
1155
133
mobilite_raisonnables
mobilite_raisonnables
0
100
3
1
1
NIL
HORIZONTAL

SLIDER
1155
100
1320
133
var_mobilite_raisonnables
var_mobilite_raisonnables
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
305
245
430
278
securite_pme
securite_pme
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
440
245
580
278
var_securite_pme
var_securite_pme
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
305
280
430
313
securite_service
securite_service
0
100
39
1
1
NIL
HORIZONTAL

SLIDER
440
280
580
313
var_securite_service
var_securite_service
0
100
13
1
1
NIL
HORIZONTAL

SLIDER
305
315
430
348
securite_publique
securite_publique
0
100
80
1
1
NIL
HORIZONTAL

SLIDER
438
315
578
348
var_securite_publique
var_securite_publique
0
100
16
1
1
NIL
HORIZONTAL

SLIDER
305
360
430
393
salaire_pme
salaire_pme
0
100
23
1
1
NIL
HORIZONTAL

SLIDER
435
360
580
393
var_salaire_pme
var_salaire_pme
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
305
395
430
428
salaire_service
salaire_service
0
100
76
1
1
NIL
HORIZONTAL

SLIDER
435
395
580
428
var_salaire_service
var_salaire_service
0
100
20
1
1
NIL
HORIZONTAL

SLIDER
305
430
430
463
salaire_publique
salaire_publique
0
100
42
1
1
NIL
HORIZONTAL

SLIDER
435
430
580
463
var_salaire_publique
var_salaire_publique
0
100
13
1
1
NIL
HORIZONTAL

SLIDER
595
360
755
393
nombre_postes_pme
nombre_postes_pme
0
100
5
1
1
NIL
HORIZONTAL

SLIDER
595
395
755
428
nombre_postes_service
nombre_postes_service
0
100
9
1
1
NIL
HORIZONTAL

SLIDER
595
430
755
463
nombre_postes_publique
nombre_postes_publique
0
100
12
1
1
NIL
HORIZONTAL

SLIDER
760
360
940
393
var_nombre_postes_pme
var_nombre_postes_pme
0
100
35
1
1
NIL
HORIZONTAL

SLIDER
760
395
940
428
var_nombre_postes_service
var_nombre_postes_service
0
100
22
1
1
NIL
HORIZONTAL

SLIDER
760
430
940
463
var_nombre_postes_publique
var_nombre_postes_publique
0
100
49
1
1
NIL
HORIZONTAL

MONITOR
960
310
1100
355
nombre total de travailleurs
nombre_travailleurs
17
1
11

MONITOR
995
355
1100
400
nombre d'emplois
nombre_emplois
17
1
11

MONITOR
960
555
1100
600
offre d'emplois des pme
pme_offres_emplois
17
1
11

MONITOR
950
600
1100
645
offre d'emplois publique
publiques_offres_emplois
17
1
11

MONITOR
945
645
1100
690
offre d'emplois service
services_offres_emplois
17
1
11

SLIDER
115
245
275
278
freq_ajustement_pme
freq_ajustement_pme
1
100
10
1
1
NIL
HORIZONTAL

SLIDER
115
280
275
313
freq_ajustement_service
freq_ajustement_service
1
100
5
1
1
NIL
HORIZONTAL

SLIDER
115
315
275
348
freq_ajustement_publique
freq_ajustement_publique
1
100
100
1
1
NIL
HORIZONTAL

SLIDER
115
360
275
393
freq_embauche_pme
freq_embauche_pme
1
100
1
1
1
NIL
HORIZONTAL

SLIDER
115
395
275
428
freq_embauche_service
freq_embauche_service
1
100
2
1
1
NIL
HORIZONTAL

SLIDER
115
430
275
463
freq_embauche_publique
freq_embauche_publique
1
100
9
1
1
NIL
HORIZONTAL

SLIDER
115
475
260
508
prob_embauche_pme
prob_embauche_pme
0
100
53
1
1
NIL
HORIZONTAL

SLIDER
260
475
410
508
prob_embauche_service
prob_embauche_service
0
100
50
1
1
NIL
HORIZONTAL

SLIDER
410
475
565
508
prob_embauche_publique
prob_embauche_publique
0
100
38
1
1
NIL
HORIZONTAL

MONITOR
965
700
1100
745
étudiants chomeurs
chomeurs_etudiants
17
1
11

MONITOR
965
410
1100
455
candidatures dans les pme
nombre_candidatures_pme
17
1
11

MONITOR
950
745
1100
790
ambitieux chômeurs
chomeurs_ambitieux
17
1
11

MONITOR
945
790
1100
835
raisonnables chômeurs
chomeurs_raisonnables
17
1
11

MONITOR
945
500
1100
545
candidatures dans les services
nombre_candidatures_service
17
1
11

MONITOR
950
455
1100
500
candidatures dans le publique
nombre_candidatures_publique
17
1
11

PLOT
535
535
930
855
chomage
NIL
NIL
0.0
10.0
0.0
10.0
true
true
PENS
"etudiants" 1.0 0 -2674135 true
"ambitieux" 1.0 0 -5825686 true
"raisonnables" 1.0 0 -13840069 true

PLOT
125
535
535
855
besoin_main_oeuvre
NIL
NIL
0.0
10.0
0.0
10.0
true
true
PENS
"pme" 1.0 0 -955883 true
"publiques" 1.0 0 -1184463 true
"services" 1.0 0 -6459832 true

SLIDER
1340
30
1510
63
formation_etudiants
formation_etudiants
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
1340
65
1510
98
formation_ambitieux
formation_ambitieux
0
100
60
1
1
NIL
HORIZONTAL

SLIDER
1340
100
1510
133
formation_raisonnables
formation_raisonnables
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
1510
30
1680
63
var_formation_etudiants
var_formation_etudiants
0
100
86
1
1
NIL
HORIZONTAL

SLIDER
1510
65
1680
98
var_formation_ambitieux
var_formation_ambitieux
0
100
50
1
1
NIL
HORIZONTAL

SLIDER
1510
100
1680
133
var_formation_raisonnables
var_formation_raisonnables
0
100
30
1
1
NIL
HORIZONTAL

SLIDER
595
245
720
278
formation_pme
formation_pme
0
100
9
1
1
NIL
HORIZONTAL

SLIDER
595
315
720
348
formation_publique
formation_publique
0
100
24
1
1
NIL
HORIZONTAL

SLIDER
595
280
720
313
formation_service
formation_service
0
100
65
1
1
NIL
HORIZONTAL

SLIDER
725
245
875
278
var_formation_pme
var_formation_pme
0
100
22
1
1
NIL
HORIZONTAL

SLIDER
725
280
875
313
var_formation_service
var_formation_service
0
100
9
1
1
NIL
HORIZONTAL

SLIDER
725
315
875
348
var_formation_publique
var_formation_publique
0
100
20
1
1
NIL
HORIZONTAL

CHOOSER
575
475
667
520
pme_prefs
pme_prefs
"salaire" "formation"
0

CHOOSER
775
475
867
520
publique_prefs
publique_prefs
"salaire" "formation"
0

CHOOSER
675
475
767
520
service_prefs
service_prefs
"salaire" "formation"
1

SLIDER
440
140
627
173
hausse_salaire_etudiants
hausse_salaire_etudiants
0
100
7
1
1
NIL
HORIZONTAL

SLIDER
815
140
1020
173
hausse_salaire_raisonnables
hausse_salaire_raisonnables
0
100
3
1
1
NIL
HORIZONTAL

SLIDER
625
140
817
173
hausse_salaire_ambitieux
hausse_salaire_ambitieux
0
100
10
1
1
NIL
HORIZONTAL

SLIDER
440
175
625
208
limite_demission_etudiants
limite_demission_etudiants
0
50
3
0.1
1
NIL
HORIZONTAL

SLIDER
625
175
815
208
limite_demission_ambitieux
limite_demission_ambitieux
0
50
1.5
0.1
1
NIL
HORIZONTAL

SLIDER
815
175
1020
208
limite_demission_raisonnables
limite_demission_raisonnables
0
50
6
0.1
1
NIL
HORIZONTAL

PLOT
125
855
580
1160
moyenne_salaires
NIL
NIL
0.0
10.0
0.0
10.0
true
true
PENS
"etudiants" 1.0 0 -2674135 true
"ambitieux" 1.0 0 -5825686 true
"raisonnables" 1.0 0 -13840069 true

PLOT
580
855
1005
1160
moyenne_salaires_demande
NIL
NIL
0.0
10.0
0.0
10.0
true
true
PENS
"etudiants" 1.0 0 -2674135 true
"ambitieux" 1.0 0 -5825686 true
"raisonnables" 1.0 0 -13840069 true

@#$#@#$#@
WHAT IS IT?
-----------
This section could give a general understanding of what the model is trying to show or explain.


HOW IT WORKS
------------
This section could explain what rules the agents use to create the overall behavior of the model.


HOW TO USE IT
-------------
This section could explain how to use the model, including a description of each of the items in the interface tab.


THINGS TO NOTICE
----------------
This section could give some ideas of things for the user to notice while running the model.


THINGS TO TRY
-------------
This section could give some ideas of things for the user to try to do (move sliders, switches, etc.) with the model.


EXTENDING THE MODEL
-------------------
This section could give some ideas of things to add or change in the procedures tab to make the model more complicated, detailed, accurate, etc.


NETLOGO FEATURES
----------------
This section could point out any especially interesting or unusual features of NetLogo that the model makes use of, particularly in the Procedures tab.  It might also point out places where workarounds were needed because of missing features.


RELATED MODELS
--------------
This section could give the names of models in the NetLogo Models Library or elsewhere which are of related interest.


CREDITS AND REFERENCES
----------------------
This section could contain a reference to the model's URL on the web if it has one, as well as any other necessary credits or references.
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
NetLogo 4.1.2
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
1
@#$#@#$#@
