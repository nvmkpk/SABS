# Adhell no-root system adblock (no VPN)
Open-source ads and trackers blocker for Samsung devices

## FAQ
### How Adhell works?
With Knox Standard SDK: https://seap.samsung.com/sdk/knox-standard-android

### Only Samsung?
Yes


### Do I have to install the MyKnox app too?
No

### Does it block on everything or just Samsung's browser?
Blocks ads system wide.

### What exactly Adhell blocks?
Adhell takes urls to block from two sources (at least for now):
 - https://adaway.org/hosts.txt
 - http://pgl.yoyo.org/adservers/serverlist.php?hostformat=hosts&showintro=0&mimetype=plaintext

Then sorts them by popularity, checks if urls are reachable. And finally generates this list: https://github.com/adhell/adhell.github.io/blob/master/urls-to-block.json

For more information about generating urls-to-block list look at this repo: https://github.com/adhell/adprovider


### Which is better, Adhell of Disconnect Pro?

Adhell is free and open source.

### Any noticeable battery drain using this?
No

### Need to be rooted?
No

### What about YouTube native app?
You may have see some ads.

### I tried this but some in-app ads were still appearing
Try rebooting. The ads might have been cached.
