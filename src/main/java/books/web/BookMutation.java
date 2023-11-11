package books.web;

//import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.yaml.snakeyaml.tokens.BlockEndToken;

import books.domain.Book;
import books.service.BookService;

@Controller
public class BookMutation {

    @Autowired
    private BookService bookService;

    @MutationMapping
    public Book addBook(@Argument String isbn, @Argument String title, @Argument String price,
            @Argument String author) {
        Book book = bookService.add(isbn, title, price, author);
        return book;
    }

    @MutationMapping
    public Book deleteBook(@Argument String isbn) {
        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return null;
        }
        bookService.delete(isbn);
        return book;
    }

    @MutationMapping
    public Book updateBook(@Argument String isbn, @Argument String title, @Argument String price,
            @Argument String author) {
        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            return null;
        }
        book.setTitle(title);
        book.setPrice(Double.parseDouble(price));
        book.setAuthor(author);
        bookService.update(book);
        return book;
    }

}
