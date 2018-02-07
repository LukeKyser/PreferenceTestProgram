package gui.setup;

import database.*;
import logic.setup.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import static gui.MainGUI.createSetupGUI;
import static resources.Constants.BUSINESS_NAME;


/**
 * AdminSetupMain
 * <p>
 * CustomizeForm Class
 *
 * @author Leron Tolmachev and Brooke Higgins
 * @version 2017.12.08
 * <p>
 * Change Log:
 * - Initial version, launches GUI.
 * -Updated the combo box.
 * -Added modules to help with functionalty.
 */
public class CustomizeForm {

    private static final Dimension SIZE_LARGE = new Dimension(650, 550);
    private static final Dimension SIZE_MEDIUM = new Dimension(650, 350);
    private static final Dimension SIZE_SMALL = new Dimension(650, 275);
    private static final String COMPLETE_CONFIRM_TEXT = "Your test changes have been successfully saved.\nPlease select an option below.";
    private static final String COMPLETE_CONFIRM_TITLE = "Changes Saved";
    private static final String SORT_RANDOM_DESCRIPTION = "Each time the a user takes this test, both the question order and item order ( A | B vs B | A ) will be randomly generated.";
    private static final String SORT_RANDOMUNBIASED_DESCRIPTION = "Each time the a user takes this test, both the question order and item order ( A | B vs B | A ) will be randomly generated in such a way that items are not likely to appear in multiple questions in a row.";
    private static final String SORT_ROTATIONAL_DESCRIPTION = "Question order is fixed for all test attempts. Questions will appear in A|B, B|C, C|D, A|C, B|D...order. Item order ( A | B vs B | A )can be randomly generated by checking the \"Randomize Item Order\" box.";
    private static final String SORT_SEQUENTIAL_DESCRIPTION = "Question order is fixed for all test attempts. Questions will appear in A|B, A|C, A|D, B|C, B|D... order. Item order ( A | B vs B | A )can be randomly generated by checking the \"Randomize Item Order\" box.";
    private static final String SORT_CUSTOM_DESCRIPTION = "Question order and Item order are fixed for all test attempts. You can customize the Question and Item order using the buttons to the right of the Question list.";

    private JPanel rootPanel;
    private final TestSetup testSetup;

    private JLabel businessLabel;
    private JLabel descriptionLabel;
    private JTextField questionTextField;
    private JLabel questionTextIcon;
    private JTextField tieTextField;
    private JLabel tieButtonTextIcon;
    private JComboBox<String> sortMethodComboBox;
    private JPanel matchUpPanel;
    private JList<MatchUp> matchUpList;
    private DefaultListModel<MatchUp> matchUpListModel;
    private JPanel customOrderPanel;
    private JButton moveUpButton;
    private JButton swapButton;
    private JButton moveDownButton;
    private JCheckBox itemOrderCheckBox;
    private JButton cancelChangesButton;
    private JButton submitButton;
    private JTextPane descriptionTextPane;
    private JLabel sortDemo;
    private JPanel fixedOrderPanel;

    /**
     * Constructor for SetupForm Class
     *  @param testSetup stores the business logic for AdminSetup Package
     * @param frame     JFrame containing SetupForm GUI
     */
    public CustomizeForm(TestSetup testSetup, JFrame frame) {

        rootPanel.setPreferredSize(SIZE_SMALL);
        businessLabel.setText(BUSINESS_NAME);

        this.testSetup = testSetup;

        // Set Icons
        questionTextIcon.setIcon(new ImageIcon(getClass().getResource("/Resources/warning.gif")));
        questionTextIcon.setVisible(false);
        tieButtonTextIcon.setIcon(new ImageIcon(getClass().getResource("/Resources/warning.gif")));
        tieButtonTextIcon.setVisible(false);
        moveUpButton.setIcon(new ImageIcon(getClass().getResource("/Resources/customUp.png")));
        swapButton.setIcon(new ImageIcon(getClass().getResource("/Resources/customSwap.png")));
        moveDownButton.setIcon(new ImageIcon(getClass().getResource("/Resources/customDown.png")));

        matchUpList.setFont(new Font("Monospaced", Font.BOLD, 12));

        // Set Action Commands
        moveUpButton.setActionCommand("Up");
        moveDownButton.setActionCommand("Down");
        swapButton.setActionCommand("Swap");

        // Hide panels
        descriptionTextPane.setVisible(false);
//        descriptionTextPane.setOpaque(false);
        descriptionTextPane.setBackground(new Color(128, 128, 128, 0));
        matchUpPanel.setVisible(false);
        customOrderPanel.setVisible(false);
        fixedOrderPanel.setVisible(false);

        //Changes frame according to selection
        FocusAdapter validateField = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String field = ((JTextField) e.getComponent()).getToolTipText();
                validate(field);
            }
        };
        ActionListener selectSort = e -> {
            submitButton.setEnabled(true);
            if(sortMethodComboBox.getSelectedIndex() != 0) {
                descriptionTextPane.setVisible(true);
                assert sortMethodComboBox.getSelectedItem() != null;
                switch ((String) sortMethodComboBox.getSelectedItem()) {
                    case "Random":
                        descriptionTextPane.setText(SORT_RANDOM_DESCRIPTION);
                        matchUpPanel.setVisible(false);
                        sortDemo.setVisible(false);
                        itemOrderCheckBox.setSelected(true);
                        rootPanel.setPreferredSize(SIZE_MEDIUM);
                        break;
                    case "Random (Unbiased)":
                        descriptionTextPane.setText(SORT_RANDOMUNBIASED_DESCRIPTION);
                        matchUpPanel.setVisible(false);
                        sortDemo.setVisible(false);
                        itemOrderCheckBox.setEnabled(true);
                        rootPanel.setPreferredSize(SIZE_MEDIUM);
                        break;
                    case "Rotational":
                        descriptionTextPane.setText(SORT_ROTATIONAL_DESCRIPTION);
                        setMatchUpList("Rotational");
                        sortDemo.setIcon(new ImageIcon(getClass().getResource("/Resources/rotational.gif")));
                        sortDemo.setVisible(true);
                        matchUpPanel.setVisible(true);
                        customOrderPanel.setVisible(false);
                        itemOrderCheckBox.setSelected(testSetup.getTest().getSettings("ItemOrder").equals("Random"));
                        fixedOrderPanel.setVisible(true);
                        rootPanel.setPreferredSize(SIZE_LARGE);

                        break;
                    case "Sequential":
                        descriptionTextPane.setText(SORT_SEQUENTIAL_DESCRIPTION);
                        setMatchUpList("Sequential");
                        sortDemo.setIcon(new ImageIcon(getClass().getResource("/Resources/sequential.gif")));
                        sortDemo.setVisible(true);
                        matchUpPanel.setVisible(true);
                        customOrderPanel.setVisible(false);
                        itemOrderCheckBox.setSelected(testSetup.getTest().getSettings("ItemOrder").equals("Random"));
                        fixedOrderPanel.setVisible(true);
                        rootPanel.setPreferredSize(SIZE_LARGE);

                        break;
                    case "Custom":
                        setMatchUpList(testSetup.getTest().getSettings("CustomMatchUps"));
                        descriptionTextPane.setText(SORT_CUSTOM_DESCRIPTION);
                        sortDemo.setVisible(false);
                        matchUpPanel.setVisible(true);
                        customOrderPanel.setVisible(true);
                        itemOrderCheckBox.setSelected(false);
                        fixedOrderPanel.setVisible(false);
                        rootPanel.setPreferredSize(SIZE_LARGE);
                        break;
                }
            } else {
                descriptionTextPane.setVisible(false);
                matchUpPanel.setVisible(false);
                customOrderPanel.setVisible(false);
                fixedOrderPanel.setVisible(false);
                rootPanel.setPreferredSize(SIZE_SMALL);
                submitButton.setEnabled(false);
            }
            frame.validate();
            frame.repaint();
            frame.pack();
        };
        ActionListener updateCustomOrder = e -> {
            if(!matchUpList.isSelectionEmpty()) {
                MatchUp selectedMatchUp = matchUpList.getSelectedValue();
                int index = matchUpList.getSelectedIndex();
                switch(e.getActionCommand()) {
                    case "Up":
                        if(index > 0) {
                            selectedMatchUp.setQuestionNumber(selectedMatchUp.getQuestionNumber() - 1);
                            matchUpListModel.get(index - 1).setQuestionNumber(matchUpListModel.get(index - 1).getQuestionNumber() - 1);
                            matchUpListModel.remove(index);
                            matchUpListModel.add(index - 1, selectedMatchUp);
                            index -= 1;
                        }
                        break;
                    case "Down":
                        if(index < matchUpListModel.size() - 1) {
                            selectedMatchUp.setQuestionNumber(selectedMatchUp.getQuestionNumber() + 1);
                            matchUpListModel.get(index + 1).setQuestionNumber(matchUpListModel.get(index + 1).getQuestionNumber() + 1);
                            matchUpListModel.remove(index);
                            matchUpListModel.add(index + 1, selectedMatchUp);
                            index += 1;
                        }
                        break;
                    case "Swap":
                        Item itemX = selectedMatchUp.getItemB();
                        Item itemY = selectedMatchUp.getItemA();
                        selectedMatchUp.setItemA(itemX);
                        selectedMatchUp.setItemB(itemY);
                        break;
                }
                matchUpList.setModel(matchUpListModel);
                matchUpList.setSelectedIndex(index);
            }
        };
        ActionListener back = e -> {
            createSetupGUI();
            frame.dispose();
        };
        ActionListener submit = e -> {
            StringBuilder customSort = new StringBuilder();
            if(sortMethodComboBox.getSelectedItem().toString().equals("Custom")) {
                for(int i = 0; i < matchUpList.getModel().getSize(); i++) {
                    customSort.append(",").append(matchUpList.getModel().getElementAt(i).getItemA().getName()).append("|").append(matchUpList.getModel().getElementAt(i).getItemB().getName());
                }
            } else {
                customSort = new StringBuilder(" none");
            }
            customSort = new StringBuilder(customSort.substring(1));
            testSetup.getTest().setSettings(questionTextField.getText() + "\\" +
                    tieTextField.getText() + "\\" +
                    sortMethodComboBox.getSelectedItem().toString() + "\\" +
                    (itemOrderCheckBox.isSelected() ? "Random" : "Fixed") +"\\" + customSort);
            System.out.println(testSetup.getTest().getSettings("all"));
            testSetup.completeSetup();
            String[] options = "Edit Another Test,Continue Editing,Exit".split(",");
            businessLabel.setIcon(null);
            switch(JOptionPane.showOptionDialog(null, COMPLETE_CONFIRM_TEXT, COMPLETE_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, options,options[0])) {
                case 0:
                    frame.dispose();
                    createSetupGUI();
                    break;
                case 2:
                    frame.dispose();
                    break;
                default:
                    break;
            }

        };

        questionTextField.addFocusListener(validateField);
        tieTextField.addFocusListener(validateField);
        sortMethodComboBox.addActionListener(selectSort);
        moveUpButton.addActionListener(updateCustomOrder);
        moveDownButton.addActionListener(updateCustomOrder);
        swapButton.addActionListener(updateCustomOrder);
        cancelChangesButton.addActionListener(back);
        submitButton.addActionListener(submit);

        // Populate sortMethodComboBox
        sortMethodComboBox.addItem("Select a sort method...");
        sortMethodComboBox.addItem("Random");
        sortMethodComboBox.addItem("Random (Unbiased)");
        sortMethodComboBox.addItem("Rotational");
        sortMethodComboBox.addItem("Sequential");
        sortMethodComboBox.addItem("Custom");

        //Auto populates test information
        questionTextField.setText(testSetup.getTest().getSettings("QuestionText"));
        tieTextField.setText(testSetup.getTest().getSettings("TieText"));
        itemOrderCheckBox.setSelected(testSetup.getTest().getSettings("ItemOrder").equals("Random"));
    }

    /*
    Returns the root panel
    @returns the root panel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void setMatchUpList(String sortMethod) {
        ArrayList<Item> items = testSetup.getItems();
        ArrayList<MatchUp> matchUps = new ArrayList<>();
        int questionNumber = 0;
        switch (sortMethod) {
            case "Sequential":
                for (int i = 0; i < items.size(); i++) {
                    Item itemA = items.get(i);
                    for (int ii = i; ii < items.size(); ii++) {
                        Item itemB = items.get(ii);
                        if (itemA != itemB) {
                            questionNumber++;
                            matchUps.add(new MatchUp(questionNumber, itemA, itemB, null));
                        }
                    }
                }
                break;
            case "Rotational":
                for (int i = 1; i < items.size(); i++) {
                    for (int ii = 0; ii < items.size() - i; ii++) {
                        Item itemA = items.get(ii);
                        Item itemB = items.get(ii + i);
                        questionNumber++;
                        matchUps.add(new MatchUp(questionNumber, itemA, itemB, null));
                    }
                }
                break;
            case "none":
                setMatchUpList("Sequential");
                return;
            default:
                String customMatchUps = testSetup.getTest().getSettings("CustomMatchUps");
                String[] itemPairs = customMatchUps.split(",");
                String[] pairedItem;
                Item itemA = null;
                Item itemB = null;
                for(int i = 0; i < itemPairs.length; i++) {
                    pairedItem = itemPairs[i].split("\\|");
                    for(Item item : items) {
                        if(item.getName().equals(pairedItem[0])){
                            itemA = item;
                        } else if(item.getName().equals(pairedItem[1])) {
                            itemB = item;
                        }
                    }
                    matchUps.add(new MatchUp(i + 1, itemA, itemB, null));
                }
                break;
        }
        matchUpListModel = new DefaultListModel<>();
        for (MatchUp matchUp : matchUps) {
            matchUpListModel.addElement(matchUp);
        }
        matchUpList.setModel(matchUpListModel);
    }

    private void validate(String field) {
        switch(field) {
            case "Question Text":
                questionTextIcon.setVisible(questionTextField.getText().isEmpty());
                break;
            case "Tie Button Text":
                tieButtonTextIcon.setVisible(tieTextField.getText().isEmpty());
                break;
        }
    }
}
