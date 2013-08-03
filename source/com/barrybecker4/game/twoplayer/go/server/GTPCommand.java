/** Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.go.server;

/**
 * The allowed GTP commands (most are required, some are optional)
 * @author Barry Becker
 */
@SuppressWarnings({"ClassWithTooManyFields"})
enum GTPCommand {
    boardsize,
    clear_board,
    echo,
    echo_err,
    fixed_handicap,
    final_score,
    final_status_list,
    genmove,
    gogui_interrupt,
    list_commands,
    known_command,
    komi,
    name,
    play,
    protocol_version,
    reg_genmove,
    time_settings,
    time_left,
    undo,
    quit,
    tesujisoft_bwboard,
    tesujisoft_delay,
    tesujisoft_invalid,
    version
}
