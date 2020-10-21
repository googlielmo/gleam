package gleam.lang;

public interface Entity extends java.io.Serializable {

    /**
     * Evaluates this entity in the given environment,
     * with the given continuation.
     *
     * @param env Environment
     * @param cont Continuation
     * @return Entity a result
     * @throws GleamException in case of error
     */
    Entity eval(Environment env, Continuation cont)
            throws GleamException;

    /**
     * Performs syntax analysis on this entity.
     *
     * @return Entity
     * @throws GleamException in case of syntax error
     */
    Entity analyze()
                throws GleamException;

    /**
     * Performs environment optimization on this entity.
     *
     * @param env Environment
     * @return Entity
     * @throws GleamException in case of error
     */
    Entity optimize(Environment env)
                    throws GleamException;

    /**
     * Writes this entity in machine-readable form
     *
     * @param out PrintWriter
     */
    void write(java.io.PrintWriter out);

    /**
     * Writes this entity in human-readable form
     *
     * @param out PrintWriter
     */
    void display(java.io.PrintWriter out);
}
