;Various agents used in the model, some mainly for visual cues
breed [customer customers]
breed [chef chefs]
breed [doorman doormen]
breed [tuna tunas]
breed [salmon salmons]
breed [rice rices]
breed [soy soys]
breed [beef beefs]
breed [nori noris]
breed [squid squids]
breed [prep preps]
breed [Calamari Calamaris] 
breed [Steak Steaks] 
breed [SushiS SushiSs] 
breed [SushiT SushiTs]

;Variables for the customer agent
customer-own [
  ;The X and Y coordinates of the destination patch
  gotoX gotoY
  ;The status which controls the agents behaviour
  status
  ;The dish which the agent has chosen to buy
  dish
  ;The seat which the agent has selected to sit in
  seat
  ;A counter which will check how long the agent ha been waiting outside the resteraunt
  arrive-count
  ;A counter which checks how long the agent has been waiting for food
  wait-count
  ;A counter which checks how long the agent has been eating
  eat-count
  ]

;Variables for the chef agent
chef-own [
  ;
  ;The X and Y coordinates of the destination patch
  gotoX gotoY
  ;The status which controls the agents behaviour
  status
  ;The target patch e.g. a workspace or the area to send food
  target
  ;The dish the agent has chosen to make
  dish
  ;A counter which is used to check how long the chef has been preparing a dish for
  chef-count
  ]


;The destination patch of the doorman
doorman-own [gotoX gotoY]

;Patch variables - adjusted to signify when a patch is occupied by an agent, wall, food etc
patches-own [busy? food?]

;Food variables - the direction in which food is travelling on the converyor belt
Calamari-own [direction]
Steak-own [direction]
SushiS-own [direction]
SushiT-own [direction]

;Global variables - used to store information which will be needed by multiple function calls
globals [
  ;A list of the possible directions (N,S,E,W)
  directions
  ;A list of the dishes available
  dishes
  ;A patch-set of spawning points for customers
  customer-spawn
  ;A patch-set of the front door
  front-door
  ;A patch-set of the entrance
  entrance
  ;A patch-set of all the seats
  seats
  ;Four patch sets containing the different sets of seats
  Nseats Sseats Eseats Wseats
  ;Four patch sets whic make up each of the corners of the resteraunt
  NEC NWC SEC SWC
  ;A patch-set of the kitchen
  kitchen
  ;A patch-set of the service points
  service
  ;patch-sets of patches relating to the exit
  exit exit-adj exit-beg
  ;Patch set of customer-die points where customers can leave the model
  customer-die
  ;Patches next to the ingrediants where the chefs can access them
  beef-patch salmon-patch tuna-patch soy-patch
  nori-patch rice-patch squid-patch
  ;Patches next to the workspace where the chefs can use them
  workspace work-patch
  ;variables which will contain how long it takes to prepare each dish
  calamari-time steak-time salmon-time tuna-time
  ;variable which contains how long ittakes to eat a dish
  eat-time
  ;variable which contains how long customers are prepared to wait for a dish
  wait-time
  ;variable which contains how long customers are prepared to wait to enter the resteraunt
  arrive-wait-time
  ;variable which will store how many angry cutomers there are
  angry-count
  ;variable which will store how many customers have been served succesfully
  served-count
  ;Variables which store a list of the past few figures for served/angry customers (used to create a smoothed average)
  smoothed-angry
  smoothed-served
  ]

;Initial tasks that are perfomed before the main loop of the model begins
to setup
  ;First the model is cleared
  ca
  ;patches are set up
  setup-patches
  ;patches are grouped into locations
  setup-locations
  ;directions list is filled with the 4 options
  set directions ["Up" "Down" "Left" "Right"]
  ;dishes list is filled with the 4 dishes
  set dishes ["Calamari" "Salmon Sushi" "Tuna Sushi" "Steak"]
  ;The gotoX and gotoY variables of all turtles are reset
  ask turtles [set gotoX 0 set gotoY 0]
  ;The time it takes to prepare each of the dishes is set
  set calamari-time 10 set steak-time 17 set salmon-time 25 set tuna-time 30
  ;The time it takes customers to eat dhishes is set
  set eat-time 40
  ;The variable which contains how long customers are prepared to wait for a dish is set
  set wait-time 200
  ;The variable which contains how long customers are prepared to wait to enter the resteraunt is set
  set arrive-wait-time 300
  ;The variables containing multiple igures for customers served/angry are set as lists
  set smoothed-angry []
  set smoothed-served []
  ;The doormen are set up
  setup-doorman
  ;The ingrediants are set up
  setup-supplies
  ;The chefs are created
  spawn-chefs
  ;The supply values for each of the dishes are reset
  balance
end

;The main loop of the model (various functions are called, the names are self-evident)
to go
  spawn-customer
  check-exit
  check-status
  check-status-chef
  balance
  update-seats
  check-full
  action
  update-belt
  plot-stuff
  tick
end

to update
  go
end


;The function used to plot the data of successfully served customers and angry customers
;  The chart will display the amount of each occuring within 100 ticks
;  The chart will also show an average of these figures, taken over 1000 ticks to get a general trend
to plot-stuff
  ;The following statement means this function will only occur every 100 ticks
  if ticks mod 100 = 0
    [
     ;The correct plot is chosen, followed by the appropriate pen
     ;  the count of angy customers is then plotted, and also added to the list 'smoothed-angry'
     set-current-plot "Customer Plot"
     set-current-plot-pen "Angry Customers"
     plot angry-count
     set smoothed-angry lput angry-count smoothed-angry
     ;The following ensures that the stored list will always contain the previous 10 values which will later be averaged
     ;  After each new value is added, the oldest value is removed from the list, but only if the list contains more than 10 values
     if length smoothed-angry > 10 [set smoothed-angry remove-item 0 smoothed-angry]
     
     ;Same as above
     set-current-plot-pen "Served Customers"
     plot served-count
     set smoothed-served lput served-count smoothed-served
     if length smoothed-served > 10 [set smoothed-served remove-item 0 smoothed-served]
     set angry-count 0
     set served-count 0
     
     ;This section of code draws the smoothed average values, the cycle only begins after 500 ticks have passed as the list
     ;  needs to accumulate values
     if ticks > 500
       [
        ;The correct plot and pen is chosen
        set-current-plot "Customer Plot"
        set-current-plot-pen "s-angry"
        ;the list is summed and divided by its length to give an average
        plot (sum smoothed-angry) / length smoothed-angry
        ;same as above
        set-current-plot-pen "s-served"
        plot  (sum smoothed-served) / length smoothed-served
       ]
    ]
         
end

;The following code is used to provide a feedback mechanism so that the chefs prepare the most needed dishes
;  it occurs if the appropriate switch has been enabled which allows this
to balance
  if auto-balance [
    ;first the amount of current instances of each food agent is found and stored
    let cnt-tuna count SushiT
    let cnt-salmon count SushiS
    let cnt-steak count Steak
    let cnt-calamari count Calamari
    
    ;the totals are added together to get a total number of instances
    let cnt-total (cnt-tuna + cnt-salmon + cnt-steak + cnt-calamari)

       ;the demand for each dish is set as the inverse proportion of that dish to the total
       ;  the function 'set-level' (see below is called to achieve this)
       set tuna-level (set-level cnt-tuna cnt-total)
       set salmon-level (set-level cnt-salmon cnt-total)
       set steak-level (set-level cnt-steak cnt-total)
       set calamari-level (set-level cnt-calamari cnt-total)

    
    ;if no dishes are present then the demand for all dishes is set at maximum (this also prevents an error associated with dividing by 0)
    if tuna-level = 0 and salmon-level = 0 and steak-level = 0 and calamari-level = 0
      [set tuna-level 10
       set salmon-level 10
       set steak-level 10
       set calamari-level 10
      ]
  ]
  
end

;The following set of code is called to automatically set the demand for each dish
;  Inputs into the function are th total amount of a particular dish and the total amount of all dishes
;  If there is none of that food type present it is set to maximum (this also prevents an error associated with dividing by 0)
;  If instances of the dish are present then demand will be set as inversly proportional to the dishes proportion of the totalk
;  Additionally if a type of dish represents more than 1/4 of the total it's demand is set to 0
;    (this prevents the belt from getting clogged up with one particular dish)
to-report set-level [food-num cnt-total]
  ifelse food-num = 0 
    [report 10]
    [ifelse food-num > cnt-total / 4
      [report 0]
      [report (1 - (food-num / cnt-total)) * 10]
    ]
end


;This function is used to initiate the movement of customers, chefs and food.
;  It also displays a label with the status of each which helps visual understanding of the model
;  The labels are only displayed if the appropriate switch is on in the main interface
to action
  ask customer[move (patch gotoX gotoY)]
  ifelse customer-labels? 
    [ask customer[set label status set label-color 0]]
    [ask customer[set label "" set label-color 0]]
  
  
  ask chef[move (patch gotoX gotoY)]
  ifelse chef-labels? 
    [ask chef[set label status set label-color 0]]
    [ask chef[set label "" set label-color 0]]
  
  food-move
end










;The following function controls the behaviour of the customer agents in different situations by changing the 'status' of the agent
;  which alters it's behaviour accordingly
to check-status
  ask customer [
    ;Initially the customer moves towards the resteraunt entrance
    if status = "arriving"
      [set gotoX -7 set gotoY 11]
    ;Once the customer reaches the doorway they head through the entrance
    if status = "arriving" and (member? patch-here front-door = true)
      [set status "queing"
       set gotoX -8 set gotoY 8]
      
    ;A counter is initially set which will count the time the customer has spent waiting to enter the resteraunt.
    ;  If the customer waits too long they will get angry and leave (see below)
    if status = "arriving" and ticks > (arrive-count + arrive-wait-time) [
      set status "leaving"
      set color 12]
    
    ;If the customer is queing and gets to the interior of the ressteraunt a seat is chosen using the 'chosen-seat' function
    if status = "queing" and (member? patch-here entrance = true)
      [set seat chosen-seat
       ;Only an empty seat will be chosen
       ifelse seat != nobody
         ;The chosen seat has it's busy? variable set to reserved to prevent other customers taking it
         [ask seat [set busy? "reserved"]]
         ;If the chosen seat has already been taken the customer will leave the resteraunt (bug fix)
         [set status "lost seat"
          set gotoX 10
          set gotoY 8]
         
      ;If the chosen seat is not busy the customer must navigate to the seat, differet sets of seats requir different paths
      if seat != nobody
        ;If the seat is on the N side then the customer can head straight to it
        [if member? seat Nseats = true
         [set status "sitting N"
          set gotoX [pxcor] of seat
          set gotoY [pycor] of seat]
        ;If the seat is on the West side the customer must first head to the top left corner of the resteraunt
        if member? seat Wseats = true
         [set status "sitting W"
          set gotoX -10
          set gotoY 8]
        ;If the seat is on the South side the customer must first head to the top left corner of the resteraunt
        if member? seat Sseats = true
         [set status "sitting S"
          set gotoX -10
          set gotoY 8]
        ;If the seat is on the South side the customer must first head to the top right corner of the resteraunt
        if member? seat Eseats = true
         [set status "sitting E"
          set gotoX 10
          set gotoY 7]
        ]
     ;Additionally at this point the customer chooses which dish they will have using the 'select-dish-customer' function
     set dish select-dish-customer
   ]
    ;If the customer is going to the West seats from the top-left corner they may go straight there
    if status = "sitting W" and (member? patch-here NWC = true)
      [set gotoX [pxcor] of seat
       set gotoY [pycor] of seat]
    ;If the customer is going to the West seats from the top-right corner they may go straight there
    if status = "sitting E" and (member? patch-here NEC = true)
      [set gotoX [pxcor] of seat
       set gotoY [pycor] of seat]
    ;If the customer is going to the South seats from the top-left corner they must first go to the bottom-left corner
    if status = "sitting S" and (member? patch-here NWC = true)
      [set gotoX -10
       set gotoY -12]
    ;If the customer is going to the South seats from the bottom-left corner they may go straight there
    if status = "sitting S" and (member? patch-here SWC = true)
      [set gotoX [pxcor] of seat
       set gotoY [pycor] of seat]
    
    ;If the customer has lost their seat they leave the resteraunt
    if status = "lost seat" and (member? patch-here NEC = true) [
      set status "leaving"]
    
    ;Once the customer has reached their seat their status is changed to 'waiting' and a counter is started
    if (status = "sitting N" or status = "sitting S" or status = "sitting E" or status = "sitting W") and patch-here = seat [
      set status "waiting"
      set wait-count ticks]
    
    
    ;The following code allows the customer to take their dish off the belt when it is in front of them
    ;  Similar code is repeated for each of the dishes
    if status = "waiting" [
      if dish = "Calamari"[
        ;First the customer checks the belt for the appropriate dish
        if any? Calamari-on neighbors4 = true[
          ;If present the cdish is removed from the belt and the food agent dies
          ask one-of Calamari-on neighbors4 [die]
          ;The status is then changed to 'eating'
          set status "eating"
          ;An eating counter is started
          set eat-count ticks]
      ]
      ;See above
      if dish = "Steak"[
        if any? Steak-on neighbors4 = true[
          ask one-of Steak-on neighbors4 [die] 
          set status "eating"
          set eat-count ticks]
      ]
      ;See above
      if dish = "Salmon Sushi"[
        if any? SushiS-on neighbors4 = true[
          ask one-of SushiS-on neighbors4 [die] 
          set status "eating"
          set eat-count ticks]
      ]
      ;See above
      if dish = "Tuna Sushi"[
        if any? SushiT-on neighbors4 = true[
          ask one-of SushiT-on neighbors4 [die] 
          set status "eating"
          set eat-count ticks]
      ]
      ;If the customer has waited longer then the pre-defined wait-time then they leave and become angry (signified by changing color)
      if ticks > (wait-count + wait-time) [
        set status "leaving"
        set color 12]
         
    ]
  
    ;Once the pre-defined eating-time has passed the customer's status is changed to leaving
    if status = "eating" and (ticks - eat-count > eat-time)
      [set status "leaving"
        ;A counter which counts how many customers have been successfully served is incremented
        set served-count (served-count + 1)]
        
      
      
    ;The following set of code directs leaving customers out of the resteraunt via the corners of the resterant
    ;From the North seats the customers go straight to the exit
    if status = "leaving" and member? patch-here Nseats = true     
         [set gotoX 8
          set gotoY 8]
    ;From the East seats the customer first goes to the top-right corner
    if status = "leaving" and member? patch-here Eseats = true
         [set gotoX 10
          set gotoY 8]
    ;From the West seats the customer first goes to the top-left corner
    if status = "leaving" and member? patch-here Wseats = true
         [set gotoX -10
          set gotoY 8]
    ;From the South seats the customer first goes to the bottom-right corner
    if status = "leaving" and member? patch-here Sseats = true
         [set gotoX 10
          set gotoY -12]
    ;From the Top-right corner the customers may go straight to the exit        
    if status = "leaving" and member? patch-here NEC = true
         [set gotoX 8
          set gotoY 8]
    ;From the Top-left corner the customers may go straight to the exit
    if status = "leaving" and member? patch-here NWC = true
         [set gotoX 8
          set gotoY 8]
    ;From the Bottom-right corner the customers must first go to the top-right corner
    if status = "leaving" and member? patch-here SEC = true
         [set gotoX 10
          set gotoY 8]
    ;From the Bottom-left corner the customers must first go to the bottom-right corner
    if status = "leaving" and member? patch-here SWC = true
         [set gotoX 10
          set gotoY -12]    
    ;From the beginning of the exit the customer heads to the end of the exit past the door
    if status = "leaving" and member? patch-here exit-beg = true
         [set gotoX 8
          set gotoY 11]
    ;From outside the door the customer must head to the edge of the map to leave the model
    if status = "leaving" and member? patch-here exit = true
         [set gotoX 8
          set gotoY 15]
    ;Similar to above but can be applied to arriving customers who have been waiting too long
    ifelse status = "leaving" and [pcolor] of patch-here = 5
         [set gotoX xcor
          set gotoY 15]
         [if status = "leaving" and seat = nobody
           [set gotoX xcor
            set gotoY 15]
         ]
    ;When the customer has reached the edge of the map they are removed from the model
    ;  If they are angry when they left they are added to the angry-count counter
    if status = "leaving" and member? patch-here customer-die = true 
          [vacate
          if color = 12 [set angry-count (angry-count + 1)]
          die]      
  ]
end






;This function controls the direction in which the food moves on the conveyor belt, direction changes occur at each corner of the belt
to check-status-food
      if patch-here = patch -7 -9 [
        set direction "up"
        set heading 0
      ]
      if patch-here = patch -7 5 [
        set direction "right"
        set heading 90
      ]
      if patch-here = patch 7 5 [
        set direction "down"
        set heading 180
      ]
      if patch-here = patch 7 -9 [
        set direction "left"
        set heading 270
      ]   
end

;This code black determines the behaviour of the chef agent in various situations
;  The behaviour of the agent is determined by it's 'status'
to check-status-chef
  ask chef [
    ;Initially the chef picks a dish to create
    if status = "selecting dish" and dish = "none"
      [set dish select-dish-chef]
      
    ;Making Calamari
    ; 1)The chef must go colloct the squid ingrediant 
    if status = "selecting dish" and dish = "Calamari" and target = "none"
      [set target one-of squid-patch with-min [distance myself]
       set status "getting squid"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 2)The chef must head to a workspace to prepare the dish
    if status = "getting squid" and dish = "Calamari" and (member? patch-here squid-patch = true)
      [set status "going to workspace"]
      
      
    ;Making Steak
    ; 1)The chef must go colloct the beef ingrediant 
    if status = "selecting dish" and dish = "Steak" and target = "none"
      [set target one-of beef-patch with-min [distance myself]
       set status "getting beef"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 2)The chef must go colloct the soy sauce ingrediant 
    if status = "getting beef" and dish = "Steak" and (member? patch-here beef-patch = true)
      [set target one-of soy-patch with-min [distance myself]
       set status "getting soy"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
      
    ; 3)The chef must head to a workspace to prepare the dish
    if status = "getting soy" and dish = "Steak" and (member? patch-here soy-patch = true)
      [set status "going to workspace"]
      
    
    ;Making Salmon Sushi
    ; 1)The chef must go colloct the salmon ingrediant 
    if status = "selecting dish" and dish = "Salmon Sushi" and target = "none"
      [set target one-of Salmon-patch with-min [distance myself]
       set status "getting salmon"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 2)The chef must go colloct the rice ingrediant 
    if status = "getting salmon" and dish = "Salmon Sushi" and (member? patch-here salmon-patch = true)
      [set target one-of rice-patch with-min [distance myself]
       set status "getting rice"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 3)The chef must go colloct the nori ingrediant 
    if status = "getting rice" and dish = "Salmon Sushi" and (member? patch-here rice-patch = true)
      [set target one-of nori-patch with-min [distance myself]
       set status "getting nori"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 4)The chef must head to a workspace to prepare the dish
    if status = "getting nori" and dish = "Salmon Sushi" and (member? patch-here nori-patch = true)
      [set status "going to workspace"]
      
      
    ;Making Tuna Sushi
    ; 1)The chef must go colloct the Tuna ingrediant 
    if status = "selecting dish" and dish = "Tuna Sushi" and target = "none"
      [set target one-of Tuna-patch with-min [distance myself]
       set status "getting tuna"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
    
    ; 2)The chef must go colloct the rice ingrediant 
    if status = "getting tuna" and dish = "Tuna Sushi" and (member? patch-here tuna-patch = true)
      [set target one-of rice-patch with-min [distance myself]
       set status "getting rice"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
      
    ; 3)The chef must go colloct the soy sauce ingrediant
    if status = "getting rice" and dish = "Tuna Sushi" and (member? patch-here rice-patch = true)
      [set target one-of soy-patch with-min [distance myself]
       set status "getting soy"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
      
    ; 4)The chef must go colloct the nori ingrediant
    if status = "getting soy" and dish = "Tuna Sushi" and (member? patch-here soy-patch = true)
      [set target one-of nori-patch with-min [distance myself]
       set status "getting nori"
       set gotoX ([pxcor] of target)
       set gotoY ([pycor] of target)]
      
    ; 5)The chef must head to a workspace to prepare the dish
    if status = "getting nori" and dish = "Tuna Sushi" and (member? patch-here nori-patch = true)
      [set status "going to workspace"]
      
      
      
    ;Going to a workspace
    ;  The chef sets it's destination to a workspace which is not occupied
    if status = "going to workspace" and ((member? target work-patch = false) or (([busy?] of target = "Yes") and (patch-here != target)))
      [if any? work-patch with [busy? = ""] = true
         [set target one-of work-patch with [busy? = ""]
          set gotoX ([pxcor] of target)
          set gotoY ([pycor] of target)]
       ]
  
         
    ;  Once at a workspace the chefs status is changed to "preparing dish"
    ;    A counter is started as each dish takes a different amount of time to prepare
    ;    the 'prep' agent is generated on the owrkspace as a visual indication that the chef is preparing food
    if (status = "going to workspace") and (member? patch-here work-patch = true) and (patch-here = target) and chef-count = ""[
      set status "preparing dish"
      set chef-count ticks
      
      if [pycor] of target = 3 [
        ask patch ([pxcor] of target) 4
          [sprout-prep 1 [setup-prep]]
      ]          
      if [pycor] of target = -7 [
        ask patch ([pxcor] of target) -8
          [sprout-prep 1 [setup-prep]]
       ]             
    ]
    
    ;Sending the dish
    ;  The following sets of code are slightly different for each dish but with essentially the same framework
    ;  They are activated when the alloted amount of ticks have passed for preparing the dish
    if status = "preparing dish" and dish = "Calamari" and (ticks - chef-count > Calamari-Time) [
      ;1) the status of the chef is changed to 'sending dish'
      set status "sending dish"
      
      ;2) The 'prep' agent is killed (getting rid of the visual prep image)
      if [pycor] of patch-here = 3 [
        ask prep-on patch ([pxcor] of patch-here) 4
          [die]
      ]
      if [pycor] of patch-here = -7 [
        ask prep-on patch ([pxcor] of patch-here) -8
          [die]
      ]
      
      ;The chef sets it's destination to one of the patches where the dish can be placed on the belt
      set target one-of service
      set gotoX ([pxcor] of target)
      set gotoY ([pycor] of target)
    ]
    
   ;see above
   if status = "preparing dish" and dish = "Steak" and (ticks - chef-count > Steak-Time) [
      set status "sending dish"
      
      if [pycor] of patch-here = 3 [
        ask prep-on patch ([pxcor] of patch-here) 4
          [die]
      ]
      if [pycor] of patch-here = -7 [
        ask prep-on patch ([pxcor] of patch-here) -8
          [die]
      ]
      
      set target one-of service
      set gotoX ([pxcor] of target)
      set gotoY ([pycor] of target)
    ]
   
   ;see above
   if status = "preparing dish" and dish = "Salmon Sushi" and (ticks - chef-count > Salmon-Time) [
      set status "sending dish"
      
      if [pycor] of patch-here = 3 [
        ask prep-on patch ([pxcor] of patch-here) 4
          [die]
      ]
      if [pycor] of patch-here = -7 [
        ask prep-on patch ([pxcor] of patch-here) -8
          [die]
      ]
      
      set target one-of service
      set gotoX ([pxcor] of target)
      set gotoY ([pycor] of target)
    ]
   
   ;see above
   if status = "preparing dish" and dish = "Tuna Sushi" and (ticks - chef-count > Tuna-Time) [
      set status "sending dish"
      
      if [pycor] of patch-here = 3 [
        ask prep-on patch ([pxcor] of patch-here) 4
          [die]
      ]
      if [pycor] of patch-here = -7 [
        ask prep-on patch ([pxcor] of patch-here) -8
          [die]
      ]
      
      set target one-of service
      set gotoX ([pxcor] of target)
      set gotoY ([pycor] of target)
    ]
      
   ;see above
   if status = "sending dish" and patch-here = target [
     if ([food?] of patch xcor (ycor - 1) = "") and ([food?] of patch (xcor - 1) (ycor - 1) = "") and ([food?] of patch (xcor + 1) (ycor - 1) = "")[
       if dish = "Steak" [
         ask patch xcor (ycor - 1) [sprout-Steak 1 [setup-Steak]]
       ]
       if dish = "Calamari" [
         ask patch xcor (ycor - 1) [sprout-calamari 1 [setup-calamari]]
       ]       
       if dish = "Salmon Sushi" [
         ask patch xcor (ycor - 1) [sprout-SushiS 1 [setup-Salmon-Sushi]]
       ]
       if dish = "Tuna Sushi" [
         ask patch xcor (ycor - 1) [sprout-SushiT 1 [setup-Tuna-Sushi]]
       ]
       
       set status "selecting dish"
       set dish "none"
       set target "none"
       set chef-count ""   
     ]
   ]      
  ]
end
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

;The following 2 functions is used to select a dish by a chef/customer respectively.
;  The chance that a particular dish will be selected is determined by the adjustable demand/suppy variables
;  By this method the observer can adjust how the supply/demand for the resteraunt functions
to-report select-dish-chef
  ;To begin with the total level is calculated
  let Total-Level (Calamari-Level + Steak-Level + Salmon-Level + Tuna-Level)
  ;A random number is generated between 0 and the total
  let rand random-float Total-Level
  
  ;Depending on how high the random number is a particular dish will be selected
  if rand < Calamari-Level
    [report "Calamari"]
  if (rand < (Calamari-Level + Steak-Level)) and (rand > Calamari-Level)
    [report "Steak"]
  if (rand < (Calamari-Level + Steak-Level + Salmon-Level)) and (rand > (Calamari-Level + Steak-Level))
    [report "Salmon Sushi"]
  if (rand < Total-Level) and (rand > (Total-Level - Tuna-Level))
    [report "Tuna Sushi"]
end


;Same as above
to-report select-dish-customer
  let Total-Level (Calamari-Demand + Steak-Demand + Salmon-Demand + Tuna-Demand)
  let rand random-float Total-Level
  
  if rand < Calamari-Demand
    [report "Calamari"]
  if (rand < (Calamari-Demand + Steak-Demand)) and (rand > Calamari-Demand)
    [report "Steak"]
  if (rand < (Calamari-Demand + Steak-Demand + Salmon-Demand)) and (rand > (Calamari-Demand + Steak-Demand))
    [report "Salmon Sushi"]
  if (rand < Total-Level) and (rand > (Total-Level - Tuna-Demand))
    [report "Tuna Sushi"]
end


;This code moves the food around the belt, the global variable 'belt-speed' can be manually altered to adjust the speed
to food-move
  ask Calamari[check-status-food]
  ask Steak[check-status-food]
  ask SushiS[check-status-food]
  ask SushiT[check-status-food]
    
  ask Calamari [fd belt-speed]
  ask Steak [fd belt-speed]
  ask SushiS [fd belt-speed]
  ask SushiT [fd belt-speed]
end


;This code makes reserved seats visible to the observer using a label
to update-seats
  ask seats [
    ifelse busy? = "reserved"
      [set plabel busy? set plabel-color 0]
      [set plabel ""]]
end


;This function checks to see if all seats are taken/reserved
;  if true then the doormen black the door preventing more customers from entering
to check-full
  ifelse ((any? seats with [busy? = ""] = false)) or (shut-doors)
    [ask doorman with [xcor != -7]
      [vacate
       set xcor -8 set ycor 9
       set gotoX -8 set gotoY 9
       occupy]
    ]
    [ask doorman with [xcor != -7]
      [vacate
       set xcor -9 set ycor 9
       set gotoX -9 set gotoY 9
       occupy]
    ]
  
  ;This piece of code addresses the problem of when too many customers have entered the resteraunt
  ;  i.e. when all seats are full and customers are looking for seats
  ;  in this case any overspill customers have their status set to 'leaving'
  if all? seats [busy? = "Yes"] = true
    [ask customer with [status = "sitting W" or status = "sitting E" or status = "sitting N" or status = "sitting S" or status = "lost seat"]
      [set status "leaving"]]
end


;This function controls the exit
;  the adjacent patches within the resteraunt are checked for agents, if present the doors open
;  this involves painting the exit white and setting the busy? variable to ""
;  if not present these steps are reverted
to check-exit
    ;ifelse ((count turtles-on exit-adj) > 0 and (any? (customer-on exit-adj) with [status = "leaving"])) or (count turtles-on exit) > 0
    ifelse (count turtles-on exit-adj) > 0 or (count turtles-on exit) > 0
         [ask exit
         [set busy? ""
          set pcolor 9.9]]
      [ask exit
         [set busy? "Yes"
          set pcolor 35]]
end


;This function can be called to choose a random seat for a customer
to-report chosen-seat
    report one-of (seats with [busy? = ""])
end



;This code updates the belt, changing the food? variable of the patches to Yes or No, this stops dishes being placed on-top of each other
to update-belt
  ask patches [
    ifelse (any? Calamari-here = true) or (any? Steak-here = true) or (any? SushiS-here = true) or (any? SushiT-here = true)
      [set food? "Yes"]
      [set food? ""]
  ]
end


;The following code is called in order to spawn customers/chefs
;  it makes sure that agents are not spawned on top of one another
;  by choosing one-of spawnpoints randomness in incorperated
to spawn-customer
  let rand random-float 1
  if rand < Customer-Chance
    [if any? customer-spawn with [busy? = ""]
      [ask one-of customer-spawn with [busy? = ""]
        [sprout-customer 1 
          [setup-customer occupy
           set status "arriving"]
      ]
    ]
  ]
end

to spawn-chefs
  repeat chef-number [
    if any? kitchen with [busy? = ""]
      [ask one-of kitchen with [busy? = ""]
        [sprout-chef 1 [setup-chef]]
    ]
  ]
end




















;The following set of code is used to setup particular patches agents etc on startup or when they are spawned

;This is the code that initially sets up the background resteraunt colors etc
to setup-patches
  ;All patches are painted white to begin with
  ask patches [set pcolor 9.9]
  ;The border is painted black
  ask patches with [pxcor = 16 or pxcor = -16 or pycor = 16 or pycor = -16] [set pcolor 0]
  ;The road is painted grey
  ask patches with [pxcor > -16 and pxcor < 16 and pycor < 16 and pycor > 11] [set pcolor 5]
  ;The surrounding grass is painted green
  ask patches with [pcolor != 0 and pcolor != 5] [set pcolor 65]
  ;The walls are painted brown
  ask patches with [pxcor > -13 and pxcor < 13 and pycor > -15 and pycor < 12][set pcolor 35] 
  ;The interior is painted white 
  ask patches with [pxcor > -11 and pxcor < 11 and pycor > -13 and pycor < 9] [set pcolor 9.9]
  ask patches with [(pxcor = 7 or pxcor = 8 or pxcor = -7 or pxcor = -8) and (pycor > 8 and pycor < 12)][set pcolor 9.9]
  ;The converyor belt is painted a darker grey
  ask patches with [pxcor > -8 and pxcor < 8 and pycor > -10 and pycor < 6][set pcolor 3]
  ;The kitchen floor is painted purple
  ask patches with [pxcor > -7 and pxcor < 7 and pycor > -9 and pycor < 5][set pcolor 114]
  ;Floorspace is cleared for the second doorman
  ask patch -9 9 [set pcolor 9.9]
  
  ;The patches that will form seats are grouped into a patch-set and colored red
  set seats (patch-set patch 8 1 patch 8 4 patch -6 -10 patch -6 6 patch -8 1 patch 3 -10 patch 8 -5
      patch -3 -10 patch 0 6 patch 0 -10 patch 8 -8 patch -3 6 patch 8 -2 patch 3 6
      patch -8 -8 patch -8 -5 patch -8 4 patch 6 -10 patch 6 6 patch -8 -2)
  ask seats [set pcolor 15]
  
  ;The patches that will form workspaces are grouped and painted light blue
  set workspace (patch-set 
    patch -3 -8 patch 6 4 patch 4 4 patch 6 -8
    patch -5 -8 patch -5 4 patch 3 4 patch 2 4
    patch -6 4 patch 0 4 patch -3 4 patch -4 -8
    patch 3 -8 patch -1 4 patch 5 -8 patch -6 -8
    patch -2 4 patch -4 4 patch 5 4 patch 4 -8
    patch 1 4)
  ask workspace [set pcolor 85]
  
  ;The kitchen patches that are adjacent to the workspace (where the chefs when preparing) will stand are grouped into a patch-set
  set work-patch (patch-set
    patch -3 -7 patch 6 3 patch 4 3 patch 6 -7
    patch -5 -7 patch -5 3 patch 3 3 patch 2 3
    patch -6 3 patch 0 3 patch -3 3 patch -4 -7
    patch 3 -7 patch -1 3 patch 5 -7 patch -6 -7
    patch -2 3 patch -4 3 patch 5 3 patch 4 -7
    patch 1 3)
  
  ;The patches where the chefs can place dishes on the conveyor belt are grouped and painted a darker blue
  set service (patch-set patch 0 -8 patch -1 -8 patch 1 -8)
  ask service [set pcolor 105]
  
  ;The patches that form the exit are grouped and painted blue (these will automatically disappear when customers are leaving)
  set exit (patch-set patch 7 11 patch 8 11)
  ask exit [set pcolor 35]
  
  ;To begin all patches have their variables busy? and food? set to ""
  ask patches [set busy? ""]
  ask patches [set food? ""]
  
  ;The borderm walls, grass, converyor belt and workspaces are set to occupied to prevent agents from moving onto them
  ask patches with [pcolor = 0 or pcolor = 65 or pcolor = 35 or pcolor = 85 or pcolor = 3][set busy? "Yes"]
end

;This code creates groups from specific patches for use in other functions to trigger different behaviour
to setup-locations
  ;Patches where customers can be spawned
  set customer-spawn patches with [pycor < max-pycor and pycor > 11 and (pxcor = 15 or pxcor = -15)]
  ;The front door
  set front-door (patch-set patch -8 11 patch -7 11)
  ;The entrance (past the doorman)
  set entrance (patch-set patch -7 8 patch -8 8)
  
  ;Different groups of seats
  set Nseats (seats with [pycor = 6])
  set Sseats (seats with [pycor = -10])
  set Eseats (seats with [pxcor = 8])
  set Wseats (seats with [pxcor = -8])
  
  ;Different corners of the resteraunt
  set NWC (patch-set patch -10 8 patch -10 7 patch -9 8 patch -9 7)
  set NEC (patch-set patch 10 8 patch 10 7 patch 9 8 patch 9 7)
  set SWC (patch-set patch -10 -11 patch -10 -12 patch -9 -11 patch -9 -12)
  set SEC (patch-set patch 10 -11 patch 10 -12 patch 9 -11 patch 9 -12)
  
  ;Patches relating to the exit
  set exit-adj (patch-set patch 7 10 patch 8 10)
  set exit-beg (patch-set patch 7 8 patch 8 8)
  
  ;Patches where the customers leave the model
  set customer-die (patches with [pycor = 15 and (pxcor > -16 and pxcor < 16)])
end

;This code sets up the various ingrediants and the patches where chefs can access them
to setup-supplies
  ask patch -6 1 [sprout-tuna 1 [setup-tuna]]
  ask patch 6 1 [sprout-tuna 1 [setup-tuna]]
  ask patch -6 0 [sprout-salmon 1 [setup-salmon]]
  ask patch 6 0 [sprout-salmon 1 [setup-salmon]]
  ask patch -6 -1 [sprout-rice 1 [setup-rice]]
  ask patch 6 -1 [sprout-rice 1 [setup-rice]]
  ask patch -6 -2 [sprout-soy 1 [setup-soy]]
  ask patch 6 -2 [sprout-soy 1 [setup-soy]]
  ask patch -6 -3 [sprout-nori 1 [setup-nori]]
  ask patch 6 -3 [sprout-nori 1 [setup-nori]]
  ask patch -6 -4 [sprout-beef 1 [setup-beef]]
  ask patch 6 -4 [sprout-beef 1 [setup-beef]]
  ask patch -6 -5 [sprout-squid 1 [setup-squid]]
  ask patch 6 -5 [sprout-squid 1 [setup-squid]]

  set tuna-patch (patch-set patch -5 1 patch 5 1)
  set salmon-patch (patch-set patch -5 0 patch 5 0)
  set rice-patch (patch-set patch -5 -1 patch 5 -1)
  set soy-patch (patch-set patch -5 -2 patch 5 -2)
  set nori-patch (patch-set patch -5 -3 patch 5 -3)
  set beef-patch (patch-set patch -5 -4 patch 5 -4)
  set squid-patch (patch-set patch -5 -5 patch 5 -4)
  
  set kitchen (patches with [pcolor = 114])
  
end

;The following code is used to setup agents when they are spawned
;  The initial states such as shape, color etc are set aswell as some initial variables
;  Most of the following code is farily self evident

to setup-customer
  set shape "person"
  set dish ""
  set arrive-count ticks
  let roll random 2
  ifelse roll > 0
    [set color 135]
    [set color 105]
end

to setup-doorman
  ask patch -7 9 [
    sprout-doorman 1 [
      set shape "person service"
      set color 7
      set gotoX -7 set gotoY 9
      occupy]]
  ask patch -9 9 [
    sprout-doorman 1 [
      set shape "person service"
      set color 7
      set gotoX -9 set gotoY 9
      occupy]]
end

to setup-tuna
  set shape "fish 2"
  set color 95
  occupy
end

to setup-salmon
  set shape "fish"
  set color 135
  occupy
end

to setup-rice
  set shape "rice2"
  set color 9.9
  occupy
end

to setup-soy
  set shape "bottle"
  set color 33
  occupy
end

to setup-nori
  set shape "container"
  set color 61
  occupy
end

to setup-beef
  set shape "cow"
  set color 35
  occupy
end

to setup-squid
  set shape "monster"
  set color 16
  occupy
end

to setup-prep
  set shape "square"
  set color 25
end

to setup-Calamari
  set shape "Calamari"
  set color 75
  set direction "left"
  set heading 270
end

to setup-Steak
  set shape "Steak"
  set color 75
  set direction "left"
  set heading 270
end

to setup-Salmon-Sushi
  set shape "Sushi"
  set color 135
  set direction "left"
  set heading 270
end

to setup-Tuna-Sushi
  set shape "Sushi"
  set color 105
  set direction "left"
  set heading 270
end

to setup-chef
  set shape "chef"
  set color 9.9
  occupy
  set status "selecting dish"
  set target "none"
  set dish "none"
  set chef-count ""
end

















;The following code lays out the strucutre of movement for agents within the model


;  The move function is the main code for this this structure, this function is agent based
;    All that is required is an input destination patch [pthere] the code takes care of the rest
;    This function also deals with the possibility that the desired adjacent patch is occupied

to move [pthere]
  ;First the function calls another function 'chech-dir' this function returns a heading (N,NW,W,SW etc)
  ;  based on the destination patch relative to the location of the caller
  let dir (check-dir pthere)
  
  ;A movement variable will store the decided adjacent patch destination (not diagonals only Up,Down,Left,Right)
  let Movement "None"
  
  ;If the destination is directly N,S,E,W of the caller then movement will only be set in the appropriate direction  
  if (dir = "N") or (dir = "S") or (dir = "E") or (dir = "W") [
    if dir = "N" [Set Movement "Up"]
    if dir = "S" [Set Movement "Down"]
    if dir = "E" [Set Movement "Right"]
    if dir = "W" [Set Movement "Left"]
  ]
  
  ;If the direction is a diagonal (not directly N,S,E,W) then a more complex method is needed to determine where to move
  ;  Essentially this decision will be a product of the total X and Y distance
  ;  E.g. if the distaance is further N than E the agent is MORE LIKELY to move North but NOT DEFINITELY
  if (dir = "NE") or (dir = "NW") or (dir = "SE") or (dir = "SW") [
    
    ;The X and Y distances are found and made postitive
    let Xdist (xcor - ([pxcor] of pthere))
    let Ydist (ycor - ([pycor] of pthere))
    
    if Xdist < 0 [set Xdist (Xdist * -1)]
    if Ydist < 0 [set Ydist (Ydist * -1)]
    
    ;The X and Y distances are added together and a random number between 0 and the combined distance is generated
    let Cdist (Xdist + Ydist)
    let rand (random Cdist) + 1
    
    ;If the random number is greater than the X distance, the agent will move along the Y axis, if not it will move along
    ;  the X axis, therefore the likelihood of moving in a direction is proportional to the length of that axis
    if dir = "NE" [
      ifelse rand > Xdist 
        [Set Movement "Up"]
        [Set Movement "Right"]]
    if dir = "NW" [
      ifelse rand > Xdist 
        [Set Movement "Up"]
        [Set Movement "Left"]]
    if dir = "SE" [
      ifelse rand > Xdist 
        [Set Movement "Down"]
        [Set Movement "Right"]]
    if dir = "SW" [
      ifelse rand > Xdist 
        [Set Movement "Down"]
        [Set Movement "Left"]]
]
  
  
  ; This part of the code deals with collision avoidance (in the case where the chosen adjacent patch is full
  Loop [
    ;The 'check-obs' function is called which checks to see if the desired patc is full
    if (check-obs Movement) = "Good" [
      
      ;If this patch is available then the function moves the agent into the patch using custom commands "m-u, m-d etc"
      ;  Vacate and Occupy commands are used to change the 'busy?' variable of the current and destination patch
      vacate
      if Movement = "Up"[m-u occupy stop]
      if Movement = "Down" [m-d occupy stop]
      if Movement = "Left" [m-l occupy stop]
      if Movement = "Right" [m-r occupy stop]
      if Movement = "None" [occupy stop]
    ]
    
    ;If the desired patch is busy then the agent will either remain where it is of choose a random direction and repeat the process
    let rand-float random-float 1
    ifelse rand-float > 0.5 
      [set Movement "None"]
      [set Movement one-of directions]
  ]
end

;This function checks to see which direction the destination patch is in it then reports a bearing
to-report check-dir [pthere]
  ;The co-ordinattes of the destination
  let Xthere ([pxcor] of pthere)
  let Ythere ([pycor] of pthere)
  ;The co-ordinates of the agent
  let Xhere xcor
  let Yhere ycor
  
  ;Case where the agent is already on the destination
  if Xhere = Xthere and Yhere = Ythere [report "Current"]
  
  ;Cases where the destinaion is not a diagonal
  if Xhere = Xthere and Yhere > Ythere [report "S"]
  if Xhere = Xthere and Yhere < Ythere [report "N"]
  if Yhere = Ythere and Xhere > Xthere [report "W"]
  if Yhere = Ythere and Xhere < Xthere [report "E"]
  
  ;Cases where the destinaion is a diagonal
  if Xhere > Xthere and Yhere > Ythere [report "SW"]
  if Xhere < Xthere and Yhere > Ythere [report "SE"]
  if Xhere > Xthere and Yhere < Ythere [report "NW"]
  if Xhere < Xthere and Yhere < Ythere [report "NE"]
end

;This function checks to see if a chosen adjacent patch is occupied, returning either 'Good' or 'Bad'
;  It does this by checking the busy? variable of that patch
to-report check-obs [Movement]
  let Xhere xcor
  let Yhere ycor
  
  if Movement = "Up"[
    ifelse [busy?] of patch Xhere (Yhere + 1) = "Yes"
      [report "Bad"]
      [report "Good"]]
  if Movement = "Down"[
    ifelse [busy?] of patch Xhere (Yhere - 1) = "Yes"
      [report "Bad"]
      [report "Good"]]
  if Movement = "Right"[
    ifelse [busy?] of patch (Xhere + 1) Yhere = "Yes"
      [report "Bad"]
      [report "Good"]]
  if Movement = "Left"[
    ifelse [busy?] of patch (Xhere - 1) Yhere = "Yes"
      [report "Bad"]
      [report "Good"]]
  if Movement = "None"[report "Good"]
end
  

;The following functions are used to move an agent left,right,up,down
to m-l
  setxy (Xcor - 1) Ycor
end

to m-r
  setxy (Xcor + 1) Ycor
end

to m-u
  setxy Xcor (Ycor + 1)
end

to m-d
  setxy Xcor (Ycor - 1)
end

;The following funcitons are used to change the busy? variable of a patch appropriately when an agent is arriving or leaving it
to occupy
  ask patch-here [set busy? "Yes"]
end

to vacate
  ask patch-here [set busy? ""]
end

to kill
  if mouse-down? [
    if count turtles-on patch mouse-xcor mouse-ycor > 0 [
      ask turtles-on patch mouse-xcor mouse-ycor [die]
      ask patch mouse-xcor mouse-ycor [set busy? ""]
    ]

  ]
end
@#$#@#$#@
GRAPHICS-WINDOW
6
21
676
712
16
16
20.0
1
11
1
1
1
0
0
0
1
-16
16
-16
16
1
1
1
ticks

BUTTON
682
21
790
54
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
683
132
856
165
Customer-Chance
Customer-Chance
0
0.15
0.1
0.01
1
NIL
HORIZONTAL

SLIDER
682
58
854
91
Chef-Number
Chef-Number
1
10
10
1
1
NIL
HORIZONTAL

BUTTON
792
21
856
54
go
go\n;ask patches [set plabel busy? set plabel-color 0]\n;ask patches [set plabel food? set plabel-color 0]
T
1
T
OBSERVER
NIL
NIL
NIL
NIL

MONITOR
218
155
280
200
Free seats
count seats with [busy? = \"\"]
17
1
11

MONITOR
283
155
349
200
Customers
count customer
17
1
11

SLIDER
682
258
822
291
Tuna-Level
Tuna-Level
0
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
682
296
822
329
Salmon-Level
Salmon-Level
0
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
682
333
822
366
Steak-Level
Steak-Level
0
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
683
370
823
403
Calamari-Level
Calamari-Level
0
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
683
95
855
128
belt-speed
belt-speed
0
0.5
0.25
0.05
1
NIL
HORIZONTAL

SLIDER
839
258
980
291
Tuna-Demand
Tuna-Demand
1
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
839
296
980
329
Salmon-Demand
Salmon-Demand
1
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
839
333
980
366
Steak-Demand
Steak-Demand
1
10
5
0.1
1
NIL
HORIZONTAL

SLIDER
839
371
981
404
Calamari-Demand
Calamari-Demand
1
10
5
0.1
1
NIL
HORIZONTAL

MONITOR
684
170
742
215
Calamari
count calamari
17
1
11

MONITOR
748
170
805
215
Steak
count steak
17
1
11

MONITOR
883
170
952
215
Tuna Sushi
count sushit
17
1
11

MONITOR
809
170
879
215
Salmon Sushi
count sushis
17
1
11

SWITCH
683
221
811
254
auto-balance
auto-balance
0
1
-1000

BUTTON
839
408
940
441
reset demand
set tuna-demand 5\nset salmon-demand 5\nset steak-demand 5\nset calamari-demand 5
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

BUTTON
683
407
783
440
reset supply
set tuna-level 5\nset salmon-level 5\nset steak-level 5\nset calamari-level 5
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL

SWITCH
814
221
930
254
shut-doors
shut-doors
1
1
-1000

PLOT
683
446
1061
715
Customer Plot
hundred ticks
number per 100 ticks
0.0
50.0
0.0
20.0
true
true
PENS
"Served Customers" 2.0 0 -13345367 true
"Angry Customers" 2.0 0 -2674135 true
"s-angry" 2.0 0 -16777216 false
"s-served" 2.0 0 -16777216 false

SWITCH
911
23
1043
56
Chef-Labels?
Chef-Labels?
1
1
-1000

SWITCH
912
59
1043
92
Customer-Labels?
Customer-Labels?
0
1
-1000

BUTTON
1099
159
1162
192
NIL
Kill
T
1
T
OBSERVER
NIL
NIL
NIL
NIL

TEXTBOX
1035
116
1185
172
If any agents seem to get stuck you can use the kill button and click on them to delete them
11
0.0
1

@#$#@#$#@
WHAT IS IT?
-----------
This is a geospatial model of the dynamics within a sushi restaurant built in order to demonstrate several key notions related to complexity theory. It additionally analyses the multiple relationships between the different systems involved allowing the observer to find an optimal system for serving customers while keeping angry customer to a minimum. Though being less abstract than most this model could potentially be useful commercially in increasing restaurant efficiency it should mainly be seen as an educational aid as the simplifications make it unrealistic.

HOW IT WORKS
------------
There are several systems and agents involved in this complex model.

The customer:
Customers are randomly generated at different arrival spawn points based on the customer-chance variable. They make their way to the entrance where they must queue to progress into the restaurant. Upon entering the restaurant the customer chooses a seat and begins waiting for their desired dish to arrive on the conveyor belt. The dish the customer chooses is based on the user-defined demand for each dish, chosen randomly using probability. Once the dish appears in front of them they take it and begin eating. Once they have finished eating they leave their seat, move towards the exit and leave the restaurant and model.
There are also reflexive systems in place: If the customer has to queue for too long upon arrival they will become angry and leave the model. If the customer has to wait too long for their desired dish they will become angry and leave the restaurant and model.



The chefs:
The number of chefs is defined by the user before setup but their initial location is randomly generated within the kitchen. The chef's progress through a cycle of dish prepariation. Initially the chef picks a dish to make; this is based on user-defined supply values for each dish. The chef must then collect all the ingredients for that dish before the agent can prepare it. There are four dishes which progress in complexity, the required ingredients are:

Tuna Sushi: Tuna, Nori, Soy Sauce, Rice
Salmon Sushi: Salmon, Rice, Nori
Soya Steak: Beef, Soy Sauce
Calamari: Squid

Once the ingredients are collected the chef moves to a counter and begins preparing the dish. Each dish takes a different amount of time to prepare (defined within the source code). Once the dish has been prepared the chef moves to the service area and places it on the conveyor belt. The chef then restarts the cycle.

Additionally there is an option to enable reflexive creation of dishes which uses information on the amount of different types of dishes in order to generate the supply values. This reduces the chance of an over-production of dishes which can lead to bottlenecks etc.



The food:
Once a dish is created it progresses along the conveyor belt, multiple dishes cannot be placed in the same patch so the conveyor belt can become full. The speed of the belt can be altered as the model is running



The doorman:
When all the seats in the restaurant are full the doormen block the entrance preventing more customers flooding the restaurant, this causes a queue to form. There is an additional option for the observer to close the doors allowing the dynamics to change as the model is running



Movement:
A new underlying infrastructure of movement was written for this model in order to create a probabilistic yet logical movement of people round the model. It was designed to be operated within a cellular environment and avoid common issues such as an agent moving at angles travelling through a separate patch to get to the patch in front of it.

This system of movement only allows agents to move horizontally and vertically, the agent has a destination patch and its route is probabilistically based on the relative x and y lengths. I.e. if a patch is further north then it is east the agent is more likely to move north but may still move east. This system is similar to how real human decisions are made.

Collision avoidance is also built into this system.

HOW TO USE IT
-------------
The only interface variable which the observer must set before the model has run is the number of chefs. All other variables can be changed on the fly allowing dynamic observation. Once the numbers of chefs are set the user should press setup followed by go

Additional variables the observer can alter include:
-The demand for each type of dish
-The supply of each dish
-The chance that customers will be generated (essentially customer rate)
-The speed of the conveyor belt
-The speed at which the model runs

Additionally there are options to:
-Turn reflexive supply of dishes on/off
-Open/Close the restaurant entrance
-Turn labels of the chefs or customers on/off

Various monitors and plots allow the user to see the result of these dynamics such as:
-The total number of customers
-The number of free seats
-the number of instances of each type of dish
-A chart plotting the number of customers served every 100 ticks
-A chart plotting the number of angry customers every 100 ticks
-An average of the number of customers served (smoothed over 10 values)
-An average of the number of angry customers (smoothed over 10 values)

The observer can experiment with altering the different interface values and seeing the result both through the monitors and charts, but also the visual phenomena that develop such as bottlenecks, queues, the spatial distribution of angry customers etc.

THINGS TO NOTICE
----------------
Look out for emergent spatial phenomena related to complexity theory such as:
-queuing at the entrance to the restaurant
-bottlenecks within the kitchen and at the exit
-congestion on the conveyor belt
-notice how probability is inherent in every aspect of the model:
    	Agents don't always move in exactly the same way
    	Demand and supply of different dishes vary naturally
    	Customer population and restaurant performance can vary over time
    

Additionally look out for feedback loops:
-what happens if the supply values is not automatically altered?
-how does what happens in one system within the model affect the functioning of other systems?
-how does the feedback loop of customers becoming angry and leaving effect the dynamics of the model?

Finally notice the restaurant system itself:
-does the system function efficiently
-which seats are more likely to produce angry customers
-how does changing the variables affect the number of served/angry customers

THINGS TO TRY
-------------
-Try creating a setup which can serve the most amount of people
-Try creating a setup which minimises the number of angry customers
   Repeat these processes but for different demand values for the dishes
-Try and control the model by manually changing the supply values of different dishes   and manually closing the restaurant entrance. Can you create a more efficient system than the automatic balancing?
-Try and find out if too many chefs spoil the sushi
-Try altering the belt speed. Does it have a significant effect on the whole system? Is the degree of its influence what you expected?

EXTENDING THE MODEL
-------------------
Pricing:
Probably the one key element missing from this restaurant system is a pricing model. There is no penalty for having additional chefs whereas in reality businesses need to consider salary as well as service. 
Implement a pricing model for the different dishes enabling higher level dynamic between cost, service, efficiency etc. This could be further extended by having customers more/less likely to order expensive items etc. Tips could be included for faster service.

Happiness:
Implement a happiness variable for the customer agent, different customers could begin with different levels of happiness, this could then be influenced by queuing time, food efficiency and pricing. This could be implemented reflexively with higher levels of happiness influencing the number of customers that arrive.

Time:
Modify the variables for eating time, waiting tolerance, preparation time etc; see how this affects the overall model.
Adapt the model so that the various speeds of processes are more similar to real life.
Implement a 'Day' where the model runs for a set number of ticks, customer numbers could vary over this period. E.g. fewer customers in the morning, with surges at lunch time and dinner. This would make the model more true to life

NETLOGO FEATURES
----------------
In general Netlogo was a very useful system for creating this type of complex agent-based model.

However the key limitation was movement. Specifically creating a realistic system where agents move with a purpose and destination, however maintaining an element of randomness. Additionally there are limitations with allowing agents to move dynamically in a patch-based environment e.g. moving half a patch, or diagonally moving through the corner of a patch but never truly interacting with it.

To overcome these problems an entirely new system of movement was designed which fitted better with the patch-based world of Netlogo. Agents were allowed to move patch-by-patch vertically and horizontally, no turning was involved. The details of this system are documented along with the code at the bottom of the procedures tab.


RELATED MODELS
--------------
This geospatial model is far less abstract then most and therefore very few models truly relate. It was developed entirely from scratch, using completely original code.

Some inspiration was drawn from Gemma Polmear's model of supermarket dynamics.


CREDITS AND REFERENCES
----------------------
As mentioned all code within this model are completely original.

The model was created by David Miller, an MSc student at Nottingham University studying Geographical Information Science.
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

bottle
false
0
Circle -7500403 true true 90 240 60
Rectangle -1 true false 135 8 165 31
Line -7500403 true 123 30 175 30
Circle -7500403 true true 150 240 60
Rectangle -7500403 true true 90 105 210 270
Rectangle -7500403 true true 120 270 180 300
Circle -7500403 true true 90 45 120
Rectangle -7500403 true true 135 27 165 51

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

calamari
false
7
Circle -14835848 true true 0 0 300
Circle -16777216 false false 0 0 300
Circle -1 true false 45 30 120
Circle -1 true false 165 90 120
Circle -1 true false 60 165 120
Circle -14835848 true true 63 48 85
Circle -14835848 true true 183 108 85
Circle -14835848 true true 78 183 85

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

chef
false
15
Polygon -1 true true 120 225 105 285 135 285 150 240 165 285 195 285 180 225
Polygon -7500403 true false 195 135 240 240 225 255 165 150
Polygon -7500403 true false 105 135 60 240 75 255 135 150
Polygon -1 true true 195 120 240 225 210 240 180 165 180 225 120 225 120 165 90 240 60 225 105 120
Polygon -16777216 true false 135 120 150 135 135 165 150 180 165 165 150 135 165 120
Circle -7500403 true false 110 35 80
Rectangle -7500403 true false 127 109 172 124
Line -16777216 false 150 148 150 225
Line -16777216 false 196 120 151 179
Line -16777216 false 104 120 149 179
Line -16777216 false 150 195 165 195
Line -16777216 false 150 225 165 225
Line -16777216 false 150 150 165 150
Polygon -1 true true 120 45 180 45 180 15 195 15 195 0 105 0 105 15 120 15
Rectangle -16777216 true false 90 285 135 300
Rectangle -16777216 true false 165 285 210 300
Line -16777216 false 120 225 180 225

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

container
false
0
Rectangle -7500403 false true 0 75 300 225
Rectangle -7500403 true true 0 75 300 225
Line -16777216 false 0 210 300 210
Line -16777216 false 0 90 300 90
Line -16777216 false 150 90 150 210
Line -16777216 false 120 90 120 210
Line -16777216 false 90 90 90 210
Line -16777216 false 240 90 240 210
Line -16777216 false 270 90 270 210
Line -16777216 false 30 90 30 210
Line -16777216 false 60 90 60 210
Line -16777216 false 210 90 210 210
Line -16777216 false 180 90 180 210

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

fish 2
false
0
Polygon -1 true false 56 133 34 127 12 105 21 126 23 146 16 163 10 194 32 177 55 173
Polygon -7500403 true true 156 229 118 242 67 248 37 248 51 222 49 168
Polygon -7500403 true true 30 60 45 75 60 105 50 136 150 53 89 56
Polygon -7500403 true true 50 132 146 52 241 72 268 119 291 147 271 156 291 164 264 208 211 239 148 231 48 177
Circle -1 true false 237 116 30
Circle -16777216 true false 241 127 12
Polygon -1 true false 159 228 160 294 182 281 206 236
Polygon -7500403 true true 102 189 109 203
Polygon -1 true false 215 182 181 192 171 177 169 164 152 142 154 123 170 119 223 163
Line -16777216 false 240 77 162 71
Line -16777216 false 164 71 98 78
Line -16777216 false 96 79 62 105
Line -16777216 false 50 179 88 217
Line -16777216 false 88 217 149 230

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

monster
false
0
Polygon -7500403 true true 75 150 90 195 210 195 225 150 255 120 255 45 180 0 120 0 45 45 45 120
Circle -16777216 true false 165 60 60
Circle -16777216 true false 75 60 60
Polygon -7500403 true true 225 150 285 195 285 285 255 300 255 210 180 165
Polygon -7500403 true true 75 150 15 195 15 285 45 300 45 210 120 165
Polygon -7500403 true true 210 210 225 285 195 285 165 165
Polygon -7500403 true true 90 210 75 285 105 285 135 165
Rectangle -7500403 true true 135 165 165 270

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

person service
false
0
Polygon -7500403 true true 180 195 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285
Polygon -7500403 true true 120 90 105 90 60 195 90 210 120 150 120 195 180 195 180 150 210 210 240 195 195 90 180 90 165 105 150 165 135 105 120 90
Polygon -1 true false 123 90 149 141 177 90
Rectangle -7500403 true true 123 76 176 92
Circle -7500403 true true 110 5 80
Line -13345367 false 121 90 194 90
Line -16777216 false 148 143 150 196
Rectangle -16777216 true false 116 186 182 198
Circle -1 true false 152 143 9
Circle -1 true false 152 166 9
Rectangle -16777216 true false 179 164 183 186
Polygon -2674135 true false 180 90 195 90 183 160 180 195 150 195 150 135 180 90
Polygon -2674135 true false 120 90 105 90 114 161 120 195 150 195 150 135 120 90
Polygon -2674135 true false 155 91 128 77 128 101
Rectangle -16777216 true false 118 129 141 140
Polygon -2674135 true false 145 91 172 77 172 101

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

rice
false
15
Polygon -7500403 true false 150 90 75 120 30 165 30 240 270 240 270 150 210 105
Polygon -1 true true 165 165 165 180 195 195 195 180 165 165
Rectangle -1 true true 75 195 105 210
Rectangle -1 true true 105 225 135 240
Rectangle -1 true true 90 180 120 195
Polygon -1 true true 210 105 195 120 210 135 225 120 210 105
Rectangle -1 true true 150 240 135 210
Rectangle -1 true true 150 210 165 240
Rectangle -1 true true 135 180 165 195
Rectangle -1 true true 135 180 165 195
Rectangle -1 true true 135 180 165 195
Polygon -1 true true 120 165 135 180 120 195
Polygon -1 true true 135 225 150 240 180 225 165 210
Polygon -1 true true 165 195 165 210 195 210 195 195
Polygon -7500403 true false 195 210 195 225 195 240 180 240 180 225
Rectangle -1 true true 30 195 60 210
Rectangle -1 true true 180 180 210 195
Polygon -1 true true 75 150 75 165 105 180 105 165 75 150
Polygon -1 true true 165 165 165 180 195 195 195 180 165 165
Polygon -1 true true 165 165 165 180 195 195 195 180 165 165
Polygon -1 true true 165 165 165 180 195 195 195 180 165 165
Polygon -1 true true 165 165 165 180 195 195 195 180 165 165
Polygon -1 true true 195 210 195 225 225 240 225 225 195 210
Polygon -1 true true 75 210 75 225 105 240 105 225 75 210
Polygon -1 true true 165 210 165 225 195 240 195 225 165 210
Rectangle -1 true true 45 210 75 225
Rectangle -1 true true 30 225 60 240
Rectangle -1 true true 240 225 270 240
Rectangle -1 true true 210 195 240 210
Rectangle -1 true true 180 135 210 150
Rectangle -1 true true 180 165 210 180
Rectangle -1 true true 90 150 120 165
Polygon -1 true true 75 150 75 165 105 180 105 165 75 150
Polygon -1 true true 75 150 75 165 105 180 105 165 75 150
Polygon -1 true true 150 135 150 150 180 165 180 150 150 135
Polygon -1 true true 45 165 45 180 75 195 75 180 45 165
Polygon -1 true true 210 210 210 225 240 240 240 225 210 210
Polygon -1 true true 240 210 240 225 270 240 270 225 240 210
Polygon -1 true true 120 135 120 150 150 165 150 150 120 135
Polygon -1 true true 165 105 150 120 165 135 180 120 165 105
Polygon -1 true true 135 105 120 120 135 135 150 120 135 105
Polygon -1 true true 225 165 210 180 225 195 240 180 225 165
Polygon -1 true true 150 150 135 165 150 180 165 165 150 150
Polygon -1 true true 90 120 75 135 90 150 105 135 90 120
Polygon -1 true true 60 135 45 150 60 165 75 150 60 135
Polygon -1 true true 240 180 240 195 270 210 270 195 240 180
Polygon -1 true true 225 135 225 150 255 165 255 150 225 135
Polygon -1 true true 180 105 180 120 210 135 210 120 180 105
Polygon -1 true true 120 195 120 210 150 225 150 210 120 195

rice2
false
15
Polygon -7500403 true false 120 0 120 30 75 30 60 75 45 75 30 105 30 120 0 120 0 270 300 270 300 210 270 210 270 75 240 30 195 15 165 0
Rectangle -1 true true 45 225 120 255
Rectangle -1 true true 135 225 210 255
Rectangle -1 true true 0 180 75 210
Rectangle -1 true true 135 180 210 210
Rectangle -1 true true 90 135 120 210
Rectangle -1 true true 225 180 255 255
Rectangle -1 true true 270 225 345 255
Rectangle -1 true true -45 225 30 255
Rectangle -1 true true 135 90 165 165
Rectangle -1 true true 45 90 75 165
Rectangle -1 true true 180 90 255 120
Rectangle -1 true true 180 135 255 165
Rectangle -1 true true 90 90 120 120
Rectangle -1 true true 0 135 30 165
Rectangle -1 true true 75 45 150 75
Rectangle -1 true true 165 45 240 75
Rectangle -1 true true 135 0 165 30

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
2
Rectangle -955883 true true 30 30 270 270
Polygon -1 true false 60 60 75 45 105 45 120 60 135 90 45 90
Circle -13791810 true false 101 191 67
Circle -2674135 true false 41 191 67
Circle -2064490 true false 71 146 67
Rectangle -6459832 true false 210 165 240 255
Polygon -7500403 true false 210 165 210 45 240 75 240 165

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

steak
false
7
Circle -14835848 true true 0 0 300
Circle -16777216 false false 0 0 300
Polygon -1 true false 210 45 180 30 135 30 90 30 60 60 45 90 45 135 60 165 90 195 120 225 120 270 135 285 180 285 225 255 255 210 255 135 240 75
Polygon -2674135 true false 180 60 150 60 105 60 90 75 75 105 75 135 105 165 150 210 150 240 150 255 165 270 180 270 225 240 240 210 240 135 225 90
Circle -1 true false 105 75 60
Circle -2674135 true false 120 90 30
Polygon -16777216 false false 90 30 180 30 210 45 240 75 255 135 255 210 225 255 180 285 135 285 120 270 120 225 60 165 45 135 45 90 60 60

sushi
false
13
Circle -14835848 true false 0 0 300
Circle -16777216 false false -2 -2 302
Polygon -1 true false 120 45 150 45 195 60 240 75 255 105 255 135 240 150 210 165 165 165 120 165 90 150 45 105 45 75 75 45
Polygon -10899396 true false 45 75 45 210 60 240 105 270 165 285 210 285 240 270 255 255 255 135 240 150 210 165 120 165 90 150
Polygon -2064490 true true 150 60 75 60 60 75 105 135 120 150 195 150 225 120 225 90 195 75
Polygon -16777216 false false 45 75 45 210 60 240 105 270 165 285 210 285 240 270 255 255 255 135 240 150 210 165 120 165 90 150
Polygon -16777216 false false 45 75 75 45 150 45 240 75 255 105 255 135 240 150 210 165 120 165 90 150

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
NetLogo 4.1.1
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
0
@#$#@#$#@
