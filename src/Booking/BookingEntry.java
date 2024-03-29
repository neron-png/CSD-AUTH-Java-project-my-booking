package Booking;

import Lodges.Lodge;
import Misc.HintedJTextField;
import Misc.Storage;
import Misc.Utils;
import Users.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import static Misc.UniqueIDGenerator.getUniqueId;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This is a Booking Entry class that builds a reservation of a customer to a specific lodge.
 * @author Christos Balaktsis
 */

public class BookingEntry implements Serializable {
    @Serial
    private static final long serialVersionUID = 1768249623478924556L;

    private final String bookingId;
    private double totalCost;
    private final Lodge lodge;
    private final User tenant;
    private final HashSet<LocalDate> period;
    private boolean valid;
    private final LocalDate entryDate;
    private LocalDate arrivalDate;
    private LocalDate departureDate;

    /**
     * Default constructor of class. Sets the user (type Customer) that asks for booking a specific lodge.
     * @param tenant The customer that applies for reservation.
     * @param lodge The requested lodge for reservation.
     */
    public BookingEntry(User tenant, Lodge lodge) {
        this.tenant = tenant;
        this.lodge = lodge;
        this.totalCost = 0;
        this.period = new HashSet<>();
        this.valid = false;
        this.entryDate = LocalDate.now();
        this.bookingId = getUniqueId();
    }

    /**
     * Method that creates a period of dates (from date1 to date2) and adds each date in a HashSet.
     * @param date1 The first date of the period.
     * @param date2 The last date of the period (not included in the HashSet).
     * @return A HashSet of Dates, the period between date1 and date2.
     */
    private HashSet<LocalDate> getPeriod(LocalDate date1, LocalDate date2) {
        while(date1.isBefore(date2)) {
            this.period.add(date1);
            date1 = date1.plusDays(1);
        }
        return this.period;
    }

    /**
     * Method for requesting booking a Lodge.
     * @param arrival The date of requested arrival in the lodge.
     * @param departure The date of requested departure from the lodge.
     * @return If the lodge is available on that period and the requested booking got approved.
     */
    public boolean bookLodge(LocalDate arrival, LocalDate departure) {
        this.arrivalDate = arrival;
        this.departureDate = departure;
        if(arrival.isAfter(departure) || arrival.isEqual(departure)) return false;
        if(lodge.bookLodge(getPeriod(arrival,departure))) {
            Storage.getBookings().add(this);
            this.totalCost = this.lodge.getDetails().getPrice() * this.period.size();
            this.valid = true;
            return true;
        }
        return false;
    }

    /**
     * @return The check-in date of the accommodation (arrival date).
     */
    public LocalDate getArrivalDate() {
        return this.arrivalDate;
    }

    /**
     * @return The check-out date of the accommodation (departure date).
     */
    public LocalDate getDepartureDate() {
        return this.departureDate;
    }

    /**
     * @return The date the booking entry was created.
     */
    public LocalDate getEntryDate() {
        return this.entryDate;
    }

    /**
     * @return If the booking entry is valid. Else it got canceled.
     */
    public boolean isValid() {
        if (LocalDate.now().isAfter(arrivalDate)){
            this.valid = false;
        }
        return this.valid;
    }

    /**
     * @return The total cost for accommodation at the booked lodge.
     */
    public double getTotalCost() {
        return this.valid ? this.totalCost : 0;
    }

    /**
     * @return The user (type Customer) requested to create a booking.
     */
    public User getTenant() {
        return this.tenant;
    }

    /**
     * @return The unique ID of the booking
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * @return The booked lodge.
     */
    public Lodge getLodge() {
        return this.lodge;
    }

    /**
     * Method that cancels the booking entry, updating the availability dates of the current lodge.
     * @return If the cancellation is completed. Else the booking was invalid.
     */
    public boolean cancelBooking() {
        if(this.valid) {
            this.lodge.setAvailableDates(this.period);
            this.valid = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof BookingEntry bookingEntry))
            return false;
        return this.lodge == bookingEntry.lodge && this.tenant.equals(bookingEntry.tenant) && this.period.equals(bookingEntry.period);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + (this.lodge != null ? this.lodge.hashCode() : 0);
        hash = 31 * hash + (this.tenant != null ? this.tenant.hashCode() : 0);
        hash = 31 * hash + (this.period != null ? this.period.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String str = "Booking Entry #" +
                this.bookingId +
                "\t\t" +
                "Booked lodge: " +
                this.lodge.getDetails().getTitle() +
                "\nTenant: " +
                this.tenant.getFullName() +
                "\nPeriod of reservation: " +
                this.arrivalDate.toString() +
                " to " +
                this.departureDate.toString() +
                "\nTotal Cost for accommodation: " +
                this.totalCost +
                "\n";
        return valid ? str : "Invalid Booking Entry\n";
    }

    static public List<BookingEntry> getBookingsOnDate(String date){
        List<BookingEntry> results = new ArrayList<>();

        if (date == null){
            results.addAll(Storage.getBookings());
            return results;
        }

        for (BookingEntry booking: Storage.getBookings()){
            if (booking.getEntryDate().toString().equals(date)){
                results.add(booking);
            }
        }

        return results;
    }

    public JPanel toJPanel(){
        JPanel bookingPanel = new JPanel();
        bookingPanel.setBackground(Color.white);
        bookingPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));

        ImageIcon scaledImage = new ImageIcon(this.lodge.getDetails().getImage().getImage().getScaledInstance(50, 50, Image.SCALE_AREA_AVERAGING));
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.white);
        imagePanel.add(new JLabel(scaledImage));

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(3, 2));
        detailPanel.setBackground(Color.white);
        detailPanel.add(new JLabel(lodge.getDetails().getTitle()),0,0);
        detailPanel.add(new JLabel("# " + getBookingId(), SwingConstants.TRAILING));
        detailPanel.add(new JLabel("From: " + arrivalDate.toString() + " to: " + departureDate.toString()));
        detailPanel.add(new JLabel("Tenant: " + getTenant().getUsername(), SwingConstants.TRAILING));
        detailPanel.add(new JLabel("Price: " + getTotalCost() + "€"));
        if (valid){
            detailPanel.add(new JLabel("State: Valid", SwingConstants.TRAILING));
        } else {
            detailPanel.add(new JLabel("State: Invalid", SwingConstants.TRAILING));
        }


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, detailPanel);
        splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        splitPane.setDividerSize(0);
        bookingPanel.add(splitPane);

        bookingPanel.setMaximumSize(bookingPanel.getPreferredSize());

        return bookingPanel;
    }

    public static void bookingDialog(Lodge lodge, User user) {
        JFrame frame = new JFrame("Book " + lodge.getDetails().getTitle());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        JPanel mainPanel = new JPanel();
        frame.add(mainPanel);

        mainPanel.setLayout(new GridLayout(3, 1));
        mainPanel.add(new JLabel("Book " + lodge.getDetails().getTitle(), JLabel.CENTER));

        JPanel datePanel = new JPanel();

        datePanel.add(new JLabel("From:"));
        HintedJTextField from_day = new HintedJTextField("DD");
        HintedJTextField from_month = new HintedJTextField("MM");
        HintedJTextField from_year = new HintedJTextField("YYYY");
        datePanel.add(from_day);
        datePanel.add(from_month);
        datePanel.add(from_year);

        datePanel.add(new JLabel("until:"));
        HintedJTextField to_day = new HintedJTextField("DD");
        HintedJTextField to_month = new HintedJTextField("MM");
        HintedJTextField to_year = new HintedJTextField("YYYY");
        datePanel.add(to_day);
        datePanel.add(to_month);
        datePanel.add(to_year);

        mainPanel.add(datePanel);

        JPanel buttonPanel = new JPanel();
        JButton book = new JButton("Book");
        buttonPanel.add(book);

        JButton cancel = new JButton("Cancel");
        buttonPanel.add(cancel);

        mainPanel.add(buttonPanel);

        frame.pack();
        frame.setVisible(true);

        cancel.addActionListener(e -> frame.dispose());

        book.addActionListener(e -> {

            Utils.dateSanitize(from_day, from_month, from_year);
            Utils.dateSanitize(to_day, to_month, to_year);

            ArrayList<LocalDate> reserveDates = new ArrayList<>();
            //Validated the datetime format
            try {
                reserveDates.add(LocalDate.parse(from_year.getText() + "-" + from_month.getText() + "-" + from_day.getText()));
                reserveDates.add(LocalDate.parse(to_year.getText() + "-" + to_month.getText() + "-" + to_day.getText()));
            } catch (java.time.format.DateTimeParseException error) {
                JOptionPane.showMessageDialog(null, "Incorrect date format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (reserveDates.get(0).isAfter(reserveDates.get(1)) || reserveDates.get(0).isBefore(LocalDate.now())){
                JOptionPane.showMessageDialog(null, "The date range you've entered isn't physically possible!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BookingEntry bookingEntry = new BookingEntry(user, lodge);
            if (bookingEntry.bookLodge(reserveDates.get(0), reserveDates.get(1))){
                JOptionPane.showMessageDialog(null, "Booked " + lodge.getDetails().getTitle() + "\nFor dates: " + reserveDates.get(0).toString()
                + " to: " + reserveDates.get(1).toString() + "\nat: " + bookingEntry.getTotalCost() + "€", "Booked successfully!", JOptionPane.PLAIN_MESSAGE);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Unfortunately this lodge has already been booked for that time period!");
            }

        });

    }

}
