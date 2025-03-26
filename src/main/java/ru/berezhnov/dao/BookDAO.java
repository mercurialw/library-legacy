package ru.berezhnov.dao;

import ru.berezhnov.models.Book;
import ru.berezhnov.models.Person;
import ru.berezhnov.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAO {

    public List<Book> index() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM book ORDER BY id")) {
            while (rs.next()) {
                books.add(mapBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book show(int id) {
        Book book = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM book WHERE id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                book = mapBook(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    public void save(Book book) {
        String sql = "INSERT INTO book (title, author, year, person_id) VALUES (?, ?, ?, NULL)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(int id, Book book) {
        String sql = "UPDATE book SET title = ?, author = ?, year = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYear());
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM book WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("year"),
            (Integer) rs.getObject("person_id") // может быть NULL
        );
    }
}