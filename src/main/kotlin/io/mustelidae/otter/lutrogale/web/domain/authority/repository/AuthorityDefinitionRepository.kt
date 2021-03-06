package io.mustelidae.otter.lutrogale.web.domain.authority.repository

import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by seooseok on 2016. 9. 29..
 * 권한 정의 레파지토리
 */
@Repository
interface AuthorityDefinitionRepository : JpaRepository<AuthorityDefinition?, Long?> {
    fun findByProjectIdAndStatusTrue(projectId: Long): List<AuthorityDefinition>?
    fun findByIdIn(ids: List<Long>): List<AuthorityDefinition>?
}
