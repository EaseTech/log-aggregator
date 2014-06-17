

package org.easetech.aggregator;

/**
 * TODO Describe me
 *
 */
public interface Translator<FROM, TO> {
    
    TO translate(FROM from);

}
