package RecyclerViewHolders;

/**
 * Created by rbech on 2/3/2016.
 */
public class MembersHolder {
    String name;
    String email;

    public MembersHolder(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
