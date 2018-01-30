import java.util.Objects;

public class UserDto {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDto() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return id == userDto.id &&
                Objects.equals(name, userDto.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
