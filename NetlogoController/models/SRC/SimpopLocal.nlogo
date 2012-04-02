breed [cities city]
breed [innovations innovation]
breed [any-market-innovations any-market-innovation]
breed [double-market-innovations double-market-innovation]
undirected-link-breed [link-graph-innovations link-graph-innovation]

extensions [profiler array]

globals
[
  g-Initial_Population_Distribution
  g-Initial_Resource_Distribution
  g-Initial_Resource_Max
  g-number-cities
  g-Mean-Population
  g-SD-population
  g-Population-Rate
  g-initial-abondance
  g-max-abondance
  g-innovation-factor
  g-distance-impact
  g-force-load-pop?
  g-distanceF
  g-probability-sucess-interaction
  g-probability-sucess-adoption
  g-delay-for-creation
  g-delay-for-copy
  g-network
  g-proximity-threshold
  g-seed-fractal
  g-cata?
  g-cataF
  g-cata-mean-poisson
  g-cata-threshold
  g-cata-radius
  
  g-size-factor
  g-map-adoptant?
  g-hide-links?
  
  g-number-new-creations
  g-number-new-adoptions
  g-last-date-creation
  g-last-date-adoption

  g-counter-innovation
  
  
  g-population
  g-file-sql
  g-file-csv
 
  g-altitude-max
  g-topo-city-influence
  
  g-random-seed
  g-name-exp
  dt
  
  g-radius-class1
  g-radius-class2
  g-radius-class3
  g-class-value
  
  g-porte-radius-class2
  g-porte-radius-class3
  g-porte-radius-class1
  g-switch-porte-radius-class1?
  
  g-initial-innovation-life
  g-max-jump-number
  
  ;pour l'algo de percolation
  g-map-percolation? ;;True-False variable to guide mapping process
  g-Global?          ;;If True, analysis is global, and local otherwise
  g-virus-spread-chance
  
  g-type-of-choose
  g-type-of-pool-diffusion
  g-type-of-market
  g-pool-innovation
  g-pool-market-innovating
  g-add-object-innovation?
  g-max-innovation
  
  list-p
  
]

__includes [ "cities.nls" "innovation.nls" "mutual-market.nls" "double-innovations-market.nls" "any-innovations-market.nls"  "output.nls" "display.nls" "alea.nls" "network.nls" "percolation.nls"  ] 


;******************************************
;INITIALISATION
;******************************************

to set-new-seed 
  set-random-seed new-seed
end
to set-random-seed [r-seed]
  random-seed r-seed
  set g-random-seed r-seed 
end

to set-name-exp [name]
  set g-name-exp name
end


to setup-globals
  
  set g-Initial_Population_Distribution Initial_Population_Distribution
  set g-Initial_Resource_Distribution Initial_Resource_Distribution
  set g-Initial_Resource_Max Initial_Resource_Max
  set g-number-cities number-cities
  set g-Mean-Population Mean-Population
  set g-SD-population SD-population
  set g-Population-Rate Population-Rate
  set g-initial-abondance initial-abondance
  set g-max-abondance max-abondance
  set g-innovation-factor innovation-factor
  set g-distance-impact distance-impact
  set g-force-load-pop? force-load-pop?
  set g-distanceF distanceF
  set g-probability-sucess-interaction probability-sucess-interaction
  set g-probability-sucess-adoption probability-adoption
  set g-delay-for-creation delay-for-creation
  set g-delay-for-copy delay-for-copy
  set g-network network
  set g-proximity-threshold proximity-threshold
  set g-altitude-max altitude-max
  set g-seed-fractal seed-fractal
  set g-cata? cata?
  set g-cataF cataF
  set g-cata-mean-poisson cata-mean-poisson
  set g-cata-threshold cata-threshold
  set g-cata-radius cata-radius
  set g-virus-spread-chance virus-spread-chance
  set g-topo-city-influence topo-city-influence
  set g-radius-class1 radius-class1
  set g-radius-class2 radius-class2
  set g-radius-class3 radius-class3
  
  set g-porte-radius-class1 porte-radius-class1
  set g-porte-radius-class2 porte-radius-class2
  set g-porte-radius-class3 porte-radius-class3
  set g-switch-porte-radius-class1? switch-porte-radius-class1?
  set g-max-innovation max-innovation
  set g-initial-innovation-life initial-innovation-life
  set g-max-jump-number max-jump-number
  set g-type-of-choose type-of-choose
  set g-type-of-pool-diffusion type-of-pool-diffusion
  set g-type-of-market type-of-market
  set g-add-object-innovation? add-object-innovation?

  
end


;to profile-model
;  setup                  ;; set up the model
;  profiler:start         ;; start profiling
;  repeat 150 [ go ]       ;; run something you want to measure
;  profiler:stop          ;; stop profiling
;  print profiler:report  ;; view the results
;  profiler:reset         ;; clear the data
;
;end


to setup
  ca
  setup-globals

  set g-file-csv ""
  set g-pool-innovation []
  set g-counter-innovation 0
  set g-population 0
  
  set-patches
  
  ; creation des agents villes, passe par la creation du reseau, voir network.nls
  create-new-cities g-number-cities g-network g-SD-population g-Mean-Population g-Initial_Population_Distribution g-Initial_Resource_Distribution g-Initial_Resource_Max g-initial-abondance g-max-abondance
  ; initialisation des agents villes
  init-cities cities
  ; creation et initialisation des agent marché d'innovation, necessite la creation d'un reseau, nb de marché  = au nombre de villes
  init-common-market-innovation cities g-network
   
  ; Affichage des villes (voir dans display)
  map-cities
  
  ; Ecriture de l'entete du fichier csv pour la sortie des resultats
  init-csv 
  
  if display-plots? [histogram-populations histogram-resources histogram-resources-max]
 
end

to setup-grille
  
  set g-file-csv ""
  set g-pool-innovation []
  set g-counter-innovation 0
  set g-population 0
  
  set-patches
  
  ; creation des agents villes, passe par la creation du reseau, voir network.nls
  create-new-cities g-number-cities g-network g-SD-population g-Mean-Population g-Initial_Population_Distribution g-Initial_Resource_Distribution g-Initial_Resource_Max g-initial-abondance g-max-abondance
  ; initialisation des agents villes
  init-cities cities
  ; creation et initialisation des agent marché d'innovation, necessite la creation d'un reseau, nb de marché  = au nombre de villes
  init-common-market-innovation cities g-network
  
  ; Affichage des villes (voir dans display)
  map-cities
  
  ; Ecriture de l'entete du fichier csv pour la sortie des resultats
  init-csv 
  if display-plots? [histogram-populations histogram-resources histogram-resources-max]

end


;******************************************
;STATIC COMPONENT (construit une fois par iteration)
;******************************************

to set-new-sum-of-population
  set g-population sum [population] of cities
end

to-report sum-population-at-ticks
  report g-population
end

;******************************************
;DYNAMIC COMPONENT 
;******************************************

;/**
;* Procedure go-loop
;* Execute les instructions de la procedure en boucle jusqu'a rencontrer une condion de sortie exit = -1
;*/

to go-loop
  let exit 0
  while[exit != -1]
  [
    set exit go
  ]
  
  export-world "world.csv"
  export-view "view.png"
  
end

to go-manual
  let exit 0
  ifelse exit != -1
  [
    set exit go
  ]
  [
  export-world "world.csv"
  export-view "view.png"
  ]
  
end

;/**
;* Procedure go
;* Procedure appelee en boucle, renvoie une valeur a la fin de chaque tour, si cette valeur = -1 alors sortie
;*/

to-report go
  
  ;On met à jour la duree de vie des innovations ( verification à t-1) et on recupere l'ensemble des innovations restantes
  ifelse (g-add-object-innovation? = true or  g-type-of-market = "withDelayOfObjectInnovation") and g-type-of-pool-diffusion = "All-limited" [
    ;g-pool-innovation est une variable globale qui contient l'ensemble des innovations utilisable pour ce tick
    set g-pool-innovation set-new-lifetime-innovation g-type-of-pool-diffusion g-max-jump-number
  ];;Sinon on conserve toutes les innovations, sans perte de lifetimewithDelayOfObjectInnovation
  [
    set g-pool-innovation innovations with [dead? = false]
  ]
  
  ; On vérifie la capacité innovantes des market ( verification t-1) et on retourne les marché innovants pour ce tour
  ; Necessite une version mise à jour de g-pool-innovation, ce qui est le cas ici.
  set g-pool-market-innovating check-common-inovate-market
  
  ;On recalcule la somme de population une fois par ticks
  set-new-sum-of-population
  
  ;FIXME : a finir d'implementer..
  if g-cata? = true
   [catastrophy]
  
  grow-cities g-population-rate
  map-cities
  
  ;;Je decremente les delai et je reouvre si il faut
  check-common-market-delay
  distribute-common-innovation
  try-common-innovate
  
 if display-plots? [histogram-populations histogram-resources histogram-resources-max plot-Cumulated-Innovations  plot-all-pop]
 
 insert-csv 
 
  tick
  
  if (ticks = 1000)[
    show " 0 to 1000 ok"
    show g-counter-innovation
  ]
  if (ticks = 2000)[
    show " 2000 to 3000"
    show g-counter-innovation
  ]
  if (ticks = 3000)[
    show " 3000 to 4000"
    show g-counter-innovation
  ]
  
  ifelse (ticks = 4000) 
  [report -1]
  [
    if (((sum [population] of cities)* 100 / (sum [own-resource-max] of cities)) > 70)
    [report -1]
    
    if (g-counter-innovation > g-max-innovation)
    [report -1]
  ]  
  
  ;If all? cities [(round population) = own-resource-max] [stop]
  
   report 0
end

;******************************************
; BINOMIAL FUNCTION
; Precision, netlogodictionary (primite MATH)
; Double Precision (64b) dans  http://fr.wikipedia.org/wiki/IEEE_754
;******************************************

to-report binomial [ pool p]

  let binomial-probability ((1 - p) ^ (pool))
  report (1 - binomial-probability)
end



@#$#@#$#@
GRAPHICS-WINDOW
226
13
582
390
25
25
6.8
1
10
1
1
1
0
0
0
1
-25
25
-25
25
1
1
1
ticks

SLIDER
7
352
179
385
Population-Rate
Population-Rate
0.001
0.3
0.02
0.0001
1
%
HORIZONTAL

BUTTON
39
17
114
50
NIL
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
6
245
178
278
number-cities
number-cities
2
500
100
1
1
NIL
HORIZONTAL

PLOT
1043
148
1329
268
Histogram-Population
pop par ville
nb villes
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 1 -16777216 true

PLOT
637
147
1040
272
Trace-Population
NIL
NIL
0.0
40.0
0.0
10.0
true
true
PENS
"Creative" 1.0 0 -2674135 true
"Adoptant" 1.0 0 -10899396 true
"Dead" 1.0 0 -13345367 true
"Normal" 1.0 0 -7500403 true

BUTTON
717
400
825
433
NIL
Trace-Cities
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

PLOT
637
275
1041
395
Trace-Resource
NIL
NIL
0.0
40.0
0.0
10.0
true
true
PENS
"Creative" 1.0 0 -2674135 true
"Adoptant" 1.0 0 -10899396 true
"Dead" 1.0 0 -13345367 true
"Normal" 1.0 0 -7500403 true

MONITOR
206
409
282
454
last date adoption
g-last-date-adoption
17
1
11

MONITOR
206
456
282
501
last date creation
g-last-date-creation
17
1
11

SLIDER
600
147
633
395
max-x-axis
max-x-axis
1
10000
4501
100
1
NIL
VERTICAL

SLIDER
1332
149
1365
267
number-bins
number-bins
1
100
38
1
1
NIL
VERTICAL

SLIDER
850
504
1035
537
Transparency
Transparency
0
255
230
10
1
NIL
HORIZONTAL

BUTTON
749
486
848
519
NIL
set-transparency
NIL
1
T
TURTLE
NIL
NIL
NIL
NIL

CHOOSER
4
96
171
141
Initial_Population_Distribution
Initial_Population_Distribution
"Normal" "LogNormal" "Exponential" "Random" "Homogeneous"
1

SLIDER
850
402
1032
435
City-Size-Reduction-Factor
City-Size-Reduction-Factor
1
1000
221
10
1
NIL
HORIZONTAL

MONITOR
363
457
439
502
Mean Pop
mean [population] of cities with [dead-city? = 0]
0
1
11

SLIDER
7
317
180
350
SD-population
SD-population
0
200
20
1
1
NIL
HORIZONTAL

SLIDER
7
282
179
315
Mean-Population
Mean-Population
30
200
80
10
1
NIL
HORIZONTAL

PLOT
909
10
1140
142
Cumulated-Innovations
Time
f(innovations)
0.0
10.0
0.0
10.0
true
true
PENS
"Creations" 1.0 0 -2674135 true
"Adoptions" 1.0 0 -10899396 true

SLIDER
850
470
1034
503
link-size-reduction-factor
link-size-reduction-factor
0
3
0.8
0.10
1
NIL
HORIZONTAL

SLIDER
850
436
1032
469
min-deep-link
min-deep-link
0
15
0
1
1
NIL
HORIZONTAL

SLIDER
6
503
178
536
innovation-factor
innovation-factor
0
1
1
0.01
1
NIL
HORIZONTAL

BUTTON
1158
67
1213
100
NIL
test-file
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
1217
67
1278
100
NIL
force-close
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
1158
31
1261
64
NIL
profile-model
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

MONITOR
377
409
434
454
all-pop
sum-population-at-ticks
17
1
11

PLOT
603
22
900
142
Total Population
NIL
NIL
0.0
10.0
0.0
10.0
true
false
PENS
"all-pop" 1.0 0 -13345367 true

SLIDER
134
539
226
572
distanceF
distanceF
0.1
1
1
0.1
1
NIL
HORIZONTAL

SLIDER
7
573
181
606
probability-sucess-interaction
probability-sucess-interaction
0.0000000001
0.01
1.8E-8
0.0000000001
1
NIL
HORIZONTAL

SLIDER
8
390
180
423
initial-abondance
initial-abondance
0
2
1
0.01
1
NIL
HORIZONTAL

CHOOSER
4
144
173
189
Initial_Resource_Distribution
Initial_Resource_Distribution
"LogNormal" "Homogeneous" "Prop-pop" "Exponential"
2

PLOT
1044
275
1327
396
Histogram-Resource
resource par ville
nb villes
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 1 -16777216 true

CHOOSER
4
192
174
237
Initial_Resource_Max
Initial_Resource_Max
"Prop-res" "Homogeneous" "Normal" "LogNormal"
1

PLOT
1046
406
1326
526
Histogram-Resource-Max
resource dispo par ville
nb villes
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 1 -16777216 true

MONITOR
444
409
518
454
min pop
min [population] of cities\n
17
1
11

MONITOR
444
456
520
501
max pop
max [population] of cities\n
17
1
11

SLIDER
284
626
401
659
proximity-threshold
proximity-threshold
0
100
7
1
1
NIL
HORIZONTAL

SLIDER
8
612
191
645
probability-adoption
probability-adoption
0
0.01
3.0E-7
0.000000001
1
NIL
HORIZONTAL

SWITCH
854
587
957
620
cata?
cata?
1
1
-1000

SLIDER
854
623
959
656
cataF
cataF
1
100
15
1
1
NIL
HORIZONTAL

MONITOR
528
456
621
501
NIL
g-random-seed
17
1
11

BUTTON
1159
104
1287
137
NIL
set-new-seed
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SLIDER
8
426
181
459
max-abondance
max-abondance
initial-abondance
20
12
0.01
1
NIL
HORIZONTAL

SLIDER
965
586
1137
619
cata-mean-poisson
cata-mean-poisson
0
100
47
1
1
NIL
HORIZONTAL

SLIDER
965
622
1137
655
cata-threshold
cata-threshold
0
100
49
1
1
NIL
HORIZONTAL

SLIDER
966
657
1138
690
cata-radius
cata-radius
0
10
5
1
1
NIL
HORIZONTAL

TEXTBOX
285
544
435
572
_______________\nNETWORK DESIGN
11
0.0
1

CHOOSER
284
577
422
622
network
network
"random" "topo" "fix" "circle" "fractal" "cristal" "linear"
2

BUTTON
718
435
825
468
biggest?
ask cities \n  [set color white]\nask cities with [population = max [population] of cities] \n  [set color yellow]
NIL
1
T
TURTLE
NIL
NIL
NIL
NIL

SLIDER
7
650
179
683
delay-for-creation
delay-for-creation
0
200
0
1
1
NIL
HORIZONTAL

SLIDER
8
687
180
720
delay-for-copy
delay-for-copy
0
200
0
1
1
NIL
HORIZONTAL

TEXTBOX
9
53
133
95
____________________\nPOP & RESSOURCE \nINITIAL DISTRIBUTION
11
0.0
1

TEXTBOX
13
469
163
497
____________________\nCREATION & ADOPTON
11
0.0
1

TEXTBOX
855
551
1005
579
______________\nCATASTROPHIES
11
0.0
1

MONITOR
286
456
359
501
% simulation
(sum [population] of cities)* 100 / (sum [own-resource-max] of cities)
3
1
11

SLIDER
284
663
402
696
seed-fractal
seed-fractal
3
7
7
2
1
NIL
HORIZONTAL

MONITOR
521
409
596
454
NIL
count cities
17
1
11

BUTTON
115
17
178
50
NIL
go-loop
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SLIDER
428
628
577
661
altitude-max
altitude-max
100
3000
1000
100
1
NIL
HORIZONTAL

SLIDER
428
663
578
696
topo-city-influence
topo-city-influence
0
4
4
1
1
NIL
HORIZONTAL

SWITCH
7
539
132
572
distance-impact
distance-impact
0
1
-1000

SWITCH
600
400
714
433
Display-Plots?
Display-Plots?
0
1
-1000

BUTTON
428
577
535
610
NIL
print-init-situation
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SLIDER
1205
590
1345
623
virus-spread-chance
virus-spread-chance
0
100
96
1
1
NIL
HORIZONTAL

TEXTBOX
1205
554
1355
582
___________\nPERCOLATION
11
0.0
1

BUTTON
1205
627
1350
660
NIL
Percolate-From-Each-Node
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

INPUTBOX
275
708
349
768
radius-class1
7
1
0
Number

INPUTBOX
351
708
428
768
radius-class2
4
1
0
Number

INPUTBOX
430
707
501
767
radius-class3
2
1
0
Number

INPUTBOX
603
685
703
745
porte-radius-class2
10
1
0
Number

INPUTBOX
604
748
703
808
porte-radius-class3
5
1
0
Number

INPUTBOX
602
619
702
679
porte-radius-class1
20
1
0
Number

SWITCH
546
577
729
610
switch-porte-radius-class1?
switch-porte-radius-class1?
0
1
-1000

PLOT
1158
663
1358
813
Histo-Network
NIL
NIL
0.0
10.0
0.0
10.0
true
false
PENS
"default" 1.0 1 -16777216 true

SWITCH
759
705
988
738
add-object-innovation?
add-object-innovation?
0
1
-1000

CHOOSER
759
740
1011
785
type-of-market
type-of-market
"withDelayOfObjectInnovation" "withDelayDouble"
0

INPUTBOX
956
789
1117
849
initial-innovation-life
300
1
0
Number

INPUTBOX
274
867
435
927
max-jump-number
-1
1
0
Number

CHOOSER
758
788
949
833
type-of-pool-diffusion
type-of-pool-diffusion
"All-illimited" "All-limited"
1

CHOOSER
758
838
950
883
type-of-choose
type-of-choose
"last-innovation" "random"
1

BUTTON
143
55
209
88
NIL
go-manual
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

MONITOR
285
504
438
549
NIL
g-number-new-adoptions
17
1
11

MONITOR
444
505
569
550
NIL
g-counter-innovation
17
1
11

MONITOR
573
505
722
550
NIL
g-number-new-creations\n
17
1
11

BUTTON
1374
84
1513
117
NIL
write-to-file-xml
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
253
777
455
810
list-population
show list-population-init
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SWITCH
11
730
181
763
force-load-pop?
force-load-pop?
1
1
-1000

INPUTBOX
715
616
802
678
max-innovation
5000
1
0
Number

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
NetLogo 4.1.3
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="experiment" repetitions="1" runMetricsEveryStep="true">
    <setup>setup-grille</setup>
    <go>go-loop</go>
    <metric>count turtles</metric>
    <enumeratedValueSet variable="max-abondance">
      <value value="2"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="network">
      <value value="&quot;random&quot;"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Initial_Population_Distribution">
      <value value="&quot;Homogeneous&quot;"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="number-bins">
      <value value="48"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Initial_Resource_Max">
      <value value="&quot;Homogeneous&quot;"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="probability-sucess-interaction">
      <value value="2.0E-6"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-abondance">
      <value value="1"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Mean-Population">
      <value value="60"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="max-x-axis">
      <value value="5401"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Display-Plots?">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="min-deep-link">
      <value value="0"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cata?">
      <value value="false"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cata-radius">
      <value value="10"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="distanceF">
      <value value="1"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="proximity-threshold">
      <value value="6"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cata-mean-poisson">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="number-cities">
      <value value="121"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="innovation-factor">
      <value value="0.1"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="probability-adoption">
      <value value="6.0E-6"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="delay-for-creation">
      <value value="2"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="SD-population">
      <value value="10"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Transparency">
      <value value="250"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="link-size-reduction-factor">
      <value value="0.8"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="delay-for-copy">
      <value value="1"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Population-Rate">
      <value value="0.05"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cata-threshold">
      <value value="49"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="Initial_Resource_Distribution">
      <value value="&quot;Prop-pop&quot;"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cataF">
      <value value="15"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="seed-fractal">
      <value value="3"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="City-Size-Reduction-Factor">
      <value value="81"/>
    </enumeratedValueSet>
  </experiment>
</experiments>
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
0
@#$#@#$#@
