package id.winnicode.horizon.service

import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.model.NewsResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserBookmarkService @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate,
) {

    @Transactional(readOnly = true)
    fun bookmarks(user: User): NewsResponse {
        val bookmarks = user.bookmarks
        return NewsResponse(
            bookmarks.size,
            bookmarks
        )
    }

    @Transactional
    fun addBookmark(user: User, newsId: Long) {
        jdbcTemplate.update("INSERT INTO users_bookmarks (username , news_id) VALUES (?, ?)", user.username, newsId)
    }

    @Transactional
    fun deleteBookmark(user: User, newsId: Long) {
        jdbcTemplate.update("DELETE FROM users_bookmarks WHERE username = ? AND news_id = ?", user.username, newsId)
    }

    @Transactional(readOnly = true)
    fun checkBookmark(user: User, newsId: Long): Boolean {
        val bookmarks = user.bookmarks
        return bookmarks.any { it.id == newsId }
    }
}