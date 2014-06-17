

package org.easetech.aggregator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Convert a list of one type to another,
 * 
 * @param <FROM> the type of element to convert from.
 * @param <TO> the type of element to convert to.
 */
public class ListTranslator<FROM, TO> implements Translator<List<FROM>, List<TO>> {
    /** The translator for the basic element type. */
    private final Translator<FROM, TO> translator;

    
    /**
     * Construct a new List Translator.
     * 
     * @param translator a translator for the basic elements
     */
    public ListTranslator(Translator<FROM, TO> translator) {
        this(translator, false);
    }
    
    /**
     * Construct a new List Translator.
     * 
     * @param translator a translator for the basic elements
     * @param preserveNullElements should null elements be preserved
     */
    public ListTranslator(Translator<FROM, TO> translator, boolean preserveNullElements) {
        this.translator = translator;
    }
    
    /**
     * Translate a list of elements from one type to another.
     * 
     * @param in the list of elements to translate.  Can be null.
     *
     * @return a list of translated elements.  Never null.
     */
    @Override
    public List<TO> translate(List<FROM> in) {
        final List<TO> result;
        if (in != null && !in.isEmpty()) {
            result = new ArrayList<TO>(in.size());
            
            for (FROM from : in) {
                TO translatedElement = translator.translate(from);
                if (translatedElement != null) {
                    result.add(translatedElement);
                }
            }
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
}
