# bb4-games

A java game playing framework and a []collection of two-player and multi-player games](http://barrybecker4.com/bb4-projects/bb4-games/games_index.html).
In all games, one or more intelligent computer opponents are allowed to play.
Here are the game implementations added so far.
   - Two player
      - Chess
      - Checkers
      - Go
      - GoMoku
      - Blockade
      - Mancala
      - Hex
   - Multi-player
      - Poker
      - Galactic
      - Set


### [How to Build](https://github.com/barrybecker4/bb4-common/wiki/Building-bb4-Projects)

If you have not already done so, first install [Git](http://git-scm.com/), and [Intellij](http://www.jetbrains.com/idea/).

Type 'gradle buildw' at the root (or ./gradlew if running in Cygwin).
If you want to open the source in Intellij, then first run 'gradle idea'.
Use gradle tasks to see all the different games that can be run using a command like 'gradle runGoMoku'

When there is a new release, versioned artifacts will be published by Barry Becker to [Sonatype](https://oss.sonatype.org).

### License
All source (unless otherwise specified in individual file) is provided under the [MIT License](http://www.opensource.org/licenses/MIT)





