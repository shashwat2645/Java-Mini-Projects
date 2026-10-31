package revision.projects;

import java.util.*;

interface Searchable{
    public void searchByTitle(String title);
    public void searchByAuthor(String author);
}

class Book{
    private int bookId;
    private String title;
    private String author;
    private String category;
    private boolean available;

    public Book(){
        this.bookId = 0;
        this.title = "";
        this.author = "";
        this.category = "";
        this.available = true;
    }

    public Book(int id, String title, String author, String category){
        this.bookId = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = true;
    }

    public int getBookId(){
        return this.bookId;
    }

    public String getBookTitle(){
        return this.title;
    }

    public String getBookAuthor(){
        return this.author;
    }

    public boolean getAvailability(){
        return this.available;
    }

    public void displayBookInfo(){
        System.out.println(
            bookId + " ".repeat(5) + title + " ".repeat(5) + author + " ".repeat(5) + category + " ".repeat(5) + available + "\n"
        );
    }

    public void issueBook(){
        available = false;
    }

    public void returnBook(){
        available = true;
    }
}

abstract class Member{
    private int memberId;
    private String name;
    private ArrayList<Book> borrowedBooks = new ArrayList<>();

    public int getMemberId(){
        return memberId;
    }

    public String getName(){
        return name;
    }

    public void setMemberId(int id){
        this.memberId = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public ArrayList<Book> getBorrowedBooks(){
        return borrowedBooks;
    }

    public void displayDetails(){
        System.out.println(
            memberId + " ".repeat(5) + name + "\n"
        );
    }

    public void borrowBook(Book book){
        borrowedBooks.add(book);
    }

    public void returnBook(Book book){
        borrowedBooks.remove(book);
    }

    public abstract int getBorrowLimit();
}

class Student extends Member{
    private String course;
    private int borrowLimit = 3;

    public Student(int id, String name, String course){
        this.setMemberId(id);
        this.setName(name);
        this.course = course;
    }

    @Override 
    public int getBorrowLimit(){
        return borrowLimit;
    }

    @Override 
    public void displayDetails(){
        System.out.println(
            super.getMemberId() + " ".repeat(5) + super.getName() + " ".repeat(5) + this.course + "\n"
        );
    }
}

class Faculty extends Member{
    private String department;
    private int borrowLimit = 5;

    public Faculty(int id, String name, String department){
        this.setMemberId(id);
        this.setName(name);
        this.department = department;
    }

    @Override 
    public int getBorrowLimit(){
        return borrowLimit;
    }

    @Override 
    public void displayDetails(){
        System.out.println(
            super.getMemberId() + " ".repeat(5) + super.getName() + " ".repeat(5) + this.department + "\n"
        );
    }
}

class Library implements Searchable{
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();

    private Member findMember(int memberId){
        for(Member member : members){
            if(member.getMemberId() == memberId){
                return member;
            }
        }
        return null;
    }

    private Book findBook(int bookId){
        for(Book book : books){
            if(book.getBookId() == bookId){
                return book;
            }
        }
        return null;
    }

    public void addBook(int id, String title, String author, String category){

        if(findBook(id) != null){
            System.out.println("Book ID already exists.");
            return;
        }
        //add to list
        books.add(new Book(id, title, author, category));
        System.out.println("Book " + title + " added to list successfully!!!");
    }

    public void removeBook(int bookId){
        Book book = findBook(bookId);

        if(book == null){
            System.out.println("Book not found.");
            return;
        }

        if(!book.getAvailability()){
            System.out.println("Book is currently issued.");
            return;
        }

        books.remove(book);
        System.out.println("Book removed successfully.");
    }

    public void registerMember(Member member){
        if(findMember(member.getMemberId()) != null){
            System.out.println("Member ID already exists.");
            return;
        }
        //add member to list
        members.add(member);
        System.out.println("Member registered successfully!!!");
    }

    public void issueBook(int bookId, int memberId){
        Member member = findMember(memberId);
        Book book = findBook(bookId);

        if(member == null){
            System.out.println("Member not found.");
            return;
        }

        if(book == null){
            System.out.println("Book not found.");
            return;
        }

        if(member.getBorrowedBooks().size() >= member.getBorrowLimit()){
            System.out.println("Borrow limit reached.");
            return;
        }
        if(book.getAvailability()){
            member.borrowBook(book);
            book.issueBook();
            System.out.println("Book issued successfully.");
        }
        else{
            System.out.println("Book is already issued.");
        }
        return;

    }

    public void returnBook(int bookId, int memberId){
        Member member = findMember(memberId);
        Book book = findBook(bookId);

        if(member == null){
            System.out.println("Member not found.");
            return;
        }

        if(book == null){
            System.out.println("Book not found.");
            return;
        }

        if(!member.getBorrowedBooks().contains(book)){
            System.out.println("Book is not issued.");
            return;
        }
        member.returnBook(book);
        book.returnBook();
        System.out.println("Book returned successfully.");

    }

    public void displayBooks(){

        if(books.isEmpty()){
            System.out.println("No books available.");
            return;
        }

        System.out.println("ID\tTitle\tAuthor\tCategory\tAvailable");

        for(Book book : books){
            book.displayBookInfo();
        }
    }

    public void displayMembers(){
        if(members.isEmpty()){
            System.out.println("No members registered.");
            return;
        }

        for(Member member : members){
            member.displayDetails();
        }
    }

    public void searchByTitle(String title){
        for(Book book : books){
            if(book.getBookTitle().equalsIgnoreCase(title)){
                book.displayBookInfo();
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void searchByAuthor(String author){
        boolean found = false;
        for(Book book : books){
            if(book.getBookAuthor().equalsIgnoreCase(author)){
                book.displayBookInfo();
                found = true;
            }
        }
        if(!found){
            System.out.println("Book not found.");
        }
    }

}

public class LibraryManagementSystem {
    public static void showMenu(){
        System.out.println("\n" + "*".repeat(8) + "MENU" + "*".repeat(8));
        System.out.println(
            "1 Add Book\n" + //
            "2 Register Student\n" + //
            "3 Register Faculty\n" + //
            "4 Display Books\n" + //
            "5 Display Members\n" + //
            "6 Search Book\n" + //
            "7 Issue Book\n" + //
            "8 Return Book\n" + //
            "9 Exit"
        );
    }
    public static void main(String[] args) {
        //declaring common variables to be used
        int id, mId;
        String name, course, department;

        Scanner sc = new Scanner(System.in);
        System.out.println("WELCOME TO LIBRARY MANAGEMENT SYSTEM");
        LibraryManagementSystem.showMenu();
        System.out.print("\nEnter your choice: ");
        int choice = sc.nextInt();
        System.out.println();

        Library library = new Library();

        while(choice!=9){
            switch(choice){
                case 1:
                    //input book details
                    System.out.print("Enter Book ID: ");
                    int bookId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter book title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter book author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter category: ");
                    String category = sc.nextLine();

                    //adding book details to list
                    library.addBook(bookId, title, author, category);
                    break;

                case 2:
                    //input student details
                    System.out.print("Enter Student ID: ");
                    id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Student name: ");
                    name = sc.nextLine();
                    System.out.print("Enter course enrolled: ");
                    course = sc.nextLine();

                    Student student = new Student(id, name, course);
                    library.registerMember(student);
                    break;

                case 3:
                    //input faculty deatils
                    System.out.print("Enter Faculty ID: ");
                    id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Faculty name: ");
                    name = sc.nextLine();
                    System.out.print("Enter department: ");
                    department = sc.nextLine();

                    Faculty faculty = new Faculty(id, name, department);
                    library.registerMember(faculty);
                    break;

                case 4:
                    //displaying available books in library
                    System.out.println("Books in Library");
                    library.displayBooks();
                    break;

                case 5:
                    //displaying members of library
                    System.out.println("Members of Library are below");
                    library.displayMembers();
                    break;

                case 6:
                    //searching by title or author
                    System.out.println(
                        "\n1 Search by Title\n" + //
                        "2 Search by Author"
                    );
                    System.out.print("Enter your choice: ");
                    int ch = sc.nextInt();
                    sc.nextLine();
                    switch(ch){
                        case 1:
                            System.out.print("Enter book title: ");
                            title = sc.nextLine();
                            library.searchByTitle(title);
                            break;
                        case 2:
                            System.out.print("Enter author name: ");
                            author = sc.nextLine();
                            library.searchByAuthor(author);
                            break;
                    }
                    break;

                case 7:
                    System.out.print("Enter book ID: ");
                    id = sc.nextInt();
                    System.out.print("Enter your ID: ");
                    mId = sc.nextInt();
                    library.issueBook(id, mId);
                    break;

                case 8:
                    System.out.print("Enter book ID: ");
                    id = sc.nextInt();
                    System.out.print("Enter your ID: ");
                    mId = sc.nextInt();
                    library.returnBook(id, mId);
                    break;

                case 9:
                    break;

                default:
                    System.out.println("You entered wrong choice. Please enter again.");
                    break;
            }
            showMenu();
            System.out.print("\nEnter your choice: ");
            choice = sc.nextInt();
            System.out.println();
        }

        sc.close();

    }
}
