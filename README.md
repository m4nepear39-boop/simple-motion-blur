# Simple Motion Blur (Fabric 1.21.11)

Frame-blending motion blur. Each frame is mixed with the previous result, so fast camera
movement leaves a smooth trail. Cost: three fullscreen passes, no per-frame allocations.
The HUD and menus are drawn after the blur, so they stay sharp.

## Build
1. Install JDK 21.
2. Open this folder in IntelliJ IDEA (Community is fine). Let Gradle import finish.
3. Gradle tab > Tasks > build > build
4. Your jar: `build/libs/simple-motion-blur-1.0.0.jar` (NOT the `-sources` one)

Command line instead: run `gradle wrapper --gradle-version 9.2.1` once, then `./gradlew build`.

## Install (MJLauncher or any launcher)
1. Create/select a Fabric 1.21.11 profile (Fabric Loader 0.18.1+).
2. Put these in the profile's `mods` folder:
   - Fabric API (0.141.1+1.21.11)
   - simple-motion-blur-1.0.0.jar

## Use (in game)
- `/motionblur`            show status
- `/motionblur on` / `off`
- `/motionblur level 1-10` (1 = light, 5 = default, 7-8 = strong PvP-style, 10 = very heavy)

Settings are saved in `config/simplemotionblur.properties`.
