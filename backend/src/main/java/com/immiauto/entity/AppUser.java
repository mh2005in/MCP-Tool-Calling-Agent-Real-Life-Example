package com.immiauto.entity;

import com.immiauto.enums.AppRole;
import com.immiauto.enums.AppUserStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps a Microsoft Entra External ID identity (external subject / oid) to an
 * application user. Identity concerns are kept separate from the domain
 * {@link Consultant} so that clients/parties can also authenticate later.
 */
@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_users_gen")
    @SequenceGenerator(name = "app_users_gen", sequenceName = "app_users_seq", allocationSize = 1)
    private Long id;

    /** Entra object id (oid) or subject (sub) - the stable external identifier. */
    @Column(name = "external_subject", nullable = false, unique = true, updatable = false)
    private String externalSubject;

    @Column(nullable = false)
    private String email;

    @Column(name = "display_name")
    private String displayName;

    /** Entra tenant id (tid claim). */
    @Column(name = "tenant_id")
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppUserStatus status;

    /** Link to the consultant account this user acts under (null for unlinked/client users). */
    @Column(name = "consultant_id")
    private Long consultantId;
}
