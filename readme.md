# Running ReconCardActivity
Install using adb.

The resources you need to modify are the xml file in card, and the png (jpg, bmp also allowed) files that are displayed. Once this is more robust, we'll update this readme with the commands that are allowed.

## Syntax
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