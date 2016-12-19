(ns adventure.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str])
  (:gen-class))

(def gamelocations
	{
	:dungeon {:desc  
"You've woken up to darkness on a dingy damp floor. With the exception of the 
fire lamp in the hallway, you cannot see anything. It is hard to breathe...
How did you get here?! The last thing you remember is going to bed at your home in Champaign. 
You eyes are becoming accustomed to your surroundings. You see a 'door' at the end of the hallway.
You walk closer to the it. You notice in the corner, a hidden ladder going 'down' into the ground.  
You must escape this place!"
           :title "in a dungeon"
           :dir {:down :cellar
           		 :door :tunnel}
           :contents #{:hai}}
	:cellar {:desc "This is an old musty wine cellar. But it is better lit than the dungeon. There's no 
exit here. Better go back 'up'. But wait! There is shiny golden 'key' on one of the wine barrels."
              :title "in the cellar"
              :dir {:up :dungeon}
              :contents #{:key}}
	:tunnel {:desc "This tunnel seems to be an underground pathway to somewhere. You see light towards
'north' the end of it."
		:title "in a tunnel"
		:dir {:north :corridor}
		:contents #{}}
	:corridor {:desc "You have three doors to your west, east and north."
		:title "in a 3-way corridor."
		:dir {:west :armory
		      :east :kitchen
	          :north :doghouse}
		:contents #{}}
	:armory {:desc "This is a vast champer filled with weapons and shields. There is shiny 'sword' that
attracts your eye. There seems to be no exit from here. "
		:title "in the armory"
		:dir {:east :corridor}
		:contents #{:sword}}
	:kitchen {:desc "You seem to have reached an old kitchen chamber, but this is also a deadend. There
is a piece of 'steak' and and a suspiciously beautiful 'apple' on the counter."		
		:title "in a kitchen"
		:dir {:west :corridor}
		:contents #{:steak
					:apple}}
	:doghouse {:desc "
You enter a dimly-list dungeon. You think it's empty but then you hear it. 
Four dogs are growling 30 feet from you on the other side of the dungeon where 
there is a door. The dogs have noticed you and they are snarling now. They start
to approach you. They seem hungry. You can't fight them because their barking will
alert any guards around. You must distract them. Maybe 'feed' them something or go
back 'south' to the corridor..."
			   :title "in the doghouse dungeon"
			   :dir {:south :corridor
			   		 :feed  :monsterpit}}
	:monsterpit {:desc "This is vast round chamber that reeks of a pungent smell. There are 
massive chains on the floor which are tied to a large mound of earth
in the center of the room. SUDDENLY, the mound moves! Wait! It's not a
mound of earth, it's a living beast, a monster! You are petrified. The
beast seems to be asleep, breathing lightly. There is door at the other 
end of the chamber. The only way to get past is to 'stab' the beast with 
a weapon in its sleep or go back..."
		:title "in a monsterpit."
		:dir {:south :doghouse
		      :fight :treasury}
		:contents #{}}
	:treasury {:desc "Around you there is a chamber full of gold coins and gems. You can't
possibly take all of it. But there is a handy bag of 'coins' nearby. 
As you make your way to the other side of the room, you a notice a
shining 'mystic crystal'. It is unlike anything you've ever seen 
before. There is door to the 'north' side of the room."
		:title "in a treasury room."
		:dir {:west :monsterpit
			  :north :hallway}
		:contents #{:coins
					:mysticcrystal}}
	:hallway {:desc "You see a guard dressed in a medieval attire walking in the opposite 
direction from you in the hallway. He hasn't noticed you yet. There
is a door on the other end. You may 'attack' the guard while he's not
looking or you may try to approach him and 'bribe' him to get some 
information. He might be able to tell you how you got here and how
you can escape."
		:title "in the hallway"
		:dir {:south :treasury
			  :fight :southcorridor
			  :bribe :southcorridor}
		:contents #{}}
	:southcorridor {:desc "There are doors to your 'west', 'south' and 'north'. Pick your destiny"
		:title "in a 3-way corridor"
		:dir {:west  :oracleroom
			  :south :riddleroom
			  :north :waterfall
			  :east  :hallway}
		:contents #{}}
	:oracleroom {:desc "
You entered a door which led to you what seems to be an Oracle room, filled with magical instruments. 
You notice that there is an instrument with a cavity shaped like the mystic crystal you saw earlier. 
There is an oracle in the room who is offering to 'upgrade' your weapon. There is no other exit, you will
have to go back to where you came from."
		:title "in the Oracleroom"
		:dir {:east :southcorridor}
		:contents #{:swordofvalor}}
	:waterfall {:desc "You have reached the inside of a cave, whose opening is blocked by a waterfall.
But on the other side of the waterfall you can see a pack of dragons drinking 
water from the lake, in what seems to be a huge dragon cave. At the end of 
the dragon cave there seems to be a ladder. The ladder must be going to the 
Tower of Winterfell. But to get there you will have to get past the Dragons. 
You need a way to pass through undetected. The dragon cave is in the 'north'."
		:title "inside a waterfall"
		:dir {:south :southcorridor
			  :north :dragoncave}
		:contents #{}}
	:dragoncave {:desc "You have dived into the water fall and now you are swimming towards the end of
end of the lake. You are underwater and so the dragons can't see you. But you
must get out of the lake and try to 'climb' up the ladder at the of the cave."
		:title "in the dragoncave"
		:dir{:climb :southhallway}
		:contents #{}}
	:southhallway {:desc "There are rooms to your 'north', 'south', and 'east'."
		:title "in the south hallway of the Tower of Winterfell"
		:dir{	:down :dragoncave
				:north :infirmary
				:south :courtyard
				:east :lavaroom}
		:contents #{}}
	:infirmary {:desc "Around you, you notice a lot of chemicals. You're good at chemistry 
so you recognize one of them to be the recipe of a poison gas 'bomb'. There 
are also a bair 'boots' labelled Inferno Jordans. There is no other exit."
		:title "in the infirmary"
		:dir{:south :southhallway}
		:contents #{:poisongas
					:infernojordans}}
	:courtyard {:desc "You have reached a courtyard full of marching soldiers. 
To get past you must 'throw' some kind of distraction that can subdue all of them
like you did with the dogs."
		:title "in the courtyard full of soldiers"
		:dir{:throw :kingtower
			 :north :southhallway}
		:contents #{}}
	:lavaroom {:desc "This room has a path made of lava. To get to the other side you have walk
on it. You better have some type of special footwear, and you better 'run'."
		:title "in the lavaroom"
		:dir{:run :kingtower
			 :west :southhallway}
		:contents #{}}
	:kingtower{:desc "You walk into the chambers and confront the Evil King. 
Evil King: Ha! You dare show up here, slave! I am the Mighty King of Winterfell. 
		   And I own you now. You shall work for me until you die. Surrender now
		   and I will let you live.
You: I don't care who you are. I am sick of this place. And I am going to go home 
using that portal of yours no matter what. 
Evil King: Hahaha! You are a fool to challenge. I have the Sword of Black Magic that
has never been defeated. If you want to go home, you must defeat me in battle or 
remain my slave forever. Do you 'accept' my challenge?"
		:title "in the Tower of Winterfell"
		:dir{:fight :won}
		:contents #{}}
	:won{:desc "Type restart to play again..."
		:title "Victorious! You won the game"
		:dir{:restart :dungeon}}
	:death {:desc "Type restart to play again..."		
	    :title "dead"
		:dir {:restart :dungeon}}

	:riddleroom {:desc "
Riddler: Hello wanderer. How can I help you?

You: I need to get to the Tower of Winterfell but to get there I need to pass through 
     dragons undetected. Can you help me?

Ridder: Lucky for you, I have just the thing for you. But.. it comes with a price. You 
		need to prove your worth by taking up my challenge. Are you up for it?

You: Like I don't have enough challenges already. Fine, what do I need to do?

Riddler: It's simple. Accept my challenge, find the crystal room and pick up the
	purple-colored Dragon Crystal. It will be make you invisible to the dragons and you 
	will be able to pass through undetected. But to get there you will have pass
	through some obstacles. To successfully pass through you will have to use your wit
	and the these three crystals - Red, Blue and Green. Red will give you the gift of
	fire, Blue the gift of water and Green the gift of nature. Here you go... 
	Now you possess all three. Say 'proceed' to accept this challenge.

"
				:title "in the Riddler's den"
				:dir {:proceed :leafroom
					  :north :southcorridor}
				:contents #{:redcrystal 
							:bluecrystal 
							:greencrystal}}
	:leafroom  {:desc "
You see a dark trail ahead of you filled with plants. It's dead silent. You proceed to walk through it. 
Huh.. this seems easy enough. SUDDENLY YOU ARE ATTACKED BY GIANT TREE BRANCH! You go flying back and land
on the wet mud, and the mud starts to engulf you. You must use one of the three crystals ('red', 'green',
or 'blue') NOW!

"
				:title "on the Poison Ivy's trail"
				:dir {:next :fireroom}
			   }
	:fireroom  {:desc "
You are on a brigde across which there is a door. Under the bridge there is molten bubbling and 
blasting lava. You proceed to walk across it...
Suddenly, the lava shoots up towards you! You must use one of the crystals NOW!

"
				:title "inside the Fiendy Furnace"
				:dir {:next :zombieroom}
			   }
			   
	:zombieroom  {:desc " 
You find yourself transported to a graveyard, dark and silent. But a bunch of dark silhouettes approaching you. ZOMBIES! 
You must use one of your crystals NOW!
"
				:title "in middle of the Grumpy Grave"
				:dir {:next :crystalroom}
			   }
	:crystalroom  {:desc " 
Around you, you see a room full of crystals. You then see a purple-colored crystal in shape of
a dragon. You may 'pick' it up and proceed through the door at the 'south' end of the room. 

"
				:title "in the Crystal Room"
				:dir {:south :southcorridor}
				:contents #{:dragoncrystal}}
	})

(def gameitems
 {	:key {:desc "You get the feeling that it can be used to open a door."
		:title "a golden key"}
	:apple {:desc "A juicy apple, you get the feeling if you eat it you'll feel better."
		:title "an apple"}
	:steak {:desc "A fine piece of meat that you put in your bag to save it for later."
		:title "a piece of steak"}
	:battleaxe {:desc "A heavy weapon that can do some real damage."
		:title "a battleaxe"}
	:sword {:desc "A shiny sword that come in handy."
		:title "a sword"}
	:mysticcrystal {:desc "This crystal seems to have something magical about it. "
		:title "a mystic crystal"}
	:coins {:desc "The shiny gold coins might come in handy."
		:title "a bag of coins"}
	:swordofvalor{:desc "The oracle tell you that this magical ancient sword has accumulated the battle skills 
of its previous owners. It's also indestrutable. Might come in handy against the Evil King."
		:title "the Sword of Valor"}
	:dragoncrystal{:desc "The Riddler said that this crystal will save you from the dragons. Let's hope so."
		:title "the Dragon crystal"}
	:redcrystal{:desc "Gift of Fire"
		:title "the Red crystal"}
	:greencrystal{:desc "Gift of Nature"
		:title "the Green crystal"}
	:bluecrystal{:desc "Gift of Water"
		:title "the Blue crystal"}
	:poisongas{:desc "This might be useful against multiple enemies at once."
		:title "a poison gas bomb"}
	:infernojordans{:desc "These boots seem special. And they seem to be indesctructible."
		:title "the Inferno Jordans"}
})

(def adventurer
  {:location :dungeon
   :inventory #{};:key :steak :sword :mysticcrystal};:dragoncrystal} 
   :seen #{}})


(defn printinventory [xx]
      (dorun (for [i xx
          	 :let [m (-> gameitems i :title)]]
              (printf " %s;" m))))

(defn status [player]
  (let [location (player :location)
		inventory (player :inventory)]
    (print (str "
You are now " (-> gamelocations location :title) ". "))
    ;(print (apply str (map (fn [item] (-> gameitems item :title)) inventory)))
 	(when-not ((player :seen) location)
    	  (print (-> gamelocations location :desc)))
 	(printf "\nYour bag:")
 	(if (= inventory #{}) (println " empty") (printinventory inventory))
    (update-in player [:seen] #(conj % location))))


(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

(defn go [dir player]
  (let [location (player :location)
        dest (->> gamelocations location :dir dir)]
    (if (nil? dest)
      (do (println "You can't go that way.")
          player)
    (assoc-in player [:location] dest))))


(defn opendoor [dir player]
	(let [location (player :location)
          dest (->> gamelocations location :dir dir)
          con (-> player :inventory)]
        (if (= location :dungeon)
        	(if (contains? con :key) (assoc-in player [:location] dest) (do (println "The door seems to be locked. You need to find a way to open it.") player))
        	(do (println "That's an invalid command. You might want to use directions.") player))
        ))

(defn feeddog [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	 (if (= location :doghouse)
	 	(if (contains? con :steak)
	 		(do (println "
You take the piece of steak from your bag and throw it towards the approaching dogs.
They suddenly smell the steak and start devouring it. Phew! You successfully distracted them.
You walk towards the end of dungeon and exit from a door."
			) (assoc-in player [:location] dest))
	 		(do (println "
You search your bag to see if there is some food you can distract the dogs with.
Unfortunately you have don't. You have no option but to run back. But in vain. 
The dogs caught up to you and you were not able to survive their hunger.") (assoc-in player [:location] :death)))
	 	(do (println "That's an invalid command") player))	 
	))


(defn grab [item player]
	(let [location (player :location)
			con (-> gamelocations location :contents)
			title (-> gameitems item :title)
			desc (-> gameitems item :desc)
			possessions (-> player :inventory)]
			(if (contains? con item) 
				(if (contains? possessions item)
					(do (printf "\nYou already possess %s.\n" title) player)
					(do (printf "\nYou now possess %s. %s\n" title desc)(update-in player [:inventory] #(conj % item))))
				(do (println "That's not here to pick up.") player))))

(defn stab [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	 (if (= location :monsterpit)
	 	(if (contains? con :sword)
	 		(do (println "
You sneak up to the sleeping monster. You wield your sword and stab him. He wakes up 
howling and attacks you. You stab him again. He gives in and collapses. You won! 
You make your way to the other side of the pit and exit through a door.") (assoc-in player [:location] dest))
	 		(do (println "
You can't fight the monster since you don't have a weapon. You try to sneak around him.
Unfortunately, he wakes up and sees you. You run for it. You're almost by the door on the 
other side of the pit. You're gonna make it! Suddenly you feel a large hand grab you by the
ankle and pull you back... You were unable to survive the monster's rage.") (assoc-in player [:location] :death)))
	 	(do (println "That's an invalid command 1") player))	 
	))

(defn fightguard [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	 (if (= location :hallway)
	 	(if (contains? con :sword)
	 		(do (println "
You weilded your sword and charged at the guard. Before he had the time to react, you killed him.
You proceeded to the next room.") (assoc-in player [:location] dest))
	 		(do (println "You sneaked upon the guard punched him. He retaliated by weilding his sword. Without a weapon to
match his, you were killed.") (assoc-in player [:location] :death)))
	 	(do (println "That's an invalid command.") player))	 
	))

(defn bribeguard [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	 (if (= location :hallway)
	 	(if (contains? con :coins)
	 		(do (println "
You approached the guard and offered him money for information. He was reluctant but 
fortunately for you he was also corrupt.
Guard: Fine, so what do you want to know?
You: I don't know how I ended up here. Where is the way out? I need to get back to my home.
Guard: 'laughs' You will have better luck turning yourself in. This place is the magical 
	   kingdom of the the Evil Kind of Winterfell. If you want to escape, you will have to 
	   reach the Tower of Winterfell and kill the Evil King in his chambers. He possesses a 
	   magical portal that he uses to capture people from other time periods and makes them
	   his slaves. To escape you will need to use that portal to get home. But the Evil King 
	   has magical weapons. That sword of yours is no good, you will need to upgrade it. 
	   Find the Oracle... And don't come back here or I will have to turn you in."
	   ) (assoc-in player [:location] dest))
	 		(do (println "You approached the guard and offered him money for information and for letting you pass. 
He asked for payment first. You checked your pockets but there were no coins. The guard got infuriated 
and captured you. You were executed.") (assoc-in player [:location] :death)))
	 	(do (println "That's an invalid command.") player)) 
	))

(defn upgrade [item player]
	(let [con (player :inventory)
		  location (player :location)]
	(if (= location :oracleroom)
		(if (contains? con :mysticcrystal) (grab item player) 
			(do (println "You don't have the mystic crystal to upgrade your sword") player))
		(do (println "Invalid command. You can't upgrade anything here.") player))
	))


(defn proceed [player dir]
	(let [location (player :location)
		  dest (->> gamelocations location :dir dir)]
	(if (= location :riddleroom)
	(do (println "You have three crystals now: Red, Green and Blue")(assoc-in player [:location] dest))
	(do (println "That's not a valid command.") player)
	)))

(defn green [dir player]
	(let [location (player :location)
		  dest (->> gamelocations location :dir dir)]
	
		(cond (= location :leafroom) (do (println "
You yielded the Green crystal from your pocket and suddenly the mud stopped sinking. A tree branch came by and
lifted you off the mud and put you back on the trail. You start walking again. You see hungry wolves around you.
But there are scared to approach you because of the Green crystal. You successfully make it accross the trail. 
You come accross a door on a large tree trunk. You open it and proceed...
You magically get transported to a different place altogether."
			) (assoc-in player [:location] dest))
			  (= location :fireroom) (do (println "
You used the green crystal and called for nature to help. A number of trees sprouted around you to protect you. 
But the lava attack burned down those trees. The Green crystal was not right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
			  (= location :zombieroom) (do (println "
You used the green crystal and called for nature to help. A number of trees sprouted around you to protect you.
But the zombie easily climbed over the trees to reach you.
The Green cyrstal was not the right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
	    :else (do(println "That's not a valid command.") player)
		)))

(defn blue [dir player]
	(let [location (player :location)
		  dest (->> gamelocations location :dir dir)]
	
		(cond (= location :fireroom) (do (println "
Using the Blue crystal formed a shield of water around you and protected you from the lava as you
proceeded to walk across the bridge. You made it! You open the door..."
			) (assoc-in player [:location] dest))
			  (= location :leafroom) (do (println "
You unleashed a water canon on the mud, but that only made the mud soggier. You sank in the mud even 
faster. The Blue crystal was not right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
			  (= location :zombieroom) (do (println "
You unleashed the water canon on the zombies. They were pushed back but they continued to approach you.
But soon they grew large enough in number that you couldn't fight them all. The Blue cyrstal was not 
the right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
	    :else (do(println "That's not a valid command.") player)
		)))

(defn red [dir player]
	(let [location (player :location)
		  dest (->> gamelocations location :dir dir)]
	
		(cond (= location :zombieroom) (do (println "
You used the Red crystal and unleashed a dragon made of fire. The dragon flew around you burning
all the zombies that tried to attack you. But the zombies are rapidly growing in number and soon 
they will overpower the dragon. But wait... you see a hollow grave with a light coming out of it.
You approach it and see that there is a ladder going down it. You climb down. You reach a cave that
leads to a door. You open it... "
			) (assoc-in player [:location] dest))
			  (= location :fireroom) (do (println "
You used the Red crystal and unleashed a dragon of fire. But the fire dragon only enhanced the lava
attack. The Red crystal was not right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
			  (= location :leafroom) (do (println "
You used the Red crystal and unleashed a dragon of fire. The fire dragon burned the Poison Ivy trail
and started a forest fire. The Red crystal was not the right choice. You were not able to survive."
			  	) (assoc-in player [:location] :death))
	    :else (do(println "That's not a valid command") player)
		)))

(defn pickdragoncrystal [player]
	(let [location (player :location)]
	(if (= location :crystalroom) 
		(do (println "Congratulations! You now posses the Dragon Crystal! It will make you invisible to the dragons.")(grab :dragoncrystal player))
		(do (println "That's an invalid command. To pick something, type its name.") player)
	)))

(defn climbd [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	(if (= location :dragoncave)
		(if (contains? con :dragoncrystal) 
			(do (println "
Since you possess the Dragon Crystal, you were able to safely get past 
the dragons without being detected and you successfully climbed up
the ladder on the other end of the cave.") (assoc-in player [:location] dest)) 
			(do(println "You jumped into the water fall and swam to the end of the lake. 
You then start running towards the end of cave. Unfortunately for you, 
the dragons noticed you and they were hungry. You were unable to 
escape their fire breath.") (assoc-in player [:location] :death)) )
		(do (println "There is nothing to climb here.") player)) 
				))

(defn throwgas [dir player]
	(let [location (player :location)
			con (player :inventory)
			dest (->> gamelocations location :dir dir)]
	(if (= location :courtyard) 	
		(if (contains? con :poisongas) 
			(do (println "You sneakily throw the poison gas bomb in the courtyard and waited for the smoke 
to clear. You were able to put all the soldiers to sleep. You walk to the other end of
the courtyard.") 
			(assoc-in player [:location] dest)) (do(println "You don't have anything to throw at the soldiers in your bag.
One of soldiers has spotted you. You were captured.") (assoc-in player [:location] :death)) )
		(do (println "Invalid command. There is nothing to throw.") player)) 
				))

(defn runacross [dir player]
	(let [location (player :location)
		  con (player :inventory)
		  dest (->> gamelocations location :dir dir)]
	(if (= location :lavaroom)
		(if (contains? con :infernojordans) 
			(do (println "You run across the lava floor to the other end of the room.
To your surprise, the Inferno Jordans protected your feet. 
You made it to the other end!") (assoc-in player [:location] dest)) 
			(do(println "You started running across the lava floor, and as you look 
down, to your horror, your shoes have gone on fire. You're
40 feet away, but the pain paralyzes you and you collapse.") (assoc-in player [:location] :death)) )
		(do (println "Invalid command. Nowhere to run.") player))	 
				))

(defn fightking [dir player]
	(let [location (player :location)
			con (player :inventory)
				dest (->> gamelocations location :dir dir)]
		
		(if (= location :kingtower)
		(if (contains? con :swordofvalor) 
			(do (println "
You accept the Evil King's challenge and engage him in a fierce battle. 
The Evil King has the sword that would have spliced your sword had it 
not been the Sword of Valor. The Sword of Valor worked as if it 
had a brain of its own, guiding you through parrying each of the Evil 
King's moves. You tired out the Evil King. You can see his ego has got 
the best of him. His grip weakens a bit. And you seize
the opportunity. You knock his sword off his hand. 
You then finish him off. You then use the magic portal to get back home."
				) (assoc-in player [:location] dest)) 
			(do(println "You engage the king in battle. The first clash of swords proves fatal to 
your sword as it gets spliced in half. Your weapon wasn't strong enough to hold against
the Sword of Black Magic. The Evil King strikes again...") (assoc-in player [:location] :death)) )
		(do(println "That's not a valid command") player))
		))

(defn poisoned [player]
	(let [location (player :location)]
	(if (= location :kitchen)
		(do (println "You took the apple and took a bike of it. In sometime, you start feeling uneasy. You're vision starts
to become blurry. You collapse...  You were poisoned.") (assoc-in player [:location] :death))
		(do (println "Invalid command. There is no apple around.") player)
	)))


(defn describe [player]
	(do (println "
Use lowercase, single-word or double-word commands. For directions use, north, south, east, west, up, down.
To pick items simply mention them. Read the description of the room for 'hints'. For example, 
to open the door, say 'door'.
To repeat the description of a room, say 'look'. ") player))

(defn tock [player]
  (update-in player [:tick] inc))

(defn respond [player command]
  (match command
        [:look] (update-in player [:seen] #(disj % (-> player :location)))
        (:or [:n] [:north] ) (go :north player)
        [:south] (go :south player)
        [:east]  (go :east player)
        [:west]  (go :west player)
	  	[:up]  (go :up player)
	 	[:down] (go :down player)
	 	[:key] (grab :key player)
	 	[:bowl] (grab :bowl player)
	 	[:door] (opendoor :door player)
	 	[:apple] (poisoned player)
	 	[:steak] (grab :steak player)
	 	[:feed] (feeddog :feed player)
	 	[:battleaxe] (grab :battleaxe player)
		[:sword] (grab :sword player)
		[:stab] (stab :fight player)
		[:restart] (go :restart player)
		[:coins] (grab :coins player)
		[:mystic crystal] (grab :mysticcrystal player)
		[:attack] (fightguard :fight player)
		[:bribe] (bribeguard :bribe player)
		[:upgrade] (upgrade :swordofvalor player)
		[:proceed] (proceed player :proceed)
		[:green] (green :next player)
		[:red] (red :next player)
		[:blue] (blue :next player)
		[:pick] (pickdragoncrystal player)
		[:lookup] (describe player)
		[:climb] (climbd :climb player)
	 	[:boots] (grab :infernojordans player)
		[:bomb] (grab :poisongas player)
		[:throw]  (throwgas :throw player)
		[:run] (runacross :run player)
		[:accept] (fightking :fight player)

         _ (do (println "I don't understand you.")
               player)

         )) 

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (loop [local-map gamelocations
         local-player adventurer]
    (let [pl (status local-player)
          _  (println "
What do you want to do? ('lookup' to see commands)")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
