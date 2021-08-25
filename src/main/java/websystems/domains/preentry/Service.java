package websystems.domains.preentry;

import java.io.Serializable;

/**
 * @author Murad
 */
public class Service implements Serializable {

    private Long id;
    private String name;

    public Service(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
