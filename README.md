# ImageTagManager

<!---
[![start with why](https://img.shields.io/badge/start%20with-why%3F-brightgreen.svg?style=flat)](http://www.ted.com/talks/simon_sinek_how_great_leaders_inspire_action)
--->
[![GitHub release](https://img.shields.io/github/release/elbosso/ImageTagManager/all.svg?maxAge=1)](https://GitHub.com/elbosso/ImageTagManager/releases/)
[![GitHub tag](https://img.shields.io/github/tag/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/tags/)
[![GitHub license](https://img.shields.io/github/license/elbosso/ImageTagManager.svg)](https://github.com/elbosso/ImageTagManager/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/issues/)
[![GitHub issues-closed](https://img.shields.io/github/issues-closed/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/issues?q=is%3Aissue+is%3Aclosed)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/elbosso/ImageTagManager/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/elbosso/ImageTagManager.svg)](https://GitHub.com/elbosso/ImageTagManager/graphs/contributors/)
[![Github All Releases](https://img.shields.io/github/downloads/elbosso/ImageTagManager/total.svg)](https://github.com/elbosso/ImageTagManager)
[![Website elbosso.github.io](https://img.shields.io/website-up-down-green-red/https/elbosso.github.io.svg)](https://elbosso.github.io/)

## Overview

This project offers a visual tag manager for images - one can use it to add arbitrary tags to images. The tags are saved inside one hidden file for each directory. 
Tags can then be searched using ordinary command line tools as for example `grep` or any other tools able to find text (regular expressions) inside text files.
The application can be built by issuing

```
mvn compile package
```

and then starting the resulting monolithic jar file by issuing

```
$JAVA_HOME/bin/java -jar target/tagmanager-<version>-jar-with-dependencies.jar
```

## Usage

The application has been developed mainly with one main goal - to be as non-intrusive as possible. Therefore,
all important functionality - all functionality that has to do with the adding of tags - is usable using
only the keyboard.

### Concepts

Tags are always three-part-strings, the individual barts being separated with dots. One example for such a 
tag would be stationwagon.color.blue. Tags always follow a simple pattern: class.property.value. The first part 
describes a class - in our example a station wagon. The second part names a property that class might have -
in our example its color. and the third part names a possible value for that property - in our example it
is the color blue.

### The layout of the main window

The window is divided into 4 parts: to the left we have the GUI for adding/removing tags. This is split up into two 
components where onyly one of them is possible at any one time. 

One of those components is the *TagPanel*:
it groups together buttons for tags known to the application using the classes of the tags as keys
for the grouping. Each button has a different color - the reason for that becomes apparent when
we explain the component in the lower part of the main window. Furthermore, each button has a checkbox.
This checkbox is checked if the tag represented by the button is set on the image currently  worked on.

Tags can be set/unset by simply clicking on the corresponding button.

The second component on the left is the List of favourites: Each time a tag is added by whatever means
to an image, this action is countes for that tag. In the favourites list, the tags are sorted according to that
number - the ones used more often can be found on the top of the list. The first 12 of them can be added
to the image currently worked on by simply pressing the corresponding function key.

The next component is located on the top of the main window. It is a panel that only becomes visible when 
at least one tag is selected for the image currently worked on. It displays all tags currently set for that image.
It does this by holding a button for each of the tags. Tags can be removed by clicking on the corresponding
button in this panel.

The central area of the main window is reserved for the image selection/viewing component. It consists of two components:
To the left, there is a list containing miniature representations of the folder currently worked in.
This list can be scrolled using the mouse wheel. Some of the icon representations here can have a small
icon representing a folder on them. This designates a sub folder - if the user double-clicks on such an
Icon, the application changes to that folder and displays all Images inside it. If the user double-clicks on 
an ordinary icon, the corresponding image is loaded into the viewer component and its associated tags 
are read and the gui initialized accordingly (tag buttons checked,...)

The viewer has a toolbar above it with some actions for example to zoom the image freely or to lock the zoom factor 
in a way that the whole image is visible or the whole height/width of the image is visible.

The last component is at the bottom of the main window. It is a text entry field where the user can enter
arbitrary text. If she finishes the text entry by pressing return, the text in the text field at that point
is added as tag to the image currently worked on. If this is a new tag, a corresponding button is added
to the TagPanel at the left of the main window. This text field has some comfort functions: It offers 
auto completion for already present tags as the user types along. Possible completions are displayed in a list
the user can navigate using the keys CURSOR UP/DOWN and PgUP and PgDn. Pressing ENTER uses the selected
entry and the user can continue typing.

Furthermore, the user can type ALT BACK_SPACE to delete all text until the last dot. Two more key strokes
are defined to select the next/previous image to work on - those are ALT ENTER and ALT SHIFT ENTER.
At last, one more keystroke is defined to go up one folder - that key stroke is ALT CURSOR_UP. 

This text field always has the focus. It is not necessary to click it to be able to type in it.  
