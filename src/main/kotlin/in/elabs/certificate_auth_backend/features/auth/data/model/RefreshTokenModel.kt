package `in`.elabs.certificate_auth_backend.features.auth.data.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "refresh_token",
    indexes = [
        Index(name = "idx_refresh_token_expires_at", columnList = "expiresAt"),
        Index(name = "idx_refresh_token_user_id", columnList = "user_id")
    ]
)
data class RefreshTokenModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val expiresAt: Instant,
    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @Column(nullable = false, unique = true)
    val hashedToken: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false ,
        foreignKey = ForeignKey(
            foreignKeyDefinition =
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
        )
    )
    val user: UserModel,
)