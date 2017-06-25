#bb4-games

A java game playing framework and a collection of two-player and multi-player games.
In all games, one or more intelligent computer opponents are allowed to play.
Here are the game implementations added so far (hopefully others will contribute more)
   - Two player
      - [Chess](http://barrybecker4.com/applets/chess_en.html)
      - [Checkers](http://barrybecker4.com/applets/checkers_en.html)
      - [Go](http://barrybecker4.com/applets/go_en.html)
      - [GoMoku](http://barrybecker4.com/applets/gomoku_en.html)
      - [Blockade](http://barrybecker4.com/applets/blockade_en.html)
      - [Mancala](http://barrybecker4.com/applets/mancala_en.html)
      - [Hex](http://barrybecker4.com/applets/hex_en.html)
   - Multi-player
      - [Poker](http://barrybecker4.com/applets/poker_en.html)
      - [Galactic](http://barrybecker4.com/applets/galactic_en.html)


### [How to Build](https://github.com/barrybecker4/bb4-common/wiki/Building-bb4-Projects)

If you have not already done so, first install [Git](http://git-scm.com/), and [Intellij](http://www.jetbrains.com/idea/).

Type 'gradle buildw' at the root (or ./gradlew if running in Cygwin).
If you want to open the source in Intellij, then first run 'gradle idea'.
Use gradle tasks to see all the different games that can be run using a command like 'gradle runGoMoku'

When there is a new release, versioned artifacts will be published by Barry Becker to [Sonatype](https://oss.sonatype.org).

### License
All source (unless otherwise specified in individual file) is provided under the [MIT License](http://www.opensource.org/licenses/MIT)





