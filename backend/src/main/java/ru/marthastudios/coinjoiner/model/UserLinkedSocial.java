package ru.marthastudios.coinjoiner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.marthastudios.coinjoiner.enums.UserLinkedSocialType;

@Data
@Table("user_linked_socials_table")
public class UserLinkedSocial {
    @Id
    @Column("id")
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("type")
    private UserLinkedSocialType type;
    @Column("identifier")
    private String identifier;
}
