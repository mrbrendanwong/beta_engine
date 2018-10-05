# BETA Engine
#### A choose-your-own-adventure game engine!

## What is this?
A DSL that allows a user to create their own choose-your-own-adventure game. Created for UBC CPSC 410.

## Game Features
* Lives: allow for the user to make X incorrect choices and redo the last scene
* Timed choices: constrain the user to making their choices in X seconds or it's game over
* Choices: options for your player to progress the game
* Exposition choices: choices that don't progress the story. Perfect for creating cutscenes
* Conditional scenes: scenes that appear based on choices you made earlier in the game
* Conditional choices: choices that appear based on choices you made earlier in the game
* Start screens: display the title of your game in style
* Multiple end screens: construct different ending for different player choices

## Technical Features
* Constrained image positioning: position your image on screen left, right, top, bottom, center
* BGM and sound effects: shape the atmosphere with unique background music and sound effects for every screen

## Code Example
```
GAME:
	title: <string>
	description: <string>
	lives: <non-negative integer>
	start_screen: <start scene name> (removed)
	stats:	(optional)
		<stat name>: <int | string>
START SCENE:
	<start scene name>:
		text:
			*”<string>”
		choice:
			text: “<string>”
			next scene: <scene name>
		timer: <integer (seconds)>	(optional)
		bgm: <file name>	(optional)
		sound effect: <file name>	(optional)
		picture:	(optional)
			file: <file name>
			position:<string [top|bottom|left|right|center]>
STORY SCENES:
	<story scene name>:
		text:
			*“<string>”
		choice:
			text: “<string>”
			next scene: <scene name>
			conditional: "<stat name> <[==, >=, <=, >, <] for int, [==] for string> <value>" (optional)
			change stat: "<stat name> <[+,-,*,/,=] for int, [=] for string> <value>" (optional)
		timer: <integer (seconds)>	(optional)
		bgm: <file name>	(optional)
		sound effect: <file name>	(optional)
		picture:	(optional)
			file: <file name>
			position:<string [top|bottom|left|right|center]>
DEATH SCENES:
	<death scene name>:
		text:
			*“<string>”
		bgm: <file name>	(optional)
		sound effect: <file name>	(optional)
		picture:	(optional)
			file: <file name>
			position:<string [top|bottom|left|right|center]>
END SCENES:
<end scene name>:
		text:
			*“<string>”
		bgm: <file name>	(optional)
		sound effect: <file name>	(optional)
		picture:	(optional)
			file: <file name>
			position:<string [top|bottom|left|right|center]>
```