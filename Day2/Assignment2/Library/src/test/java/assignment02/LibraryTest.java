package assignment02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library<String> lib;
    private Library<PhoneNumber> phoneLibrary;

    @BeforeEach
    public void setUp() {
        lib = new Library<>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");
        lib.add(9780143127550L, "Malcolm Gladwell", "David and Goliath");
        lib.add(9780140449136L, "Jon Krakauer", "Into Thin Air"); // Same author, different title


    }

    @Test
    public void testGetInventoryList() {
        ArrayList<LibraryBook<String>> sortedByIsbn = lib.getInventoryList();
        assertEquals(5, sortedByIsbn.size());
        assertEquals(9780140449136L, sortedByIsbn.get(0).getIsbn());
        assertEquals(9780143127550L, sortedByIsbn.get(1).getIsbn()); // Smallest ISBN
        assertEquals(9780330351690L, sortedByIsbn.get(2).getIsbn());
        assertEquals(9780374292799L, sortedByIsbn.get(3).getIsbn());
        assertEquals(9780446580342L, sortedByIsbn.get(4).getIsbn()); // Largest ISBN
    }

    @Test
    public void testGetOrderedByAuthor() {
        ArrayList<LibraryBook<String>> sortedByAuthor = lib.getOrderedByAuthor();

        assertEquals(5, sortedByAuthor.size());
        assertEquals("David Baldacci", sortedByAuthor.get(0).getAuthor());
        assertEquals("Jon Krakauer", sortedByAuthor.get(1).getAuthor());
        assertEquals("Jon Krakauer", sortedByAuthor.get(2).getAuthor()); // Second book by Krakauer
        assertEquals("Malcolm Gladwell", sortedByAuthor.get(3).getAuthor());
        assertEquals("Thomas L. Friedman", sortedByAuthor.get(4).getAuthor());

        // Additional assertions to verify title sorting for the same author
        assertEquals("Into Thin Air", sortedByAuthor.get(1).getTitle()); // Comes before "Into the Wild"
        assertEquals("Into the Wild", sortedByAuthor.get(2).getTitle());
    }

    @Test
    public void testGetOverdueList() {
        // Check out some books with due dates
        lib.checkout(9780374292799L, "Alice", 1, 1, 2022); // Overdue
        lib.checkout(9780330351690L, "Bob", 5, 1, 2023); // Not overdue
        lib.checkout(9780446580342L, "Charlie", 1, 1, 2021); // Overdue


        // Test getOverdueList for books due before January 1, 2022
        ArrayList<LibraryBook<String>> overdueBooks = lib.getOverdueList(5, 1, 2022);
        // Verify the number of overdue books
        assertEquals(2, overdueBooks.size(), "There should be 2 overdue books.");
        // Verify the order by due date (oldest first)
        assertEquals(9780446580342L, overdueBooks.get(0).getIsbn(), "First overdue book should be the oldest.");
        assertEquals(9780374292799L, overdueBooks.get(1).getIsbn(), "Second overdue book should be the next oldest.");
    }

    @Test
    public void testSmallLibraryCheckout() {
        boolean checkoutResult = lib.checkout(9780330351690L, "Jane Doe", 1, 1, 2025);
        assertTrue(checkoutResult, "Checkout should succeed for available book.");

        ArrayList<LibraryBook<String>> booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(1, booksCheckedOut.size());

        // Verify ISBN, author, and title directly
        LibraryBook<String> checkedOutBook = booksCheckedOut.get(0);
        assertEquals(9780330351690L, checkedOutBook.getIsbn());
        assertEquals("Jon Krakauer", checkedOutBook.getAuthor());
        assertEquals("Into the Wild", checkedOutBook.getTitle());
    }


    @Test
    public void testEmpty() {
        assertNull(lib.lookup(978037429279L));

        ArrayList<LibraryBook<String>> booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(booksCheckedOut.size(), 0);

        assertFalse(lib.checkout(978037429279L, "Jane Doe", 1, 1, 2008));
        assertFalse(lib.checkin(978037429279L));
        assertFalse(lib.checkin("Jane Doe"));
    }

    @Test
    public void testNonEmpty() {
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        assertNull(lib.lookup(9780330351690L));

        assertTrue(lib.checkout(9780330351690L, "Jane Doe", 1, 1, 2008));
        ArrayList<LibraryBook<String>> booksCheckedOut = lib.lookup("Jane Doe");
        assertEquals(1, booksCheckedOut.size());

        LibraryBook<String> checkedOutBook = booksCheckedOut.get(0);
        assertEquals(9780330351690L, checkedOutBook.getIsbn());
        assertEquals("Jane Doe", checkedOutBook.getHolder());
        assertEquals(new GregorianCalendar(2008, 1, 1), checkedOutBook.getDueDate());

        assertTrue(lib.checkin(9780330351690L));
        assertNull(lib.lookup(9780330351690L));
        assertEquals(0, lib.lookup("Jane Doe").size());
    }

    @Test
    public void testCheckoutAlreadyCheckedOutBook() {
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        assertTrue(lib.checkout(9780330351690L, "Jane Doe", 1, 1, 2008));

        assertFalse(lib.checkout(9780330351690L, "John Smith", 2, 15, 2008));
        assertEquals("Jane Doe", lib.lookup(9780330351690L));
    }

    @Test
    public void testCheckinWithIncorrectISBN() {
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        assertFalse(lib.checkin(1234567890123L)); // Invalid ISBN
    }

    @Test
    public void testMultipleCheckoutsAndCheckins() {
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        assertTrue(lib.checkout(9780374292799L, "Alice", 2, 15, 2023));
        assertTrue(lib.checkout(9780446580342L, "Bob", 3, 10, 2023));

        assertEquals(1, lib.lookup("Alice").size());
        assertEquals(9780374292799L, lib.lookup("Alice").get(0).getIsbn());

        assertEquals(1, lib.lookup("Bob").size());
        assertEquals(9780446580342L, lib.lookup("Bob").get(0).getIsbn());

        assertTrue(lib.checkin("Alice"));
        assertTrue(lib.checkin("Bob"));
        assertEquals(0, lib.lookup("Alice").size());
        assertEquals(0, lib.lookup("Bob").size());
    }

    @Test
    public void testLargeLibrary() {
        Library<String> lib = new Library<>();
        lib.addAll("/Users/ajithalphonse/MSD/CS6012/Day2/Mushroom_Publishing.txt");

        // Check that the library loaded books
        assertFalse(lib.getInventoryList().isEmpty(), "Library should contain books after loading from file.");

        // Attempt to check out a book that is presumably not in the library
        boolean checkoutResult = lib.checkout(9780143127550L, "Jane Doe", 1, 1, 2025);
        assertFalse(checkoutResult, "Checkout should fail if the ISBN is not in the library.");

        // Attempt to check in a patron with no checked-out books
        boolean checkinResult = lib.checkin("Jane Doe");
        assertFalse(checkinResult, "Check-in should fail if 'Jane Doe' has no books checked out.");
    }

    @Test
    public void stringLibraryTest() {
        Library<String> lib = new Library<>();
        lib.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        lib.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        lib.add(9780446580342L, "David Baldacci", "Simple Genius");

        String patron1 = "Jane Doe";

        assertTrue(lib.checkout(9780330351690L, patron1, 1, 1, 2008));
        assertTrue(lib.checkout(9780374292799L, patron1, 1, 1, 2008));

        ArrayList<LibraryBook<String>> booksCheckedOut1 = lib.lookup(patron1);
        assertEquals(booksCheckedOut1.size(), 2);
        assertEquals(booksCheckedOut1.get(0).getHolder(), patron1);
        assertEquals(booksCheckedOut1.get(0).getDueDate(), new GregorianCalendar(2008, 1, 1));
        assertEquals(booksCheckedOut1.get(1).getHolder(), patron1);
        assertEquals(booksCheckedOut1.get(1).getDueDate(), new GregorianCalendar(2008, 1, 1));

        assertTrue(lib.checkin(patron1));
    }

    @Test
    public void phoneNumberTest() {
        phoneLibrary = new Library<>();
        phoneLibrary.add(9780374292799L, "Thomas L. Friedman", "The World is Flat");
        phoneLibrary.add(9780330351690L, "Jon Krakauer", "Into the Wild");
        phoneLibrary.add(9780446580342L, "David Baldacci", "Simple Genius");

        PhoneNumber patronPhone = new PhoneNumber("801.555.1234");

        // Attempt to check out a book with the patron's phone number
        boolean checkoutResult = phoneLibrary.checkout(9780330351690L, patronPhone, 1, 1, 2025);
        assertTrue(checkoutResult, "Checkout should succeed for an available book with a phone number as the holder.");

        // Verify that the book is checked out to the correct phone number
        ArrayList<LibraryBook<PhoneNumber>> booksCheckedOut = phoneLibrary.lookup(patronPhone);
        assertEquals(1, booksCheckedOut.size());
        assertEquals(9780330351690L, booksCheckedOut.get(0).getIsbn());

        // Attempt to check in the book
        boolean checkinResult = phoneLibrary.checkin(patronPhone);
        assertTrue(checkinResult, "Checkin should succeed when the patron with the phone number has checked-out books.");
    }
}