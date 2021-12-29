package Users.Actions.Graphical.Customer;

import Lodges.Amenities;
import Lodges.Lodge;
import Misc.HintedJTextField;
import Misc.Storage;
import Users.Actions.Graphical.GUIAction;
import Users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class LookupLodges extends GUIAction {
    @Override
    protected String getName() {
        return "Look up Lodges";
    }

    @Override
    protected void invoke() {

        actionArea.setLayout(new BorderLayout());



        JPanel sidepanel = new JPanel();
        sidepanel.setLayout(new BorderLayout());
//        actionArea.add(sidepanel, BorderLayout.WEST);
        JPanel optionsArea = new JPanel();
        optionsArea.setLayout(new BoxLayout(optionsArea, BoxLayout.Y_AXIS));
        sidepanel.add(optionsArea, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
//        actionArea.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new FlowLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidepanel, mainPanel);
//        sidepanel.setMinimumSize(new Dimension(10, 10));
        actionArea.add(splitPane);

        HashMap<JCheckBox, Amenities> checkBoxes = new HashMap<>();
        for (Amenities amenity : Amenities.values()){
            JCheckBox checkBox = new JCheckBox(amenity.label);
            checkBox.setFocusable(false);
            optionsArea.add(checkBox);
            checkBoxes.put(checkBox, amenity);
        }

        HintedJTextField place = new HintedJTextField("Where?");
        place.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) place.getPreferredSize().getHeight()));
        optionsArea.add(place);

        JButton search = new JButton("Search");
        sidepanel.add(search, BorderLayout.SOUTH);

        search.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();

            HashSet<Amenities> selectedAmenities = new HashSet<>();
            for (JCheckBox checkBox : checkBoxes.keySet()){
                if (checkBox.isSelected()){
                    selectedAmenities.add(checkBoxes.get(checkBox));
                }
            }

            for (Lodge lodge : Storage.getLodges()){
                if (lodge.getAmenities().containsAll(selectedAmenities) && (place.getText().isBlank() ||
                        place.getText().equalsIgnoreCase(lodge.getDetails().getLocation()))){
                    mainPanel.add(lodge.toJPanel());
                }
            }
        });


    }
}