package io.mustelidae.otter.lutrogale.web.domain.grant.repository

import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * Created by seooseok on 2016. 10. 5..
 */
interface UserAuthorityGrantRepository : JpaRepository<UserAuthorityGrant, Long> {
    @Query("SELECT DISTINCT uag FROM UserAuthorityGrant uag JOIN FETCH uag.user u JOIN FETCH uag.authorityDefinition ad JOIN FETCH ad.project WHERE uag.status = true AND uag.user.id = :userId")
    fun findByStatusTrueAndUserId(@Param("userId") userId: Long): List<UserAuthorityGrant>?

    fun findAllByUserIdAndAuthorityDefinitionIdInAndStatusTrue(userId: Long, authorityDefinitionIds: List<Long>): List<UserAuthorityGrant>?
}
