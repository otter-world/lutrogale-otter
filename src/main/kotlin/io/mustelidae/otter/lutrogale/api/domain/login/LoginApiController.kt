package io.mustelidae.otter.lutrogale.api.domain.login

import io.mustelidae.otter.lutrogale.web.commons.ApiRes
import io.mustelidae.otter.lutrogale.web.commons.utils.RequestHelper.addSessionBy
import io.mustelidae.otter.lutrogale.web.domain.admin.Admin
import io.mustelidae.otter.lutrogale.web.domain.session.OsoriSessionInfo
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * Created by htwoh on 2017. 3. 2..
 */
@RestController
class LoginApiController(
    private val httpSession: HttpSession,
    private val adminLoginInteraction: AdminLoginInteraction
) {

    @Operation(hidden = true)
    @PostMapping("/v1/check-login")
    fun checkLogin(@RequestBody request: LoginResources.Request, httpServletRequest: HttpServletRequest): ApiRes<*> {

        val admin: Admin = adminLoginInteraction.loginCheck(request.email, request.password)
        val sessionInfo = OsoriSessionInfo.of(admin)
        addSessionBy(httpSession, sessionInfo)
        return ApiRes<Any?>(admin.email)
    }
}
