# JavaFX-CONNECT4 🕹️ - version 1.0
<p align="left"> <img src="https://komarev.com/ghpvc/?username=bha-bilel&label=Project%20views&color=0e75b6&style=plastic" alt="bha-bilel" /> </p>

> Play Connect 4 with your friends!
> 
> Jump to the [Setup](#install-instructions) section to to download the game, enjoy!
> 
> Discover [How to play](#how-to-play) and give me your [Feedback](#feedback) on this project.
> 
> You can find 5 more board games in the [Collection](#collection) section, give them a try!

## Table of contents
* [The little step](#the-little-step)
* [Game Charachteristics](game-charachteristics)
* [Install Instructions](#install-instructions)
* [How to play](#how-to-play)
* [Collection](#collection)
* [Additional info](#additional-info)
* [Feedback](#feedback)
* [Copyright notice](#copyright-notice)

# The little step
This project is the **fourth** learning project of my collection.

This game was no challenge for my increasing skills, I made it for the fun of it.
###### The project in its current state is way more featured and structured than a basic connect 4 game

Developing the next two projects were my ultimate goal from the beginning, so make sure you check them out!

# Game Charachteristics
- Tabletop view (2D)
- Multiplayer (no AI)
- Light and dark themes
- Local and online mode
- Distributed (client/server)
- Socket programming (TCP + MultiCast)
- Three languages (English, French and Arabic)
- Message oriented communication (except game app)

# Install instructions
- First, you need to download the adequate version based on your OS:
  - windows: [64 bit](./setup/connectFour-win64.exe?raw=true), or [32 bit](./setup/connectFour-win32.exe?raw=true)
  - macOS: [10.10 or later](./setup/connectFour-mac.zip?raw=true)
  - linux: [s390x](./setup/connectFour-linux-s390x.zip?raw=true)
###### app executables created using jlink, and only tested on windows 7/10, I would appreciate if someone test it on mac/linux

After downloading, extract the archive to whatever location you want.

- Where to find game executable?
  - If you have mac or linux:
    - Navigate to the 'bin' folder inside the extracted folder
    - You will find 'connectFour.bat' which is the game executable
  - If you have windows, it's called 'launcher.bat' inside the extracted folder

**WARNING**: **MAKE SURE** you allow program through firewall and run it as administrator

# How to play

The first time you launch the game you'll be prompted with welcome alerts, that will let you pick your preferred language and theme, and teach you some shortcuts, after that you need to choose between local and online mode to get started.

![Main app gui](./screenshots/mainApp.png)

In the main menu, you'll need to enter a username, then either 
- Host a room.
- Join public/local rooms.
- or Join a specific room through its ID (only available in online mode).

![Join app gui](./screenshots/joinApp.png)

In the join menu, you'll either see all (non full) rooms in local mode, or a list of public rooms in online mode.

![Room app gui](./screenshots/roomApp.png)

After joining a **room** the player is presented with this gui, where he can:
- Start a **chat** with people already in the room
- Change his **name**
- Take an empty **place**
- Set his **ready** status

The host has in addition the ability to:
- Change the room's **privacy** to either public/private (only available in online mode)
- **Kick** someone out of the room
- **Start** the game

Host privileges are **passed** automatically to the next player if the host leaves the room

![Game app gui](./screenshots/gameApp.png)

The game works as a regular Connect4 game, but in a distributed environment, 
you can end the game without leaving the room using the **Game** menu on top.

# Collection
- [XO](https://github.com/BHA-Bilel/JavaFX-XO)
- [Checkers](https://github.com/BHA-Bilel/JavaFX-CHECKERS)
- [Chess](https://github.com/BHA-Bilel/JavaFX-CHESS)
- Connect4 (current)
- [Dominoes](https://github.com/BHA-Bilel/JavaFX-DOMINOS)
- [Coinche](https://github.com/BHA-Bilel/JavaFX-COINCHE)

# Additional info
- The game will create config files in "your_home_directory/.BG/.CONNECT4" to persist necessary info and your preferences updated, if you suddenly lose access to the game it may be that they became corrupted,  delete them and they'll be replaced with default ones automatically.
- For the time being, you can only play the game locally, as the [online server](https://github.com/BHA-Bilel/BG-SERVER) isn't deployed yet, if the project get a noticeable audience (which is very unlikely) I will host the server on some cloud service, but I will need some financial support.
- JavaFX apps can be deployed to android/ios devices so you might see some cross-platfom support in the future!
- All the collection was initially developed in **Eclipse IDE** in late 2019, before I migrated to **Intellij IDEA** in 2021 to code the remaining parts while redesigning some parts to fit the new workflow model.

- This project wasn't my first nor my last experience coding in JavaFX, I'll do my best to publish other projects on my GitHub.

- **All** of the projects/repositories in my profile are produced by an **individual** effort, that I coded from **scratch**.

# Feedback
What do you think of this project? leave your thoughts/recommendations!

<p align="center">
  <a href="https://gist.github.com/BHA-Bilel/8bd1e48295e4ddaa446384879eced441">
    <img src="https://gist.githubusercontent.com/BHA-Bilel/6eb01c298f0ccceff7511427afb52534/raw/ebb3b59e6e8af742699627d15672f28a1f144d26/feedback.gif" alt="Click here to give feedback!">
  </a>
</p>

# Copyright notice
This public repository contain purposely **unlicensed** source code (**NOT** open-source), 
that I only consider as a personal side project and a way to showcase my skills.
You can surely and gladly download and play my game, or view how it's made.

However, **I DO NOT** grant any kind of usage (Commercial, Patent, Private), Distribution or Modification of the source code contained in this repository.
For a **private** license agreement please contact me at: bilel.bha.pro@gmail.com
