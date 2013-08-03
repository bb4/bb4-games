/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.AbstractDialog;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.List;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
public final class OrdersDialog extends OptionsDialog  {

    private GalacticPlayer player_;
    //private Galaxy galaxy_;
    private GradientButton addOrderButton_;
    private GradientButton removeOrderButton_;

    private OrdersTable ordersTable_;
    private GradientButton okButton_;

    private int numPreExistingOrders_;
    private int numYearsRemaining_;

    private static final long serialVersionUID = 0L;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public OrdersDialog(Component parent, GalacticPlayer player, int numYearsRemaining) {
        super(parent);
        player_ = player;
        ordersTable_ = new OrdersTable(player.getOrders());
        numPreExistingOrders_ = ordersTable_.getNumRows();
        numYearsRemaining_ = numYearsRemaining;

        commonInit();
        showContent();
    }

    @Override
    protected JComponent createDialogContent() {
        setResizable( true );

        JPanel buttonsPanel = createButtonsPanel();

        // the table title and add button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(AbstractDialog.createMarginBorder());

        //String[] arg = {player_.getName()};
        String title =  MessageFormat.format(GameContext.getLabel("CURRENT_ORDERS"), player_.getName());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(player_.getColor().darker());
        titleLabel.setIcon(player_.getIcon());
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel addremoveButtonsPanel = new JPanel();

        addOrderButton_ = new GradientButton(GameContext.getLabel("NEW_ORDER"));
        addOrderButton_.addActionListener(this);
        addremoveButtonsPanel.add(addOrderButton_, BorderLayout.CENTER);

        removeOrderButton_ = new GradientButton(GameContext.getLabel("REMOVE_ORDER"));
        removeOrderButton_.addActionListener(this);
        addremoveButtonsPanel.add(removeOrderButton_, BorderLayout.EAST);

        titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(ordersTable_.getTable());
        scrollPane.setPreferredSize(new Dimension(360,120));
        scrollPane.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                scrollPane.getBorder()));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("CURRENT_ORDERS_TITLE");
    }

    /**
     *  create the OK Cancel buttons that go at the button
     */
    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    /**
     *
     * @return  the orders in the table
     */
    public List<Order> getOrders() {
        return ordersTable_.getOrders();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == okButton_) {
            this.setVisible(false);
        }
        else if ( source == cancelButton) {
            cancel();
        }
        else if (source == addOrderButton_) {
            addOrder();
        }
        else if (source == removeOrderButton_) {
            if  (ordersTable_.getNumRows() > numPreExistingOrders_) {
                ordersTable_.removeRow(ordersTable_.getNumRows()-1);
            }
            else {
                JOptionPane.showMessageDialog(this, "Can't remove pre-existing orders",
                                              "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * add another row to the end of the table.
     */
    private void addOrder()  {
        // open a dlg to get an order
        OrderDialog orderDialog =
                new OrderDialog(player_, ordersTable_.getCurrentOutGoingShips(), numYearsRemaining_);

        orderDialog.setLocation((int)(this.getLocation().getX() + 40), (int)(this.getLocation().getY() +170));


        boolean canceled = orderDialog.showDialog();

        if ( !canceled ) { // newGame a game with the newly defined options
            Order order = orderDialog.getOrder();
            if (order != null)
                ordersTable_.addRow(order);
        }
    }

}

