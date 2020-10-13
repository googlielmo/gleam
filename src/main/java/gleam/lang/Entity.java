package gleam.lang;

public interface Entity extends java.io.Serializable {
    Entity eval(Environment env, Continuation cont)
            throws GleamException;

    Entity analyze()
                throws GleamException;

    Entity optimize(Environment env)
                    throws GleamException;

    /**
     * Writes this entity in machine-readable form
     */
    void write(java.io.PrintWriter out);

    void display(java.io.PrintWriter out);

    String toString();
}
