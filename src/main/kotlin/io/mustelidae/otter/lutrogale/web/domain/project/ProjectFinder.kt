package io.mustelidae.otter.lutrogale.web.domain.project

import io.mustelidae.otter.lutrogale.web.commons.exception.ApplicationException
import io.mustelidae.otter.lutrogale.web.commons.exception.HumanErr
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigationInteraction
import io.mustelidae.otter.lutrogale.web.domain.navigation.api.NavigationResources.Reply.ReplyOfMenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.repository.ProjectRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProjectFinder(
    private val projectRepository: ProjectRepository,
    private val menuNavigationInteraction: MenuNavigationInteraction
) {

    fun findAllByLive(): List<Project> {
        return findAll().filter { it.status }
    }

    protected fun findAll(): List<Project> {
        return projectRepository.findAll()
    }

    fun findBy(id: Long): Project {
        return projectRepository.findByIdOrNull(id) ?: throw ApplicationException(HumanErr.IS_EMPTY)
    }

    fun findByLive(id: Long): Project {
        val project = findBy(id)
        if (!project.status)
            throw ApplicationException(HumanErr.IS_EXPIRE)
        return project
    }

    fun findByLiveProjectOfApiKey(apiKey: String): Project {
        val project = projectRepository.findByApiKey(apiKey) ?: throw ApplicationException(HumanErr.INVALID_APIKEY)
        if (!project.status)
            throw ApplicationException(HumanErr.IS_EXPIRE)

        return project
    }

    fun findAllByIncludeNavigationsProject(id: Long): List<ReplyOfMenuNavigation> {
        val project = findByLive(id)

        return project.menuNavigations.map {
            ReplyOfMenuNavigation.from(it, menuNavigationInteraction.getFullUrl(it))
        }
    }
}
