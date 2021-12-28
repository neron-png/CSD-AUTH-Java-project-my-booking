package Users.Actions.Graphical.Admin;

import Lodges.Lodge;
import Misc.HintedJTextField;
import Misc.Storage;
import Users.Actions.Graphical.GUIAction;
import Users.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LookupUsers extends GUIAction {
    @Override
    protected String getName() {
        return "Look up Users";
    }

    @Override
    protected void invoke() {

        actionArea.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        actionArea.add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        actionArea.add(mainPanel, BorderLayout.CENTER);

        topPanel.setLayout(new FlowLayout());

        topPanel.add(new JLabel("Username:"));
        HintedJTextField lrdUsername = new HintedJTextField("Username");
        topPanel.add(lrdUsername);
        JButton search = new JButton("Search");
        JButton searchall = new JButton("Show All");
        search.setFocusable(false);
        searchall.setFocusable(false);
        topPanel.add(search);
        topPanel.add(searchall);

        search.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();

            if (User.getUserFromUsername(lrdUsername.getText()) == null){
                mainPanel.add(error("No user found under that username!"));
                return;
            }

            mainPanel.add(User.getUserFromUsername(lrdUsername.getText()).toJPanel());

        });

        searchall.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();

            if (Storage.getUsers().size() == 0){
                mainPanel.add(error("No users have been registered!"));
                return;
            }

            mainPanel.setLayout(new FlowLayout());
//            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            for (User user : Storage.getUsers()){
                mainPanel.add(user.toJPanel());
                mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }

        });

    }
}