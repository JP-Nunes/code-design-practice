package br.com.study.codedesignpractice.author

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class AuthorRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val authorRepository: AuthorRepository
) {

    @Test
    fun `should retrieve author with correct generated id and timestamp`() {
        val author = Author(
            name = "John Doe",
            email = "",
            description = "A sample author"
        )

        entityManager.persist(author)
        entityManager.flush()

        val retrievedAuthor = authorRepository.findByIdOrNull(author.id!!)

        assertThat(retrievedAuthor).isEqualTo(author)
    }
}