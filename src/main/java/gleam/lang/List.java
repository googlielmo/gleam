package gleam.lang;

public interface List extends Entity {

    Entity getCar()
            throws GleamException;

    Entity getCdr()
            throws GleamException;

    void setCar(Entity obj)
            throws GleamException;

    void setCdr(Entity obj)
            throws GleamException;
}
