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
$JAVA_HOME/bin/java -jar target/ImageTagManager-<version>-jar-with-dependencies.jar
```
