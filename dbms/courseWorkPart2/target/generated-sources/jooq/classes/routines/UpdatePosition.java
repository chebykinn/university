/**
 * This class is generated by jOOQ
 */
package classes.routines;


import classes.Public;

import javax.annotation.Generated;

import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UpdatePosition extends AbstractRoutine<java.lang.Void> {

    private static final long serialVersionUID = -848101648;

    /**
     * The parameter <code>public.update_position.pos_id</code>.
     */
    public static final Parameter<Integer> POS_ID = createParameter("pos_id", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * The parameter <code>public.update_position.name</code>.
     */
    public static final Parameter<String> NAME = createParameter("name", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>public.update_position.description</code>.
     */
    public static final Parameter<String> DESCRIPTION = createParameter("description", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * Create a new routine call instance
     */
    public UpdatePosition() {
        super("update_position", Public.PUBLIC);

        addInParameter(POS_ID);
        addInParameter(NAME);
        addInParameter(DESCRIPTION);
    }

    /**
     * Set the <code>pos_id</code> parameter IN value to the routine
     */
    public void setPosId(Integer value) {
        setValue(POS_ID, value);
    }

    /**
     * Set the <code>name</code> parameter IN value to the routine
     */
    public void setName_(String value) {
        setValue(NAME, value);
    }

    /**
     * Set the <code>description</code> parameter IN value to the routine
     */
    public void setDescription(String value) {
        setValue(DESCRIPTION, value);
    }
}