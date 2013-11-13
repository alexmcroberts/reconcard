# Recon Cards
Recon Cards is essentially a slideshow app which allows for rapid prototyping of app designs for any Recon HUD. It is possible to link 
slides together. Actions mentioned in the syntax section below are matched 1:1 to the actions of a typical Android D-Pad.

### Running Recon Cards
Install using adb from the build directory (find where the .apk is in the project root after the project is built):

    adb -d install -r reconcard.apk
    adb -d shell "rm -r /mnt/storage/multi_card/"
    adb -d shell "mkdir /mnt/storage/multi_card/"
    adb -d push assets /mnt/storage/multi_card/
    adb -d shell "am start -n com.reconinstruments.reconcard.multi/com.reconinstruments.reconcard.multi.ApplicationListViewActivity"

The resources you need to modify are the xml file in card, and the png (jpg, bmp also allowed) files that are displayed.
Once this is more robust, we'll update this readme with the commands that are allowed.

### Example Resources

There are example resources under the asset folder of this repo. The last four steps above should how to copy these to "/multi_card".
These "apps" will appear in the initial screen of the Recon Cards app. These "apps" are identical except for their title, and the assets they point too - the behaviour
is the same. There is no theoretical limit to the number of "apps" that can be displayed in this list.

### Syntax
* id: Assigned to each image, should be a unique integer e.g. 1
* next: An integer pointing to id of another image. Used in conjunction with delay
* delay: How long a delay in milliseconds before jumping to the integer stored in 'next'.
* left: An integer pointing to id of another image, which is shown on pressing the LEFT button
* right: An integer pointing to id of another image, which is shown on pressing the RIGHT button
* up: An integer pointing to id of another image, which is shown on pressing the UP button
* down: An integer pointing to id of another image, which is shown on pressing the DOWN button
* back: An integer pointing to id of another image, which is shown on pressing the BACK button
* animation-in: Optional. The animation to be used. Defaulting to none by not including this, options include (slide) "left"; "right", "up", "down", (scale) "fade-in", "fade-out".
* animation-in-duration: Integer in milliseconds to determine how long the animation should take, defaulting to 1000, if not included when animation-in is used.

**Sample:**
    `<ReconCard id="1" img="directions1.png" left="8" right="7" next="2" animation-in="fade-in" />`
