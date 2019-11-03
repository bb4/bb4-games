/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.Move;

import java.util.EventListener;

/**
 * This interface must be implemented by any class that wants to receive GameChangedEvents
 *
 * @author Barry Becker
 */
public interface GameChangedListener<M extends Move> extends EventListener {

    void gameChanged( GameChangedEvent<M> evt );
}
