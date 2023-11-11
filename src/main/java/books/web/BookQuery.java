package books.web;

//import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import books.domain.Book;
import books.service.BookService;

import java.util.List;

@Controller
public class BookQuery {

    @Autowired
    private BookService bookService;

    @QueryMapping
    public Book getBook(@Argument String isbn) {
        Book book = bookService.findByIsbn(isbn);
        return book;
    }

    @QueryMapping
    public List<Book> searchBooks(@Argument String author) {
        System.out.println("searchBooks " + author);
        return bookService.findByAuthor(author);
    }

}
