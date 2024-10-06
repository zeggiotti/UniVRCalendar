package prettifier;

public class Event {

    private String name;
    private String location;

    public Event(String name, String location) {
        this.name = name;
        this.location = location;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event)
            return name.equals(((Event) o).name);
        else return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
