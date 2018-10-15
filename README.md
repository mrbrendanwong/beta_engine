# BETA (β) Engine
#### A choose-your-own-adventure game engine!

## What is this?
A DSL that allows a user to create their own choose-your-own-adventure game. Created for UBC CPSC 410.

Built on Java 8 with JDK version 1.8.0_181

## Try it out!
Download the .jar [here](https://drive.google.com/open?id=1qwTiAtOuj0PNbt0xrk85thoUz5RO7cv1)!

## Game Features
* Start screens: display the title of your game in style
* Multiple end screens: construct different ending for different player choices
* Choices: options for your player to progress the game
* Exposition choices: choices that don't progress the story. Perfect for creating cut scenes
* Conditional choices: choices that appear based on choices you made earlier in the game
* Timed choices: constrain the user to making their choices in X seconds or it's game over

## Technical Features
* Constrained image positioning: Layer and position your image on screen left, right, top, bottom, center
* BGM and sound effects: shape the atmosphere with unique background music and sound effects for every screen

## Grammar
```
PROGRAM 		::= GAME_SETTINGS START_SCENE STORY_SCENES DEATH_SCENES END_SCENES
GAME_SETTINGS	::= "GAME:" "title:" STRING "description:" STRING [STATS]
STATS			::= [STAT*]
STAT			::= NAME [INTEGER|STRING]
START_SCENE     ::= NAME CHOICES [TEXT|BGM|SOUND_EFFECT|PICTURE]*
STORY_SCENE		::= NAME CHOICES [TEXT|TIMER|SOUND_EFFECT|PICTURE]*
DEATH_SCENE		::= NAME [TEXT|BGM|SOUND_EFFECT|PICTURE]*
END_SCENE		::= NAME [TEXT|BGM|SOUND_EFFECT|PICTURE]*
TEXT			::= "text:" STRING*
CHOICES			::= [CHOICE*]
CHOICE			::= "choice:" TEXT "next scene:" STRING
TIMER			::= "timer:" INTEGER
BGM				::= "bgm:" STRING
SOUND_EFFECT	::= "sound effect:" STRING
PICTURE			::= "picture:" "file:" STRING "position:" ["top"|"bottom"|"left"|"right"|"center"]
NAME			::= [aA-zZ]*[0-9]*":"
```

## Coding Syntax
```
GAME:
	title: "<string>"
	description: "<string>"
	stats:	(optional)
		<stat name>: <int | string>
START SCENE:
	<start scene name>:
		text:
			*"<string>"
		choice:
			text: "<string>"
			next scene: <scene name>
		bgm: "<file name>"	(optional)
		sound effect: "<file name>"	(optional)
		picture:	(optional)
			file: "<file name>"
			position:<string [top|bottom|left|right|center]>
STORY SCENES:
	<story scene name>:
		text:
			*"<string>"
		choice:
			text: "<string>"
			next scene: <scene name>
			conditional: "<stat name> <[==, >=, <=, >, <] for int, [==] for string> <value>" (optional)
			change stat: "<stat name> <[+,-,*,/,=] for int, [=] for string> <value>" (optional)
		timer: <integer (seconds)>	(optional)
		    next scene: <scene name>
		bgm: "<file name>"	(optional)
		sound effect: "<file name>"	(optional)
		picture:	(optional)
			file: "<file name>"
			position: <string [top|bottom|left|right|center]>
DEATH SCENES:
	<death scene name>:
		text:
			*"<string>"
		bgm: "<file name>"	(optional)
		sound effect: "<file name>"	(optional)
		picture:	(optional)
			file: "<file name>"
			position:<string [top|bottom|left|right|center]>
END SCENES:
<end scene name>:
		text:
			*"<string>"
		bgm: "<file name>"	(optional)
		sound effect: "<file name>"	(optional)
		picture:	(optional)
			file: "<file name>"
			position: <string [top|bottom|left|right|center]>
```

## Troubleshooting
* Make sure your text, file names, conditionals, and stat changes are surround by quotes
* Ensure that your file paths are correct
* Only 4 choices will appear for each scene, even if you make more
* Follow the syntax to a T
* Start scenes shouldn't have timers as they act as the title screen for the game

## Current Issues
* Due to how the DSL is parsed, any underscore character that is outside of quotes may cause errors.
If there is an underscore anywhere, it will need to be surrounded in quotes, even if the syntax does not say to do so.