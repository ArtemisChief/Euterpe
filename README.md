# Euterpe

> a toy based on Midi for creating music in a way of coding

Simple editor and Midi player for a original designed music language, which supports limited but core functions of creating .mid type media file. Use it for making track, for playing music, or just for fun.

## Example

Run this

```
paragraph soprano
instrument= 0
volume= 127
speed= 140
1= D
3345 5432 <4444 4444>
1123 322 <4444 4*82>
3345 5432 <4444 4444>
1123 211 <4444 4*82>
2231 23431 <4444 4{88}44>
23432 12(5) <4{88}44 {44}4>
33345 54342 <{44}444 44{48}8>
1123 211 <4444 4*82>
end

paragraph alto
instrument= 0
volume= 110
speed= 140
1= D
1123 321(5) <4444 4444>
(3555) 1(77) <4444 4*82>
1123 321(5) <4444 4444>
(3555) (533) <4444 4*82>
(77)1(5) (77)1(5) <4444 4444>
(7#5#5#56#45) <4444 {44}4>
11123 3211(5) <{44}444 44{48}8>
(3555 533) <4444 4*82>
end

play(soprano&alto)
```

To get the [demo.mid](https://github.com/ArtemisiaChief/Euterpe/raw/master/demo.mid) (Ode to joy)

## Functions

* Use numbered musical notation style code to create .mid file
* Support 128 types of Midi instruments, check it at the help menu
* Load soundfont and play the score directly with simple player interface
* Set key mapping for convenience of typing sharp or flag notes

## Usage

For the numbers in < >
* 1 for whole note
* 2 for half note
* 4 for quarter note
* 8 for eighth note
* g for sixteenth note
* w for thirty-second note
* \* for dotted note
* { } for tie
  
For the numbers not in < >
* 1-7 for notes
* 0 for rest
* \# for sharp
* b for flat
* ( ) for ottava alta
* \[ \] for ottava bassa
* | | for the notes playing the same time

For the play function
* "," means play the second paragraph after the first one
* "&" means play the two paragraph at the same time

## Install

Requirement: Java Runtime Environment 8

The Euterpe project builds and tests on the following platforms:

* Microsoft Windows 10
* Linux (Ubuntu 18.04)

The runnable jar file is available below:
[Download Here](https://github.com/ArtemisiaChief/Euterpe/releases/download/1.0/Euterpe.v1.0.7z)


For the source code, just download the project and add the libraries in lib folder. As for IDEA, click "File - Project Structure - Libraries - Add" to add the three jar separately.
