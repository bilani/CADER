package fr.samovar.acme.cadermfs;

import java.util.List;

/**
 * @author Stéphane Jean
 * This interface manages the result of a query
 */

public interface Result {
    
    /**
     * Close the result set
     * @throws Exception
     */
    void close() throws Exception;
    
    
    /**
     * Iterates to the next result
     * @return true if there is a next result
     * @throws Exception
     */
    boolean next() throws Exception;
    
    /**
     * Return the string value of the result for the given column
     * @param col the column
     * @return the string value of the result for the given column
     * @throws Exception
     */
    String getString(int col) throws Exception;
    
    /**
     * Return the number of rows of this result
     * @return the number of rows of this result
     * @throws Exception
     */
    int getNbRow() throws Exception;
    
    /**
     * Return return a list of results ; the maximum number of element is set with maxk
     * @return the number of rows of this result with a maximum set to maxk
     * @throws Exception
     */
    List<String> getNbRow(int maxK) throws Exception;
    
    /**
     * Return the string values of the result for the given columns
     * @param cols the given columns
     * @return the string values of the result for the given columns
     * @throws Exception
     */
    String[] getString(String[] cols) throws Exception;

}
