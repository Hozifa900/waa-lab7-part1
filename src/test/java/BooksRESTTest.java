import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import books.domain.Book;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

public class BooksRESTTest {

    @BeforeClass
    public static void setup() {
        RestAssured.port = Integer.valueOf(8080);
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "";
    }

    @Test
    public void testGetOneBook() {
        // add the book to be fetched
        Book book = new Book("878","Book 123", 18.95, "Joe Smith");
        given()
                .contentType("application/json")
                .body(book)
                .when().post("/books").then()
                .statusCode(200);
        // test getting the book
        given()
                .when()
                .get("books/878")
                .then()
                .contentType(ContentType.JSON)
                .and()
                .body("isbn",equalTo("878"))
                .body("title",equalTo("Book 123"))
                .body("price",equalTo(18.95f))
                .body("author",equalTo("Joe Smith"));
        //cleanup
        given()
                .when()
                .delete("books/878");
    }

    @Test
    public void testDeleteBook() {
        // add the book to be deleted
        Book book = new Book("878","Book 123", 18.95, "Joe Smith");
        given()
                .contentType("application/json")
                .body(book)
                .when().post("/books").then()
                .statusCode(200);

        given()
                .when()
                .delete("books/878");

        given()
                .when()
                .get("books/878")
                .then()
                .statusCode(404)
                .and()
                .body("errorMessage",equalTo("Book with isbn= 878 is not available"));
    }

    @Test
    public void testAddBook() {
        // add the book
        Book book = new Book("878","Book 123", 18.95, "Joe Smith");
        given()
                .contentType("application/json")
                .body(book)
                .when().post("/books").then()
                .statusCode(200);
        // get the book and verify
        given()
                .when()
                .get("books/878")
                .then()
                .statusCode(200)
                .and()
                .body("isbn",equalTo("878"))
                .body("title",equalTo("Book 123"))
                .body("price",equalTo(18.95f))
                .body("author",equalTo("Joe Smith"));
        //cleanup
        given()
                .when()
                .delete("books/878");
    }

    @Test
    public void testUpdateBook() {
        // add the book
        Book book = new Book("878","Book 123", 18.95, "Joe Smith");
        Book updateBook = new Book("878","Book 776", 20.95, "Mary Jones");
        given()
                .contentType("application/json")
                .body(book)
                .when().post("/books").then()
                .statusCode(200);
        //update book
        given()
                .contentType("application/json")
                .body(updateBook)
                .when().put("/books/"+updateBook.getIsbn()).then()
                .statusCode(200);
        // get the book and verify
        given()
                .when()
                .get("books/878")
                .then()
                .statusCode(200)
                .and()
                .body("isbn",equalTo("878"))
                .body("title",equalTo("Book 776"))
                .body("price",equalTo(20.95f))
                .body("author",equalTo("Mary Jones"));
        //cleanup
        given()
                .when()
                .delete("books/878");
    }

    @Test
    public void testGetAllBooks() {
        // add the books
        Book book1 = new Book("878","Book 123", 18.95, "Joe Smith");
        Book book2 = new Book("334","Book 776", 20.95, "Mary Jones");
        given()
                .contentType("application/json")
                .body(book1)
                .when().post("/books").then()
                .statusCode(200);
        given()
                .contentType("application/json")
                .body(book2)
                .when().post("/books").then()
                .statusCode(200);

        // get all books and verify
        given()
                .when()
                .get("books")
                .then()
                .statusCode(200)
                .and()
                .body("books.isbn",hasItems("334", "878"))
                .body("books.title",hasItems("Book 776", "Book 123"))
                .body("books.price",hasItems(20.95f, 18.95f))
                .body("books.author",hasItems("Mary Jones", "Joe Smith"));
        //cleanup
        given()
                .when()
                .delete("books/878");
        given()
                .when()
                .delete("books/334");
    }

    @Test
    public void testSearchBooks() {
        // add the books
        Book book1 = new Book("878","Book 123", 18.95, "Joe Smith");
        Book book2 = new Book("334","Book 776", 20.95, "Mary Jones");
        given()
                .contentType("application/json")
                .body(book1)
                .when().post("/books").then()
                .statusCode(200);
        given()
                .contentType("application/json")
                .body(book2)
                .when().post("/books").then()
                .statusCode(200);

        // get all books from author and verify
        given()
                .when()
                .get("books?author='Joe Smith'")
                .then()
                .statusCode(200)
                .and()
                .body("books.isbn",hasItems("878"))
                .body("books.title",hasItems("Book 123"))
                .body("books.price",hasItems(18.95f))
                .body("books.author",hasItems("Joe Smith"));
        //cleanup
        given()
                .when()
                .delete("books/878");
        given()
                .when()
                .delete("books/334");
    }


    @Test
    public void testAddBookValidation() {
        // add the book
        Book book = new Book("8","B", 18.95, "Joe Smith");
        given()
                .contentType("application/json")
                .body(book)
                .when().post("/books").then()
                .statusCode(400)
                .and()
                .body("isSuccess",equalTo(false));

    }
}
