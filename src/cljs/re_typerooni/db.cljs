(ns re-typerooni.db
  ;;(:require [schema.core :as s])
  )

(defonce words-10ff ["or" "line" "important" "may" "life" "mountain" "went"
                     "change" "along" "water" "through" "just" "look" "because" "than" "into"
                     "three" "after" "does" "stop" "get" "eye" "small" "world" "carry" "play"
                     "all" "really" "before" "don't" "family" "river" "enough" "another" "came"
                     "number" "why" "might" "write" "must" "other" "air" "something" "even" "own"
                     "children" "in" "keep" "saw" "kind" "see" "without" "country" "left" "night"
                     "would" "for" "name" "being" "far" "quickly" "down" "between" "like" "miss"
                     "to" "very" "which" "who" "seem" "some" "large" "letter" "food" "got" "never"
                     "feet" "part" "from" "have" "away" "sound" "same" "those" "take" "great"
                     "make" "no" "said" "day" "mean" "plant" "she" "book" "quick" "most" "hard"
                     "way" "where" "long" "and" "say" "follow" "around" "each" "young" "animal"
                     "he" "land" "been" "do" "car" "place" "together" "Indian" "will" "sentence"
                     "group" "point" "different" "white" "can" "page" "them" "thought" "song" "new"
                     "watch" "oil" "we" "ask" "two" "not" "any" "boy" "are" "list" "an" "if" "be"
                     "city" "time" "end" "these" "picture" "show" "out" "tree" "over" "by" "run"
                     "form" "head" "leave" "house" "you" "start" "right" "his" "that" "hear" "on"
                     "so" "her" "how" "high" "me" "my" "sea" "study" "help" "is" "move" "your"
                     "face" "what" "read" "it's" "their" "big" "use" "too" "below" "light" "when"
                     "give" "near" "its" "of" "want" "later" "found" "home" "it" "both" "then"
                     "talk" "earth" "until" "set" "only" "idea" "men" "quite" "old" "off" "took"
                     "has" "last" "us" "school" "made" "had" "hand" "second" "tell" "come" "well"
                     "example" "year" "girl" "much" "every" "walk" "up" "here" "spell" "need"
                     "there" "next" "answer" "open" "him" "add" "such" "many" "learn" "was"
                     "people" "as" "with" "mother" "word" "our" "first" "did" "America" "could"
                     "were" "now" "back" "find" "work" "under" "still" "little" "eat" "father"
                     "also" "state" "thing" "try" "think" "often" "paper" "turn" "above" "go"
                     "once" "they" "call" "the" "close" "this" "grow" "one" "while" "sometimes"
                     "story" "about" "but" "cut" "at" "few" "question" "began" "almost" "let" "put"
                     "again" "side" "good" "four" "always" "mile" "soon" "know" "man" "should"
                     "live" "begin" "more"])

#_(def WORD-ID s/Int)
#_(def WORDLET {:id WORD-ID :correctness s/Bool :backspace-used s/Bool})
#_(def schema {:name s/Str
               :timer s/Int
               :status (s/enum
                        :new
                        :started
                        :finished)
               :current-input s/Str
               :current-word-timestamps [s/Int]
               :row-offset s/Int
               :current-word s/Int
               :test-words [WORDLET]})

(def default-game
  {:name "Words Per Minute"
   :timer 60
   :status :new ;; statue should be :new :started :finished
   ;;   :dictionary words-10ff
   :current-input ""
   :current-backspace-used false
   :current-word-timestamps []
   :num-row-in-display 3
   :row-offset 0
   :current-word 0
   :test-words []})

;; TODO add schema validation
