package ru.berezhnov.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.berezhnov.models.Book;
import ru.berezhnov.models.Person;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class BookDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> index() {
        List<Book> result = jdbcTemplate.query("SELECT * FROM book", new BeanPropertyRowMapper<>(Book.class));
        result.sort(Comparator.comparingInt(Book::getId));
        return result;
    }

    public Book show(int id) {
        return jdbcTemplate.query("SELECT * FROM book WHERE id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Book.class))
                .stream().findAny().orElse(null);
    }

    public void update(int id, Book updatedBook) {
        jdbcTemplate.update("UPDATE book SET title=?, author=?, year=?, person_id=? WHERE id=?", updatedBook.getTitle(),
                updatedBook.getAuthor(), updatedBook.getYear(), updatedBook.getBorrowerId(), id);
    }

    public void save(Book book) {
        jdbcTemplate.update("INSERT INTO book(title, author, year, person_id) VALUES (?, ?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getYear(), book.getBorrowerId());
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM book WHERE id=?", id);
    }

    public Optional<Person> getPersonByBookId(int bookId) {
        return jdbcTemplate.query("SELECT person.* FROM person INNER JOIN book ON person.id=book.person_id WHERE book.id=?",
                        new Object[]{bookId}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny();
    }

    public void giveBookToPerson(int bookId, Person person) {
        jdbcTemplate.update("UPDATE book SET person_id=? WHERE id=?", person.getId(), bookId);
    }
    public void takeBookFromPerson(int bookId) {
        jdbcTemplate.update("UPDATE book SET person_id=null WHERE id=?", bookId);
    }
}
