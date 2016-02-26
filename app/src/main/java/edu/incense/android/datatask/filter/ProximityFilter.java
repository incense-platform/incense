/**
 * 
 */
package edu.incense.android.datatask.filter;

import edu.incense.android.datatask.data.Data;

/**
 * @author mxpxgx
 *
 */
public class ProximityFilter extends DataFilter {

    public ProximityFilter(){
        super();
        setFilterName(this.getClass().getName());
    }
    
    /** 
     * @see edu.incense.android.datatask.filter.DataFilter#computeSingleData(edu.incense.android.datatask.data.Data)
     */
    @Override
    protected void computeSingleData(Data data) {
        // TODO Auto-generated method stub
        
    }

}
