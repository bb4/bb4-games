/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Allow the user to specify a single order
 * TODO: show the distance when they have both origin and destination specified.
 *
 * @author Barry Becker
 */
final class OrderDialog extends OptionsDialog
                        implements ActionListener, ItemListener {

    private GradientButton okButton;

    private JComboBox<String> originCombo;
    private JComboBox<String> destinationCombo;

    private JLabel availableShips;
    private NumberInput numShips;
    private GalacticPlayer player;

    private int numYearsRemaining;
    /** total number of outgoing ships for each planet for which there are orders */
    private Map totalOutgoing;

    private static final int DEFAULT_FLEET_SIZE = 10;


    /**
     * constructor - create the tree dialog.
     */
    public OrderDialog(GalacticPlayer player, Map totalOutgoing, int numYearsRemaining) {

        this.totalOutgoing = totalOutgoing;
        this.numYearsRemaining = numYearsRemaining;
        this.player = player;

        showContent();
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    public JComponent createDialogContent() {

        JPanel mainPanel = new JPanel(true);
        setResizable(true);
        mainPanel.setLayout(new BorderLayout());

        JPanel buttonsPanel = createButtonsPanel();

        // add the form elements
        String labelText = GameContext.getLabel("ORIGIN");
        originCombo = new JComboBox<>();
        originCombo.addItemListener(this);
        initPlanetSelect(originCombo, player);
        JPanel originPanel = createComboInputPanel(labelText, originCombo);

        labelText = GameContext.getLabel("DESTINATION");
        destinationCombo = new JComboBox<>();
        destinationCombo.addItemListener(this);
        JPanel destPanel = createComboInputPanel( labelText, destinationCombo);
        initPlanetSelect(destinationCombo, null);
        availableShips = new JLabel();

        showAvailableShips(getOrigin());

        JPanel routePanel = new JPanel(new BorderLayout(), true);
        routePanel.setMinimumSize(new Dimension(30,60));
        routePanel.add(originPanel, BorderLayout.NORTH);
        routePanel.add(destPanel, BorderLayout.CENTER);
        routePanel.add(availableShips, BorderLayout.SOUTH);

        numShips = new NumberInput(GameContext.getLabel("NUMBER_OF_SHIPS_TO_SEND"), DEFAULT_FLEET_SIZE);

        mainPanel.add(routePanel, BorderLayout.NORTH);
        mainPanel.add(numShips, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * If player is null return a combo with all planets
     * @param combo the combo box to initialize valued for.
     * @param player show planets for specified player only. If null, show all planets.
     */
    private void initPlanetSelect(JComboBox<String> combo, GalacticPlayer player) {

        List<Planet> planets =  Galaxy.getPlanets(player);
        String sPlanets[] = new String[planets.size()];
        for (int i=0; i<planets.size(); i++)  {
            Planet planet = planets.get(i);
            sPlanets[i] = Character.toString(planet.getName());
        }
        ComboBoxModel<String> comboModel = new DefaultComboBoxModel<>(sPlanets);
        combo.setOpaque(true);
        combo.setModel(comboModel);
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    public JPanel createButtonsPanel() {

        JPanel buttonsPanel = new JPanel(true );

        okButton = new GradientButton();
        initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton(cancelButton(), GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton());

        return buttonsPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("MAKE_ORDER");
    }

    /**
     * Shows the number of available ships remaining for the specified planet
     * @param planet planet to show ships for
     */
    private void showAvailableShips(Planet planet) {
        assert(planet != null);

        int availShips = planet.getNumShips() - getOutgoingShips(planet);
        String[] arg = {(""+planet.getName()), Integer.toString(availShips)};
        String text = MessageFormat.format(GameContext.getLabel("AVAILABLE_SHIPS"), (Object[])arg);
        availableShips.setText(text);
    }

    private int getOutgoingShips(Planet planet) {
        int outgoing = 0;
        if (totalOutgoing.get(planet) != null)  {
           outgoing = (Integer) totalOutgoing.get(planet);
        }
        return outgoing;
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     */
    @Override
    public void actionPerformed( ActionEvent e ) {

        Object source = e.getSource();
        if (source == okButton) {
            // if there is not enough time to reach the planet, warn the user, and don't close the dlg.
            Order order = getOrder();
            // the order may be null if it was not valid
            if (order == null) {
                return;
            }
            double timeNeeded = order.getTimeNeeded();
            if (timeNeeded > numYearsRemaining) {
                JOptionPane.showMessageDialog(this,
                       "There are not enough years left (" + numYearsRemaining + ") in the game to reach that planet",
                       "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                this.setVisible(false);
            }
        }
        else if ( source == cancelButton()) {
            cancel();
        }
        else {
            GameContext.log(0, "actionPerformed source=" + source + ". not cancel and not ok" );
        }
    }

    /**
     * called when the origin combo changes
     */
    @Override
    public void itemStateChanged( ItemEvent e) {
        Object source = e.getSource();
        if (source == originCombo)  {
            showAvailableShips(getOrigin());
        }
        this.repaint(); // I don't think this should be necessary
    }

    /**
     * fill in the order properties based on specified field elements.
     * @return retrieve the specified order.
     */
    public Order getOrder() {

        Planet origin = getOrigin();
        Planet destination = getDestination();

        int fleetSize = getFleetSize();
        if (fleetSize > (origin.getNumShips() - getOutgoingShips(origin))) {
            JOptionPane.showMessageDialog(this, GameContext.getLabel("CANT_SEND_MORE_THAN_YOU_HAVE"));
            return null;
        }
        return new Order(origin, destination, fleetSize);
    }

    private Planet getOrigin() {
        return Galaxy.getPlanet(originCombo.getSelectedItem().toString().charAt(0));
    }

    private Planet getDestination() {
        return Galaxy.getPlanet(destinationCombo.getSelectedItem().toString().charAt(0));
    }

    private int getFleetSize() {
        return numShips.getIntValue();
    }

    /**
     * @param labelText  the left hand side label.
     * @param combo the dropdown of values
     * @return a combo element with a label on the left and a dropdown on the right.
     */
    private JPanel createComboInputPanel(String labelText, JComboBox<String> combo) {

        JPanel comboPanel = new JPanel(true);
        comboPanel.setLayout(new BoxLayout( comboPanel, BoxLayout.X_AXIS ));
        comboPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        JLabel label = new JLabel( labelText );
        comboPanel.add( label );

        comboPanel.add(new JPanel(true));
        comboPanel.add(combo);
        return comboPanel;
    }
}
