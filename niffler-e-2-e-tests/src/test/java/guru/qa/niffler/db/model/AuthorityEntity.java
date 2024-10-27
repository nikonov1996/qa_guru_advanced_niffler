package guru.qa.niffler.db.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuthorityEntity implements Serializable {
  private UUID id;
  private Authority authority;
  private UserEntity user;

}
