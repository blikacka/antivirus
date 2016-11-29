package antivirus.components;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Field;
import javax.swing.JPanel;

public class Awt1 {

    /**
     * attempts to retrieve a component from a JFrame or JDialog using the name
     * of the private variable that NetBeans (or other IDE) created to refer to
     * it in code.
     *
     * @param <T> Generics allow easier casting from the calling side.
     * @param window JFrame or JDialog containing component
     * @param name name of the private field variable, case sensitive
     * @return null if no match, otherwise a component.
     */
    @SuppressWarnings("unchecked")
    static public <T extends Component> T getComponentByName(Window window, String name) {

        // loop through all of the class fields on that form
        for (Field field : window.getClass().getDeclaredFields()) {

            try {
                // let us look at private fields, please
                field.setAccessible(true);

                // compare the variable name to the name passed in
                if (name.equals(field.getName())) {

                    // get a potential match (assuming correct &lt;T&gt;ype)
                    final Object potentialMatch = field.get(window);

                    // cast and return the component
                    return (T) potentialMatch;
                }

            } catch (SecurityException | IllegalArgumentException
                    | IllegalAccessException ex) {

                // ignore exceptions
            }

        }

        // no match found
        return null;
    }
    /**
     * attempts to retrieve a component from a JFrame or JDialog using the name
     * of the private variable that NetBeans (or other IDE) created to refer to
     * it in code.
     *
     * @param <T> Generics allow easier casting from the calling side.
     * @param window JFrame or JDialog containing component
     * @param name name of the private field variable, case sensitive
     * @return null if no match, otherwise a component.
     */
    @SuppressWarnings("unchecked")
    static public <T extends Component> T getComponentByNamePane(JPanel window, String name) {

        // loop through all of the class fields on that form
        for (Field field : window.getClass().getDeclaredFields()) {

            try {
                // let us look at private fields, please
                field.setAccessible(true);

                // compare the variable name to the name passed in
                if (name.equals(field.getName())) {

                    // get a potential match (assuming correct &lt;T&gt;ype)
                    final Object potentialMatch = field.get(window);

                    // cast and return the component
                    return (T) potentialMatch;
                }

            } catch (SecurityException | IllegalArgumentException
                    | IllegalAccessException ex) {

                // ignore exceptions
            }

        }

        // no match found
        return null;
    }

}
