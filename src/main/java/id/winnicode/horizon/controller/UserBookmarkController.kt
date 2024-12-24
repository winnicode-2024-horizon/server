package id.winnicode.horizon.controller

import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.model.NewsResponse
import id.winnicode.horizon.model.ResponseStatusType
import id.winnicode.horizon.model.WebResponse
import id.winnicode.horizon.service.UserBookmarkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
class UserBookmarkController @Autowired constructor(
    private val userBookmarkService: UserBookmarkService,
) {

    @GetMapping(
        path = ["/api/users/bookmarks"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun bookmarks(userToken: UserToken): WebResponse<NewsResponse> {
        val response = userBookmarkService.bookmarks(userToken.user)

        return WebResponse<NewsResponse>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = response
            }
    }

    @PostMapping(
        path = ["/api/users/bookmarks"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun addBookmark(@RequestParam id: Long, userToken: UserToken): WebResponse<String> {
        userBookmarkService.addBookmark(userToken.user, id)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }

    @DeleteMapping(
        path = ["/api/users/bookmarks"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteBookmark(@RequestParam id: Long, userToken: UserToken): WebResponse<String> {
        userBookmarkService.deleteBookmark(userToken.user, id)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }

    @GetMapping(
        path = ["/api/users/bookmarks/news/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun isBookmarked(@PathVariable id: Long, userToken: UserToken): WebResponse<Boolean> {
        val isBookmarked = userBookmarkService.checkBookmark(userToken.user, id)

        return WebResponse<Boolean>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = isBookmarked
            }
    }

}