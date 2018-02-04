# Simple AdBlocker for Samsung (SABS)
[![Logo](https://imgur.com/fNCaCMl.png "SABS")](https://github.com/LayoutXML/SABS/releases)

[![forthebadge](http://forthebadge.com/images/badges/gluten-free.svg)](http://forthebadge.com)
[![forthebadge](http://forthebadge.com/images/badges/powered-by-electricity.svg)](http://forthebadge.com)

[Download](https://github.com/LayoutXML/SABS/releases)

SABS is a system-wide, rootless AdBlocker, package disabler, permission manager and more. It works by using Samsung's KNOX SDK, therefore only works on Samsung devices. Because of a sheer amount of Samsung phones, all with different Android and KNOX versions, screen sizes and other specifications, SABS only focuses on Galaxy S8, S8+ and Note 8 devices with latest Android version. What makes SABS stand out from other adblockers is that it can make reversable system level changes because of the Samsung's KNOX tools, and doesn't run in the background. With it, you can easily block url domains, disable system apps that you can't in settings, remove permissions from apps that you can't usually control.

[![Screenshot1](https://imgur.com/OyYfMtZ.png)](https://github.com/LayoutXML/SABS/releases)
[![Screenshot2](https://imgur.com/FaO7yyE.png)](https://github.com/LayoutXML/SABS/releases)
[![Screenshot3](https://imgur.com/eOLQqV3.png)](https://github.com/LayoutXML/SABS/releases)

## Table of contents
* [Setup](https://github.com/LayoutXML/SABS#setup)
* [Issues](https://github.com/LayoutXML/SABS#issues)
* [FAQ](https://github.com/LayoutXML/SABS#faq)
* [Feedback](https://github.com/LayoutXML/SABS#feedback)
* [Contribute](https://github.com/LayoutXML/SABS#contribute)

## Setup
### To use SABS you need to:
1. Download the latest version (apk file) [here](https://github.com/LayoutXML/SABS/releases).
2. Sign in [here](https://seap.samsung.com/enrollment)
3. Go [here](https://seap.samsung.com/license-keys/create#section-knox-standard-sdk)
4. Go to "Legacy SDKs"
5. Go to "Knox Standard SDK"
6. Select "Enterprise license key"
7. Write any word in alias
8. Press "Generate license key"
9. Copy the key and paste it into the app when it asks for it. **If the key starts with letters "KLM", you didn't follow the steps correctly.** Complete the steps 4-9 again.

Steps 4-8 are demonstrated in [the picture here](https://i.imgur.com/LTAdkpW.png).

## Issues
* Sometimes license is not activated. Make sure you have the key that **does not** start with letters "KLM". If after multiple tries and app restarts you still receive a license activation error, reinstall the app.
* Sometimes adblocker stops working but other parts of the app continues to work if more than one adblocker is in use. If it happens to you, turn off blocking in all apps that use KNOX SDK such as Adhell, Disconnect pro etc, and SABS. Turn on blocking in SABS again. Wait a couple of minutes before concluding that adblocking doesn't work again (it takes some time to block all domains).
* Ads are not blocked in Youtube and Chrome. Youtube serves its ads from the same domain as videos so there's no way of blocking them. Chrome recently had some changes in how it works but luckily you can fix it [as explained here](https://www.xda-developers.com/fix-dns-ad-blocker-chrome/).
* Some apps that need internet or websites might work incorrecly if their domain is blocked. Whitelist them in settings. It is recommended to whitelist Google Music, Allo and Duo apps.

## FAQ

### Is using SABS legal?
Yes, getting a free license key from Samsung is legal and using it in SABS does not violate any terms and conditions.

Blocking ads itself is also not illegal but it's a moral grey area. Ask yourself - do you want to support app developers and website creators? If yes, then don't use an adblocker or whitelist those apps or websites in settings. Remember that by not blocking ads you are letting companies track websites you use and spy on you.

### Why do I have to get my own key?
For me to get a production license key means creating a company, becoming a Samsung's partner or buying an expensive license key from Samsung's partners. I can't afford that as I'm about to go to college and can barely afford the first year of my studies, as well as developing SABS is just my hobby.

### Will this work on my device?
SABS only works for some Samsung devices. If you use a Galaxy S8, S8+ or Note 8, you don't need to worry. If you are using a different Samsung device, try and see it yourself.

### Where does it block ads? Only browser or all apps?
SABS blocks ads in many apps including browsers. If you still see some ads, you can send a message to the developer on [Reddit](reddit.com/u/LayoutXML) with a list of apps where ads are not blocked.

### Where is the "hosts" file with blocked domains?
It's on the Github page. File is named standard-package.txt. You can add your own sources like [Adaway](https://adaway.org/hosts.txt) in app settings. Source file must only have url domains, 127.0.0.1s and comments in lines starting with #. Standard package is updated independently to app version.

### Can I use it with Disconnect Pro?
It is not recommended as both SABS and Disconnect Pro use Samsung's Firewall. SABS has all the features of Disconnect Pro and more.

### Does it drain battery?
No, not at all. SABS might even improve your battery as apps cannot constantly communicate with ad and tracker domains. SABS doesn't run in background because it doesn't need to.

### Do I have to be rooted?
No. SABS might not even work if you are rooted.

## Feedback
I'm eagerly waiting for your feedback on [Reddit](reddit.com/u/LayoutXML). Message me with your suggestions, bug reports and list of apps where ads are not blocked.

## Contribute
I welcome any help. Translations are not my priority at the moment and could wait at least for beta releases.
As a student (at school) with no background in Android development I'm struggling with a few things such as
* Permission search (list search)
* TXT file chooser and saver (for import/export of disabled packages)
* Sorting of apps, permissions
* Background ad domains updater, app updater.




---

SABS is licensed under the "MIT License". https://github.com/LayoutXML/SABS/blob/master/LICENSE.

Simple Ad Blocker for Samsung (SABS) is based on "Adhell 2" open source project (https://github.com/MilanParikh/Adhell2)
LayoutXML and SABS project contributors cannot guarantee that "Adhell 2" author(-s) and/or contributors and/or other affiliated people have permission to use, distribute and/or give permission for others to use and/or distribute code, source files, name, related files and other, follow all licensing agreements, rules and/or other agreements or rules, and therefore should not be held accountable for any use of "Adhell 2" code, files and/or other. LayoutXML (username "Minecrab" on various websites) has full permission from "Adhell 2" author on social media website "Reddit" (user name "FiendFyre498") to use "Adhell 2" code and files provided on website "github" in any way. 
