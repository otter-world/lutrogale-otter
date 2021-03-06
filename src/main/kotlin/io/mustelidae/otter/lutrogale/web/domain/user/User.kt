package io.mustelidae.otter.lutrogale.web.domain.user

import io.mustelidae.otter.lutrogale.api.common.Audit
import io.mustelidae.otter.lutrogale.api.config.InvalidArgumentException
import io.mustelidae.otter.lutrogale.web.domain.authority.AuthorityDefinition
import io.mustelidae.otter.lutrogale.web.domain.grant.UserAuthorityGrant
import io.mustelidae.otter.lutrogale.web.domain.grant.UserPersonalGrant
import io.mustelidae.otter.lutrogale.web.domain.navigation.MenuNavigation
import io.mustelidae.otter.lutrogale.web.domain.project.Project
import org.hibernate.annotations.Where
import java.util.function.Consumer
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Created by HanJaehyun on 2016. 9. 21..
 */
@Entity
class User(
    @Column(nullable = false, length = 30)
    var name: String,
    @Column(unique = true, nullable = false, length = 50)
    val email: String
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @Column(length = 50)
    var department: String? = null

    var isPrivacy = false

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    var status: Status = Status.wait

    @Where(clause = "status = true")
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = [ALL])
    var userAuthorityGrants: MutableList<UserAuthorityGrant> = arrayListOf()
        protected set

    val authorityDefinitions: List<AuthorityDefinition>
        get() = userAuthorityGrants.map { it.authorityDefinition!! }

    fun addBy(userAuthorityGrant: UserAuthorityGrant) {
        if (this.userAuthorityGrants.contains(userAuthorityGrant))
            throw InvalidArgumentException("이미 해당 권한 그룹은 허용되어 있습니다.")

        userAuthorityGrants.add(userAuthorityGrant)
        if (userAuthorityGrant.user != this)
            userAuthorityGrant.setBy(this)
    }

    @Where(clause = "status = true")
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = [ALL])
    var userPersonalGrants: MutableList<UserPersonalGrant> = arrayListOf()
        protected set

    val menuNavigations: List<MenuNavigation>
        get() = userPersonalGrants.map { it.menuNavigation!! }

    fun addBy(menuNavigation: MenuNavigation) {
        if (this.menuNavigations.contains(menuNavigation))
            throw IllegalStateException("이미 해당 권한 그룹은 허용되어 있습니다.")

        val userPersonalGrant = UserPersonalGrant()
        userPersonalGrant.setBy(this)
        userPersonalGrant.setBy(menuNavigation)

        addBy(userPersonalGrant)
    }

    fun addBy(userPersonalGrant: UserPersonalGrant) {
        this.userPersonalGrants.add(userPersonalGrant)
        if (userPersonalGrant.user != this)
            userPersonalGrant.setBy(this)
    }

    fun removeBy(menuNavigation: MenuNavigation?) {
        this.userPersonalGrants.forEach(
            Consumer { grant: UserPersonalGrant ->
                if (grant.menuNavigation!! == menuNavigation)
                    grant.expire()
            }
        )
    }

    fun expire() {
        status = Status.expire
    }

    fun getProjects(): List<Project> {
        return this.authorityDefinitions.map { it.project!! }
    }

    /**
     * Client_User Status
     */
    enum class Status {
        /* 허가 */
        allow,
        /* 불가 */
        reject,
        /* 대기 */
        wait,
        /* 만료 */
        expire
    }
}
