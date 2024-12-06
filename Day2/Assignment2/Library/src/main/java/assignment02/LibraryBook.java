package assignment02;


import java.util.GregorianCalendar;

/**
 * Representation of a library book
 * @param <HolderType> - The data type of the values uniquely identifying the holder of the book
 */
public class LibraryBook<HolderType> extends Book {
    HolderType holder = null;
    GregorianCalendar dueDate = null;

    /**
     * Create a library book from required book attributes
     * @param isbn  unique numeric identifier of the book
     * @param author full name of the author (first and last)
     * @param title title of the book
     */
    public LibraryBook(long isbn, String author, String title) {
        super(isbn, author, title);
    }

    /**
     * Set the due date of the book
     * @param year due year
     * @param month due month
     * @param day due day
     */
    private void setDueDate(int year, int month, int day){
        dueDate = new GregorianCalendar(year, month, day);
    }

    public GregorianCalendar getDueDate() {
        return dueDate;
    }

    private void setHolder(HolderType holder){
        this.holder = holder;
    }

    public HolderType getHolder(){
        return holder;
    }

    /**
     * Checkout the book by adding a holder and due date to the book
     * @param holder Identifier of the person checking out the book
     * @param year due date year
     * @param month due date month
     * @param day due date day
     */
    public void checkOut(HolderType holder, int year, int month, int day){
        setDueDate(year, month, day);
        setHolder(holder);
    }

    /**
     * Check in the book (i.e. set the holder and due date to null)
     */
    public void checkIn(){
        holder = null;
        dueDate = null;
    }

    /**
     * See if the book is already resered
     * @return True if there is a holder associated with this book, otherwise false.
     */
    public boolean isCheckedOut(){
        return holder != null;
    }

}
