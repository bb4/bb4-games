#bb4-games



A java game playing framework and a collection of two-player and multi-player games.
In all games, one or more intelligent computer opponents are allowed to play.
Here are the game implementations added so far (hopefully others will contribute more)
   - Two player
      - [Chess](http://barrybecker4.com/applets/chess_en.html)
      - [Checkers](http://barrybecker4.com/applets/checkers_en.html)
      - [Go](http://barrybecker4.com/applets/go_en.html)
      - [Pente](http://barrybecker4.com/applets/pente_en.html)
      - [Blockade](http://barrybecker4.com/applets/blockade_en.html)
   - Multi-player
      - [Poker](http://barrybecker4.com/applets/poker_en.html)
      - [Galactic](http://barrybecker4.com/applets/galactic_en.html)
      - [Set](http://barrybecker4.com/applets/set_en.html)


### [How to Build](https://github.com/barrybecker4/bb4-common/wiki/Building-bb4-Projects)

If you have not already done so, first install [Git](http://git-scm.com/), [Gradle](http://www.gradle.org/), and [Intellij](http://www.jetbrains.com/idea/).

Type 'gradle build' at the root.
If you want to open the source in Intellij, then first run 'gradle idea'.
Use gradle tasks to see all the different games that can be run using a command like 'gradle runPente'

When there is a new release, versioned artifacts will be published by Barry Becker to [Sonatype](https://oss.sonatype.org).

### License
All source (unless otherwise specified in individual file) is provided under the [MIT License](http://www.opensource.org/licenses/MIT)





