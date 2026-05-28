package pizzeria.domain.entities;

import pizzeria.domain.enums.Role;
import java.util.UUID;

public class User {

  private UUID id;
  private String name;
  private String email;
  private String password;
  private String avatarPath;
  private Role role;

  public User() {
    this.role = Role.USER;
  }

  public User(UUID id, String name, String email, String password) {
    this(id, name, email, password, null, Role.USER);
  }

  public User(UUID id, String name, String email, String password, String avatarPath) {
    this(id, name, email, password, avatarPath, Role.USER);
  }

  public User(UUID id, String name, String email, String password, String avatarPath, Role role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.avatarPath = avatarPath;
    this.role = role;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getAvatarPath() { return avatarPath; }
  public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public String getRoleName() { return role == null ? "USER" : role.name(); }
  public void setRoleName(String role) { this.role = Role.fromString(role); }
}
