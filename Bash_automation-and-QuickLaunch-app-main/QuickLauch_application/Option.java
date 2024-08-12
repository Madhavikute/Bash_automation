import java.util.ArrayList;
import java.util.List;

public class Option {
    private String description;
    private final List<String> commands;
    private final List<String> links;

    public Option(String description) {
        this.description = description;
        this.commands = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void clearCommands() {
        commands.clear();
    }

    public List<String> getLinks() {
        return links;
    }

    public void addLink(String link) {
        links.add(link);
    }

    public void clearLinks() {
        links.clear();
    }

    @Override
    public String toString() {
        return description;
    }
}
