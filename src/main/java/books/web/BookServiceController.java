package books.web;

import java.util.*;
import java.util.stream.Collectors;

import books.domain.Book;
import books.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestController
public class BookServiceController {

    @Autowired
    BookService bookService;

    @GetMapping("/books/{isbn}")
    public ResponseEntity<?> getBook(@PathVariable String isbn) {
        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return new ResponseEntity<CustomErrorType>(
                    new CustomErrorType("Book with isbn= " + isbn + " is not available"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return new ResponseEntity<CustomErrorType>(
                    new CustomErrorType("Book with isbn = " + isbn + " is not available"), HttpStatus.NOT_FOUND);
        }
        bookService.delete(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
        bookService.add(book.getIsbn(), book.getTitle(), String.valueOf(book.getPrice()), book.getAuthor());
        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }

    @PutMapping("/books/{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable String isbn, @Valid @RequestBody Book book) {
        bookService.update(book);
        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }

    @GetMapping("/books")
    public ResponseEntity<?> searchBooks(@RequestParam(value = "author", required = false) String author) {
        Books allbooks = new Books();
        if (author == null) { // get all books
            allbooks.setBooks(bookService.findAll());
        } else { // get books from an certain author
            String authorName = author.substring(1, author.length() - 1); // remove quotes form the name
            List<Book> booklist = bookService.findByAuthor(authorName);
            allbooks.setBooks(booklist);
        }
        return new ResponseEntity<Books>(allbooks, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println("******************************************");
        System.out.println(ex.getBindingResult().getFieldErrors());
        Map<String, Object> fieldError = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            fieldError.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("isSuccess", false);
        map.put("data", null);
        map.put("status", HttpStatus.BAD_REQUEST);
        map.put("fieldError", fieldError);
        return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
    }
}
