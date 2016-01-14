# Release Notes

## Version 2.2.0 (01/15/2016)

* Extended patterns for pronouns
	* "His father John, => (John; [is] father [of]; Him)"
* Updated relnoun_prefixes list
* (Selective Prefix check) Less dependency of relnoun_prefixes : Better extractions in the sentences with relational prefixes
	* "Iranian film director Jafar Panahi => (Jafar Panahi; [is] film director [of]; Iran)"

## Version 2.0.0 (12/30/2015)

* Allows nnp relation words (previous version only allowed nn relation words)
* Works well with Demonyms
	* "Indian president Mukherjee => Mukherjee; [is] president [of]; India"
* Modified the patterns to allow reloun_prefixes (500+ prefixes list as of now)
	* "West Bengali Chief Minister Mamata Banerjee => (Mamata Banerjee; [is] Chief Minister [of]; West Bengal)"
* Title Extractor (configurable) 
	* "President Obama was born in Hawaii on August 4, 1961 => (Obama; [is] President [of]; [UNKNOWN])"
* AppositiveExtractor2 
	* "Lauren Faust, a cartoonist, => (Lauren Faust; [is]; a cartoonist)"
* OfCommaExtractor 
	* "The father of Michael, John, => (John; [is] The father of; Michael)"
* Distinguishes the [from] & [of] extractions
	* "Indian player Sachin Tendulkar received the Arjuna Award in 1994. => (Sachin Tendulkar, [is] player [from], India)"
	* "United States President Barack Obama gave a speech today. => (Barack Obama, [is] President [of], United States)"
* Includes a File Mode
