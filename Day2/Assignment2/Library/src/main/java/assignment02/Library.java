package assignment02;


import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.Comparator;

/**
 * Class representation of a library (a collection of library books).
 *
 */
public class Library<Type> {

    private ArrayList<LibraryBook<Type>> library;

    public Library() {
        library = new ArrayList<>();
    }

    /**
     * Add the specified book to the library, assume no duplicates.

     */
    public void add(long isbn, String author, String title) {
        library.add((LibraryBook<Type>) new LibraryBook(isbn, author, title));
    }

    /**
     * Add the list of library books to the library, assume no duplicates.
     *
     * @param list
     *          -- list of library books to be added
     */
    public void addAll(ArrayList<LibraryBook<Type>> list) {
        library.addAll(list);
    }

    /**
     * Add books specified by the input file. One book per line with ISBN, author,
     * and title separated by tabs.

     */
    public void addAll(String filename) {
        ArrayList<LibraryBook<Type>> booksToAdd = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(filename))) {
            int lineNumber = 1;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                try (Scanner lineScanner = new Scanner(line)) {
                    lineScanner.useDelimiter("\\t");

                    if (!lineScanner.hasNextLong()) {
                        throw new ParseException("ISBN missing or invalid", lineNumber);
                    }
                    long isbn = lineScanner.nextLong();

                    if (!lineScanner.hasNext()) {
                        throw new ParseException("Author missing", lineNumber);
                    }
                    String author = lineScanner.next();

                    if (!lineScanner.hasNext()) {
                        throw new ParseException("Title missing", lineNumber);
                    }
                    String title = lineScanner.next();

                    booksToAdd.add(new LibraryBook<>(isbn, author, title));
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage() + ". No books added to the library.");
        } catch (ParseException e) {
            System.err.println("Parsing error: " + e.getMessage() + " at line " + e.getErrorOffset() + ". No books added.");
        }

        library.addAll(booksToAdd);
    }


    /**
     * Retrieves the holder of the library book with the given ISBN.

     */

    public Type lookup(long isbn) {
        for (Object item : library) {
            LibraryBook<Type> book = (LibraryBook<Type>) item;
            if (book.getIsbn() == isbn) {
                return book.getHolder(); // can return null when book is not checked out
            }
        }
        return null;
    }

    /**
     * Returns the list of library books checked out to the specified holder.
     */
    public ArrayList<LibraryBook<Type>> lookup(Type holder) {
        ArrayList<LibraryBook<Type>> booksCheckedOut = new ArrayList<>();
        for (LibraryBook<Type> book : library) {
            if (holder.equals(book.getHolder())) {
                booksCheckedOut.add(book);
            }
        }
        return booksCheckedOut;
    }


    /**
     * Sets the holder and due date of the library book with the specified ISBN.
     */
    public boolean checkout(long isbn, Type holder, int month, int day, int year) {
        for (Object item : library) {
            LibraryBook<Type> book = (LibraryBook<Type>) item;
            if (book.getIsbn() == isbn) {
                if (book.isCheckedOut()) {
                    return false;
                } else {
                    book.checkOut(holder, year, month, day);
                    return true;
                }
            }
        }
        return false; // isbn not found
    }

    /**
     * Unsets the holder and due date of the library book.

     */
    public boolean checkin(long isbn) {
        for (Object item : library) {
            LibraryBook<Type> book = (LibraryBook<Type>) item;
            if (book.getIsbn() == isbn) {
                if (book.isCheckedOut()) {
                    book.checkIn();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * Unsets the holder and due date for all library books checked out by the specified holder.
     */
    public boolean checkin(Type holder) {
        boolean found = false;
        for (LibraryBook<Type> book : library) {
            if (holder.equals(book.getHolder())) {
                book.checkIn();
                found = true;
            }
        }
        return found;
    }


    public ArrayList<LibraryBook<Type>> getAllbooks(){
        return library;
    }

    /**
     * Get a copy of the existing library member variable
     * @return copy of library
     */
    private ArrayList<LibraryBook<Type>> getCopy(){
        ArrayList<LibraryBook<Type>> libraryCopy = new ArrayList<>();
        libraryCopy.addAll(library);
        return libraryCopy;
    }

    /**
     * Returns the list of library books, sorted by ISBN (smallest ISBN
     first).
     */
    public ArrayList<LibraryBook<Type>> getInventoryList() {
        ArrayList<LibraryBook<Type>> libraryCopy = getCopy();
        sort(libraryCopy, new OrderByIsbn());
        return libraryCopy;
    }

    /**
     * Returns the list of library books, sorted by author
     */
    public ArrayList<LibraryBook<Type>> getOrderedByAuthor() {
        ArrayList<LibraryBook<Type>> libraryCopy = getCopy();
        sort(libraryCopy, new OrderByAuthor());
        return libraryCopy;
    }

    /**
     * Returns the list of library books whose due date is older than the
     input
     * date. The list is sorted by date (oldest first).
     *
     * If no library books are overdue, returns an empty list.
     */
    public ArrayList<LibraryBook<Type>> getOverdueList(int month, int day, int year) {
        GregorianCalendar deadline = new GregorianCalendar(year, month, day);
        ArrayList<LibraryBook<Type>> pastDue = new ArrayList<>();
        for (LibraryBook<Type> book : library) {
            if (book.isCheckedOut() && book.getDueDate().compareTo(deadline) < 0) {
                pastDue.add(book);
            }
        }
        sort(pastDue, new OrderByDueDate());
        return pastDue;
    }

    /**
     * Returns the list of library books whose due date is older than the input
     * date. The list is sorted by date (oldest first).
     *
     * If no library books are overdue, returns an empty list.
     */
    private static <ListType> void sort(ArrayList<ListType> list, Comparator<ListType> c) {
        for (int i = 0; i < list.size() - 1; i++) {
            int j, minIndex;
            for (j = i + 1, minIndex = i; j < list.size(); j++)
                if (c.compare(list.get(j), list.get(minIndex)) < 0)
                    minIndex = j;
            ListType temp = list.get(i);
            list.set(i, list.get(minIndex));
            list.set(minIndex, temp);
        }
    }

    /**
     * Comparator that orders library books by ISBN in ascending order.
     *
     * Returns a negative value if lhs has a smaller ISBN than rhs,
     * a positive value if lhs has a larger ISBN, and 0 if they are equal.
     */

    protected class OrderByIsbn implements Comparator<LibraryBook<Type>> {

        public int compare(LibraryBook<Type> lhs, LibraryBook<Type> rhs) {
            return (int) (lhs.getIsbn() - rhs.getIsbn());
        }
    }

    /**
     * Comparator that orders library books by author name,
     * using the book title as a tie-breaker when authors are identical.
     */
    protected class OrderByAuthor implements Comparator<LibraryBook<Type>> {
        @Override
        public int compare(LibraryBook<Type> lhs, LibraryBook<Type> rhs) {
            int authorComparison = lhs.getAuthor().compareTo(rhs.getAuthor());
            if (authorComparison != 0) {
                return authorComparison;
            }
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }

    /**
     * Comparator that defines an ordering among library books using the
     due date.
     */
    protected class OrderByDueDate implements Comparator<LibraryBook<Type>> {
        @Override
        public int compare(LibraryBook<Type> lhs, LibraryBook<Type> rhs) {
            // If both books have due dates, compare them
            if (lhs.getDueDate() != null && rhs.getDueDate() != null) {
                return lhs.getDueDate().compareTo(rhs.getDueDate());
            }
            // If lhs has no due date (not checked out), it should appear later in the list
            if (lhs.getDueDate() == null) {
                return 1;
            }
            // If rhs has no due date (not checked out), it should appear later in the list
            if (rhs.getDueDate() == null) {
                return -1;
            }
            return 0;
        }
    }

}